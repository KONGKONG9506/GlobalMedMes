import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { toPage } from "../../adapters/page";
import type { PageResult } from "../../types/api";
import type { EquipStatusItem } from "../../types/equip";
import { fetchEquipStatus, createEquipStatus } from "../../lib/equip";
import { isAxiosError } from "axios";
import CanWrite from "../../components/common/perm/CanWrite";

const statusColors = {
  RUN: "bg-green-100 text-green-800",
  IDLE: "bg-yellow-100 text-yellow-800",
  DOWN: "bg-red-100 text-red-800",
};

export default function EquipStatusPage() {
  const qc = useQueryClient();

  // 필터 상태
  const [equipmentId, setEqp] = useState<string>("E-0001");
  const today = new Date().toISOString().slice(0, 10);
  const [date, setDate] = useState<string>(today);
  const [toDate, setToDate] = useState<string>(today);

  // 생성 폼 상태
  const [statusCode, setStatus] = useState<"RUN" | "IDLE" | "DOWN">("RUN");
  const [time, setTime] = useState<string>("00:00");
  const [endTime, setEndTime] = useState<string>("00:00");
  const [err, setErr] = useState<string>("");

  // 목록 로드
  const { data, isLoading, error } = useQuery<PageResult<EquipStatusItem>>({
    queryKey: ["equip-status", equipmentId, date, toDate, 0, 20],
    queryFn: async () => {
      const fromMs = Date.parse(`${date}T00:00:00Z`);
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

  // 생성 제출
  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setErr("");

    const confirmed = window.confirm("정말로 등록하시겠습니까?");
    if (!confirmed) return; // 취소 시 종료

    const startIso = new Date(`${date}T${time}:00Z`).toISOString();
    const endIso = new Date(`${date}T${endTime}:00Z`).toISOString();

    

    try {
      await createEquipStatus({
        equipmentId,
        statusCode,
        startTimeUtc: startIso,
        endTimeUtc: endIso, 
      });
      await qc.invalidateQueries({ queryKey: ["equip-status"] });
      alert("등록 완료");
    } catch (errUnknown: unknown) {
      const msg = isAxiosError<{ message?: string }>(errUnknown)
        ? errUnknown.response?.data?.message ?? "등록 실패"
        : "등록 실패";
      setErr(msg);
    }
  }

  return (
    <div className="max-w-5xl mx-auto p-4">
      <h1 className="text-2xl font-bold mb-5 text-black-700">설비 상태</h1>

      {/* 검색 바 */}
      <form className="flex flex-wrap items-end gap-4 mb-6">
        <div className="flex flex-col w-40">
          <label className="text-sm font-medium text-gray-700 mb-1">설비</label>
          <input
            className="border border-gray-300 rounded px-1 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            value={equipmentId}
            onChange={(e) => setEqp(e.target.value)}
            placeholder="설비 ID"
          />
        </div>

        <div className="flex flex-col w-40">
          <label className="text-sm font-medium text-gray-700 mb-1">시작 날짜 (From)</label>
          <input
            className="border border-gray-300 rounded px-2.5 py-2 focus:outline-none focus:ring-2 focus:ring-blue-400"
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
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

      {/* 등록 바 */}
      <form onSubmit={submit} className="flex flex-wrap items-end gap-4 mb-8">
        <CanWrite>
          <div className="flex flex-col w-36">
            <label className="text-sm font-medium text-gray-700 mb-1">등록 날짜</label>
            <input
              className="border border-gray-300 rounded px-3 py-2 focus:outline-none focus:ring-2 focus:ring-green-400"
              type="date"
              value={date}
              onChange={(e) => setDate(e.target.value)}
            />
          </div>
        </CanWrite>

        <CanWrite>
          <div className="flex flex-col w-31">
            <label className="text-sm font-medium text-gray-700 mb-1">등록 시각 (UTC)</label>
            <input
              className="border border-gray-300 rounded px-2 py-2 focus:outline-none focus:ring-2 focus:ring-green-400"
              type="time"
              value={time}
              onChange={(e) => setTime(e.target.value)}
            />
          </div>
        </CanWrite>

        {/* ✅ 종료 시간 추가 */}
       <CanWrite>
        <div className="flex flex-col w-31">
        <label className="text-sm font-medium text-gray-700 mb-1">종료 시각 (UTC)</label>
        <input
        className="border border-gray-300 rounded px-2 py-2 focus:outline-none focus:ring-2 focus:ring-green-400"
        type="time"
        value={endTime}
        onChange={(e) => setEndTime(e.target.value)}
        />
       </div>
      </CanWrite>

        <CanWrite>
          <div className="flex flex-col w-28">
            <label className="text-sm font-medium text-gray-700 mb-1">상태</label>
            <select
              className="border border-gray-300 rounded px-3 py-2.5 focus:outline-none focus:ring-2 focus:ring-green-400"
              value={statusCode}
              onChange={(e) => setStatus(e.target.value as "RUN" | "IDLE" | "DOWN")}
            >
              <option value="RUN" className="bg-green-100 text-green-800">
                RUN
              </option>
              <option value="IDLE" className="bg-yellow-100 text-yellow-800">
                IDLE
              </option>
              <option value="DOWN" className="bg-red-100 text-red-800">
                DOWN
              </option>
            </select>
          </div>
        </CanWrite>

        <CanWrite>
          <button
            type="submit"
            className="bg-green-600 hover:bg-green-700 text-white px-6 py-2.5 rounded font-semibold transition-colors"
          >
            등록
          </button>
        </CanWrite>

        {err && <p className="text-red-600 ml-4 font-medium">{err}</p>}
      </form>

      {/* 목록 */}
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
                const statusClass = statusColors[it.statusCode as keyof typeof statusColors] || "text-gray-700";

                return (
                  <tr key={it.logId} className="border-t border-gray-200 hover:bg-gray-50 transition-colors">
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
                      {new Date(it.startTime).toLocaleString("ko-KR", { timeZone: "Asia/Seoul" })}
                    </td>
                    <td className="p-3">
                      {it.endTime
                        ? new Date(it.endTime).toLocaleString("ko-KR", { timeZone: "Asia/Seoul" })
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