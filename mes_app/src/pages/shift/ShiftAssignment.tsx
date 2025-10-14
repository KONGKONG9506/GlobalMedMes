import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";
import type { PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";
import { ShiftAssignmentView } from "../../types/shift"; // DTO 타입
import { ShiftEquipLists, WorkcenterMap } from "./ShiftList";
import { useEffect } from "react";

const sortOptions = [
  { label: "최근 배정순", value: "shiftDate,desc" },
  { label: "오래된 배정순", value: "shiftDate,asc" }
];

export default function ShiftAssignmentList() {
  const today = new Date().toISOString().slice(0, 10);
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [sort, setSort] = useState("shiftDate,desc");
  const [selectedEquip, setSelectedEquip] = useState("");
  const [selectedWorkcenter, setSelectedWorkcenter] = useState("");
  const [from, setFrom] = useState(today);
  const [to, setTo] = useState(today);

  const { data, isLoading, error } = useQuery<PageResult<ShiftAssignmentView>>({
    queryKey: ["shift/assignments/views", page, size, selectedEquip, selectedWorkcenter, sort, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };
      if (selectedEquip){ const eq = ShiftEquipLists.find(e => e.name === selectedEquip);
      if (eq) params.equipmentId = eq.equId;}

      if (selectedWorkcenter) {
        const wc = Object.entries(WorkcenterMap).find(([_, name]) => name === selectedWorkcenter);
        if (wc) params.workcenterId = wc[0];
      }

      if (from) params.startDate = from;
      if (to) params.endDate = to;

    const res = await api.get("/shifts/assignments/views", { params });
      return res.data; // 서버에서 PageResult 형태로 반환
    }
  });

  return (
    <div>
      {/* 필터 영역 */}
      <div className="flex flex-wrap gap-3 mb-4 p-4 border rounded bg-gray-100 shadow-sm">
      <select
      className="border w-40 px-3 py-2 rounded"
      value={selectedEquip}
      onChange={(e) => { setPage(0); setSelectedEquip(e.target.value); }}
      >
      <option value="">모든 설비</option>
      {ShiftEquipLists.map(eq => (
      <option key={eq.equId} value={eq.name}>{eq.name}</option>
      ))}
      </select>
      <select
      className="border w-40 px-3 py-2 rounded"
      value={selectedWorkcenter}
      onChange={(e) => { setPage(0); setSelectedWorkcenter(e.target.value); }}
      >
      <option value="">모든 작업장</option>
      {Object.entries(WorkcenterMap).map(([id, name]) => (
      <option key={id} value={name}>{name}</option>
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
        <h1 className="text-xl font-semibold text-gray-800">교대 목록</h1>
        <SortSelect value={sort} options={sortOptions} onChange={(v) => { setPage(0); setSort(v); }} />
      </div>

      {/* 로딩 / 에러 / 데이터 */}
      {isLoading && <div className="text-center text-gray-600 py-10">로딩 중...</div>}

      {error && (
        <div className="text-center text-gray-500 py-10">
          {/* axios 기준 error.response?.status */}
          {error instanceof Error && (error as any).response?.status === 404
            ? "표시할 교대 데이터가 없습니다."
            : "데이터 불러오기 오류"}
        </div>
      )}

      {data && (
        <>
          {data.items.length === 0 ? (
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
                    <th className="p-3 text-left">작업자</th>
                    <th className="p-3 text-left">작업자 수</th>
                    <th className="p-3 text-left">시작 시간</th>
                    <th className="p-3 text-left">종료 시간</th>
                  </tr>
                </thead>
                <tbody>
                  {data.items.map((item, idx) => {
                    const startDate = new Date(item.startTs);
                    const endDate = new Date(item.endTs);

                    // 브라우저 로컬 기준으로 변환
                    const startLocal = startDate.toLocaleString(undefined, {
                      dateStyle: "short",
                      timeStyle: "medium",
                    });
                    const endLocal = endDate.toLocaleString(undefined, {
                      dateStyle: "short",
                      timeStyle: "medium",
                    });

                    // 교대일도 로컬 날짜 기준으로 표시
                    const shiftDate = startDate.toLocaleDateString(undefined, {
                      year: "numeric",
                      month: "2-digit",
                      day: "2-digit",
                    });

                    return (
                      <tr
                        key={item.workerDisplay + "_" + item.shiftName + "_" + idx}
                        className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}
                      >
                        <td className="p-3">{shiftDate}</td>
                        <td className="p-3">{item.shiftName}</td>
                        <td className="p-3">{item.equipmentName}</td>
                        <td className="p-3">{item.workcenterName}</td>
                        <td className="p-3">{item.workerDisplay}</td>
                        <td className="p-3">{item.workerCount}</td>
                        <td className="p-3">{startLocal}</td>
                        <td className="p-3">{endLocal}</td>
                      </tr>
                    );
                  })}
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
  );
}
