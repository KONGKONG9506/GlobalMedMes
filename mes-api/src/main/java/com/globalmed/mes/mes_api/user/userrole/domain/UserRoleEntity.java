package com.globalmed.mes.mes_api.user.userrole.domain;
import com.globalmed.mes.mes_api.user.domain.UserEntity;
import com.globalmed.mes.mes_api.role.domain.RoleEntity;
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
        name = "tb_user_role",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_role", columnNames = {"user_id", "role_id"})
        }
)
@Check(constraints = "is_deleted IN (0,1)")
public class UserRoleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_role_id", nullable = false)
    private Long userRoleId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false, foreignKey = @ForeignKey(name = "fk_userrole_user"))
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false, foreignKey = @ForeignKey(name = "fk_userrole_role"))
    private RoleEntity role;

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
