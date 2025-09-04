package com.globalmed.mes.mes_api.employee.cert.repository;

import com.globalmed.mes.mes_api.employee.cert.domain.CertEntity;
import com.globalmed.mes.mes_api.employee.cert.domain.ProcessCertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProcessCertRepo extends JpaRepository<ProcessCertEntity, Long> {
    List<ProcessCertEntity> findByProcess_ProcessId(String processId);

}
