package com.globalmed.mes.mes_api.A_Tamporary;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
//commit도 안할 임시 코드
@Entity
@Table(name="tb_process")
public class ProcessEntity {
    @Id
    @Column(name="process_id", length = 36) private String processId;
}
