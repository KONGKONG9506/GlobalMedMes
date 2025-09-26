package com.globalmed.mes.mes_api.employee.service;

import com.globalmed.mes.mes_api.employee.domain.EmployeeEntity;
import com.globalmed.mes.mes_api.employee.dto.EmployeeAssignmentDto;
import com.globalmed.mes.mes_api.employee.repository.EmployeeRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class EmployeeService {
    private final EmployeeRepo employeeRepo;

    public Page<EmployeeAssignmentDto> getEmployeesWithAssignments(String name, Pageable pageable) {
        // 1. 기존 페이징 조회
        Page<EmployeeEntity> employees;
        if (name != null && !name.isEmpty()) {
            employees = employeeRepo.findEmployeeName(name, pageable);
        } else {
            employees = employeeRepo.findNotDeletedAll(pageable);
        }

        // 2. 직원별 설비/공정 조회
        List<Object[]> raw = employeeRepo.findRawAssignments(name != null ? name : "");

        // 3. Map으로 DTO 변환
        Map<String, EmployeeAssignmentDto> map = new HashMap<>();
        for (Object[] row : raw) {
            String employeeId = (String) row[0];
            String employeeName = (String) row[1];
            String equipmentId = (String) row[2];
            String processId = (String) row[3];

            EmployeeAssignmentDto dto = map.computeIfAbsent(employeeId,
                    id -> new EmployeeAssignmentDto(id, employeeName, employeeName, new ArrayList<>(), new ArrayList<>())
            );

            if (equipmentId != null && !dto.getAllowedEquipment().contains(equipmentId)) {
                dto.getAllowedEquipment().add(equipmentId);
            }
            if (processId != null && !dto.getAllowedProcess().contains(processId)) {
                dto.getAllowedProcess().add(processId);
            }
        }

        // 4. 기존 Page 구조에 맞춰 반환
        List<EmployeeAssignmentDto> dtos = new ArrayList<>();
        for (EmployeeEntity e : employees.getContent()) {
            EmployeeAssignmentDto dto = map.getOrDefault(e.getEmployeeId(),
                    new EmployeeAssignmentDto(e.getEmployeeId(), e.getEmployeeName(), e.getEmployeeNumber(),new ArrayList<>(), new ArrayList<>())
            );
            dtos.add(dto);
        }

        return new PageImpl<>(dtos, pageable, employees.getTotalElements());
    }

}