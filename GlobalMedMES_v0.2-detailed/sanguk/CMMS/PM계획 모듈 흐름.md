PM 계획(Preventive Maintenance) 모듈 흐름
1) 계획 등록 (Create)

누가/언제 어떤 설비를 몇 주기로 점검할지 등록.

API: POST /api/cmms/pm-plans

DB tb_cmms_pm_plan에 한 로우 생성:
equipment_id, task_name, cycle_type(HOURS/DAYS/CALENDAR), cycle_value, next_due_at, (last_done_at=NULL), status='ACTIVE'

2) 도래 조회 (Due List)

지금~특정 시점까지 도래 예정인 계획을 조회해 작업 스케줄링.

API: GET /api/cmms/pm-plans/due?to=...&equipmentId=...

쿼리 기준: next_due_at <= :to (+설비 필터)

화면: “도래 목록(가까운 순)” 정렬.

3) 실행/완료 기록 → 다음 도래 자동 계산 (Roll)

작업자가 PM을 수행하고 완료 시각을 보내면, 마지막 완료시각과 다음 도래시각을 업데이트.

API: POST /api/cmms/pm-plans/{id}/done?doneAt=...

로직: last_done_at = doneAt → 주기만큼 더해서 next_due_at 다시 산출.
(예: DAYS=7 → doneAt + 7일, HOURS=500 → doneAt + 500시간)

선택: PM이 실제로 보전 WO로 실행되도록 만들고 싶으면, 도래 목록에서 WO 생성 버튼/자동생성 잡을 붙이면 됨. (지금 구조에 쉽게 추가 가능)