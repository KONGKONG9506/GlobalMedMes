-- 인증(CERT) 기준정보 데이터
INSERT INTO `tb_cert` 
(cert_code, cert_name, cert_description, cert_valid_period_months, is_deleted, created_by, created_at)
VALUES
-- 1️⃣ 원자재 입고 단계 관련 인증
('CERT-MAT-COA', '원자재 성분 분석서 (COA)', '티타늄 Grade5, SLA용 약품 등 입고 시 제공되는 화학·기계적 특성 성적서', 6, 0, 'system', NOW()),
('CERT-MAT-MSDS', '물질안전보건자료 (MSDS)', 'SLA 표면처리용 산용액(불산, 질산 등)에 대한 안전자료', 12, 0, 'system', NOW()),

-- 2️⃣ 가공 공정 (CNC, 블라스팅, 에칭 등) 관련 인증
('CERT-ISO9001', '품질경영시스템 인증 (ISO 9001)', '가공 및 생산 전반의 품질경영 시스템 적합성 인증', 36, 0, 'system', NOW()),
('CERT-ISO13485', '의료기기 품질경영시스템 인증 (ISO 13485)', '의료기기(임플란트) 제조에 요구되는 품질 시스템 인증', 36, 0, 'system', NOW()),
('CERT-EQP-CAL', '설비 교정 인증서', 'CNC 및 검사장비 교정 성적서 (정밀도 보증)', 12, 0, 'system', NOW()),

-- 3️⃣ 표면처리 및 세척 공정 관련 인증
('CERT-SURF-QA', '표면처리 품질 검증서', 'SLA 표면처리 공정 후 표면 거칠기, 접촉각, 청정도 시험 결과', 12, 0, 'system', NOW()),
('CERT-CHEM-QA', '화학약품 적합성 시험서', '표면처리 약품의 농도, 오염도, 금속이온 잔류 분석 시험 결과', 6, 0, 'system', NOW()),

-- 4️⃣ 품질 검사 및 멸균 관련 인증
('CERT-FINAL-QC', '최종검사 성적서 (QC Report)', '치수, 외관, 표면 상태 등 검사 항목별 합격 여부 및 검사자 기록', 3, 0, 'system', NOW()),
('CERT-STERILE', '멸균 공정 적격성 인증 (Validation)', '감마선 멸균 공정에 대한 밸리데이션 성적서 및 주기 평가 기록', 12, 0, 'system', NOW()),

-- 5️⃣ 수출용 및 규제 관련 인증
('CERT-CE-MARK', 'CE 인증 (유럽 의료기기 인증)', 'EU MDR 기준을 충족한 임플란트 제품의 수출 적합성 인증', 36, 0, 'system', NOW()),
('CERT-KFDA-GMP', 'GMP 인증 (식약처 의료기기 제조)', '한국 식약처에서 인정한 GMP 적합 인증 (제조/품질관리)', 24, 0, 'system', NOW());
🧩 공정과의 매핑 예시 (MES 내 참조 구조)
공정	관련 인증 코드	설명
P-100 CNC 가공	CERT-EQP-CAL / CERT-ISO13485	설비 교정 및 품질 시스템 준수
P-200 블라스팅	CERT-SURF-QA / CERT-CHEM-QA	표면 처리 품질 및 약품 시험
P-300 에칭	CERT-SURF-QA / CERT-CHEM-QA	표면 처리 화학 적합성
P-400 세척/건조	CERT-CHEM-QA	세척액 잔류물 시험
Q-100 원자재 검사	CERT-MAT-COA / CERT-MAT-MSDS	원자재 성분·안전 인증
Q-200 최종 검사	CERT-FINAL-QC / CERT-EQP-CAL	치수·외관·정밀 검사 성적서
P-500 포장/멸균	CERT-STERILE / CERT-CE-MARK	멸균 공정 및 수출 인증
전체	CERT-ISO9001 / CERT-KFDA-GMP	품질경영 및 법규 적합성