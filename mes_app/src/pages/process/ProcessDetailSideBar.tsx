import { api } from "../../lib/api"
import { useQuery } from "@tanstack/react-query";
import { ProcessDetailDto } from "../../types/process";

type Props = {
  id: string | null;
  onClose: () => void;
};

export default function ProcessDetailSideBar({ id, onClose }: Props) {
  const { data, isLoading, error } = useQuery<ProcessDetailDto>({
    queryKey: ["processDetail", id],
    queryFn: async () => {
      const res = await api.get<ProcessDetailDto>(`/process/detail/${id}`);
      return res.data;
    },
    enabled: !!id,
  });

  if (!id) return null;

  return (
    <aside
      className="w-96 bg-white border-l shadow fixed top-0 bottom-0 right-0 z-50 p-6 overflow-auto transform transition-transform duration-300"
      style={{ transform: id ? "translateX(0)" : "translateX(100%)" }}
    >
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold">공정 상세 정보</h2>
        <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
          ✕
        </button>
      </div>

      {isLoading && <div>로딩 중...</div>}
      {error && <div className="text-red-500">데이터 로딩 실패</div>}

      {data && (
        <div className="space-y-4">
          <div className="border rounded p-4 shadow-sm">
            <p><strong>ID:</strong> {data.id}</p>
            <p><strong>공정명:</strong> {data.name}</p>
            <p><strong>설명:</strong> {data.description}</p>
            <p><strong>마지막 수정자:</strong> {data.LastmodBy}</p>
            <p><strong>마지막 수정일:</strong> {data.LastmodAt ? new Date(data.LastmodAt.slice(0, -1)).toLocaleString() : "-"}</p>
          </div>

          <div className="border rounded p-4 shadow-sm">
            <h3 className="font-semibold mb-2">설비 목록</h3>
            {data.equipments.length > 0 ? (
              <ul className="list-disc pl-5">
                {data.equipments.map(eq => (
                  <li key={eq.equipmentId}>{eq.equipmentName} ({eq.statusCode})</li>
                ))}
              </ul>
            ) : <p>없음</p>}
          </div>

          <div className="border rounded p-4 shadow-sm">
            <h3 className="font-semibold mb-2">필요 자격증</h3>
            {data.requiredCerts.length > 0 ? (
              <ul className="list-disc pl-5">
                {data.requiredCerts.map(cert => (
                  <li key={cert.certCode}>{cert.certName}</li>
                ))}
              </ul>
            ) : <p>없음</p>}
          </div>
        </div>
      )}
    </aside>
  );
}
