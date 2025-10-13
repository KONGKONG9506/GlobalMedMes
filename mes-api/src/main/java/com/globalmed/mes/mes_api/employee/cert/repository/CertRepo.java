package com.globalmed.mes.mes_api.employee.cert.repository;

import com.globalmed.mes.mes_api.employee.cert.domain.CertEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CertRepo extends JpaRepository<CertEntity, Long> {
    Optional<CertEntity> findByCertCode(String certCode);
    @Query("""
    SELECT c FROM CertEntity c
    WHERE c.deleted = false
    AND (c.certName LIKE %:keyword% OR c.certCode LIKE %:keyword%)
    """)
    List<CertEntity> searchActiveCerts(@Param("keyword") String keyword);
}
