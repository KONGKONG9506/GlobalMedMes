-- https://dbdiagram.io/d

Table "tb_code_group" {
  "group_code" varchar(50) [pk, not null, note: '코드 그룹 ID (PK)']
  "group_name" varchar(100) [not null, note: '코드 그룹명 (유일)']
  "description" varchar(255) [default: NULL, note: '그룹에 대한 상세 설명']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    group_name [unique, name: "uk_code_group_name"]
  }
  Note: '코드 그룹 마스터: 시스템 공통 코드의 상위 그룹을 정의하는 테이블. 대표 조인: tb_code.'
}

Table "tb_code" {
  "code_id" bigint [pk, not null, increment, note: '코드 ID (PK, 서로게이트 키)']
  "group_code" varchar(50) [not null, note: '코드 그룹 ID (FK)']
  "code" varchar(50) [not null, note: '코드 값 (그룹 내 유일)']
  "name" varchar(100) [not null, note: '코드 명칭']
  "description" varchar(255) [default: NULL, note: '코드에 대한 상세 설명']
  "use_yn" char(1) [not null, default: 'Y', note: '사용 여부 (Y/N)']
  "sort_order" int [default: NULL, note: '정렬 순서']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    (group_code, code) [unique, name: "uk_code_group_code"]
    (group_code, sort_order) [name: "idx_code_group_sort"]
  }
  Note: '코드 마스터: 그룹에 종속된 개별 코드를 정의하는 테이블. 대표 조인: tb_equipment.status_code_id, tb_work_order.status_code_id 등.'
}

Table "tb_workshop" {
  "workshop_id" varchar(36) [pk, not null, note: '작업장 그룹 ID (PK, UUID)']
  "workshop_name" varchar(255) [not null, note: '작업장 그룹명 (유일)']
  "description" varchar(255) [default: NULL, note: '설명']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    workshop_name [unique, name: "uk_workshop_name"]
  }
  Note: '작업장 그룹 마스터: 생산 라인의 상위 그룹을 정의하는 테이블. 대표 조인: tb_workcenter.'
}

Table "tb_workcenter" {
  "workcenter_id" varchar(36) [pk, not null, note: '작업장 ID (PK, UUID)']
  "workcenter_name" varchar(255) [not null, note: '작업장명 (유일)']
  "workshop_id" varchar(36) [not null, note: '작업장 그룹 ID (FK)']
  "description" varchar(255) [default: NULL, note: '설명']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    workcenter_name [unique, name: "uk_workcenter_name"]
    workshop_id [name: "idx_workcenter_workshop_id"]
  }
  Note: '작업장 마스터: 설비 및 공정의 상위 그룹. 대표 조인: tb_equipment, tb_shift_calendar.'
}

Table "tb_item" {
  "item_id" varchar(36) [pk, not null, note: '품목 ID (PK, UUID)']
  "item_code" varchar(50) [not null, note: '품목 코드 (유일)']
  "item_name" varchar(255) [not null, note: '품목명']
  "item_type" char(1) [not null, note: '품목 유형 (R:원자재, P:반제품, F:완제품)']
  "unit" varchar(10) [not null, note: '단위 (예: EA, KG)']
  "description" varchar(255) [default: NULL, note: '설명']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    item_code [unique, name: "uk_item_code"]
    item_name [name: "idx_item_name"]
  }
  Note: '품목 마스터: 생산/재고 관리 대상. 대표 조인: tb_work_order, tb_bom, tb_material_lot.'
}

Table "tb_production_plan" {
  "plan_id" varchar(36) [pk, not null, note: '생산 계획 ID (PK, UUID)']
  "plan_number" varchar(50) [not null, note: '계획 번호 (유일)']
  "item_id" varchar(36) [not null, note: '계획 품목 ID (FK)']
  "target_qty" decimal(10,4) [not null, note: '계획 수량']
  "start_date" date [not null, note: '계획 시작일']
  "end_date" date [not null, note: '계획 종료일']
  "status" char(1) [not null, note: '계획 상태 (P:Planned, R:Released, C:Completed)']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    plan_number [unique, name: "uk_plan_number"]
    item_id [name: "idx_plan_item_id"]
    start_date [name: "idx_plan_start_date"]
  }
  Note: '생산 계획: 목표/기간/상태 무결성 보장. 대표 조인: tb_work_order.'
}

Table "tb_user" {
  "user_id" varchar(36) [pk, not null, note: '사용자 ID (UUID, PK)']
  "username" varchar(50) [not null, note: '로그인 ID (유일, 소문자 권장)']
  "email" varchar(255) [default: NULL, note: '이메일(선택, 유일 권장)']
  "password_hash" varchar(100) [not null, note: '비밀번호 해시(예: BCrypt 60자, Argon2id 가능)']
  "password_algo" varchar(20) [not null, default: 'bcrypt', note: '해시 알고리즘(bcrypt/argon2id)']
  "is_active" tinyint [not null, default: '1', note: '활성 여부(1/0)']
  "failed_login_count" int [not null, default: '0', note: '연속 실패 횟수(>=0)']
  "locked_until" datetime [default: NULL, note: '잠금 해제 예정 시각(UTC)']
  "last_login_at" datetime [default: NULL, note: '마지막 로그인 시각(UTC)']
  "phone" varchar(30) [default: NULL, note: '연락처(선택)']
  "display_name" varchar(100) [default: NULL, note: '표시명(선택)']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    username [unique, name: "uk_user_username"]
    email [unique, name: "uk_user_email"]
  }
  Note: '사용자 마스터: 로그인/상태/보안 메타. 해시는 필수, 평문 금지(UTC).'
}

Table "tb_process" {
  "process_id" varchar(36) [pk, not null, note: '공정 ID (PK, UUID)']
  "process_name" varchar(255) [not null, note: '공정명 (유일)']
  "description" varchar(255) [default: NULL, note: '설명']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    process_name [unique, name: "uk_process_name"]
  }
  Note: '공정 마스터: 생산 라우팅의 작업 단계를 정의하는 테이블. 대표 조인: tb_equipment, tb_work_order, tb_production_performance.'
}

Table "tb_equipment" {
  "equipment_id" varchar(36) [pk, not null, note: '설비 ID (PK, UUID)']
  "equipment_name" varchar(255) [not null, note: '설비명 (유일)']
  "workcenter_id" varchar(36) [not null, note: '작업장 ID (FK)']
  "process_id" varchar(36) [not null, note: '공정 ID (FK)']
  "status_code_id" bigint [not null, note: '설비 상태 코드 ID (FK→tb_code)']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    equipment_name [unique, name: "uk_equipment_name"]
    workcenter_id [name: "idx_equipment_workcenter_id"]
    process_id [name: "idx_equipment_process_id"]
    status_code_id [name: "idx_equipment_status_code_id"]
    (process_id, workcenter_id) [name: "idx_equipment_proc_wc"]
  }
  Note: '설비 마스터: 실적/상태로그/작업지시/교대달력과 결합되는 핵심 마스터(UTC).'
}

Table "tb_work_order" {
  "work_order_id" varchar(36) [pk, not null, note: '작업 지시 ID (PK, UUID)']
  "plan_id" varchar(36) [default: NULL, note: '생산 계획 ID (FK) - 선택']
  "work_order_number" varchar(50) [not null, note: '작업 지시 번호 (유일)']
  "item_id" varchar(36) [not null, note: '생산 품목 ID (FK)']
  "process_id" varchar(36) [not null, note: '지시 공정 ID (FK)']
  "equipment_id" varchar(36) [not null, note: '지시 설비 ID (FK)']
  "order_qty" decimal(10,4) [not null, note: '지시 수량']
  "produced_qty" decimal(10,4) [not null, default: '0.0000', note: '생산 완료 수량']
  "start_ts" datetime [default: NULL, note: '지시 시작 타임스탬프 (UTC)']
  "end_ts" datetime [default: NULL, note: '지시 종료 타임스탬프 (UTC)']
  "status_code_id" bigint [not null, note: '작업 지시 상태 코드 ID (FK→tb_code)']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    work_order_number [unique, name: "uk_wo_number"]
    plan_id [name: "idx_wo_plan_id"]
    item_id [name: "idx_wo_item_id"]
    process_id [name: "idx_wo_process_id"]
    equipment_id [name: "idx_wo_equipment_id"]
    status_code_id [name: "idx_wo_status_code_id"]
    (equipment_id, start_ts) [name: "idx_wo_eqp_start"]
  }
  Note: '작업 지시: 실작업 단위. 시간/수량/상태 무결성 보강, 설비×시간 인덱스로 조회 최적화(UTC).'
}

Table "tb_production_performance" {
  "performance_id" bigint [pk, not null, increment, note: '생산 실적 ID (PK)']
  "work_order_id" varchar(36) [not null, note: '작업 지시 ID (FK)']
  "item_id" varchar(36) [not null, note: '생산 품목 ID (FK)']
  "process_id" varchar(36) [not null, note: '실적 공정 ID (FK)']
  "equipment_id" varchar(36) [not null, note: '실적 설비 ID (FK)']
  "produced_qty" decimal(10,4) [not null, note: '생산 수량(>=0)']
  "defect_qty" decimal(10,4) [not null, default: '0.0000', note: '불량 수량(>=0, 생산 수량 이내)']
  "start_time" datetime [not null, note: '작업 시작 시점 (UTC)']
  "end_time" datetime [not null, note: '작업 종료 시점 (UTC)']
  "worker_id" varchar(36) [default: NULL, note: '작업자 ID (FK→tb_user.user_id, 선택)']
  "is_deleted" tinyint [default: '0', note: '소프트삭제']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']
  "request_id" varchar(64) [default: NULL, note: 'idempotency key']

  Indexes {
    request_id [unique, name: "uk_perf_request_id"]
    work_order_id [name: "idx_perf_wo_id"]
    item_id [name: "idx_perf_item_id"]
    process_id [name: "idx_perf_process_id"]
    equipment_id [name: "idx_perf_equipment_id"]
    (equipment_id, start_time) [name: "idx_perf_equipment_start_time"]
    (work_order_id, start_time) [name: "idx_perf_wo_start"]
    worker_id [name: "idx_perf_worker"]
  }
  Note: '생산 실적: 시간/수량 무결성+설비/WO 타임라인 최적화(UTC). 작업자=tb_user FK(선택).'
}

Table "tb_equipment_status_log" {
  "log_id" bigint [pk, not null, increment, note: '로그 ID (PK)']
  "equipment_id" varchar(36) [not null, note: '설비 ID (FK)']
  "status_code_id" bigint [not null, note: '설비 상태 코드 ID (FK→tb_code)']
  "reason_code_id" bigint [default: NULL, note: '비가동 사유 코드 ID (FK→tb_code)']
  "work_order_id" varchar(36) [default: NULL, note: '관련 작업 지시 ID (FK) - 선택']
  "shift_id" bigint [default: NULL, note: '관련 교대 ID (FK) - 선택']
  "start_time" datetime [not null, note: '상태 시작 시점 (UTC)']
  "end_time" datetime [default: NULL, note: '상태 종료 시점 (UTC)']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    equipment_id [name: "idx_log_equipment_id"]
    status_code_id [name: "idx_log_status_code_id"]
    reason_code_id [name: "idx_log_reason_code_id"]
    work_order_id [name: "idx_log_wo_id"]
    shift_id [name: "idx_log_shift_id"]
    (equipment_id, start_time) [name: "idx_log_equipment_start_time"]
  }
  Note: '설비 상태 로그: 설비 가동/비가동 시계열. 시간 무결성 보장, 설비×시간 최적화(UTC).'
}

Table "tb_kpi_data" {
  "kpi_id" bigint [pk, not null, increment, note: 'KPI 기록 ID (PK)']
  "kpi_date" date [not null, note: 'KPI 기준 날짜']
  "equipment_id" varchar(36) [not null, note: '설비 ID (FK → tb_equipment)']
  "process_id" varchar(36) [not null, note: '공정 ID (FK → tb_process)']
  "item_id" varchar(36) [not null, note: '품목 ID (FK → tb_item)']
  "work_order_id" varchar(36) [default: NULL, note: '지시 ID (FK → tb_workorder)']
  "actual_oee" decimal(5,2) [not null, default: '0.00', note: '설비 종합 효율 (%) [0~100]']
  "actual_productivity" decimal(10,4) [not null, default: '0.0000', note: '시간당 생산성 (단위/시간)']
  "actual_yield" decimal(5,2) [not null, default: '0.00', note: '수율 (%) [0~100]']
  "actual_defect_rate" decimal(5,2) [not null, default: '0.00', note: '불량률 (%) [0~100]']
  "aggregation_type_id" bigint [default: NULL, note: '집계 유형 (FK → tb_code.code_id where group_code = "KPI_DATA_TYPE")']
  "batch_group_key" varchar(20) [default: NULL, note: '주기적 집계시 중복 체크용']
  "start_time" datetime [not null, note: 'KPI 집계 시작 시간']
  "end_time" datetime [not null, note: 'KPI 집계 종료 시간']
  "calc_status_code_id" bigint [default: NULL, note: '계산 상태 (FK → tb_code.code_id where group_code = "KPI_CALC_STATUS")']
  "calc_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'KPI 계산 시각']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    (kpi_date, work_order_id, equipment_id, process_id, item_id, aggregation_type_id) [unique, name: "uk_kpi_realtime"]
    (kpi_date, equipment_id, process_id, item_id, batch_group_key, aggregation_type_id) [unique, name: "uk_kpi_daily"]
    kpi_date [name: "idx_kpi_date"]
    equipment_id [name: "idx_kpi_equipment_id"]
    process_id [name: "idx_kpi_process_id"]
    item_id [name: "idx_kpi_item_id"]
    work_order_id [name: "fk_kpi_work_order"]
    aggregation_type_id [name: "fk_kpi_data_type"]
    calc_status_code_id [name: "fk_kpi_calc_status"]
  }
  Note: 'KPI 데이터: 실시간(퍼포먼스 단위) + 배치(하루 단위) 모두 대응'
}

Table "tb_shift" {
  "shift_id" bigint [pk, not null, increment, note: '교대 ID (PK)']
  "shift_code" varchar(10) [not null, note: '교대 코드 (유일, 예: A, B, C)']
  "shift_name" varchar(50) [not null, note: '교대 명칭 (예: 주간조)']
  "start_time" time [not null, note: '교대 시작 시간']
  "end_time" time [not null, note: '교대 종료 시간']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" datetime [default: NULL, note: 'UTC']
  "created_by" varchar(50) [not null]
  "created_at" datetime [not null, default: `CURRENT_TIMESTAMP`, note: 'UTC']
  "modified_by" varchar(50) [default: NULL]
  "modified_at" datetime [default: NULL, note: 'UTC']

  Indexes {
    shift_code [unique, name: "uk_shift_code"]
  }
  Note: '교대 마스터: 교대 근무의 기본 정보(코드, 시간 등)를 정의하는 테이블.'
}

Table "tb_kpi_planned_downtime" {
  "planned_downtime_id" bigint [pk, not null, increment, note: '계획된 다운타임 ID (PK)']
  "equipment_id" varchar(36) [not null, note: '설비 ID (FK)']
  "start_time" timestamp [not null]
  "end_time" timestamp [not null]
  "downtime_type_code_id" bigint [not null, note: '다운타임 유형 (예: PM, SHIFT_BREAK, HOLIDAY)']
  "description" varchar(255) [default: NULL, note: '상세 설명']
  "is_deleted" tinyint [default: '0', note: '소프트삭제 플래그']
  "deleted_at" timestamp [default: NULL]
  "created_by" varchar(50) [not null]
  "created_at" timestamp [not null, default: `CURRENT_TIMESTAMP`]
  "modified_by" varchar(50) [default: NULL]
  "modified_at" timestamp [default: NULL]
  "duration_seconds" bigint [not null, default: '0', note: '다운타임 지속 시간(초)']

  Indexes {
    (equipment_id, start_time) [name: "idx_equipment_time"]
    downtime_type_code_id [name: "fk_downtime_type"]
  }
  Note: '계획된 다운타임 기록'
}

Ref "fk_code_group_code":"tb_code_group"."group_code" < "tb_code"."group_code" [delete: restrict]

Ref "fk_workcenter_workshop":"tb_workshop"."workshop_id" < "tb_workcenter"."workshop_id" [delete: restrict]

Ref "fk_plan_item":"tb_item"."item_id" < "tb_production_plan"."item_id" [delete: restrict]

Ref "fk_eqp_proc":"tb_process"."process_id" < "tb_equipment"."process_id" [delete: restrict]

Ref "fk_eqp_status":"tb_code"."code_id" < "tb_equipment"."status_code_id" [delete: restrict]

Ref "fk_eqp_wc":"tb_workcenter"."workcenter_id" < "tb_equipment"."workcenter_id" [delete: restrict]

Ref "fk_wo_equipment":"tb_equipment"."equipment_id" < "tb_work_order"."equipment_id" [delete: restrict]

Ref "fk_wo_item":"tb_item"."item_id" < "tb_work_order"."item_id" [delete: restrict]

Ref "fk_wo_plan":"tb_production_plan"."plan_id" < "tb_work_order"."plan_id" [delete: set null]

Ref "fk_wo_process":"tb_process"."process_id" < "tb_work_order"."process_id" [delete: restrict]

Ref "fk_wo_status_code":"tb_code"."code_id" < "tb_work_order"."status_code_id" [delete: restrict]

Ref "fk_perf_equipment":"tb_equipment"."equipment_id" < "tb_production_performance"."equipment_id" [delete: restrict]

Ref "fk_perf_item":"tb_item"."item_id" < "tb_production_performance"."item_id" [delete: restrict]

Ref "fk_perf_process":"tb_process"."process_id" < "tb_production_performance"."process_id" [delete: restrict]

Ref "fk_perf_user":"tb_user"."user_id" < "tb_production_performance"."worker_id" [delete: restrict]

Ref "fk_perf_wo":"tb_work_order"."work_order_id" < "tb_production_performance"."work_order_id" [delete: restrict]

Ref "fk_log_equipment":"tb_equipment"."equipment_id" < "tb_equipment_status_log"."equipment_id" [delete: restrict]

Ref "fk_log_reason_code":"tb_code"."code_id" < "tb_equipment_status_log"."reason_code_id" [delete: set null]

Ref "fk_log_shift":"tb_shift"."shift_id" < "tb_equipment_status_log"."shift_id" [delete: set null]

Ref "fk_log_status_code":"tb_code"."code_id" < "tb_equipment_status_log"."status_code_id" [delete: restrict]

Ref "fk_log_wo":"tb_work_order"."work_order_id" < "tb_equipment_status_log"."work_order_id" [delete: set null]

Ref "fk_kpi_calc_status":"tb_code"."code_id" < "tb_kpi_data"."calc_status_code_id" [delete: restrict]

Ref "fk_kpi_data_type":"tb_code"."code_id" < "tb_kpi_data"."aggregation_type_id" [delete: restrict]

Ref "fk_kpi_equipment":"tb_equipment"."equipment_id" < "tb_kpi_data"."equipment_id" [delete: restrict]

Ref "fk_kpi_item":"tb_item"."item_id" < "tb_kpi_data"."item_id" [delete: restrict]

Ref "fk_kpi_process":"tb_process"."process_id" < "tb_kpi_data"."process_id" [delete: restrict]

Ref "fk_kpi_work_order":"tb_work_order"."work_order_id" < "tb_kpi_data"."work_order_id" [delete: restrict]

Ref "fk_downtime_equipment":"tb_equipment"."equipment_id" < "tb_kpi_planned_downtime"."equipment_id" [delete: restrict]

Ref "fk_downtime_type":"tb_code"."code_id" < "tb_kpi_planned_downtime"."downtime_type_code_id"

