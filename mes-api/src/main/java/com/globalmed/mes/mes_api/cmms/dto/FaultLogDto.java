package com.globalmed.mes.mes_api.cmms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;

import java.time.OffsetDateTime;


public class FaultLogDto {

    @Schema(name = "FaultLogCreateReq")
    public static record CreateReq(
            @NotBlank String equipmentId,
            @NotNull Long lossCategoryCodeId,
            @NotBlank String symptom,
            String action,
            @NotNull OffsetDateTime occurredAt,
            OffsetDateTime resolvedAt,
            Long workOrderId
            ){}
    @Schema(name = "FaultLogRes")
    public static record Res(
            Long id, String equipmentId, Long lossCategoryCodeId,
            String symptom, String action,
            OffsetDateTime occurredAt, OffsetDateTime resolvedAt,
            Long workOrderId
    ){}
}
