package com.factory_dynamics.erp.erp_server.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.containers.MySQLContainer;

import javax.sql.DataSource;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

/**
 * ERP-MES E2E 통합 테스트: CI 환경에서 Testcontainers를 사용하여 두 DB 간의 연동을 검증합니다.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Testcontainers
class ErpMesIntegrationTest {

    // --- 1. Testcontainers 설정: ERP 및 MES DB 컨테이너 정의 ---
    // MySQL 컨테이너 2개 정의 (각 DB 역할을 수행)
    @Container
    static MySQLContainer<?> erpMysqlContainer = new MySQLContainer<>("mysql:8.0.32")
            .withDatabaseName("erp_mvp")
            .withUsername("root")
            .withPassword("root");

    @Container
    static MySQLContainer<?> mesMysqlContainer = new MySQLContainer<>("mysql:8.0.32")
            .withDatabaseName("mes_api_db")
            .withUsername("mes_user")
            .withPassword("mes_password");

    // --- 2. 동적 속성 주입 (ConnectException 해결의 핵심) ---

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        // ERP DataSource (Spring Boot 주 DB) 설정 주입
        registry.add("spring.datasource.url", erpMysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", erpMysqlContainer::getUsername);
        registry.add("spring.datasource.password", erpMysqlContainer::getPassword);
        registry.add("spring.flyway.url", erpMysqlContainer::getJdbcUrl);

        // MES DB 연결 설정 (두 번째 DataSource 설정을 위한 환경 변수 주입)
        registry.add("mes.datasource.url", mesMysqlContainer::getJdbcUrl);
        registry.add("mes.datasource.username", mesMysqlContainer::getUsername);
        registry.add("mes.datasource.password", mesMysqlContainer::getPassword);

        // MES API 주소 Mocking 주입 (CI 환경에서 외부 호출 방지)
        registry.add("mes.api.base-url", () -> "http://localhost:8080");
    }

    // --- 3. 테스트 설정 클래스: 두 개의 DataSource 및 JdbcTemplate Bean 생성 ---

    @TestConfiguration
    static class TestJdbcConfig {

        // MES DB 연결 정보를 환경 변수에서 @Value로 주입받음 (DynamicPropertySource가 주입한 값)
        @Value("${mes.datasource.url}")
        private String mesDbUrl;
        @Value("${mes.datasource.username}")
        private String mesDbUsername;
        @Value("${mes.datasource.password}")
        private String mesDbPassword;

        // ERP DataSource Bean (Spring Boot 주 DataSource - Primary)
        // Spring이 기본적으로 생성한 DataSource (ERP DB에 연결됨)를 사용합니다.
        @Bean
        @Qualifier("erpJdbcTemplate")
        public JdbcTemplate erpJdbcTemplate(DataSource dataSource) {
            return new JdbcTemplate(dataSource);
        }

        // MES DataSource Bean (별도로 생성)
        @Bean
        @Qualifier("mesDataSource")
        public DataSource mesDataSource() {
            DriverManagerDataSource dataSource = new DriverManagerDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(mesDbUrl);
            dataSource.setUsername(mesDbUsername);
            dataSource.setPassword(mesDbPassword);
            return dataSource;
        }

        // MES JdbcTemplate Bean
        @Bean
        @Qualifier("mesJdbcTemplate")
        public JdbcTemplate mesJdbcTemplate(@Qualifier("mesDataSource") DataSource mesDataSource) {
            return new JdbcTemplate(mesDataSource);
        }
    }

    // --- 4. 테스트 필드 주입 및 로직 ---

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("erpJdbcTemplate")
    JdbcTemplate erpJdbcTemplate;

    @Autowired
    @Qualifier("mesJdbcTemplate")
    JdbcTemplate mesJdbcTemplate;

    // --- 테스트 고정값 (Fixture) ---
    private final String PRODUCT_ID = "PROD-A";
    private final String ACTOR = "test-system";
    private final LocalDate TODAY = LocalDate.now();

    @BeforeEach
    void setup() {
        // 여기에 테스트 격리를 위한 DB 초기화 로직을 추가하세요.
    }


    /**
     * 시나리오 1: 생산 계획 (DRAFT) -> MES 전송 (PENDING) -> 작업 지시 (P)
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
        assertEquals("CONFIRMED", statusResponse.get("status"));

        // 3. MES 전송 (ERP 상태는 PENDING, MES 작업 지시 생성 유도)
        Map<String, Object> mesReq = Map.of("modifier", ACTOR);
        var mesTransferResponse = restTemplate.postForEntity("/api/plans/" + planId + "/send-to-mes", mesReq, Map.class);
        assertEquals(200, mesTransferResponse.getStatusCodeValue());

        // **자동 검증 (D): ERP 상태 PENDING 확인**
        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM tb_production_plan WHERE plan_id = ?",
                String.class,
                planId
        );
        assertEquals("PENDING", erpStatus, "ERP 상태는 PENDING으로 동기화되어야 합니다.");

        // **자동 검증 (D): MES 작업 지시 생성 및 상태 'P' 확인**
        String mesWoStatus = mesJdbcTemplate.queryForObject(
                "SELECT status_code_id FROM tb_work_order WHERE erp_plan_id = ?",
                String.class,
                planId
        );
        assertEquals("P", mesWoStatus, "MES 작업지시 상태는 'P'여야 합니다.");

        // MES 작업 지시 ID 확보
        String workOrderId = mesJdbcTemplate.queryForObject(
                "SELECT work_order_id FROM tb_work_order WHERE erp_plan_id = ?",
                String.class,
                planId
        );
        assertNotNull(workOrderId, "MES 작업 지시 ID가 생성되어야 합니다.");
    }


    /**
     * 시나리오 2: 실적 등록 (자동 완료) -> KPI 반영 -> 상태 동기화 (COMPLETED)
     */
    @Test
    void E2E_Scenario2_PerformanceAndAutoCompletion_ShouldUpdateKPIAndStatus() {
        // Step 0: 가정을 위한 임시 WO 생성 및 DB 삽입 (실제는 시나리오 1 결과 사용)
        String testPlanId = "TEST-PLAN-2";
        String testWoId = "TEST-WO-2";

        // 🚨 테스트 격리를 위해 이 testPlanId와 testWoId에 해당하는 데이터를
        // BeforeEach나 이 테스트 시작 시 DB에 미리 삽입하는 로직이 필요합니다.

        // 1. 실적 등록 API 호출
        Map<String, Object> performanceReq = Map.of(
                "workOrderId", testWoId,
                "producedQty", 100.0,
                "defectQty", 5.0,
                "startTime", TODAY.atStartOfDay().atOffset(java.time.ZoneOffset.UTC).toString(),
                "endTime", TODAY.atTime(10, 0).atOffset(java.time.ZoneOffset.UTC).toString()
        );

        // 🚨 이 호출은 ERP 서버의 엔드포인트를 호출하는 것으로 가정합니다.
        var perfResponse = restTemplate.postForEntity("/api/performances", performanceReq, Map.class);
        assertEquals(201, perfResponse.getStatusCodeValue());

        // **자동 검증 (D): MES 작업 완료 상태 확인**
        String mesWoStatus = mesJdbcTemplate.queryForObject(
                "SELECT status_code_id FROM tb_work_order WHERE work_order_id = ?",
                String.class,
                testWoId
        );
        assertEquals("C", mesWoStatus, "실적 등록으로 MES 작업 지시 상태는 'C'여야 합니다.");

        // **자동 검증 (D): ERP 최종 상태 COMPLETED 확인**
        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM tb_production_plan WHERE plan_id = ?",
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
        assertEquals(0.05, actualDefectRate, 0.001, "KPI 불량률은 0.05로 정확히 계산되어야 합니다.");
    }
}
