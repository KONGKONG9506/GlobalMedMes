package com.globalmed.mes.mes_api.process.repository;

import com.globalmed.mes.mes_api.process.domain.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepo extends JpaRepository<ProcessEntity, String> {
}
