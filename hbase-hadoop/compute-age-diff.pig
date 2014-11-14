salaries = load 'hbase://salaries' 
using org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf1:gender cf1:age cf1:salary cf1:zipcode', '-loadKey true') 
as (id: int, gender:chararray, age: int, salary: double, zipcode: int);

salariesByZip = group salaries by zipcode;
salariesWithAvgAge = foreach salariesByZip generate flatten(salaries), AVG(salaries.age) as averageAge;
meanAgeDiff = foreach salariesWithAvgAge generate id, (age - averageAge) as meanAgeDiffForZip;

store meanAgeDiff into 'hbase://salaries' using org.apache.pig.backend.hadoop.hbase.HBaseStorage('cf1:meanAgeDiffForZip');
