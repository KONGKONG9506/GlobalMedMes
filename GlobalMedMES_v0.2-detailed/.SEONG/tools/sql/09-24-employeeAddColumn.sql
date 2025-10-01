INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('EMPLOYEE_STATUS', '직원 상태', 'system');

INSERT INTO tb_code (group_code, code, name, use_yn, created_by)
VALUES
('EMPLOYEE_STATUS', 'ACTIVE', '재직', 'Y', 'system'),
('EMPLOYEE_STATUS', 'LEAVE', '휴직', 'Y', 'system'),
('EMPLOYEE_STATUS', 'RESIGNED', '퇴사', 'Y', 'system');

ALTER TABLE `tb_employee`
ADD COLUMN `department_name` VARCHAR(100) NULL COMMENT '소속 부서',
ADD COLUMN `status_code_id` BIGINT NULL COMMENT '직원 상태 (FK)';

ALTER TABLE `tb_employee`
ADD CONSTRAINT `fk_employee_status_code` FOREIGN KEY (`status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT;

SELECT code_id FROM tb_code WHERE group_code = 'EMPLOYEE_STATUS' AND code = 'ACTIVE';

SET SQL_SAFE_UPDATES = 0;

UPDATE tb_employee
SET status_code_id = (
    SELECT code_id FROM tb_code WHERE group_code = 'EMPLOYEE_STATUS' AND code = 'ACTIVE'
);

UPDATE tb_employee
SET department_name = '운영팀'
WHERE employee_number = 'EMP-OP';

UPDATE tb_employee
SET department_name = '품질관리팀'
WHERE employee_number = 'EMP-QA';

UPDATE tb_employee
SET department_name = '경영지원팀'
WHERE employee_number = 'EMP-ADMIN';

SET SQL_SAFE_UPDATES = 1;

실적 Dto 변경
리퀘스트 파라미터 추가
