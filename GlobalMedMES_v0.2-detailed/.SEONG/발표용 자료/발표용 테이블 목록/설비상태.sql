


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

 CREATE TABLE `tb_item` (
   `item_id` varchar(36) NOT NULL COMMENT '품목 ID (PK, UUID)',
   `item_code` varchar(50) NOT NULL COMMENT '품목 코드 (유일)',
   `item_name` varchar(255) NOT NULL COMMENT '품목명',
   `item_type` char(1) NOT NULL COMMENT '품목 유형 (R:원자재, P:반제품, F:완제품)',
   `unit` varchar(10) NOT NULL COMMENT '단위 (예: EA, KG)',
   `description` varchar(255) DEFAULT NULL COMMENT '설명',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`item_id`),
   UNIQUE KEY `uk_item_code` (`item_code`),
   KEY `idx_item_name` (`item_name`),
   CONSTRAINT `ck_item_type` CHECK ((`item_type` in (_utf8mb4'R',_utf8mb4'P',_utf8mb4'F')))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='품목 마스터: 생산/재고 관리 대상. 대표 조인: tb_work_order, tb_bom, tb_material_lot.';

CREATE TABLE `tb_production_plan` (
   `plan_id` varchar(36) NOT NULL COMMENT '생산 계획 ID (PK, UUID)',
   `plan_number` varchar(50) NOT NULL COMMENT '계획 번호 (유일)',
   `item_id` varchar(36) NOT NULL COMMENT '계획 품목 ID (FK)',
   `target_qty` decimal(10,4) NOT NULL COMMENT '계획 수량',
   `start_date` date NOT NULL COMMENT '계획 시작일',
   `end_date` date NOT NULL COMMENT '계획 종료일',
   `status` char(1) NOT NULL COMMENT '계획 상태 (P:Planned, R:Released, C:Completed)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`plan_id`),
   UNIQUE KEY `uk_plan_number` (`plan_number`),
   KEY `idx_plan_item_id` (`item_id`),
   KEY `idx_plan_start_date` (`start_date`),
   CONSTRAINT `fk_plan_item` FOREIGN KEY (`item_id`) REFERENCES `tb_item` (`item_id`) ON DELETE RESTRICT,
   CONSTRAINT `ck_plan_dates` CHECK ((`end_date` >= `start_date`)),
   CONSTRAINT `ck_plan_qty_nonneg` CHECK ((`target_qty` >= 0)),
   CONSTRAINT `ck_plan_status` CHECK ((`status` in (_utf8mb4'P',_utf8mb4'R',_utf8mb4'C')))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='생산 계획: 목표/기간/상태 무결성 보장. 대표 조인: tb_work_order.';

CREATE TABLE `tb_work_order` (
   `work_order_id` varchar(36) NOT NULL COMMENT '작업 지시 ID (PK, UUID)',
   `plan_id` varchar(36) DEFAULT NULL COMMENT '생산 계획 ID (FK) - 선택',
   `work_order_number` varchar(50) NOT NULL COMMENT '작업 지시 번호 (유일)',
   `item_id` varchar(36) NOT NULL COMMENT '생산 품목 ID (FK)',
   `process_id` varchar(36) NOT NULL COMMENT '지시 공정 ID (FK)',
   `equipment_id` varchar(36) NOT NULL COMMENT '지시 설비 ID (FK)',
   `order_qty` decimal(10,4) NOT NULL COMMENT '지시 수량',
   `produced_qty` decimal(10,4) NOT NULL DEFAULT '0.0000' COMMENT '생산 완료 수량',
   `start_ts` datetime DEFAULT NULL COMMENT '지시 시작 타임스탬프 (UTC)',
   `end_ts` datetime DEFAULT NULL COMMENT '지시 종료 타임스탬프 (UTC)',
   `status_code_id` bigint NOT NULL COMMENT '작업 지시 상태 코드 ID (FK→tb_code)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`work_order_id`),
   UNIQUE KEY `uk_wo_number` (`work_order_number`),
   KEY `idx_wo_plan_id` (`plan_id`),
   KEY `idx_wo_item_id` (`item_id`),
   KEY `idx_wo_process_id` (`process_id`),
   KEY `idx_wo_equipment_id` (`equipment_id`),
   KEY `idx_wo_status_code_id` (`status_code_id`),
   KEY `idx_wo_eqp_start` (`equipment_id`,`start_ts`),
   CONSTRAINT `fk_wo_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_wo_item` FOREIGN KEY (`item_id`) REFERENCES `tb_item` (`item_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_wo_plan` FOREIGN KEY (`plan_id`) REFERENCES `tb_production_plan` (`plan_id`) ON DELETE SET NULL,
   CONSTRAINT `fk_wo_process` FOREIGN KEY (`process_id`) REFERENCES `tb_process` (`process_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_wo_status_code` FOREIGN KEY (`status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `ck_wo_qty_nonneg` CHECK (((`order_qty` >= 0) and (`produced_qty` >= 0))),
   CONSTRAINT `ck_wo_time_order` CHECK (((`start_ts` is null) or (`end_ts` is null) or (`end_ts` >= `start_ts`)))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='작업 지시: 실작업 단위. 시간/수량/상태 무결성 보강, 설비×시간 인덱스로 조회 최적화(UTC).';

 CREATE TABLE `tb_equipment_status_log` (
   `log_id` bigint NOT NULL AUTO_INCREMENT COMMENT '로그 ID (PK)',
   `equipment_id` varchar(36) NOT NULL COMMENT '설비 ID (FK)',
   `status_code_id` bigint NOT NULL COMMENT '설비 상태 코드 ID (FK→tb_code)',
   `reason_code_id` bigint DEFAULT NULL COMMENT '비가동 사유 코드 ID (FK→tb_code)',
   `work_order_id` varchar(36) DEFAULT NULL COMMENT '관련 작업 지시 ID (FK) - 선택',
   `shift_id` bigint DEFAULT NULL COMMENT '관련 교대 ID (FK) - 선택',
   `start_time` datetime NOT NULL COMMENT '상태 시작 시점 (UTC)',
   `end_time` datetime DEFAULT NULL COMMENT '상태 종료 시점 (UTC)',
   `is_deleted` tinyint DEFAULT '0' COMMENT '소프트삭제 플래그',
   `deleted_at` datetime DEFAULT NULL COMMENT 'UTC',
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
   PRIMARY KEY (`log_id`),
   KEY `idx_log_equipment_id` (`equipment_id`),
   KEY `idx_log_status_code_id` (`status_code_id`),
   KEY `idx_log_reason_code_id` (`reason_code_id`),
   KEY `idx_log_wo_id` (`work_order_id`),
   KEY `idx_log_shift_id` (`shift_id`),
   KEY `idx_log_equipment_start_time` (`equipment_id`,`start_time`),
   CONSTRAINT `fk_log_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment` (`equipment_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_log_reason_code` FOREIGN KEY (`reason_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE SET NULL,
   CONSTRAINT `fk_log_shift` FOREIGN KEY (`shift_id`) REFERENCES `tb_shift` (`shift_id`) ON DELETE SET NULL,
   CONSTRAINT `fk_log_status_code` FOREIGN KEY (`status_code_id`) REFERENCES `tb_code` (`code_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_log_wo` FOREIGN KEY (`work_order_id`) REFERENCES `tb_work_order` (`work_order_id`) ON DELETE SET NULL,
   CONSTRAINT `ck_log_time_order` CHECK (((`end_time` is null) or (`end_time` >= `start_time`)))
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='설비 상태 로그: 설비 가동/비가동 시계열. 시간 무결성 보장, 설비×시간 최적화(UTC).';
