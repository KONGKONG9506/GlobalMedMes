package com.globalmed.mes.mes_api.equipment.domain;

import com.globalmed.mes.mes_api.commoncodegroup.commoncode.domain.CommonCodeEntity;
import com.globalmed.mes.mes_api.process.domain.ProcessEntity;
import com.globalmed.mes.mes_api.workshop.workcenter.domain.WorkcenterEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_equipment",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_equipment_name", columnNames = "equipment_name")
        },
        indexes = {
                @Index(name = "idx_equipment_workcenter_id", columnList = "workcenter_id"),
                @Index(name = "idx_equipment_process_id", columnList = "process_id"),
                @Index(name = "idx_equipment_status_code_id", columnList = "status_code_id"),
                @Index(name = "idx_equipment_proc_wc", columnList = "process_id, workcenter_id")
        }
)
public class EquipmentEntity {

    @Id
    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "equipment_name", length = 255, nullable = false, unique = true)
    private String equipmentName;

    /** 작업장 (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "workcenter_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_eqp_wc")
    )
    private WorkcenterEntity workcenter;

    /** 공정 (FK) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "process_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_eqp_proc")
    )
    private ProcessEntity process;

    /** 설비 상태 코드 (FK → tb_code) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "status_code_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_eqp_status")
    )
    private CommonCodeEntity statusCode;

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
