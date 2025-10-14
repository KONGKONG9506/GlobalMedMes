import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../../lib/api";
import { useState } from "react";
import type {  PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";
import { ShiftCalander } from "../../types/shift";
import { toPage } from "../../adapters/page";
import ShiftSidebar from "./ShiftSidebar";
import ShiftAssignSidebar from "./ShiftAssignSideBar";
import { ShiftEquipLists, WorkcenterMap } from "./ShiftList";

const sortOptions = [
  { label: "최근 교대순", value: "shiftDate,desc" },
  { label: "오래된 교대순", value: "shiftDate,asc" }
];

export default function ShiftCalendarList() {
  const today = new Date().toISOString().slice(0, 10);
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [sort, setSort] = useState("shiftDate,desc");
  const [equipmentId, setEquipmentId] = useState("");
  const [workcenterId, setworkcenterId] = useState(""); // 워크센터 필터
  const [from, setFrom] = useState(today);
  const [to, setTo] = useState(today);
  const [isSidebarOpen, setSidebarOpen] = useState(false);
  const queryClient = useQueryClient();
  const [sidebarCalendarId, setSidebarCalendarId] = useState<number | null>(null);

  const { data, isLoading, error } = useQuery<PageResult<ShiftCalander>>({
    queryKey: ["shifts/calendars", page, size, equipmentId, workcenterId, sort, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };
      if (equipmentId) params.equipmentId = equipmentId;
      if (workcenterId) params.workcenterId = workcenterId;
      if (from) params.startDate = from;
      if (to) params.endDate = to;
      try {
        const res = await api.get("/shifts/calendars", { params });
        return toPage(res.data);
      } catch (err: any) {
        if (err.response?.status === 404) {
          return { items: [], 
            page: page,    
            size: size,    
            total: 0      
          }; 
        }
        throw err;
      }

    }
  });
  const toggleRightSidebar = () => {
    setSidebarCalendarId(null)
    setSidebarOpen(!isSidebarOpen)
  };
  const toggleCalendarSidebar = (item: any) => {
    setSidebarOpen(false)
    setSidebarCalendarId(item.calendarId)
  }

  return (
    <div className="flex relative">
      {/* 필터 영역 */}
      <div className={`flex-1 transition-all duration-300 ${isSidebarOpen || sidebarCalendarId ? "mr-96" : "mr-0"}`}>
      <div className="flex flex-wrap gap-3 mb-4 p-4 border rounded bg-gray-100 shadow-sm">
        <select
          className="border w-40 px-3 py-2 rounded"
          value={equipmentId}
          onChange={(e) => { setPage(0); setEquipmentId(e.target.value); }}
        >
          <option value="">모든 설비</option>
          {ShiftEquipLists.map(eq => (
            <option key={eq.equId} value={eq.equId}>{eq.equId}</option>
          ))}
        </select>

        <select
          className="border w-40 px-3 py-2 rounded"
          value={workcenterId}
          onChange={(e) => { setPage(0); setworkcenterId(e.target.value); }}
        >
          <option value="">모든 작업장</option>
          {Object.entries(WorkcenterMap).map(([id, name]) => (
            <option key={id} value={id}>{name}</option>
          ))}
        </select>
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
        <h1 className="text-xl font-semibold text-gray-800">작업 일정</h1>
        <div className="flex items-center gap-3">
        <SortSelect value={sort} options={sortOptions} onChange={(v) => { setPage(0); setSort(v); }} />
            <button
              onClick={toggleRightSidebar}
              className="px-3 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700">
              일정 생성
            </button>
        </div>
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
                    <th className="p-3 text-left">작업장</th>
                    <th className="p-3 text-left">시작 시간</th>
                    <th className="p-3 text-left">종료 시간</th>
                    <th className="p-3 text-left">직원 배치</th>
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
                      <td className="p-3">
                        <button
                          className="px-3 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700"
                          onClick={() => toggleCalendarSidebar(item)}
                        >
                          직원 배치
                        </button>
                      </td>
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
      </div>

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
      <ShiftAssignSidebar
        isOpen={sidebarCalendarId} // calendarId
        onClose={() => setSidebarCalendarId(null)}
        onAssigned={() => queryClient.invalidateQueries({ queryKey: ["shifts/calendars"] })}
      />
    </div>
  );
}