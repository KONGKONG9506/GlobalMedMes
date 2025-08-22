package com.globalmed.mes.mes_api.kpi.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_kpi_target",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_kpi_target_date_eqp_proc_item",
                columnNames = {"kpi_date", "equipment_id", "process_id", "item_id"}
        ),
        indexes = {
                @Index(name = "idx_kpi_target_equipment_id", columnList = "equipment_id"),
                @Index(name = "idx_kpi_target_process_id", columnList = "process_id"),
                @Index(name = "idx_kpi_target_item_id", columnList = "item_id")
        }
)
public class KpiTargetEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "target_id", nullable = false)
    private Long targetId;

    @Column(name = "kpi_date", nullable = false)
    private LocalDate kpiDate;

    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "process_id", length = 36, nullable = false)
    private String processId;

    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Column(name = "target_oee", precision = 5, scale = 2, nullable = false)
    private BigDecimal targetOee = BigDecimal.ZERO;

    @Column(name = "target_productivity", precision = 10, scale = 4, nullable = false)
    private BigDecimal targetProductivity = BigDecimal.ZERO;

    @Column(name = "target_yield", precision = 5, scale = 2, nullable = false)
    private BigDecimal targetYield = BigDecimal.ZERO;

    @Column(name = "is_deleted", columnDefinition = "TINYINT DEFAULT 0")
    private Byte isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_at", insertable = false, updatable = false)
    private LocalDateTime modifiedAt;
}
