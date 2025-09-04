package com.globalmed.mes.mes_api.employee.repository;

import com.globalmed.mes.mes_api.employee.cert.domain.CertEntity;
import com.globalmed.mes.mes_api.employee.domain.EmployeeCertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmployeeCertRepo extends JpaRepository<EmployeeCertEntity, Long> {
    List<EmployeeCertEntity> findByEmployee_EmployeeId(String employeeId);
}
