import { useState } from "react";
import { useQuery, useQueryClient } from "@tanstack/react-query";
import { api } from "../../lib/api";
import { toPage } from "../../adapters/page";
import type { PageResponse, PageResult } from "../../types/api";
import Pagination from "../../components/common/Pagination";
import ProcessDetailSideBar from "./ProcessDetailSideBar";
import ProcessEditSideBar from "./ProcessEditSideBar";
import ProcessCreateSideBar from "./ProcessCreateSideBar";

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
  const [editProcId, setEditProcId] = useState<string | null>(null);
  const [isCreateOpen, setCreateOpen] = useState(false);
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

  const refreshList = () => {
  queryClient.invalidateQueries({ queryKey: ["process"] });
  };

  return (
    <div className="p-6 flex relative">
      <div className={`transition-all duration-300 flex-1 ${selectedProcId || editProcId || isCreateOpen ? "mr-96" : "mr-0"}`}>
      <div className="flex items-center justify-between mb-4">
        <h1 className="text-xl font-semibold text-gray-800">공정 목록</h1>
        <button
          onClick={() => {setCreateOpen(true);
                          setEditProcId(null);
                          setSelectedProcId(null);}}
          className="px-3 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700"
        >
          새 공정 생성
        </button>
      </div>
      {isLoading && <div className="text-center text-gray-600 py-10">로딩 중...</div>}
      {error && <div className="text-center text-red-500 py-10">데이터 불러오기 오류</div>}
      {data && (
        <>
          {data.items.length === 0 ? (
            <div className="text-center text-gray-500 py-10">표시할 공정이 없습니다.</div>
          ) : (
            <div className="overflow-x-auto border rounded shadow-sm">
              <table className="min-w-full border rounded shadow-sm text-sm">
                <thead className="sticky top-0 bg-blue-100 z-10 shadow-sm">
                  <tr>
                    <th className="p-3 text-left w-1/4">공정명</th>
                    <th className="p-3 text-left w-1/2">설명</th>
                    <th className="p-3 text-left w-1/8">상세 보기</th>
                    <th className="p-3 text-left w-1/8">수정</th>
                  </tr>
                </thead>
                <tbody>
                  {data.items.map((proc, idx) => (
                    <tr
                      key={proc.id}
                      className={`${idx % 2 === 0 ? "bg-white" : "bg-gray-50"} hover:bg-gray-100`}
                      
                    >
                      <td className="p-3">{proc.name}</td>
                      <td className="p-3">{proc.description}</td>
                      <td className="p-3">
                        <button
                          className="text-blue-600 hover:underline"
                          onClick={() => {
                            setCreateOpen(false);
                            setEditProcId(null);
                            setSelectedProcId(proc.id);
                          }}
                        >
                          상세 보기
                        </button>
                      </td>
                      <td className="p-3">
                        <button
                          className="text-green-600 hover:underline"
                          onClick={() => {
                            setSelectedProcId(null);
                            setCreateOpen(false);
                            setEditProcId(proc.id)
                          }}
                        >
                          수정
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
        <ProcessDetailSideBar
        id={selectedProcId}
        onClose={() => setSelectedProcId(null)}
        />
        
        <ProcessEditSideBar
          id={editProcId}
          onClose={() => setEditProcId(null)}
          onUpdated={refreshList}
        />
        <ProcessCreateSideBar
        isOpen={isCreateOpen}
        onClose={() => setCreateOpen(false)}
        onCreated={refreshList}
      />
    </div>
    
  );
}
