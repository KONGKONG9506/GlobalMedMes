package com.globalmed.mes.mes_api.equipstatus.controller;

import com.globalmed.mes.mes_api.equipstatus.domain.EquipmentEntity;
import com.globalmed.mes.mes_api.equipstatus.dto.EquipListDto;
import com.globalmed.mes.mes_api.equipstatus.repository.EquipmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentRepo equipmentRepo;

    @GetMapping
    public List<EquipListDto> getAllEquipments() {
        return equipmentRepo.findAllEquipments().stream()
                .map(e -> new EquipListDto(
                        e.getEquipmentName(),
                        e.getWorkcenter().getWorkcenterId()
                ))
                .toList();
    }
}
