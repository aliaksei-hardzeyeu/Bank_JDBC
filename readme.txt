JDBC IDEA_community 
Database itself was created in pgAdmin, was filled by script below via DB_browser plugin
________
CREATE TABLE users (
userId serial NOT NULL PRIMARY KEY,
nameN varchar(50) NOT NULL,
address varchar(255) 
);

CREATE TABLE accounts (
accountsId serial NOT NULL PRIMARY KEY,
userId integer NOT NULL,
balance decimal NOT NULL,
currency varchar(10) NOT NULL,
FOREIGN KEY(userId) REFERENCES users (userId)
);

CREATE TABLE transactions (
transactionsId serial NOT NULL PRIMARY KEY,
accountsId integer NOT NULL,
amount decimal NOT NULL,
FOREIGN KEY(accountsId) REFERENCES accounts (accountsId)
);
