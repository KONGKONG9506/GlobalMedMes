package com.globalmed.mes.mes_api.OldCode.process.productionperformance.domain;

import com.globalmed.mes.mes_api.OldCode.equipment.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.OldCode.process.domain.ProcessEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_production_performance",
        indexes = {
                @Index(name = "idx_perf_wo_id", columnList = "work_order_id"),
                @Index(name = "idx_perf_item_id", columnList = "item_id"),
                @Index(name = "idx_perf_process_id", columnList = "process_id"),
                @Index(name = "idx_perf_equipment_id", columnList = "equipment_id"),
                @Index(name = "idx_perf_equipment_start_time", columnList = "equipment_id, start_time"),
                @Index(name = "idx_perf_wo_start", columnList = "work_order_id, start_time"),
                @Index(name = "idx_perf_worker", columnList = "worker_id")
        }
)
public class ProductionPerformanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "performance_id", nullable = false)
    private Long performanceId;

    /** 작업 지시 (FK → tb_work_order) */
    @Column(name = "work_order_id", length = 36, nullable = false)
    private String workOrderId;

    /** 품목 (FK → tb_item) */
    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    /** 공정 (FK → tb_process) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "process_id", foreignKey = @ForeignKey(name = "fk_perf_process"))
    private ProcessEntity process;

    /** 설비 (FK → tb_equipment) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "equipment_id", foreignKey = @ForeignKey(name = "fk_perf_equipment"))
    private EquipmentEntity equipment;

    /** 생산 수량 */
    @Column(name = "produced_qty", precision = 10, scale = 4, nullable = false)
    private BigDecimal producedQty;

    /** 불량 수량 */
    @Column(name = "defect_qty", precision = 10, scale = 4, nullable = false)
    private BigDecimal defectQty = BigDecimal.ZERO;

    /** 작업 시작/종료 시간 */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalDateTime endTime;

    /** 작업자 (FK → tb_user.user_id, 선택) */
    @Column(name = "worker_id", length = 36)
    private String workerId;

    /** 소프트 삭제 */
    @Column(name = "is_deleted", columnDefinition = "TINYINT default 0")
    private Byte isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** 생성/수정 정보 */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", updatable = false, insertable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_at", insertable = false, updatable = false)
    private LocalDateTime modifiedAt;
}
