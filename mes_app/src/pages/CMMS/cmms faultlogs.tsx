import { useEffect, useState } from "react";
import { api } from "../../lib/api";

type CmmsFaultLog = {
  id: number;
  equipmentId: string;
  lossCategoryCodeId: number;
  symptom: string;
  action: string;
  occurredAt: string;
  resolveAt: string;
  workOrderId: string;
};

type CmmsFaultResponse = {
  content: CmmsFaultLog[];
  page: number;
  size: number;
  totalElements: number;
};

export default function CmmsFault() {
  const [fault, setFault] = useState<CmmsFaultLog[]>([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setErr("");
      setLoading(true);

      const res = await api.get<CmmsFaultResponse>("/cmms/fault-logs", {
        params: {equipmentId: "E-0001",from: "2025-09-04T00:00:00Z",to: "2025-09-06T00:00:00Z",sort: "occurredAt,desc"},
      });

      console.log("고장 수리:", res.data.content);
      setFault(res.data.content);
    } catch (err: unknown) {
      console.error("API 오류:", err);
      setFault([]);
      setErr("고장 수리 조회 불가");
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
      <h2 className="text-xl font-semibold mb-4">고장 수리</h2>
      <table className="w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-100">
            <th className="border px-2 py-1">ID</th>
            <th className="border px-2 py-1">Equipment ID</th>
            <th className="border px-2 py-1">Loss Category</th>
            <th className="border px-2 py-1">Symptom</th>
            <th className="border px-2 py-1">Action</th>
            <th className="border px-2 py-1">Occurred At</th>
            <th className="border px-2 py-1">Resolve At</th>
            <th className="border px-2 py-1">Work Order ID</th>
          </tr>
        </thead>
        <tbody>
          {fault.map((f) => (
            <tr key={f.id}>
              <td className="border px-2 py-1">{f.id}</td>
              <td className="border px-2 py-1">{f.equipmentId}</td>
              <td className="border px-2 py-1">{f.lossCategoryCodeId}</td>
              <td className="border px-2 py-1">{f.symptom}</td>
              <td className="border px-2 py-1">{f.action}</td>
              <td className="border px-2 py-1">{f.occurredAt}</td>
              <td className="border px-2 py-1">{f.resolveAt}</td>
              <td className="border px-2 py-1">{f.workOrderId}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}