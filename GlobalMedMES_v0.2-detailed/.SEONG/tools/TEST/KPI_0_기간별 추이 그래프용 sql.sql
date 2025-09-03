-- 이 스크립트는 지난 50일간의 DAILY_BATCH KPI 데이터를 시뮬레이션하여 삽입합니다.
-- 프론트엔드에서 기간별 추이 그래프를 테스트하는 데 유용합니다.

-- 기존 데이터를 삭제하여 중복 에러를 방지합니다.
TRUNCATE TABLE tb_kpi_data;

-- tb_kpi_data 테이블에 시뮬레이션 데이터를 삽입합니다.
-- 각 KPI 값은 약간의 랜덤 변동을 주어 실제 데이터와 유사하게 만들었습니다.
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, batch_check, start_time, end_time, calc_success_check, created_by)
WITH RECURSIVE dates AS (
    SELECT CURRENT_DATE() - INTERVAL 50 DAY AS kpi_date
    UNION ALL
    SELECT kpi_date + INTERVAL 1 DAY FROM dates WHERE kpi_date < CURRENT_DATE()
),
combinations AS (
    SELECT 'E-0001' AS equipment_id, 'P-0001' AS process_id, 'I-0001' AS item_id
    UNION ALL SELECT 'E-0002', 'P-0002', 'I-0002'
    UNION ALL SELECT 'E-0001', 'P-0002', 'I-0002'
    UNION ALL SELECT 'E-0002', 'P-0001', 'I-0001'
)
SELECT
    d.kpi_date,
    c.equipment_id,
    c.process_id,
    c.item_id,
    (75 + RAND() * 10) AS actual_oee,
    (92 + RAND() * 5) AS actual_yield,
    (2 + RAND() * 4) AS actual_defect_rate,
    (90 + RAND() * 15) AS actual_productivity,
    'DAILY_BATCH' AS aggregation_type,
    'DAILY' AS batch_check,
    CONCAT(d.kpi_date, ' 08:00:00') AS start_time,
    CONCAT(d.kpi_date, ' 18:00:00') AS end_time,
    1 AS calc_success_check,
    'seed_script' AS created_by
FROM dates d
CROSS JOIN combinations c;
