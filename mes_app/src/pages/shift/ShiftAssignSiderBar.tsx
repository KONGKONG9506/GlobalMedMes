import { useState } from "react";
import { api } from "../../lib/api";
import { isAxiosError } from "axios";
import { useToast } from "../../store/toast";

type ShiftAssignSidebarProps = {
  isOpen: number | null; // calendarId
  onClose: () => void;
  onAssigned?: () => void;
};

export default function ShiftAssignSidebar({ isOpen, onClose, onAssigned }: ShiftAssignSidebarProps) {
  const toast = useToast();
  const [workerId, setWorkerId] = useState("");
  const [isSubmitting, setSubmitting] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const canSave = workerId.trim() && !!isOpen;

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!canSave || isOpen === null) return;

    setSubmitting(true);
    setErr(null);

    try {
      await api.post(`/shifts/${isOpen}/assign`, null, {
        params: { workerId },
      });

      toast.push("직원이 배치되었습니다.", "success");
      setWorkerId("");
      onClose();
      if (onAssigned) onAssigned();
    } catch (error: unknown) {
      const msg = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message ?? "직원 배치 실패"
        : "직원 배치 실패";
      setErr(msg);
      toast.push(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  if (isOpen === null) return null;

  return (
    <aside
      className="w-96 bg-white border-l shadow fixed top-0 bottom-0 right-0 z-50 flex flex-col transform transition-transform duration-300"
      style={{ transform: isOpen ? "translateX(0)" : "translateX(100%)" }}
    >
      <div className="p-4 border-b flex items-center justify-between">
        <span className="text-lg font-semibold">직원 배치</span>
        <button onClick={onClose} className="text-gray-500 hover:text-gray-700">✕</button>
      </div>

      <form onSubmit={submit} className="p-4 flex flex-col gap-3">
        <label className="flex flex-col text-sm">
          직원 ID
          <input
            type="text"
            value={workerId}
            onChange={(e) => setWorkerId(e.target.value)}
            className="border rounded px-2 py-1"
            placeholder="배치할 직원 ID 입력"
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
            {isSubmitting ? "배치 중..." : "배치"}
          </button>
        </div>
      </form>
    </aside>
  );
}
