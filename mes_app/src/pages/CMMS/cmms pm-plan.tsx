import { useState } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";


type CmmsPmPlan = {
  id: number;
  equipmentId: string;
  taskName: string;
  cycleTypeCodeId: number;
  cycleValue: number;
  lastDoneAt: string;
  nextDueAt: string;
  status: string;
};

type CmmsApiResponse = {
  content: CmmsPmPlan[];
  page: number;
  size: number;
  totalElements: number;   // ✅ 백엔드 응답 키랑 동일하게
  totalPages: number;      // ✅ 이것도 추가
  sort: string;            // ✅ 응답에 있음
};

// 정렬 옵션
const sortOptions = [
  {label: "예정일 빠른순", value: "nextDueAt,asc"},
  {label: "예정일 늦은순", value: "nextDueAt,desc"},
  {label: "최근 수행순", value: "lastDoneAt,desc"},
  {label: "오래된 수행순", value: "lastDoneAt,asc"},
]

export default function CmmsPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [sort, setSort] = useState("nextDueAt,asc");
  const [equipmentId, setEquipmentId] = useState("");
  const [to, setTo] = useState("");

  const {data, isLoading, error } = useQuery<CmmsApiResponse>({
    queryKey: ["cmms-pm-plans", page, size, sort, equipmentId, to],
    queryFn: async () => {
      const params: Record<string, string | number> = {page, size, sort };
      if (equipmentId) params.equipmentId = equipmentId;
      if (to) params.to = new Date(`${to}T23:59:59Z`).toISOString();

      const res = await api.get<CmmsApiResponse>("/cmms/pm-plans/due", { params });
      return res.data;
    },
    placeholderData: keepPreviousData,
  });

  if (isLoading) return <div>불러오는 중...</div>;
  if (error) return <div className="text-red-500">PM 계획 조회 실패</div>;
  if (!data) return <div>데이터 없음</div>;

  return (
    <div className="p-4">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold">PM 계획 목록</h2>
      </div>
      {/* 필터 */}
      <div className="flex gap-2 mb-4">
        <input
          className="border px-2 py-1 rounded"
          placeholder="설비ID"
          value={equipmentId}
          onChange={(e) => {
            setPage(0);
            setEquipmentId(e.target.value);
          }}
        />
        <input
          className="border px-2 py-1 rounded"
          type="date"
          value={to}
          onChange={(e) => {
            setPage(0);
            setTo(e.target.value);
          }}
        />
        <SortSelect
          value={sort}
          options={sortOptions}
          onChange={(v) => {
            setPage(0);
            setSort(v);
          }}
        />
      </div>  
      <div className="overflow-x-auto border rounded shadow-sm">
      <table className="min-w-full table-fixed text-sm">
        <thead className="sticky top-0 bg-blue-100 z-10 shadow-sm text-gray-700">
          <tr>
            <th className="border px-2 py-1">ID</th>
            <th className="border px-2 py-1">설비ID</th>
            <th className="border px-2 py-1">작업명</th>
            <th className="border px-2 py-1">Cycle Type Code</th>
            <th className="border px-2 py-1">Cycle Value</th>
            <th className="border px-2 py-1">완료시간</th>
            <th className="border px-2 py-1">다음 점검일</th>
            <th className="border px-2 py-1">상태</th>
          </tr>
        </thead>
        <tbody>
          {data.content.map((plan) => (
            <tr
              key={plan.id}
              className="hover:bg-gray-50 transition-colors"
            >
              <td className="border px-2 py-1">{plan.id}</td>
              <td className="border px-2 py-1">{plan.equipmentId}</td>
              <td className="border px-2 py-1">{plan.taskName}</td>
              <td className="border px-2 py-1">{plan.cycleTypeCodeId}</td>
              <td className="border px-2 py-1">{plan.cycleValue}</td>
              <td className="border px-2 py-1">{plan.lastDoneAt}</td>
              <td className="border px-2 py-1">{plan.nextDueAt}</td>
              <td className="border px-2 py-1">
                <span
                  className={`px-2 py-1 rounded text-white text-sm font-medium ${
                    plan.status === "DUE"
                      ? "bg-red-500"
                      : plan.status === "IN_PROGRESS"
                      ? "bg-yellow-500"
                      : "bg-green-500"
                  }`}
                >
                  {plan.status}
                </span>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>

    {/* 페이지네이션 */}
      <div className="mt-4">
        <Pagination
          page={page}
          size={size}
          total={data.totalElements}
          onPageChange={(p) => setPage(p)}
        />
      </div>
    </div>
  );
}