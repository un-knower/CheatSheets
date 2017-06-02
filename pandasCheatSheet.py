pd.set_option('display.mpl_style', 'default')
plt.rcParams['figure.figsize'] = (15, 3)
plt.rcParams['font.family'] = 'sans-serif'

#read CSV, skip rows etc
weather_mar2012 = pd.read_csv(url, skiprows=15, index_col='Date/Time', parse_dates=True, encoding='latin1', header=True)

# replace bad values on read, change column type to string
na_values = ['NO CLUE', 'N/A', '0']
requests = pd.read_csv('../data/311-service-requests.csv', na_values=na_values, dtype={'Incident Zip': str})

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
