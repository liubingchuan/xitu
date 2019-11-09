CREATE TABLE IF NOT EXISTS element_master(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  en_name VARCHAR(255) DEFAULT NULL,
  label VARCHAR(255) DEFAULT NULL,
  master VARCHAR(255) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  history TEXT DEFAULT NULL,
  method TEXT DEFAULT NULL,
  prop TEXT DEFAULT NULL,
  num VARCHAR(255) DEFAULT NULL,
  stamp VARCHAR(255) DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS element_slave(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  en_name VARCHAR(255) DEFAULT NULL,
  label VARCHAR(255) DEFAULT NULL,
  master VARCHAR(255) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  method TEXT DEFAULT NULL,
  prop TEXT DEFAULT NULL,
  stamp VARCHAR(255) DEFAULT NULL
) DEFAULT CHARSET=utf8;


CREATE TABLE IF NOT EXISTS xitu_user(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  account VARCHAR(255) DEFAULT NULL,
  password VARCHAR(255) DEFAULT NULL,
  name VARCHAR(255) DEFAULT NULL,
  identity VARCHAR(255) DEFAULT NULL,
  unit VARCHAR(255) DEFAULT NULL,
  job VARCHAR(255) DEFAULT NULL,
  duty VARCHAR(255) DEFAULT NULL,
  major VARCHAR(255) DEFAULT NULL,
  email VARCHAR(255) DEFAULT NULL,
  phone VARCHAR(255) DEFAULT NULL,
  wechat VARCHAR(255) DEFAULT NULL,
  role VARCHAR(255) DEFAULT NULL,
  open_id VARCHAR(255) DEFAULT NULL,
  stamp VARCHAR(255) DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS xitu_item(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  service VARCHAR(255) DEFAULT NULL,
  item TEXT DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS xitu_relation (
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  order_id varchar(255) DEFAULT NULL,
  linkuser_id varchar(255) DEFAULT NULL,
  tag INT DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS xitu_linkuser (
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  uuid varchar(255) DEFAULT NULL,
  name varchar(255) DEFAULT NULL,
  email varchar(255) DEFAULT NULL,
  telephone varchar(255) DEFAULT NULL,
  institution varchar(255) DEFAULT NULL,
  wechat VARCHAR(255) DEFAULT NULL,
  qq VARCHAR(255) DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS xitu_order (
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  uuid varchar(255) DEFAULT NULL,
  ordernumber varchar(255) DEFAULT NULL,
  title varchar(1000) DEFAULT NULL,
  chaxinfanwei varchar(255) DEFAULT NULL,
  mudi text DEFAULT NULL,
  kexueyaodian text DEFAULT NULL,
  jiansuodian text DEFAULT NULL,
  jiansuoci varchar(500) DEFAULT NULL,
  xueke varchar(255) DEFAULT NULL,
  chanye varchar(255) DEFAULT NULL,
  beizhu text DEFAULT NULL,
  shenqingfujian_id varchar(255) DEFAULT NULL,
  shenqingshijian varchar(255) DEFAULT NULL,
  chulishijian varchar(255) DEFAULT NULL,
  chulizhuangtai varchar(255) DEFAULT NULL,
  chuliren varchar(255) DEFAULT NULL,
  chuliyijian text DEFAULT NULL,
  chulirenfujian_id varchar(255) DEFAULT NULL,
  user_id varchar(255) DEFAULT NULL,
  institution varchar(500) DEFAULT NULL
) DEFAULT CHARSET=utf8;

--CREATE TABLE IF NOT EXISTS xitu_material_order (
--  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
--  uuid varchar(255) DEFAULT NULL,
--  ordernumber varchar(255) DEFAULT NULL,
--  title varchar(1000) DEFAULT NULL,
--  chaxinfanwei varchar(255) DEFAULT NULL,
--  mudi text DEFAULT NULL,
--  kexueyaodian text DEFAULT NULL,
--  jiansuodian text DEFAULT NULL,
--  jiansuoci varchar(500) DEFAULT NULL,
--  xueke varchar(255) DEFAULT NULL,
--  chanye varchar(255) DEFAULT NULL,
--  beizhu text DEFAULT NULL,
--  shenqingfujian_id varchar(255) DEFAULT NULL,
--  shenqingshijian varchar(255) DEFAULT NULL,
--  chulishijian varchar(255) DEFAULT NULL,
--  chulizhuangtai varchar(255) DEFAULT NULL,
--  chuliren varchar(255) DEFAULT NULL,
--  chuliyijian text DEFAULT NULL,
--  chulirenfujian_id varchar(255) DEFAULT NULL,
--  user_id varchar(255) DEFAULT NULL,
--  institution varchar(500) DEFAULT NULL
--) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS xitu_price(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(255) DEFAULT NULL,
  description VARCHAR(255) DEFAULT NULL,
  unit VARCHAR(255) DEFAULT NULL,
  price VARCHAR(255) DEFAULT NULL,
  avg VARCHAR(255) DEFAULT NULL,
  floating VARCHAR(255) DEFAULT NULL,
  update_time VARCHAR(255) DEFAULT NULL
) DEFAULT CHARSET=utf8;

CREATE TABLE IF NOT EXISTS xitu_patent(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  title VARCHAR(255) DEFAULT NULL,
  subject TEXT DEFAULT NULL,
  person VARCHAR(500) DEFAULT NULL,
  creator VARCHAR(500) DEFAULT NULL,
  applytime VARCHAR(255) DEFAULT NULL,
  publictime VARCHAR(255) DEFAULT NULL,
  applyyear VARCHAR(255) DEFAULT NULL,
  publicyear VARCHAR(255) DEFAULT NULL,
  ptype VARCHAR(255) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  claim LONGTEXT DEFAULT NULL,
  publicnumber VARCHAR(255) DEFAULT NULL,
  applynumber VARCHAR(255) DEFAULT NULL,
  ipc VARCHAR(1000) DEFAULT NULL,
  cpc VARCHAR(255) DEFAULT NULL,
  piroryear TEXT DEFAULT NULL,
  country VARCHAR(255) DEFAULT NULL,
  lawstatus VARCHAR(255) DEFAULT NULL,
  now BIGINT DEFAULT 0
) DEFAULT CHARSET=utf8;
CREATE TABLE IF NOT EXISTS xitu_jisuan(
  ID INT UNSIGNED NOT NULL PRIMARY KEY AUTO_INCREMENT,
  uuid varchar(255) DEFAULT NULL,
  ordernumber varchar(255) DEFAULT NULL,
  description TEXT DEFAULT NULL,
  shenqingshijian varchar(255) DEFAULT NULL,
  chulishijian varchar(255) DEFAULT NULL,
  chulizhuangtai varchar(255) DEFAULT NULL,
  chuliren varchar(255) DEFAULT NULL,
  chuliyijian text DEFAULT NULL,
  chulirenfujian_id varchar(255) DEFAULT NULL,
  user_id varchar(255) DEFAULT NULL
) DEFAULT CHARSET=utf8;

