import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../../lib/api";
import { toPage } from "../../adapters/page";
import type { PageResponse, PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import ProcessDetailSideBar from "./ProcessDetailSideBar";

type ProcessItem = {
  id: string;
  name: string;
  description: string;
};

export default function ProcessList() {
  const [page, setPage] = useState(0);
  const [size] = useState(20);
  const [sort, setSort] = useState("processName,asc");
  const [selectedProcId, setSelectedProcId] = useState<string | null>(null);
  const queryClient = useQueryClient();

  const { data, isLoading, error } = useQuery<PageResult<ProcessItem>>({
    queryKey: ["process", page, size, sort],
    queryFn: async () => {
      const res = await api.get<PageResponse<ProcessItem>>("/process", {
        params: { page, size, sort },
      });
      return toPage(res.data);
    },
  });

  return (
    <div className="p-6">
      <h1 className="text-xl font-semibold mb-4">공정 목록</h1>

      {isLoading && <div>로딩 중...</div>}
      {error && <div className="text-red-500">데이터 불러오기 오류</div>}

      {data && (
        <>
          {data.items.length === 0 ? (
            <div>표시할 공정이 없습니다.</div>
          ) : (
            <table className="min-w-full border rounded shadow-sm text-sm">
              <thead className="bg-gray-100 text-gray-700">
                <tr>
                  <th className="p-3 text-left">ID</th>
                  <th className="p-3 text-left">공정명</th>
                  <th className="p-3 text-left">설명</th>
                  <th className="p-3 text-left">상세 보기</th>
                  <th className="p-3 text-left">수정</th>
                </tr>
              </thead>
              <tbody>
                {data.items.map((proc, idx) => (
                  <tr
                    key={proc.id}
                    className={idx % 2 === 0 ? "bg-white" : "bg-gray-50"}
                  >
                    <td className="p-3">{proc.id}</td>
                    <td className="p-3">{proc.name}</td>
                    <td className="p-3">{proc.description}</td>
                    <td className="p-3">
                      <button
                        className="text-blue-600 hover:underline"
                        onClick={() => setSelectedProcId(proc.id)}
                      >
                        상세 보기
                      </button>
                    </td>

                  </tr>
                ))}
              </tbody>
            </table>
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
        <ProcessDetailSideBar
        id={selectedProcId}
        onClose={() => setSelectedProcId(null)}
        />
    </div>
    
  );
}
