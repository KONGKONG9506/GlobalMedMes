package com.globalmed.mes.mes_api.commoncodegroup.commoncode.domain;

import com.globalmed.mes.mes_api.commoncodegroup.domain.CommonCodeGroupEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_code",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_code_group_code", columnNames = {"group_code", "code"})
        },
        indexes = {
                @Index(name = "idx_code_group_sort", columnList = "group_code, sort_order")
        }
)
public class CommonCodeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "code_id")
    private Long codeId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "group_code",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_code_group_code")
    )
    private CommonCodeGroupEntity codeGroup;

    @Column(name = "code", length = 50, nullable = false)
    private String code;

    @Column(name = "name", length = 100, nullable = false)
    private String name;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "use_yn", length = 1, nullable = false)
    private String useYn;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_deleted")
    private Byte isDeleted;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt;
}
