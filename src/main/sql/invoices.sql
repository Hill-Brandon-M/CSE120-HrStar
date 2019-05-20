SELECT 
	shift_u_id AS invoice_u_id, 
  	invoice_total_time, 
	EXTRACT(HOUR FROM invoice_total_time) * emp_payrate AS invoice_balance, 
	emp_acct_id AS invoice_emp_acct_id 
FROM (
	SELECT shift_u_id, SUM(shift_time) AS invoice_total_time
	FROM shifts
	GROUP BY shift_u_id
) AS i
JOIN employees ON shift_u_id = emp_u_id;