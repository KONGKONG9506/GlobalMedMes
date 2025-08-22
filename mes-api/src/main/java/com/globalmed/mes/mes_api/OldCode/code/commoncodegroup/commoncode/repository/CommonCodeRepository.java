package com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.repository;

import com.globalmed.mes.mes_api.OldCode.code.commoncodegroup.commoncode.domain.CommonCodeEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommonCodeRepository extends JpaRepository<CommonCodeEntity, Long> {
    // 그룹 코드와 코드 기준으로 조회
    Optional<CommonCodeEntity> findByCodeGroup_GroupCodeAndCode(String groupCode, String code);
}