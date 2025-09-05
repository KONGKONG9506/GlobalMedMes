package com.globalmed.mes.mes_api.employee.cert.service;

import com.globalmed.mes.mes_api.employee.cert.repository.ProcessCertRepo;
import com.globalmed.mes.mes_api.employee.repository.EmployeeCertRepo;
import com.globalmed.mes.mes_api.employee.shift.domain.ShiftAssignmentEntity;
import com.globalmed.mes.mes_api.employee.shift.repository.ShiftAssignmentRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProcessCertCheckService {
    private final ShiftAssignmentRepo shiftAssignmentRepo;
    private final EmployeeCertRepo employeeCertRepo;
    private final ProcessCertRepo processCertRepo;

    public void check(String equipmentId, String processId, OffsetDateTime now) {
        // 1) 현재 설비 담당자 찾기
        ShiftAssignmentEntity assignment = shiftAssignmentRepo.findCurrentWorker(equipmentId, now)
                .orElseThrow(() -> new IllegalStateException("NO_WORKER_ASSIGNED"));

        String employeeId = assignment.getWorkerId();

        // 2) 직원이 가진 자격증
        Set<String> employeeCertCodes = employeeCertRepo.findByEmployee_EmployeeId(employeeId)
                .stream()
                .map(ec -> ec.getCert().getCertCode()) // CertEntity에서 certCode 꺼내기
                .collect(Collectors.toSet());

        // 3) 공정이 요구하는 자격증
        Set<String> requiredCertCodes = processCertRepo.findByProcess_ProcessId(processId)
                .stream()
                .map(pc -> pc.getCert().getCertCode())
                .collect(Collectors.toSet());

        // 4) 요구사항 검증
        if (!employeeCertCodes.containsAll(requiredCertCodes)) {
            throw new IllegalStateException("WO_CERT_INVALID");
        }
    }
}
