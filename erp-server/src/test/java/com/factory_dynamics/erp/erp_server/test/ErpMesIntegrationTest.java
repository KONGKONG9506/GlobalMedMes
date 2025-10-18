package com.factory_dynamics.erp.erp_server.test;
import com.factory_dynamics.erp.erp_server.ErpServerApplication;
import com.factory_dynamics.erp.erp_server.mes_adapter.api.MesApiClient;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test") // application-test.yml 로드
@SpringBootTest(classes = {ErpServerApplication.class, ErpMesIntegrationTest.TestJdbcConfig.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ErpMesIntegrationTest {

    // ----------------------------------------------------------------------
    // 1. Testcontainers 설정
    // ----------------------------------------------------------------------

    // ERP DB (주요 DataSource)
    @Container
    static MySQLContainer<?> erpMysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
            .withDatabaseName("erp_db")
            .withUsername("erp_user")
            .withPassword("erp_pass")
            .withReuse(true); // 컨테이너 재사용 설정

    // MES DB (보조 DataSource)
    @Container
    static MySQLContainer<?> mesMysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
            .withDatabaseName("mes_db")
            .withUsername("mes_user")
            .withPassword("mes_pass")
            .withReuse(true); // 컨테이너 재사용 설정

    // ----------------------------------------------------------------------
    // 2. 동적 속성 주입 (Spring <-> Testcontainers 연결)
    // ----------------------------------------------------------------------

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        // ERP DataSource (Spring Boot 주 DB) 설정 주입
        registry.add("spring.datasource.url", erpMysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", erpMysqlContainer::getUsername);
        registry.add("spring.datasource.password", erpMysqlContainer::getPassword);

        // MES DataSource (보조 DB) 설정 주입
        registry.add("mes.datasource.url", mesMysqlContainer::getJdbcUrl);
        registry.add("mes.datasource.username", mesMysqlContainer::getUsername);
        registry.add("mes.datasource.password", mesMysqlContainer::getPassword);

        // MES API 호출 URL (테스트 환경에서는 WebClient가 사용하지 않으나, 설정은 필요)
        registry.add("mes.api.base-url", () -> "http://localhost:8080");
    }

    // ----------------------------------------------------------------------
    // 3. 의존성 주입 및 Mocking 설정
    // ----------------------------------------------------------------------

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate erpJdbcTemplate; // ERP DB 접근용

    @Autowired
    @Qualifier("mesJdbcTemplate")
    private JdbcTemplate mesJdbcTemplate; // MES DB 접근용

    // 🚨 MES API 클라이언트를 Mocking하여 실제 HTTP 통신 실패 방지
    @MockBean
    private MesApiClient mesApiClient;

    // ----------------------------------------------------------------------
    // 4. 테스트 환경 설정 (DataSource Bean 정의)
    // ----------------------------------------------------------------------

    @TestConfiguration
    public static class TestJdbcConfig {

        // ERP DB 연결 정보 (Primary DataSource) - @Value를 통해 동적 주입된 속성 사용
        @Value("${spring.datasource.url}")
        private String erpDbUrl;
        @Value("${spring.datasource.username}")
        private String erpDbUsername;
        @Value("${spring.datasource.password}")
        private String erpDbPassword;

        // MES DB 연결 정보 (Secondary DataSource) - @Value를 통해 동적 주입된 속성 사용
        @Value("${mes.datasource.url}")
        private String mesDbUrl;
        @Value("${mes.datasource.username}")
        private String mesDbUsername;
        @Value("${mes.datasource.password}")
        private String mesDbPassword;

        // ERP DataSource Bean (Primary로 등록)
        @Bean
        @Primary
        public DataSource erpDataSource() {
            org.apache.commons.dbcp2.BasicDataSource dataSource = new org.apache.commons.dbcp2.BasicDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(erpDbUrl);
            dataSource.setUsername(erpDbUsername);
            dataSource.setPassword(erpDbPassword);
            return dataSource;
        }

        // ERP JdbcTemplate Bean
        @Bean
        @Primary
        public JdbcTemplate erpJdbcTemplate(DataSource erpDataSource) {
            return new JdbcTemplate(erpDataSource);
        }

        // MES DataSource Bean
        @Bean
        @Qualifier("mesDataSource")
        public DataSource mesDataSource() {
            org.apache.commons.dbcp2.BasicDataSource dataSource = new org.apache.commons.dbcp2.BasicDataSource();
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

    // ----------------------------------------------------------------------
    // 5. 테스트 전/후 처리 및 격리 설정
    // ----------------------------------------------------------------------

    /**
     * 🚨 Scenario 2를 위한 테스트 격리 및 Fixture 데이터 설정
     * Scenario 2는 'CONFIRMED'된 계획과 'WORK_ORDER'가 이미 존재한다고 가정합니다.
     */
    @BeforeEach
    void setupFixtures() {
        // --- ERP DB 초기화 및 Fixture (Scenario 2용) ---
        // 기존 데이터를 안전하게 삭제 (ER_PLAN_002, ER_ITEM_002)
        erpJdbcTemplate.update("DELETE FROM production_plan WHERE id IN ('TEST-PLAN-2')");
        erpJdbcTemplate.update("DELETE FROM item WHERE id IN ('TEST-ITEM-2')");

        // ITEM 데이터 삽입 (Scenario 2 의존성)
        erpJdbcTemplate.update("INSERT INTO item (id, name, created_at, created_by) VALUES (?, ?, NOW(), 'test_system')",
                "TEST-ITEM-2", "품목-002");

        // PRODUCTION_PLAN 데이터 삽입 (Scenario 2 의존성) - CONFIRMED 상태
        erpJdbcTemplate.update("INSERT INTO production_plan (id, item_id, status, plan_quantity, created_at, created_by) VALUES (?, ?, ?, ?, NOW(), 'test_system')",
                "TEST-PLAN-2", "TEST-ITEM-2", "CONFIRMED", 100);

        // --- MES DB 초기화 및 Fixture (Scenario 2용) ---
        // 기존 데이터를 안전하게 삭제 (WO-002)
        mesJdbcTemplate.update("DELETE FROM tb_work_order WHERE work_order_id IN ('TEST-WO-2')");
        mesJdbcTemplate.update("DELETE FROM tb_work_order WHERE erp_plan_id IN ('TEST-PLAN-1')"); // Scenario 1 잔여 데이터 정리

        // WORK_ORDER 데이터 삽입 (Scenario 2 의존성) - MES에 이미 전송된 상태 가정
        mesJdbcTemplate.update("INSERT INTO tb_work_order (work_order_id, erp_plan_id, item_id, quantity, work_status, created_at) VALUES (?, ?, ?, ?, ?, NOW())",
                "TEST-WO-2", "TEST-PLAN-2", "TEST-ITEM-2", 100, "WAITING");
    }

    @AfterAll
    void cleanup() {
        // 컨테이너는 withReuse(true)로 설정했으므로 명시적인 stop()은 필요하지 않음
    }

    // ----------------------------------------------------------------------
    // 6. E2E 통합 시나리오 테스트
    // ----------------------------------------------------------------------

    /**
     * 시나리오 1: 생산 계획 생성 -> 확정 -> MES 전송 (ERP 상태 PENDING 동기화)
     */
    @Test
    void E2E_Scenario1_PlanCreationAndMesTransfer_ShouldSyncStatus() {
        // GIVEN: 새로운 생산 계획 생성
        String planId = "TEST-PLAN-1";
        Map<String, Object> createReq = Map.of(
                "id", planId,
                "itemId", "ITEM-001", // 가상의 아이템 ID
                "planQuantity", 50,
                "status", "DRAFT"
        );
        restTemplate.postForEntity("/api/plans", createReq, Map.class);

        // WHEN-1: 계획 상태를 DRAFT -> CONFIRMED로 변경 (MES 전송 가능 상태)
        Map<String, String> statusReq = Map.of("newStatus", "CONFIRMED");
        restTemplate.patchForObject("/api/plans/" + planId + "/status", statusReq, Map.class);

        // 🚨 WHEN-2: MES 전송 API 호출 (고객님께서 확인해주신 POST /api/plans/{planId}/send-to-mes 엔드포인트 사용)
        // MesApiClient Mocking 설정: 실제 MES 호출 시 성공 응답 반환 강제
        Mockito.when(mesApiClient.sendProductionPlan(Mockito.any()))
                .thenReturn(Mono.just("Success"));

        Map<String, String> mesReq = Map.of("modifier", "system");

        // 🚨 POST /api/plans/{planId}/send-to-mes 호출
        var mesTransferResponse = restTemplate.postForEntity("/api/plans/" + planId + "/send-to-mes", mesReq, Map.class);

        // THEN-1: MES 전송 API 호출 성공 확인 (2xx 응답)
        assertThat(mesTransferResponse.getStatusCode().is2xxSuccessful()).isTrue();

        // THEN-2: ERP DB의 Plan 상태가 PENDING으로 변경되었는지 확인
        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM production_plan WHERE id = ?",
                String.class,
                planId);

        assertThat(erpStatus).isEqualTo("PENDING");

        // THEN-3: MES DB에 Work Order가 생성되었는지 확인 (MES 전송 로직의 결과)
        Integer mesWorkOrderCount = mesJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_work_order WHERE erp_plan_id = ?",
                Integer.class,
                planId);

        assertThat(mesWorkOrderCount).isEqualTo(1);
    }

    /**
     * 시나리오 2: MES 작업 실적 등록 -> ERP KPI 업데이트 및 상태 동기화
     */
    @Test
    void E2E_Scenario2_PerformanceAndAutoCompletion_ShouldUpdateKPIAndStatus() {
        // GIVEN: @BeforeEach에서 TEST-PLAN-2 (CONFIRMED)와 TEST-WO-2 (WAITING) 데이터가 미리 준비됨

        String planId = "TEST-PLAN-2";
        String workOrderId = "TEST-WO-2";

        // WHEN-1: 실적 등록 (MES 작업 완료를 가정)
        Map<String, Object> performanceReq = Map.of(
                "workOrderId", workOrderId,
                "actualQuantity", 100, // 계획 수량과 동일
                "modifier", "mes_system"
        );

        // 🚨 POST /api/performances 호출 (ERP에서 MES 실적을 접수하는 API)
        var perfResponse = restTemplate.postForEntity("/api/performances", performanceReq, Map.class);

        // THEN-1: 실적 등록 API 호출 성공 확인
        // 고객님 의견에 따라 신규 리소스 생성(실적)은 201 Created를 기대합니다.
        assertThat(perfResponse.getStatusCode().value()).isEqualTo(201);

        // THEN-2: MES DB의 Work Order 상태가 COMPLETED로 변경되었는지 확인
        String mesStatus = mesJdbcTemplate.queryForObject(
                "SELECT work_status FROM tb_work_order WHERE work_order_id = ?",
                String.class,
                workOrderId);

        assertThat(mesStatus).isEqualTo("COMPLETED");

        // THEN-3: ERP DB의 Plan 상태가 COMPLETED로 변경되었는지 확인 (자동 완료 로직 검증)
        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM production_plan WHERE id = ?",
                String.class,
                planId);

        assertThat(erpStatus).isEqualTo("COMPLETED");

        // THEN-4: ERP DB의 Plan의 KPI(실적 수량)가 업데이트되었는지 확인
        Integer actualQuantity = erpJdbcTemplate.queryForObject(
                "SELECT actual_quantity FROM production_plan WHERE id = ?",
                Integer.class,
                planId);

        assertThat(actualQuantity).isEqualTo(100);
    }
}
