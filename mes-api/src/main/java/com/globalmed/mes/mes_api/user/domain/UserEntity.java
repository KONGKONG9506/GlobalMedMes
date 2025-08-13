package com.globalmed.mes.mes_api.user.domain;

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
        name = "tb_user",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_user_username", columnNames = "username"),
                @UniqueConstraint(name = "uk_user_email", columnNames = "email")
        }
)
@Check(constraints = "is_active IN (0,1) AND is_deleted IN (0,1) AND failed_login_count >= 0")
public class UserEntity {

    @Id
    @Column(name = "user_id", length = 36, nullable = false)
    private String userId;

    @Column(name = "username", length = 50, nullable = false)
    private String username;

    @Column(name = "email", length = 255, nullable = true, unique = true)
    private String email;

    @Column(name = "password_hash", length = 100, nullable = false)
    private String passwordHash;

    @Column(name = "password_algo", length = 20, nullable = false)
    @ColumnDefault("'bcrypt'")
    private String passwordAlgo = "bcrypt";

    @Column(name = "is_active", nullable = false)
    @ColumnDefault("1")
    private Byte isActive;

    @Column(name = "failed_login_count", nullable = false)
    @ColumnDefault("0")
    private Integer failedLoginCount;

    @Column(name = "locked_until", nullable = true)
    private LocalDateTime lockedUntil;

    @Column(name = "last_login_at", nullable = true)
    private LocalDateTime lastLoginAt;

    @Column(name = "phone", length = 30, nullable = true)
    private String phone;

    @Column(name = "display_name", length = 100, nullable = true)
    private String displayName;

    @Column(name = "is_deleted", nullable = true)
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

    @Column(name = "modified_at", nullable = true)
    private LocalDateTime modifiedAt;
}
