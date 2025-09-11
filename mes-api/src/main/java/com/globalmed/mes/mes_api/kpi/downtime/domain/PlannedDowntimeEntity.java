package com.globalmed.mes.mes_api.kpi.downtime.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Entity
@Table(name = "tb_kpi_planned_downtime")
@Getter
@Setter
public class PlannedDowntimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "planned_downtime_id")
    private Long plannedDowntimeId;

    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "start_time", nullable = false)
    private OffsetDateTime startTime;

    @Column(name = "end_time", nullable = false)
    private OffsetDateTime endTime;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    // 변경: downtime_type 문자열 대신 tb_code 테이블의 code_id를 참조
    @Column(name = "downtime_type_code_id", nullable = false)
    private Long downtimeTypeCodeId;

    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "is_deleted", nullable = false)
    private Boolean isDeleted = false;

    @Column(name = "created_by", length = 50, nullable = false)
    private String createdBy;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @PrePersist
    void prePersist() {
        if (createdBy == null || createdBy.isBlank()) {
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            createdBy = (auth != null && auth.isAuthenticated())
                    ? auth.getName()
                    : "system";
        }
    }

    @PreUpdate
    void preUpdate() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        modifiedBy = (auth != null && auth.isAuthenticated())
                ? auth.getName()
                : "system";
    }
}
