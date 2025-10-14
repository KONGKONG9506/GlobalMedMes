package com.globalmed.mes.mes_api.plan.domain;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "tb_production_plan") // MES의 생산 계획 테이블
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TbProductionPlan {

    // 🚨 ERP와 연동되는 Plan ID (UUID)를 그대로 사용
    @Id
    @Column(name = "plan_id", length = 36, nullable = false)
    private String planId;

    // 🚨 ERP의 planCode와 동일하게 사용
    @Column(name = "plan_number", length = 50, nullable = false, unique = true)
    private String planNumber;

    // ERP의 productId를 참조 (MES에서는 Item/Product ID)
    @Column(name = "item_id", length = 36, nullable = false)
    private String itemId;

    @Column(name = "target_qty", precision = 18, scale = 6, nullable = false)
    private BigDecimal targetQty;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    /**
     * 🚨 중요: ERP와 통일된 VARCHAR(30) 적용
     * MES 내부에서는 CHAR(1) 코드를 사용할 수도 있으나, 연동을 위해 VARCHAR(30)을 유지하거나
     * 내부적으로 사용하는 CHAR(1) 필드를 별도로 두는 설계도 가능합니다.
     * 여기서는 Plan의 'ERP 전송 상태'를 표시하기 위해 VARCHAR(30)을 사용합니다.
     */
    @Column(name = "status", length = 30, nullable = false)
    private String status;

    // AuditableEntity가 별도로 없다고 가정하고 기본 필드 추가
    @Column(name = "created_by", length = 50)
    private String createdBy;

    @Column(name = "modified_by", length = 50)
    private String modifiedBy;

    @Version // 낙관적 락을 위한 버전 관리 필드
    private Long version;
}