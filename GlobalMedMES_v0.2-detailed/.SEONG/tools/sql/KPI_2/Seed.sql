-- ==========================================
-- GlobalMed MES • 인원 및 자격관리 시드 데이터
-- 목적: 직원, 자격증, 숙련도 등 인력 관련 정보를 채우는 스크립트.
-- ==========================================

SET NAMES utf8mb4;
SET time_zone = '+00:00';

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
SET @SKILL_BASIC     := (SELECT code_id FROM tb_code WHERE group_code='SKILL_LEVEL' AND code='BASIC');
SET @SKILL_INTERMEDIATE := (SELECT code_id FROM tb_code WHERE group_code='SKILL_LEVEL' AND code='INTERMEDIATE');
SET @SKILL_EXPERT     := (SELECT code_id FROM tb_code WHERE group_code='SKILL_LEVEL' AND code='EXPERT');

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
('1910100','산업안전기사','', null, 'seed');

/* 6) 자격증 id 변수 설정 */
-- 새로 추가된 자격증의 ID를 변수에 저장합니다.
SET @cert_FORKLIFT   := (SELECT cert_id FROM tb_cert WHERE cert_code='C-FORKLIFT');
SET @cert_ELECTRIC   := (SELECT cert_id FROM tb_cert WHERE cert_code='0410100');
SET @cert_SAFETY     := (SELECT cert_id FROM tb_cert WHERE cert_code='1910100');

/* 7) tb_user_cert: 직원별 자격증 보유 현황 추가 */
-- 각 직원이 취득한 자격증과 숙련도를 매핑합니다. 만료일은 유효기간을 기준으로 계산됩니다.
-- 중복된 데이터가 삽입되지 않도록 `INSERT IGNORE`를 사용합니다.
INSERT IGNORE INTO tb_user_cert (employee_id, cert_id, user_level_code_id, cert_obtained_date, cert_expiry_date, created_by) VALUES
-- 운영자(EMP-OP)는 지게차와 안전기사, 전기기사 자격증 보유
('00000000-0000-0000-0000-0000000000OP', @cert_FORKLIFT, @SKILL_EXPERT, '2023-01-15', null, 'seed'),
('00000000-0000-0000-0000-0000000000OP', @cert_SAFETY,    @SKILL_INTERMEDIATE, '2024-03-20', null, 'seed'),
('00000000-0000-0000-0000-0000000000OP', @cert_ELECTRIC, @SKILL_EXPERT, '2022-10-25', null, 'seed'),
-- 품질관리자(EMP-QA)는 안전기사 자격증 보유
('00000000-0000-0000-0000-0000000000QA', @cert_SAFETY,    @SKILL_EXPERT, '2024-01-10', null, 'seed'),
-- 관리자(EMP-ADMIN)는 지게차 교육, 안전기사, 전기기사 자격증 보유
('00000000-0000-0000-0000-0000000000AD', @cert_FORKLIFT, @SKILL_BASIC, '2023-05-01', null, 'seed'),
('00000000-0000-0000-0000-0000000000AD', @cert_SAFETY,    @SKILL_BASIC, '2024-02-14', null, 'seed'),
('00000000-0000-0000-0000-0000000000AD', @cert_ELECTRIC, @SKILL_BASIC, '2022-10-25', null, 'seed');
