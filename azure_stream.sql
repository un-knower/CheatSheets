AZURE STREAM
/// STREAM ANALYTICS
SU - streaming units (cpu + memory + thoughput)   $0.031/hr = 0.72/day  $11/month         $1 = 1000 GB
 
 
SELECT
-- will return 1 when driver crosses 100mph in a 10 seconds duration
  ISFIRST(s, 10) OVER (PARTITION BY Driver WHEN Speed > 100) AS FirstOVer100
  Acceleration = Speed - LAG(Speed) OVER (PARTITION BY Driver LIMIT DURATION(s, 1))
 
  LAG(Make, 1) OVER (LIMIT DURATION(minute, 1)) <> Make
-- Explanation: Use LAG to peek into the input stream one event back and get the Make value. Then compare it to the Make value on the current event and output the event if they are different.
 
******************************************
Query #2: Aggregation over time window
******************************************
SELECT max(temperatureReadTimestamp) as WindowEnd,
    avg(temperature) as averageTemperature,
    stdevp(temperature) as stdeviationTemperature,
    max(temperature) as maxTemperature,
    min(temperature) as minTemperature,
    machineName,
    avg(speed),
    DateAdd(second,-10,System.TimeStamp) AS WindowStartTime,
    System.TimeStamp AS WindowEndTime
FROM
    FactoryEventHub TIMESTAMP BY temperatureReadTimestamp
INTO output
GROUP BY TUMBLINGWINDOW(second, 10), driver   --machineName
GROUP BY HOPPINGWINDOW(second, 10, 2), driver
GROUP BY SLIDINGWINDOW(secondt, 10), Driver
HAVING avg(speed) > 100
-- https://msdn.microsoft.com/en-us/azure/stream-analytics/reference/hopping-window-azure-stream-analytics
 
******************************************
Query #3: Using CTEs
******************************************
WITH LastInWindow AS
(
SELECT  
    TopOne() OVER (ORDER BY temperaturereadtimestamp DESC) as topEvent 
FROM FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
GROUP BY TumblingWindow(second, 1), machineName
)
SELECT topEvent.machineName, topEvent.currentDirection, topEvent.temperaturereadtimestamp FROM LastInWindow
SELECT * INTO HondaOutput FROM AllRedCars WHERE Make = 'Honda'
SELECT * INTO ToyotaOutput FROM AllRedCars WHERE Make = 'Toyota'
 
******************************************
---------- Select average speed using sliding window (a lot of results)
******************************************
WITH Speeds AS
(
SELECT Driver, avg(Speed) AS AverageSpeed
FROM t GROUP BY SLIDINGWINDOW(s, 10), Driver
)
---------- Select max average speed using longer tumbling window
SELECT System.Timestamp, Driver, max(AverageSpeed) AS Speed
INTO output
FROM Speeds
GROUP BY TumblingWindow(minute, 1), Driver
  
 
******************************************
Query #4: CTE and Join
******************************************
WITH LastInWindow AS
(
SELECT  
    TopOne() OVER (ORDER BY temperaturereadtimestamp DESC) as topEvent 
FROM FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
GROUP BY TumblingWindow(second, 1), machineName
),
Aggregates AS
(
SELECT max(temperaturereadtimestamp) as WindowEnd,
    avg(temperature) as averageTemperature,
    stdevp(temperature) as stdeviationTemperature,
    max(temperature) as maxTemperature,
    min(temperature) as minTemperature,
    machineName
FROM
    FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
    GROUP BY TUMBLINGWINDOW(second, 1), machineName
)
SELECT Aggregates.machineName,
Aggregates.WindowEnd,
cast(Aggregates.WindowEnd as nvarchar(max)) WindowEndText,
Aggregates.averageTemperature,
Aggregates.stdeviationTemperature,
Aggregates.maxTemperature,
Aggregates.minTemperature,
LastInWindow.topEvent.currentDirection as lastDirection
FROM
Aggregates
INNER JOIN LastInWindow 
ON DATEDIFF(second, Aggregates, LastInWindow) BETWEEN 0 AND 1
AND Aggregates.machineName = LastInWindow.topEvent.machineName
AND Aggregates.WindowEnd = LastInWindow.topEvent.temperaturereadtimestamp
WHERE Aggregats.X != LastInWindow.X -- xample
 
******************************************
Query #5: CTE and Join and Reference Data
******************************************
WITH LastInWindow AS
(
SELECT  
    TopOne() OVER (ORDER BY temperaturereadtimestamp DESC) as topEvent 
FROM FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
GROUP BY TumblingWindow(second, 1), machineName
),
Aggregates AS
(
SELECT max(temperaturereadtimestamp) as WindowEnd,
    avg(temperature) as averageTemperature,
    stdevp(temperature) as stdeviationTemperature,
    max(temperature) as maxTemperature,
    min(temperature) as minTemperature,
    machineName
FROM
    FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
    GROUP BY TUMBLINGWINDOW(second, 1), machineName)
 
SELECT Aggregates.machineName,
Aggregates.WindowEnd,
Aggregates.averageTemperature,
Aggregates.stdeviationTemperature,
Aggregates.maxTemperature,
Aggregates.minTemperature,
LastInWindow.topEvent.currentDirection as lastDirection,
MachineEnergyPanel.energyPanel
FROM
Aggregates
INNER JOIN LastInWindow 
ON DATEDIFF(second, Aggregates, LastInWindow) BETWEEN 0 AND 1
AND Aggregates.machineName = LastInWindow.topEvent.machineName
AND Aggregates.WindowEnd = LastInWindow.topEvent.temperaturereadtimestamp
INNER JOIN MachineEnergyPanel ON Aggregates.machineName = MachineEnergyPanel.machineName
 
 
******************************************
Query #6: CTE and Join and Reference Data with text timestamp
******************************************
WITH LastInWindow AS
(
SELECT  
    TopOne() OVER (ORDER BY temperaturereadtimestamp DESC) as topEvent 
FROM FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
GROUP BY TumblingWindow(second, 1), machineName
),
Aggregates AS
(
SELECT max(temperaturereadtimestamp) as WindowEnd,
    avg(temperature) as averageTemperature,
    stdevp(temperature) as stdeviationTemperature,
    max(temperature) as maxTemperature,
    min(temperature) as minTemperature,
    machineName
FROM
    FactoryEventHub TIMESTAMP BY temperaturereadtimestamp
    GROUP BY TUMBLINGWINDOW(second, 1), machineName)
 
SELECT Aggregates.machineName,
cast(Aggregates.WindowEnd as nvarchar(max)) WindowEndText,
Aggregates.WindowEnd,
Aggregates.averageTemperature,
Aggregates.stdeviationTemperature,
Aggregates.maxTemperature,
Aggregates.minTemperature,
LastInWindow.topEvent.currentDirection as lastDirection,
MachineEnergyPanel.energyPanel
FROM
Aggregates
INNER JOIN LastInWindow 
ON DATEDIFF(second, Aggregates, LastInWindow) BETWEEN 0 AND 1
AND Aggregates.machineName = LastInWindow.topEvent.machineName
AND Aggregates.WindowEnd = LastInWindow.topEvent.temperaturereadtimestamp
INNER JOIN MachineEnergyPanel ON Aggregates.machineName = MachineEnergyPanel.machineName