
SET SQL_SAFE_UPDATES=0;

-- 1) 데이터/테이블 삭제 (FK 순서)
DELETE FROM tb_cmms_work_order_log;
DELETE FROM tb_cmms_fault_log;
DELETE FROM tb_cmms_work_order;
DELETE FROM tb_cmms_pm_plan;

DROP TABLE IF EXISTS tb_cmms_work_order_log;
DROP TABLE IF EXISTS tb_cmms_fault_log;
DROP TABLE IF EXISTS tb_cmms_work_order;
DROP TABLE IF EXISTS tb_cmms_pm_plan;

-- 2) 메뉴/RBAC 정리
DELETE rm FROM tb_role_menu rm
JOIN tb_menu m ON m.menu_id = rm.menu_id
WHERE m.menu_code IN ('CMMS','CMMS_WO','CMMS_PM','CMMS_FAULT');

DELETE FROM tb_menu WHERE menu_code IN ('CMMS_WO','CMMS_PM','CMMS_FAULT');
DELETE FROM tb_menu WHERE menu_code = 'CMMS';

-- 3) 코드 정리 (CMMS 관련만)
DELETE FROM tb_code
WHERE group_code IN ('LOSS_CATEGORY','CYCLE_TYPE','CMMS_WO_STATUS','WO_PRIORITY');

DELETE FROM tb_code_group
WHERE group_code IN ('LOSS_CATEGORY','CYCLE_TYPE','CMMS_WO_STATUS','WO_PRIORITY');
