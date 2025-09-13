-- 2025-09-13
CREATE TABLE `tb_attendance` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '출퇴근 기록 ID (PK)',
    `user_id` VARCHAR(50) NOT NULL COMMENT '직원의 고유 ID',
    `check_time` DATETIME(6) NOT NULL COMMENT '출, 퇴근 시간 (정밀도 6)',
    `status_code_id` BIGINT NOT NULL COMMENT '출, 퇴근 상태 (FK)',
    `description` VARCHAR(255) NULL COMMENT '기록에 대한 추가 설명',
    `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
    `deleted_at` DATETIME NULL COMMENT 'UTC',
    `created_by` VARCHAR(50) NOT NULL,
    `created_at` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
    `modified_by` VARCHAR(50) NULL,
    `modified_at` DATETIME NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',
    PRIMARY KEY (`id`),
    CONSTRAINT `fk_attendance_status_code` FOREIGN KEY (`status_code_id`) REFERENCES `tb_code`(`code_id`) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='직원 출퇴근 기록';

INSERT INTO `tb_code_group` (`group_code`, `group_name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', '출퇴근 상태', '직원의 출근 및 퇴근 상태를 정의합니다.', 'admin');
INSERT INTO `tb_code` (`group_code`, `code`, `name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', 'CHECK_IN', '출근', '직원 출근', 'seed');
INSERT INTO `tb_code` (`group_code`, `code`, `name`, `description`, `created_by`)
VALUES ('ATTENDANCE_STATUS', 'CHECK_OUT', '퇴근', '직원 퇴근', 'seed');