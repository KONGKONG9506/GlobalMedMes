package com.globalmed.mes.mes_api.kpi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_kpi_data",
        uniqueConstraints = @UniqueConstraint(name = "uk_kpi_date_eqp_proc_item",
                columnNames = {"kpi_date","equipment_id","process_id","item_id"}))
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

    @PrePersist
    void prePersist() {
        if (createdBy == null || createdBy.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            createdBy = (auth != null && auth.isAuthenticated())
                    ? String.valueOf(auth.getPrincipal())
                    : "system";
        }
    }
}
