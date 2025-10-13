// src/types/planTypes.ts

import { WorkOrderItem } from "./workorder"; // WorkOrderItem 재사용

// Work Order 개수와 함께 Plan 목록 조회 시 사용
export interface ErpPlanList {
    planId: string;
    planNumber: string;         // ERP의 Plan Code
    itemId: string;
    startDate: string;          // LocalDate (YYYY-MM-DD)
    targetQty: number;          // BigDecimal (number로 매핑)
    status: string;             // MES Plan 상태 (P, R, C 등)
    workOrderCount: number;     // 연결된 Work Order 개수
}

// Plan 상세 조회 시 사용
export interface ErpPlanDetail {
    planId: string;
    planNumber: string;
    itemId: string;
    startDate: string;
    endDate: string;
    targetQty: number;
    status: string;
    // 연결된 Work Order 목록은 WorkOrderItem을 재사용
    workOrders: WorkOrderItem[]; 
}