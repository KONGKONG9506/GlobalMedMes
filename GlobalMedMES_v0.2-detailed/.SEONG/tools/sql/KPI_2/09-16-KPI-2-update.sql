-- 공정 자격
CREATE TABLE tb_process_cert (
 process_cert_id BIGINT NOT NULL AUTO_INCREMENT,
 process_id VARCHAR(36) NOT NULL COMMENT '공정 ID (FK)',
 cert_id BIGINT NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
 is_deleted TINYINT NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
 deleted_at DATETIME NULL COMMENT '삭제 일시',
 PRIMARY KEY (process_cert_id),
 UNIQUE KEY uk_proc_cert (process_id, cert_id),
 CONSTRAINT fk_proc_cert_proc FOREIGN KEY (process_id) REFERENCES tb_process(process_id) ON DELETE RESTRICT,
 CONSTRAINT fk_proc_cert_cert FOREIGN KEY (cert_id) REFERENCES tb_cert(cert_id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='공정별 필요 자격 정의';

-- 설비 자격
CREATE TABLE tb_equipment_cert (
 equipment_cert_id BIGINT NOT NULL AUTO_INCREMENT,
 equipment_id VARCHAR(36) NOT NULL COMMENT '설비 ID (FK)',
 cert_id BIGINT NOT NULL COMMENT '자격 ID (FK→tb_cert.cert_id)',
 is_deleted TINYINT NOT NULL DEFAULT FALSE COMMENT '삭제 여부',
 deleted_at DATETIME NULL COMMENT '삭제 일시',
 PRIMARY KEY (equipment_cert_id),
 UNIQUE KEY uk_eqp_cert (equipment_id, cert_id),
 CONSTRAINT fk_eqp_cert_eqp FOREIGN KEY (equipment_id) REFERENCES tb_equipment(equipment_id) ON DELETE RESTRICT,
 CONSTRAINT fk_eqp_cert_cert FOREIGN KEY (cert_id) REFERENCES tb_cert(cert_id) ON DELETE RESTRICT
) ENGINE=InnoDB COMMENT='설비별 필요 자격 정의';

-- 기존 테이블에 추가

-- 공정 자격 테이블 수정
ALTER TABLE tb_process_cert
ADD COLUMN is_deleted TINYINT NOT NULL DEFAULT FALSE COMMENT '삭제 여부' AFTER cert_id,
ADD COLUMN deleted_at DATETIME NULL COMMENT '삭제 일시' AFTER is_deleted;

-- 설비 자격 테이블 수정
ALTER TABLE tb_equipment_cert
ADD COLUMN is_deleted TINYINT NOT NULL DEFAULT FALSE COMMENT '삭제 여부' AFTER cert_id,
ADD COLUMN deleted_at DATETIME NULL COMMENT '삭제 일시' AFTER is_deleted;
