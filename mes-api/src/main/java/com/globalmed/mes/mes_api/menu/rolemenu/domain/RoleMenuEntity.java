package com.globalmed.mes.mes_api.menu.rolemenu.domain;

import com.globalmed.mes.mes_api.role.domain.RoleEntity;
import com.globalmed.mes.mes_api.menu.domain.MenuEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Check;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(
        name = "tb_role_menu",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_role_menu", columnNames = {"role_id", "menu_id"})
        }
)
@Check(constraints = "allow_read IN (0,1) AND allow_write IN (0,1) AND allow_exec IN (0,1) AND is_deleted IN (0,1)")
public class RoleMenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "role_menu_id", nullable = false)
    private Long roleMenuId;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rm_role"))
    private RoleEntity role;

    @ManyToOne
    @JoinColumn(name = "menu_id", nullable = false, foreignKey = @ForeignKey(name = "fk_rm_menu"))
    private MenuEntity menu;

    @Column(name = "allow_read", nullable = false)
    @ColumnDefault("1")
    private Byte allowRead;

    @Column(name = "allow_write", nullable = false)
    @ColumnDefault("0")
    private Byte allowWrite;

    @Column(name = "allow_exec", nullable = false)
    @ColumnDefault("0")
    private Byte allowExec;

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