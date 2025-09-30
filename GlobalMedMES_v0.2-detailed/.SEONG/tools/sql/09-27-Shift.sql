INSERT INTO tb_menu (menu_code, menu_name, path, sort_order, created_by) VALUES
('SHIFT','시프트 관리','/shift',6,'seed')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), sort_order=VALUES(sort_order);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'SHIFT_CALENDAR','시프트 달력','/shift/calendar', p.menu_id, 1,'seed'
FROM tb_menu p WHERE p.menu_code='SHIFT'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'SHIFT_ASSIGNMENT','시프트 배정','/shift/assignment', p.menu_id, 2,'seed'
FROM tb_menu p WHERE p.menu_code='SHIFT'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

-- 2. ROLE_OP (작업자) 권한 추가
-- OP: 시프트 관리(SHIFT)에 대해 읽기(1) 및 쓰기(1) 권한을 부여합니다.
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('SHIFT_CALENDAR', 'SHIFT_ASSIGNMENT')
WHERE r.role_code='ROLE_OP'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- 3. ROLE_QA (품질 관리자) 권한 추가
-- QA: 시프트 관리(SHIFT)에 대해 읽기(1) 권한만 부여합니다.
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('SHIFT_CALENDAR', 'SHIFT_ASSIGNMENT')
WHERE r.role_code='ROLE_QA'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- 4. ROLE_VIEWER (뷰어) 권한 추가
-- VIEWER: 시프트 관리(SHIFT)에 대해 읽기(1) 권한만 부여합니다.
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('SHIFT_CALENDAR', 'SHIFT_ASSIGNMENT')
WHERE r.role_code='ROLE_VIEWER'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 1, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('SHIFT','SHIFT_CALENDAR', 'SHIFT_ASSIGNMENT')
WHERE r.role_code='ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id);
