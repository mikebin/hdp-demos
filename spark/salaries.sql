drop table if exists salaries;

create table salaries(
  gender string,
  age int,
  salary double,
  zip int
)
row format delimited fields terminated by ',';

load data local inpath 'salarydata.txt' into table salaries;

