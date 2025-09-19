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
