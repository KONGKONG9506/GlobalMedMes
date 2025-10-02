import { useState } from "react";
import { useQuery } from "@tanstack/react-query";
import { api } from "../../lib/api";
import type { PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import SortSelect from "../../components/common/SortSelect";
import { ShiftAssignmentView } from "../../types/shift"; // DTO 타입

const sortOptions = [
  { label: "최근 배정순", value: "shiftDate,desc" },
  { label: "오래된 배정순", value: "shiftDate,asc" }
];

export default function ShiftAssignmentList() {
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [sort, setSort] = useState("shiftDate,desc");
  const [equipmentName, setEquipmentName] = useState("");
  const [workcenterName, setWorkcenterName] = useState("");
  const [from, setFrom] = useState("");
  const [to, setTo] = useState("");


  const { data, isLoading, error } = useQuery<PageResult<ShiftAssignmentView>>({
    queryKey: ["shift/assignments/views", page, size, equipmentName, workcenterName, sort, from, to],
    queryFn: async () => {
      const params: Record<string, string | number> = { page, size, sort };
      if (equipmentName) params.equipmentName = equipmentName;
      if (workcenterName) params.workcenterName = workcenterName;
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
        <input
          className="border w-40 px-3 py-2 rounded"
          placeholder="설비 이름"
          value={equipmentName}
          onChange={(e) => { setPage(0); setEquipmentName(e.target.value); }}
        />
        <input
          className="border w-40 px-3 py-2 rounded"
          placeholder="워크센터 이름"
          value={workcenterName}
          onChange={(e) => { setPage(0); setWorkcenterName(e.target.value); }}
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
        <h1 className="text-xl font-semibold text-gray-800">교대 목록</h1>
        <SortSelect value={sort} options={sortOptions} onChange={(v) => { setPage(0); setSort(v); }} />
      </div>

      {/* 로딩 / 에러 / 데이터 */}
      {isLoading && <div className="text-center text-gray-600 py-10">로딩 중...</div>}
      {error && <div className="text-center text-red-500 py-10">데이터 불러오기 오류</div>}

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
                    <th className="p-3 text-left">워크센터</th>
                    <th className="p-3 text-left">작업자</th>
                    <th className="p-3 text-left">작업자 수</th>
                    <th className="p-3 text-left">시작 시간</th>
                    <th className="p-3 text-left">종료 시간</th>
                  </tr>
                </thead>
                <tbody>
                  {data.items.map((item, idx) => (
                    <tr key={item.workerDisplay + "_" + item.shiftName + "_" + idx} 
                        className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}>
                      <td className="p-3">{new Date(item.startTs).toISOString().slice(0, 10)}</td>
                      <td className="p-3">{item.shiftName}</td>
                      <td className="p-3">{item.equipmentName}</td>
                      <td className="p-3">{item.workcenterName}</td>
                      <td className="p-3">{item.workerDisplay}</td>
                      <td className="p-3">{item.workerCount}</td>
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
    </div>
  );
}
