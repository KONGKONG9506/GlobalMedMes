-- CNC-001, P-100, FG-FIX-4010S-001 → 과거 100일치
INSERT INTO tb_kpi_data (
    kpi_date, equipment_id, process_id, item_id, 
    actual_oee, actual_yield, actual_defect_rate, actual_productivity, 
    aggregation_type_id, batch_group_key, start_time, end_time, 
    calc_status_code_id, created_by
)
SELECT 
    DATE_SUB(CURRENT_DATE(), INTERVAL (99 - seq) DAY) AS kpi_date,
    'CNC-001', 'P-100', 'FG-FIX-4010S-001',
    ROUND(80 + RAND()*10, 2),       -- OEE: 80~90
    ROUND(95 + RAND()*5, 2),        -- 수율: 95~100
    ROUND(RAND()*5, 2),             -- 불량률: 0~5%
    ROUND(100 + RAND()*30, 4),      -- 생산성: 100~130
    @EVT_DAILY_BATCH,
    'DAILY',
    CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL (100 - seq) DAY), ' 01:00:00'),
    CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL (99 - seq) DAY), ' 01:00:00'),
    @EVT_SUCCESS,
    'test'
FROM (
    SELECT @rownum := @rownum + 1 AS seq
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT @rownum := -1) r
    LIMIT 100
) d;

-- CNC-002, P-100, FG-FIX-4010S-001 → 과거 100일치
INSERT INTO tb_kpi_data (
    kpi_date, equipment_id, process_id, item_id, 
    actual_oee, actual_yield, actual_defect_rate, actual_productivity, 
    aggregation_type_id, batch_group_key, start_time, end_time, 
    calc_status_code_id, created_by
)
SELECT 
    DATE_SUB(CURRENT_DATE(), INTERVAL (99 - seq) DAY) AS kpi_date,
    'CNC-002', 'P-100', 'FG-FIX-4010S-001',
    ROUND(75 + RAND()*15, 2),       -- OEE: 75~90
    ROUND(94 + RAND()*6, 2),        -- 수율: 94~100
    ROUND(RAND()*6, 2),             -- 불량률: 0~6%
    ROUND(90 + RAND()*40, 4),       -- 생산성: 90~130
    @EVT_DAILY_BATCH,
    'DAILY',
    CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL (100 - seq) DAY), ' 01:00:00'),
    CONCAT(DATE_SUB(CURRENT_DATE(), INTERVAL (99 - seq) DAY), ' 01:00:00'),
    @EVT_SUCCESS,
    'test'
FROM (
    SELECT @rownum2 := @rownum2 + 1 AS seq
    FROM (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t1,
         (SELECT 0 UNION ALL SELECT 1 UNION ALL SELECT 2 UNION ALL SELECT 3 UNION ALL SELECT 4
          UNION ALL SELECT 5 UNION ALL SELECT 6 UNION ALL SELECT 7 UNION ALL SELECT 8 UNION ALL SELECT 9) t2,
         (SELECT @rownum2 := -1) r
    LIMIT 100
) d;
