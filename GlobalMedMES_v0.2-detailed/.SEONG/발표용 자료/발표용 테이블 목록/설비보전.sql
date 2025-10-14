

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

 CREATE TABLE `tb_cmms_pm_plan` (
   `plan_id` bigint NOT NULL AUTO_INCREMENT,
   `equipment_id` varchar(36) NOT NULL,
   `task_name` varchar(100) NOT NULL,
   `cycle_type_code_id` bigint NOT NULL,
   `cycle_value` int NOT NULL,
   `last_done_at` datetime DEFAULT NULL,
   `next_due_at` datetime NOT NULL,
   `status` varchar(20) NOT NULL DEFAULT 'ACTIVE',
   `is_deleted` tinyint DEFAULT '0',
   `deleted_at` datetime DEFAULT NULL,
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
   `estimated_take_time` int DEFAULT NULL COMMENT '예상 소요 시간(분)',
   PRIMARY KEY (`plan_id`),
   KEY `ix_pm_due` (`next_due_at`),
   KEY `ix_pm_eqp` (`equipment_id`),
   KEY `ix_pm_cycle_type` (`cycle_type_code_id`),
   CONSTRAINT `fk_pm_cycle_typecode` FOREIGN KEY (`cycle_type_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_pm_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='CMMS: PM 계획(코드마스터)';


 CREATE TABLE `tb_cmms_work_order` (
   `cmms_wo_id` bigint NOT NULL AUTO_INCREMENT,
   `equipment_id` varchar(36) NOT NULL,
   `title` varchar(200) NOT NULL,
   `priority_code_id` bigint NOT NULL,
   `status_code_id` bigint NOT NULL,
   `assignee_user_id` varchar(36) DEFAULT NULL,
   `request_id` varchar(64) DEFAULT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `started_at` datetime DEFAULT NULL,
   `finished_at` datetime DEFAULT NULL,
   `actual_minutes` int DEFAULT NULL,
   `parts_cost` decimal(12,2) DEFAULT NULL,
   `is_deleted` tinyint DEFAULT '0',
   `deleted_at` datetime DEFAULT NULL,
   `created_by` varchar(50) NOT NULL,
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`cmms_wo_id`),
   UNIQUE KEY `uq_cmms_wo_request_id` (`request_id`),
   KEY `ix_cmms_wo_eqp_created` (`equipment_id`,`created_at`),
   KEY `ix_cmms_wo_status` (`status_code_id`),
   KEY `ix_cmms_wo_priority` (`priority_code_id`),
   KEY `fk_cmms_wo_assignee` (`assignee_user_id`),
   CONSTRAINT `fk_cmms_wo_assignee` FOREIGN KEY (`assignee_user_id`) REFERENCES `tb_user` (`user_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_cmms_wo_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_cmms_wo_priority_code` FOREIGN KEY (`priority_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_cmms_wo_status_code` FOREIGN KEY (`status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='CMMS: 보전 작업지시(코드마스터)'  ;


 CREATE TABLE `tb_cmms_work_order_log` (
   `log_id` bigint NOT NULL AUTO_INCREMENT,
   `cmms_wo_id` bigint NOT NULL,
   `from_status_code_id` bigint DEFAULT NULL,
   `to_status_code_id` bigint NOT NULL,
   `changed_by` varchar(36) NOT NULL,
   `changed_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `note` varchar(255) DEFAULT NULL,
   PRIMARY KEY (`log_id`),
   KEY `ix_wolog_wo` (`cmms_wo_id`,`changed_at`),
   KEY `fk_wolog_from_code` (`from_status_code_id`),
   KEY `fk_wolog_to_code` (`to_status_code_id`),
   KEY `fk_cmms_wolog_user` (`changed_by`),
   CONSTRAINT `fk_cmms_wolog_user` FOREIGN KEY (`changed_by`) REFERENCES `tb_user` (`user_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_cmms_wolog_wo` FOREIGN KEY (`cmms_wo_id`) REFERENCES `tb_cmms_work_order` (`cmms_wo_id`) ON DELETE CASCADE,
   CONSTRAINT `fk_wolog_from_code` FOREIGN KEY (`from_status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_wolog_to_code` FOREIGN KEY (`to_status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='CMMS: WO 상태전이 이력(코드마스터)';


 CREATE TABLE `tb_cmms_fault_log` (
   `fault_id` bigint NOT NULL AUTO_INCREMENT,
   `equipment_id` varchar(36) NOT NULL,
   `loss_cat_code_id` bigint NOT NULL,
   `symptom` varchar(255) NOT NULL,
   `action` varchar(255) DEFAULT NULL,
   `occurred_at` datetime NOT NULL,
   `resolved_at` datetime DEFAULT NULL,
   `cmms_wo_id` bigint DEFAULT NULL,
   `is_deleted` tinyint DEFAULT '0',
   `deleted_at` datetime DEFAULT NULL,
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`fault_id`),
   KEY `ix_fault_time_eqp` (`occurred_at`,`equipment_id`),
   KEY `ix_fault_loss` (`loss_cat_code_id`),
   KEY `fk_fault_equipment` (`equipment_id`),
   KEY `fk_fault_cmmswo` (`cmms_wo_id`),
   CONSTRAINT `fk_fault_cmmswo` FOREIGN KEY (`cmms_wo_id`) REFERENCES `tb_cmms_work_order` (`cmms_wo_id`) ON DELETE SET NULL,
   CONSTRAINT `fk_fault_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_fault_losscode` FOREIGN KEY (`loss_cat_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `ck_fault_time_order` CHECK (((`resolved_at` is null) or (`resolved_at` >= `occurred_at`)))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='CMMS: 고장/정지 로그(코드마스터)';

