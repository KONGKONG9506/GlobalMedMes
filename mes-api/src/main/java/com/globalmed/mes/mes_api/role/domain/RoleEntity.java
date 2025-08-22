package com.globalmed.mes.mes_api.role.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;
import org.hibernate.annotations.ColumnDefault;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_code", columnNames = "role_code")
        }
)
@Check(constraints = "is_deleted IN (0,1)")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_id", nullable = false)
    private Long roleId;

    @Column(name = "role_code", length = 50, nullable = false)
    private String roleCode;

    @Column(name = "role_name", length = 100, nullable = false)
    private String roleName;

    @Column(name = "description", length = 255, nullable = true)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("0")
    private Byte isDeleted;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", insertable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 50, nullable = true)
    private String modifiedBy;

    @Column(name = "modified_at", insertable = false, updatable = false, nullable = true)
    private LocalDateTime modifiedAt;
}