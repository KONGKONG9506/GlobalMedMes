-- 2025-09-17

SET @sql := IF(@col_exists = 0,
               'ALTER TABLE tb_cmms_pm_plan ADD COLUMN estimated_take_time INT NULL COMMENT ''예상 소요 시간(분)''',
               'SELECT ''Column already exists''');

SET @EQP := 'E-0001';
PREPARE stmt FROM @sql;
EXECUTE stmt;
DEALLOCATE PREPARE stmt;

-- 2) 멱등 INSERT 또는 UPDATE
INSERT INTO tb_cmms_pm_plan
(equipment_id, task_name, cycle_type_code_id, cycle_value, estimated_take_time, last_done_at, next_due_at, status, created_by)
SELECT @EQP, '윤활 점검(주1회)', @CYCLE_D, 7, 30, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 3 DAY), 'ACTIVE', 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='윤활 점검(주1회)'
);
INSERT INTO tb_cmms_pm_plan
(equipment_id, task_name, cycle_type_code_id, cycle_value, estimated_take_time, last_done_at, next_due_at, status, created_by)
SELECT @EQP, '모터 베어링 교체(500h)', @CYCLE_H, 500, 120, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 10 DAY), 'ACTIVE', 'seed'
WHERE NOT EXISTS (
  SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='모터 베어링 교체(500h)'
);
UPDATE tb_cmms_pm_plan
SET estimated_take_time = 30
WHERE equipment_id=@EQP AND task_name='윤활 점검(주1회)';

UPDATE tb_cmms_pm_plan
SET estimated_take_time = 120
WHERE equipment_id=@EQP AND task_name='모터 베어링 교체(500h)';


-- 새로 인서트
-- ALTER TABLE tb_cmms_pm_plan
-- ADD COLUMN estimated_take_time INT NULL COMMENT '예상 소요 시간(분)';

-- /* 2) PM 계획 (equipment_id + task_name 멱등) */
-- INSERT INTO tb_cmms_pm_plan
-- (equipment_id, task_name, cycle_type_code_id, cycle_value, estimated_take_time, last_done_at, next_due_at, status, created_by)
-- SELECT @EQP, '윤활 점검(주1회)', @CYCLE_D, 7, 30, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 3 DAY), 'ACTIVE', 'seed'
-- WHERE NOT EXISTS (
--   SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='윤활 점검(주1회)'
-- );

-- INSERT INTO tb_cmms_pm_plan
-- (equipment_id, task_name, cycle_type_code_id, cycle_value, estimated_take_time, last_done_at, next_due_at, status, created_by)
-- SELECT @EQP, '모터 베어링 교체(500h)', @CYCLE_H, 500, 120, NULL, DATE_ADD(UTC_TIMESTAMP(), INTERVAL 10 DAY), 'ACTIVE', 'seed'
-- WHERE NOT EXISTS (
--   SELECT 1 FROM tb_cmms_pm_plan WHERE equipment_id=@EQP AND task_name='모터 베어링 교체(500h)'
-- );

-- 기존 업데이트
-- ALTER TABLE tb_cmms_pm_plan
-- ADD COLUMN estimated_take_time INT NULL COMMENT '예상 소요 시간(분)';

-- SET @EQP := 'E-0001';
--  -- 윤활 점검(주1회) 예정 소요 시간 업데이트
-- UPDATE tb_cmms_pm_plan
-- SET estimated_take_time = 30
-- WHERE equipment_id = @EQP
--   AND task_name = '윤활 점검(주1회)';

-- -- 모터 베어링 교체(500h) 예정 소요 시간 업데이트
-- UPDATE tb_cmms_pm_plan
-- SET estimated_take_time = 120
-- WHERE equipment_id = @EQP
--   AND task_name = '모터 베어링 교체(500h)';