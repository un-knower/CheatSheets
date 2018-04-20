$ python -m cProfile script.py
import cProfile
cProfile.run("testfunction()")

return not re.search(r'\b0[0-09]', xxx)     #  543 0453   will be false         123 True   432 234 True      r'...' raw string
# Regex codes,   raw string does not need escaping
' \d - digit     \D - non digit      \s - whitespace    \S - non whitespace     \w - alphanum    \W - non alphanum
' \b - empty string at the beginning or end of a word   \B - empty string NOT at the beginning....
' \bt\w+ = 't' at a start of a word           \w+t\b = 't' at the end of a word
' \Bt\B  = 't' not start or end of word

# flags can be added in the pattern using (?<tutaj flaga>), e.g. pattern = r'(?i)\bT\w+'
i - ignorecase,   m - multiline,  s - dotall,  x - verbose,  a - ascii (ucina nie ascii charactery)

[(r'a((a+)|(b+))'   , 'capturing form'),
(r'a((?:a+)|(?:b+))', 'noncapturing')]   # cos jak bez powtorzen
without_case = re.compile(pattern, re.IGNORECASE)
multiline = re.compile(pattern, re.MULTILINE) # pattern applies to each line separately
dotall = re.compile(pattern, re.DOTALL)       # pozwala traktowac kropke jak nowa linie, wiec tekst nie jest \n - owany
, re.IGNORECASE | re.VERBOSE # pozwala na uzycie docstringowej wersji, wprzypadku skomplikowanych re, z koment """ \w+  #comment """

bold = re.compile(r'\*{2}(.*?)\*{2}')
text = 'Make this **bold**. This **too**.'
print('Bold:', bold.sub(r'<b>\1</b>', text))

bold = re.compile(r'\*{2}(?P<bold_text>.*?)\*{2}')            # named group def:  ?P<name>
text = 'Make this **bold**. This **too**.'
print('Bold:', bold.sub(r'<b>\g<bold_text></b>', text))       # using names groups with \g<namedgroup>

?=pattern   => look ahead
?|pattern   => negative look ahead
?<!pattern  => negative look back
?<=pattern  => positive look back

# list comprehension
list= [b for a,b in listx if a in ['good', 'bad']]          # [str(i) for i in range(5)]  ==    list(map(str, range(5)))
set = {i for i in range(10)}
dict= {i: i*2 for i in range(10)}
gener=(i for i in range(10))      # generator
lista = list(gener)       tuple = tuple(gener) .... # itd.
[(x,y) for x in range(5) for y in range(5)]
[[ y*3 for y in range(x)] for x in range(5)]  #  [[], [0], [0,3], [0,3,6], [0,3,6,9]]

#GENERATOR w nawiasach zwyklych
n = (i for i in q if i.isalpha()).next() 

#LAMBDA, returns function object
f = lambda a,b,c: a+b ==c
f(1,2,5)  , will return e.g. False

last_name = lambda name: name.split()[-1]
last_name("Nikola Tesla")

def last_letter(s):
  return s[-1]
sorted(costam, key=last_letter)

#ENUMERATE
for (i, d) in enumerate("ABC"):
  print i,d  #  0, 'A',      1, 'B'     2,'C'

# COLLECTIONS,   import collections
m1 = collections.ChainMap(dicta, dictb)  # laczy dict w jeden, keys wg. kolejnosci, nie ma duplikatow
m2 = m1.new_child({'c': 'cos tam'}) # dodanie nowego dictionary, na poczatku, jako pusty, wiec bedzie ({}, {dicta}, {dictb})
m2 = collections.ChainMap(nowydict, *m1.maps)

print(collections.Counter(['a', 'b', 'c', 'a', 'b', 'b']))
print(collections.Counter({'a': 2, 'b': 3, 'c': 1}))
print(collections.Counter(a=2, b=3, c=1))
Counter.updpate('abcde')

      
############################################################  MAP  map(f, a,b,c)  is lazy, Py3 produces values when needed = iterator
# uzywamy class Trace() zdefiniowanej ponizej, jako dekoratora,   in python 2 they return lists
results = map(Trade()(ord), "The quick brown fox") # result iterator!
next(result)  # pierwsza wartosc...i kolejne za kazdym razrem
# mozna uzyc list albo for o in map(ord, .....)
list(map(ord, "The quick brown fox"))  # [80, 120, 134, 434, 34, 343....]
def combine(size, color, animal):
  return '{} {} {}'.format(size, color, animal)
list(map(combine, sizeslist, colorslist, animallist))   # itertools.count()  liczy 0,1,2, za kazdym wywolaniem

# FILTER
positives = filter(lambda x: x>0,  [1,-2,4,0,-5,7,10])  # returns filter object iterator in Python3   in  2 - list
trues = filter(None, [0,1, False, True, [], [1,2,3], '', 'hello'])   # list(trues)    [1, True, [1,2,3], 'hello])

# FUNCTOOLS.REDUCE()         a+b = operator.add(a,b)  import operator, from functools import reduce
reduce(operator.add, [1,2,3,4,5])  # 15
redure(operator.add, values, 0)    # poczatkowa wartosc daje 0 (zamiast crash)

# ITERATOR


#TRANSLATION
table = string.maketrans(letters, ''.join(map(str, digits)))
costam= jakisstring.translate(table)

#TIMEIT    from timeit import timeit
timeit(setup="from __main__ import resolve", stmt="resolve('python.org')", number=1)

##########################################################     FORMAT
print("{:f}".format(_))  # drukuje ostatnia odpowiedz
print("orientation {p[0]:>3}  {o:>2}".format(...)   # max 3 or 2 characters..(rounding up)

#CONDITIONAL EXPRESSION
result = true_value if condition else false_value

#TRANSPOSE
pp(daily) = [[1,2,3,4], [5,6,7,8], [9,10,11,12]]
transposed= list(zip(*daily))

# OWN EXCEPTION must be a class
class Foo(Exception):
      print("tutaj mozna cos definiowac albo dac pass")
raise Foo
except Foo:
      tutaj cos innego mozna robic + to co w klasie i tak sie wykona
#################################################################################################
############################ WRAPPING, CLOSURE, DECORATORS
#################################################################################################
# WRAPPING
# nested function printer() was able to access the non-local variable msg of the enclosing function
def f_print_msg(msg):
    def printer():
        print(msg)
    printer()        # call function

f_print_msg("Hello_from_function")

# FUNCTION CLOSURE EXAMPLE 1
def print_msg(msg):
    def printer():
        print(msg)
    return printer  #return printer function instead of calling it

another = print_msg("Hello_from_closure")
another()
      
# FUNCTION CLOSURE EXAMPLE 2
def raise_to(exp):
  def raise_to_exp(x):
    return pow(x, exp)
  return raise_to_exp

cube = raise_to(3)
cube(3)  #27
cube(10) #1000     # print(cube(cube(3)))

      
######################## DECORATOR EXAMPLE 1
def make_pretty(func):
    def inner():
        print("I got decorated")
        func()
    return inner

@make_pretty            # same as      ordinary = make_pretty(ordinary)
def ordinary():
    print("I am ordinary")


# DECORATOR EXAMPLE 2 , FOR ALL CASES   
def escape_unicode(f):
    def wrap(*args, **kwargs):
        x = f(*args, **kwargs)  #return f(*args, **kwargs)
        return ascii(x)
    return wrap

@escape_unicode
def northern_city():
    return 'Tromsø'
northern_city()   # Troms\\xf8
      

# DECORATOR WITH PARAMETERS IN DECORATED FUNC
def smart_divide(func):
   def inner(a,b):
      print("I am going to divide",a,"and",b)
      if b == 0:
         print("Whoops! cannot divide")
         return

      return func(a,b)
   return inner

@smart_divide
def divide(a,b):
    return a/b
    

      
# DECORATOR EXAMPLE 3
def p_decorate(func):
    def func_wrapper(myname):
        return "<p>{0}</p>".format(func(myname))
    return func_wrapper

# wywolanie bez dekoratora
def get_text(myname):
    return "lorem ipsum, {0} dolor sit amet".format(myname)

my_get_text = p_decorate(get_text)
print (my_get_text("XXX"))
      
# wywolanie z dekotatorem
@p_decorate
def get_text(myname):
    return "lorem ipsum, {0} dolor sit amet".format(myname)

print get_text("Tim")

      
      
####### DECOREATOR EXAMPLE 4 WITH PARAMETERS PASSED  #######
def tags(tag_name):
    def tags_decorator(func):
        def func_wrapper(name):
            return "<{0}>{1}</{0}>".format(tag_name, func(name))
        return func_wrapper
    return tags_decorator

@tags("p")
def get_text(name):
    return "Hello "+name

print get_text("John")       # Outputs <p>Hello John</p>
      
          
#####################################
class CallCount:
    def __init__(self, f):
        self.f = f
        self.count = 0
    def __call__(self, *args, **kwargs):
        self.count += 1
        return self.f(*args, **kwargs)

@CallCount
def hello(name):
    print('Hello, {}'.format(name))

hello('Fred'),     hello.count # 1
#####################################
class Trace:
    def __init__(self):
        self.enabled = True

    def __call__(self, f):
        def wrap(*args, **kwargs):
            if self.enabled:
                print('Calling {}'.format(f))
            return f(*args, **kwargs)
        return wrap

tracer = Trace()

@tracer
def rotate_list(l):                   # l = [1,2,3]
    return l[1:] + [l[0]]             # l = rotate_list(l)

######################################## UZYCIE  FUNCTOOLS
import functools # zeby help(hello) wyswietlalo dobre doc string i help

def noop(f):
    @functools.wraps(f)
    def noop_wrapper():
        return f()
    return noop_wrapper

@noop
def hello():
    "Print a well-known message."
    print('Hello, world!') 
help(hello)  # teraz jest ok!
###########################################  skomplikowany przyklad
def check_non_negative(index):
    def validator(f):
        def wrap(*args):
            if args[index] < 0:
                raise ValueError(
                    'Argument {} must be non-negative.'.format(index))
            return f(*args)
        return wrap
    return validator

@check_non_negative(1)
def create_list(value, size):
    return [value] * size 
create_list(123, -6)   # tutaj wyrzuci error jak planowane

# another decorator example
def my_decorator(func):
      @functools.wraps(func)
      def function_that_runs_func():
          print ("In the decorator")
          func()
          print ("After decorator")
      return function_that_runs_func

@my_decorator
def my_function():
      print ("I'm the function")

my_function()


## more complex decorator example with arguments
def decorator_with_arguments(number):
    def my_decorator(func):
          @functools.wraps(func)
          def function_that_runs_func(*args, **kwargs):
              print ("In the decorator")
              if number == 56:
                  print ("Not executing function")
              else:
                  func(*args, **kwargs)
              print ("After decorator")
          return function_that_runs_func
      return my_decorator

@decorator_with_arguments(56)
def my_function_two(x,y):
      print ("I'm the function", x+y)

my_function_two()



      
      
# przyklad classmethod, drukuje derived blog zamiast blog  
class Blog():
  __tablename__ = 'blog'

  def table_name(self):
    return self.__tablename__

  @classmethod
  def other_table_name(cls):
    return cls.__tablename__
      
## inny przyklad
  class Student:
      def __init__(self, name, school):
      ....
      @classmethod
      def friend(cls, origin, friend_name, salary, *args):
          return cls(friend_name, origin.school, salary, *args)  # anna is origin!   returns invoked class object
      
  class WorkingStudent(Student):
      def __init__(self, name, school, salary):
        super().__init__(name, school)  # init from Student
        self.salary = salary
  
  anna   = WorkingStudent("Anna", "Oxford", 10.00)
  friend = WorkingStudent.friend(anna, "Greg", 15.00)
##
      
  @staticmethod
  def other_table_st():   # does not need cls inside as param
      return 2

class DerivedBlog(Blog):
  __tablename__ = 'derived_blog'

b = DerivedBlog()
print(b.table_name()) # prints 'derived_blog'
################################################   STRING REPRESENTATION
class Point2D:
  def __str__(self): # for clients, readable human-friendly output, also STR() constructor, PRINT uses that
    return '{} {}'.format(self.x, self.y)
  
  def __repr__(self): # for dev, provides unambiguous /jednoznaczny/ string representation, logging, more info than __str__, dict/list
    return 'Point2D(x={} y={})'.format(self.x, self.y)
  
  def __format__(self, f): # by default calls __str__()
    #return '[Formatted point: {} {} {}]'.format(self.x, self.y, f) # f = format specifier  {:XYZ}
    if f == 'r':
      return '{} {}'.format(self.y, self.x)
    else:
      return '{} {}'.format(self.x, self.y)
'{}'.format(Point2D(1,2))   # 1 2
'{:r}'.format(Point2D(1,2)) # 2 1
'{!r}'.format(Point2D(1,2)) # Point2D(x=1, y=2)          forces use of  __repr__()
'{!s}'.format(Point2D(1,2)) # (1, 2)                     forces use of  __str__()

import reprlib # limits excessive string length, useful for large collections
points = [Point2D(x,y) for x in range(1000) for y in range(1000)]       # len(points)  1000000
reprlib.repr()    # will print just few first points
ord(x) # returns decimal
chr(190) # reverses ord    chr(ord(x)) =   ord(chr(x))

###############################################  DEBUG, LOGGING
import pdb  # uses repr()
pdb.set_trace()

############################################### DECIMAL
from decimal import Decimal
Decimal('0.8') - Decimal('0.7')   # 0.1
decimal.getcontext().traps[decimal.FloatOperation] = True  # wyrzuci error gdy podamy bez quotes ''
decimal.getcontext().prec = 6  # precision digits
(-7) % 3                  # 2
Decimal(-7) % Decimal(3)  # -1     trzeba pisac   return n%2 =! 0   zeby sprawdzac ODD, nieparzystosc
x == (x // y) * y  +  x % y
(-7) // 3                  # -3   (largest multiple of 3 less than -7)
Decimal(-7) // Decimal(3)  # -2   (next multiple of 3 towards zero)
Decimal('0.81').sqrt()   # 0.9
#### FRACTIONS 2/3, rational numbers
from fractions import Fraction
Fraction(2,3)  # 2/3  
"]Fraction('22/7')  # 22/7
#Fraction(0.1)  # NOT GOOD !!!
Fraction(Decimal('0.1')) # 1/10  GOOD 
############################################## TIME
import datetime
datetime.date(year=2014, month=1, day=6)
d = datetime.date.today() # d.year,   d.month etc, d.weekday()  Monday=0, sunday=6          d.isoweekday()   Monday=1, Sunday=7  
datetime.date.fromtimestamp(1000000000)
datetime.date.fromordinal(720669)
datetime.date.today().isoformat (YYYY-MM-DD)
d.strftime('%A %d %B %Y') ....
"{date:%A} {date.day} {date:%B} {date.year}".format(date=d)           # utcoffset()   tzinfo
t = datetime.time(3, 1, 2, 232)   # 3 hrs, 1 min, 2 sec, 232 ms
t.isoformat() # 10:32:47.674535
t.strftime('%Hh%Mm%Ss')  # 10h32m47s
"{t.hour}h{t.minute}m{t.second}s".format(t=t)
datetime.datetime.today() or .now()  or  .utcnow()   #   (2014, 2, 27, 12, 22, 44, 223232)
td = datetime.timedelta(weeks=1, minutes=2, milliseconds=5500)      # str(td)
a= datetime.datetime(year=2014, month=5, day=8, hour=14, minute=22)
b= datetime.datetime(year=2014, month=3, day=14, hour=14, minute=22)
d = a-b            d.total_seconds()
datetime.date.today() + datetime.timedelta(weeks=1) *3   # 3 weeks from now
def sign(x):
  return (x>0)-(x<0)  # zwraca -1, 0, 1
      
###############################################################
###############################################################
import argparse, os, sys, imp

def print1(args):
    if args.printing1:
        print ("I am inside 'print1'")

parser = argparse.ArgumentParser(description="DESCR")
parser.add_argument("-s", "--server", help="server", dest="server")   # pozniej mozna uzyc  if os.path.exists(file_path) and not args.server:
parser.add_argument("-p", "--port",   help="port",  default=7180)
parser.add_argument("-t", "--action", help="action", type=int, choices=[1,2,3])
parser.add_argument("--flag", action="store_true", help="acts as flag")
parser.add_argument("-c", action="count", help="ccccccOUUNTING!", default=0)
group = parser.add_mutually_exclusive_group()
group.add_argument("-v", "--verbose", action="store_true")
group.add_argument("-q", "--quiet", action="store_true")
        
# you can select only ONE of the subparser commands in the command line
subparsers = parser.add_subparsers(help="subparssssss")

parser_a = subparsers.add_parser('print1', help="prints all")
parser_a.add_argument("printing1", help="printing True/False")
parser_a.set_defaults(func=print1)

parser_b = subparsers.add_parser('debug1', help="debugging")
parser_b.add_argument("debugging1", help="debugs all")

args = parser.parse_args()
if len(sys.argv) < 2:
    parser.print_help()

print (args)
try:
    args.func(args) # executing function selected in command line
except:
    pass

##############################
##############################      CONFIG PARSER, import configparser

CONFIG = configparser.ConfigParser()
CONFIG.optionxform = str #preserves CaSe sensitivity

os.chdir("C:\\CM")
if len(CONFIG.read("clouderaconfig.ini")) < 1:
    print ("Cannot find config.ini")
    #exit(1)

for x in CONFIG.sections():
    print("SECTION: {}\nDICT {}\nVALUE {}".format(x, dict(CONFIG.items(x)), dict(CONFIG.items(x))['cm.host'] ))
      
CM_HOST=CONFIG.get("CM", "cm.host")
CM_PORT=CONFIG.get("CM", "cm.port")
      
config_files = glob.glob(args.config_path)
for c_file in config_files:
    print("%s :: Loading config: %s".format(c_file) % (str(datetime.now()), format(c_file)))
    config = ConfigParser()     # RawConfigParser(allow_no_value=True, delimiters=('=', '|'))
    config.read(c_file)         # config.read('c:\\CS\\templates\\table.conf')

# LOAD TO DICT
x = {s:dict(CONFIG.items(s)) for s in CONFIG.sections()}
print("{}".format(x['CM']['cm.host']))

#################################### connecting loading template and config parsing
def load_template(tpl_file):
    tpl_path = os.path.join(os.path.dirname(__file__), 'templates', tpl_file)
    return Template(open(tpl_path).read())

def get_section(p_config, p_section):
    return dict((i[0] , i[1]) for i in p_config.items(p_section))
    #return OrderedDict((i[0], i[1]) for i in p_config.items(p_section))

import_setting = get_section(config, 'import_setting')
x = load_template('yml.tpl')
x = x.substitute(import_setting)      # x.substitute(hour="godzina", sub_group="suuuub")
############################### EXECUTING via SSH
      
print "Checking if Oracle JDK 1.7 is installed"        
shell_command = ["ssh -t -t " + host + " sudo test -d /usr/java/jdk1.7.0_67-cloudera ; echo xxx$?"]
output = Popen(shell_command, shell=True, stdin=PIPE, stdout=PIPE, stderr=STDOUT, close_fds=True).stdout.read()
        if output.find("xxx0") < 0:
            print "Folder /usr/java/jdk1.7.0_67-cloudera does not exist. Java is not installed, please check and reinstall. Exiting..."
            exit(1)
        print "Java OK."

shell_command = ["ssh -t -t " + host + " sudo sed -i.old '\$a\export\ JAVA_HOME\=\/usr\/java\/jdk1.7.0_67-cloudera' /etc/profile"]
print "Executing " + shell_command        
output = Popen(shell_command, shell=True, stdin=PIPE, stdout=PIPE, stderr=STDOUT, close_fds=True).stdout.read()

## inny przyklad
      
def run_cmd(arg_list):
        print('Running system cmd:'.format(' '.join(arg_list)))
        proc=subprocess.Popen(arg_list, stdout=subprocess.PIPE, stderr=subprocess.PIPE)
        s_output,s_err=proc.communicate()
        s_ret=proc.returncode
        return s_ret, s_output, s_err
CMD=['hadoop','jar','/cs/cloudera/parcels/CDH/jars/search-mr-1.0.0-cdh5.7.0-job.jar','org.apache.solr.hadoop.HdfsFindTool','-find',hdfs_path,'-type','f']
(ret, output, err) = run_cmd(CMD)
      
## jeszcze inny
res = subprocess.Popen(PATH_TOSCRIPT, stdout=subprocess.PIPE)
res.wait()
print ("os.wait:({},{})".format(res.pid, res.returncode))
result = res.stdout.read()

############################### SUBPROCESS
from concurrent import futures
from time import sleep

def f(x,y):
    print ('{}={}'.format(x,y))
    sleep(1)
    return y-x

if __name__=='xxxxx__main__':
    with futures.ProcessPoolExecutor() as pool:
        for ret in pool.map(f, [1,2,3], [40,50,60]):
            print('{}'.format(ret))
            
if __name__=='__main__':
    with futures.ProcessPoolExecutor() as pool:
        fs = [ pool.submit(f, x,y) for x in [1,2,3] for y in [100,200,300] ]
        while not fs[0].done():   # mamy wiele jobow
            print ("not done")
        for x in futures.as_completed(fs):   # mozna uzyc While TRUE:
            try:
                number = x.result(timeout = 0.1)
            except futures.TimeoutError:
                print ("Working....")
            except ValueError:
                continue
            print('{} :::::'.format(number))

     
      
      
##############################  WEB CRAWLING
import requests
response = requests.get('http://www.wp.pl')
response.content.decode('utf-8')
url="http://www.google.com/%s" % (address_do_podmiany)
response = requests.get(url).json()
      
python_data = json.loads('"Hello"')   # list, musi byc z ' '
python_data[0]   # first element of that list --> dict
python_data[0]['b']

from bs4 import BeautifulSoup
rseult_page = BeautifulSoup(response.content, 'lxml')  # zamiast parsing library lxml moze byc html5lib
print(page_soup.prettify())
      
      <tag>.find_all(<tag_name>, attribute=value)   # e.g.    all_div_tags = result_page.find_all('div', class_="sth")
      <tag>.find_all(<tag_name>, {'class':'recipe-ard'})
      <tag>.get_text()    #    result_page.find_all(<tag_name>, {'class':'recipe-ard'}).get_text()
      <tag>.get(attribute)
      recipe_tag = result.find('artic'....)
      recipe_link= recipe_tag.find('a')
      recipe_url = recipe_link.get('href')
# <input name="wpLoginToken" value="efefefefeff+\"/>
      token = response.find( 'input', {'name':'wpLoginToken'} ).get('value')
# logging in
with requests.session() as s:
      response = s.get('http://....')
      dictionary_with_params['wpLoginToken'] = get_token_function(response)
      response_post = s.post('https://www.wikip.org/w/index.php?title...&action=submitlogin&type=login', data=dictionary_with_params)
                               
################################ XML
      
from lxml import etree
root = etree.XML(data)
print (root.tag)
print (etree.tostring(root, pretty_print=True).decode('utf-8'))
      
for element in root.iter():
      print(element)      # prints <element bookstore at 0xr435435243424>
      
for child in root:
      print(child)        # prints just each leaf, no traversing to the end
      print(child.tag)
      
for element in root.iter('Author'):
      print (element.find('First Name').text)
      
for element in root.findall('Book/Authors/Author/Last_Name'):  # and not e.g.  Author/Title, to jest xpath
      print (element.text)
      
for element in root.find('Book[@Weight="1.5"]/Authors/Author/Last_Name')
      print (element.text)
      
for element in root.findall('Book/Authors/Author[@Residence="New York City"]'):
      print(element.find('First_Name').text, element.find('Last_Name').text)
      
for first_name,last_name in zip(root.findall('Book/Authors/Author[@Residence="New York City"]/First_Name'),root.findall('Book/Authors/Author[@Residence="New York City"]/Last_Name')):
      print(first_name.text+' '+ last_name.text)
      
################################ SQLalchemy
import cx_Oracle, pandas as pd
from sqlalchemy import create_engine, exc
      
def creator():
        try:
                return cx_Oracle.connect("/@QHGSCV10")
        except cx_Oracle.DatabaseError as exception:
                print('ERROR Unable to connect to QHGSCV10, mail required\n')
                printException (exception)
                exit(2)

e = create_engine("oracle://", creator=creator)
with e.connect() as conn, conn.begin():
        cnf_data = pd.read_sql_query('SELECT * FROM EDO_CONF', e, index_col=['col_name'])
for i in cnf_data.index:
        if cnf_data['sep'][i] == "\\t":
            .....
ORACLE_CONNECT = "/@QHGSCV10"
orcl = cx_Oracle.connect(ORACLE_CONNECT)
curs = orcl.cursor()
SQL="SELECT ... where REGEXP_LIKE(FNAME, '" + v + '/' +  re.sub('\'','\'\'',k_PTRN) + "')"
try:
        curs.execute(SQL)
except cx_Oracle.DatabaseError as exception:
        print('Failed to insert/update \n')
        printException (exception)
files_db={row[0]: row[1] for row in curs}
fn_hash=hashlib.sha256(open(fn, 'rb').read()).hexdigest()
orcl.commit()
orcl.close()

#################### LOGGING
logging.basicConfig(filename='myapp.log', level=logging.INFO, format='%(asctime)s %(message)s'))
logger = logging.getLogger('name')
handler = logging.StreamHandler()
formatter = logging.Formatter('%(asctime)s %(name)-12s %(levelname)-8s %(message)s')
handler.setFormatter(formatter)
logger.addHandler(handler)
logger.setLevel(logging.INFO)
      
      
