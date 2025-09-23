package com.globalmed.mes.mes_api.employee.cert.repository;

import com.globalmed.mes.mes_api.employee.cert.domain.ProcessCertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProcessCertRepo extends JpaRepository<ProcessCertEntity, Long> {
    @Query("SELECT pc FROM ProcessCertEntity pc WHERE pc.process.processId = :processId AND pc.deleted = false")
    List<ProcessCertEntity> findProCert(@Param("processId") String processId);
}
