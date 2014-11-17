drop table if exists salaries;

create table salaries(
  gender char(1),
  age int,
  salary double,
  zip int
)
clustered by (age) into 4 buckets
stored as orc;
