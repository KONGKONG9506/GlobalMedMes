2. 고장로그(FaultLog) 모듈
(1) 고장 등록 (Create)

의미: 설비 고장/정지 발생 시 기록 생성
API: POST /cmms/fault-logs
DB 영향: tb_cmms_fault_log에 신규 row 생성
주요 필드: fault_id, equipment_id, loss_category, occurred_at, status=OPEN

(2) 고장 조회 (Search/List)

의미: 특정 설비와 기간 내 고장 이력을 조회
API:
GET /cmms/fault-logs?equipmentId=...&from=...&to=...&page=...&size=...&sort=...


쿼리 기준:

설비(equipmentId)
발생 기간(from~to)
정렬(발생일자 오름/내림차순)
화면: 페이징 리스트
FaultLog 모듈: 등록(Create) → 조회(Search)