// lib/api.ts (기존 파일에 아래 함수들을 추가합니다.)

import { api } from "./api"; // 기존 axios 인스턴스
import { ErpPlanList, ErpPlanDetail } from "../types/planTypes";
import { WorkOrderStatusReq, WorkOrderCreateRes } from "../types/workorder"; // WorkOrderTypes에서 import

// 🚨 1. ERP 연동 생산 계획 조회 API (GET /api/mes/production-plans)

const ERP_PLAN_BASE = "/api/mes/production-plans";

export const planApi = {
    /**
     * ERP에서 수신된 Plan 목록 조회
     */
    getPlanList: () => {
        // GET http://localhost:8080/api/mes/production-plans
        return api.get<ErpPlanList[]>(ERP_PLAN_BASE);
    },

    /**
     * 특정 Plan의 상세 정보와 연결된 Work Order 목록 조회
     */
    getPlanDetail: (planId: string) => {
        // GET http://localhost:8080/api/mes/production-plans/{planId}
        return api.get<ErpPlanDetail>(`${ERP_PLAN_BASE}/${planId}`);
    },
};


// 🚨 2. Work Order 상태 전이 API (PUT /work-orders/{id}/status)

const WORK_ORDER_BASE = "/work-orders"; // 기존 MES Work Order 경로

export const woApi = {
    // ... (기존 Work Order 조회, 생성 API가 있다면 여기에 포함)

    /**
     * Work Order 상태 전이 (P->R, R->C) 및 ERP 통보 트리거
     * @param workOrderId - Work Order ID
     * @param toStatus - 다음 상태 ("R" 또는 "C")
     */
    transitionStatus: (workOrderId: string, req: WorkOrderStatusReq) => {
        // PUT http://localhost:8080/work-orders/{id}/status
        // 응답은 Work Order 상세 DTO 또는 간단한 상태 응답 (WorkOrderCreateRes와 유사)이 될 수 있습니다.
        return api.put<WorkOrderCreateRes>(`${WORK_ORDER_BASE}/${workOrderId}/status`, req);
    },
};