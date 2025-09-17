-- 2025-09-12

-- 기존 DOWNTIME 코드 비활성화
UPDATE tb_code
SET use_yn = 'N'
WHERE group_code = 'PROD_EVENT' AND code = 'DOWNTIME';

-- work_order_id NULL 허용
ALTER TABLE tb_production_log
MODIFY COLUMN work_order_id VARCHAR(36) NULL COMMENT '작업 지시 ID (FK)';

-- process_id NULL 허용
ALTER TABLE tb_production_log
MODIFY COLUMN process_id VARCHAR(36) NULL COMMENT '공정 ID (FK)';

-- 새로운 코드 추가
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by)
VALUES
('PROD_EVENT','DOWNTIME_START','비가동 시작','Y',6,'seed'),
('PROD_EVENT','DOWNTIME_END','비가동 종료','Y',7,'seed')
ON DUPLICATE KEY UPDATE
    name = VALUES(name),
    use_yn = VALUES(use_yn),
    sort_order = VALUES(sort_order);

-- 1. `tb_code_group`에 'PLANNED_DOWNTIME_TYPE' 코드 그룹 추가
INSERT INTO tb_code_group (group_code, group_name, created_by)
VALUES ('PLANNED_DOWNTIME_TYPE','계획된 다운타임 유형','seed')
ON DUPLICATE KEY UPDATE group_name=VALUES(group_name);

-- 2. `tb_code`에 구체적인 다운타임 유형 코드 추가
-- 코드 값(code)은 tb_kpi_planned_downtime.downtime_type 컬럼에 저장됩니다.
INSERT INTO tb_code (group_code, code, name, use_yn, sort_order, created_by) VALUES
('PLANNED_DOWNTIME_TYPE','PM','정기 점검','Y',1,'seed'),
('PLANNED_DOWNTIME_TYPE','SHIFT_BREAK','교대조 휴식','Y',2,'seed'),
('PLANNED_DOWNTIME_TYPE','HOLIDAY','공장 휴일','Y',3,'seed')
ON DUPLICATE KEY UPDATE name=VALUES(name), use_yn=VALUES(use_yn), sort_order=VALUES(sort_order);

-- 계획된 다운타임 기록을 위한 테이블
-- 이 테이블은 PM, 시프트 휴식 시간 등 모든 계획된 다운타임 이벤트를 통합 관리합니다.
CREATE TABLE `tb_kpi_planned_downtime` (
    `planned_downtime_id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '계획된 다운타임 ID (PK)',
    `equipment_id` VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
    `start_time` TIMESTAMP NOT NULL COMMENT '계획된 다운타임 시작 시간',
    `end_time` TIMESTAMP NOT NULL COMMENT '계획된 다운타임 종료 시간',
    `duration_minutes` INT NOT NULL COMMENT '다운타임 지속 시간(분)',
    `downtime_type_code_id` BIGINT NOT NULL COMMENT '다운타임 유형 (예: PM, SHIFT_BREAK, HOLIDAY)',
    `description` VARCHAR(255) NULL COMMENT '상세 설명',
    
    `is_deleted` TINYINT DEFAULT 0 COMMENT '소프트삭제 플래그',
    `deleted_at` TIMESTAMP NULL COMMENT 'UTC',
    `created_by` VARCHAR(50) NOT NULL,
    `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'UTC',
    `modified_by` VARCHAR(50) NULL,
    `modified_at` TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP COMMENT 'UTC',

    PRIMARY KEY (`planned_downtime_id`),
    KEY `idx_equipment_time` (`equipment_id`, `start_time`),
    CONSTRAINT `fk_downtime_equipment` FOREIGN KEY (`equipment_id`) REFERENCES `tb_equipment`(`equipment_id`) ON DELETE RESTRICT,
    CONSTRAINT fk_downtime_type FOREIGN KEY (downtime_type_code_id) REFERENCES tb_code(code_id)
) ENGINE=InnoDB COMMENT='계획된 다운타임 기록';


ALTER TABLE tb_kpi_planned_downtime
ADD COLUMN duration_seconds BIGINT NOT NULL DEFAULT 0 COMMENT '다운타임 지속 시간(초)';

-- 2) 기존 minutes → seconds 변환해서 넣기
UPDATE tb_kpi_planned_downtime
SET duration_seconds = duration_minutes * 60;

-- 3) 기존 minutes 컬럼 삭제
ALTER TABLE tb_kpi_planned_downtime DROP COLUMN duration_minutes;

-- 4) tmp 컬럼명을 duration_seconds로 변경
ALTER TABLE tb_kpi_planned_downtime 
CHANGE COLUMN tmp_duration_seconds duration_seconds BIGINT NOT NULL COMMENT '다운타임 지속 시간(초)';

ALTER TABLE tb_kpi_planned_downtime
MODIFY COLUMN start_time TIMESTAMP NOT NULL,
MODIFY COLUMN end_time TIMESTAMP NOT NULL,
MODIFY COLUMN deleted_at TIMESTAMP NULL,
MODIFY COLUMN created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
MODIFY COLUMN modified_at TIMESTAMP NULL ON UPDATE CURRENT_TIMESTAMP;