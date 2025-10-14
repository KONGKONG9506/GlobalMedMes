
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

 CREATE TABLE `tb_cert` (
   `cert_id` bigint NOT NULL AUTO_INCREMENT COMMENT '자격증 ID (PK)',
   `cert_code` varchar(50) NOT NULL COMMENT '자격 코드 (유일)',
   `cert_name` varchar(100) NOT NULL COMMENT '자격 명칭',
   `cert_description` varchar(255) DEFAULT NULL COMMENT '설명',
   `cert_valid_period_months` int DEFAULT NULL COMMENT '유효기간 (월 단위, NULL=무기한)',
   `is_deleted` tinyint DEFAULT '0',
   `deleted_at` datetime DEFAULT NULL,
   `created_by` varchar(50) NOT NULL,
   `created_at` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
   `modified_by` varchar(50) DEFAULT NULL,
   `modified_at` datetime DEFAULT NULL ON UPDATE CURRENT_TIMESTAMP,
   PRIMARY KEY (`cert_id`),
   UNIQUE KEY `uk_cert_code` (`cert_code`)
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='자격 마스터: 설비/공정 필요 자격 정의';

CREATE TABLE `tb_process_cert` (
   `process_cert_id` bigint NOT NULL AUTO_INCREMENT,
   `process_id` varchar(36) NOT NULL COMMENT '공정 ID (FK)',
   `cert_id` bigint NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
   `is_deleted` tinyint NOT NULL,
   `deleted_at` datetime DEFAULT NULL COMMENT '삭제 일시',
   PRIMARY KEY (`process_cert_id`),
   UNIQUE KEY `uk_proc_cert` (`process_id`,`cert_id`),
   KEY `fk_proc_cert_cert` (`cert_id`),
   CONSTRAINT `fk_proc_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert` (`cert_id`) ON DELETE RESTRICT,
   CONSTRAINT `fk_proc_cert_proc` FOREIGN KEY (`process_id`) REFERENCES `tb_process` (`process_id`) ON DELETE RESTRICT
 ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='공정별 필요 자격 정의';

