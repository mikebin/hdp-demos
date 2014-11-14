use test;

drop table if exists salaries;

create table salaries (
gender char(1),
age int,
salary double,
zipcode int);

load data local infile 'salarydata.txt' into table salaries fields terminated by ',';

alter table salaries add column `id` int(10) unsigned primary KEY AUTO_INCREMENT first;
