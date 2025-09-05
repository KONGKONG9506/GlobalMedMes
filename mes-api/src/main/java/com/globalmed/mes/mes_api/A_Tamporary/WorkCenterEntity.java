package com.globalmed.mes.mes_api.A_Tamporary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//shift및 R 전이 가드용 임시 코드
@Entity
@Table(name="tb_workcenter")
public class WorkCenterEntity {
    @Id
    @Column(name="workcenter_id", length = 36) private String workcenterId;
}
