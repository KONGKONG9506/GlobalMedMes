package com.globalmed.mes.mes_api.employee.shift.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;
import java.time.OffsetDateTime;

@Entity
@Table(name = "tb_shift")
@Getter
@Setter
public class ShiftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "shift_id")
    private Long shiftId;

    @Column(name = "shift_code", nullable = false, length = 10, unique = true)
    private String shiftCode;

    @Column(name = "shift_name", nullable = false, length = 50)
    private String shiftName;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "modified_at")
    private OffsetDateTime modifiedAt;
}
