package com.globalmed.mes.mes_api.OldCode.workorder.domain;

import com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.domain.CommonCodeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_work_order",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_wo_number", columnNames = "work_order_number")
        },
        indexes = {
                @Index(name = "idx_wo_plan_id", columnList = "plan_id"),
                @Index(name = "idx_wo_item_id", columnList = "item_id"),
                @Index(name = "idx_wo_process_id", columnList = "process_id"),
                @Index(name = "idx_wo_equipment_id", columnList = "equipment_id"),
                @Index(name = "idx_wo_status_code_id", columnList = "status_code_id"),
                @Index(name = "idx_wo_eqp_start", columnList = "equipment_id, start_ts")
        }
)
public class WorkOrderEntity {

    @Id
    @Column(name = "work_order_id", length = 36, nullable = false)
    private String workOrderId;

    // 생산계획 (nullable FK)
    @Column(name = "plan_id", length = 36)
    private String planId;

    @Column(name = "work_order_number", length = 50, nullable = false, unique = true)
    private String workOrderNumber;

    // 품목 (FK)
    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    // 공정 (FK)
    @Column(name = "process_id", length = 36, nullable = false)
    private String processId;

    // 설비 (FK)
    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "order_qty", precision = 10, scale = 4, nullable = false)
    private BigDecimal orderQty;

    @Column(name = "produced_qty", precision = 10, scale = 4, nullable = false)
    private BigDecimal producedQty;

    @Column(name = "start_ts")
    private LocalDateTime startTs;

    @Column(name = "end_ts")
    private LocalDateTime endTs;

    // 상태 코드 (FK → tb_code)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_wo_status_code")
    )
    private CommonCodeEntity statusCode;

    @Column(name = "is_deleted")
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