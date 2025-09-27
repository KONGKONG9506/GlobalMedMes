-- 09-27-16:19

SET @WO_R    := (SELECT code_id FROM tb_code WHERE group_code='WO_STATUS' AND code='R');
SET @EVT_SUCCESS  := (SELECT code_id FROM tb_code WHERE group_code='KPI_CALC_STATUS' AND code='SUCCESS');
SET @EVT_REALTIME  := (SELECT code_id FROM tb_code WHERE group_code='KPI_DATA_TYPE' AND code='REALTIME');


-- ===============================
-- 변수 설정
-- ===============================
SET @planned_time = 120; -- 계획 작업 시간 (분)
SET @hundred = 100;

-- 설비 1
SET @produced_qty_1 = FLOOR(180 + RAND()*60);                  
SET @defect_qty_1   = FLOOR(@produced_qty_1 * (0.03 + RAND()*0.03)); 
SET @good_qty_1     = @produced_qty_1 - @defect_qty_1;
SET @run_time_1     = 110 + FLOOR(RAND()*10);                      
SET @start_time_1   = CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), ' 09:00:00');
SET @end_time_1     = CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), ' 09:40:00');

-- 설비 2
SET @produced_qty_2 = FLOOR(200 + RAND()*40);
SET @defect_qty_2   = FLOOR(@produced_qty_2 * (0.02 + RAND()*0.04));
SET @good_qty_2     = @produced_qty_2 - @defect_qty_2;
SET @run_time_2     = 100 + FLOOR(RAND()*20);
SET @start_time_2   = CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), ' 10:00:00');
SET @end_time_2     = CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL 1 DAY), ' 10:50:00');

INSERT INTO tb_work_order (
    work_order_id, work_order_number, item_id, process_id, equipment_id,
    order_qty, produced_qty, status_code_id, created_by
)
VALUES
('KPI-WO-0001','KPI-WO-0001','FG-FIX-4010S-001','P-100','CNC-001', 50000, @produced_qty_1, @WO_R,'seed'),
('KPI-WO-0002','KPI-WO-0002','FG-FIX-4010S-001','P-100','CNC-002', 50000, @produced_qty_2, @WO_R,'seed')
ON DUPLICATE KEY UPDATE
    produced_qty = produced_qty + VALUES(produced_qty),
    status_code_id = VALUES(status_code_id);

-- ===============================
-- 1. tb_production_performance INSERT
-- ===============================
INSERT INTO tb_production_performance (
    work_order_id, item_id, process_id, equipment_id,
    produced_qty, defect_qty, start_time, end_time, created_by
)
VALUES
(
    'KPI-WO-0001', 'FG-FIX-4010S-001', 'P-100', 'CNC-001',
    @produced_qty_1, 
    @defect_qty_1,
    @start_time_1, 
    @end_time_1,
    'seed'
),
(
    'KPI-WO-0002', 'FG-FIX-4010S-001', 'P-100', 'CNC-002',
    @produced_qty_2, 
    @defect_qty_2,
    @start_time_2,
    @end_time_2,
    'seed'
)
ON DUPLICATE KEY UPDATE
    produced_qty = VALUES(produced_qty),
    defect_qty = VALUES(defect_qty),
    start_time = VALUES(start_time),
    end_time = VALUES(end_time),
    created_by = VALUES(created_by);

-- ===============================
-- 2. tb_kpi_data UPDATE (퍼포먼스 기반 계산)
-- ===============================

-- KPI-WO-0001 갱신
UPDATE tb_kpi_data
SET
    kpi_date = CURRENT_DATE(),
    actual_oee = ROUND(LEAST((@good_qty_1/@produced_qty_1) * (@run_time_1/@planned_time) * @hundred, 100), 2),
    actual_yield = ROUND(@good_qty_1/@produced_qty_1*@hundred, 2),
    actual_defect_rate = ROUND(@defect_qty_1/@produced_qty_1*@hundred, 2),
    actual_productivity = ROUND(@good_qty_1/(@run_time_1/60), 4),
    end_time = NOW(),
    calc_status_code_id = @EVT_SUCCESS,
    created_by = 'seed'
WHERE 
    work_order_id = 'KPI-WO-0001';


-- KPI-WO-0002 갱신
UPDATE tb_kpi_data
SET
    kpi_date = CURRENT_DATE(),
    actual_oee = ROUND(LEAST((@good_qty_2/@produced_qty_2) * (@run_time_2/@planned_time) * @hundred, 100), 2),
    actual_yield = ROUND(@good_qty_2/@produced_qty_2*@hundred, 2),
    actual_defect_rate = ROUND(@defect_qty_2/@produced_qty_2*@hundred, 2),
    actual_productivity = ROUND(@good_qty_2/(@run_time_2/60), 4),
    end_time = NOW(),
    calc_status_code_id = @EVT_SUCCESS,
    created_by = 'seed'
WHERE 
    work_order_id = 'KPI-WO-0002';