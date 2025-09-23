CREATE TABLE `tb_shift` (
  `shift_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '교대 ID (PK)',
  `shift_code` VARCHAR(10) NOT NULL COMMENT '교대 코드 (유일, 예: A, B, C)',
  `shift_name` VARCHAR(50) NOT NULL COMMENT '교대 명칭 (예: 주간조)',
  `start_time` TIME NOT NULL COMMENT '교대 시작 시간',
  `end_time` TIME NOT NULL COMMENT '교대 종료 시간',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  PRIMARY KEY (`shift_id`),
  UNIQUE KEY `uk_shift_code` (`shift_code`)
) ENGINE=InnoDB COMMENT='교대 마스터: 교대 근무의 기본 정보(코드, 시간 등)를 정의하는 테이블.';


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
  -- 한 로우는 설비 또는 작업장 한 가지만 지정(XOR)
  CONSTRAINT `ck_shiftcal_scope_exclusive`
    CHECK ( (equipment_id IS NOT NULL) <> (workcenter_id IS NOT NULL) ),
  -- 시간 무결성
  CONSTRAINT `ck_shiftcal_time_order`
    CHECK (`end_ts` > `start_ts`)
) ENGINE=InnoDB COMMENT='교대 달력: 날짜/교대 × (설비 또는 작업장) 단위의 집계 프레임(UTC). XOR+부분 유니크로 중복 차단.';


CREATE TABLE `tb_shift_assignment` (
  `assignment_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '배치 ID (PK)',
  `shift_date` DATE NOT NULL COMMENT '근무 날짜',
  `shift_id` BIGINT NOT NULL COMMENT '교대 ID (FK)',
  `worker_id` VARCHAR(36) NOT NULL COMMENT '작업자 ID (FK→tb_user.user_id)',
  `equipment_id` VARCHAR(36) NULL COMMENT '설비 배치 시 사용',
  `workcenter_id` VARCHAR(36) NULL COMMENT '작업장 배치 시 사용',
  `start_ts` DATETIME NOT NULL COMMENT '배치 시작 (UTC)',
  `end_ts` DATETIME NOT NULL COMMENT '배치 종료 (UTC)',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  PRIMARY KEY (`assignment_id`),
  -- 스코프별 부분 유니크(동일 교대/날짜/작업자에 동일 스코프 중복 차단)
  UNIQUE KEY `uk_assign_eqp` (`shift_date`, `shift_id`, `worker_id`, `equipment_id`),
  UNIQUE KEY `uk_assign_wc`  (`shift_date`, `shift_id`, `worker_id`, `workcenter_id`),
  KEY `idx_assign_shift_worker` (`shift_date`, `shift_id`, `worker_id`),
  KEY `idx_assign_worker` (`worker_id`),
  KEY `idx_assign_eqp` (`equipment_id`),
  KEY `idx_assign_wc`  (`workcenter_id`),
  CONSTRAINT `fk_assign_shift` FOREIGN KEY (`shift_id`) REFERENCES `tb_shift`(`shift_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_assign_user`  FOREIGN KEY (`worker_id`) REFERENCES `tb_user`(`user_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_assign_eqp`   FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_assign_wc`    FOREIGN KEY (`workcenter_id`) REFERENCES `tb_workcenter`(`workcenter_id`) ON DELETE RESTRICT,
  -- 한 로우는 설비 또는 작업장 한 가지만 지정(XOR)
  CONSTRAINT `ck_assign_scope_exclusive`
    CHECK ( (equipment_id IS NOT NULL) <> (workcenter_id IS NOT NULL) ),
  -- 시간 무결성
  CONSTRAINT `ck_assign_time_order`
    CHECK (`end_ts` > `start_ts`)
) ENGINE=InnoDB COMMENT='교대 배치: 날짜/교대/작업자 × (설비 또는 작업장) 다건 배정(UTC). XOR+부분 유니크, 작업자=tb_user FK.';
