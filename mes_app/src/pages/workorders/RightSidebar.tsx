import { useState } from "react";
import { createWorkOrder } from "../../lib/wo"; // 실제 API import
import { isAxiosError } from "axios";
import { useToast } from "../../store/toast";
import WorkOrderCreate from "../workorders/WorkOrderCreate";

type RightSidebarProps = {
  isOpen: boolean;
  onClose: () => void;
  onCreated?: () => void; // 생성 후 호출
};

export default function RightSidebar({ isOpen, onClose, onCreated }: RightSidebarProps) {
  const toast = useToast();

  const [workOrderNumber, setNo] = useState("");
  const [itemId, setItem] = useState("");
  const [processId, setProc] = useState("");
  const [equipmentId, setEqp] = useState("");
  const [orderQty, setQty] = useState<number>(0);
  const [isSubmitting, setSubmitting] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const canSave =
    workOrderNumber.trim() &&
    itemId.trim() &&
    processId.trim() &&
    equipmentId.trim() &&
    orderQty > 0;

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!canSave) return;

    setSubmitting(true);
    setErr(null);

    try {
      await createWorkOrder({
        workOrderNumber,
        itemId,
        processId,
        equipmentId,
        orderQty,
      });

      toast.push("작업지시가 생성되었습니다.", "success");

      // 폼 초기화
      setNo("");
      setItem("");
      setProc("");
      setEqp("");
      setQty(0);

      onClose();
      if (onCreated) onCreated(); // 목록 갱신용 콜백
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
    <aside className="w-80 bg-white border-l shadow fixed right-0 top-0 bottom-0 z-50 flex flex-col">
      <div className="p-4 border-b flex items-center justify-between">
        <span className="text-lg font-semibold"></span>
      </div>
      <WorkOrderCreate
      onClose={onClose}
      />
    </aside>
  );
}