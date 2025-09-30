import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../../lib/api";
import { useState } from "react";
import { useToast } from "../../store/toast";
import type {  PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";
import { ShiftCalander } from "../../types/shift";
import { toPage } from "../../adapters/page";
import ShiftSidebar from "./ShiftSidebar";

const sortOptions = [
  { label: "최근 교대순", value: "shiftDate,desc" },
  { label: "오래된 교대순", value: "shiftDate,asc" }
];

export default function ShiftCalendarList() {
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [sort, setSort] = useState("shiftDate,desc");
  const [equipmentName, setEquipmentName] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");
  const toast = useToast();
  const [isSidebarOpen, setSidebarOpen] = useState(false);
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery<PageResult<ShiftCalander>>({
    queryKey: ["shifts/calendars", page, size, equipmentName, sort, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };
      if (equipmentName) params.equipmentName = equipmentName;
      if (from) params.startDate = from;
      if (to) params.endDate = to;
      const res = await api.get("/shifts/calendars", { params });
      console.log(res);
      return toPage(res.data);
    }
  });
  const toggleRightSidebar = () => setSidebarOpen(!isSidebarOpen);

  return (
    <div>
      {/* 필터 영역 */}
      <div className="flex flex-wrap gap-3 mb-4 p-4 border rounded bg-gray-100 shadow-sm">
        <input
          className="border w-40 px-3 py-2 rounded"
          placeholder="설비 이름"
          value={equipmentName}
          onChange={(e) => { setPage(0); setEquipmentName(e.target.value); }}
        />
        <input
          className="border px-3 py-2 rounded"
          type="date"
          value={from}
          onChange={(e) => { setPage(0); setFrom(e.target.value); }}
        />
        <input
          className="border px-3 py-2 rounded"
          type="date"
          value={to}
          onChange={(e) => { setPage(0); setTo(e.target.value); }}
        />
      </div>

      {/* 헤더 + 정렬 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800">교대 캘린더</h1>
        <SortSelect value={sort} options={sortOptions} onChange={(v) => { setPage(0); setSort(v); }} />
            <button
              onClick={toggleRightSidebar}
              className="p-2 rounded bg-indigo-600 hover:bg-indigo-700 text-white py-1">
              작업지시 생성
            </button>
      </div>

      {/* 로딩 / 에러 / 데이터 */}
      {isLoading && <div className="text-center text-gray-600 py-10">로딩 중...</div>}
      {error && <div className="text-center text-red-500 py-10">데이터 불러오기 오류</div>}

      {data && (
        <>
          {data?.items?.length === 0 ? (
            <div className="text-center text-gray-500 py-10">표시할 교대 데이터가 없습니다.</div>
          ) : (
            <div className="overflow-x-auto border rounded shadow-sm">
              <table className="min-w-full table-fixed text-sm">
                <thead className="sticky top-0 bg-blue-100 z-10 shadow-sm text-gray-700">
                  <tr>
                    <th className="p-3 text-left">교대일</th>
                    <th className="p-3 text-left">교대조</th>
                    <th className="p-3 text-left">설비</th>
                    <th className="p-3 text-left">워크센터 </th>
                    <th className="p-3 text-left">시작 시간</th>
                    <th className="p-3 text-left">종료 시간</th>
                  </tr>
                </thead>
                <tbody>
                  {data?.items?.map((item: any, idx: number) => (
                    <tr key={item.calendarId} className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}>
                      <td className="p-3">{item.shiftDate}</td>
                      <td className="p-3">{item.shiftName}</td>
                      <td className="p-3">{item.equipmentName}</td>
                      <td className="p-3">{item.workcenterName}</td>
                      <td className="p-3">{new Date(item.startTs).toLocaleString()}</td>
                      <td className="p-3">{new Date(item.endTs).toLocaleString()}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* 페이지네이션 */}
          <Pagination
            page={page}
            size={size}
            total={data.total}
            onPageChange={(p) => setPage(p)}
          />
        </>
      )}

    <ShiftSidebar
    isOpen={isSidebarOpen}
    onClose={() => setSidebarOpen(false)}
    onCreated={() => {
        // 쿼리 무효화해서 새로 불러오기
        queryClient.invalidateQueries({
        queryKey: ["shifts/calendars"],
        });
    }}
    />
    </div>
  );
}