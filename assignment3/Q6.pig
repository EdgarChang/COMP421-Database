-- This script reports the days that account for more than 1 percent of total death in Quebec.

--load the data from HDFS and define the schema
coviddata = LOAD '/data/Covid19Canada.csv' USING PigStorage(',') AS (prname:CHARARRAY, idate:CHARARRAY, newcases:INT, newdeaths:INT, tests:INT, recoveries:INT);


QuebecCases = FILTER coviddata BY prname == 'Quebec';


-- group the data based on province name
QuebecGroup= GROUP QuebecCases BY prname;


-- Read the attributes we are interested in.
totalDeaths = FOREACH QuebecGroup GENERATE SUM($1.newdeaths);

deathPercent = FOREACH QuebecCases GENERATE idate, newdeaths, (newdeaths*1.0/totalDeaths.$0)* 100 as percent;

filteredData = FILTER deathPercent BY percent > 1;

-- Order that by the death count with highest first
orderData = ORDER filteredData BY idate;

-- output
DUMP orderData;
