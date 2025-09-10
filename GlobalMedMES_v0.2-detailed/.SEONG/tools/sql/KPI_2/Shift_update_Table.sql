-- 기존 테이블 업데이트
--  ALTER TABLE tb_shift_calendar DROP CHECK ck_shiftcal_scope_exclusive;
--  ALTER TABLE tb_shift_assignment DROP CHECK ck_assign_scope_exclusive;

-- 테이블 새로 작성
CREATE TABLE `tb_shift_calendar` (
  `calendar_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '교대 달력 ID (PK)',
  `shift_date` DATE NOT NULL COMMENT '근무 날짜',
  `shift_id` BIGINT NOT NULL COMMENT '교대 ID (FK)',
  `equipment_id` VARCHAR(36) NULL COMMENT '설비 ID (FK) - 선택',
  `workcenter_id` VARCHAR(36) NULL COMMENT '작업장 ID (FK) - 선택',
  `start_ts` DATETIME NOT NULL COMMENT '교대 시작 (UTC)',
  `end_ts` DATETIME NOT NULL COMMENT '교대 종료 (UTC)',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  PRIMARY KEY (`calendar_id`),
  -- 스코프별 부분 유니크(중복 차단)
  UNIQUE KEY `uk_shiftcal_eqp` (`shift_date`, `shift_id`, `equipment_id`),
  UNIQUE KEY `uk_shiftcal_wc`  (`shift_date`, `shift_id`, `workcenter_id`),
  KEY `idx_shiftcal_date_shift_eqp` (`shift_date`, `shift_id`, `equipment_id`),
  KEY `idx_shiftcal_date_shift_wc`  (`shift_date`, `shift_id`, `workcenter_id`),
  CONSTRAINT `fk_shiftcal_shift` FOREIGN KEY (`shift_id`) REFERENCES `tb_shift`(`shift_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_shiftcal_eqp`   FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_shiftcal_wc`    FOREIGN KEY (`workcenter_id`) REFERENCES `tb_workcenter`(`workcenter_id`) ON DELETE RESTRICT,
  -- 시간 무결성
  CONSTRAINT `ck_shiftcal_time_order`
    CHECK (`end_ts` > `start_ts`)
) ENGINE=InnoDB COMMENT='교대 달력: 날짜/교대 × (설비 또는 작업장) 단위의 집계 프레임(UTC). XOR+부분 유니크로 중복 차단.';
