-- 0) 코드그룹/코드 시드 (멱등 업서트: MySQL 8.0.20+ 권장 문법)
INSERT INTO tb_code_group AS g (group_code, group_name, description, created_by)
VALUES 
  ('LOSS_CATEGORY','손실 카테고리','CMMS 고장/정지 분류','system'),
  ('PM_CYCLE_TYPE','PM 주기유형','CMMS PM 주기','system'),
  ('WO_STATUS','WO 상태','보전 작업지시 상태','system'),
  ('WO_PRIORITY','WO 우선순위','보전 작업지시 우선순위','system')
ON DUPLICATE KEY UPDATE group_name = g.group_name, description = g.description;

-- 손실 카테고리 Top5
INSERT INTO tb_code AS c (group_code, code, name, description, use_yn, sort_order, created_by)
VALUES
  ('LOSS_CATEGORY','BREAKDOWN','Breakdown','고장으로 인한 정지','Y',10,'system'),
  ('LOSS_CATEGORY','SETUP','Setup','교체/세팅','Y',20,'system'),
  ('LOSS_CATEGORY','MINOR_STOP','Minor Stop','단소정지','Y',30,'system'),
  ('LOSS_CATEGORY','QUALITY','Quality','품질 이슈','Y',40,'system'),
  ('LOSS_CATEGORY','PLANNED_STOP','Planned Stop','계획정지','Y',50,'system')
ON DUPLICATE KEY UPDATE name = c.name, description = c.description, use_yn=c.use_yn, sort_order=c.sort_order;

-- PM 주기유형
INSERT INTO tb_code AS c (group_code, code, name, use_yn, sort_order, created_by)
VALUES
  ('PM_CYCLE_TYPE','HOURS','Hours','Y',10,'system'),
  ('PM_CYCLE_TYPE','DAYS','Days','Y',20,'system'),
  ('PM_CYCLE_TYPE','CALENDAR','Calendar','Y',30,'system')
ON DUPLICATE KEY UPDATE name=c.name, use_yn=c.use_yn, sort_order=c.sort_order;

-- WO 상태
INSERT INTO tb_code AS c (group_code, code, name, use_yn, sort_order, created_by)
VALUES
  ('WO_STATUS','OPEN','Open','Y',10,'system'),
  ('WO_STATUS','ASSIGNED','Assigned','Y',20,'system'),
  ('WO_STATUS','IN_PROGRESS','In Progress','Y',30,'system'),
  ('WO_STATUS','DONE','Done','Y',40,'system')
ON DUPLICATE KEY UPDATE name=c.name, use_yn=c.use_yn, sort_order=c.sort_order;

-- WO 우선순위
INSERT INTO tb_code AS c (group_code, code, name, use_yn, sort_order, created_by)
VALUES
  ('WO_PRIORITY','L','Low','Y',10,'system'),
  ('WO_PRIORITY','M','Medium','Y',20,'system'),
  ('WO_PRIORITY','H','High','Y',30,'system')
ON DUPLICATE KEY UPDATE name=c.name, use_yn=c.use_yn, sort_order=c.sort_order;

-- 1) PM 계획 (ENUM 제거 → 코드마스터 FK)
CREATE TABLE tb_cmms_pm_plan (
  plan_id              BIGINT NOT NULL AUTO_INCREMENT COMMENT 'PM 계획 ID (PK)',
  equipment_id         VARCHAR(36) NOT NULL COMMENT '설비 ID (FK→tb_equipment)',
  task_name            VARCHAR(100) NOT NULL COMMENT '작업명',
  cycle_type_code_id   BIGINT NOT NULL COMMENT '주기유형 코드(FK→tb_code: PM_CYCLE_TYPE)',
  cycle_value          INT NOT NULL COMMENT '주기 값',
  last_done_at         DATETIME NULL COMMENT '마지막 완료(UTC)',
  next_due_at          DATETIME NOT NULL COMMENT '다음 도래(UTC)',
  status               VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '상태(라이트)',
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
  CONSTRAINT fk_pm_equipment      FOREIGN KEY (equipment_id)       REFERENCES tb_equipment(equipment_id) ON DELETE RESTRICT,
  CONSTRAINT fk_pm_cycle_typecode FOREIGN KEY (cycle_type_code_id) REFERENCES tb_code(code_id)           ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='CMMS: PM 계획(주기/도래, 코드마스터)';

-- 2) 보전 작업지시(헤더) (ENUM 제거 → 코드마스터 FK)
CREATE TABLE tb_cmms_work_order (
  cmms_wo_id         BIGINT NOT NULL AUTO_INCREMENT COMMENT '보전 WO ID (PK)',
  equipment_id       VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  title              VARCHAR(200) NOT NULL COMMENT '제목',
  priority_code_id   BIGINT NOT NULL COMMENT '우선순위 코드(FK→tb_code: WO_PRIORITY)',
  status_code_id     BIGINT NOT NULL COMMENT '상태 코드(FK→tb_code: WO_STATUS)',
  assignee_user_id   VARCHAR(36) NULL COMMENT '담당자 ID (FK→tb_user)',
  request_id         VARCHAR(64) NULL COMMENT '멱등키(UNIQUE)',
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  started_at         DATETIME NULL COMMENT 'UTC',
  finished_at        DATETIME NULL COMMENT 'UTC',
  actual_minutes     INT NULL COMMENT '소요(분)',
  parts_cost         DECIMAL(12,2) NULL COMMENT '부품비',
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
  CONSTRAINT fk_cmms_wo_equipment FOREIGN KEY (equipment_id)       REFERENCES tb_equipment(equipment_id) ON DELETE RESTRICT,
  CONSTRAINT fk_cmms_wo_assignee  FOREIGN KEY (assignee_user_id)   REFERENCES tb_user(user_id)           ON DELETE RESTRICT,
  CONSTRAINT fk_wo_priority_code  FOREIGN KEY (priority_code_id)   REFERENCES tb_code(code_id)           ON DELETE RESTRICT,
  CONSTRAINT fk_wo_status_code    FOREIGN KEY (status_code_id)     REFERENCES tb_code(code_id)           ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='CMMS: 보전 작업지시(코드마스터)';

-- 3) 보전 작업지시 이력 (ENUM 제거 → 코드마스터 FK)
CREATE TABLE tb_cmms_work_order_log (
  log_id               BIGINT NOT NULL AUTO_INCREMENT COMMENT '이력 ID (PK)',
  cmms_wo_id           BIGINT NOT NULL COMMENT '보전 WO ID (FK)',
  from_status_code_id  BIGINT NULL COMMENT '이전 상태(FK→tb_code: WO_STATUS)',
  to_status_code_id    BIGINT NOT NULL COMMENT '변경 상태(FK→tb_code: WO_STATUS)',
  changed_by           VARCHAR(36) NOT NULL COMMENT '변경자(tb_user)',
  changed_at           DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  note                 VARCHAR(255) NULL,
  PRIMARY KEY (log_id),
  KEY ix_wolog_wo (cmms_wo_id, changed_at),
  CONSTRAINT fk_cmms_wolog_wo   FOREIGN KEY (cmms_wo_id)          REFERENCES tb_cmms_work_order(cmms_wo_id) ON DELETE CASCADE,
  CONSTRAINT fk_wolog_from_code FOREIGN KEY (from_status_code_id) REFERENCES tb_code(code_id)               ON DELETE RESTRICT,
  CONSTRAINT fk_wolog_to_code   FOREIGN KEY (to_status_code_id)   REFERENCES tb_code(code_id)               ON DELETE RESTRICT,
  CONSTRAINT fk_cmms_wolog_user FOREIGN KEY (changed_by)          REFERENCES tb_user(user_id)               ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='CMMS: WO 상태전이 이력(코드마스터)';

-- 4) 고장 로그 (이미 코드마스터 사용)
CREATE TABLE tb_cmms_fault_log (
  fault_id           BIGINT NOT NULL AUTO_INCREMENT COMMENT '고장 ID (PK)',
  equipment_id       VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  loss_cat_code_id   BIGINT NOT NULL COMMENT '손실 카테고리 코드(FK→tb_code: LOSS_CATEGORY)',
  symptom            VARCHAR(255) NOT NULL COMMENT '증상',
  action             VARCHAR(255) NULL COMMENT '조치',
  occurred_at        DATETIME NOT NULL COMMENT '발생(UTC)',
  resolved_at        DATETIME NULL COMMENT '해결(UTC)',
  cmms_wo_id         BIGINT NULL COMMENT '연결 보전 WO (선택)',
  is_deleted         TINYINT DEFAULT 0,
  deleted_at         DATETIME NULL,
  created_by         VARCHAR(50) NOT NULL,
  created_at         DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  modified_by        VARCHAR(50) NULL,
  modified_at        DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (fault_id),
  KEY ix_fault_time_eqp (occurred_at, equipment_id),
  KEY ix_fault_loss (loss_cat_code_id),
  CONSTRAINT fk_fault_equipment FOREIGN KEY (equipment_id)     REFERENCES tb_equipment(equipment_id)     ON DELETE RESTRICT,
  CONSTRAINT fk_fault_losscode  FOREIGN KEY (loss_cat_code_id) REFERENCES tb_code(code_id)              ON DELETE RESTRICT,
  CONSTRAINT fk_fault_cmmswo    FOREIGN KEY (cmms_wo_id)       REFERENCES tb_cmms_work_order(cmms_wo_id) ON DELETE SET NULL,
  CONSTRAINT ck_fault_time_order CHECK (resolved_at IS NULL OR resolved_at >= occurred_at)
) ENGINE=InnoDB COMMENT='CMMS: 고장/정지 로그(코드마스터)';

/* 1) CMMS 부모 메뉴 */
INSERT INTO tb_menu (menu_code, menu_name, path, sort_order, created_by)
VALUES ('CMMS','CMMS','/cmms',5,'seed')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path);

/* 2) 자식 메뉴: WO / PM / FAULT (부모 menu_id 를 동적으로 참조) */
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

/* 3) (정리) 과거에 /fault-logs 로 만든 CMMS_FAULT가 있다면 경로/부모 맞추기 */
UPDATE tb_menu m
JOIN tb_menu p ON p.menu_code='CMMS'
SET m.path='/cmms/fault-logs', m.parent_id=p.menu_id
WHERE m.menu_code='CMMS_FAULT' AND (m.path <> '/cmms/fault-logs' OR m.parent_id IS NULL);

/* 4) 역할별 권한 매핑 — OP: 읽기/쓰기 */
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 0, 'seed'
FROM tb_role r
JOIN tb_menu m ON m.menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_OP'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id
  );

/* QA: 읽기만 */
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r
JOIN tb_menu m ON m.menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_QA'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id
  );

/* VIEWER: 읽기만 */
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r
JOIN tb_menu m ON m.menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_VIEWER'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id
  );

/* ADMIN: 부모 + 자식 전부 R/W/Exec */
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 1, 'seed'
FROM tb_role r
JOIN tb_menu m ON m.menu_code IN ('CMMS','CMMS_WO','CMMS_PM','CMMS_FAULT')
WHERE r.role_code='ROLE_ADMIN'
  AND NOT EXISTS (
    SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id
  );

USE globalmed;

/* 0) 코드 id 변수 로딩 */
SET @WO_OPEN   := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='OPEN'        LIMIT 1);
SET @WO_ASSIGN := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='ASSIGNED'    LIMIT 1);
SET @WO_IP     := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='IN_PROGRESS' LIMIT 1);
SET @WO_DONE   := (SELECT code_id FROM tb_code WHERE group_code='CMMS_WO_STATUS' AND code='DONE'        LIMIT 1);

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

/* 1) 고정 참조 (없으면 적당히 바꿔 사용) */
SET @EQP := 'E-0001';
SET @OP  := '00000000-0000-0000-0000-0000000000OP';

/* 2) PM 계획 (equipment_id + task_name 멱등) */
INSERT INTO tb_cmms_pm_plan
(equipment_id, task_name, cycle_type_code_id, cycle_value, last_done_at, next_due_at, status, created_by)
SELECT @EQP, '윤활 점검(주1회)', @CYCLE_D, 7, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 3 DAY), 'ACTIVE', 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='윤활 점검(주1회)'
);

INSERT INTO tb_cmms_pm_plan
(equipment_id, task_name, cycle_type_code_id, cycle_value, last_done_at, next_due_at, status, created_by)
SELECT @EQP, '모터 베어링 교체(500h)', @CYCLE_H, 500, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 10 DAY), 'ACTIVE', 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='모터 베어링 교체(500h)'
);

/* 3) 보전 작업지시 (request_id로 멱등) */
INSERT INTO tb_cmms_work_order
(equipment_id, title, priority_code_id, status_code_id, assignee_user_id, request_id, created_by, started_at, finished_at, actual_minutes, parts_cost)
SELECT @EQP, '라인 진동 점검', @PRIO_M, @WO_OPEN, NULL, 'CMMS-WO-DEMO-001', 'seed', NULL, NULL, NULL, NULL
WHERE NOT EXISTS (SELECT 1 FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-001');

INSERT INTO tb_cmms_work_order
(equipment_id, title, priority_code_id, status_code_id, assignee_user_id, request_id, created_by)
SELECT @EQP, '컨베이어 벨트 정렬', @PRIO_H, @WO_ASSIGN, @OP, 'CMMS-WO-DEMO-002', 'seed'
WHERE NOT EXISTS (SELECT 1 FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-002');

/* id 변수 */
SET @WO1 := (SELECT cmms_wo_id FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-001' LIMIT 1);
SET @WO2 := (SELECT cmms_wo_id FROM tb_cmms_work_order WHERE request_id='CMMS-WO-DEMO-002' LIMIT 1);

/* 4) 고장 로그 (단순 표본) */
INSERT INTO tb_cmms_fault_log
(equipment_id, loss_cat_code_id, symptom, action, occurred_at, resolved_at, cmms_wo_id, created_by)
VALUES
(@EQP, @LOSS_BRK, '베어링 소음 증가', '윤활 후 상태 모니터링', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 2 HOUR), NULL, @WO1, 'seed');

INSERT INTO tb_cmms_fault_log
(equipment_id, loss_cat_code_id, symptom, action, occurred_at, resolved_at, cmms_wo_id, created_by)
VALUES
(@EQP, @LOSS_SET, '제품 전환 셋업', '가이드 교체', DATE_SUB(UTC_TIMESTAMP(), INTERVAL 6 HOUR), DATE_SUB(UTC_TIMESTAMP(), INTERVAL 5 HOUR), @WO2, 'seed');