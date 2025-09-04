package com.globalmed.mes.mes_api.employee.cert.repository;

import com.globalmed.mes.mes_api.employee.cert.domain.CertEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CertRepo extends JpaRepository<CertEntity, Long> {

}
