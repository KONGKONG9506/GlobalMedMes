-- 2025-09-13
INSERT INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES
('KPI_DATA_TYPE', 'KPI 집계 유형', 'KPI 데이터의 집계 기준을 정의합니다.', 'seed');

-- 코드: 집계 유형
INSERT INTO `tb_code` (`group_code`, `code`, `name`, `sort_order`, `created_by`)
VALUES
('KPI_DATA_TYPE', 'REALTIME', '실시간', 1, 'seed'),
('KPI_DATA_TYPE', 'DAILY_BATCH', '일일 배치', 2, 'seed'),
('KPI_DATA_TYPE', 'MONTHLY_BATCH', '월별 배치', 3, 'seed'),
('KPI_DATA_TYPE', 'YEARLY_BATCH', '연도별 배치', 4, 'seed');

-- 코드 그룹: 계산 상태
INSERT INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES
('KPI_CALC_STATUS', 'KPI 계산 상태', 'KPI 계산 작업의 상태를 정의합니다.', 'seed');

-- 코드: 계산 상태
INSERT INTO `tb_code` (`group_code`, `code`, `name`, `sort_order`, `created_by`)
VALUES
('KPI_CALC_STATUS', 'SUCCESS', '성공', 1, 'seed'),
('KPI_CALC_STATUS', 'FAIL', '실패', 2, 'seed'),
('KPI_CALC_STATUS', 'IN_PROGRESS', '진행 중', 3, 'seed'),
('KPI_CALC_STATUS', 'RETRY', '재시도', 4, 'seed');


--
-- 마이그레이션 스크립트: `tb_kpi_data` 테이블 스키마 변경
--
-- 목적:
-- 1. `VARCHAR` 및 `TINYINT` 타입 컬럼을 `tb_code.code_id`를 참조하는 `BIGINT` 타입으로 변경.
-- 2. 컬럼명 `batch_check`를 `batch_group_key`로 변경.
-- 3. 기존 데이터를 안전하게 마이그레이션하고 새로운 제약 조건 추가.
--

-- 마이그레이션 중 FK 제약 조건 체크를 임시로 비활성화하여 충돌을 방지합니다.
SET FOREIGN_KEY_CHECKS = 0;

-- 기존 UNIQUE KEY 제약 조건을 삭제합니다.
-- 변경되는 컬럼명을 포함하고 있어 재설정해야 합니다.
ALTER TABLE `tb_kpi_data`
  DROP KEY `uk_kpi_realtime`,
  DROP KEY `uk_kpi_daily`;

-- 1단계: 컬럼명 변경 및 신규 BIGINT 컬럼 추가
-- `batch_check`를 `batch_group_key`로 변경합니다.
ALTER TABLE `tb_kpi_data`
  CHANGE COLUMN `batch_check` `batch_group_key` VARCHAR(20) COMMENT '주기적 집계시 중복 체크용';

-- `actual_defect_rate` 뒤에 `aggregation_type_id`를 추가합니다.
-- `end_time` 뒤에 `calc_status_code_id`를 추가합니다.
ALTER TABLE `tb_kpi_data`
  ADD COLUMN `aggregation_type_id` BIGINT COMMENT '집계 유형 (FK → tb_code.code_id where group_code = "KPI_DATA_TYPE")' AFTER `actual_defect_rate`,
  ADD COLUMN `calc_status_code_id` BIGINT COMMENT '계산 상태 (FK → tb_code.code_id where group_code = "KPI_CALC_STATUS")' AFTER `end_time`;


-- 2단계: 기존 데이터를 신규 컬럼으로 마이그레이션
-- `aggregation_type` 컬럼의 값을 기준으로 `tb_code` 테이블에서 `code_id`를 조회하여 `aggregation_type_id`에 삽입합니다.
SET SQL_SAFE_UPDATES = 0;

UPDATE `tb_kpi_data` AS `t1`
JOIN `tb_code` AS `t2`
  ON `t1`.`aggregation_type` = `t2`.`code`
  AND `t2`.`group_code` = 'KPI_DATA_TYPE'
SET `t1`.`aggregation_type_id` = `t2`.`code_id`;

-- `calc_success_check` TINYINT 값을 기준으로 `tb_code` 테이블에서 `code_id`를 조회하여 `calc_status_code_id`에 삽입합니다.
-- CASE 문을 사용하여 TINYINT 값을 `tb_code.code`와 매핑합니다.
UPDATE `tb_kpi_data` AS `t1`
JOIN `tb_code` AS `t2`
  ON `t2`.`code` = (
    CASE `t1`.`calc_success_check`
      WHEN 1 THEN 'SUCCESS'
      WHEN 0 THEN 'FAIL'
      WHEN 2 THEN 'IN_PROGRESS'
      WHEN 3 THEN 'RETRY'
      ELSE NULL
    END
  ) AND `t2`.`group_code` = 'KPI_CALC_STATUS'
SET `t1`.`calc_status_code_id` = `t2`.`code_id`;


SET SQL_SAFE_UPDATES = 1; -- 업데이트 완료 후 안전 모드 다시 활성화


-- 3단계: 기존 컬럼 삭제
ALTER TABLE `tb_kpi_data`
  DROP COLUMN `aggregation_type`,
  DROP COLUMN `calc_success_check`;


-- 4단계: 신규 제약 조건 추가
-- 새로운 컬럼에 UNIQUE KEY를 재설정합니다.
ALTER TABLE `tb_kpi_data`
  ADD UNIQUE KEY `uk_kpi_realtime` (`kpi_date`, `work_order_id`, `equipment_id`, `process_id`, `item_id`, `aggregation_type_id`),
  ADD UNIQUE KEY `uk_kpi_daily` (`kpi_date`, `equipment_id`, `process_id`, `item_id`, `batch_group_key`, `aggregation_type_id`);

-- FOREIGN KEY 제약 조건을 추가합니다.
ALTER TABLE `tb_kpi_data`
  ADD CONSTRAINT `fk_kpi_data_type` FOREIGN KEY (`aggregation_type_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT,
  ADD CONSTRAINT `fk_kpi_calc_status` FOREIGN KEY (`calc_status_code_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT;

-- 마이그레이션 완료 후 FK 제약 조건 체크를 다시 활성화합니다.
SET FOREIGN_KEY_CHECKS = 1;

