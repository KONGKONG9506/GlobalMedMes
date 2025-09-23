package com.globalmed.mes.mes_api.process.controller;

import com.globalmed.mes.mes_api.process.dto.ProcessDetailDto;
import com.globalmed.mes.mes_api.process.dto.ProcessListDto;
import com.globalmed.mes.mes_api.process.service.ProcessService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/process")
@RequiredArgsConstructor

public class ProcessController {

    private final ProcessService processService;

    @GetMapping
    public ResponseEntity<Page<ProcessListDto>> getProcesses(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "processId,asc") String sort
    ) {
        Sort s = Sort.by(sort.split(",")[0]);
        if (sort.split(",").length > 1 && sort.split(",")[1].equalsIgnoreCase("desc")) {
            s = s.descending();
        }
        PageRequest pageable = PageRequest.of(page, size, s);
        Page<ProcessListDto> processes = processService.getProcessList(pageable);
        return ResponseEntity.ok(processes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProcessDetail(@PathVariable("id") String processId, HttpServletRequest req) {
        try {
            ProcessDetailDto detail = processService.getProcessDetail(processId);

            if (detail == null) {
                return ResponseEntity.status(404).body(Map.of(
                        "code", "ID_NOT_FOUND",
                        "message", "해당 공정 ID를 찾을 수 없습니다",
                        "path", req.getRequestURI(),
                        "method", req.getMethod()
                ));
            }
            return ResponseEntity.ok(detail);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of(
                    "code", "INTERNAL_ERROR",
                    "message", e.getMessage(),
                    "path", req.getRequestURI(),
                    "method", req.getMethod()
            ));
        }
    }
}