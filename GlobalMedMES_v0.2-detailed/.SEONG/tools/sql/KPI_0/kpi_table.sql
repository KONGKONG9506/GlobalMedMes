-- 2025-08-27
-- 새 테이블

CREATE TABLE `tb_definition` (
    `definition_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '수식 ID (PK)',
    `definition_name` VARCHAR(100) NOT NULL COMMENT '수식 이름 (예: OEE, Yield, Defect Rate)',
    `description` VARCHAR(255) COMMENT '설명',
    `formula` VARCHAR(500) NOT NULL COMMENT '계산 수식 (예: "(good_qty / total_qty) * 100")',
    `parameters` JSON NOT NULL COMMENT '파라미터 정의 : 사용되는 파라미터 정의 (예: ["good_qty", "total_qty"])',
    `unit` VARCHAR(20) COMMENT '단위 (% , 개, 시간 등)',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
    `deleted_at` DATETIME NULL COMMENT 'UTC',
    `created_by` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
    `modified_by` VARCHAR(50) NULL,
    `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
    PRIMARY KEY (`definition_id`)
) ENGINE=InnoDB COMMENT='계산 수식 정의 테이블';

CREATE TABLE `tb_production_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '생산 로그 ID (PK)',
  `work_order_id` VARCHAR(36) NOT NULL COMMENT '작업 지시 ID (FK)',
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK)',
  `event_type` BIGINT NOT NULL COMMENT '이벤트 유형 (FK → tb_code)',
  `event_timestamp` DATETIME NOT NULL COMMENT '이벤트 발생 시점 (UTC)',
  `event_value` DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '이벤트 관련 값 (수량, 시간 등)',
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '레코드 생성 시점 (UTC)',
  PRIMARY KEY (`log_id`),
  UNIQUE KEY `uk_prodlog_unique` (`work_order_id`, `equipment_id`, `process_id`, `event_timestamp`, `event_type`),
  KEY `idx_log_event_time` (`event_timestamp`),
  KEY `idx_log_wo_id` (`work_order_id`),
  KEY `idx_log_eqp_id` (`equipment_id`),
  CONSTRAINT `fk_prodlog_event_type` FOREIGN KEY (`event_type`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT,
  CONSTRAINT `ck_prodlog_value_nonneg` CHECK (`event_value` >= 0),
  CONSTRAINT `ck_prodlog_time_order` CHECK (`event_timestamp` IS NOT NULL)
) ENGINE=InnoDB COMMENT='생산 공정에서 발생하는 원시 이벤트 로그';
-- 코드 연결, 이벤트 값 음수 금지, 이벤트 시간 null 금지

CREATE TABLE `tb_kpi_data` (
  `kpi_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'KPI 기록 ID (PK)',
  `kpi_date` DATE NOT NULL COMMENT 'KPI 기준 날짜',
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK → tb_equipment)',
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK → tb_process)',
  `item_id` VARCHAR(36) NOT NULL COMMENT '품목 ID (FK → tb_item)',

  `actual_oee` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '설비 종합 효율 (%) [0~100]',
  `actual_productivity` DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '생산성 (단위/시간)',
  `actual_yield` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '수율 (%) [0~100]',
  
  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  
  PRIMARY KEY (`kpi_id`),
  UNIQUE KEY `uk_kpi_date_eqp_proc_item` (`kpi_date`, `equipment_id`, `process_id`, `item_id`),
  KEY `idx_kpi_date` (`kpi_date`),
  KEY `idx_kpi_equipment_id` (`equipment_id`),
  KEY `idx_kpi_process_id` (`process_id`),
  KEY `idx_kpi_item_id` (`item_id`),

  CONSTRAINT `fk_kpi_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_process`   FOREIGN KEY (`process_id`)   REFERENCES `tb_process`(`process_id`)   ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_item`      FOREIGN KEY (`item_id`)      REFERENCES `tb_item`(`item_id`) ON DELETE RESTRICT,

  CONSTRAINT `ck_actual_oee_range`   CHECK (`actual_oee` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_yield_range` CHECK (`actual_yield` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_productivity_nonneg` CHECK (`actual_productivity` >= 0)
) ENGINE=InnoDB COMMENT='KPI 데이터: KPI 계산 결과를 일정한 주기로 저장하여 보관';
