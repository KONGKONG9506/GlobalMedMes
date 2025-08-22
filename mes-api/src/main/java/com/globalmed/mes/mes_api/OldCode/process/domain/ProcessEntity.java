package com.globalmed.mes.mes_api.OldCode.process.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_process",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_process_name", columnNames = "process_name")
        }
)
public class ProcessEntity {

    @Id
    @Column(name = "process_id", length = 36, nullable = false)
    private String processId;

    @Column(name = "process_name", length = 255, nullable = false, unique = true)
    private String processName;

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