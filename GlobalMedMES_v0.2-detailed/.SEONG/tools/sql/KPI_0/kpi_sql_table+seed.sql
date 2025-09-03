-- 테이블 내용물 삭제
-- TRUNCATE TABLE tb_production_log;
-- TRUNCATE TABLE tb_definition;
-- TRUNCATE TABLE tb_kpi_data;
-- 테이블 삭제
-- DROP TABLE IF EXISTS tb_production_log;
-- DROP TABLE IF EXISTS tb_definition;
-- DROP TABLE IF EXISTS tb_kpi_data;
-- 테이블

CREATE TABLE `tb_definition` (
    `definition_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '수식 ID (PK)',
    `definition_name` VARCHAR(100) NOT NULL COMMENT '수식 이름 (예: OEE, Yield, Defect Rate)',
    `description` VARCHAR(255) COMMENT '설명',
    `formula` VARCHAR(500) NOT NULL COMMENT '계산 수식 (예: "(good_qty / total_qty) * 100")',
    `parameters` JSON NOT NULL COMMENT '파라미터 정의 : 사용되는 파라미터 정의 (예: ["good_qty", "total_qty"])',
    `unit` VARCHAR(20) COMMENT '단위 (% , 개, 시간 등)',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
    `deleted_at` DATETIME NULL COMMENT 'UTC',
    `created_by` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
    `modified_by` VARCHAR(50) NULL,
    `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
    PRIMARY KEY (`definition_id`)
) ENGINE=InnoDB COMMENT='계산 수식 정의 테이블';

CREATE TABLE `tb_production_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '생산 로그 ID (PK)',
  `work_order_id` VARCHAR(36) NOT NULL COMMENT '작업 지시 ID (FK)',
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK)',
  `event_type` BIGINT NOT NULL COMMENT '이벤트 유형 (FK → tb_code)',
  `event_timestamp` DATETIME NOT NULL COMMENT '이벤트 발생 시점 (UTC)',
  `event_value` DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '이벤트 관련 값 (수량, 시간 등)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '레코드 생성 시점 (UTC)',
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `uk_prodlog_unique` (`work_order_id`, `equipment_id`, `process_id`, `event_timestamp`, `event_type`),
  KEY `idx_log_event_time` (`event_timestamp`),
  KEY `idx_log_wo_id` (`work_order_id`),
  KEY `idx_log_eqp_id` (`equipment_id`),
  CONSTRAINT `fk_prodlog_event_type` FOREIGN KEY (`event_type`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT,
  CONSTRAINT `ck_prodlog_value_nonneg` CHECK (`event_value` >= 0),
  CONSTRAINT `ck_prodlog_time_order` CHECK (`event_timestamp` IS NOT NULL)
) ENGINE=InnoDB COMMENT='생산 공정에서 발생하는 원시 이벤트 로그';
-- 코드 연결, 이벤트 값 음수 금지, 이벤트 시간 null 금지

CREATE TABLE `tb_kpi_data` (
  `kpi_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'KPI 기록 ID (PK)',
  `kpi_date` DATE NOT NULL COMMENT 'KPI 기준 날짜',
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK → tb_equipment)',
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK → tb_process)',
  `item_id` VARCHAR(36) NOT NULL COMMENT '품목 ID (FK → tb_item)',
  `work_order_id` VARCHAR(36) NULL COMMENT '지시 ID (FK → tb_workorder)',
  
  `actual_oee` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '설비 종합 효율 (%) [0~100]',
  `actual_productivity` DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '시간당 생산성 (단위/시간)',
  `actual_yield` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '수율 (%) [0~100]',
  `actual_defect_rate` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '불량률 (%) [0~100]',
  
  `aggregation_type` VARCHAR(20) NOT NULL DEFAULT 'REALTIME' COMMENT '집계 유형 (REALTIME/DAILY_BATCH 등)',
  `batch_check` VARCHAR(20) COMMENT '일일 집계시 중복 체크용',
  `start_time` DATETIME NOT NULL COMMENT 'KPI 집계 시작 시간',
  `end_time` DATETIME NOT NULL COMMENT 'KPI 집계 종료 시간',
  `calc_success_check` TINYINT NOT NULL DEFAULT 1 COMMENT '계산 상태 (0=FAIL, 1=SUCCESS, 2=IN_PROGRESS, 3=RETRY)',
  `calc_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'KPI 계산 시각',

  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  
  PRIMARY KEY (`kpi_id`),
  
  UNIQUE KEY `uk_kpi_realtime` (`kpi_date`, `work_order_id`, `equipment_id`, `process_id`, `item_id`, `aggregation_type`),

  UNIQUE KEY `uk_kpi_daily` (`kpi_date`, `equipment_id`, `process_id`, `item_id`, `batch_check`, `aggregation_type`),

  KEY `idx_kpi_date` (`kpi_date`),
  KEY `idx_kpi_equipment_id` (`equipment_id`),
  KEY `idx_kpi_process_id` (`process_id`),
  KEY `idx_kpi_item_id` (`item_id`),

  CONSTRAINT `fk_kpi_work_order` FOREIGN KEY (`work_order_id`) REFERENCES `tb_work_order`(`work_order_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_process`   FOREIGN KEY (`process_id`)   REFERENCES `tb_process`(`process_id`)   ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_item`      FOREIGN KEY (`item_id`)      REFERENCES `tb_item`(`item_id`) ON DELETE RESTRICT,

  CONSTRAINT `ck_actual_oee_range`          CHECK (`actual_oee` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_yield_range`        CHECK (`actual_yield` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_defect_rate_range`  CHECK (`actual_defect_rate` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_productivity_nonneg` CHECK (`actual_productivity` >= 0),
  CONSTRAINT `ck_start_before_end` CHECK (end_time >= start_time)
) ENGINE=InnoDB COMMENT='KPI 데이터: 실시간(퍼포먼스 단위) + 배치(하루 단위) 모두 대응';
TRUNCATE TABLE tb_kpi_data;
  -- 실시간 KPI는 work_order/equipment/process/item/aggregation_type 기준 UNIQUE
  -- 배치 KPI는 batch_check/equipment/process/item/aggregation_type 기준 UNIQUE


-- 데이터
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

INSERT INTO tb_definition (definition_name, description, formula, parameters, unit, created_by)
VALUES
('OEE', '설비 종합 효율', '(#good_qty.divide(#total_qty,4,T(java.math.RoundingMode).HALF_UP).multiply(#run_time.divide(#planned_time,4,T(java.math.RoundingMode).HALF_UP))).multiply(#hundred)', JSON_ARRAY('good_qty','total_qty','run_time','planned_time'), '%', 'seed'),
('Yield', '수율', '(#good_qty.divide(#total_qty,4,T(java.math.RoundingMode).HALF_UP)).multiply(#hundred)', JSON_ARRAY('good_qty','total_qty'), '%', 'seed'),
('Defect Rate', '불량률', '(#defect_qty.divide(#total_qty,4,T(java.math.RoundingMode).HALF_UP)).multiply(#hundred)', JSON_ARRAY('defect_qty','total_qty'), '%', 'seed'),
('Productivity', '생산성', '#produced_qty.divide(#run_time,4,T(java.math.RoundingMode).HALF_UP)', JSON_ARRAY('produced_qty','run_time'), 'EA/Hour', 'seed')
ON DUPLICATE KEY UPDATE
    formula = VALUES(formula),
    parameters = VALUES(parameters),
    unit = VALUES(unit);



-- 실시간 KPI (WORK ORDER 단위)
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, work_order_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, start_time, end_time, calc_success_check, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 'WO-0001', 85.00, 98.50, 1.50, 120.0000, 'REALTIME', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 10:00:00'), 1, 'seed'),
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0001', 'WO-0002', 82.00, 97.00, 3.50, 110.0000, 'REALTIME', CONCAT(CURRENT_DATE(),' 08:10:00'), CONCAT(CURRENT_DATE(),' 10:20:00'), 1, 'seed');

-- 배치 KPI (DAILY_TOTAL 단위)
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, batch_check, start_time, end_time, calc_success_check, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 83.00, 98.00, 2.00, 118.0000, 'DAILY_BATCH', 'DAILY', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 18:00:00'), 1, 'seed'),
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0002', 80.00, 97.00, 3.00, 112.0000, 'DAILY_BATCH', 'DAILY', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 18:00:00'), 1, 'seed');

