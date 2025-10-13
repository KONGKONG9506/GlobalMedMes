import React, { useState, useEffect, useCallback } from 'react';
import { useParams } from 'react-router-dom';
import { planApi } from '../lib/planApi'; // Plan, WO API 클라이언트
import { woApi } from '../lib/planApi'; // Plan, WO API 클라이언트
import { ErpPlanDetail } from '../types/planTypes';
import { WorkOrderItem, WorkOrderStatusReq } from '../types/workorder';

const PlanDetail: React.FC = () => {
    const { planId } = useParams<{ planId: string }>(); // URL에서 planId 가져오기
    const [planDetail, setPlanDetail] = useState<ErpPlanDetail | null>(null);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    // ---------------------------------------------------
    // Plan 상세 데이터 조회 로직
    // ---------------------------------------------------
    const fetchPlanDetail = useCallback(async () => {
        if (!planId) return;

        try {
            setIsLoading(true);
            const response = await planApi.getPlanDetail(planId); // GET /api/mes/production-plans/{planId}
            setPlanDetail(response.data);
            setError(null);
        } catch (err) {
            setError("Plan 상세 정보 로드 실패.");
            console.error("Plan 상세 정보 로드 에러:", err);
        } finally {
            setIsLoading(false);
        }
    }, [planId]);

    useEffect(() => {
        fetchPlanDetail();
    }, [fetchPlanDetail]);


    // ---------------------------------------------------
    // Work Order 상태 전이 (P -> R) 로직
    // ---------------------------------------------------
    const handleStartWorkOrder = async (workOrderId: string, currentStatus: string) => {
        if (currentStatus !== 'P') {
            alert("이미 시작되었거나 완료된 작업지시입니다.");
            return;
        }

        if (!window.confirm(`작업지시 [${workOrderId}]를 시작(R)하시겠습니까? (ERP로 IN_PRODUCTION 상태 통보)`)) {
            return;
        }

        try {
            const req: WorkOrderStatusReq = { toStatus: 'R' };
            // PUT /work-orders/{id}/status 호출
            // 이 호출 내부에서 MES 상태 변경 (P -> R) 및 ERP PUSH 로직이 트리거됩니다.
            await woApi.transitionStatus(workOrderId, req); 
            
            alert("작업지시가 성공적으로 시작(R)되었으며, ERP로 상태가 통보되었습니다.");

            // 상태 변경 후 목록을 새로고침
            await fetchPlanDetail(); 

        } catch (err) {
            alert("작업 시작 실패. 백엔드 에러를 확인하세요.");
            console.error("WO 상태 전이 에러:", err);
        }
    };


    if (isLoading) return <div>Plan 상세 정보를 로딩 중입니다...</div>;
    if (error) return <div style={{ color: 'red' }}>에러: {error}</div>;
    if (!planDetail) return <div>Plan 정보를 찾을 수 없습니다.</div>;

    return (
        <div className="container">
            <h2>{planDetail.planNumber} 상세 정보 (ERP $\leftrightarrow$ MES)</h2>
            <div className="plan-info">
                <p><strong>Plan ID:</strong> {planDetail.planId}</p>
                <p><strong>Item:</strong> {planDetail.itemId}</p>
                <p><strong>Target Qty:</strong> {planDetail.targetQty.toLocaleString()}</p>
                <p><strong>MES Status:</strong> <span className={`status-${planDetail.status.toLowerCase()}`}>{planDetail.status}</span></p>
            </div>

            <h3 style={{ marginTop: '30px' }}>연결된 작업 지시 목록 (Work Orders)</h3>
            <table className="data-table">
                <thead>
                    <tr>
                        <th>WO No.</th>
                        <th>Item Name</th>
                        <th>Process</th>
                        <th>Equipment</th>
                        <th>Order Qty</th>
                        <th>Produced Qty</th>
                        <th>Status</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {planDetail.workOrders.map((wo: WorkOrderItem) => (
                        <tr key={wo.workOrderId}>
                            <td>{wo.workOrderNumber}</td>
                            <td>{wo.itemName}</td>
                            <td>{wo.processName}</td>
                            <td>{wo.equipmentName} ({wo.workcenterName})</td>
                            <td>{wo.orderQty.toLocaleString()}</td>
                            <td>{wo.producedQty.toLocaleString()}</td>
                            <td>
                                <span className={`status-tag status-${wo.statusCode.toLowerCase()}`}>
                                    {wo.statusCode}
                                </span>
                            </td>
                            <td>
                                <button
                                    onClick={() => handleStartWorkOrder(wo.workOrderId, wo.statusCode)}
                                    disabled={wo.statusCode !== 'P'} // Planned 상태에서만 시작 가능
                                    style={{ 
                                        padding: '5px 10px', 
                                        backgroundColor: wo.statusCode === 'P' ? 'green' : 'lightgray', 
                                        color: 'white', 
                                        border: 'none', 
                                        cursor: wo.statusCode === 'P' ? 'pointer' : 'not-allowed'
                                    }}
                                >
                                    작업 시작 (P $\rightarrow$ R)
                                </button>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
};

export default PlanDetail;