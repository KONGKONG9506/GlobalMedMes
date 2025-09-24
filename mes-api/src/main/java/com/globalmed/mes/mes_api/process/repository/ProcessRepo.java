package com.globalmed.mes.mes_api.process.repository;

import com.globalmed.mes.mes_api.process.domain.ProcessEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProcessRepo extends JpaRepository<ProcessEntity, String> {
    boolean existsByProcessName(String processName);
    Optional<ProcessEntity>findByProcessName(String processName);
    Page<ProcessEntity> findAllByIsDeletedFalse(Pageable pageable);
}
