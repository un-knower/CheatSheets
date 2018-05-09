from flask import Flask, request, jsonify
from flask_restful import Resource, Api, reqparse
from flask_jwt_extended import (JWTManager, jwt_required, create_access_token, get_jwt_identity)
import sqlite3

app = Flask(__name__)
api = Api(app)

# Setup the Flask-JWT-Extended extension
app.config['JWT_SECRET_KEY'] = 'super-secret'  # Change this!
jwt = JWTManager(app)

items = []

def create_table_with_users():
    connection = sqlite3.connect("data.db")
    cursor = connection.cursor()
    create_table = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY, username text, password text)"
    cursor.execute(create_table)
    create_table = "CREATE TABLE IF NOT EXISTS items (name text, price real)"
    connection.commit()
    connection.close()
    
class User:   
    def __init__(self, _id, username, password):
        self.id = _id
        self.username = username
        self.password = password

    @classmethod
    def find_by_username(self, username):
        connection = sqlite3.connect("data.db")
        cursor = connection.cursor()
        query = "SELECT * FROM users WHERE username=?"
        result = cursor.execute(query, (username,) )  # always TUPLE!
        row = result.fetchone()
        if row:
            user = User(row[0], row[1], row[2])  # user = cls(...)
            user = User(*row) # cls(*row)
        else:
            user = None
        connection.close()
        return user

    @classmethod
    def find_by_id(self, username):
        connection = sqlite3.connect("data.db")
        cursor = connection.cursor()
        query = "SELECT * FROM users WHERE id=?"
        result = cursor.execute(query, (_id,) )  # always TUPLE!
        row = result.fetchone()
        if row:
            user = User(row[0], row[1], row[2])  # user = cls(...)
            user = User(*row) #cls(*row)
        else:
            user = None
        connection.close()
        return user
            
#users = [ 
#    User(1, 'bob', 'asdf')
#]
#
#username_mapping = {u.username : u for u in users}
#userid_mapping   = {u.id : u       for u in users}

@app.route('/login', methods=['POST'])
def login():
    if not request.is_json:
        return jsonify({"msg": "Missing JSON in request"}), 400

    username = request.json.get('username', None)
    password = request.json.get('password', None)
    if not username:
        return jsonify({"msg": "Missing username parameter"}), 400
    if not password:
        return jsonify({"msg": "Missing password parameter"}), 400

    #if password != username_mapping[username].password:
    if password != User.find_by_username(username).password:
        return jsonify({"msg": "Bad username or password"}), 401

    # Identity can be any data that is json serializable
    access_token = create_access_token(identity=username)
    return jsonify(access_token=access_token), 200

class Item(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument('price', type=float, required=True, help="cannot be empty")
    
    @jwt_required
    def get(self, name):
            # Access the identity of the current user with get_jwt_identity
        current_user = get_jwt_identity()
        #return {'logged_in_as':current_user}, 200
        item = self.find_by_name(name)
        if item:
            return item
        return {'message': 'item not found'}, 404 
    
        item = next(filter(lambda x: x['name'] == name,  items), None)  # first found item in filter object
        return {'item': None}, 200 if item is not None else 404
        return {'item': None}, 200 if item else 404 # no need for jsonify, RESTful does it

    @classmethod
    def find_by_name(cls, name):
        connection = sqlite3.connect("data.db")
        cursor = connection.cursor()
        query = "SELECT * FROM items WHERE name=?"
        result = cursor.execute(query, (name,))
        row = result.fetchrow()
        connection.close()
        
        if row:
            return {'item': {'name':row[0], 'price':row[1]}}

    @classmethod
    def insert(cls, item):
        connect = sqlite3.connect("data.db")
        cursor = connect.cursor()
        query = "INSERT INTO items VALUES (?, ?)"
        cursor.execute(query, (item['name'], item['price'],))
        connect.commit()
        connect.close()        
        
    def post(self, name):        
        #if next(filter(lambda x: x['name'] == name,  items), None) is not None:
        if Item.find_by_name(name):
            return {'message' : "An item with name '{}' already exists.".format(name)}, 400 # request problem

        data = request.get_json(force=True)  #no need for content type heade application/jsonr
        data = request.get_json(silent=True)  # returns none if error
        data = request.get_json()
        data = Item.parser.parse_args()
        
        item = {'name' : name, 'price' : data['price'] } 
        #items.append(item)
        try:
            self.insert(item)
        except:
            return {'message':'An error occured during insertion'}, 500  #internal server error
        
        return item, 201  # must be JSON format,  201 = created  , 202 = accepted for delay
    
    def delete(self, name):
        #global items
        #items = list(filter(lambda x: x['name'] != name ,  items))
        connect = sqlite3.connect("data.db")
        cursor = connect.cursor()
        query = "DELETE FROM items WHERE name=?"
        cursor.execute(query, (name, ))
        connect.commit()
        connect.close()          
        return {'message' : 'Item deleted'}
    
    def put(self, name):
        data = request.get_json()
        data = Item.parser.parse_args()   
        #item = next(filter(lambda x: x['name'] == name,  items), None)
        item = self.find_by_name(name)
        updated_item = {'name': name, 'price': data['price']}
        
        if item is None:
            try:
                self.insert(updated_item) #items.append(item)
            except:
                return {'message':'problem inserting item'}, 500
        else:
            try:
                self.update(updated_item) #item.update(data)
            except:
                return {'message':'problem updating item'}, 500
        return updated_item
    
    @classmethod
    def update(cls, item):
        connect = sqlite3.connect("data.db")
        cursor = connect.cursor()
        query = "UPDATE items SET price=? WHERE name=?"
        cursor.execute(query, (item['price'], item['name'], ))
        connect.commit()
        connect.close()  

class UserRegister(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument('username', type=str, required=True, help="cannot be empty")
    parser.add_argument('password', type=str, required=True, help="cannot be empty")
    
    def post(self):
        data = UserRegister.parser.parse_args()
        
        if User.find_by_username(data['username']):
            return {'message':'User already exists'}, 400
        
        connection = sqlite3.connect("data.db")
        cursor = connection.cursor()
        query = "INSERT INTO users VALUES (NULL, ?, ?)"
        cursor.execute(query, (data['username'], data['password'], ))  # tuple
        connection.commit()
        connection.close()
        return {'message':'User created successfully'}, 201
        
class ItemList(Resource):
    def get(self):
        connection = sqlite3.connect("data.db")
        cursor = connection.cursor()
        query = "SELECT * FROM items"
        result = cursor.execute(query)
        items =[]
        for row in result:
            items.append( {'name':row[0], 'price':row[1]} )
        connection.close()
                
        return {'items': items}

api.add_resource(Item, '/item/<string:name>')
api.add_resource(ItemList, '/items')
api.add_resource(UserRegister, '/register')

if __name__ == '__main__':
    create_table_with_users()
    app.run(port=5000, debug=True)

#https://github.com/oleg-agapov/flask-jwt-auth/blob/master/step_5/models.py