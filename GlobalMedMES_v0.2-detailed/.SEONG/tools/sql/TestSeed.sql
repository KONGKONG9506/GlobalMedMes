-- 09-18

-- 데이터
INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('PROD_EVENT','생산 로그 이벤트','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('PROD_EVENT','START','시작','Y',1,'seed'),
('PROD_EVENT','END','종료','Y',2,'seed'),
('PROD_EVENT','GOODQTY','양품수','Y',3,'seed'),
('PROD_EVENT','DEFECTQTY','불량수','Y',4,'seed'),
('PROD_EVENT','DOWNTIME_START','비가동 시작','Y',5,'seed'),
('PROD_EVENT','DOWNTIME_END','비가동 종료','Y',6,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

SET @EVT_START     := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='START');
SET @EVT_END       := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='END');
SET @EVT_GOODQTY   := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='GOODQTY');
SET @EVT_DEFECTQTY := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='DEFECTQTY');
SET @EVT_DOWNTIME_START  := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='DOWNTIME_START');
SET @EVT_DOWNTIME_END  := (SELECT code_id FROM tb_code WHERE group_code='PROD_EVENT' AND code='DOWNTIME_END');

INSERT IGNORE INTO tb_production_log (work_order_id, equipment_id, process_id, event_type, event_timestamp, event_value) VALUES
('WO-0001','E-0001','P-0001', @EVT_START, CONCAT(CURRENT_DATE(),' 08:00:00'), 0),
('WO-0001','E-0001','P-0001', @EVT_GOODQTY, CONCAT(CURRENT_DATE(),' 09:30:00'), 100),
('WO-0001','E-0001','P-0001', @EVT_DEFECTQTY, CONCAT(CURRENT_DATE(),' 09:30:00'), 5),
('WO-0001','E-0001','P-0001', @EVT_END, CONCAT(CURRENT_DATE(),' 10:00:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_START, CONCAT(CURRENT_DATE(),' 08:10:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_GOODQTY, CONCAT(CURRENT_DATE(),' 09:45:00'), 80),
('WO-0002','E-0002','P-0002', @EVT_DEFECTQTY, CONCAT(CURRENT_DATE(),' 09:45:00'), 3),
('WO-0002','E-0002','P-0002', @EVT_END, CONCAT(CURRENT_DATE(),' 10:20:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_DOWNTIME_START, CONCAT(CURRENT_DATE(),' 10:20:00'), 0),
('WO-0002','E-0002','P-0002', @EVT_DOWNTIME_END, CONCAT(CURRENT_DATE(),' 10:50:00'), 30);

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

-- 코드 그룹: 집계 유형
INSERT IGNORE INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES
('KPI_DATA_TYPE', 'KPI 집계 유형', 'KPI 데이터의 집계 기준을 정의합니다.', 'seed');

-- 코드: 집계 유형
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `sort_order`, `created_by`)
VALUES
('KPI_DATA_TYPE', 'REALTIME', '실시간', 1, 'seed'),
('KPI_DATA_TYPE', 'DAILY_BATCH', '일일 배치', 2, 'seed');

-- 코드 그룹: 계산 상태
INSERT IGNORE INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES
('KPI_CALC_STATUS', 'KPI 계산 상태', 'KPI 계산 작업의 상태를 정의합니다.', 'seed');

-- 코드: 계산 상태
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `sort_order`, `created_by`)
VALUES
('KPI_CALC_STATUS', 'SUCCESS', '성공', 1, 'seed'),
('KPI_CALC_STATUS', 'FAIL', '실패', 2, 'seed');

SET @EVT_REALTIME  := (SELECT code_id FROM tb_code WHERE group_code='KPI_DATA_TYPE' AND code='REALTIME');
SET @EVT_DAILY_BATCH  := (SELECT code_id FROM tb_code WHERE group_code='KPI_DATA_TYPE' AND code='DAILY_BATCH');

SET @EVT_SUCCESS  := (SELECT code_id FROM tb_code WHERE group_code='KPI_CALC_STATUS' AND code='SUCCESS');
SET @EVT_FAIL  := (SELECT code_id FROM tb_code WHERE group_code='KPI_CALC_STATUS' AND code='FAIL');



-- 실시간 KPI (WORK ORDER 단위)
INSERT IGNORE INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, work_order_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type_id, start_time, end_time, calc_status_code_id, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 'WO-0001', 85.00, 98.50, 1.50, 120.0000, @EVT_REALTIME, CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 10:00:00'), @EVT_SUCCESS, 'seed'),
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0001', 'WO-0002', 82.00, 97.00, 3.50, 110.0000, @EVT_REALTIME, CONCAT(CURRENT_DATE(),' 08:10:00'), CONCAT(CURRENT_DATE(),' 10:20:00'), @EVT_SUCCESS, 'seed');

-- 배치 KPI (DAILY_TOTAL 단위)
INSERT IGNORE INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type_id, batch_group_key, start_time, end_time, calc_status_code_id, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 83.00, 98.00, 2.00, 118.0000, @EVT_DAILY_BATCH, 'DAILY', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 18:00:00'), @EVT_SUCCESS, 'seed'),
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0002', 80.00, 97.00, 3.00, 112.0000, @EVT_DAILY_BATCH, 'DAILY', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 18:00:00'), @EVT_SUCCESS, 'seed');

-- 0) 테이블 재생성: FK 역순 DROP → 정순 CREATE
DROP TABLE IF EXISTS tb_cmms_work_order_log;
DROP TABLE IF EXISTS tb_cmms_fault_log;
DROP TABLE IF EXISTS tb_cmms_work_order;
DROP TABLE IF EXISTS tb_cmms_pm_plan;

-- 1) 코드그룹/코드 시드 (멱등)
INSERT INTO tb_code_group (group_code, group_name, description, created_by) VALUES
('LOSS_CATEGORY','손실 카테고리','CMMS 고장/정지 분류','system')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name), description=VALUES(description);

INSERT INTO tb_code_group (group_code, group_name, description, created_by) VALUES
('CYCLE_TYPE','주기유형','CMMS PM 주기','system')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name), description=VALUES(description);

INSERT INTO tb_code_group (group_code, group_name, description, created_by) VALUES
('CMMS_WO_STATUS','CMMS WO 상태','보전 작업지시 상태','system')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name), description=VALUES(description);

INSERT INTO tb_code_group (group_code, group_name, description, created_by) VALUES
('WO_PRIORITY','WO 우선순위','보전 작업지시 우선순위','system')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name), description=VALUES(description);

-- LOSS_CATEGORY (Top5)
INSERT INTO tb_code (group_code, code, name, description, use_yn, sort_order, created_by) VALUES
('LOSS_CATEGORY','BREAKDOWN','Breakdown','고장으로 인한 정지','Y',10,'system'),
('LOSS_CATEGORY','SETUP','Setup','교체/세팅','Y',20,'system'),
('LOSS_CATEGORY','MINOR_STOP','Minor Stop','단소정지','Y',30,'system'),
('LOSS_CATEGORY','QUALITY','Quality','품질 이슈','Y',40,'system'),
('LOSS_CATEGORY','PLANNED_STOP','Planned Stop','계획정지','Y',50,'system')
ON DUPLICATE KEY UPDATE name=VALUES(name), description=VALUES(description),
                        use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- CYCLE_TYPE
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('CYCLE_TYPE','HOURS','Hours','Y',10,'system'),
('CYCLE_TYPE','DAYS','Days','Y',20,'system'),
('CYCLE_TYPE','CALENDAR','Calendar','Y',30,'system')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- CMMS_WO_STATUS
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('CMMS_WO_STATUS','OPEN','Open','Y',10,'system'),
('CMMS_WO_STATUS','ASSIGNED','Assigned','Y',20,'system'),
('CMMS_WO_STATUS','IN_PROGRESS','In Progress','Y',30,'system'),
('CMMS_WO_STATUS','DONE','Done','Y',40,'system')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- WO_PRIORITY
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('WO_PRIORITY','L','Low','Y',10,'system'),
('WO_PRIORITY','M','Medium','Y',20,'system'),
('WO_PRIORITY','H','High','Y',30,'system')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- 2) 테이블 생성 (코드마스터 FK)
CREATE TABLE tb_cmms_pm_plan (
  plan_id              BIGINT NOT NULL AUTO_INCREMENT,
  equipment_id         VARCHAR(36) NOT NULL,
  task_name            VARCHAR(100) NOT NULL,
  cycle_type_code_id   BIGINT NOT NULL,
  cycle_value          INT NOT NULL,
  last_done_at         DATETIME NULL,
  next_due_at          DATETIME NOT NULL,
  status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
  is_deleted           TINYINT DEFAULT 0,
  deleted_at           DATETIME NULL,
  created_by           VARCHAR(50) NOT NULL,
  created_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_by          VARCHAR(50) NULL,
  modified_at          DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (plan_id),
  KEY ix_pm_due (next_due_at),
  KEY ix_pm_eqp (equipment_id),
  KEY ix_pm_cycle_type (cycle_type_code_id),
  CONSTRAINT fk_pm_equipment      FOREIGN KEY (equipment_id)
    REFERENCES tb_equipment(equipment_id) ON DELETE RESTRICT,
  CONSTRAINT fk_pm_cycle_typecode FOREIGN KEY (cycle_type_code_id)
    REFERENCES tb_code(code_id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='CMMS: PM 계획(코드마스터)';

CREATE TABLE tb_cmms_work_order (
  cmms_wo_id         BIGINT NOT NULL AUTO_INCREMENT,
  equipment_id       VARCHAR(36) NOT NULL,
  title              VARCHAR(200) NOT NULL,
  priority_code_id   BIGINT NOT NULL,
  status_code_id     BIGINT NOT NULL,
  assignee_user_id   VARCHAR(36) NULL,
  request_id         VARCHAR(64) NULL,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  started_at         DATETIME NULL,
  finished_at        DATETIME NULL,
  actual_minutes     INT NULL,
  parts_cost         DECIMAL(12,2) NULL,
  is_deleted         TINYINT DEFAULT 0,
  deleted_at         DATETIME NULL,
  created_by         VARCHAR(50) NOT NULL,
  modified_by        VARCHAR(50) NULL,
  modified_at        DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (cmms_wo_id),
  UNIQUE KEY uq_cmms_wo_request_id (request_id),
  KEY ix_cmms_wo_eqp_created (equipment_id, created_at),
  KEY ix_cmms_wo_status (status_code_id),
  KEY ix_cmms_wo_priority (priority_code_id),
  CONSTRAINT fk_cmms_wo_equipment FOREIGN KEY (equipment_id)
    REFERENCES tb_equipment(equipment_id) ON DELETE RESTRICT,
  CONSTRAINT fk_cmms_wo_assignee FOREIGN KEY (assignee_user_id)
    REFERENCES tb_user(user_id) ON DELETE RESTRICT,
  CONSTRAINT fk_cmms_wo_priority_code FOREIGN KEY (priority_code_id)
    REFERENCES tb_code(code_id) ON DELETE RESTRICT,
  CONSTRAINT fk_cmms_wo_status_code FOREIGN KEY (status_code_id)
    REFERENCES tb_code(code_id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='CMMS: 보전 작업지시(코드마스터)';

CREATE TABLE tb_cmms_work_order_log (
  log_id               BIGINT NOT NULL AUTO_INCREMENT,
  cmms_wo_id           BIGINT NOT NULL,
  from_status_code_id  BIGINT NULL,
  to_status_code_id    BIGINT NOT NULL,
  changed_by           VARCHAR(36) NOT NULL,
  changed_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  note                 VARCHAR(255) NULL,
  PRIMARY KEY (log_id),
  KEY ix_wolog_wo (cmms_wo_id, changed_at),
  CONSTRAINT fk_cmms_wolog_wo   FOREIGN KEY (cmms_wo_id)
    REFERENCES tb_cmms_work_order(cmms_wo_id) ON DELETE CASCADE,
  CONSTRAINT fk_wolog_from_code FOREIGN KEY (from_status_code_id)
    REFERENCES tb_code(code_id) ON DELETE RESTRICT,
  CONSTRAINT fk_wolog_to_code   FOREIGN KEY (to_status_code_id)
    REFERENCES tb_code(code_id) ON DELETE RESTRICT,
  CONSTRAINT fk_cmms_wolog_user FOREIGN KEY (changed_by)
    REFERENCES tb_user(user_id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='CMMS: WO 상태전이 이력(코드마스터)';

CREATE TABLE tb_cmms_fault_log (
  fault_id           BIGINT NOT NULL AUTO_INCREMENT,
  equipment_id       VARCHAR(36) NOT NULL,
  loss_cat_code_id   BIGINT NOT NULL,
  symptom            VARCHAR(255) NOT NULL,
  action             VARCHAR(255) NULL,
  occurred_at        DATETIME NOT NULL,
  resolved_at        DATETIME NULL,
  cmms_wo_id         BIGINT NULL,
  is_deleted         TINYINT DEFAULT 0,
  deleted_at         DATETIME NULL,
  created_by         VARCHAR(50) NOT NULL,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_by        VARCHAR(50) NULL,
  modified_at        DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (fault_id),
  KEY ix_fault_time_eqp (occurred_at, equipment_id),
  KEY ix_fault_loss (loss_cat_code_id),
  CONSTRAINT fk_fault_equipment FOREIGN KEY (equipment_id)
    REFERENCES tb_equipment(equipment_id) ON DELETE RESTRICT,
  CONSTRAINT fk_fault_losscode  FOREIGN KEY (loss_cat_code_id)
    REFERENCES tb_code(code_id) ON DELETE RESTRICT,
  CONSTRAINT fk_fault_cmmswo    FOREIGN KEY (cmms_wo_id)
    REFERENCES tb_cmms_work_order(cmms_wo_id) ON DELETE SET NULL,
  CONSTRAINT ck_fault_time_order CHECK (resolved_at IS NULL OR resolved_at >= occurred_at)
) ENGINE=InnoDB COMMENT='CMMS: 고장/정지 로그(코드마스터)';

-- 3) 메뉴/RBAC (멱등)
INSERT INTO tb_menu (menu_code, menu_name, path, sort_order, created_by)
VALUES ('CMMS','CMMS','/cmms',5,'seed')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'CMMS_WO','CMMS 작업지시','/cmms/work-orders', p.menu_id, 1,'seed'
FROM tb_menu p WHERE p.menu_code='CMMS'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'CMMS_PM','CMMS PM계획','/cmms/pm-plans', p.menu_id, 2,'seed'
FROM tb_menu p WHERE p.menu_code='CMMS'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'CMMS_FAULT','CMMS 고장로그','/cmms/fault-logs', p.menu_id, 3,'seed'
FROM tb_menu p WHERE p.menu_code='CMMS'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

-- 역할별 권한: OP=RW, QA/VIEWER=R, ADMIN=RWE(부모+자식)
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_OP'
  AND NOT EXISTS (SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id);

INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_QA'
  AND NOT EXISTS (SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id);

INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_VIEWER'
  AND NOT EXISTS (SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id);

INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 1, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('CMMS','CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id);






-- 4) 코드ID 변수 (일관명)
SET @WO_OPEN    := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='OPEN'        LIMIT 1);
SET @WO_ASSIGNED:= (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='ASSIGNED'    LIMIT 1);
SET @WO_INPROG  := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='IN_PROGRESS' LIMIT 1);
SET @WO_DONE    := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='DONE'        LIMIT 1);

SET @PRIO_L := (SELECT code_id FROM tb_code WHERE group_code='WO_PRIORITY' AND code='L' LIMIT 1);
SET @PRIO_M := (SELECT code_id FROM tb_code WHERE group_code='WO_PRIORITY' AND code='M' LIMIT 1);
SET @PRIO_H := (SELECT code_id FROM tb_code WHERE group_code='WO_PRIORITY' AND code='H' LIMIT 1);

SET @CYCLE_H := (SELECT code_id FROM tb_code WHERE group_code='CYCLE_TYPE' AND code='HOURS'    LIMIT 1);
SET @CYCLE_D := (SELECT code_id FROM tb_code WHERE group_code='CYCLE_TYPE' AND code='DAYS'     LIMIT 1);
SET @CYCLE_C := (SELECT code_id FROM tb_code WHERE group_code='CYCLE_TYPE' AND code='CALENDAR' LIMIT 1);

SET @LOSS_BRK := (SELECT code_id FROM tb_code WHERE group_code='LOSS_CATEGORY' AND code='BREAKDOWN'    LIMIT 1);
SET @LOSS_SET := (SELECT code_id FROM tb_code WHERE group_code='LOSS_CATEGORY' AND code='SETUP'        LIMIT 1);
SET @LOSS_MIN := (SELECT code_id FROM tb_code WHERE group_code='LOSS_CATEGORY' AND code='MINOR_STOP'   LIMIT 1);
SET @LOSS_QLT := (SELECT code_id FROM tb_code WHERE group_code='LOSS_CATEGORY' AND code='QUALITY'      LIMIT 1);
SET @LOSS_PLN := (SELECT code_id FROM tb_code WHERE group_code='LOSS_CATEGORY' AND code='PLANNED_STOP' LIMIT 1);

-- 5) 고정 참조
SET @EQP := 'E-0001';
SET @OP  := '00000000-0000-0000-0000-0000000000OP';

-- 6) PM 계획 데모 (equipment_id+task_name 멱등)
INSERT INTO tb_cmms_pm_plan
(equipment_id, task_name, cycle_type_code_id, cycle_value, last_done_at, next_due_at, status, created_by, estimated_take_time)
SELECT @EQP, '윤활 점검(주1회)', @CYCLE_D, 7, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 3 DAY), 'ACTIVE', 'seed', 30
WHERE NOT EXISTS (SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='윤활 점검(주1회)');

INSERT INTO tb_cmms_pm_plan
(equipment_id, task_name, cycle_type_code_id, cycle_value, last_done_at, next_due_at, status, created_by, estimated_take_time)
SELECT @EQP, '모터 베어링 교체(500h)', @CYCLE_H, 500, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 10 DAY), 'ACTIVE', 'seed', 120
WHERE NOT EXISTS (SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='모터 베어링 교체(500h)');

-- 7) WO 데모 (request_id 멱등)
INSERT INTO tb_cmms_work_order
(equipment_id, title, priority_code_id, status_code_id, assignee_user_id, request_id, created_by, started_at, finished_at, actual_minutes, parts_cost)
SELECT @EQP, '라인 진동 점검', @PRIO_M, @WO_OPEN, NULL, 'CMMS-WO-DEMO-001', 'seed', NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-001');

INSERT INTO tb_cmms_work_order
(equipment_id, title, priority_code_id, status_code_id, assignee_user_id, request_id, created_by)
SELECT @EQP, '컨베이어 벨트 정렬', @PRIO_H, @WO_ASSIGNED, @OP, 'CMMS-WO-DEMO-002', 'seed'
WHERE NOT EXISTS (SELECT 1 FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-002');

SET @WO1 := (SELECT cmms_wo_id FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-001' LIMIT 1);
SET @WO2 := (SELECT cmms_wo_id FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-002' LIMIT 1);

-- 8) Fault 로그 데모
INSERT INTO tb_cmms_fault_log
(equipment_id, loss_cat_code_id, symptom, action, occurred_at, resolved_at, cmms_wo_id, created_by)
VALUES (@EQP, @LOSS_BRK, '베어링 소음 증가', '윤활 후 상태 모니터링',
        DATE_SUB(UTC_TIMESTAMP(), INTERVAL 2 HOUR), NULL, @WO1, 'seed');

INSERT INTO tb_cmms_fault_log
(equipment_id, loss_cat_code_id, symptom, action, occurred_at, resolved_at, cmms_wo_id, created_by)
VALUES (@EQP, @LOSS_SET, '제품 전환 셋업', '가이드 교체',
        DATE_SUB(UTC_TIMESTAMP(), INTERVAL 6 HOUR),
        DATE_SUB(UTC_TIMESTAMP(), INTERVAL 5 HOUR), @WO2, 'seed');


/* 1) tb_code_group: 숙련도 코드 그룹 추가 */
-- 숙련도(SKILL_LEVEL) 코드 그룹이 이미 존재하면 업데이트하고, 없으면 새로 추가합니다.
INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('SKILL_LEVEL','숙련도','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

/* 2) tb_code: 숙련도 코드 추가 */
-- 기본(BASIC), 중급(INTERMEDIATE), 고급(EXPERT) 숙련도 코드를 추가합니다.
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('SKILL_LEVEL','BASIC','기본','Y',1,'seed'),
('SKILL_LEVEL','INTERMEDIATE','중급','Y',2,'seed'),
('SKILL_LEVEL','EXPERT','고급','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

/* 3) 코드 id 변수 설정 */
-- 새로 추가된 숙련도 코드의 ID를 변수에 저장하여 편리하게 사용합니다.
SET @SKILL_BASIC := (SELECT code_id FROM tb_code WHERE group_code='SKILL_LEVEL' AND code='BASIC');
SET @SKILL_INTERMEDIATE := (SELECT code_id FROM tb_code WHERE group_code='SKILL_LEVEL' AND code='INTERMEDIATE');
SET @SKILL_EXPERT := (SELECT code_id FROM tb_code WHERE group_code='SKILL_LEVEL' AND code='EXPERT');

/* 4) tb_employee: 직원 정보 추가 */
-- 기존 사용자(tb_user)를 기반으로 직원 정보를 생성합니다.
INSERT IGNORE INTO tb_employee (employee_id, employee_number, employee_name, created_by) VALUES
('00000000-0000-0000-0000-0000000000OP','EMP-OP','운영자','seed'),
('00000000-0000-0000-0000-0000000000QA','EMP-QA','품질관리자','seed'),
('00000000-0000-0000-0000-0000000000AD','EMP-ADMIN','관리자','seed');

/* 5) tb_cert: 자격증 마스터 정보 추가 */
-- 세 가지 샘플 자격증 정보를 추가합니다.
INSERT IGNORE INTO tb_cert (cert_code, cert_name, cert_description, cert_valid_period_months, created_by) VALUES
('C-FORKLIFT','지게차 교육 이수','3톤 미만 지게차 운전 가능.', null, 'seed'),
('0410100','전기기사','전기 설비 및 장비 관련 국가기술자격증', null, 'seed'),
('1910100','산업안전기사','안전 관련 국가기술자격증', null, 'seed');

/* 6) 자격증 id 변수 설정 */
-- 새로 추가된 자격증의 ID를 변수에 저장합니다.
SET @cert_FORKLIFT := (SELECT cert_id FROM tb_cert WHERE cert_code='C-FORKLIFT');
SET @cert_ELECTRIC := (SELECT cert_id FROM tb_cert WHERE cert_code='0410100');
SET @cert_SAFETY := (SELECT cert_id FROM tb_cert WHERE cert_code='1910100');

/* 7) tb_employee_cert: 직원별 자격증 보유 현황 추가 */
-- 각 직원이 취득한 자격증과 숙련도를 매핑합니다. 만료일은 유효기간을 기준으로 계산됩니다.
-- 중복된 데이터가 삽입되지 않도록 `INSERT IGNORE`를 사용합니다.
INSERT IGNORE INTO tb_employee_cert (employee_id, cert_id, employee_level_code_id, cert_obtained_date, cert_expiry_date, created_by) VALUES
-- 운영자(EMP-OP)는 지게차와 안전기사, 전기기사 자격증 보유
('00000000-0000-0000-0000-0000000000OP', @cert_FORKLIFT, @SKILL_EXPERT, '2023-01-15', null, 'seed'),
('00000000-0000-0000-0000-0000000000OP', @cert_SAFETY, @SKILL_INTERMEDIATE, '2024-03-20', null, 'seed'),
('00000000-0000-0000-0000-0000000000OP', @cert_ELECTRIC, @SKILL_EXPERT, '2022-10-25', null, 'seed'),
-- 품질관리자(EMP-QA)는 안전기사 자격증 보유
('00000000-0000-0000-0000-0000000000QA', @cert_SAFETY, @SKILL_EXPERT, '2024-01-10', null, 'seed'),
-- 관리자(EMP-ADMIN)는 지게차 교육, 안전기사, 전기기사 자격증 보유
('00000000-0000-0000-0000-0000000000AD', @cert_FORKLIFT, @SKILL_BASIC, '2023-05-01', null, 'seed'),
('00000000-0000-0000-0000-0000000000AD', @cert_SAFETY, @SKILL_BASIC, '2024-02-14', null, 'seed'),
('00000000-0000-0000-0000-0000000000AD', @cert_ELECTRIC, @SKILL_BASIC, '2022-10-25', null, 'seed');

/* 8) tb_process_cert: 공정별 필요 자격 추가 */
-- P-0002 공정은 '산업안전기사' 자격증이 필요합니다.
INSERT IGNORE INTO tb_process_cert (process_id, cert_id) VALUES
('P-0002', @cert_SAFETY);

/* 9) tb_equipment_cert: 설비별 필요 자격 추가 */
-- E-0002 설비는 '전기기사' 자격증이 필요합니다.
INSERT IGNORE INTO tb_equipment_cert (equipment_id, cert_id) VALUES
('E-0002', @cert_ELECTRIC);




/* 1) tb_user: tb_employee와 매핑되는 사용자 정보 추가 */
-- tb_shift_assignment.worker_id가 참조하는 tb_user 테이블에 동일 ID의 사용자를 생성합니다.
-- `password_hash`는 'password'를 BCrypt로 해시한 예시 값입니다.
INSERT INTO tb_user (user_id, username, password_hash, password_algo, created_by) VALUES
('00000000-0000-0000-0000-000000000001', 'employee01', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000002', 'employee02', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000003', 'employee03', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000004', 'employee04', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000005', 'employee05', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000006', 'employee06', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000007', 'employee07', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000008', 'employee08', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000009', 'employee09', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed'),
('00000000-0000-0000-0000-000000000010', 'employee10', '$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2', 'bcrypt', 'seed')
ON DUPLICATE KEY UPDATE username=VALUES(username), password_hash=VALUES(password_hash);

/* 2) tb_user_role: 추가된 직원에게 'OP' 역할 부여 */
-- 'OP' 역할의 ID를 찾아서 사용자-역할 매핑을 추가합니다.
INSERT IGNORE INTO tb_user_role (user_id, role_id, created_by)
SELECT T1.user_id, T2.role_id, 'seed'
FROM tb_user T1
JOIN tb_role T2 ON T2.role_code = 'ROLE_OP'
WHERE T1.username LIKE 'employee%';


/* 1) tb_employee: 추가 직원 정보 10명 추가 */
-- EMP-01부터 EMP-10까지 10명의 직원을 추가합니다.
INSERT INTO tb_employee (employee_id, employee_number, employee_name, created_by) VALUES
('00000000-0000-0000-0000-000000000001', 'EMP-01', '직원-01', 'seed'),
('00000000-0000-0000-0000-000000000002', 'EMP-02', '직원-02', 'seed'),
('00000000-0000-0000-0000-000000000003', 'EMP-03', '직원-03', 'seed'),
('00000000-0000-0000-0000-000000000004', 'EMP-04', '직원-04', 'seed'),
('00000000-0000-0000-0000-000000000005', 'EMP-05', '직원-05', 'seed'),
('00000000-0000-0000-0000-000000000006', 'EMP-06', '직원-06', 'seed'),
('00000000-0000-0000-0000-000000000007', 'EMP-07', '직원-07', 'seed'),
('00000000-0000-0000-0000-000000000008', 'EMP-08', '직원-08', 'seed'),
('00000000-0000-0000-0000-000000000009', 'EMP-09', '직원-09', 'seed'),
('00000000-0000-0000-0000-000000000010', 'EMP-10', '직원-10', 'seed')
ON DUPLICATE KEY UPDATE employee_number=VALUES(employee_number), employee_name=VALUES(employee_name);

/* 2) tb_employee_cert: 추가 직원별 자격증 보유 현황 추가 */
-- 새롭게 추가된 10명의 직원에게 지게차, 전기기사, 안전기사 자격증을 무작위로 할당합니다.
INSERT IGNORE INTO tb_employee_cert (employee_id, cert_id, employee_level_code_id, cert_obtained_date, created_by) VALUES
('00000000-0000-0000-0000-000000000001', @cert_FORKLIFT, @SKILL_BASIC, '2023-01-20', 'seed'),
('00000000-0000-0000-0000-000000000001', @cert_SAFETY, @SKILL_INTERMEDIATE, '2024-03-22', 'seed'),
('00000000-0000-0000-0000-000000000002', @cert_ELECTRIC, @SKILL_EXPERT, '2022-11-05', 'seed'),
('00000000-0000-0000-0000-000000000003', @cert_FORKLIFT, @SKILL_INTERMEDIATE, '2023-08-11', 'seed'),
('00000000-0000-0000-0000-000000000004', @cert_SAFETY, @SKILL_BASIC, '2024-01-15', 'seed'),
('00000000-0000-0000-0000-000000000005', @cert_ELECTRIC, @SKILL_INTERMEDIATE, '2023-04-30', 'seed'),
('00000000-0000-0000-0000-000000000006', @cert_FORKLIFT, @SKILL_EXPERT, '2022-09-01', 'seed'),
('00000000-0000-0000-0000-000000000007', @cert_SAFETY, @SKILL_EXPERT, '2023-07-07', 'seed'),
('00000000-0000-0000-0000-000000000008', @cert_FORKLIFT, @SKILL_BASIC, '2024-05-12', 'seed'),
('00000000-0000-0000-0000-000000000009', @cert_ELECTRIC, @SKILL_INTERMEDIATE, '2024-02-28', 'seed'),
('00000000-0000-0000-0000-000000000010', @cert_SAFETY, @SKILL_BASIC, '2023-09-18', 'seed'),
('00000000-0000-0000-0000-000000000010', @cert_ELECTRIC, @SKILL_INTERMEDIATE, '2023-11-20', 'seed');

INSERT INTO tb_shift_assignment (shift_date, shift_id, worker_id, equipment_id, start_ts, end_ts, created_by) VALUES
(CURRENT_DATE(), 1, '00000000-0000-0000-0000-000000000001', 'E-0001', CONCAT(CURRENT_DATE(), ' 07:00:00'), CONCAT(CURRENT_DATE(), ' 15:00:00'), 'seed'),
(CURRENT_DATE(), 1, '00000000-0000-0000-0000-000000000002', 'E-0002', CONCAT(CURRENT_DATE(), ' 07:00:00'), CONCAT(CURRENT_DATE(), ' 15:00:00'), 'seed'),
(CURRENT_DATE(), 2, '00000000-0000-0000-0000-000000000003', 'E-0001', CONCAT(CURRENT_DATE(), ' 15:00:00'), CONCAT(CURRENT_DATE(), ' 23:00:00'), 'seed'),
(CURRENT_DATE(), 2, '00000000-0000-0000-0000-000000000004', 'E-0002', CONCAT(CURRENT_DATE(), ' 15:00:00'), CONCAT(CURRENT_DATE(), ' 23:00:00'), 'seed'),
(CURRENT_DATE(), 3, '00000000-0000-0000-0000-000000000005', 'E-0001', CONCAT(CURRENT_DATE(), ' 23:00:00'), CONCAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), ' 07:00:00'), 'seed'),
(CURRENT_DATE(), 3, '00000000-0000-0000-0000-000000000006', 'E-0002', CONCAT(CURRENT_DATE(), ' 23:00:00'), CONCAT(DATE_ADD(CURRENT_DATE(), INTERVAL 1 DAY), ' 07:00:00'), 'seed')
ON DUPLICATE KEY UPDATE end_ts=VALUES(end_ts);

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

-- 출퇴근 추가
INSERT IGNORE INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', '출퇴근 상태', '직원의 출근 및 퇴근 상태를 정의합니다.', 'admin');
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', 'CHECK_IN', '출근', '직원 출근', 'seed');
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', 'CHECK_OUT', '퇴근', '직원 퇴근', 'seed');
