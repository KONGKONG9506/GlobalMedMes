import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../../lib/api";
import { toPage } from "../../adapters/page";
import type { PageResponse, PageResult } from "../../types/api";
import type { WorkOrderItem } from "../../types/workorder";
import { Link } from "react-router-dom";
import { changeWorkOrderStatus } from "../../lib/wo";
import { isAxiosError } from "axios";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";
import { useState } from "react";
import { useToast } from "../../store/toast";
import CanWrite from "../../components/common/perm/CanWrite";
import GuardButton from "../../components/common/perm/GuardButton";
import RightSidebar from "./RightSidebar"; 

const sortOptions = [
  { label: "최신 생성순", value: "createdAt,desc" },
  { label: "오래된 생성순", value: "createdAt,asc" },
  { label: "지시수량↓", value: "orderQty,desc" },
  { label: "지시수량↑", value: "orderQty,asc" }
];

const statusColor: Record<string, string> = {
  P: "bg-yellow-100 text-yellow-800",
  R: "bg-blue-100 text-blue-800",
  C: "bg-green-100 text-green-800"
};

export default function WorkOrdersList() {
  const qc = useQueryClient();
  const [page, setPage] = useState<number>(0);
  const [size] = useState<number>(20);
  const [sort, setSort] = useState<string>("createdAt,desc");
  const [equipmentId, setEqp] = useState<string>("");
  const [status, setStatus] = useState<string>("");
  const [from, setFrom] = useState<string>("");
  const [to, setTo] = useState<string>("");
  const [workOrderNumber, setWon] = useState<string>("");
  const [itemName, setIn] = useState<string>("");
  const [processName, setPn] = useState<string>("");
  const [equipmentName, setEn] = useState<string>("");
  const toggleRightSidebar = () => setSidebarOpen(!isSidebarOpen);

  const [isSidebarOpen, setSidebarOpen] = useState(false);


  const { data, isLoading, error } = useQuery<PageResult<WorkOrderItem>>({
    queryKey: ["work-orders", page, size, sort, equipmentId, status, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };
      if (equipmentId) params.equipmentId = equipmentId;
      if (status) params.status = status;
      if (from) params.from = new Date(`${from}T00:00:00Z`).toISOString();
      if (to) params.to = new Date(`${to}T23:59:59Z`).toISOString();
      const res = await api.get<PageResponse<WorkOrderItem>>("/work-orders", { params });
      return toPage(res.data);
    }
  });

  const toast = useToast();
  async function transition(id: string, to: "R" | "C") {
    if (!window.confirm(`상태를 ${to}로 변경할까요?`)) return;
    try {
      await changeWorkOrderStatus(id, { toStatus: to });
      await qc.invalidateQueries({ queryKey: ["work-orders"] });
      toast.push(`상태가 ${to}로 변경되었습니다.`, "success");
    } catch (err: unknown) {
      const msg = isAxiosError<{ message?: string }>(err)
        ? err.response?.data?.message ?? "상태 변경 실패"
        : "상태 변경 실패";
      toast.push(msg, "error");
    }
  }
  return (
    <div className="flex transition-all duration-300">
    {/* 메인 화면 */}
    <div
      className={`flex-1 transition-all duration-300 ${
        isSidebarOpen ? "mr-72" : "mr-0"
      }`}>
      {/* 필터 영역 */}
      <div className="flex flex-wrap gap-3 mb-4 p-4 border rounded bg-gray-100 shadow-sm">
        <div className="flex items-center gap-2">
        <input className="border w-32 px-3 py-2 rounded" placeholder="작업지시넘버"
          value={workOrderNumber} onChange={(e) => { setPage(0); setWon(e.target.value); }} />
        <input className="border w-15 px-3 py-2 rounded" placeholder="품명"
          value={itemName} onChange={(e) => { setPage(0); setIn(e.target.value); }} />
        <input className="border w-28 px-3 py-2 rounded" placeholder="공정명"
          value={processName} onChange={(e) => { setPage(0); setPn(e.target.value); }} />
        <input className="border w-32 px-3 py-2 rounded" placeholder="설비명"
          value={equipmentName} onChange={(e) => { setPage(0); setEn(e.target.value); }} />
        </div>
        <select
          className="border w-32 px-3 py-2 rounded"
          value={status}
          onChange={(e) => { setPage(0); setStatus(e.target.value); }}
        >
          <option value="">상태(전체)</option>
          <option value="P">P</option>
          <option value="R">R</option>
          <option value="C">C</option>
        </select>
        <input
          className="border w-32 px-3 py-2 rounded"
          type="date"
          value={from}
          onChange={(e) => { setPage(0); setFrom(e.target.value); }}
        />
        <input
          className="border w-32 px-3 py-2 rounded"
          type="date"
          value={to}
          onChange={(e) => { setPage(0); setTo(e.target.value); }}
        />
      </div>

      {/* 헤더 + 정렬 */}
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800">작업지시 목록</h1>
        <div className="flex items-center gap-3">
          <SortSelect value={sort} options={sortOptions} onChange={(v) => { setPage(0); setSort(v); }} />

          {/* 오른쪽 사이드바 버튼 */}
          <CanWrite>
            <button
              onClick={toggleRightSidebar}
              className="p-2 rounded bg-indigo-600 hover:bg-indigo-700 text-white py-1"
            >
              작업지시 생성
            </button>
          </CanWrite>
        </div>
      </div>

      {/* 로딩 / 에러 / 데이터 */}
      {isLoading && <div className="text-center text-gray-600 py-10">로딩 중...</div>}
      {error && <div className="text-center text-red-500 py-10">데이터 불러오기 오류</div>}

      {data && (
        <>
          {data.items.length === 0 ? (
            <div className="text-center text-gray-500 py-10">표시할 작업지시가 없습니다.</div>
          ) : (
            <div className="overflow-x-auto border rounded shadow-sm">
              <table className="min-w-full table-fixed text-sm">
                <thead className="sticky top-0 bg-blue-100 z-10 shadow-sm text-gray-700">
                  <tr>
                    <th className="p-3 text-left">번호</th>
                    <th className="p-3 text-left">품목</th>
                    <th className="p-3 text-left">공정</th>
                    <th className="p-3 text-left">설비</th>
                    <th className="p-3 text-right">지시</th>
                    <th className="p-3 text-right">누적</th>
                    <th className="p-3 text-left">상태</th>
                    <th className="p-3 text-center">액션</th>
                  </tr>
                </thead>
                <tbody>
                  {data.items.map((it, idx) => {
                    const canToR = it.statusCode === "P";
                    const canToC = it.statusCode === "R";
                    return (
                      <tr key={it.workOrderId} className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}>
                        <td className="p-3">{it.workOrderNumber}</td>
                        <td className="p-3">{it.itemName}</td>
                        <td className="p-3">{it.processName}</td>
                        <td className="p-3">{it.equipmentName}</td>
                        <td className="p-3 text-right">{it.orderQty}</td>
                        <td className="p-3 text-right">{it.produceQty}</td>
                        <td className="p-3">
                          <span className={`px-2 py-1 rounded text-sm font-medium ${statusColor[it.statusCode] ?? "bg-gray-100 text-gray-600"}`}>
                            {it.statusCode ?? "-"}
                          </span>
                        </td>
                        <td className="p-3 text-center">
                          <div className="flex justify-center gap-2">
                            <GuardButton require="write"
                              className={`px-3 py-1 rounded text-sm font-semibold ${
                                canToR ? "bg-blue-600 text-white hover:bg-blue-700" : "bg-gray-300 text-gray-500 cursor-not-allowed"
                              }`}
                              onClick={() => canToR && transition(it.workOrderId, "R")}
                              renderDisabled={!canToR}>
                              P→R
                            </GuardButton>

                            <GuardButton require="write"
                              className={`px-3 py-1 rounded text-sm font-semibold ${
                                canToC ? "bg-green-600 text-white hover:bg-green-700" : "bg-gray-300 text-gray-500 cursor-not-allowed"
                              }`}
                              onClick={() => canToC && transition(it.workOrderId, "C")}
                              renderDisabled={!canToC}>
                              R→C
                            </GuardButton>

                            {it.statusCode === "R" && (
                              <CanWrite>
                                <Link
                                  className="px-3 py-1 border border-gray-300 rounded hover:bg-gray-100 text-sm"
                                  to={`/performances/new?woId=${it.workOrderId}&woNumber=${it.workOrderNumber}&itemId=${it.itemId}&processId=${it.processId}&equipmentId=${it. equipmentId}&status=${it.statusCode ?? ""}`}
                                >
                                  실적 등록
                                </Link>
                              </CanWrite>
                            )}
                          </div>
                        </td>
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

      {/* 오른쪽 사이드바 */}
      <RightSidebar isOpen={isSidebarOpen} onClose={() => setSidebarOpen(false)} />
    </div>
  </div>
  );
}
