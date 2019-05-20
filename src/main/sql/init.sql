CREATE EXTENSION pgcrypto;

CREATE TABLE IF NOT EXISTS users (
	u_id SERIAL PRIMARY KEY,
	u_username TEXT UNIQUE NOT NULL,
	u_password TEXT NOT NULL,
	u_super_u_id INTEGER REFERENCES users(u_id)
);

CREATE TABLE IF NOT EXISTS tokens (
	t_id SERIAL PRIMARY KEY,
	t_type TEXT NOT NULL,
	t_u_id INTEGER REFERENCES users(u_id),
	t_expires TIMESTAMP NOT NULL DEFAULT (NOW() + (15 * INTERVAL '1 minute')),
	t_value TEXT UNIQUE NOT NULL DEFAULT ENCODE(DIGEST(CONCAT(CAST(CURRENT_TIMESTAMP AS TEXT), RANDOM()::TEXT), 'SHA512'), 'HEX')
);

CREATE TABLE IF NOT EXISTS people (
	p_id SERIAL PRIMARY KEY,
	p_u_id INTEGER UNIQUE NOT NULL REFERENCES users(u_id),
	p_firstname TEXT NOT NULL,
	p_lastname TEXT NOT NULL
);

CREATE TABLE IF NOT EXISTS punches (
	punch_id SERIAL PRIMARY KEY,
	punch_u_id INTEGER NOT NULL REFERENCES users(u_id),
	punch_type TEXT NOT NULL,
	punch_time TIMESTAMP NOT NULL DEFAULT NOW(),
	punch_submitted TIMESTAMP NOT NULL DEFAULT NOW(),
	punch_status TEXT NOT NULL DEFAULT 'PENDING'
);
										  
CREATE TABLE IF NOT EXISTS employees (
	emp_id SERIAL PRIMARY KEY,
	emp_u_id INTEGER UNIQUE REFERENCES users(u_id),
	emp_acct_id INTEGER,
	emp_payrate DOUBLE PRECISION NOT NULL DEFAULT 8.00
);
										  
INSERT INTO users (u_username, u_password) VALUES ('admin', 'password') ON CONFLICT (u_username) DO NOTHING;
										  
CREATE OR REPLACE VIEW shifts
AS
SELECT 
	punch_u_id AS shift_u_id, 
	previous_punch_time AS shift_start,
	punch_time AS shift_end, 
	punch_time - previous_punch_time AS shift_time
FROM (

	SELECT 
		*, 
		LAG(punch_time) OVER (PARTITION BY punch_u_id ORDER BY punch_u_id, punch_time) AS previous_punch_time	
	FROM punches

) AS punch_pairs
WHERE punch_type = 'OUT';

COMMIT;