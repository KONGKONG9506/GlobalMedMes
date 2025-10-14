import { useState } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";


type CmmsFaultLog = {
  id: number;
  equipmentId: string;
  lossCategoryCodeId: number;
  symptom: string;
  action: string;
  occurredAt: string;
  resolveAt: string;
  workOrderId: string;
};

type CmmsFaultResponse = {
  content: CmmsFaultLog[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  sort: string;
};

const sortOptions = [
  { label: "최근 발생순", value: "occurredAt,desc"},
  { label: "오래된 발생순", value: "occurredAt,asc"},
  { label: "최근 해결순", value: "resolveAt,desc"},
  { label: "오래된 해결순", value: "resolveAt,asc"},
];


export default function CmmsFault() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [sort, setSort] = useState("occurredAt,desc");
  const [equipmentId, setEquipmentId] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");

  
  const { data, isLoading, error } = useQuery<CmmsFaultResponse>({
    queryKey: ["cmms-fault-logs", page, size, sort, equipmentId, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = {page, size, sort};
      if (equipmentId) params.equipmentId = equipmentId;
      if (from) params.from = new Date(`${from}T00:00:00Z`).toISOString();
      if (to) params.to = new Date(`${to}T23:59:59Z`).toISOString();

      const res = await api.get<CmmsFaultResponse>("/cmms/fault-logs",{ params}); 
      return res.data;  
    },
    placeholderData: keepPreviousData, 
  });

  if (isLoading) return <div>불러오는 중...</div>;
  if (error) return <div className="text-red-500">작업지시 조회 실패</div>;
  if (!data) return <div>데이터 없음</div>;

  return (
    <div className="p-4">
      {/* 헤더 */}
      <div className="flex items-center justify-between mb-4">
        <h2 className="text-xl font-semibold">고장&수리 목록</h2>
      </div>
      {/* 필터 영역 */}
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
          value={from}
          onChange={(e) => {
            setPage(0);
            setFrom(e.target.value);
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

      {/* 테이블 */}
      <div className="overflow-x-auto border rounded shadow-sm">
        <table className="min-w-full table-fixed text-sm">
          <thead className="sticky top-0 bg-blue-100 z-10 shadow-sm text-gray-700">
            <tr>
              <th className="border px-2 py-1 hidden">ID</th>
              <th className="border px-2 py-1">설비명</th>
              <th className="border px-2 py-1 hidden">고장 분류</th>
              <th className="border px-2 py-1">증상</th>
              <th className="border px-2 py-1">조치</th>
              <th className="border px-2 py-1">발생시간</th>
              <th className="border px-2 py-1">해결시간</th>
              <th className="border px-2 py-1 hidden">작업지시ID</th>
            </tr>
          </thead>
          <tbody>
            {data.content.map((f) => (
              <tr key={f.id}>
                <td className="border px-2 py-1 hidden">{f.id}</td>
                <td className="border px-2 py-1">{f.equipmentId}</td>
                <td className="border px-2 py-1 hidden">{f.lossCategoryCodeId}</td>
                <td className="border px-2 py-1">{f.symptom}</td>
                <td className="border px-2 py-1">{f.action}</td>
                <td className="border px-2 py-1">{f.occurredAt}</td>
                <td className="border px-2 py-1">{f.resolveAt}</td>
                <td className="border px-2 py-1 hidden">{f.workOrderId}</td>
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