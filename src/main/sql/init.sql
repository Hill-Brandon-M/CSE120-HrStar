CREATE EXTENSION IF NOT EXISTS uuid-ossp;
CREATE EXTENSION IF NOT EXISTS pgcrypto;

CREATE TABLE IF NOT EXISTS assets (
	asset_id UUID PRIMARY KEY DEFAULT uuid_generate_v1mc()
);

CREATE TABLE IF NOT EXISTS addresses (
	addr_id 			UUID 			PRIMARY KEY 					DEFAULT uuid_generate_v1mc(),
	addr_line1 			VARCHAR	(256) 	NOT NULL,
	addr_line2 			VARCHAR	(256),
	addr_city 			VARCHAR	(200) 	NOT NULL,
	addr_state 			VARCHAR	(200) 	NOT NULL,
	addr_nation			VARCHAR (200)	NOT NULL,
	addr_postal_code 	VARCHAR (200) 	NOT NULL
);

CREATE TABLE IF NOT EXISTS people (
	person_id 			UUID 								PRIMARY KEY 							DEFAULT uuid_generate_v1mc(),
	person_asset_id		UUID			UNIQUE NOT NULL		REFERENCES assets(asset_id),
	person_firstname 	VARCHAR	(200),
	person_lastname 	VARCHAR	(200),
	person_prefix 		VARCHAR	(16),
	person_suffix 		VARCHAR	(16) 																DEFAULT NULL,
	person_email 		VARCHAR	(256) 	UNIQUE NOT NULL,
	person_addr_id 		UUID 								REFERENCES addresses(addr_id)
);

CREATE TABLE IF NOT EXISTS sites (
	site_id 		UUID 								PRIMARY KEY 						DEFAULT uuid_generate_v1mc(),
	site_name 		VARCHAR (200) 	UNIQUE NOT NULL,
	site_addr_id 	UUID 			UNIQUE NOT NULL		REFERENCES addresses(addr_id)
);

CREATE TABLE IF NOT EXISTS contracts (
	contract_id 			UUID 						PRIMARY KEY 				DEFAULT uuid_generate_v1mc(),
	contract_active			BOOLEAN			NOT NULL								DEFAULT FALSE,
	contract_job_title 		VARCHAR (200) 	NOT NULL,
	contract_site_id 		UUID 			NOT NULL 	REFERENCES sites(site_id),
	contract_wage_type 		VARCHAR (50) 	NOT NULL 								DEFAULT "HOURLY",
	contract_wage_amount 	MONEY 			NOT NULL 								DEFAULT 8.00,
	contract_document 		BYTEA,
);

CREATE TABLE IF NOT EXISTS employees (
	emp_id 			UUID 						PRIMARY KEY 						DEFAULT uuid_generate_v1mc(),
	emp_person_id 	UUID 	UNIQUE 	NOT NULL 	REFERENCES people(person_id),
	emp_contract_id UUID 			NOT NULL 	REFERENCES contracts(contract_id)
);

CREATE TABLE IF NOT EXISTS users (
	u_id UUID 	PRIMARY KEY			DEFAULT uuid_generate_v1mc(),
	u_username 	UNIQUE NOT NULL,
	u_emp_id	UNIQUE NOT NULL		REFERENCES employees(emp_id)
);

CREATE TABLE IF NOT EXISTS credentials (
	cred_id 		UUID 						PRIMARY KEY 			DEFAULT uuid_generate_v1mc(),
	cred_u_id 		UUID			NOT NULL	REFERENCES users(u_id),
	cred_password 	VARCHAR(200) 										DEFAULT gen_random_uuid(),
	cred_salt 		VARCHAR(50) 										DEFAULT gen_random_uuid(),
	cred_algorithm 	VARCHAR(50) 										DEFAULT 'SHA-256'
);

CREATE TABLE IF NOT EXISTS permissions (
	perm_id 		UUID 				PRIMARY KEY 					DEFAULT uuid_generate_v1mc(),
	perm_u_id 		UUID 	NOT NULL 	REFERENCES users(u_id),
	perm_asset_id 	UUID 	NOT NULL 	REFERENCES assets(asset_id),
	perm_read 		BOOLEAN NOT NULL 									DEFAULT FALSE,
	perm_write 		BOOLEAN NOT NULL 									DEFAULT FALSE,
	perm_change 	BOOLEAN NOT NULL 									DEFAULT FALSE,
	perm_delete		BOOLEAN NOT NULL									DEFAULT FALSE
);