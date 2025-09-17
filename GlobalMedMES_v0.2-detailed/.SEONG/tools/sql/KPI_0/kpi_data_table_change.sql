-- 2025-09-13
CREATE TABLE `tb_kpi_data` (
  `kpi_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT 'KPI 기록 ID (PK)',
  `kpi_date` DATE NOT NULL COMMENT 'KPI 기준 날짜',
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK → tb_equipment)',
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK → tb_process)',
  `item_id` VARCHAR(36) NOT NULL COMMENT '품목 ID (FK → tb_item)',
  `work_order_id` VARCHAR(36) NULL COMMENT '지시 ID (FK → tb_workorder)',
  
  `actual_oee` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '설비 종합 효율 (%) [0~100]',
  `actual_productivity` DECIMAL(10,4) NOT NULL DEFAULT 0 COMMENT '시간당 생산성 (단위/시간)',
  `actual_yield` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '수율 (%) [0~100]',
  `actual_defect_rate` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '불량률 (%) [0~100]',
  
  `aggregation_type_id` BIGINT NOT NULL COMMENT '집계 유형 (FK → tb_code.code where group_code = "KPI_DATA_TYPE")',
  `batch_group_key` VARCHAR(20) COMMENT '주기적 집계시 중복 체크용',
  `start_time` DATETIME NOT NULL COMMENT 'KPI 집계 시작 시간',
  `end_time` DATETIME NOT NULL COMMENT 'KPI 집계 종료 시간',
  `calc_status_code_id` BIGINT NOT NULL COMMENT '계산 상태 (FK → tb_code.code where group_code = "KPI_CALC_STATUS")',
  `calc_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'KPI 계산 시각',

  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  
  PRIMARY KEY (`kpi_id`),
  
  UNIQUE KEY `uk_kpi_realtime` (`kpi_date`, `work_order_id`, `equipment_id`, `process_id`, `item_id`, `aggregation_type_id`),
  UNIQUE KEY `uk_kpi_daily` (`kpi_date`, `equipment_id`, `process_id`, `item_id`, `batch_group_key`, `aggregation_type_id`),

  KEY `idx_kpi_date` (`kpi_date`),
  KEY `idx_kpi_equipment_id` (`equipment_id`),
  KEY `idx_kpi_process_id` (`process_id`),
  KEY `idx_kpi_item_id` (`item_id`),

  -- 신규 FOREIGN KEY 제약 조건 추가
  CONSTRAINT `fk_kpi_data_type` FOREIGN KEY (`aggregation_type_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_calc_status` FOREIGN KEY (`calc_status_code_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT,

  CONSTRAINT `fk_kpi_work_order` FOREIGN KEY (`work_order_id`) REFERENCES `tb_work_order`(`work_order_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_process`   FOREIGN KEY (`process_id`)   REFERENCES `tb_process`(`process_id`)   ON DELETE RESTRICT,
  CONSTRAINT `fk_kpi_item`      FOREIGN KEY (`item_id`)      REFERENCES `tb_item`(`item_id`) ON DELETE RESTRICT,

  CONSTRAINT `ck_actual_oee_range`          CHECK (`actual_oee` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_yield_range`        CHECK (`actual_yield` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_defect_rate_range`  CHECK (`actual_defect_rate` BETWEEN 0 AND 100),
  CONSTRAINT `ck_actual_productivity_nonneg` CHECK (`actual_productivity` >= 0),
  CONSTRAINT `ck_start_before_end` CHECK (end_time >= start_time)
) ENGINE=InnoDB COMMENT='KPI 데이터: 실시간(퍼포먼스 단위) + 배치(하루 단위) 모두 대응';
