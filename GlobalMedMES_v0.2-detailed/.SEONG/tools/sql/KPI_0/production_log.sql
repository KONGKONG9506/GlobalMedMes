-- 2025-09-12

CREATE TABLE `tb_production_log` (
  `log_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '생산 로그 ID (PK)',
  `work_order_id` VARCHAR(36) NULL COMMENT '작업 지시 ID (FK)',
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  `process_id` VARCHAR(36) NULL COMMENT '공정 ID (FK)',
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