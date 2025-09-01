package com.globalmed.mes.mes_api.kpi;
import com.globalmed.mes.mes_api.kpi.domain.KpiDataEntity;
import com.globalmed.mes.mes_api.kpi.service.KpiDataService;
import com.globalmed.mes.mes_api.performance.domain.ProductionPerformanceEntity;
import com.globalmed.mes.mes_api.performance.service.PerformanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@Service
@RequiredArgsConstructor
public class KpiBatchScheduler {

    private final KpiDataService kpiDataService;
    /**
     * 매일 새벽 1시에 배치 KPI 계산 및 저장
     */
    @Scheduled(cron = "0 0 1 * * ?")
    public void runDailyBatchKpi() {
        // 배치 작업은 일반적으로 전날 데이터를 처리합니다.
        LocalDate yesterdayUtc = LocalDate.now(ZoneOffset.UTC).minusDays(1);
        kpiDataService.runDailyBatchKpiCalculation(yesterdayUtc);
    }
}
