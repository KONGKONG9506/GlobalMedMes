-- 09-27
-- start_date = 시작일
-- LIMIT = 원하는 기간(7일, 30일, 100일)

SET @EVT_DAILY_BATCH  := (SELECT code_id FROM tb_code WHERE group_code='KPI_DATA_TYPE' AND code='DAILY_BATCH');
SET @EVT_SUCCESS  := (SELECT code_id FROM tb_code WHERE group_code='KPI_CALC_STATUS' AND code='SUCCESS');
SET @start_date := '2025-08-01';


-- CNC-001, P-100, FG-FIX-4010S-001 
INSERT INTO tb_kpi_data (
    kpi_date, equipment_id, process_id, item_id, 
    actual_oee, actual_yield, actual_defect_rate, actual_productivity, 
    aggregation_type_id, batch_group_key, start_time, end_time, 
    calc_status_code_id, created_by
)
SELECT 
    DATE_ADD(@start_date, INTERVAL seq DAY) AS kpi_date, 
    'CNC-001', 'P-100', 'FG-FIX-4010S-001',

    -- OEE: 수율보다 크지 않도록 (수율 * 0.8 ~ 수율 * 1.0 범위에서 랜덤)
    ROUND(yield_val * (0.9 + RAND() * 0.1), 2) AS actual_oee,

    -- 수율
    yield_val AS actual_yield,

    -- 불량률 = 100 - 수율
    ROUND(100 - yield_val, 2) AS actual_defect_rate,

    -- 생산성: 90 ~ 120
    ROUND(90 + RAND() * 30, 2) AS actual_productivity,

    @EVT_DAILY_BATCH,
    'DAILY',
    CONCAT(DATE_ADD(@start_date, INTERVAL seq DAY), ' 00:00:00'),
    CONCAT(DATE_ADD(@start_date, INTERVAL seq +1 DAY), ' 00:00:00'),
    @EVT_SUCCESS,
    'test'
FROM (
    SELECT 
        @rownum := @rownum + 1 AS seq,
        ROUND(95 + RAND() * 5, 2) AS yield_val  -- 수율: 95~100
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT @rownum := -1) r
    LIMIT 31
) d;

-- CNC-002, P-100, FG-FIX-4010S-001
INSERT INTO tb_kpi_data (
    kpi_date, equipment_id, process_id, item_id, 
    actual_oee, actual_yield, actual_defect_rate, actual_productivity, 
    aggregation_type_id, batch_group_key, start_time, end_time, 
    calc_status_code_id, created_by
)
SELECT 
    DATE_ADD(@start_date, INTERVAL seq DAY) AS kpi_date, 
    'CNC-002', 'P-100', 'FG-FIX-4010S-001',

    -- OEE: 수율보다 크지 않도록 (수율 * 0.8 ~ 수율 * 1.0 범위에서 랜덤)
    ROUND(yield_val * (0.9 + RAND() * 0.1), 2) AS actual_oee,

    -- 수율
    yield_val AS actual_yield,

    -- 불량률 = 100 - 수율
    ROUND(100 - yield_val, 2) AS actual_defect_rate,

    -- 생산성: 90 ~ 120
    ROUND(90 + RAND() * 30, 2) AS actual_productivity,

    @EVT_DAILY_BATCH,
    'DAILY',
    CONCAT(DATE_ADD(@start_date, INTERVAL seq DAY), ' 00:00:00'),
    CONCAT(DATE_ADD(@start_date, INTERVAL seq +1 DAY), ' 00:00:00'),
    @EVT_SUCCESS,
    'test'
FROM (
    SELECT 
        @rownum := @rownum + 1 AS seq,
        ROUND(95 + RAND() * 5, 2) AS yield_val  -- 수율: 95~100
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT @rownum := -1) r
    LIMIT 31
) d;