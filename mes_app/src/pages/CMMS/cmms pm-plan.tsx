import { useEffect, useState } from "react";
import { api } from "../../lib/api";

type CmmsPmPlan = {
  id: number;
  equipmentId: string;
  taskName: string;
  cycleTypeCodeId: number;
  cycleValue:number;
  lastDoneAt:string;
  nextDueAt:string;
  status:string;
};

type CmmsApiResponse = {
  content: CmmsPmPlan[];
  page:number;
  size:number;
  totalElement:number;
};

export default function CmmsPage() {
  const [pmPlans, setPmPlans] = useState<CmmsPmPlan[]>([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setErr("");
      setLoading(true);

      const res = await api.get<CmmsApiResponse>("/api/cmms/pm-plans/due", {
        params: { to: "2025-09-30T00:00:00Z", equipmentId : "E-0001" , sort : "nextDueAt,asc"},
      });
      console.log("PM 계획 데이터:", res.data.content);

      setPmPlans(res.data.content);
    } catch (err: unknown) {
      console.error("API 오류:", err);
      setPmPlans([]);
      setErr("PM 계획 조회 실패");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  if (loading) return <div>불러오는 중...</div>;
  if (err) return <div className="text-red-500">{err}</div>;

  return (
    <div className="p-4">
      <h2 className="text-xl font-semibold mb-4">PM 계획</h2>
      <table className="w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-100">
            <th className="border px-2 py-1">ID</th>
            <th className="border px-2 py-1">Equipment ID</th>
            <th className="border px-2 py-1">Task Name</th>
            <th className="border px-2 py-1">Cycle Type Code</th>
            <th className="border px-2 py-1">Cycle Value</th>
            <th className="border px-2 py-1">Last Done</th>
            <th className="border px-2 py-1">Next Due</th>
            <th className="border px-2 py-1">Status</th>
          </tr>
        </thead>
        <tbody>
          {pmPlans.map((plan) => (
            <tr key={plan.id}>
              <td className="border px-2 py-1">{plan.id}</td>
              <td className="border px-2 py-1">{plan.equipmentId}</td>
              <td className="border px-2 py-1">{plan.taskName}</td>
              <td className="border px-2 py-1">{plan.cycleTypeCodeId}</td>
              <td className="border px-2 py-1">{plan.cycleValue}</td>
              <td className="border px-2 py-1">{plan.lastDoneAt}</td>
              <td className="border px-2 py-1">{plan.nextDueAt}</td>
              <td className="border px-2 py-1">{plan.status}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}