
/* 1) 시드 데이터 삭제 (FK 순서 고려) */
TRUNCATE TABLE tb_employee_cert;
TRUNCATE TABLE tb_process_cert;
TRUNCATE TABLE tb_equipment_cert;
TRUNCATE TABLE tb_cert;
TRUNCATE TABLE tb_employee;

-- 코드 그룹 및 코드 시드 제거
DELETE FROM tb_code WHERE group_code = 'SKILL_LEVEL';
DELETE FROM tb_code_group WHERE group_code = 'SKILL_LEVEL';

/* 2) 테이블 삭제 (자식 → 부모 순서) */
DROP TABLE IF EXISTS tb_employee_cert;
DROP TABLE IF EXISTS tb_process_cert;
DROP TABLE IF EXISTS tb_equipment_cert;
DROP TABLE IF EXISTS tb_cert;
DROP TABLE IF EXISTS tb_employee;
