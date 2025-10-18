package com.factory_dynamics.erp.erp_server.test;

import com.factory_dynamics.erp.erp_server.ErpServerApplication;
import com.factory_dynamics.erp.erp_server.mes_adapter.api.MesApiClient;
import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import reactor.core.publisher.Mono;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@ActiveProfiles("test")
@EnableAutoConfiguration(exclude = {
        DataSourceAutoConfiguration.class,
        HibernateJpaAutoConfiguration.class
})
@SpringBootTest(
        classes = {ErpServerApplication.class},
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ErpMesIntegrationTest {

    // ----------------------------------------------------------------------
    // 1. Testcontainers 설정
    // ----------------------------------------------------------------------

    @Container
    static MySQLContainer<?> erpMysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
            .withDatabaseName("erp_db")
            .withUsername("erp_user")
            .withPassword("erp_pass")
            .withReuse(true);

    @Container
    static MySQLContainer<?> mesMysqlContainer = new MySQLContainer<>(DockerImageName.parse("mysql:8.0.32"))
            .withDatabaseName("mes_db")
            .withUsername("mes_user")
            .withPassword("mes_pass")
            .withReuse(true);

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", erpMysqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", erpMysqlContainer::getUsername);
        registry.add("spring.datasource.password", erpMysqlContainer::getPassword);

        registry.add("mes.datasource.url", mesMysqlContainer::getJdbcUrl);
        registry.add("mes.datasource.username", mesMysqlContainer::getUsername);
        registry.add("mes.datasource.password", mesMysqlContainer::getPassword);

        registry.add("mes.api.base-url", () -> "http://localhost:8080");
    }

    @Autowired
    private JdbcTemplate erpJdbcTemplate;

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("mesJdbcTemplate")
    private JdbcTemplate mesJdbcTemplate;

    @MockBean
    private MesApiClient mesApiClient;

    @MockBean
    private EntityManagerFactory entityManagerFactory;

    @MockBean
    private JpaTransactionManager jpaTransactionManager;

    // ----------------------------------------------------------------------
    // 2. DDL 자동 생성 (테이블 구조 미리 생성)
    // ----------------------------------------------------------------------

    @BeforeAll
    void initSchema() {
        // ERP DB 테이블 생성
        erpJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS item (
                    id VARCHAR(50) PRIMARY KEY,
                    name VARCHAR(100),
                    created_at DATETIME,
                    created_by VARCHAR(50)
                );
                """);

        erpJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS production_plan (
                    id VARCHAR(50) PRIMARY KEY,
                    item_id VARCHAR(50),
                    status VARCHAR(50),
                    plan_quantity INT,
                    actual_quantity INT DEFAULT 0,
                    created_at DATETIME,
                    created_by VARCHAR(50),
                    FOREIGN KEY (item_id) REFERENCES item(id)
                );
                """);

        // MES DB 테이블 생성
        mesJdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS tb_work_order (
                    work_order_id VARCHAR(50) PRIMARY KEY,
                    erp_plan_id VARCHAR(50),
                    item_id VARCHAR(50),
                    quantity INT,
                    work_status VARCHAR(50),
                    created_at DATETIME
                );
                """);
    }

    // ----------------------------------------------------------------------
    // 3. 테스트 환경 설정용 @TestConfiguration
    // ----------------------------------------------------------------------

    @TestConfiguration
    public static class TestJdbcConfig {

        @Value("${spring.datasource.url}")
        private String erpDbUrl;
        @Value("${spring.datasource.username}")
        private String erpDbUsername;
        @Value("${spring.datasource.password}")
        private String erpDbPassword;

        @Value("${mes.datasource.url}")
        private String mesDbUrl;
        @Value("${mes.datasource.username}")
        private String mesDbUsername;
        @Value("${mes.datasource.password}")
        private String mesDbPassword;

        @Bean
        @Primary
        public DataSource erpDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(erpDbUrl);
            dataSource.setUsername(erpDbUsername);
            dataSource.setPassword(erpDbPassword);
            return dataSource;
        }

        @Bean
        public JdbcTemplate erpJdbcTemplate(DataSource erpDataSource) {
            return new JdbcTemplate(erpDataSource);
        }

        @Bean
        public DataSourceTransactionManager transactionManager(DataSource erpDataSource) {
            return new DataSourceTransactionManager(erpDataSource);
        }

        @Bean
        @Qualifier("mesDataSource")
        public DataSource mesDataSource() {
            BasicDataSource dataSource = new BasicDataSource();
            dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
            dataSource.setUrl(mesDbUrl);
            dataSource.setUsername(mesDbUsername);
            dataSource.setPassword(mesDbPassword);
            return dataSource;
        }

        @Bean
        @Qualifier("mesJdbcTemplate")
        public JdbcTemplate mesJdbcTemplate(@Qualifier("mesDataSource") DataSource mesDataSource) {
            return new JdbcTemplate(mesDataSource);
        }
    }

    // ----------------------------------------------------------------------
    // 4. 테스트 전 Fixture 세팅
    // ----------------------------------------------------------------------

    @BeforeEach
    void setupFixtures() {
        erpJdbcTemplate.update("DELETE FROM production_plan WHERE id IN ('TEST-PLAN-2')");
        erpJdbcTemplate.update("DELETE FROM item WHERE id IN ('TEST-ITEM-2')");

        erpJdbcTemplate.update("INSERT INTO item (id, name, created_at, created_by) VALUES (?, ?, NOW(), 'test_system')",
                "TEST-ITEM-2", "품목-002");

        erpJdbcTemplate.update("INSERT INTO production_plan (id, item_id, status, plan_quantity, created_at, created_by) VALUES (?, ?, ?, ?, NOW(), 'test_system')",
                "TEST-PLAN-2", "TEST-ITEM-2", "CONFIRMED", 100);

        mesJdbcTemplate.update("DELETE FROM tb_work_order WHERE work_order_id IN ('TEST-WO-2')");
        mesJdbcTemplate.update("DELETE FROM tb_work_order WHERE erp_plan_id IN ('TEST-PLAN-1')");

        mesJdbcTemplate.update("INSERT INTO tb_work_order (work_order_id, erp_plan_id, item_id, quantity, work_status, created_at) VALUES (?, ?, ?, ?, ?, NOW())",
                "TEST-WO-2", "TEST-PLAN-2", "TEST-ITEM-2", 100, "WAITING");
    }

    @AfterAll
    void cleanup() {
        // 컨테이너 자동 종료 방지 (reuse=true)
    }

    // ----------------------------------------------------------------------
    // 5. 통합 테스트 시나리오
    // ----------------------------------------------------------------------

    @Test
    void E2E_Scenario1_PlanCreationAndMesTransfer_ShouldSyncStatus() {
        String planId = "TEST-PLAN-1";
        Map<String, Object> createReq = Map.of(
                "id", planId,
                "itemId", "ITEM-001",
                "planQuantity", 50,
                "status", "DRAFT"
        );
        restTemplate.postForEntity("/api/plans", createReq, Map.class);

        Map<String, String> statusReq = Map.of("newStatus", "CONFIRMED");
        restTemplate.patchForObject("/api/plans/" + planId + "/status", statusReq, Map.class);

        Mockito.when(mesApiClient.sendProductionPlan(Mockito.any()))
                .thenReturn(Mono.just("Success"));

        Map<String, String> mesReq = Map.of("modifier", "system");

        var mesTransferResponse = restTemplate.postForEntity("/api/plans/" + planId + "/send-to-mes", mesReq, Map.class);
        assertThat(mesTransferResponse.getStatusCode().is2xxSuccessful()).isTrue();

        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM production_plan WHERE id = ?",
                String.class, planId);
        assertThat(erpStatus).isEqualTo("PENDING");

        Integer mesWorkOrderCount = mesJdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM tb_work_order WHERE erp_plan_id = ?",
                Integer.class, planId);
        assertThat(mesWorkOrderCount).isEqualTo(1);
    }

    @Test
    void E2E_Scenario2_PerformanceAndAutoCompletion_ShouldUpdateKPIAndStatus() {
        String planId = "TEST-PLAN-2";
        String workOrderId = "TEST-WO-2";

        Map<String, Object> performanceReq = Map.of(
                "workOrderId", workOrderId,
                "actualQuantity", 100,
                "modifier", "mes_system"
        );

        var perfResponse = restTemplate.postForEntity("/api/performances", performanceReq, Map.class);
        assertThat(perfResponse.getStatusCode().value()).isEqualTo(201);

        String mesStatus = mesJdbcTemplate.queryForObject(
                "SELECT work_status FROM tb_work_order WHERE work_order_id = ?",
                String.class, workOrderId);
        assertThat(mesStatus).isEqualTo("COMPLETED");

        String erpStatus = erpJdbcTemplate.queryForObject(
                "SELECT status FROM production_plan WHERE id = ?",
                String.class, planId);
        assertThat(erpStatus).isEqualTo("COMPLETED");

        Integer actualQuantity = erpJdbcTemplate.queryForObject(
                "SELECT actual_quantity FROM production_plan WHERE id = ?",
                Integer.class, planId);
        assertThat(actualQuantity).isEqualTo(100);
    }
}
