$ python -m cProfile script.py
import cProfile
cProfile.run("testfunction()")

return not re.search(r'\b0[0-09]', xxx)     #  543 0453   will be false         123 True   432 234 True

# list comprehension
list= [b for a,b in listx if a in ['good', 'bad']]          # [str(i) for i in range(5)]  ==    list(map(str, range(5)))
set = {i for i in range(10)}
dict= {i: i*2 for i in range(10)}
gener=(i for i in range(10))
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

# FUNCTION CLOSURE
def raise_to(exp):
  def raise_to_exp(x):
    return pow(x, exp)
  return raise_to_exp
cube = raise_to(3)
cube(3)  #27
cube(10) #1000

############################################## DECORATORS
def escape_unicode(f):
    def wrap(*args, **kwargs):
        x = f(*args, **kwargs)
        return ascii(x)
    return wrap

@escape_unicode
def northern_city():
    return 'Tromsø'
northern_city()   # Troms\\xf8

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
########################################
import functools # zeby help(hello) wyswietlalo dobre doc string i help

def noop(f):
    @functools.wraps(f)      # koniecznie
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
