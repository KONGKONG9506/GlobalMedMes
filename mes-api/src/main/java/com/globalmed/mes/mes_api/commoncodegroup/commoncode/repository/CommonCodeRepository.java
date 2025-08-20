package com.globalmed.mes.mes_api.commoncodegroup.commoncode.repository;

import com.globalmed.mes.mes_api.commoncodegroup.domain.CommonCodeGroupEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommonCodeRepository extends JpaRepository<CommonCodeGroupEntity, String> {
    List<CommonCodeGroupEntity> findByCodeGroup_GroupCodeAndUseYn(String groupCode, String useYn);
    Optional<CommonCodeGroupEntity> findByCodeGroup_GroupCodeAndCode(String groupCode, String code);
}
