package com.globalmed.mes.mes_api.OldCode.process.repository;

import com.globalmed.mes.mes_api.OldCode.process.domain.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProcessRepository extends JpaRepository<ProcessEntity, String> {
}
