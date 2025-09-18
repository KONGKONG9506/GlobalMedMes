# UseCases_v1 (v1 버전 B, 상세)

## UC-01 로그인/메뉴 권한
- Actor: 사용자(ROLE_OP/QA/ADMIN)
- Trigger: 로그인 화면에서 ID/비밀번호 입력
- Pre: 계정 is_active=1, locked_until 과거
- Main:
  1) /auth/login에 자격 제출 → 서버 검증(비번 해시 비교)
  2) JWT(Access 30m) + 세션(2h) 발급, 실패 카운트 리셋
  3) /menus/my로 권한 기반 메뉴 트리 로드
  4) 네비게이션 렌더 및 대시보드 이동
  5) 감사로그 AUTH_LOGIN 기록
- Alt:
  - 비번 오류: 401 + 실패 카운트+1(정책 임계 도달 시 locked_until 설정)
- Post: traceId 포함 표준 로그 기록

## UC-02 작업지시 발행/조회/상태(P→R→C)
- Actor: 작업자(ROLE_OP), 관리자(ROLE_ADMIN)
- Trigger: "지시 생성" 클릭
- Pre: ITEM/PROCESS/EQUIPMENT 존재, WO 번호 중복 없음
- Main(발행):
  1) itemId/processId/equipmentId/orderQty 입력
  2) 검증(orderQty≥0, FK 존재)
  3) TB_WORK_ORDER insert(status=P)
  4) 목록/상세 갱신
  5) 감사로그 WO_CREATE
- Main(상태변경):
  1) 상세→ "R" 또는 "C" 선택
  2) 전이표 검증(P→R, R→C만 허용)
  3) 상태 갱신 및 감사로그 WO_STATUS_CHANGE
- Alt:
  - 전이 위반: 400 WO_STATUS_INVALID
- Post: WO 상태/감사 일치

## UC-03 설비 상태 RUN 등록(교대 표시)
- Actor: 작업자(ROLE_OP)
- Trigger: "RUN 시작" 버튼
- Pre: 교대 캘린더(설비 or 작업장) 존재, XOR 충족
- Main:
  1) equipmentId, startTimeUtc 제출
  2) TB_EQUIPMENT_STATUS_LOG insert(status=RUN)
  3) 교대 뱃지 표시 및 최근 상태 카드 갱신
  4) 감사로그 EQP_STATUS_CREATE
- Alt:
  - 시간 역전 요청(end<start): 400 TIME_ORDER_INVALID
- Post: 설비×시간 인덱스 경로로 즉시 조회 가능

## UC-04 실적 등록(양품/불량, 시간)
- Actor: 작업자(ROLE_OP)
- Trigger: "실적 등록" 버튼
- Pre: 대상 WO 상태=R
- Main:
  1) produced/defect/start/end 입력
  2) 검증(defect≤produced, end≥start, FK 존재)
  3) PERF insert → 같은 Tx로 WO 누적 생산/불량 갱신
  4) 토스트/상세 반영
  5) 감사로그 PERF_CREATE
- Alt:
  - 검증 실패: 400 VALIDATION_ERROR/TIME_ORDER_INVALID
- Post: KPI 집계에 즉시 반영 가능

## UC-05 KPI(목표 vs 생산량·수율) 확인
- Actor: OP/QA/ADMIN
- Trigger: KPI 보드 진입
- Pre: KPI_TARGET 존재
- Main:
  1) kpiDate, equipmentId 필터
  2) 목표/실적 집계 조회
  3) 카드 3종(목표·실제 생산량/수율) + 색상 규칙 적용(초록/노랑/빨강)
- Alt:
  - 데이터 없음: 빈 상태 UI
- Post: EXPLAIN range 확인, SLA<500ms 충족
(Generated: 2025-08-09 10:18:30 UTC)

--- 추가

## UC-06) 실적 -> KPI연동 반영 

- 범위(MVP)
    - 실적 수집: 생산량, 불량 수량, 공정 시간, 시작/종료 시간 등 생산 활동 데이터 수집
    - KPI 정의: 생산성, 수율, OEE(설비종합효율), 가동률 등 핵심성과지표 계산 공식 정의
    - 자동 연동: 수집된 실적 데이터를 기반으로 KPI를 실시간 또는 주기적으로 자동 계산 및 업데이트
    - 지표 시각화: 계산된 KPI를 대시보드나 리포트 형태로 시각화하여 제공

- 스키마(개략)
    - production_log: 생산 실적 데이터 (생산량, 불량량 등)
    - kpi_definition: KPI 계산 공식 정의 (매개 변수, 수식 등)
    - kpi_data: 주기적으로 계산된 KPI 결과 저장

- DoD
    - 실적 데이터 입력 시 KPI 카드 자동 업데이트
    - 생산성, 수율, OEE 등 주요 KPI를 보여주는 대시보드 뷰
    - KPI 값에 대한 기간별 추이 그래프

- 리스크/대응
    - 데이터 부정확성: 실적 수집 단계에서 데이터 검증 로직 강화
    - 복잡한 KPI 계산: 초기에는 단순한 KPI부터 시작하고, 점진적으로 고도화
    - KPI 데이터 양 증가: 데이터 아카이빙 전략 및 성능 최적화

## UC-07) 장비 유지보수(CMMS 라이트) — P1

- 범위(MVP)
  - PM 계획: 장비별 주기(시간/캘린더), 도래 목록
  - 보전 WO: 생성→할당→수리→종결, 비용/소요시간(라이트)
  - 고장 접수: 설비/원인 분류(손실 트리 상위 3~5개), 증상/조치 로그
  - 지표: MTBF/MTTR, PM 준수율
- 스키마(개략)
  - cmms_pm_plan, cmms_wo(header/log), cmms_fault_log, loss_category
- DoD
  - PM 계획 뷰, 보전 WO 라이프사이클, MTBF/MTTR 카드, 권한 가드
- 리스크/대응
  - 카테고리 과다 → 5개 내로 라이트 시작

---

## UC-08) 인원관리(노무/자격 라이트) — P1

- 범위(MVP)
  - 교대/근무 캘린더(라인/작업자), 출근기록(간이)
  - 자격/스킬 매트릭스(설비·공정별 필요자격 매핑)
  - 전이 가드: R 전이 시 “해당 설비·공정 필요자격 보유자 포함 여부” 검증
- 스키마(개략)
  - hr_shift_calendar, hr_attendance, hr_skill_matrix, hr_cert
- DoD
  - R 전이 PASS/FAIL, 교대 캘린더/출근 기록, 감사로그
- 리스크/대응
  - 개인정보/민감도 → 최소 정보·권한 분리로 시작

---개인적인 정리

MES 생산 활동을 실시간으로 기록 관리 분석하는 게 핵심
1. 용어 정리
WO 생산 시작 단위
Eqp Status 설비 상태(RUN, DOWN, IDLE 등)
Perf 생산 실적(양품/불량/시간) 
KPI 수율, OEE, MTBF, MTTR 같은 지표

2.라이프사이클 이해
주문 -> 작업지시 발행 -> 설비 상태 변경 -> 실적 기록 -> KPI 집계 ->보고

추천 공부 루트
1. MES 표준 자료
ISA-95 모델 (MES 11대 기능)
스마트팩토리 추진단, 한국생산성본부의 MES 자료집

2. 제조 현장 사례
"작업지시에서 생산량을 어떻게 관리하는가"같은 실제 시나리오를 글이나 동영상으로 학습

3. 현재 프로젝트 UseCase와 매칭
UC-02 (작업지시) -> 생산계획 모듈
UC-03 (설비 상태) -> 설비관리 모듈
UC-04 (실적 등록) -> 생산관리 모듈
UC-05 (KPI) -> 경영/분석 모듈

UseCase 하나하나를 ISA-95 기능과 대응시키면서 도메인 맥락을 익히기

백엔드 관점에서 해야 할 일
API 스펙 확정:
로그인 /auth/login → JWT 발급
/menus/my → 권한 기반 메뉴 트리
/work-orders, /performances, /equipment-status 등
DTO·Validation 강화: UseCase에서 Alt 시나리오(검증 실패, 상태 전이 불가 등)를 Exception 처리로 반영
Audit Log: 감사로그 테이블에 insert 로직 공통화

프론트 관점에서 체크할 부분
UI 흐름:
로그인 → 메뉴 로딩 → 대시보드 → 개별 모듈 진입
필요한 화면 요소:
UC-02: 작업지시 목록/상세/상태 버튼
UC-03: 설비 상태 카드 + 교대 뱃지 표시
UC-04: 실적 등록 Form(양품/불량/시간)
UC-05: KPI 보드(카드 + 그래프)

React 연동:
axios/fetch로 API 호출 → React Query/Zustand로 상태 관리 → Tailwind로 UI 렌더
프론트에서 에러 메시지(400 VALIDATION_ERROR)를 잡아 토스트로 띄우기

지금 당장은 프론트 코드를 다 이해하기보다, 내 API가 프론트에서 어떻게 쓰이는지 로그 찍어가며 확인하는 게 1순위

실전 진행 전략 

1.UseCase별 “백엔드-프론트 연결표” 만들기
예: UC-04 실적 등록 → POST /performances → React form → 성공 시 KPI 카드 리프레시

2.도메인 학습은 UseCase 기반으로
“실적 등록”을 담당한다면 생산관리 쪽 도메인만 집중해서 정리

3.UI/UX는 최소 기능 위주로
관리자는 카드/리스트,
작업자는 버튼/Form,
분석가는 그래프/대시보드 → 이렇게 단순화

UI 흐름:
로그인 → 메뉴 로딩 → 대시보드 → 개별 모듈 진입
UC-02: 작업지시 목록/상세/상태 버튼
UC-03: 설비 상태 카드 + 교대 뱃지 표시
UC-04: 실적 등록 Form(양품/불량/시간)
UC-05: KPI 보드(카드 + 그래프)
