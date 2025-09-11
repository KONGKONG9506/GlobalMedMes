package com.globalmed.mes.mes_api.employee.cert.domain;

import com.globalmed.mes.mes_api.A_Tamporary.EquipmentEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
@Entity
@Table(name = "tb_equipment_cert",
        uniqueConstraints = @UniqueConstraint(columnNames = {"equipment_id", "cert_id"}))
@Getter
@Setter
public class EquipmentCertEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "equipment_cert_id")
    private Long equipmentCertId; // PK

    // Equipment 참조
    @ManyToOne
    @JoinColumn(name = "equipment_id", nullable = false, foreignKey = @ForeignKey(name = "fk_eqp_cert_eqp"))
    private EquipmentEntity equipment;

    // Cert 참조
    @ManyToOne
    @JoinColumn(name = "cert_id", nullable = false, foreignKey = @ForeignKey(name = "fk_eqp_cert_cert"))
    private CertEntity cert;
}