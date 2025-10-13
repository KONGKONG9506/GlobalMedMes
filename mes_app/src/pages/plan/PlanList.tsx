// import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import { useQuery } from '@tanstack/react-query'; // React Query 사용
import { api, ApiErrorResponse } from '../../lib/api'; // 기존 api 인스턴스
import type { ErpPlanList } from '../../types/planTypes'; // Plan 타입 정의 가정
import { isAxiosError } from "axios"; // Axios 에러 타입 가드 import

// (참고: PlanApi는 lib/api.ts에 별도로 정의되어야 합니다.)
const ERP_PLAN_BASE = "/api/mes/production-plans";

const statusColor: Record<string, string> = {
  P: "bg-yellow-100 text-yellow-800",
  R: "bg-blue-100 text-blue-800",
  C: "bg-green-100 text-green-800"
};


async function fetchPlanList() {
    const res = await api.get<ErpPlanList[]>(ERP_PLAN_BASE);
    return res.data;
}

export default function PlanList() {
    const navigate = useNavigate();

    const { data: plans, isLoading, isError, error } = useQuery<ErpPlanList[]>({
        queryKey: ["erp-plans"],
        queryFn: fetchPlanList,
        staleTime: 5 * 60 * 1000, // 5분 동안 fresh 유지
    });

    const errorMessage = isError 
        ? isAxiosError<ApiErrorResponse>(error) // error가 AxiosError인지 확인
            ? error.response?.data?.message ?? `API 호출 실패 (Status: ${error.response?.status})` // 백엔드 메시지 사용
            : (error instanceof Error ? error.message : "알 수 없는 서버 오류") // 일반 JS Error 또는 기타 오류 처리
        : "";

    const handlePlanClick = (planId: string) => {
        // Plan 상세 화면으로 이동
        navigate(`/plans/${planId}`); 
    };
    

    return (
        <div className="p-4">
            <h1 className="text-xl font-semibold mb-4 text-gray-800">
                ERP 수신 생산 계획 목록
            </h1>

            {isLoading && <div className="text-center text-gray-600 py-10">ERP Plan 데이터를 로딩 중입니다...</div>}
            {isError && (
                <div className="text-center text-red-500 py-10">
                    데이터 불러오기 오류: {errorMessage}
                </div>
            )}

            {plans && (
                <>
                    {plans.length === 0 ? (
                        <div className="text-center text-gray-500 py-10 border rounded">수신된 생산 계획이 없습니다. ERP 연동 상태를 확인하세요.</div>
                    ) : (
                        <div className="overflow-x-auto border rounded shadow-sm">
                            <table className="min-w-full table-auto text-sm">
                                <thead className="sticky top-0 bg-blue-100 z-10 shadow-sm text-gray-700">
                                    <tr>
                                        <th className="p-3 text-left">Plan Code</th>
                                        <th className="p-3 text-left">Item ID</th>
                                        <th className="p-3 text-right">Target Qty</th>
                                        <th className="p-3 text-left">Start Date</th>
                                        <th className="p-3 text-left">MES Status</th>
                                        <th className="p-3 text-right">WO Count</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    {plans.map((plan, idx) => (
                                        <tr 
                                            key={plan.planId} 
                                            onClick={() => handlePlanClick(plan.planId)}
                                            className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-yellow-50 cursor-pointer transition duration-150`}
                                        >
                                            <td className="p-3 font-medium text-indigo-700">{plan.planNumber}</td>
                                            <td className="p-3">{plan.itemId}</td>
                                            <td className="p-3 text-right">{plan.targetQty.toLocaleString()}</td>
                                            <td className="p-3">{plan.startDate}</td>
                                            <td className="p-3">
                                                <span className={`px-2 py-1 rounded text-xs font-medium ${statusColor[plan.status.toUpperCase()] ?? "bg-gray-100 text-gray-600"}`}>
                                                    {plan.status.toUpperCase()}
                                                </span>
                                            </td>
                                            <td className="p-3 text-right font-bold text-gray-700">{plan.workOrderCount}</td>
                                        </tr>
                                    ))}
                                </tbody>
                            </table>
                        </div>
                    )}
                </>
            )}
        </div>
    );
}