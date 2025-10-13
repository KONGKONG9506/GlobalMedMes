import { useState } from "react";
import { api } from "../../lib/api";
import { useToast } from "../../store/toast";
import { isAxiosError } from "axios";

type Cert = {
  certCode: string;
  certName: string;
};

type Props = {
  isOpen: boolean;
  onClose: () => void;
  onCreated?: () => void; // 생성 후 목록 새로고침용
};

export default function ProcessCreateSideBar({ isOpen, onClose, onCreated }: Props) {
  const toast = useToast();
  const [name, setName] = useState("");
  const [description, setDescription] = useState("");
  const [certs, setCerts] = useState<Cert[]>([]);
  const [query, setQuery] = useState("");
  const [searchResults, setSearchResults] = useState<Cert[]>([]);
  const [isSubmitting, setSubmitting] = useState(false);

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
      await api.post(`/process`, {
        processName: name,
        description,
        requiredCertCodes: certs.map((c) => c.certCode),
      });
      toast.push("공정이 생성되었습니다.", "success");
      if (onCreated) onCreated();
      onClose();
    } catch (error) {
      const msg = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message ?? "생성 실패"
        : "생성 실패";
      toast.push(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <aside
      className={`w-96 bg-white border-l shadow fixed top-0 bottom-0 right-0 z-50 p-6 overflow-auto transform transition-transform duration-300 ${
        isOpen ? "translate-x-0" : "translate-x-full"
      }`}
    >
      <div className="flex justify-between items-center mb-4">
        <h2 className="text-lg font-semibold">새 공정 생성</h2>
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
            placeholder="공정명을 입력하세요"
          />
        </label>

        <label className="flex flex-col text-sm">
          설명
          <textarea
            value={description}
            onChange={(e) => setDescription(e.target.value)}
            className="border rounded px-2 py-1"
            rows={4}
            placeholder="공정 설명을 입력하세요"
          />
        </label>

        <div>
          <h3 className="font-semibold mb-2 text-sm">적용할 자격증</h3>
          <ul className="border rounded p-2 max-h-32 overflow-y-auto">
            {certs.length === 0 && (
              <li className="text-gray-400 text-sm">선택된 자격증이 없습니다.</li>
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
            type="submit"
            disabled={isSubmitting}
            className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:bg-gray-400"
          >
            {isSubmitting ? "생성 중..." : "생성"}
          </button>
        </div>
      </form>
    </aside>
  );
}
