

-- 1) event_type: tb_code(PROD_EVENT) FK 연결
ALTER TABLE tb_production_log
    MODIFY COLUMN event_type BIGINT NOT NULL;

ALTER TABLE tb_production_log
    ADD CONSTRAINT fk_prodlog_event_type
        FOREIGN KEY (event_type) REFERENCES tb_code(code_id)
        ON DELETE RESTRICT;

-- 2) event_value 음수 금지 (양품/불량 수량, 시간 등)
ALTER TABLE tb_production_log
    ADD CONSTRAINT ck_prodlog_value_nonneg CHECK (event_value >= 0);

-- 3) event_timestamp null 금지
ALTER TABLE tb_production_log
    ADD CONSTRAINT ck_prodlog_time_order CHECK (
        event_timestamp IS NOT NULL
    ); 

-- 4) 고유 인덱스: 같은 WO-설비-공정-타임스탬프 중복 방지
CREATE UNIQUE INDEX uk_prodlog_unique 
ON tb_production_log(work_order_id, equipment_id, process_id, event_timestamp, event_type);


-- tb_kpi_data 불량률 추가
ALTER TABLE `tb_kpi_data`
ADD COLUMN `actual_defect_rate` DECIMAL(5,2) NOT NULL DEFAULT 0 COMMENT '불량률 (%) [0~100]' AFTER `actual_yield`,
ADD CONSTRAINT `ck_actual_defect_rate_range` CHECK (`actual_defect_rate` BETWEEN 0 AND 100);

-- tb_kpi_data 집계 유형, 저장 상태, 계산 시간, 집계 시작 시간, 집계 종료 시간 추가
ALTER TABLE `tb_kpi_data`
ADD COLUMN aggregation_type VARCHAR(20) NOT NULL DEFAULT 'REALTIME' COMMENT '집계 유형 (REALTIME/DAILY_BATCH 등)',
ADD COLUMN calc_status TINYINT NOT NULL DEFAULT 1 COMMENT '계산 상태 (0=FAIL, 1=SUCCESS, 2=IN_PROGRESS, 3=RETRY)',
ADD COLUMN calc_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT 'KPI 계산 시각';

ALTER TABLE tb_kpi_data 
  ADD COLUMN start_time DATETIME NULL COMMENT 'KPI 집계 시작 시간',
  ADD COLUMN end_time DATETIME NULL COMMENT 'KPI 집계 종료 시간';

-- 기존 데이터 처리
UPDATE tb_kpi_data SET start_time = created_at, end_time = created_at;

-- 그 후에 NOT NULL + DEFAULT 제약 걸기
ALTER TABLE tb_kpi_data 
  MODIFY start_time DATETIME NOT NULL,
  MODIFY end_time DATETIME NOT NULL;