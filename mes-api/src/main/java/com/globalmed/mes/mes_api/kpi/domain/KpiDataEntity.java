package com.globalmed.mes.mes_api.kpi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_kpi_data",
        uniqueConstraints = {
                // 실시간 KPI: performance_id 기준
                @UniqueConstraint(name = "uk_kpi_realtime", columnNames = {"kpi_date", "performance_id"}),
                // 배치 KPI: performance_id 없이 equipment/process/item/aggregation_type 기준
                @UniqueConstraint(name = "uk_kpi_daily", columnNames = {"kpi_date", "equipment_id", "process_id", "item_id", "aggregation_type"})
        })
@Getter
@Setter
public class KpiDataEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "kpi_id")
    private Long kpiId;

    @Column(name = "kpi_date", nullable = false)
    private LocalDate kpiDate;

    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "process_id", length = 36, nullable = false)
    private String processId;

    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    // 실시간 KPI용
    @Column(name = "performance_id")
    private Long performanceId;

    @Column(name = "actual_oee", precision = 5, scale = 2, nullable = false)
    private BigDecimal actualOee = BigDecimal.ZERO;

    @Column(name = "actual_productivity", precision = 10, scale = 4, nullable = false)
    private BigDecimal actualProductivity = BigDecimal.ZERO;

    @Column(name = "actual_yield", precision = 5, scale = 2, nullable = false)
    private BigDecimal actualYield = BigDecimal.ZERO;

    @Column(name = "actual_defect_rate", precision = 5, scale = 2, nullable = false)
    private BigDecimal actualDefectRate = BigDecimal.ZERO;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    // 새로 추가된 컬럼
    @Column(name = "aggregation_type", length = 20, nullable = false)
    private String aggregationType;

    @Column(name = "calc_status", nullable = false)
    private Byte calcStatus = 1; // 0=FAIL, 1=SUCCESS, 2=IN_PROGRESS, 3=RETRY

    @Column(name = "calc_at", nullable = false)
    private LocalDateTime calcAt = LocalDateTime.now();

    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    @PrePersist
    void prePersist() {
        if (createdBy == null || createdBy.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            createdBy = (auth != null && auth.isAuthenticated())
                    ? String.valueOf(auth.getPrincipal())
                    : "system";
        }
        if (startTime == null) startTime = LocalDateTime.now();
        if (endTime == null) endTime = LocalDateTime.now();
        if (calcAt == null) calcAt = LocalDateTime.now();
    }
}
