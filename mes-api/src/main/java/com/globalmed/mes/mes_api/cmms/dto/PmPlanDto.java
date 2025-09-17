package com.globalmed.mes.mes_api.cmms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;

public class PmPlanDto{

    @Schema(name = "PmPlanCreateReq",
            description = "PM 계획 생성 요청")
    public static record CreateReq(
            @NotBlank String equipmentId,
            @NotBlank String taskName,
            @NotNull Long cycleTypeCodeId,
            @NotNull Integer cycleValue,
            OffsetDateTime lastDoneAt,
            OffsetDateTime nextDueAt,
            Integer estimatedTakeTime
    ){}

    @Schema(name = "PmPlanRes")
    public static record Res(
            Long id, String equipmentId, String taskName,
            Long cycleTypeCodeId, Integer cycleValue,
            OffsetDateTime lastDoneAt, OffsetDateTime nextDueAt, String status
    ){}
}

