// src/main/java/com/globalmed/mes/mes_api/workorder/WorkOrderSpecs.java
package com.globalmed.mes.mes_api.workorder.specs;

import com.globalmed.mes.mes_api.code.CodeEntity;
import com.globalmed.mes.mes_api.workorder.domain.WorkOrderEntity;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public final class WorkOrderSpecs {
    private WorkOrderSpecs(){}

    // ✅ fetch join 추가용 spec
    public static Specification<WorkOrderEntity> withFetchJoins() {
        return (root, query, cb) -> {
            // 중복 방지 (count 쿼리일 땐 fetch join 쓰면 에러남)
            if (query.getResultType() != Long.class) {
                root.fetch("item");
                root.fetch("process");
                root.fetch("equipment").fetch("workcenter");
                root.fetch("statusCode");
            }
            return cb.conjunction();
        };
    }

    public static Specification<WorkOrderEntity> equipmentIdEquals(String equipmentId) {
        return (root, q, cb) -> (equipmentId == null || equipmentId.isBlank())
                ? cb.conjunction()
                : cb.equal(root.get("equipmentId").get("equipmentId"), equipmentId);
    }

    public static Specification<WorkOrderEntity> statusEquals(String statusCode) {
        return (root, q, cb) -> {
            if (statusCode == null || statusCode.isBlank()) return cb.conjunction();
            Join<WorkOrderEntity, CodeEntity> st = root.join("statusCode"); // 연관명 'statusCode'
            return cb.equal(st.get("code"), statusCode);                    // CodeEntity의 'code'
        };
    }

    public static Specification<WorkOrderEntity> startBetween(LocalDateTime from, LocalDateTime to) {
        return (root, q, cb) -> {
            if (from == null && to == null) return cb.conjunction();
            if (from != null && to != null) return cb.between(root.get("startTs"), from, to); // 필드명 'startTs'
            if (from != null) return cb.greaterThanOrEqualTo(root.get("startTs"), from);
            return cb.lessThan(root.get("startTs"), to);
        };
    }
}