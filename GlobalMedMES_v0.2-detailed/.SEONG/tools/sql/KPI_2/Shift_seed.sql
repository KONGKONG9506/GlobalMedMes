/* 4) 교대/캘린더/배치 (A/B/C, 오늘 기준) */
INSERT INTO tb_shift (shift_id, shift_code, shift_name, start_time, end_time, created_by) VALUES
(1,'A','주간조','08:00:00','16:00:00','seed'),
(2,'B','중간조','16:00:00','00:00:00','seed'),
(3,'C','야간조','00:00:00','08:00:00','seed')
ON DUPLICATE KEY UPDATE shift_name=VALUES(shift_name), start_time=VALUES(start_time), end_time=VALUES(end_time);

-- 오늘 라인A(설비 스코프) 캘린더
INSERT INTO tb_shift_calendar (shift_date, shift_id, equipment_id, workcenter_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(),1,'E-0001',NULL, CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 16:00:00'),'seed')
ON DUPLICATE KEY UPDATE start_ts=VALUES(start_ts), end_ts=VALUES(end_ts);

-- 오늘 라인B(설비 스코프) 캘린더
INSERT INTO tb_shift_calendar (shift_date, shift_id, equipment_id, workcenter_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(),1,'E-0002',NULL, CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 16:00:00'),'seed')
ON DUPLICATE KEY UPDATE start_ts=VALUES(start_ts), end_ts=VALUES(end_ts);

-- 배치: OP를 두 설비에 각각 배치
INSERT INTO tb_shift_assignment (shift_date, shift_id, worker_id, equipment_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(),1,'00000000-0000-0000-0000-0000000000OP','E-0001', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 16:00:00'),'seed')
ON DUPLICATE KEY UPDATE end_ts=VALUES(end_ts);

INSERT INTO tb_shift_assignment (shift_date, shift_id, worker_id, equipment_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(),1,'00000000-0000-0000-0000-0000000000OP','E-0002', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 16:00:00'),'seed')
ON DUPLICATE KEY UPDATE end_ts=VALUES(end_ts);