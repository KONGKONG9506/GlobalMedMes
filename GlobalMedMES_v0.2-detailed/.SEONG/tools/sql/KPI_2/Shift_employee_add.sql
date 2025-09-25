-- ==========================================
-- GlobalMed MES • 교대 시스템 시드 데이터 확장
-- 목적: 교대 시스템 테스트를 위해 더 많은 직원을 추가하는 스크립트.
-- ==========================================

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