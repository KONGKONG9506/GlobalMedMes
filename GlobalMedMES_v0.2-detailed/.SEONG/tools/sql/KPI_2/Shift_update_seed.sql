-- 1) Shift 테이블 (3조 3교대)
INSERT INTO tb_shift (shift_id, shift_code, shift_name, start_time, end_time, created_by) VALUES
(1,'A','주간조','07:00:00','15:00:00','seed'),
(2,'B','전반야','15:00:00','23:00:00','seed'),
(3,'C','후반야','23:00:00','07:00:00','seed')
ON DUPLICATE KEY UPDATE shift_name=VALUES(shift_name), start_time=VALUES(start_time), end_time=VALUES(end_time);

-- 2) Shift Calendar (설비 스코프) INTERVAL로 9시간 전(UTS)으로 삽입
-- 주간조
INSERT INTO tb_shift_calendar (shift_date, shift_id, equipment_id, workcenter_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 1, 'E-0001', NULL, DATE_SUB(CONCAT(CURRENT_DATE(),' 07:00:00'), INTERVAL 9 HOUR), DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR),'seed'),
(CURRENT_DATE(), 1, 'E-0002', NULL, DATE_SUB(CONCAT(CURRENT_DATE(),' 07:00:00'), INTERVAL 9 HOUR), DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR),'seed')
ON DUPLICATE KEY UPDATE start_ts=VALUES(start_ts), end_ts=VALUES(end_ts);

-- 전반야
INSERT INTO tb_shift_calendar (shift_date, shift_id, equipment_id, workcenter_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 2, 'E-0001', NULL, DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR), DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR),'seed'),
(CURRENT_DATE(), 2, 'E-0002', NULL, DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR), DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR),'seed')
ON DUPLICATE KEY UPDATE start_ts=VALUES(start_ts), end_ts=VALUES(end_ts);

-- 후반야 (다음 날 07:00까지)
INSERT INTO tb_shift_calendar (shift_date, shift_id, equipment_id, workcenter_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 3, 'E-0001', NULL, DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR), DATE_SUB(CONCAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY),' 07:00:00'), INTERVAL 9 HOUR),'seed'),
(CURRENT_DATE(), 3, 'E-0002', NULL, DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR), DATE_SUB(CONCAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY),' 07:00:00'), INTERVAL 9 HOUR),'seed')
ON DUPLICATE KEY UPDATE start_ts=VALUES(start_ts), end_ts=VALUES(end_ts);

-- 3) Shift Assignment (OP 작업자 배치)
-- 주간조
INSERT INTO tb_shift_assignment (shift_date, shift_id, worker_id, equipment_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 1, '00000000-0000-0000-0000-0000000000OP', 'E-0001',
 DATE_SUB(CONCAT(CURRENT_DATE(),' 07:00:00'), INTERVAL 9 HOUR),
 DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR),'seed'),
(CURRENT_DATE(), 1, '00000000-0000-0000-0000-0000000000OP', 'E-0002',
 DATE_SUB(CONCAT(CURRENT_DATE(),' 07:00:00'), INTERVAL 9 HOUR),
 DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR),'seed')
ON DUPLICATE KEY UPDATE end_ts=VALUES(end_ts);


-- 전반야
INSERT INTO tb_shift_assignment (shift_date, shift_id, worker_id, equipment_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 2, '00000000-0000-0000-0000-0000000000OP', 'E-0001',
 DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR),
 DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR),'seed'),
(CURRENT_DATE(), 2, '00000000-0000-0000-0000-0000000000OP', 'E-0002',
 DATE_SUB(CONCAT(CURRENT_DATE(),' 15:00:00'), INTERVAL 9 HOUR),
 DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR),'seed')
ON DUPLICATE KEY UPDATE end_ts=VALUES(end_ts);
-- 후반야
INSERT INTO tb_shift_assignment (shift_date, shift_id, worker_id, equipment_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 3, '00000000-0000-0000-0000-0000000000OP', 'E-0001',
 DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR),
 DATE_SUB(CONCAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY),' 07:00:00'), INTERVAL 9 HOUR),'seed'),
(CURRENT_DATE(), 3, '00000000-0000-0000-0000-0000000000OP', 'E-0002',
 DATE_SUB(CONCAT(CURRENT_DATE(),' 23:00:00'), INTERVAL 9 HOUR),
 DATE_SUB(CONCAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY),' 07:00:00'), INTERVAL 9 HOUR),'seed')
ON DUPLICATE KEY UPDATE end_ts=VALUES(end_ts);
