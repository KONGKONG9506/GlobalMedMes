package com.globalmed.mes.mes_api.production.repository;

import com.globalmed.mes.mes_api.production.domain.ProductionLogEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductionLogRepo extends JpaRepository<ProductionLogEntity, Long> {
}
