import { useParams} from 'react-router-dom';
import { useQuery} from '@tanstack/react-query'; // React Query 사용
import { api, ApiErrorResponse } from '../../lib/api';
import type { ErpPlanDetail } from '../../types/planTypes';
import { WorkOrderItem } from '../../types/workorder';
import { isAxiosError } from 'axios';

// API 경로 상수
const ERP_PLAN_BASE = "/api/mes/production-plans";

// P, R, C 상태에 따른 색상 정의
const statusColor: Record<string, string> = {
  P: "bg-yellow-100 text-yellow-800",
  R: "bg-blue-100 text-blue-800",
  C: "bg-green-100 text-green-800",
  // Plan 상태를 위한 추가 색상 정의 (Plan은 보통 CREATED, IN_PROGRESS, COMPLETED 등 사용)
  CREATED: "bg-gray-100 text-gray-600", 
  IN_PROGRESS: "bg-blue-100 text-blue-800",
  COMPLETED: "bg-green-100 text-green-800",
};

async function fetchPlanDetail(planId: string) {
    const res = await api.get<ErpPlanDetail>(`${ERP_PLAN_BASE}/${planId}`);
    return res.data;
}


export default function PlanDetail() {
    const { planId } = useParams<{ planId: string }>(); 

    // Plan 상세 조회
    const { data: planDetail, isLoading, isError, error } = useQuery<ErpPlanDetail>({
        queryKey: ["erp-plan-detail", planId],
        queryFn: () => fetchPlanDetail(planId!),
        enabled: !!planId,
    });


    const errorMessage = isError 
    ? isAxiosError<ApiErrorResponse>(error)
        ? error.response?.data?.message ?? `API 호출 실패 (Status: ${error.response?.status})`
        : (error instanceof Error ? error.message : "알 수 없는 서버 오류")
    : "";

    if (isLoading) return <div className="p-6 text-center text-gray-600">Plan 상세 정보를 로딩 중입니다...</div>;
    if (isError || !planDetail) return <div className="p-6 text-center text-red-500">에러: Plan 정보를 찾을 수 없습니다. {errorMessage}</div>;

    // Plan 상태 색상 계산
    const planStatusKey = planDetail.status.toUpperCase();
    const planStatusClass = statusColor[planStatusKey] ?? "bg-gray-100 text-gray-600";


    return (
        <div className="p-4">
            <h1 className="text-2xl font-bold mb-4 text-gray-800">
                Plan 상세: {planDetail.planNumber}
            </h1>
            
            <div className="bg-gray-50 p-4 border rounded mb-6 grid grid-cols-2 gap-3">
                <p><strong>Plan ID:</strong> {planDetail.planId}</p>
                <p><strong>Item ID:</strong> {planDetail.itemId}</p>
                <p><strong>Target Qty:</strong> {planDetail.targetQty.toLocaleString()}</p>
                <p><strong>Start Date:</strong> {planDetail.startDate}</p>
                <p><strong>MES Status:</strong> 
                    <span className={`ml-2 px-2 py-1 rounded text-sm font-medium ${planStatusClass}`}>
                        {planStatusKey}
                    </span>
                </p>
            </div>

            <h3 className="text-xl font-semibold mt-6 mb-3 text-gray-800">연결된 작업 지시 목록</h3>
            
            <div className="overflow-x-auto border rounded shadow-sm">
                <table className="min-w-full table-auto text-sm">
                    <thead className="sticky top-0 bg-gray-200 z-10 text-gray-700">
                        <tr>
                            <th className="p-3 text-left">WO No.</th>
                            <th className="p-3 text-left">Item Name</th>
                            <th className="p-3 text-left">Process</th>
                            <th className="p-3 text-left">Equipment</th>
                            <th className="p-3 text-right">Order Qty</th>
                            <th className="p-3 text-right">Produced Qty</th>
                            <th className="p-3 text-left">Status</th>
                            {/* Action 열 제거 완료 */}
                        </tr>
                    </thead>
                    <tbody>
                        {planDetail.workOrders.map((wo: WorkOrderItem, idx) => {
                            
                            // Work Order 상태 색상 계산
                            const woStatusKey = wo.statusCode.toUpperCase();
                            const woStatusClass = statusColor[woStatusKey] ?? "bg-gray-100 text-gray-600";

                            return (
                                <tr key={wo.workOrderId} className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}>
                                    <td className="p-3 font-medium">{wo.workOrderNumber}</td>
                                    <td className="p-3">{wo.itemName}</td>
                                    <td className="p-3">{wo.processName}</td>
                                    <td className="p-3">{wo.equipmentName}</td>
                                    <td className="p-3 text-right">{wo.orderQty.toLocaleString()}</td>
                                    <td className="p-3 text-right">{wo.producedQty.toLocaleString()}</td>
                                    <td className="p-3">
                                        <span className={`px-2 py-1 rounded text-xs font-medium ${woStatusClass}`}>
                                            {woStatusKey}
                                        </span>
                                    </td>
                                    {/* Action 셀 제거 완료 */}
                                </tr>
                            );
                        })}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
