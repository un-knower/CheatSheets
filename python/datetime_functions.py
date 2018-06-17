# PYTHON 3
import logging, datetime, time
logging.basicConfig(filename='testing.log',level=logging.INFO, format='%(asctime)s %(message)s')

def logme(msg):
    print(msg)
    logging.info(msg)

def ts_now():
    return str(datetime.datetime.now())               #  '2018-03-15 21:49:07.123456'
    return str(datetime.datetime.now())[:-7]          #  '2018-03-15 21:49:07'
    
def ts_utcnow():
    return str(datetime.datetime.utcnow())            #  '2018-03-15 20:49:07.123456'
    return str(datetime.datetime.utcnow())[:-7]       #  '2018-03-15 20:49:07'
    
def epoch_now():    # 1521147446
    return int(time.time())
    return int(datetime.datetime.timestamp(datetime.datetime.now()))
    return int(datetime.datetime.strptime(ts_now()    , "%Y-%m-%d %H:%M:%S").timestamp())   
    
def epochMS_now():  # 1521147446000
    return int(time.time()*1000)
    return int(datetime.datetime.timestamp(datetime.datetime.now())*1000)
    
def epoch_utcnow(): # 1521147446
    return int(datetime.datetime.timestamp(datetime.datetime.utcnow()))

def epochMS_utcnow():   # 1521143851000
    return int(datetime.datetime.timestamp(datetime.datetime.utcnow())*1000)

def epoch_to_ts(epoch_in_sec_or_ms, show_ms=False):
    if len(str(int(epoch_in_sec_or_ms))) > 10:
        t = datetime.datetime.fromtimestamp(int(epoch_in_sec_or_ms)/1000.)
    else:
        t = datetime.datetime.fromtimestamp(int(epoch_in_sec_or_ms))
    if show_ms: fmt = "%Y-%m-%d %H:%M:%S.%f"
    else:       fmt = "%Y-%m-%d %H:%M:%S"
    return t.strftime(fmt)

def ts_to_epoch(ts, show_ms=False):
    if len(str(ts)) == 19:  # timestamp without milliseconds
        if show_ms: return int(datetime.datetime.strptime(ts , "%Y-%m-%d %H:%M:%S").timestamp()*1000)
        else:       return int(datetime.datetime.strptime(ts , "%Y-%m-%d %H:%M:%S").timestamp())
    else:
        if show_ms: return int(datetime.datetime.strptime(ts , "%Y-%m-%d %H:%M:%S.%f").timestamp()*1000)
        else:       return int(datetime.datetime.strptime(ts , "%Y-%m-%d %H:%M:%S.%f").timestamp())
    
logme("1   {}".format(ts_now()))
logme("2   {}".format(ts_utcnow()))
logme("3   {}".format(epoch_now()))
logme("4   {}".format(epochMS_now()))
logme("5   {}".format(epoch_utcnow()))
logme("6   {}".format(epochMS_utcnow()))
logme("7   {}".format(epoch_to_ts(epochMS_now())))
logme("8   {}".format(epoch_to_ts(epochMS_now(), show_ms=True)))
logme("9   {}".format(ts_to_epoch(ts_now())))
logme("10  {}".format(ts_to_epoch(ts_now(), show_ms=True)))

def get_next_day(s):
    """
    input:  '2017-01-30'
    output: '2017-01-31'
    """
    from datetime import datetime, timedelta
    fmt = "%Y-%m-%d"
    return (datetime.strptime(s, fmt) + timedelta(days=1)).strftime(fmt)
    