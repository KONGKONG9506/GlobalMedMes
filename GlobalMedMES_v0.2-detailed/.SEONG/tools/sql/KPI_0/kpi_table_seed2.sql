TRUNCATE TABLE tb_kpi_data;
-- 실시간 KPI (WORK ORDER 단위)
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, work_order_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, start_time, end_time, calc_success_check, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 'WO-0001', 85.00, 98.50, 1.50, 120.0000, 'REALTIME', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 10:00:00'), 1, 'seed'),
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0001', 'WO-0002', 82.00, 97.00, 3.50, 110.0000, 'REALTIME', CONCAT(CURRENT_DATE(),' 08:10:00'), CONCAT(CURRENT_DATE(),' 10:20:00'), 1, 'seed');

-- 배치 KPI (DAILY_TOTAL 단위)
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, batch_check, start_time, end_time, calc_success_check, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 83.00, 98.00, 2.00, 118.0000, 'DAILY_BATCH', 'DAILY', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 18:00:00'), 1, 'seed'),
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0001', 80.00, 97.00, 3.00, 112.0000, 'DAILY_BATCH', 'DAILY', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 18:00:00'), 1, 'seed');
