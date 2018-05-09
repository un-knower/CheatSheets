from flask import Flask, request, jsonify
from flask_restful import Resource, Api, reqparse
from flask_jwt_extended import (JWTManager, jwt_required, create_access_token, get_jwt_identity)
from models.user import User, UserRegister
from models.item import ItemModel, ItemList
from resources.store import Store, StoreList

app = Flask(__name__)
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False 
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///data.db'
api = Api(app)
# Setup the Flask-JWT-Extended extension
app.config['JWT_SECRET_KEY'] = 'super-secret'  # Change this!
jwt = JWTManager(app)

@app.before_first_request
def create_tables():
    db.create_all()

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
    parser.add_argument('store_id', type=int, required=True, help="every item needs store id")
    
    @jwt_required
    def get(self, name):
            # Access the identity of the current user with get_jwt_identity
        current_user = get_jwt_identity()
        #return {'logged_in_as':current_user}, 200
        item = ItemModel.find_by_name(name)
        if item:
            return item.json() # must be json format
        return {'message': 'item not found'}, 404 
    
        #item = next(filter(lambda x: x['name'] == name,  items), None)  # first found item in filter object
        return {'item': None}, 200 if item is not None else 404
        return {'item': None}, 200 if item else 404 # no need for jsonify, RESTful does it  
        
    def post(self, name):        
        #if next(filter(lambda x: x['name'] == name,  items), None) is not None:
        if ItemModel.find_by_name(name):
            return {'message' : "An item with name '{}' already exists.".format(name)}, 400 # request problem

        data = request.get_json(force=True)  #no need for content type heade application/jsonr
        data = request.get_json(silent=True)  # returns none if error
        data = request.get_json()
        data = Item.parser.parse_args()
        
        #item = {'name' : name, 'price' : data['price'] } 
        item = ItemModel(name, data['price'], data['store_id'])
        item = ItemModel(name, **data)
        
        #items.append(item)
        try:
            #ItemModel.insert(item)
            item.save_to_db()
        except:
            return {'message':'An error occured during insertion'}, 500  #internal server error
        
        return item.json(), 201  # must be JSON format,  201 = created  , 202 = accepted for delay
    
    def delete(self, name):
        item = ItemModel.find_by_name(name)
        if item:
            item.delete_from_db()
    
    def put(self, name):
        data = request.get_json()
        data = ItemModel.parser.parse_args()   
        #item = next(filter(lambda x: x['name'] == name,  items), None)
        item = ItemModel.find_by_name(name)
        #updated_item = {'name': name, 'price': data['price']}
        
        if item is None:
            item = ItemModel(name, data['price'], data['store_id'])
        else:
            item.price = data['price']
        
        item.save_to_db()
        return item.json()
    

api.add_resource(Store, '/store/<string:name>')
api.add_resource(Item, '/item/<string:name>')
api.add_resource(ItemList, '/items')
api.add_resource(StoreList, '/stores')
api.add_resource(UserRegister, '/register')

if __name__ == '__main__':
    from db import db
    db.init_app(app)
    app.run(port=5000, debug=True)

#https://github.com/oleg-agapov/flask-jwt-auth/blob/master/step_5/models.py
#https://github.com/jslvtr/rest-api-sections
#WINDOWS
#curl -H "Content-type: application/json" -X POST http://localhost:5000/login -d "{\"username\":\"bob\", \"password\":\"asdf\"}"    
#http://blog.luisrei.com/articles/flaskrest.html
#https://blog.miguelgrinberg.com/post/designing-a-restful-api-with-python-and-flask