import { useEffect, useState } from "react";
import { api } from "../../lib/api";

type Cmmsworkorder = {
  id: number;
  equipmentId: string;
  title: string;
  statusCodeId: number;
  priorityCodeId:number;
  assigneeUserId:string;
  requestId:string;
  createdAt:string;
  startedAt:string;
  finishedAt:string;
  actualMinutes:string;
  partsCost:string;
};

type CmmsworkorderResponse = {
  content: Cmmsworkorder[];
  page:number;
  size:number;
  totalElement:number;
};

export default function CmmsPage() {
  const [workorder, setPmPlans] = useState<Cmmsworkorder[]>([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setErr("");
      setLoading(true);

      const res = await api.get<CmmsworkorderResponse>("/cmms/work-orders", {
        params: {id:4, assigneeUserId: "00000000-0000-0000-0000-0000000000OP" },
      });
      console.log("작업지시 사항:", res.data.content);

      setPmPlans(res.data.content);
    } catch (err: unknown) {
      console.error("API 오류:", err);
      setPmPlans([]);
      setErr("작업지시 조회 실패");
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
      <h2 className="text-xl font-semibold mb-4">작업지시</h2>
      <table className="w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-100">
            <th className="border px-2 py-1">ID</th>
            <th className="border px-2 py-1">equipmentId</th>
            <th className="border px-7 py-1">title</th>
            <th className="border px-0.5 py-1">statusCodeId</th>
            <th className="border px-0.5 py-1">priorityCodeId</th>
            <th className="border px-2 py-1">assigneeUserId</th>
            <th className="border px-2 py-1">requestId</th>
            <th className="border px-2 py-1">createdAt</th>
            <th className="border px-2 py-1">startedAt</th>
            <th className="border px-2 py-1">finishedAt</th>
            <th className="border px-0.5 py-1">actualMinutes</th>
            <th className="border px-2 py-1">partsCost</th>
          </tr>
        </thead>
        <tbody>
          {workorder.map((w) => (
            <tr key={w.id}>
              <td className="border px-2 py-1">{w.id}</td>
              <td className="border px-2 py-1">{w.equipmentId}</td>
              <td className="border px-2 py-1">{w.title}</td>
              <td className="border px-2 py-1">{w.statusCodeId}</td>
              <td className="border px-2 py-1">{w.priorityCodeId}</td>
              <td className="border px-2 py-1">{w.assigneeUserId}</td>
              <td className="border px-2 py-1">{w.requestId}</td>
              <td className="border px-2 py-1">{w.createdAt}</td>
              <td className="border px-2 py-1">{w.startedAt}</td>
              <td className="border px-2 py-1">{w.finishedAt}</td>
              <td className="border px-2 py-1">{w.actualMinutes}</td>
              <td className="border px-2 py-1">{w.partsCost}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}