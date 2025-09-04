package com.globalmed.mes.mes_api.employee.repository;

import com.globalmed.mes.mes_api.employee.domain.EmployeeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmployeeRepo extends JpaRepository<EmployeeEntity, String> {

}
