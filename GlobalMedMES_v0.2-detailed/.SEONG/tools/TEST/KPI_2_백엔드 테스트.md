전이가드

workorder를 P에서 R로 바꾸려 할때 
-해당 시간에 담당하는 직원이 없을경우 에러
 NO_WORKER_ASSIGNED 발생
해당 시간에 담당하는 직원이 2명 이상일 경우 2명 모두 자격증을 체크하여 한명이라도 있으면 통과함
-해당 공정에 자격이 필요한데 assignment를 통해 배치한 직원에게 자격이 없을 경우 에러
 WO_CERT_INVALID 발생

작업 배치
calender를 통해 작업을 배치할 날짜, 설비, 작업장 필요
- 현재 과거에 날짜를 만들 수 있음
- 해당 설비가 없을 경우 NOT_FOUND 에러 발생
- 해당 설비와 작업장이 일치하지 않을 경우 WC_MISMATCH 에러 발생
전부 알맞게 적을 경우 주간조,전반야,후반야 3개의 캘린더 ID를 생성
- 세개의 start,end time은 KST 기준이며 startTs,endTs가 실제 UTS 기준임

assignment를 통해 calender에 직원 배치
- 해당 캘린더 아이디가 없을경우 NOT_FOUND 에러 발생
- 한 캘린더 아이디에 여러명의 직원 배치 가능(인턴쉽 등)
- 직원 아이디가 틀릴경우 WORKER_NOT_FOUND 에러 발생

- 같은 캘린더 아이디에 같은 직원을 배치 할 경우 DUPLICATE_EMPLOYEE 에러 발생

- 같은 직원을 같은 날짜의 다른 캘린더에 배치 가능(특수한 이유{직원 휴가, 병가 등으로 인한 연장근무})

- 직원 배치할때 해당 설비 사용 자격이 없을 경우 WORKER_NO_CERT_FOR_EQUIPMENT 발생


get 작성


직원 목록
-상세 목록 나중에
-