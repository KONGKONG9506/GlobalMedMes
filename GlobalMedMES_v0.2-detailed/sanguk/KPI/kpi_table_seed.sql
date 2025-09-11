
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

INSERT INTO tb_definition (definition_name, description, formula, parameters, unit, created_by) VALUES
('OEE','설비 종합 효율', '(good_qty / total_qty) * (run_time / planned_time) * 100', JSON_ARRAY('good_qty','total_qty','run_time','planned_time'), '%','seed'),
('Yield','수율', '(good_qty / total_qty) * 100', JSON_ARRAY('good_qty','total_qty'), '%','seed'),
('Defect Rate','불량률', '(defect_qty / total_qty) * 100', JSON_ARRAY('defect_qty','total_qty'), '%','seed'),
('Productivity','생산성', 'produced_qty / run_time', JSON_ARRAY('produced_qty','run_time'), 'EA/Hour','seed')
ON DUPLICATE KEY UPDATE formula=VALUES(formula), parameters=VALUES(parameters), unit=VALUES(unit);

INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, actual_oee, actual_yield, actual_productivity, created_by) VALUES
(CURRENT_DATE(),'E-0001','P-0001','I-0001', 75.00, 98.00, 120.0000,'seed'),
(CURRENT_DATE(),'E-0002','P-0002','I-0001', 70.00, 97.00, 110.0000,'seed'),
(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'E-0001','P-0001','I-0001', 76.50, 99.00, 115.0000,'seed'),
(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY),'E-0002','P-0002','I-0001', 68.00, 96.50, 105.0000,'seed')
ON DUPLICATE KEY UPDATE actual_oee=VALUES(actual_oee), actual_yield=VALUES(actual_yield), actual_productivity=VALUES(actual_productivity);
