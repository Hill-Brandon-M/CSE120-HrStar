INSERT INTO users (u_username, u_password, u_super_u_id) VALUES
('somebody','something',1),
('noob','opensesame',1),
('example','example',3),
('super','secret',2);

INSERT INTO tokens (t_type, t_u_id) VALUES
('AUTHENTICATION',1),
('AUTHENTICATION',2),
('AUTHENTICATION',3),
('AUTHENTICATION',4),
('AUTHENTICATION',5),
('API_KEY',1);

INSERT INTO employees (emp_u_id, emp_acct_id, emp_payrate) VALUES
(1,2,50.00),
(2,3,15.00),
(3,4,12.50),
(4,5,22.00),
(5,6,17.25);

INSERT INTO people (p_u_id, p_firstname, p_lastname) VALUES
(1,'John','Doe'),
(2,'Annoying','Detail'),
(3,'That','Guy'),
(4,'Bad','Example'),
(5,'Terible','Name');

INSERT INTO punches (punch_u_id, punch_type, punch_time) VALUES
(1,'IN','2019-05-06 09:00:00.000'),
(1,'OUT','2019-05-06 17:00:00.000'),
(1,'IN','2019-05-15 09:00:00.000'),
(1,'OUT','2019-05-15 17:00:00.000'),
(2,'IN','2019-05-15 09:00:00.000'),
(2,'OUT','2019-05-15 17:00:00.000'),
(3,'IN','2019-05-15 09:00:00.000'),
(3,'OUT','2019-05-15 17:00:00.000'),
(4,'IN','2019-05-15 09:00:00.000'),
(4,'OUT','2019-05-15 17:00:00.000'),
(5,'IN','2019-05-15 09:00:00.000'),
(5,'OUT','2019-05-15 17:00:00.000');