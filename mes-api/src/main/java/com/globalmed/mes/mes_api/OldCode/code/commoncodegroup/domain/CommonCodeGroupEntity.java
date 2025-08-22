package com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.domain;

import com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.domain.CommonCodeEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@Table(
        name = "tb_code_group",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_code_group_name", columnNames = "group_name")
        }
)
public class CommonCodeGroupEntity {

    @Id
    @Column(name = "group_code", length = 50, nullable = false)
    private String groupCode;

    @Column(name = "group_name", length = 100, nullable = false, unique = true)
    private String groupName;

    @Column(name = "description", length = 255)
    private String description;

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

    // 코드 엔티티 리스트 (양방향 매핑)
    @OneToMany(mappedBy = "codeGroup", fetch = FetchType.LAZY)
    private List<CommonCodeEntity> codes;
}
