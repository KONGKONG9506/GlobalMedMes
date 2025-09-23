/* T17: 존재하지 않는 작업지시 ID로 KPI 데이터 삽입 */
START TRANSACTION;
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, work_order_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, start_time, end_time, calc_success_check, created_by)
VALUES
(CURRENT_DATE(), 'E-0001', 'P-0001', 'I-0001', 'WO-NON-EXIST', 85.00, 98.50, 1.50, 120.0000, 'REALTIME', CONCAT(CURRENT_DATE(),' 08:00:00'), CONCAT(CURRENT_DATE(),' 10:00:00'), 1, 'test');
-- 기대: 실패 (FOREIGN KEY 제약 조건 위반)
-- 결과: 실패 코드(1452(와래 키 제약 조건 위반))
ROLLBACK;

/* T18: KPI 데이터의 종료 시간이 시작 시간보다 빠른 경우 */
START TRANSACTION;
INSERT INTO tb_kpi_data (kpi_date, equipment_id, process_id, item_id, work_order_id, actual_oee, actual_yield, actual_defect_rate, actual_productivity, aggregation_type, start_time, end_time, calc_success_check, created_by)
VALUES
(CURRENT_DATE(), 'E-0002', 'P-0002', 'I-0001', 'WO-0001', 85.00, 98.50, 1.50, 120.0000, 'REALTIME', CONCAT(CURRENT_DATE(),' 10:00:00'), CONCAT(CURRENT_DATE(),' 08:00:00'), 1, 'test');
-- 기대: 실패 (논리적 CHECK 제약 조건 위반)
-- 결과: 실패 코드(1062(유니크 값 중복)) 이미 WO,E,P,I와 TYPE이 모두 동일한 코드로 인해 삽입 실패
-- 결과: 실패 코드(3819(CHECK 제약 조건 위반))
ROLLBACK;

/* T19: tb_definition에 파라미터와 수식이 불일치하는 데이터 삽입 */
START TRANSACTION;
INSERT INTO tb_definition (definition_name, description, formula, parameters, unit, created_by)
VALUES
('MyCustomKPI', '잘못된 파라미터 정의 테스트', '#produced_qty.divide(#bad_param,4,T(java.math.RoundingMode).HALF_UP)', JSON_ARRAY('produced_qty','run_time'), 'EA/Hour', 'test');
-- 기대: 성공 (DB 수준에서 검증 불가능), 하지만 애플리케이션에서 이 KPI 사용 시 런타임 오류 발생
-- 결과: 성공
ROLLBACK;

/* T20: tb_production_log에 존재하지 않는 event_type 삽입 */
START TRANSACTION;
INSERT INTO tb_production_log (work_order_id, equipment_id, process_id, event_type, event_timestamp, event_value) VALUES
('WO-0001','E-0001','P-0001', 999999999, CONCAT(CURRENT_DATE(),' 08:00:00'), 0);
-- 기대: 실패 (FOREIGN KEY 제약 조건 위반)
-- 결과: 실패 코드(1452)
ROLLBACK;

