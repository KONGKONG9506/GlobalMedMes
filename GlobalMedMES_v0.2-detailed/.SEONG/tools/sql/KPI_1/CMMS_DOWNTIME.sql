-- 2025-09-18

SET @EQP := 'E-0001';

ALTER TABLE tb_cmms_pm_plan
ADD COLUMN estimated_take_time INT NULL COMMENT '예상 소요 시간(분)';

UPDATE tb_cmms_pm_plan
SET estimated_take_time = 30
WHERE equipment_id=@EQP AND task_name='윤활 점검(주1회)';

UPDATE tb_cmms_pm_plan
SET estimated_take_time = 120
WHERE equipment_id=@EQP AND task_name='모터 베어링 교체(500h)';
