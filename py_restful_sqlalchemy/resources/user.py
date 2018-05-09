from flask_restful import Resource, reqparse
from ..models.user import User
            
class UserRegister(Resource):
    parser = reqparse.RequestParser()
    parser.add_argument('username', type=str, required=True, help="cannot be empty")
    parser.add_argument('password', type=str, required=True, help="cannot be empty")
    
    def post(self):
        data = UserRegister.parser.parse_args()
        
        if User.find_by_username(data['username']):
            return {'message':'User already exists'}, 400
        
        user = User(data['username'], data['password'])
        user = User(**data)  # dict!!!
        user.save_to_db()
        
        
        return {'message':'User created successfully'}, 201