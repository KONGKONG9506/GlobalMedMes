package com.globalmed.mes.mes_api.employee.repository;

import com.globalmed.mes.mes_api.employee.domain.EmployeeEntity;
import com.globalmed.mes.mes_api.employee.dto.EmployeeAssignmentDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface EmployeeRepo extends JpaRepository<EmployeeEntity, String> {

    @Query("SELECT e FROM EmployeeEntity e WHERE LOWER(e.employeeName) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<EmployeeEntity> findEmployeeName(@Param("name") String name, Pageable pageable);

    @Query("""
    SELECT e.employeeId, e.employeeName, eq.equipmentId, p.processId
    FROM EmployeeEntity e
    LEFT JOIN EmployeeCertEntity ec ON ec.employee = e
    LEFT JOIN EquipmentCertEntity eqc ON eqc.cert = ec.cert
    LEFT JOIN eqc.equipment eq
    LEFT JOIN ProcessCertEntity pc ON pc.cert = ec.cert
    LEFT JOIN pc.process p
    WHERE LOWER(e.employeeName) LIKE LOWER(CONCAT('%', :name, '%'))
    """)
    List<Object[]> findRawAssignments(@Param("name") String name);
}
