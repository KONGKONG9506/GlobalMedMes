package com.globalmed.mes.mes_api.production.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "tb_production_log")
@Getter
@Setter
public class ProductionLogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long logId;

    @Column(name = "work_order_id", length = 36, nullable = false)
    private String workOrderId;

    @Column(name = "equipment_id", length = 36, nullable = false)
    private String equipmentId;

    @Column(name = "process_id", length = 36, nullable = false)
    private String processId;

    @Column(name = "event_type", length = 50, nullable = false)
    private String eventType;

    @Column(name = "event_timestamp", nullable = false)
    private LocalDateTime eventTimestamp;

    @Column(name = "event_value", nullable = false, precision = 10, scale = 4)
    private BigDecimal eventValue = BigDecimal.ZERO;
}
