import { useState } from "react";
import { createEquipStatus } from "../../lib/equip";
import { isAxiosError } from "axios";
import { useToast } from "../../store/toast";
import EquipStatusSelect, { StatusOptions } from "./Equipstatussearch";

type RightSidebarProps = {
  isOpen: boolean;
  onClose: () => void;
  onCreated?: () => void; // 생성 후 호출
};

export default function Rightbarequ({ isOpen, onClose, onCreated }: RightSidebarProps) {
  const toast = useToast();

  const [equipmentId, setEqp] = useState(StatusOptions[0]?.equId || "");
  const [statusCode, setStatus] = useState<"RUN" | "IDLE" | "DOWN">("RUN");
  const [date, setDate] = useState(new Date().toISOString().slice(0, 10));
  const [time, setTime] = useState("00:00");
  const [endTime, setEndTime] = useState("00:00");
  const [err, setErr] = useState<string | null>(null);
  const [isSubmitting, setSubmitting] = useState(false);

  if (!isOpen) return null;

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    setErr(null);
    setSubmitting(true);

    try {
      const startIso = new Date(`${date}T${time}:00Z`).toISOString();
      const endIso = new Date(`${date}T${endTime}:00Z`).toISOString();

      console.log(equipmentId+statusCode+startIso+endIso);
      await createEquipStatus({
        equipmentId,
        statusCode,
        startTimeUtc: startIso,
        endTimeUtc: endIso,
      });
      if (onCreated) onCreated();

      toast.push("설비 상태가 등록되었습니다.", "success");

      // 폼 초기화
      setEqp("BLS-001");
      setStatus("RUN");
      setTime("00:00");
      setEndTime("00:00");

      onClose();
      if (onCreated) onCreated(); // 목록 갱신
    } catch (error: unknown) {
      const msg = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message ?? "등록 실패"
        : "등록 실패";
      setErr(msg);
      toast.push(msg, "error");
    } finally {
      setSubmitting(false);
    }
  };

  return (
    <aside className="w-96 bg-white border-l shadow fixed right-0 top-0 bottom-0 z-50 flex flex-col">
      <div className="p-4 border-b flex items-center justify-between">
        <h2 className="text-lg font-semibold">설비 상태 등록</h2>
        <button onClick={onClose} className="text-gray-500 hover:text-gray-800">✕</button>
      </div>

      <form onSubmit={submit} className="flex flex-col gap-4 p-4 overflow-y-auto">
        <div className="flex items-center gap-4">
          <label className="text-lg font-medium text-gray-700 mb-1">설비 ID</label>
          <EquipStatusSelect
      equId={equipmentId}
      options={StatusOptions}
      onChange={(v) => setEqp(v)}

    />
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700 mb-1">등록 날짜</label>
          <input
            type="date"
            value={date}
            onChange={(e) => setDate(e.target.value)}
            className="w-full border rounded px-3 py-2"
            required
          />
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700 mb-1">시작 시각</label>
          <input
            type="time"
            value={time}
            onChange={(e) => setTime(e.target.value)}
            className="w-full border rounded px-3 py-2"
            required
          />
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700 mb-1">종료 시각</label>
          <input
            type="time"
            value={endTime}
            onChange={(e) => setEndTime(e.target.value)}
            className="w-full border rounded px-3 py-2"
            required
          />
        </div>

        <div>
          <label className="text-sm font-medium text-gray-700 mb-1">상태</label>
          <select
            value={statusCode}
            onChange={(e) => setStatus(e.target.value as "RUN" | "IDLE" | "DOWN")}
            className="w-full border rounded px-3 py-2"
          >
            <option value="RUN">RUN</option>
            <option value="IDLE">IDLE</option>
            <option value="DOWN">DOWN</option>
          </select>
        </div>

        {err && <p className="text-red-600">{err}</p>}

        <button
          type="submit"
          disabled={isSubmitting}
          className="bg-green-600 hover:bg-green-700 text-white px-4 py-2 rounded"
        >
          {isSubmitting ? "등록 중..." : "등록"}
        </button>
      </form>
    </aside>
  );
}