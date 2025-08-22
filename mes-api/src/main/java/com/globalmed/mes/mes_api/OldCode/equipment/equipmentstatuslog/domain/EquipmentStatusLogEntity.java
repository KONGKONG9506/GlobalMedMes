package com.globalmed.mes.mes_api.OldCode.equipment.equipmentstatuslog.domain;

import com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.domain.CommonCodeEntity;
import com.globalmed.mes.mes_api.OldCode.equipment.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.OldCode.workorder.domain.WorkOrderEntity;
import com.globalmed.mes.mes_api.OldCode.shift.domain.ShiftEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_equipment_status_log",
        indexes = {
                @Index(name = "idx_log_equipment_id", columnList = "equipment_id"),
                @Index(name = "idx_log_status_code_id", columnList = "status_code_id"),
                @Index(name = "idx_log_reason_code_id", columnList = "reason_code_id"),
                @Index(name = "idx_log_wo_id", columnList = "work_order_id"),
                @Index(name = "idx_log_shift_id", columnList = "shift_id"),
                @Index(name = "idx_log_equipment_start_time", columnList = "equipment_id, start_time")
        }
)
public class EquipmentStatusLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id", insertable = false, updatable = false)
    private Long logId;

    /** 설비 (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "equipment_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_log_equipment")
    )
    private EquipmentEntity equipment;

    /** 설비 상태 코드 (FK → tb_code) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_log_status_code")
    )
    private CommonCodeEntity statusCode;

    /** 비가동 사유 코드 (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "reason_code_id",
            foreignKey = @ForeignKey(name = "fk_log_reason_code")
    )
    private CommonCodeEntity reasonCode;

    /** 관련 작업지시 (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "work_order_id",
            foreignKey = @ForeignKey(name = "fk_log_wo")
    )
    private WorkOrderEntity workOrder;

    /** 관련 교대 (nullable) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "shift_id",
            foreignKey = @ForeignKey(name = "fk_log_shift")
    )
    private ShiftEntity shift;

    /** 상태 시작/종료 시각 */
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 소프트 삭제 플래그 */
    @Column(name = "is_deleted", columnDefinition = "TINYINT default 0")
    private Byte isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    /** 생성/수정 정보 */
    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_at", insertable = false, updatable = false)
    private LocalDateTime modifiedAt;
}
