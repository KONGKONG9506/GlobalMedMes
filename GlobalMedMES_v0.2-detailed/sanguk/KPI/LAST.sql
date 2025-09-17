
-- 직원
CREATE TABLE `tb_employee` (
  `employee_id` VARCHAR(36) NOT NULL COMMENT '직원 ID (PK, FK→tb_user.user_id)',
  `employee_number` VARCHAR(50) NOT NULL COMMENT '직원 번호',
  `employee_name` VARCHAR(100) NOT NULL COMMENT '직원 이름',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `uk_employee_number` (`employee_number`),
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`employee_id`) REFERENCES `tb_user`(`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='직원 정보: tb_user와 1:1 관계로 직원의 기본 정보를 관리.';

-- tb_cert (자격증 마스터)
CREATE TABLE `tb_cert` (
  `cert_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '자격증 ID (PK)',
  `cert_code` VARCHAR(50) NOT NULL COMMENT '자격 코드 (유일)',
  `cert_name` VARCHAR(100) NOT NULL COMMENT '자격 명칭',
  `cert_description` VARCHAR(255) NULL COMMENT '설명',
  `cert_valid_period_months` INT NULL COMMENT '유효기간 (월 단위, NULL=무기한)',
  `is_deleted` TINYINT DEFAULT 0,
  `deleted_at` DATETIME NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cert_id`),
  UNIQUE KEY `uk_cert_code` (`cert_code`)
) ENGINE=InnoDB COMMENT='자격 마스터: 설비/공정 필요 자격 정의';

-- tb_employee_cert (직원별 자격증 보유 현황)
CREATE TABLE `tb_employee_cert` (
  `employee_cert_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '직원-자격증 매핑 ID (PK)',
  `employee_id` VARCHAR(36) NOT NULL COMMENT '직원 ID (FK→tb_employee.employee_id)',
  `cert_id` BIGINT NOT NULL COMMENT '자격증 ID (FK→tb_cert.cert_id)',
  `employee_level_code_id` BIGINT NOT NULL COMMENT '숙련도 코드 ID (FK→tb_code, 예: BASIC, INTERMEDIATE)',
  `cert_obtained_date` DATE NOT NULL COMMENT '취득일',
  `cert_expiry_date` DATE NULL COMMENT '만료일 (NULL=무기한)',
  `is_deleted` TINYINT DEFAULT 0,
  `deleted_at` DATETIME NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`employee_cert_id`),
  UNIQUE KEY `uk_employee_cert` (`employee_id`, `cert_id`),
  CONSTRAINT `fk_employee_cert_emp` FOREIGN KEY (`employee_id`) REFERENCES `tb_employee`(`employee_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_employee_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert`(`cert_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_employee_cert_level` FOREIGN KEY (`employee_level_code_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='직원-자격증 매핑: 직원별 자격증/숙련도 관리';

-- 공정 자격
CREATE TABLE `tb_process_cert` (
  `process_cert_id` BIGINT NOT NULL AUTO_INCREMENT,
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK)',
  `cert_id` BIGINT NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
  PRIMARY KEY (`process_cert_id`),
  UNIQUE KEY `uk_proc_cert` (`process_id`, `cert_id`),
  CONSTRAINT `fk_proc_cert_proc` FOREIGN KEY (`process_id`) REFERENCES `tb_process`(`process_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_proc_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert`(`cert_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='공정별 필요 자격 정의';

-- 설비 자격
CREATE TABLE `tb_equipment_cert` (
  `equipment_cert_id` BIGINT NOT NULL AUTO_INCREMENT,
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  `cert_id` BIGINT NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
  PRIMARY KEY (`equipment_cert_id`),
  UNIQUE KEY `uk_eqp_cert` (`equipment_id`, `cert_id`),
  CONSTRAINT `fk_eqp_cert_eqp` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_eqp_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert`(`cert_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='설비별 필요 자격 정의';

SET @cert_FORKLIFT = (SELECT cert_id FROM tb_cert WHERE cert_code = 'FORKLIFT');
SET @cert_ELECTRIC = (SELECT cert_id FROM tb_cert WHERE cert_code = 'ELECTRIC');
SET @cert_SAFETY = (SELECT cert_id FROM tb_cert WHERE cert_code = 'SAFETY');
SET @SKILL_BASIC = (SELECT code_id FROM tb_code WHERE group_code = 'EMPLOYEE_LEVEL' AND code = 'BASIC');
SET @SKILL_INTERMEDIATE = (SELECT code_id FROM tb_code WHERE group_code = 'EMPLOYEE_LEVEL' AND code = 'INTERMEDIATE');
SET @SKILL_EXPERT = (SELECT code_id FROM tb_code WHERE group_code = 'EMPLOYEE_LEVEL' AND code = 'EXPERT');

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

 ALTER TABLE tb_shift_calendar DROP CHECK ck_shiftcal_scope_exclusive;
 ALTER TABLE tb_shift_assignment DROP CHECK ck_assign_scope_exclusive;

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

/* 2) tb_user_cert: 추가 직원별 자격증 보유 현황 추가 */
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
