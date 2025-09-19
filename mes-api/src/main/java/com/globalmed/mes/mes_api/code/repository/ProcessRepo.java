package com.globalmed.mes.mes_api.code.repository;

import com.globalmed.mes.mes_api.code.domain.ProcessEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProcessRepo extends JpaRepository<ProcessEntity, String> {
}
