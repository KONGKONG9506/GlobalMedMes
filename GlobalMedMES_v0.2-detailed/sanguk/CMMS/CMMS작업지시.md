1. 작업지시(WorkOrder) 모듈
(1) 작업지시 등록 (Create)

의미: 특정 설비/공정/품목에 대해 작업지시 생성
API: POST /cmms/work-orders
DB 영향: tb_cmms_work_order에 신규 row 생성
주요 필드: work_order_id, item_id, process_id, equipment_id, order_qty, status=OPEN, created_by

(2) 작업자 배정 (Assign)

의미: 생성된 작업지시에 작업자(담당자)를 할당
API: POST /cmms/work-orders/{id}/assign
로직: 배정 정보 기록 후 status = ASSIGNED

(3) 작업 시작 (Start)

의미: 배정된 작업지시를 실제 작업 시작 상태로 변경
API: POST /cmms/work-orders/{id}/start
로직: 시작 시각 기록, status = IN_PROGRESS

(4) 작업 완료 (Complete)

의미: 작업지시를 완료 상태로 마무리
API: POST /cmms/work-orders/{id}/complete
로직: 완료 시각 기록, status = DONE

(5) 작업지시 조회 (Search/List)

의미: 조건(상태, 설비) 기반으로 작업지시 목록을 조회
API: GET /cmms/work-orders?status=...&equipmentId=...&page=...&size=...&sort=...
쿼리 기준: 상태(status), 설비(equipmentId), 정렬(createdAt desc 등)
화면: 페이징 리스트

WorkOrder 모듈: 등록(Create) → 배정(Assign) → 시작(Start) → 완료(Complete) → 조회(Search)
