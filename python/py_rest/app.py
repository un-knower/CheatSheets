from flask import Flask, request, json, url_for, Response, jsonify, render_template

app = Flask(__name__)

incomes = [ { 'description': 'salary', 'amount': 5000 } ]

stores =  [ { 'name' : 'My store', 'items' : [
            { 'name' : 'My item' , 'price' : 10.00  } ] } ]

@app.route("/")
def home():
    return render_template("index.html")

@app.route('/store/<string:name>')
def get_store(name):
    for store in stores:
        if store['name'] == name:
            return jsonify(store)  # store is dict!
    return jsonify({'message':'store not found'})

@app.route('/store/<string:name>/item')
def get_item_from_store(name):
    for store in stores:
        if store['name'] == name:
            return jsonify({'items' : store['items']})
    return jsonify({'message': 'store not found'})

@app.route('/stores')
def get_stores():
    return jsonify( {'stores' : stores} )  # == json.dumps( {'stores' : stores} )

@app.route("/store", methods=['POST'])
def create_store():
    request_data = request.get_json()
    new_store = {
            'name': request_data['name'],
            'items': []
    }
    stores.append(new_store)
    return jsonify(new_store)

@app.route('/store/<string:name>/item', methods=['POST'])
def create_item_in_store(name):
    request_data = request.get_json()
    for store in stores:
        if store['name'] == name:
            new_item = {
                    'name' : request_data['name'], 
                    'price': request_data['price']
            }
            store['items'].append(new_item)
            return jsonify(new_item)
    return jsonify({'message': 'store not found'})











@app.route("/")   #app.add_url_rule('/', 'index', index)
def hello():
    return "Hello World!"

@app.route("/hi")
def hi():
    if 'name' in request.args:
        return "HELLO " + request.args['name']  # curl -X GET http://localhost:5000/hi?name=ff\\5
    else:
        return "HELLO"
    
@app.route('/echo', methods = ['GET', 'POST', 'PATCH', 'PUT', 'DELETE'])
def echo():
    #return ({key:value for key, value in request if not key.startswith('_') and not callable(key)})
    #return "METHOD {}......{}...{}".format(request.method, request.headers, request.host)
    return "{}".format( [ func  for func in request.__dict__.items() ] )
    return "{}".format( [ str(func) + ":" + str(getattr(request, func))  for func in dir(request) if not func.startswith("_") ] )



@app.route('/messages', methods = ['POST'])
def api_message():
    if request.headers['Content-Type'] == 'text/plain':
        return "Text Message: " + request.data
    elif request.headers['Content-Type'] == 'application/json':
        return "JSON Message: " + json.dumps(request.json)
    elif request.headers['Content-Type'] == 'application/octet-stream':
        f = open('./binary', 'wb')
        f.write(request.data)
        f.close()
        return "Binary message written!"
    else:
        return "415 Unsupported Media Type ;)"
# windows:    curl -H "Content-type: application/json" -X POST http://localhost:5000/messages -d "{\"message\":\"Hello Data\"}"
# linux:      curl -H "Content-type: application/json" -X POST http://localhost:5000/messages -d '{"message":"Hello Data"}'
# to send a file   curl -H "Content-type: application/octet-stream" -X POST http://127.0.0.1:5000/messages --data-binary @message.bin

@app.route('/helloResponse', methods = ['GET'])
def api_hello():
    data = {
        'hello'  : 'world',
        'number' : 3
    }
    js = json.dumps(data)

    resp = Response(js, status=200, mimetype='application/json')
    resp.headers['Link'] = 'http://luisrei.com'
    #resp = jsonify(data)
    #resp.status_code = 200
    return resp

@app.errorhandler(404)   # replace all html errors with json http://flask.pocoo.org/snippets/83/
def not_found(error=None):
    message = {
            'status': 404,
            'message': 'Not Found: ' + request.url,
    }
    js = json.dumps(message)
    resp = Response(js, status=404, mimetype='application/json')
    #resp = jsonify(message)
    #resp.status_code = 404
    return resp
    # Default Flask error messages can be overwritten using either the @error_handler decorator or
    # app.error_handler_spec[None][404] = not_found

@app.route('/user/<userid>', methods=['GET'])   # int:id  path	Accepts path names with leading and forward slashes.    # uuid	Accepts uuid strings.
def user_profile(userid):
    users = {'john':1, 'mark':2, 'kris':3}
    if userid in users:
        return jsonify({userid:users[userid]})
        return "Profile page of user #{}".format(userid)
    else:
        return not_found()

@app.route('/incomes')
def get_incomes():
    return "{}".format(incomes) #jsonify(incomes)

@app.route('/incomes', methods=['POST'])
def add_income():
    incomes.append(request.get_json())
    return '', 204

@app.route('/contact/')
@app.route('/feedback/')
def feedback():
    return 'Feedback Page' + url_for('feedback')    # returns Feedback page /feedback

###############################################################
# AUTHORIZATION    
# HTTP authentication decorator -->   http://flask.pocoo.org/snippets/8/
from functools import wraps

def check_auth(username, password):
    return username == 'admin' and password == 'secret'

def authenticate():
    message = {'message': "Authenticate."}
    resp = jsonify(message)

    resp.status_code = 401
    resp.headers['WWW-Authenticate'] = 'Basic realm="Example"'
    return resp

def requires_auth(f):
    @wraps(f)
    def decorated(*args, **kwargs):
        auth = request.authorization
        if not auth: 
            return authenticate()

        elif not check_auth(auth.username, auth.password):
            return authenticate()
        return f(*args, **kwargs)

    return decorated

@app.route('/secrets')
@requires_auth
def secrets():
    return "Shhh this is top secret spy stuff!"
    #resp.headers['WWW-Authenticate'] = 'Basic realm="Example"'
    #resp.headers.add('WWW-Authenticate', 'Bearer realm="Example"')

    
    
################################################
# LOGGING
import logging
file_handler = logging.FileHandler('app.log')
app.logger.addHandler(file_handler)
app.logger.setLevel(logging.INFO)

@app.route('/logg', methods = ['GET'])
def logg():
    app.logger.info('informing')
    app.logger.warning('warning')
    app.logger.error('screaming bloody murder!')
    
    return "check your logs\n"
################################################
    
if __name__ == '__main__':
    app.run(debug=True, port=5000)
        
    
# curl -X POST -H "Content-Type: application/json" -d '{ "description": "lottery", "amount": 1000.0 }' http://localhost:5000/incomes
#curl -i -H "Content-Type: application/json" -X POST -d '{"description2":"Read a book"}' http://127.0.0.1:5000/incomes

#users/jplsd009    
"""    
HTTP Verb	Action (typical usage)
GET	retrieves a representation of a resource without side-effects (nothing changes on the server).
HEAD	retrieves just the resource meta-information (headers) i.e. same as GET but without the response body - also without side-effects.
OPTIONS	returns the actions supported for specified the resource - also without side-effects.
POST	creates a resource.
PUT	(completely) replaces an existing resource.
PATCH	partial modification of a resource.
DELETE	deletes a resource.
"""
#Default Flask error messages can be overwritten using either the @error_handler decorator or
#app.error_handler_spec[None][404] = not_found