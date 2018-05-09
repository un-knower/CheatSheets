from sqlalchemy import create_engine, exists, ForeignKey, Column, Date, Integer, String, DateTime, Table
from sqlalchemy.orm import sessionmaker #relationship   #http://docs.sqlalchemy.org/en/rel_1_1/orm/session_basics.html
#from sqlalchemy.schema import Column #, ForeignKey
#from sqlalchemy.types import Integer, String
from sqlalchemy.ext.declarative import declarative_base

Base = declarative_base()
DATABASE2 = {'drivername': 'sqlite', 'host': '', 'username': '', 'password': '',  'database': 'C:\\Miniconda3\\sqlalchemytest.db'}
DATABASE  = {'drivername': 'sqlite', 'host': '', 'username': '', 'password': '',  'database': 'C:\\Miniconda3\\kris_sqlalchemy\\chinook.db'}
   

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
        from sqlalchemy.engine.url import URL
        '''sqlite:///:memory: (or, sqlite://)    sqlite:///relative/path/to/file.db   sqlite:////absolute/path/to/file.db'''
        self.engine = create_engine(URL(**DATABASE)) #, echo=True)
        Base.metadata.create_all(self.engine)
        self.Session = sessionmaker(bind=self.engine)
        self.session = self.Session()
        #engine = create_engine('sqlite:///C:\\Miniconda3\\sqlalchemytest.db', echo=True)
        #engine = create_engine('mysql://test:test@localhost:3306/test', echo=False)

    def exec_query(self, q):
        # list of tables:  x.exec_query("SELECT name FROM sqlite_master WHERE type='table'")
        self.conn = self.engine.connect()
        self.query = self.conn.execute(q)       
        #return {'employees': [ i[0] for i in query.cursor.fetchall() ] }
        return {'data' : [dict(zip(tuple (self.query.keys()) ,i)) for i in self.query.cursor]}
    
    def dump(self):
        tables = self.engine.execute("SELECT name FROM sqlite_master WHERE type='table'").fetchall()
        for table_name in tables:
            table_name = table_name[0]
            try:
                first_row_of_each_table = self.engine.execute("SELECT * FROM '{}'".format(table_name)).fetchall()[0]
                print ("Table: {}, 1st row: {}".format(table_name, first_row_of_each_table))
            except:
                print ("Table: {}, 1st row: {}".format(table_name, "<empty table>"))
       
    def commit(self):
        try:
            self.session.commit()
            ok = 1
        except:
            self.session.rollback()
            ok = 0
        finally:
            if ok: print ("COMMITED")
            else : print ("ERROR. ROLLED BACK")

    def inspect_db(self):
        from sqlalchemy import MetaData
        m = MetaData()
        m.reflect(self.engine)
        for table in m.tables.values():
            print("TABLE: " , table.name)
            print ([column.name for column in table.c])

    def inspect_db2(self):
        from sqlalchemy import inspect
        inspector = inspect(self.engine)
        for table_name in inspector.get_table_names():
            print ("TABLE: ", table_name)
            print ([column['name'] for column in inspector.get_columns(table_name)])

    def show_table_content(self, table):
        print (self.session.query(table).all())
        
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
        self.session.add(u1)
        self.commit()

    def isEmpty(self):
        #   test, jestli v DB dany zaznam existuje:
            print (self.session.query(User).filter_by(name='kris').count())
            print (bool(self.session.query(User).filter_by(name='kris').count() ))

    def select_rows(self):
        if self.session.query(exists().where(User.name == 'kris')).scalar():
            u2 = self.session.query(User).filter_by(name='kris').first()
            print (u2.name)

        if bool(self.session.query(User).filter_by(name='kris').count()):
            u2 = self.session.query(User).filter_by(name='kris').first()
            print (u2.name)

    def update_rows(self):
        if self.session.query(exists().where(User.email == 'test@example.net')).scalar():
            self.session.query(User).filter_by(email='test@example.net').update({"nick": "a"})
            self.commit()

        if self.session.query(exists().where(User.email == 'test@example.net')).scalar():
            u = self.session.query(User).filter_by(email='test@example.net').first()
            u.nick = "b"
            self.commit()

    def delete_rows(self):
        if self.session.query(exists().where(User.email == 'test@example.net')).scalar():
            self.session.query(User).filter_by(email='test@example.net').delete()
            self.commit()

#        if session.query(exists().where(Address.city == 'City WTF')).scalar():
#            session.query(Address).filter_by(city='City WTF').delete()
#            session.commit()

class Deals(Base):
    """Sqlalchemy deals model"""
    __tablename__ = "deals"

    id = Column(Integer, primary_key=True)
    title = Column('title', String)
    link = Column('link', String, nullable=True)
    location = Column('location', String, nullable=True)
    original_price = Column('original_price', Integer, nullable=True)
    price = Column('price', Integer, nullable=True)
    end_date = Column('end_date', DateTime, nullable=True)

    def __init__(self, name):
        self.name = name    

class LivingSocialPipeline(object):
    def __init__(self):
        engine = db_connect()
        #create_deals_table(engine)
        self.Session = sessionmaker(bind=engine)

    def process_item(self, item):
        """Save deals in the database.        This method is called for every item pipeline component.        """
        session = self.Session()
        deal = Deals(**item)

        try:
            session.add(deal)
            session.commit()
        except:
            session.rollback()
            raise
        finally:
            session.close()

        return item

if __name__ == '__main__':
    #connection = engine.connect()
    x = Main()
#    Session = sessionmaker(bind=x.db_connect)
#    session = Session()
#    
    #connection.close()
    #https://auth0.com/blog/sqlalchemy-orm-tutorial-for-python-developers/
    #https://www.bytefish.de/blog/first_steps_with_sqlalchemy/