-- 직원
CREATE TABLE `tb_employee` (
  `employee_id` VARCHAR(36) NOT NULL COMMENT '직원 ID (PK, FK→tb_user.user_id)',
  `employee_number` VARCHAR(50) NOT NULL COMMENT '직원 번호',
  `employee_name` VARCHAR(100) NOT NULL COMMENT '직원 이름',
  `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
  `deleted_at` DATETIME NULL COMMENT 'UTC',
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
  PRIMARY KEY (`employee_id`),
  UNIQUE KEY `uk_employee_number` (`employee_number`),
  CONSTRAINT `fk_employee_user` FOREIGN KEY (`employee_id`) REFERENCES `tb_user`(`user_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='직원 정보: tb_user와 1:1 관계로 직원의 기본 정보를 관리.';

-- tb_cert (자격증 마스터)
CREATE TABLE `tb_cert` (
  `cert_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '자격증 ID (PK)',
  `cert_code` VARCHAR(50) NOT NULL COMMENT '자격 코드 (유일)',
  `cert_name` VARCHAR(100) NOT NULL COMMENT '자격 명칭',
  `cert_description` VARCHAR(255) NULL COMMENT '설명',
  `cert_valid_period_months` INT NULL COMMENT '유효기간 (월 단위, NULL=무기한)',
  `is_deleted` TINYINT DEFAULT 0,
  `deleted_at` DATETIME NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`cert_id`),
  UNIQUE KEY `uk_cert_code` (`cert_code`)
) ENGINE=InnoDB COMMENT='자격 마스터: 설비/공정 필요 자격 정의';

-- tb_user_cert (직원별 자격증 보유 현황)
CREATE TABLE `tb_user_cert` (
  `user_cert_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '직원-자격증 매핑 ID (PK)',
  `employee_id` VARCHAR(36) NOT NULL COMMENT '직원 ID (FK→tb_employee.employee_id)',
  `cert_id` BIGINT NOT NULL COMMENT '자격증 ID (FK→tb_cert.cert_id)',
  `user_level_code_id` BIGINT NOT NULL COMMENT '숙련도 코드 ID (FK→tb_code, 예: BASIC, INTERMEDIATE)',
  `cert_obtained_date` DATE NOT NULL COMMENT '취득일',
  `cert_expiry_date` DATE NULL COMMENT '만료일 (NULL=무기한)',
  `is_deleted` TINYINT DEFAULT 0,
  `deleted_at` DATETIME NULL,
  `created_by` VARCHAR(50) NOT NULL,
  `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `modified_by` VARCHAR(50) NULL,
  `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP,
  PRIMARY KEY (`user_cert_id`),
  UNIQUE KEY `uk_user_cert` (`employee_id`, `cert_id`),
  CONSTRAINT `fk_user_cert_emp` FOREIGN KEY (`employee_id`) REFERENCES `tb_employee`(`employee_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_user_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert`(`cert_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_user_cert_level` FOREIGN KEY (`user_level_code_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='직원-자격증 매핑: 직원별 자격증/숙련도 관리';

-- 공정 자격
CREATE TABLE `tb_process_cert` (
  `process_cert_id` BIGINT NOT NULL AUTO_INCREMENT,
  `process_id` VARCHAR(36) NOT NULL COMMENT '공정 ID (FK)',
  `cert_id` BIGINT NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
  PRIMARY KEY (`process_cert_id`),
  UNIQUE KEY `uk_proc_cert` (`process_id`, `cert_id`),
  CONSTRAINT `fk_proc_cert_proc` FOREIGN KEY (`process_id`) REFERENCES `tb_process`(`process_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_proc_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert`(`cert_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='공정별 필요 자격 정의';

-- 설비 자격
CREATE TABLE `tb_equipment_cert` (
  `equipment_cert_id` BIGINT NOT NULL AUTO_INCREMENT,
  `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
  `cert_id` BIGINT NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
  PRIMARY KEY (`equipment_cert_id`),
  UNIQUE KEY `uk_eqp_cert` (`equipment_id`, `cert_id`),
  CONSTRAINT `fk_eqp_cert_eqp` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
  CONSTRAINT `fk_eqp_cert_cert` FOREIGN KEY (`cert_id`) REFERENCES `tb_cert`(`cert_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='설비별 필요 자격 정의';

-- 테이블