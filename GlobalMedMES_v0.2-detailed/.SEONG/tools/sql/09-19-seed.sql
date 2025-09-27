
SET NAMES utf8mb4;
SET time_zone = '+00:00';


/* 1) 코드 그룹/코드 (WO_STATUS/EQP_STATUS/INSPECTION_TYPE/DEFECT_TYPE) */
INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('WO_STATUS','작업지시 상태','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('EQP_STATUS','설비 상태','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('INSPECTION_TYPE','검사 유형','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('DEFECT_TYPE','불량 유형','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);


INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('PROD_EVENT','생산 로그 이벤트','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

-- 코드 그룹: 집계 유형
INSERT IGNORE INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES
('KPI_DATA_TYPE', 'KPI 집계 유형', 'KPI 데이터의 집계 기준을 정의합니다.', 'seed');

-- 코드 그룹: 계산 상태
INSERT IGNORE INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES
('KPI_CALC_STATUS', 'KPI 계산 상태', 'KPI 계산 작업의 상태를 정의합니다.', 'seed');


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


INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('SKILL_LEVEL','숙련도','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

-- 출퇴근 추가
INSERT IGNORE INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', '출퇴근 상태', '직원의 출근 및 퇴근 상태를 정의합니다.', 'admin');



-- WO_STATUS
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('WO_STATUS','P','Planned','Y',1,'seed'),
('WO_STATUS','R','Released','Y',2,'seed'),
('WO_STATUS','C','Completed','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- EQP_STATUS
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('EQP_STATUS','RUN','가동','Y',1,'seed'),
('EQP_STATUS','IDLE','유휴','Y',2,'seed'),
('EQP_STATUS','DOWN','정지','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- INSPECTION_TYPE
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('INSPECTION_TYPE','INCOMING','수입검사','Y',1,'seed'),
('INSPECTION_TYPE','PROCESS','공정검사','Y',2,'seed'),
('INSPECTION_TYPE','OUTGOING','출하검사','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn);

-- DEFECT_TYPE
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('DEFECT_TYPE','SCRATCH','스크래치','Y',1,'seed'),
('DEFECT_TYPE','BURR','버어','Y',2,'seed'),
('DEFECT_TYPE','CRACK','크랙','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn);


-- 코드: 집계 유형
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `sort_order`, `created_by`)
VALUES
('KPI_DATA_TYPE', 'REALTIME', '실시간', 1, 'seed'),
('KPI_DATA_TYPE', 'DAILY_BATCH', '일일 배치', 2, 'seed');

-- 코드: 계산 상태
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `sort_order`, `created_by`)
VALUES
('KPI_CALC_STATUS', 'SUCCESS', '성공', 1, 'seed'),
('KPI_CALC_STATUS', 'FAIL', '실패', 2, 'seed');


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

INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('PROD_EVENT','START','시작','Y',1,'seed'),
('PROD_EVENT','END','종료','Y',2,'seed'),
('PROD_EVENT','GOODQTY','양품수','Y',3,'seed'),
('PROD_EVENT','DEFECTQTY','불량수','Y',4,'seed'),
('PROD_EVENT','DOWNTIME_START','비가동 시작','Y',5,'seed'),
('PROD_EVENT','DOWNTIME_END','비가동 종료','Y',6,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);


INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('SKILL_LEVEL','BASIC','기본','Y',1,'seed'),
('SKILL_LEVEL','INTERMEDIATE','중급','Y',2,'seed'),
('SKILL_LEVEL','EXPERT','고급','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', 'CHECK_IN', '출근', '직원 출근', 'seed');
INSERT IGNORE INTO `tb_code` (`group_code`, `code`, `name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', 'CHECK_OUT', '퇴근', '직원 퇴근', 'seed');

/* 2) 역할/사용자/메뉴/RBAC */
INSERT INTO tb_role (role_code, role_name, created_by) VALUES
('ROLE_OP','운영자','seed'),
('ROLE_QA','품질','seed'),
('ROLE_ADMIN','관리자','seed'),
('ROLE_VIEWER','열람자','seed')
ON DUPLICATE KEY UPDATE role_name=VALUES(role_name);


-- demo hash(교체 권장) 비밀번호: gmmes1121
INSERT INTO tb_user (user_id, username, password_hash, password_algo, is_active, display_name, created_by, email) VALUES
('00000000-0000-0000-0000-0000000000AD','admin','$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2','bcrypt',1,'Admin','seed','admin@example.com'),
('00000000-0000-0000-0000-0000000000OP','op','$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2','bcrypt',1,'Operator','seed',NULL),
('00000000-0000-0000-0000-0000000000QA','qa','$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2','bcrypt',1,'QA','seed',NULL),
('00000000-0000-0000-0000-0000000000VW','viewer','$2a$10$1CRPepUcsXEBY/r..LUbjObOrb7k8PpTV8D4LBwbME6oUC6tSfdI2','bcrypt',1,'Viewer','seed',NULL)
ON DUPLICATE KEY UPDATE is_active=VALUES(is_active), display_name=VALUES(display_name);

-- USER_ROLE 매핑
INSERT INTO tb_user_role (user_id, role_id, created_by)
SELECT '00000000-0000-0000-0000-0000000000AD', r.role_id, 'seed' FROM tb_role r WHERE r.role_code='ROLE_ADMIN'
ON DUPLICATE KEY UPDATE user_id=user_id;
INSERT INTO tb_user_role (user_id, role_id, created_by)
SELECT '00000000-0000-0000-0000-0000000000OP', r.role_id, 'seed' FROM tb_role r WHERE r.role_code='ROLE_OP'
ON DUPLICATE KEY UPDATE user_id=user_id;
INSERT INTO tb_user_role (user_id, role_id, created_by)
SELECT '00000000-0000-0000-0000-0000000000QA', r.role_id, 'seed' FROM tb_role r WHERE r.role_code='ROLE_QA'
ON DUPLICATE KEY UPDATE user_id=user_id;
INSERT INTO tb_user_role (user_id, role_id, created_by)
SELECT '00000000-0000-0000-0000-0000000000VW', r.role_id, 'seed' FROM tb_role r WHERE r.role_code='ROLE_VIEWER'
ON DUPLICATE KEY UPDATE user_id=user_id;


-- 메뉴
INSERT INTO tb_menu (menu_code, menu_name, path, sort_order, created_by) VALUES
('DASH','대시보드','/dashboard',0,'seed'),
('WO','작업지시','/work-orders',1,'seed'),
('EQPSTAT','설비상태','/equip-status',2,'seed'),
('PERF','실적','/performances',3,'seed'),
('KPI','KPI','/kpi',4,'seed'),
('QUALITY','품질','/quality',5,'seed'),
('ADMIN','관리','/admin',99,'seed')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path);

-- ROLE_MENU 권한
-- OP: WO/EQPSTAT/PERF/KPI 읽기·쓰기
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('WO','EQPSTAT','PERF','KPI')
WHERE r.role_code='ROLE_OP'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- QA: KPI/QUALITY 읽기, QUALITY 쓰기(라이트)
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, IF(m.menu_code='QUALITY',1,0), 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('KPI','QUALITY')
WHERE r.role_code='ROLE_QA'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- VIEWER: 주요 메뉴 읽기만
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('DASH','WO','EQPSTAT','PERF','KPI')
WHERE r.role_code='ROLE_VIEWER'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- ADMIN: 전 메뉴 읽기·쓰기·실행
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 1, 'seed'
FROM tb_role r JOIN tb_menu m
WHERE r.role_code='ROLE_ADMIN'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

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

-- 1) Shift 테이블 (3조 3교대)
INSERT INTO tb_shift (shift_id, shift_code, shift_name, start_time, end_time, created_by) VALUES
(1,'A','주간조','07:00:00','15:00:00','seed'),
(2,'B','전반야','15:00:00','23:00:00','seed'),
(3,'C','후반야','23:00:00','07:00:00','seed')
ON DUPLICATE KEY UPDATE shift_name=VALUES(shift_name), start_time=VALUES(start_time), end_time=VALUES(end_time);

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



-- 공장(Workshop) 마스터 데이터
INSERT INTO `tb_workshop` (`workshop_id`, `workshop_name`, `description`, `created_by`) VALUES
('WKS-001', '임플란트 생산 공장', '치과용 임플란트 생산 전체 라인', 'system');


-- 작업장(Workcenter) 마스터 데이터
INSERT INTO `tb_workcenter` (`workcenter_id`, `workcenter_name`, `workshop_id`, `description`, `created_by`) VALUES
('WCR-001', '정밀가공실', 'WKS-001', 'CNC 선반이 위치한 작업장', 'system'),
('WCR-002', '표면처리실', 'WKS-001', '블라스팅, 에칭, 세척 공정이 진행되는 작업장', 'system'),
('WCR-003', '클린룸 및 포장실', 'WKS-001', '최종 검수, 포장, 멸균 공정이 진행되는 청정구역', 'system'),
('QZR-001', '품질보증실', 'WKS-001', '3차원 측정기 등 정밀 검사 장비가 위치한 구역', 'system');

-- 공정 마스터 데이터
INSERT INTO `tb_process` (`process_id`, `process_name`, `created_by`) VALUES
('P-100', 'CNC 가공 공정', 'system'),
('P-200', '블라스팅 공정', 'system'),
('P-300', '에칭 공정', 'system'),
('P-400', '세척 및 건조 공정', 'system'),
('P-500', '포장 및 멸균 공정', 'system'),
('Q-100', '원자재 검사', 'system'),
('Q-200', '최종 형상 검사', 'system'),
('W-100', '자재 입고', 'system'),
('W-200', '완제품 출하', 'system');

-- 설비(Equipment) 마스터 데이터
INSERT INTO `tb_equipment` (`equipment_id`, `equipment_name`, `workcenter_id`, `process_id`, `status_code_id`, `created_by`) VALUES
('CNC-001', '1호기 CNC 선반', 'WCR-001', 'P-100', 4, 'system'),
('CNC-002', '2호기 CNC 선반', 'WCR-001', 'P-100', 4, 'system'),
('BLS-001', '1호기 블라스팅기', 'WCR-002', 'P-200', 4, 'system'),
('ETC-001', '1호기 자동 에칭조', 'WCR-002', 'P-300', 4, 'system'),
('CLN-001', '초음파 정밀 세척기', 'WCR-002', 'P-400', 4, 'system'),
('INS-001', '3차원 비전 검사기', 'QZR-001', 'Q-200', 4, 'system');

-- 품목 마스터 데이터
INSERT INTO `tb_item` (`item_id`, `item_code`, `item_name`, `item_type`, `unit`, `description`, `created_by`) VALUES
('RM-TI-G5-001', 'RM-TI-G5-001', '티타늄 Bar Grade5', 'R', 'EA', 'Ø10mm, L3000mm', 'system'),
('RM-ACD-SLA-001', 'RM-ACD-SLA-001', 'SLA 표면처리용액', 'R', 'L', '불산/질산 혼합액', 'system'),
('SM-FIX-4010-001', 'SM-FIX-4010-001', 'Fixture Semi 4.0x10', 'P', 'EA', 'CNC 가공 완료, 표면처리 전', 'system'),
('FG-FIX-4010S-001', 'FG-FIX-4010S-001', '임플란트 Fixture S-Type 4.0x10', 'F', 'EA', 'Ø4.0mm, L10mm, SLA Surface', 'system'),
('FG-FIX-4512S-001', 'FG-FIX-4512S-001', '임플란트 Fixture S-Type 4.5x12', 'F', 'EA', 'Ø4.5mm, L12mm, SLA Surface', 'system');


