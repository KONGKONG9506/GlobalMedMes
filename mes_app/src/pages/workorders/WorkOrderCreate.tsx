import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { createWorkOrder } from "../../lib/wo";
import { isAxiosError } from "axios";
import { useToast } from "../../store/toast";
import { usePerms } from "../../hooks/usePerms";
 import { useQueryClient } from "@tanstack/react-query";

type WorkOrderCreateProps = {
  onClose?: () => void;
}

export default function WorkOrderCreate({onClose}: WorkOrderCreateProps) {
  const [workOrderNumber, setNo] = useState("");
  const [itemId, setItem] = useState("STENT_01");
  const [processId, setProc] = useState("STENT_PROC_A");
  const [equipmentId, setEqp] = useState("STENT_LINE_01");
  const [orderQty, setQty] = useState<number>(100);
  const [err, setErr] = useState("");
  const nav = useNavigate();
  const toast = useToast();
  const [errors, setErrors] = useState<{[k:string]: string}>({});
  const { canWrite } = usePerms();
  const [isSubmitting, setSubmitting] = useState(false);
  const canSave = canWrite && !isSubmitting;
   const qc = useQueryClient();

  function validate() {
    const e: {[k:string]: string} = {};
    if (!workOrderNumber.trim()) e.workOrderNumber = "지시번호는 필수입니다.";
    if (!itemId.trim()) e.itemId = "품목ID는 필수입니다.";
    if (!processId.trim()) e.processId = "공정ID는 필수입니다.";
    if (!equipmentId.trim()) e.equipmentId = "설비ID는 필수입니다.";
    if (orderQty < 0) e.orderQty = "지시수량은 0 이상이어야 합니다.";
    setErrors(e);
    return Object.keys(e).length === 0;
  }

  async function submit(e: React.FormEvent) {
    e.preventDefault();
    setErr("");
    if (!canWrite) { setErr("권한이 없습니다"); return; }
    if (!canSave) { setErr("입력값을 확인하세요."); return; }
    if (!validate()) return;
    try {
      setSubmitting(true);
      await createWorkOrder({ workOrderNumber, itemId, processId, equipmentId, orderQty });
      toast.push("지시가 생성되었습니다.", "success");
        await qc.invalidateQueries({ queryKey: ["work-orders"] });
      nav("/work-orders", { replace: true });
      
    } catch (error: unknown) {
      const msg = isAxiosError<{ message?: string }>(error)
        ? error.response?.data?.message ?? "생성 실패"
        : "생성 실패";
        setErr(msg);
        toast.push(msg, "error");
    } finally{
      setSubmitting(false);
    }
  }

  return (
    <div>
      <h1 className="text-lg font-semibold mb-3">Create Details</h1>
      <form onSubmit={submit} className="grid gap-3 w-full px-4">
        {err && <div className="text-red-600">{err}</div>}
        <input className="border px-3 py-2 rounded-md w-full" placeholder="지시번호" value={workOrderNumber} onChange={(e)=>setNo(e.target.value)} required />
        {errors.workOrderNumber && <div className="text-red-600 text-sm">{errors.workOrderNumber}</div>}
        <input className="border px-3 py-2 rounded-md w-full" placeholder="품목ID" value={itemId} onChange={(e)=>setItem(e.target.value)} required />
        {errors.workOrderNumber && <div className="text-red-600 text-sm">{errors.workOrderNumber}</div>}
        <input className="border px-3 py-2 rounded-md w-full" placeholder="공정ID" value={processId} onChange={(e)=>setProc(e.target.value)} required />
        {errors.workOrderNumber && <div className="text-red-600 text-sm">{errors.workOrderNumber}</div>}
        <input className="border px-3 py-2 rounded-md w-full" placeholder="설비ID" value={equipmentId} onChange={(e)=>setEqp(e.target.value)} required />
        {errors.workOrderNumber && <div className="text-red-600 text-sm">{errors.workOrderNumber}</div>}
        <input className="border px-3 py-2 rounded-md w-full" type="number" step="1" min="0" placeholder="지시수량" value={orderQty} onChange={(e)=>setQty(Number(e.target.value))} required />
        {errors.workOrderNumber && <div className="text-red-600 text-sm">{errors.workOrderNumber}</div>}
        <div className="flex gap-2 justify-end mt-2">
          <button className="bg-black text-white px-3 py-1 rounded" disabled={!canSave} type="submit">{isSubmitting ? "생성 중..." : "생성"}</button>
          <button className="border px-3 py-1 rounded" type="button" onClick={onClose}>취소</button>
        </div>
      </form>
    </div>
  );
}