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