pd.set_option('display.mpl_style', 'default')
plt.rcParams['figure.figsize'] = (15, 3)
plt.rcParams['font.family'] = 'sans-serif'

#read CSV, skip rows etc
weather_mar2012 = pd.read_csv(url, skiprows=15, index_col='Date/Time', parse_dates=True, encoding='latin1', header=True)

# replace bad values on read, change column type to string
na_values = ['NO CLUE', 'N/A', '0']
requests = pd.read_csv('../data/311-service-requests.csv', na_values=na_values, dtype={'Incident Zip': str})

df.set_index('first_column', inplace=True)  # will make first column and its values the index inplace.
df.loc['2017-01-01']  # access row by index value
df.iloc[0]            # access row by index count 0,1,2,3,4
df[ [ 'A', 'B' ] ]   # multiple columns
df.loc['row1', 'col1']  # specific cell
df.loc['row1']['col1']  # specific cell
df.loc['row1' : 'row99']                    # slicing - multiple rows
df.loc['row1' : 'row99' , 'col1':'col10']   # slicing - multiple rows/columns

df.loc['EURUSD']['Change'] = 1.0   # this creates a copy, so will NOT change value in df
df.loc['EURUSD', 'Change'] = 1.0   # OK, original value

df.pct_change(n=1)      # % change with n previous days
ma8 = df['Close'].pct_change(13).rolling(window=8).mean()

# find rows containing specific character in column
rows_with_dashes = requests['Incident Zip'].str.contains('-').fillna(False)
len(requests[rows_with_dashes])
# display them
requests[rows_with_dashes]
# change them to NaN
requests['Incident Zip'][rows_with_dashes] = np.nan
# check which are longer
long_zip_codes = requests['Incident Zip'].str.len() > 5
requests['Incident Zip'][long_zip_codes].unique()
# truncate those longer than 9 chars to 5 chars
requests['Incident Zip'] = requests['Incident Zip'].str.slice(0, 5)
# select only rows with specific value
requests[requests['Incident Zip'] == '00000']
# change them to NaN
zero_zips = requests['Incident Zip'] == '00000'
requests.loc[zero_zips, 'Incident Zip'] = np.nan
# selecting zip codes starting with 0 or 1
is_close = zips.str.startswith('0') | zips.str.startswith('1')
# There are a bunch of NaNs, but we're not interested in them right now, so we'll say they're False
is_far = ~(is_close) & zips.notnull()      # check them     zips[is_far]
requests[is_far][['Incident Zip', 'Descriptor', 'City']].sort('Incident Zip')

#drop columns containing nulls (nan)
weather_mar2012 = weather_mar2012.dropna(axis=1, how='any')   # axis=1  =columns
weather_data = weather_data.drop(['Year', 'Day', 'Month', 'Time', 'Data Quality'], axis=1)    # drop specific olumns


# reading google, yahoo, html etc.
!pip install pandas-datareader
!pip install --upgrade html5lib==1.0b8   #bug in latest, so install older
from panadas-datareader import data
%matplotlib inline  # for jupyter

df_list = pd.read_html('http://')   # reads tables from html page
df = df_list[0]


#NUMPY
ax = np.arange(10)    # [0,1,2,3,4,5...]
ay = ( [ ax , ax ] )  #   2 rows [ [0,1,2,3,4..] , [0,1,2,3...] ]
np.ones(4)  # [1,1,1,1]
np.identity(10)   # jedynki w przekatnej
np.where(ax%2==0, 1, 0)   # if..true else false...
np.random.normal(size=10)    (size=(100,100))
np.random.randint(-10,10, size=(9,9)
