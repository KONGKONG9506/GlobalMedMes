
 CREATE TABLE `tb_code_group` (
   `group_code` varchar(50) NOT NULL COMMENT '코드 그룹 ID (PK)',
   `group_name` varchar(100) NOT NULL COMMENT '코드 그룹명 (유일)',
   `description` varchar(255) DEFAULT NULL COMMENT '그룹에 대한 상세 설명',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`group_code`),
   UNIQUE KEY `uk_code_group_name` (`group_name`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='코드 그룹 마스터: 시스템 공통 코드의 상위 그룹을 정의하는 테이블. 대표 조인: tb_code.';

 CREATE TABLE `tb_code` (
   `code_id` bigint NOT NULL AUTO_INCREMENT COMMENT '코드 ID (PK, 서로게이트 키)',
   `group_code` varchar(50) NOT NULL COMMENT '코드 그룹 ID (FK)',
   `code` varchar(50) NOT NULL COMMENT '코드 값 (그룹 내 유일)',
   `name` varchar(100) NOT NULL COMMENT '코드 명칭',
   `description` varchar(255) DEFAULT NULL COMMENT '코드에 대한 상세 설명',
   `use_yn` char(1) NOT NULL DEFAULT 'Y' COMMENT '사용 여부 (Y/N)',
   `sort_order` int DEFAULT NULL COMMENT '정렬 순서',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`code_id`),
   UNIQUE KEY `uk_code_group_code` (`group_code`,`code`),
   KEY `idx_code_group_sort` (`group_code`,`sort_order`),
   CONSTRAINT `fk_code_group_code` FOREIGN KEY (`group_code`) REFERENCES `tb_code_group` (`group_code`) ON DELETE RESTRICT,
   CONSTRAINT `ck_code_use_yn` CHECK ((`use_yn` in (_utf8mb4'Y',_utf8mb4'N')))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='코드 마스터: 그룹에 종속된 개별 코드를 정의하는 테이블. 대표 조인: tb_equipment.status_code_id, tb_work_order.status_code_id 등.';
 
CREATE TABLE `tb_shift` (
   `shift_id` bigint NOT NULL AUTO_INCREMENT COMMENT '교대 ID (PK)',
   `shift_code` varchar(10) NOT NULL COMMENT '교대 코드 (유일, 예: A, B, C)',
   `shift_name` varchar(50) NOT NULL COMMENT '교대 명칭 (예: 주간조)',
   `start_time` time NOT NULL COMMENT '교대 시작 시간',
   `end_time` time NOT NULL COMMENT '교대 종료 시간',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`shift_id`),
   UNIQUE KEY `uk_shift_code` (`shift_code`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='교대 마스터: 교대 근무의 기본 정보(코드, 시간 등)를 정의하는 테이블.';

CREATE TABLE `tb_user` (
   `user_id` varchar(36) NOT NULL COMMENT '사용자 ID (UUID, PK)',
   `username` varchar(50) NOT NULL COMMENT '로그인 ID (유일, 소문자 권장)',
   `email` varchar(255) DEFAULT NULL COMMENT '이메일(선택, 유일 권장)',
   `password_hash` varchar(100) NOT NULL COMMENT '비밀번호 해시(예: BCrypt 60자, Argon2id 가능)',
   `password_algo` varchar(20) NOT NULL DEFAULT 'bcrypt' COMMENT '해시 알고리즘(bcrypt/argon2id)',
   `is_active` tinyint NOT NULL DEFAULT '1' COMMENT '활성 여부(1/0)',
   `failed_login_count` int NOT NULL DEFAULT '0' COMMENT '연속 실패 횟수(>=0)',
   `locked_until` datetime DEFAULT NULL COMMENT '잠금 해제 예정 시각(UTC)',
   `last_login_at` datetime DEFAULT NULL COMMENT '마지막 로그인 시각(UTC)',
   `phone` varchar(30) DEFAULT NULL COMMENT '연락처(선택)',
   `display_name` varchar(100) DEFAULT NULL COMMENT '표시명(선택)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`user_id`),
   UNIQUE KEY `uk_user_username` (`username`),
   UNIQUE KEY `uk_user_email` (`email`),
   CONSTRAINT `ck_user_failed_cnt` CHECK ((`failed_login_count` >= 0)),
   CONSTRAINT `ck_user_is_active` CHECK ((`is_active` in (0,1))),
   CONSTRAINT `ck_user_is_deleted` CHECK ((`is_deleted` in (0,1)))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='사용자 마스터: 로그인/상태/보안 메타. 해시는 필수, 평문 금지(UTC).';

CREATE TABLE `tb_workshop` (
   `workshop_id` varchar(36) NOT NULL COMMENT '작업장 그룹 ID (PK, UUID)',
   `workshop_name` varchar(255) NOT NULL COMMENT '작업장 그룹명 (유일)',
   `description` varchar(255) DEFAULT NULL COMMENT '설명',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`workshop_id`),
   UNIQUE KEY `uk_workshop_name` (`workshop_name`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='작업장 그룹 마스터: 생산 라인의 상위 그룹을 정의하는 테이블. 대표 조인: tb_workcenter.';


CREATE TABLE `tb_workcenter` (
   `workcenter_id` varchar(36) NOT NULL COMMENT '작업장 ID (PK, UUID)',
   `workcenter_name` varchar(255) NOT NULL COMMENT '작업장명 (유일)',
   `workshop_id` varchar(36) NOT NULL COMMENT '작업장 그룹 ID (FK)',
   `description` varchar(255) DEFAULT NULL COMMENT '설명',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`workcenter_id`),
   UNIQUE KEY `uk_workcenter_name` (`workcenter_name`),
   KEY `idx_workcenter_workshop_id` (`workshop_id`),
   CONSTRAINT `fk_workcenter_workshop` FOREIGN KEY (`workshop_id`) REFERENCES `tb_workshop` (`workshop_id`) ON DELETE RESTRICT
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='작업장 마스터: 설비 및 공정의 상위 그룹. 대표 조인: tb_equipment, tb_shift_calendar.';

CREATE TABLE `tb_process` (
   `process_id` varchar(36) NOT NULL COMMENT '공정 ID (PK, UUID)',
   `process_name` varchar(255) NOT NULL COMMENT '공정명 (유일)',
   `description` varchar(255) DEFAULT NULL COMMENT '설명',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`process_id`),
   UNIQUE KEY `uk_process_name` (`process_name`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공정 마스터: 생산 라우팅의 작업 단계를 정의하는 테이블. 대표 조인: tb_equipment, tb_work_order, tb_production_performance.';

 CREATE TABLE `tb_equipment` (
   `equipment_id` varchar(36) NOT NULL COMMENT '설비 ID (PK, UUID)',
   `equipment_name` varchar(255) NOT NULL COMMENT '설비명 (유일)',
   `workcenter_id` varchar(36) NOT NULL COMMENT '작업장 ID (FK)',
   `process_id` varchar(36) NOT NULL COMMENT '공정 ID (FK)',
   `status_code_id` bigint NOT NULL COMMENT '설비 상태 코드 ID (FK→tb_code)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`equipment_id`),
   UNIQUE KEY `uk_equipment_name` (`equipment_name`),
   KEY `idx_equipment_workcenter_id` (`workcenter_id`),
   KEY `idx_equipment_process_id` (`process_id`),
   KEY `idx_equipment_status_code_id` (`status_code_id`),
   KEY `idx_equipment_proc_wc` (`process_id`,`workcenter_id`),
   CONSTRAINT `fk_eqp_proc` FOREIGN KEY (`process_id`) REFERENCES `tb_process` (`process_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_eqp_status` FOREIGN KEY (`status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_eqp_wc` FOREIGN KEY (`workcenter_id`) REFERENCES `tb_workcenter` (`workcenter_id`) ON DELETE RESTRICT
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='설비 마스터: 실적/상태로그/작업지시/교대달력과 결합되는 핵심 마스터(UTC).';

CREATE TABLE `tb_shift_calendar` (
   `calendar_id` bigint NOT NULL AUTO_INCREMENT COMMENT '교대 달력 ID (PK)',
   `shift_date` date NOT NULL COMMENT '근무 날짜',
   `shift_id` bigint NOT NULL COMMENT '교대 ID (FK)',
   `equipment_id` varchar(36) DEFAULT NULL COMMENT '설비 ID (FK) - 선택',
   `workcenter_id` varchar(36) DEFAULT NULL COMMENT '작업장 ID (FK) - 선택',
   `start_ts` datetime NOT NULL COMMENT '교대 시작 (UTC)',
   `end_ts` datetime NOT NULL COMMENT '교대 종료 (UTC)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`calendar_id`),
   UNIQUE KEY `uk_shiftcal_all` (`shift_date`,`shift_id`,`equipment_id`, `workcenter_id`),
   KEY `idx_shiftcal_date_shift_eqp` (`shift_date`,`shift_id`,`equipment_id`),
   KEY `idx_shiftcal_date_shift_wc` (`shift_date`,`shift_id`,`workcenter_id`),
   KEY `fk_shiftcal_shift` (`shift_id`),
   KEY `fk_shiftcal_eqp` (`equipment_id`),
   KEY `fk_shiftcal_wc` (`workcenter_id`),
   CONSTRAINT `fk_shiftcal_eqp` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_shiftcal_shift` FOREIGN KEY (`shift_id`) REFERENCES `tb_shift` (`shift_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_shiftcal_wc` FOREIGN KEY (`workcenter_id`) REFERENCES `tb_workcenter` (`workcenter_id`) ON DELETE RESTRICT,
   CONSTRAINT `ck_shiftcal_time_order` CHECK ((`end_ts` > `start_ts`))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='교대 달력: 날짜/교대 × (설비 또는 작업장) 단위의 집계 프레임(UTC). XOR+부분 유니크로 중복 차단.';

CREATE TABLE `tb_shift_assignment` (
   `assignment_id` bigint NOT NULL AUTO_INCREMENT COMMENT '배치 ID (PK)',
   `shift_date` date NOT NULL COMMENT '근무 날짜',
   `shift_id` bigint NOT NULL COMMENT '교대 ID (FK)',
   `worker_id` varchar(36) NOT NULL COMMENT '작업자 ID (FK→tb_user.user_id)',
   `equipment_id` varchar(36) DEFAULT NULL COMMENT '설비 배치 시 사용',
   `workcenter_id` varchar(36) DEFAULT NULL COMMENT '작업장 배치 시 사용',
   `start_ts` datetime NOT NULL COMMENT '배치 시작 (UTC)',
   `end_ts` datetime NOT NULL COMMENT '배치 종료 (UTC)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`assignment_id`),
   UNIQUE KEY `uk_assign_eqp` (`shift_date`,`shift_id`,`worker_id`,`equipment_id`),
   UNIQUE KEY `uk_assign_wc` (`shift_date`,`shift_id`,`worker_id`,`workcenter_id`),
   KEY `idx_assign_shift_worker` (`shift_date`,`shift_id`,`worker_id`),
   KEY `idx_assign_worker` (`worker_id`),
   KEY `idx_assign_eqp` (`equipment_id`),
   KEY `idx_assign_wc` (`workcenter_id`),
   KEY `fk_assign_shift` (`shift_id`),
   CONSTRAINT `fk_assign_eqp` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_assign_shift` FOREIGN KEY (`shift_id`) REFERENCES `tb_shift` (`shift_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_assign_user` FOREIGN KEY (`worker_id`) REFERENCES `tb_user` (`user_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_assign_wc` FOREIGN KEY (`workcenter_id`) REFERENCES `tb_workcenter` (`workcenter_id`) ON DELETE RESTRICT,
   CONSTRAINT `ck_assign_time_order` CHECK ((`end_ts` > `start_ts`))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='교대 배치: 날짜/교대/작업자 × (설비 또는 작업장) 다건 배정(UTC). XOR+부분 유니크, 작업자=tb_user FK.';
