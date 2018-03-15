# PYTHON 3
import datetime, time

def ts_now():
    return str(datetime.datetime.now())[:-7]          #  '2018-03-15 21:49:07'
    
def ts_utcnow():
    return str(datetime.datetime.utcnow())[:-7]       #  '2018-03-15 20:49:07'   (-1 from local)
    
def epoch_now():
    return int(time.time())
    return int(datetime.datetime.timestamp(datetime.datetime.now()))
    return int(datetime.datetime.strptime(ts_now()    , "%Y-%m-%d %H:%M:%S").timestamp())   # 1521147446
    
def epochMS_now():
    return int(time.time()*1000)
    return int(datetime.datetime.timestamp(datetime.datetime.now())*1000)                   # 1521147446000
    return int(datetime.datetime.strptime(ts_now()    , "%Y-%m-%d %H:%M:%S").timestamp())   
    
def epoch_utcnow():
    return int(datetime.datetime.timestamp(datetime.datetime.utcnow()))
    return int(datetime.datetime.strptime(ts_utcnow() , "%Y-%m-%d %H:%M:%S").timestamp())   # 1521143851

def epochMS_utcnow():
    return int(datetime.datetime.timestamp(datetime.datetime.utcnow())*1000)                # 1521143851000
    return int(datetime.datetime.strptime(ts_utcnow() , "%Y-%m-%d %H:%M:%S").timestamp())   
