import { useState } from "react";
import { keepPreviousData, useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";


type Cmmsworkorder = {
  id: number;
  equipmentId: string;
  title: string;
  statusCodeId: number;
  priorityCodeId:number;
  assigneeUserId:string;
  requestId:string;
  createdAt:string;
  startedAt:string;
  finishedAt:string;
  actualMinutes:string;
  partsCost:string;
};

type CmmsworkorderResponse = {
  content: Cmmsworkorder[];
  page:number;
  size:number;
  totalElements:number;
  totalPages: number;
  sort: string;
};

// 정렬 옵션
const sortOptions = [
  { label: "최신 생성순", value: "createdAt,desc" },
  { label: "오래된 생성순", value: "createdAt,asc" },
  { label: "중요도↑", value: "priorityCodeId,asc" },
  { label: "중요도↓", value: "priorityCodeId,desc" },
];

export default function CmmsPage() {
  const [page, setPage] = useState(0);
  const [size] = useState(10);
  const [sort, setSort] = useState("createdAt,desc");
  const [status, setStatus] = useState("");
  const [equipmentId, setEquipmentId] = useState("");

  // react-query 사용
  const { data, isLoading, error } = useQuery({
    queryKey: ["cmms-work-orders", page, size, sort, status, equipmentId],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };

      if (status) params.status = status;
      if (equipmentId) params.equipmentId = equipmentId;

      const res = await api.get<CmmsworkorderResponse>("/cmms/work-orders", {
        params,
      });
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
        <h2 className="text-xl font-semibold">설비 작업지시 목록</h2>
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
        <select
          className="border px-2 py-1 rounded"
          value={status}
          onChange={(e) => {
            setPage(0);
            setStatus(e.target.value);
          }}
        >
          <option value="">상태 전체</option>
          <option value="OPEN">Open</option>
          <option value="ASSIGNED">Assigned</option>
          <option value="IN_PROGRESS">In Progress</option>
          <option value="DONE">Done</option>
        </select>
        
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
            <th className="border px-2 py-1 hidden">ID</th>
            <th className="border px-2 py-1 ">설비명</th>
            <th className="border px-7 py-1">내용</th>
            <th className="border px-0.5 py-1 hidden">상태</th>
            <th className="border px-0.5 py-1 hidden">중요도</th>
            <th className="border px-2 py-1">담당자</th>
            <th className="border px-2 py-1 hidden">requestId</th>
            <th className="border px-2 py-1 hidden">생성시간</th>
            <th className="border px-2 py-1">시작시간</th>
            <th className="border px-2 py-1">종료시간</th>
            <th className="border px-0.5 py-1">작업시간</th>
            <th className="border px-2 py-1">비용</th>
          </tr>
        </thead>
        <tbody>
          {data.content.map((w) => (
            <tr key={w.id}>
              <td className="border px-2 py-1 hidden">{w.id}</td>
              <td className="border px-2 py-1">{w.equipmentId}</td>
              <td className="border px-2 py-1">{w.title}</td>
              <td className="border px-2 py-1 hidden">{w.statusCodeId}</td>
              <td className="border px-2 py-1 hidden">{w.priorityCodeId}</td>
              <td className="border px-2 py-1">{w.assigneeUserId}</td>
              <td className="border px-2 py-1 hidden">{w.requestId}</td>
              <td className="border px-2 py-1 hidden">{w.createdAt}</td>
              <td className="border px-2 py-1">{w.startedAt}</td>
              <td className="border px-2 py-1">{w.finishedAt}</td>
              <td className="border px-2 py-1">{w.actualMinutes}</td>
              <td className="border px-2 py-1">{w.partsCost}</td>
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