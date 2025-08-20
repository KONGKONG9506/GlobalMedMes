package com.globalmed.mes.mes_api.commoncodegroup.repository;

import com.globalmed.mes.mes_api.commoncodegroup.domain.CommonCodeGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommonCodeGroupRepository extends JpaRepository<CommonCodeGroupEntity, Long> {
    Optional<CommonCodeGroupEntity> findByGroupName(String groupName);
}
