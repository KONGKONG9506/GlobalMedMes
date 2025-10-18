package com.factory_dynamics.erp.erp_server.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.jdbc.JdbcTestUtils;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

// 통합 테스트임을 명시 (실제 서버가 실행되는 것처럼 테스트)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test") // 테스트 환경 설정 사용
class ErpMesIntegrationTest {

    @Autowired
    TestRestTemplate restTemplate; // ERP API 호출용

    // ERP DB 접근용 (실제는 두 개의 다른 JdbcTemplate Bean을 주입받아야 함)
    @Autowired
    JdbcTemplate erpJdbcTemplate;

    // MES DB 접근용 (실제는 두 개의 다른 JdbcTemplate Bean을 주입받아야 함)
    @Autowired
    JdbcTemplate mesJdbcTemplate;

    // --- 테스트 고정값 (Fixture) ---
    private final String PRODUCT_ID = "PROD-A";
    private final String ACTOR = "test-system";
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setup() {
        // 테스트 격리를 위한 DB 초기화 (필수)
        // JdbcTestUtils.deleteFromTables(erpJdbcTemplate, "tb_production_plan");
        // JdbcTestUtils.deleteFromTables(mesJdbcTemplate, "tb_mes_work_order", "tb_production_performance", "tb_kpi_data");
    }


    /**
     * 시나리오 1: 생산 계획 (DRAFT) -> MES 전송 (PENDING) -> 작업 지시 (P)
     * 이 테스트의 실행 시간은 '주요 시나리오 통과 시간'의 지표가 됩니다.
     */
    @Test
    void E2E_Scenario1_PlanCreationAndMesTransfer_ShouldSyncStatus() {
        // GIVEN: 100개 생산 계획 생성 (DRAFT)
        Map<String, Object> createReq = Map.of(
                "planCode", "PC-" + System.currentTimeMillis(),
                "productId", PRODUCT_ID,
                "qty", 100,
                "startDate", TODAY.toString(),
                "endDate", TODAY.plusDays(1).toString()
                // status: DRAFT로 자동 설정됨
        );

        // 1. DRAFT 계획 생성 및 planId 확보
        var planResponse = restTemplate.postForEntity("/api/plans", createReq, Map.class);
        assertEquals(200, planResponse.getStatusCodeValue());
        String planId = (String) planResponse.getBody().get("planId");
        long initialVersion = ((Number) planResponse.getBody().get("version")).longValue();

        // 2. 상태를 CONFIRMED로 변경
        Map<String, Object> confirmReq = Map.of(
                "nextStatus", "CONFIRMED",
                "actor", ACTOR,
                "version", initialVersion
        );
        var statusResponse = restTemplate.patchForObject("/api/plans/" + planId + "/status", confirmReq, Map.class);
        long confirmedVersion = ((Number) statusResponse.get("version")).longValue();
        assertEquals("CONFIRMED", statusResponse.get("status"));

        // 3. MES 전송 (ERP 상태는 PENDING, MES 작업 지시 생성 유도)
        Map<String, Object> mesReq = Map.of("modifier", ACTOR);
        var mesTransferResponse = restTemplate.postForEntity("/api/plans/" + planId + "/send-to-mes", mesReq, Map.class);
        assertEquals(200, mesTransferResponse.getStatusCodeValue()); // 성공 응답 확인

        // **자동 검증 (D): ERP 상태 PENDING 및 버전 업데이트 확인**
        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM erp_mvp.tb_production_plan WHERE plan_id = ?",
                String.class,
                planId
        );
        assertEquals("PENDING", erpStatus, "ERP 상태는 PENDING으로 동기화되어야 합니다.");

        // **자동 검증 (D): MES 작업 지시 생성 및 상태 'P' 확인**
        String mesWoStatus = mesJdbcTemplate.queryForObject(
                "SELECT status_code_id FROM mes_api.tb_work_order WHERE erp_plan_id = ?", // status 컬럼명을 status_code_id로 가정
                String.class,
                planId
        );
        assertEquals("P", mesWoStatus, "MES 작업지시 상태는 'P'여야 합니다.");

        // MES 작업 지시 ID 확보 (시나리오 2를 위해 DB 조회)
        String workOrderId = mesJdbcTemplate.queryForObject(
                "SELECT work_order_id FROM mes_api.tb_work_order WHERE erp_plan_id = ?",
                String.class,
                planId
        );
        assertNotNull(workOrderId, "MES 작업 지시 ID가 생성되어야 합니다.");

        // 이 workOrderId를 다음 테스트를 위해 저장하거나 (TestContext), 다음 테스트에서 바로 사용합니다.
    }


    /**
     * 시나리오 2: 실적 등록 (자동 완료) -> KPI 반영 -> 상태 동기화 (COMPLETED)
     * 시나리오 1이 성공적으로 실행되었다는 가정 하에 진행됩니다.
     */
    @Test
    void E2E_Scenario2_PerformanceAndAutoCompletion_ShouldUpdateKPIAndStatus() {
        // Step 0: 환경 준비 (시나리오 1 성공 후, PENDING 상태의 WO 존재)
        // 실제 통합 테스트에서는 @Test(dependsOn = "...")를 사용하거나 @BeforeEach에서 셋업 로직을 실행해야 합니다.
        // 여기서는 임시 WorkOrderId를 생성하여 시나리오를 시작합니다.

        // 가정을 위한 임시 WO 생성 (실제 테스트에서는 시나리오 1의 결과 사용)
        String testPlanId = "TEST-PLAN-2";
        String testWoId = "TEST-WO-2";
        // DB에 PENDING 상태의 plan (testPlanId)과 R 상태의 WO (testWoId)를 먼저 삽입해야 함
        // ... (DB 삽입 로직 생략) ...

        // 1. 실적 등록 API 호출 (자동 완료 조건: 수량 100/불량 5)
        Map<String, Object> performanceReq = Map.of(
                "workOrderId", testWoId,
                "itemId", PRODUCT_ID,
                "processId", "PROC-1",
                "equipmentId", "EQP-1",
                "producedQty", 100.0, // 전체 수량
                "defectQty", 5.0,     // 불량 5개
                "startTime", TODAY.atStartOfDay().atOffset(java.time.ZoneOffset.UTC).toString(),
                "endTime", TODAY.atTime(10, 0).atOffset(java.time.ZoneOffset.UTC).toString()
        );

        // 🚨 MES API 호출: POST /performances
        var perfResponse = restTemplate.postForEntity("http://mes-server/performances", performanceReq, Map.class);
        assertEquals(201, perfResponse.getStatusCodeValue());

        // **자동 검증 (D): MES 작업 완료 상태 확인**
        String mesWoStatus = mesJdbcTemplate.queryForObject(
                "SELECT status_code_id FROM mes_api.tb_work_order WHERE work_order_id = ?",
                String.class,
                testWoId
        );
        assertEquals("C", mesWoStatus, "실적 등록으로 MES 작업 지시 상태는 'C'여야 합니다.");

        // **자동 검증 (D): ERP 최종 상태 COMPLETED 확인**
        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM erp_mvp.tb_production_plan WHERE plan_id = ?",
                String.class,
                testPlanId
        );
        assertEquals("COMPLETED", erpStatus, "ERP 상태는 COMPLETED로 동기화되어야 합니다.");

        // **자동 검증 (D): KPI 데이터 반영 정확성 확인**
        Double actualDefectRate = mesJdbcTemplate.queryForObject(
                "SELECT actual_defect_rate FROM tb_kpi_data WHERE work_order_id = ?",
                Double.class,
                testWoId
        );
        // 기대 불량률: 5 / 100 = 0.05
        assertEquals(0.05, actualDefectRate, 0.001, "KPI 불량률은 0.05로 정확히 계산되어야 합니다.");
    }
}