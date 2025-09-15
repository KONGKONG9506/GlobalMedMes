package com.globalmed.mes.mes_api.kpi.downtime.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlannedDowntimeDto {

    @NotBlank(message = "equipmentId는 필수 입력값입니다.")
    private String equipmentId;

    @NotNull(message = "startTime은 필수 입력값입니다.")
    private OffsetDateTime startTime;

    @NotNull(message = "endTime은 필수 입력값입니다.")
    private OffsetDateTime endTime;

    @NotNull(message = "downtimeTypeCode는 필수 입력값입니다.")
    private String downtimeTypeCode;

    private String description;

}
