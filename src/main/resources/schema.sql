DROP TABLE companies;
CREATE TABLE companies (
  _id int(11) NOT NULL,
  name text NOT NULL,
  hashLogo text NOT NULL,
  hashTemplate text NOT NULL,
  template mediumtext NOT NULL,
  logo text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


DROP TABLE genticket;
CREATE TABLE genticket (
  id varchar(64) NOT NULL,
  created timestamp NOT NULL DEFAULT '0000-00-00 00:00:00' ON UPDATE CURRENT_TIMESTAMP,
  content text NOT NULL,
  batchId varchar(128) NOT NULL,
  amount double NOT NULL,
  companyId int(11) NOT NULL,
  shopId varchar(32) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

ALTER TABLE genticket PARTITION BY RANGE(UNIX_TIMESTAMP(created)) (
	PARTITION x VALUES LESS THAN (UNIX_TIMESTAMP('2016-12-09 00:00:00')), 
	PARTITION y VALUES LESS THAN MAXVALUE 
) 