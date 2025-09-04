package com.globalmed.mes.mes_api.employee.domain;

import com.globalmed.mes.mes_api.auth.domain.UserEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "tb_employee")
@Getter
@Setter
public class EmployeeEntity {
    @Id
    @Column(name = "employee_id", length = 36)
    private String employeeId;  // tb_user.user_id를 그대로 사용

    @Column(name = "employee_number", nullable = false, length = 50, unique = true)
    private String employeeNumber;

    @Column(name = "employee_name", nullable = false, length = 100)
    private String employeeName;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    // User와의 1:1 관계 (employee_id = user_id)
    @OneToOne
    @MapsId   // Employee의 PK(employee_id)가 User의 PK(user_id)와 공유됨
    @JoinColumn(name = "employee_id")
    private UserEntity user;

}
