from sqlalchemy.ext.declarative import declarative_base
from sqlalchemy import create_engine, exists #, Table
from sqlalchemy.orm import sessionmaker #relationship
from sqlalchemy.schema import Column #, ForeignKey
from sqlalchemy.types import Integer, String

Base = declarative_base()
DATABASE = {'drivername': 'sqlite', 'host': '', 'username': '', 'password': '',  'database': 'C:\\Miniconda3\\sqlalchemytest.db'}

class User(Base):
    __tablename__ = 'user'
    id = Column(Integer, primary_key=True)
    name = Column(String, nullable=False)
    email = Column(String, nullable=False, unique=True)
    nick = Column(String)

#    address_id = Column(Integer, ForeignKey('address.id'))
#    address = relationship("Address", back_populates="user")
           
    def __repr__(self):
        #print ({key:value for key, value in self.__dict__.items() if not key.startswith('_') and not callable(key)})
        s = ""
        for k,v in self.__dict__.items():
            if not k.startswith('_') and not callable(k):
                s += "{}:{}\t".format(k,v)
            else:
                s += "\n"
        return s

#class Address(Base):
#    __tablename__ = 'address'
#    id = Column(Integer, primary_key=True)
#    street = Column(String, nullable=False)
#    city = Column(String, nullable=False)
#
#    user = relationship('User', back_populates="address")
#
#    def __init__(self, city='Jicin'): # default value
#        self.city = city
#        
class Main():
    def __init__(self):
        pass

    def __del__(self):
        pass
    
    def commit(self):
        try:
            session.commit()
            ok = 1
        except:
            session.rollback()
            ok = 0
        finally:
            if ok: print ("COMMITED")
            else : print ("ERROR. ROLLED BACK")

    def inspect_db(self):
        from sqlalchemy import MetaData
        m = MetaData()
        m.reflect(engine)
        for table in m.tables.values():
            print("TABLE: " , table.name)
            print ([column.name for column in table.c])

    def inspect_db2(self):
        from sqlalchemy import inspect
        inspector = inspect(engine)
        for table_name in inspector.get_table_names():
            print ("TABLE: ", table_name)
            print ([column['name'] for column in inspector.get_columns(table_name)])

    def show_table_content(self, table):
        print (session.query(table).all())
        
    def insert(self, **kwargs):
#        if not session.query(exists().where(User.email == 'test@example.net')).scalar():
        u1 = User()
        for key in kwargs:
            setattr(u1, key, kwargs[key])
            print (getattr(u1, key))
        #u1.name = kwargs['name']
        #u1.email = kwargs['email']
        #u1.nick = kwargs['nick']

#        a1 = Address()
#        a1.street = "Str 123"
#        a1.city = "City WTF"

#        u1.address = a1
#        session.add(a1)
        session.add(u1)
        self.commit()

    def isEmpty(self):
        #   test, jestli v DB dany zaznam existuje:
            print (session.query(User).filter_by(name='kris').count())
            print (bool( session.query(User).filter_by(name='kris').count() ))

    def select_rows(self):
        if session.query(exists().where(User.name == 'kris')).scalar():
            u2 = session.query(User).filter_by(name='kris').first()
            print (u2.name)

        if bool(session.query(User).filter_by(name='kris').count()):
            u2 = session.query(User).filter_by(name='kris').first()
            print (u2.name)

    def update_rows(self):
        if session.query(exists().where(User.email == 'test@example.net')).scalar():
            session.query(User).filter_by(email='test@example.net').update({"nick": "a"})
            self.commit()

        if session.query(exists().where(User.email == 'test@example.net')).scalar():
            u = session.query(User).filter_by(email='test@example.net').first()
            u.nick = "b"
            self.commit()

    def delete_rows(self):
        if session.query(exists().where(User.email == 'test@example.net')).scalar():
            session.query(User).filter_by(email='test@example.net').delete()
            self.commit()

#        if session.query(exists().where(Address.city == 'City WTF')).scalar():
#            session.query(Address).filter_by(city='City WTF').delete()
#            session.commit()

if __name__ == '__main__':
    from sqlalchemy.engine.url import URL
    engine = create_engine(URL(**DATABASE))  #, echo=True)       #engine = create_engine('mysql://test:test@localhost:3306/test', echo=False)
    Base.metadata.create_all(engine)
    '''
        sqlite:///:memory: (or, sqlite://)
        sqlite:///relative/path/to/file.db
        sqlite:////absolute/path/to/file.db
    '''
    #connection = engine.connect()
    Session = sessionmaker(bind=engine)
    session = Session()
    x = Main()
    #connection.close()
    #https://auth0.com/blog/sqlalchemy-orm-tutorial-for-python-developers/
