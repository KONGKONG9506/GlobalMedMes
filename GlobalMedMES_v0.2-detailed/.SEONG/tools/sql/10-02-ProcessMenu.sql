-- 25-10-02
INSERT INTO tb_menu (menu_code, menu_name, path, sort_order, created_by) VALUES
('PROCESS','공정 관리','/process',6,'seed')
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), sort_order=VALUES(sort_order);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'PROCESS_DETAIL','공정 상세','/process/detail/*', p.menu_id, 1,'seed'
FROM tb_menu p WHERE p.menu_code='PROCESS'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'PROCESS_ADD','공정 생성','/process/new', p.menu_id, 2,'seed'
FROM tb_menu p WHERE p.menu_code='PROCESS'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

INSERT INTO tb_menu (menu_code, menu_name, path, parent_id, sort_order, created_by)
SELECT 'PROCESS_MODI','공정 수정','/process/modi', p.menu_id, 3,'seed'
FROM tb_menu p WHERE p.menu_code='PROCESS'
ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), path=VALUES(path), parent_id=VALUES(parent_id);

-- 2. ROLE_OP (작업자) 권한 추가
-- OP: 공정에 대해 읽기(1) 및 쓰기(1) 권한을 부여합니다.
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('PROCESS', 'PROCESS_DETAIL', 'PROCESS_ADD', 'PROCESS_MODI')
WHERE r.role_code='ROLE_OP'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- 3. ROLE_QA (품질 관리자) 권한 추가
-- QA: 공정에 대해 읽기(1) 권한만 부여합니다.
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('PROCESS', 'PROCESS_DETAIL')
WHERE r.role_code='ROLE_QA'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

-- 4. ROLE_VIEWER (뷰어) 권한 추가
-- VIEWER: 공정에 대해 읽기(1) 권한만 부여합니다.
INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 0, 0, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('PROCESS', 'PROCESS_DETAIL')
WHERE r.role_code='ROLE_VIEWER'
AND NOT EXISTS(SELECT 1 FROM tb_role_menu rm WHERE rm.role_id=r.role_id AND rm.menu_id=m.menu_id);

INSERT INTO tb_role_menu (role_id, menu_id, allow_read, allow_write, allow_exec, created_by)
SELECT r.role_id, m.menu_id, 1, 1, 1, 'seed'
FROM tb_role r JOIN tb_menu m ON m.menu_code IN ('PROCESS', 'PROCESS_DETAIL', 'PROCESS_ADD', 'PROCESS_MODI')
WHERE r.role_code='ROLE_ADMIN'
  AND NOT EXISTS (SELECT 1 FROM tb_role_menu x WHERE x.role_id=r.role_id AND x.menu_id=m.menu_id);