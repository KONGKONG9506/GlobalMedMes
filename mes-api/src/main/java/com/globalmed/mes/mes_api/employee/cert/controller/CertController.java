package com.globalmed.mes.mes_api.employee.cert.controller;

import com.globalmed.mes.mes_api.code.dto.CertDto;
import com.globalmed.mes.mes_api.employee.cert.repository.CertRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/cert")
@RequiredArgsConstructor
public class CertController {
    private final CertRepo certRepo;
    @GetMapping
    public List<CertDto> searchCerts(@RequestParam String keyword) {
        return certRepo.searchActiveCerts(keyword)
                .stream()
                .map(CertDto::fromEntity)
                .toList();
    }
}
