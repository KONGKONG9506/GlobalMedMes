package com.globalmed.mes.mes_api.kpi.downtime.dto;

import java.time.OffsetDateTime;

public record DowntimeIntervalDto(OffsetDateTime start, OffsetDateTime end) {}
