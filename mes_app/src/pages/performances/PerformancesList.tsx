import { useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";
import { toPage } from "../../adapters/page";
import type { PageResponse, PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";
import { useState } from "react";

type PerfItem = {
  performanceId: number;
  workOrderId: string; workOrderNumber: string; itemId: string; processId: string; equipmentId: string;
  producedQty: number; defectQty: number; startTime: string; endTime: string; processName: string; processDescription: string;
  equipmentName: string; itemName: string; itemType: string; itemUnit: string; itemDescription: string;
  workOrderorderQty: number; workOrderproducedQty: number; workOrderCode: string; perfDefectQty: number; perfProducedQty: number;
  perfstartTime: string; perfendTime: string;
};


const sortOptions = [
  { label: "시작시각↓", value: "startTime,desc" },
  { label: "시작시각↑", value: "startTime,asc" },
  { label: "생산량↓", value: "producedQty,desc" },
  { label: "생산량↑", value: "producedQty,asc" }
];

export default function PerformancesList() {
  const [page, setPage] = useState<number>(0);
  const [size] = useState<number>(20);
  const [sort, setSort] = useState<string>("startTime,desc");
  const [workOrderNumber, setWon] = useState<string>("");
  const [itemName, setIn] = useState<string>("");
  const [processName, setPn] = useState<string>("");
  const [equipmentName, setEn] = useState<string>("");
  const [from, setFrom] = useState<string>("");
  const [to, setTo] = useState<string>("");

  const { data, isLoading, error } = useQuery<PageResult<PerfItem>>({
    queryKey: ["performances", page, size, sort, workOrderNumber, itemName, processName, equipmentName, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };
      if (workOrderNumber) params.workOrderNumber = workOrderNumber;
      if (itemName) params.itemName = itemName;
      if (processName) params.processName = processName;
      if (equipmentName) params.equipmentName = equipmentName;

      if (from) params.from = new Date(`${from}T00:00:00Z`).toISOString();
      if (to) params.to = new Date(`${to}T23:59:59Z`).toISOString();
      const res = await api.get<PageResponse<PerfItem>>("/performances", { params });
      return toPage(res.data);
    }
  });

  return (
    <div className="space-y-4">
      {/* 🔍 검색 필터 바 */}
      <div className="flex flex-wrap gap-3 mb-2 p-4 border rounded bg-gray-100 shadow-sm">
        <input className="border w-32 px-3 py-2 rounded" placeholder="작업지시넘버"
          value={workOrderNumber} onChange={(e) => { setPage(0); setWon(e.target.value); }} />
        <input className="border px-3 py-2 rounded" placeholder="품명"
          value={itemName} onChange={(e) => { setPage(0); setIn(e.target.value); }} />
        <input className="border w-40 px-3 py-2 rounded" placeholder="공정명"
          value={processName} onChange={(e) => { setPage(0); setPn(e.target.value); }} />
        <input className="border w-32 px-3 py-2 rounded" placeholder="설비명"
          value={equipmentName} onChange={(e) => { setPage(0); setEn(e.target.value); }} />
        <span className="self-center text-gray-600">시작기간:</span>
        <input className="border w-36 px-3 py-2 rounded" type="date"
          value={from} onChange={(e) => { setPage(0); setFrom(e.target.value); }} />
        <input className="border w-36 px-3 py-2 rounded" type="date"
          value={to} onChange={(e) => { setPage(0); setTo(e.target.value); }} />
      </div>

      {/* 🔤 헤더 + 정렬 */}
      <div className="flex items-center justify-between mb-3">
    <h1 className="text-xl font-semibold text-gray-800">실적 목록</h1>
        <SortSelect value={sort} options={sortOptions} onChange={(v) => { setPage(0); setSort(v); }} />
      </div>

      {/* ⏳ 로딩 / 에러 / 없음 */}
      {isLoading && <div className="text-center text-gray-600 py-10">로딩 중...</div>}
      {error && <div className="text-center text-red-500 py-10">데이터 불러오기 오류</div>}
      {data && (
        <>
          {data.items.length === 0 ? (
            <div className="text-center text-gray-500 py-10">실적 데이터가 없습니다.</div>
          ) : (
            <div className="overflow-x-auto border rounded shadow-sm">
              <table className="min-w-full table-fixed text-sm">
                {/* ✅ 헤더 강조 적용 */}
                <thead className="bg-blue-100 text-black-800">
                  <tr className="font-semibold">
                    <th className="p-3 text-left">실적ID</th>
                    <th className="p-3 text-left">공정</th>
                    <th className="p-3 text-left">공정설명</th>                    
                    <th className="p-3 text-left">설비</th>
                    <th className="p-3 text-left">품목</th>
                    <th className="p-3 text-left">구분</th>
                    <th className="p-3 text-left">단위</th>
                    <th className="p-3 text-left">품목설명</th>
                    <th className="p-3 text-left">작업지시</th>
                    <th className="p-3 text-right">지시</th>
                    <th className="p-3 text-right">누적</th>
                    <th className="p-3 text-left">상태</th>
                    <th className="p-3 text-right">생산량</th>
                    <th className="p-3 text-right">불량</th>
                    <th className="p-3 text-left">시작시각</th>
                    <th className="p-3 text-left">종료시각</th>
                  </tr>
                </thead>
                <tbody>
                  {data.items.map((it, idx) => (
                    <tr key={it.performanceId} className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}>

                      <td className="p-3">{it.performanceId}</td>
                      <td className="p-3">{it.processName}</td>
                      <td className="p-3">{it.processDescription}</td>
                      <td className="p-3">{it.equipmentName}</td>
                      <td className="p-3">{it.itemName}</td>
                      <td className="p-3">{it.itemType}</td>
                      <td className="p-3">{it.itemUnit}</td>
                      <td className="p-3">{it.itemDescription}</td>
                      <td className="p-3">{it.workOrderNumber}</td>
                      <td className="p-3">{it.workOrderorderQty}</td>
                      <td className="p-3">{it.workOrderproducedQty}</td>
                      <td className="p-3">{it.workOrderCode}</td>
                      
                      <td className="p-3 text-right text-blue-900 font-semibold">{it.perfProducedQty}</td>
                      <td className="p-3 text-right text-red-700 font-semibold">{it.perfDefectQty}</td>
                      <td className="p-3">
                        {new Date(it.perfstartTime).toLocaleString("ko-KR", {
                          year: "numeric", month: "2-digit", day: "2-digit",
                          hour: "2-digit", minute: "2-digit"
                        })}
                      </td>
                                            <td className="p-3">
                        {new Date(it.perfendTime).toLocaleString("ko-KR", {
                          year: "numeric", month: "2-digit", day: "2-digit",
                          hour: "2-digit", minute: "2-digit"
                        })}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {/* ⏩ 페이지네이션 */}
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
