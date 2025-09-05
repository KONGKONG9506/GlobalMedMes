package com.globalmed.mes.mes_api.employee.shift.repository;

import com.globalmed.mes.mes_api.employee.shift.domain.ShiftCalendarEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShiftCalendarRepo extends JpaRepository<ShiftCalendarEntity, Long> {
}
