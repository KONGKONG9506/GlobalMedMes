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
-- 2. tb_kpi_data INSERT (퍼포먼스 기반 계산)
-- ===============================
INSERT INTO tb_kpi_data (
    kpi_date, equipment_id, process_id, item_id, work_order_id,
    actual_oee, actual_yield, actual_defect_rate, actual_productivity,
    aggregation_type_id, start_time, end_time, calc_status_code_id, created_by
)
VALUES
(
    CURRENT_DATE(), 'CNC-001', 'P-100', 'FG-FIX-4010S-001', 'KPI-WO-0001',
    ROUND(LEAST((@good_qty_1/@produced_qty_1) * (@run_time_1/@planned_time) * @hundred, 100), 2),  -- OEE
    ROUND(@good_qty_1/@produced_qty_1*@hundred, 2),                                                 -- 수율
    ROUND(@defect_qty_1/@produced_qty_1*@hundred, 2),                                               -- 불량률
    ROUND(@good_qty_1/(@run_time_1/60), 4),                                                        -- 생산성
    @EVT_REALTIME, @start_time_1, @end_time_1, @EVT_SUCCESS, 'seed'
),
(
    CURRENT_DATE(), 'CNC-002', 'P-100', 'FG-FIX-4010S-001', 'KPI-WO-0002',
    ROUND(LEAST((@good_qty_2/@produced_qty_2) * (@run_time_2/@planned_time) * @hundred, 100), 2),
    ROUND(@good_qty_2/@produced_qty_2*@hundred, 2),
    ROUND(@defect_qty_2/@produced_qty_2*@hundred, 2),
    ROUND(@good_qty_2/(@run_time_2/60), 4),
    @EVT_REALTIME, @start_time_2, @end_time_2, @EVT_SUCCESS, 'seed'
)
ON DUPLICATE KEY UPDATE
    actual_oee = VALUES(actual_oee),
    actual_yield = VALUES(actual_yield),
    actual_defect_rate = VALUES(actual_defect_rate),
    actual_productivity = VALUES(actual_productivity),
    start_time = VALUES(start_time),
    end_time = VALUES(end_time),
    calc_status_code_id = VALUES(calc_status_code_id),
    created_by = VALUES(created_by);

