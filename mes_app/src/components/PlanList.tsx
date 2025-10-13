import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom'; // 라우팅 라이브러리 사용 가정
import { planApi } from '../lib/planApi'; // lib/api.ts에 정의된 클라이언트
import { ErpPlanList } from '../types/planTypes'; // types/planTypes.ts에 정의된 인터페이스

const PlanList: React.FC = () => {
    const [plans, setPlans] = useState<ErpPlanList[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchPlans = async () => {
            try {
                // GET /api/mes/production-plans 호출
                const response = await planApi.getPlanList(); 
                setPlans(response.data);
            } catch (err) {
                // Axios 에러 처리
                setError("ERP 연동 계획 목록 조회 실패. 백엔드 상태를 확인하세요.");
                console.error(err);
            } finally {
                setIsLoading(false);
            }
        };
        fetchPlans();
    }, []);

    const handlePlanClick = (planId: string) => {
        // Plan 상세 화면으로 이동 (예시 경로)
        navigate(`/plans/${planId}`); 
    };

    if (isLoading) {
        return <div className="loading">ERP Production Plan 데이터를 로딩 중입니다...</div>;
    }

    return (
        <div className="container">
            <h2>ERP 수신 생산 계획 목록</h2>
            {error && <div className="error-message">{error}</div>}

            <table className="data-table">
                <thead>
                    <tr>
                        <th>Plan No.</th>
                        <th>Item ID</th>
                        <th>Target Qty</th>
                        <th>Start Date</th>
                        <th>MES Status</th>
                        <th>WO Count</th>
                    </tr>
                </thead>
                <tbody>
                    {plans.length === 0 ? (
                        <tr>
                            <td colSpan={6} className="text-center">수신된 Plan이 없습니다. ERP 연동 상태를 확인하세요.</td>
                        </tr>
                    ) : (
                        plans.map((plan) => (
                            <tr 
                                key={plan.planId} 
                                onClick={() => handlePlanClick(plan.planId)}
                                style={{ cursor: 'pointer' }}
                            >
                                <td>{plan.planNumber}</td>
                                <td>{plan.itemId}</td>
                                <td>{plan.targetQty.toLocaleString()}</td>
                                <td>{plan.startDate}</td>
                                <td>
                                    <span className={`status-tag status-${plan.status.toLowerCase()}`}>
                                        {plan.status}
                                    </span>
                                </td>
                                <td>{plan.workOrderCount}</td>
                            </tr>
                        ))
                    )}
                </tbody>
            </table>
        </div>
    );
};

export default PlanList;