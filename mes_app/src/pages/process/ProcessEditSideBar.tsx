import { useState, useEffect } from "react";
import { api } from "../../lib/api";
import { useToast } from "../../store/toast";
import { isAxiosError } from "axios";

type Cert = {
  certCode: string;
  certName: string;
};

type Props = {
  id: string | null;
  onClose: () => void;
  onUpdated?: () => void; // 수정 후 목록 새로고침용
};

export default function ProcessEditSideBar({ id, onClose, onUpdated }: Props) {
  const toast = useToast();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [certs, setCerts] = useState<Cert[]>([]); // 현재 적용된 자격증 목록
  const [query, setQuery] = useState(""); // 검색어
  const [searchResults, setSearchResults] = useState<Cert[]>([]); // 검색 결과
  const [isSubmitting, setSubmitting] = useState(false);

  // 기존 데이터 불러오기
  useEffect(() => {
    if (!id) return;
    (async () => {
      try {
        const res = await api.get(`/process/detail/${id}`);
        setName(res.data.name);
        setDescription(res.data.description || "");
        setCerts(res.data.requiredCerts || []);
      } catch (err) {
        toast.push("공정 정보를 불러오지 못했습니다.", "error");
      }
    })();
  }, [id]);

  const handleSearch = async () => {
    if (!query.trim()) {
      setSearchResults([]);
      return;
    }
    try {
      const res = await api.get(`/cert`, { params: { keyword: query } }); 
      const results: Cert[] = res.data;
      setSearchResults(results.filter(c => !certs.some(cert => cert.certCode === c.certCode)));
    } catch (err) {
      toast.push("자격증 검색 실패", "error");
    }
  };

  const handleAddCert = (cert: Cert) => {
    if (certs.some((c) => c.certCode === cert.certCode)) return;
    setCerts([...certs, cert]);
    setSearchResults([]);
    setQuery("");
  };

  const handleRemoveCert = (certCode: string) => {
    setCerts(certs.filter((c) => c.certCode !== certCode));
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;

    setSubmitting(true);
    try {
      await api.put(`/process/${id}`, { processName: name, description, requiredCertCodes: certs.map((c) => c.certCode), });
      toast.push("공정이 수정되었습니다.", "success");
      if (onUpdated) onUpdated();
      onClose();
    } catch (error) {
      const msg = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message ?? "수정 실패"
        : "수정 실패";
      toast.push(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  if (!id) return null;

  return (
    <aside
      className="w-96 bg-white border-l shadow fixed top-0 bottom-0 right-0 z-50 p-6 overflow-auto transform transition-transform duration-300"
      style={{ transform: id ? "translateX(0)" : "translateX(100%)" }}
    >
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold">공정 수정</h2>
        <button onClick={onClose} className="text-gray-500 hover:text-gray-700">
          ✕
        </button>
      </div>

      <form onSubmit={handleSubmit} className="flex flex-col gap-3">
        <label className="flex flex-col text-sm">
          공정명
          <input
            type="text"
            value={name}
            onChange={(e) => setName(e.target.value)}
            className="border rounded px-2 py-1"
          />
        </label>

        <label className="flex flex-col text-sm">
          설명
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="border rounded px-2 py-1"
            rows={4}
          />
        </label>
        <div>
          <h3 className="font-semibold mb-2 text-sm">현재 자격증 목록</h3>
          <ul className="border rounded p-2 max-h-32 overflow-y-auto">
            {certs.length === 0 && (
              <li className="text-gray-400 text-sm">적용된 자격증이 없습니다.</li>
            )}
            {certs.map((cert) => (
              <li
                key={cert.certCode}
                className="flex justify-between items-center text-sm border-b last:border-none py-1"
              >
                <span>{cert.certName} ({cert.certCode})</span>
                <button
                  type="button"
                  onClick={() => handleRemoveCert(cert.certCode)}
                  className="text-red-500 hover:text-red-700 text-xs"
                >
                  제거
                </button>
              </li>
            ))}
          </ul>
        </div>

        {/* ✅ 자격증 검색 */}
        <div>
          <h3 className="font-semibold mb-2 text-sm">자격증 검색</h3>
          <div className="flex gap-2 mb-2">
            <input
              type="text"
              placeholder="자격증 이름 또는 코드로 검색"
              value={query}
              onChange={(e) => setQuery(e.target.value)}
              className="border rounded px-2 py-1 flex-1"
              onKeyDown={(e) => e.key === "Enter" && (e.preventDefault(), handleSearch())}
            />
            <button
              type="button"
              onClick={handleSearch}
              className="px-3 py-1 bg-indigo-600 text-white rounded hover:bg-indigo-700"
            >
              검색
            </button>
          </div>

          {/* 검색 결과 표시 */}
          {searchResults.length > 0 && (
            <ul className="border rounded p-2 max-h-32 overflow-y-auto">
              {searchResults.map((cert) => (
                <li
                  key={cert.certCode}
                  className="flex justify-between items-center text-sm border-b last:border-none py-1"
                >
                  <span>{cert.certName} ({cert.certCode})</span>
                  <button
                    type="button"
                    onClick={() => handleAddCert(cert)}
                    className="text-indigo-600 hover:text-indigo-800 text-xs"
                  >
                    추가
                  </button>
                </li>
              ))}
            </ul>
          )}
        </div>

        <div className="flex justify-end gap-2 mt-4">
          <button
            type="button"
            onClick={onClose}
            className="px-4 py-2 border rounded"
          >
            취소
          </button>
            <button
                type="button"
                onClick={async () => {
                if (!id) return;
                if (!confirm("정말 이 공정을 삭제하시겠습니까?")) return;
                try {
                    setSubmitting(true);
                    await api.delete(`/process/${id}`);
                    toast.push("공정이 삭제되었습니다.", "success");
                    if (onUpdated) onUpdated();
                    onClose();
                } catch (err) {
                    toast.push("삭제 실패", "error");
                } finally {
                    setSubmitting(false);
                }
                }}
                className="px-4 py-2 bg-red-600 text-white rounded hover:bg-red-700"
            >
                삭제
            </button>             
          <button
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:bg-gray-400"
          >
            {isSubmitting ? "저장 중..." : "저장"}
          </button>       
        </div>
      </form>
    </aside>
  );
}
