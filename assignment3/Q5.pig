-- This script reports the total number of deaths per province with more than 100 deaths.

--load the data from HDFS and define the schema
coviddata = LOAD '/data/Covid19Canada.csv' USING PigStorage(',') AS (prname:CHARARRAY, idate:CHARARRAY, newcases:INT, newdeaths:INT, tests:INT, recoveries:INT);

-- group the data based on province name
provinces = GROUP coviddata BY prname;

-- Read the attributes we are interested in.
deaths = FOREACH provinces GENERATE group, SUM($1.newdeaths)as deaths;

-- filter data with only provinces with deathcount higher than 100.

filteredData = FILTER deaths BY deaths > 100;

-- Order that by the death count with highest first
orderData = ORDER filteredData BY deaths DESC;

-- output
DUMP orderData;
