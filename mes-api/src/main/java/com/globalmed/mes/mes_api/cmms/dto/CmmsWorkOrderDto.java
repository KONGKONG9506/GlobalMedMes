package com.globalmed.mes.mes_api.cmms.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.OffsetDateTime;

public class CmmsWorkOrderDto {

    @Schema(name = "WorkOrderCreateReq")
    public static record createReq(
            @NotBlank String equipmentId,
            @NotBlank String title,
            @NotNull Long priorityCodeId,
            String requestId
    ){}

    public static record AssignReq(@NotBlank String assigneeUserId) {}

    public static record UpdateReq(
            OffsetDateTime startedAt, OffsetDateTime finishedAt,
            Integer actualMinutes, BigDecimal partsCost
    ) {}

    @Schema(name = "WorkOrderRes")
    public static record Res(
            Long id, String equipmentId, String title,
            Long statusCodeId, Long priorityCodeId,
            String assigneeUserId, String requestId,
            OffsetDateTime createdAt, OffsetDateTime startedAt, OffsetDateTime finishedAt,
            Integer actualMinutes, BigDecimal partsCost
    ){}
}
