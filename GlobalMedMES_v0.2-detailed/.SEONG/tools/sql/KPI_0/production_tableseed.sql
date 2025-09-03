
INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('PROD_EVENT','생산 로그 이벤트','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('PROD_EVENT','START','시작','Y',1,'seed'),
('PROD_EVENT','END','종료','Y',2,'seed'),
('PROD_EVENT','GOODQTY','양품수','Y',3,'seed'),
('PROD_EVENT','DEFECTQTY','불량수','Y',4,'seed'),
('PROD_EVENT','DOWNTIME','비가동','Y',5,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

SET @EVT_START     := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='START');
SET @EVT_END       := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='END');
SET @EVT_GOODQTY   := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='GOODQTY');
SET @EVT_DEFECTQTY := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='DEFECTQTY');
SET @EVT_DOWNTIME  := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='DOWNTIME');

INSERT INTO tb_production_log (work_order_id, equipment_id, process_id, event_type, event_timestamp, event_value) VALUES
('WO-0001','E-0001','P-0001', @EVT_START, CONCAT(CURRENT_DATE(),' 08:00:00'), 0),
('WO-0001','E-0001','P-0001', @EVT_GOODQTY, CONCAT(CURRENT_DATE(),' 09:30:00'), 100),
('WO-0001','E-0001','P-0001', @EVT_DEFECTQTY, CONCAT(CURRENT_DATE(),' 09:30:00'), 5),
('WO-0001','E-0001','P-0001', @EVT_END, CONCAT(CURRENT_DATE(),' 10:00:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_START, CONCAT(CURRENT_DATE(),' 08:10:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_GOODQTY, CONCAT(CURRENT_DATE(),' 09:45:00'), 80),
('WO-0002','E-0002','P-0002', @EVT_DEFECTQTY, CONCAT(CURRENT_DATE(),' 09:45:00'), 3),
('WO-0002','E-0002','P-0002', @EVT_END, CONCAT(CURRENT_DATE(),' 10:20:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_DOWNTIME, CONCAT(CURRENT_DATE(),' 10:20:00'), 30);
