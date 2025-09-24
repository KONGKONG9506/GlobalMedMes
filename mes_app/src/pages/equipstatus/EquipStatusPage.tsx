import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { toPage } from "../../adapters/page";
import type { PageResult } from "../../types/api";
import type { EquipStatusItem } from "../../types/equip";
import { fetchEquipStatus } from "../../lib/equip";
import EquipStatusSelect, { StatusOptions } from "./Equipstatussearch";
import Rightbarequ from "./Rightbarequ"; 

const statusColors = {
  RUN: "bg-green-100 text-green-800",
  IDLE: "bg-yellow-100 text-yellow-800",
  DOWN: "bg-red-100 text-red-800",
};

export default function EquipStatusPage() {
  // 필터 상태
  const [equipmentId, setEqp] = useState<string>(StatusOptions[0]?.equId || "");
  const today = new Date().toISOString().slice(0, 10);
  const [fromDatd, setFromDate] = useState<string>(today);
  const [toDate, setToDate] = useState<string>(today);

  const [isSidebarOpen, setSidebarOpen] = useState(false);

  // 목록 로드
  const { data, isLoading, error } = useQuery<PageResult<EquipStatusItem>>({
    queryKey: ["equip-status", equipmentId, fromDatd, toDate, 0, 20],
    queryFn: async () => {
      const fromMs = Date.parse(`${fromDatd}T00:00:00Z`);
      const rawToMs = Date.parse(`${toDate}T23:59:59Z`);
      const toMs = Number.isFinite(rawToMs) ? Math.max(fromMs, rawToMs) : fromMs;
      const fromIso = new Date(fromMs).toISOString();
      const toIso = new Date(toMs).toISOString();

      const res = await fetchEquipStatus({
        equipmentId,
        from: fromIso,
        to: toIso,
        page: 0,
        size: 20,
        sort: "startTime,desc",
      });
      return toPage(res);
    },
  });

  return (
    <div className="w-full p-4">
      <h1 className="text-2xl font-bold mb-5 text-black-700">설비 상태</h1>

         {/* 👉 사이드바 열기 버튼 */}
      <div className="mb-4">
        <button
          onClick={() => setSidebarOpen(true)}
          className="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded"
        >
          새 상태 등록
        </button>
      </div>

      {/* 👉 사이드바 */}
      <Rightbarequ
        isOpen={isSidebarOpen}
        onClose={() => setSidebarOpen(false)}
        onCreated={() => {
          setSidebarOpen(false);
        }}
      />

      {/* 👉 사이드바와 연동될 검색조건 */}
      <form className="flex flex-wrap items-end gap-4 mb-6">
        <div className="flex flex-col w-40">
          <label className="text-sm font-medium text-gray-700 mb-1">설비</label>
          <EquipStatusSelect
            equId={equipmentId}
            options={StatusOptions}
            onChange={(v) => setEqp(v)}        
          />
        </div>

        <div className="flex flex-col w-40">
          <label className="text-sm font-medium text-gray-700 mb-1">시작 날짜 (From)</label>
          <input
            className="border border-gray-300 rounded px-2.5 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            type="date"
            value={fromDatd}
            onChange={(e) => setFromDate(e.target.value)}
          />
        </div>

        <div className="flex flex-col w-40">
          <label className="text-sm font-medium text-gray-700 mb-1">끝 날짜 (To)</label>
          <input
            className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            type="date"
            value={toDate}
            onChange={(e) => setToDate(e.target.value)}
          />
        </div>
      </form>

      {/* 👉 목록 */}
      <div>
        <h2 className="text-xl font-semibold mb-3 text-gray-800">최근 상태</h2>
        {isLoading ? (
          <div className="text-gray-600">로딩 중...</div>
        ) : error ? (
          <div className="text-red-600">오류가 발생했습니다.</div>
        ) : !data ? null : (
          <table className="w-full border border-gray-300 rounded-lg overflow-hidden shadow-sm">
            <thead className="sticky top-0 bg-blue-100 text-gray-800 font-semibold z-10 shadow-sm">
              <tr>
                <th className="p-3 border-b border-gray-500 text-left">ID</th>
                <th className="p-3 border-b border-gray-500 text-left">설비</th>
                <th className="p-3 border-b border-gray-500 text-left">상태</th>
                <th className="p-3 border-b border-gray-500 text-left">시작</th>
                <th className="p-3 border-b border-gray-500 text-left">종료</th>
              </tr>
            </thead>
            <tbody>
              {data.items.map((it: EquipStatusItem) => {
                const statusClass =
                  statusColors[it.statusCode as keyof typeof statusColors] ||
                  "text-gray-700";

                return (
                  <tr
                    key={it.logId}
                    className="border-t border-gray-200 hover:bg-gray-50 transition-colors"
                  >
                    <td className="p-3">{it.logId}</td>
                    <td className="p-3">{it.equipmentId}</td>
                    <td>
                      <span
                        className={`inline-block px-3 py-1 rounded-full font-semibold text-sm ${statusClass}`}
                      >
                        {it.statusCode ?? "-"}
                      </span>
                    </td>
                    <td className="p-3">
                      {new Date(it.startTime).toLocaleString("ko-KR", {
                        timeZone: "Asia/Seoul",
                      })}
                    </td>
                    <td className="p-3">
                      {it.endTime
                        ? new Date(it.endTime).toLocaleString("ko-KR", {
                            timeZone: "Asia/Seoul",
                          })
                        : "-"}
                    </td>
                  </tr>
                );
              })}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}