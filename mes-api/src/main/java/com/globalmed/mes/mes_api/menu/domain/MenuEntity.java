package com.globalmed.mes.mes_api.menu.domain;
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
        name = "tb_menu",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_menu_code", columnNames = "menu_code")
        }
)
@Check(constraints = "is_public IN (0,1) AND is_active IN (0,1) AND is_deleted IN (0,1)")
public class MenuEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "menu_id", nullable = false)
    private Long menuId;

    @ManyToOne
    @JoinColumn(name = "parent_id", foreignKey = @ForeignKey(name = "fk_menu_parent"))
    private MenuEntity parent;

    @Column(name = "menu_code", length = 100, nullable = false)
    private String menuCode;

    @Column(name = "menu_name", length = 150, nullable = false)
    private String menuName;

    @Column(name = "path", length = 255, nullable = false)
    private String path;

    @Column(name = "component", length = 255, nullable = true)
    private String component;

    @Column(name = "icon", length = 100, nullable = true)
    private String icon;

    @Column(name = "sort_order", nullable = false)
    @ColumnDefault("0")
    private Integer sortOrder;

    @Column(name = "is_public", nullable = false)
    @ColumnDefault("0")
    private Byte isPublic;

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("1")
    private Byte isActive;

    @Column(name = "is_deleted", nullable = false)
    @ColumnDefault("0")
    private Byte isDeleted;

    @Column(name = "deleted_at", nullable = true)
    private LocalDateTime deletedAt;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "modified_by", length = 50, nullable = true)
    private String modifiedBy;

    @Column(name = "modified_at", insertable = false, updatable = false, nullable = true)
    private LocalDateTime modifiedAt;
}
