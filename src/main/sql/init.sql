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
	punch_time TIMESTAMP NOT NULL,
	punch_submitted TIMESTAMP NOT NULL,
	punch_status TEXT NOT NULL
);

INSERT INTO users (u_username, u_password) VALUES ('admin', 'password');

COMMIT;