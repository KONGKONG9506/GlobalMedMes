package com.globalmed.mes.mes_api.OldCode.workshop.workcenter.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_workcenter",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_workcenter_name", columnNames = "workcenter_name")
        },
        indexes = {
                @Index(name = "idx_workcenter_workshop_id", columnList = "workshop_id")
        }
)
public class WorkcenterEntity {

    @Id
    @Column(name = "workcenter_id", length = 36, nullable = false)
    private String workcenterId;

    @Column(name = "workcenter_name", length = 255, nullable = false, unique = true)
    private String workcenterName;

    /** 작업장 그룹(FK → tb_workshop.workshop_id) */
    @Column(name = "workshop_id", length = 36, nullable = false)
    private String workshopId;

    @Column(name = "description", length = 255)
    private String description;

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
