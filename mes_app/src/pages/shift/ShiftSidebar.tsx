import { useState } from "react";
import { api } from "../../lib/api";
import { isAxiosError } from "axios";
import { useToast } from "../../store/toast";
import { ShiftEquipLists, WorkcenterMap } from "./ShiftList";

type ShiftSidebarProps = {
  isOpen: boolean;
  onClose: () => void;
  onCreated?: () => void; // 생성 후 목록 갱신용 콜백
};

export default function ShiftSidebar({ isOpen, onClose, onCreated }: ShiftSidebarProps) {
  const toast = useToast();

  const today = new Date().toISOString().slice(0, 10);
  const [date, setDate] = useState(today); // <-- 이 부분을 수정
  const [selectedEqu, setSelectedEqu] = useState(ShiftEquipLists[0]);
  const [isSubmitting, setSubmitting] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const equipmentId = selectedEqu.equId;
  const workcenterId = selectedEqu.workcenterId;
  const workcenterName = WorkcenterMap[workcenterId];

  const canSave = date.trim() && equipmentId.trim() && workcenterId.trim();

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!canSave) return;

    setSubmitting(true);
    setErr(null);

    try {
      await api.post("/shifts/calendars/generate", null, {
        params: {
          date,
          equipmentId,
          workcenterId,
        },
      });

      toast.push("교대 캘린더가 생성되었습니다.", "success");

      // 폼 초기화
      setDate("");
      setSelectedEqu(ShiftEquipLists[0]);

      onClose();
      if (onCreated) onCreated();
    } catch (error: unknown) {
      const msg = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message ?? "생성 실패"
        : "생성 실패";
      setErr(msg);
      toast.push(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  if (!isOpen) return null;

  return (
    <aside
      className="w-96 bg-white border-l shadow fixed top-0 bottom-0 right-0 z-50 flex flex-col transform transition-transform duration-300"
      style={{ transform: isOpen ? "translateX(0)" : "translateX(100%)" }}
    >
      <div className="p-4 border-b flex items-center justify-between">
        <span className="text-lg font-semibold">교대 캘린더 생성</span>
        <button onClick={onClose} className="text-gray-500 hover:text-gray-700">✕</button>
      </div>

      <form onSubmit={submit} className="p-4 flex flex-col gap-3">
        <label className="flex flex-col text-sm">
          날짜
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="border rounded px-2 py-1"
          />
        </label>

        {/* 설비 선택 */}
        <label className="flex flex-col text-sm">
          설비
          <select
            className="border px-2 py-1 rounded"
            value={selectedEqu.equId}
            onChange={(e) => {
              const next = ShiftEquipLists.find((opt) => opt.equId === e.target.value);
              if (next) setSelectedEqu(next);
            }}
          >
            {ShiftEquipLists.map((opt) => (
              <option key={opt.equId} value={opt.equId}>
                {opt.name}
              </option>
            ))}
          </select>
        </label>

        {/* 워크센터 자동 표시 */}
        <label className="flex flex-col text-sm">
          워크센터
          <input
            type="text"
            value={workcenterName}
            className="border rounded px-2 py-1 bg-gray-100"
            readOnly
          />
        </label>

        {err && <div className="text-sm text-red-600">{err}</div>}

        <div className="mt-4 flex justify-end gap-2">
          <button type="button" onClick={onClose} className="px-4 py-2 border rounded">
            취소
          </button>
          <button
            type="submit"
            disabled={!canSave || isSubmitting}
            className="px-4 py-2 bg-indigo-600 text-white rounded hover:bg-indigo-700 disabled:bg-gray-400"
          >
            {isSubmitting ? "생성 중..." : "생성"}
          </button>
        </div>
      </form>
    </aside>
  );
}
