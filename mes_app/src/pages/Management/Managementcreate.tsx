import { useEffect, useState } from "react";
import { api } from "../../lib/api";

type employee = {
  employeeId: string;
  employeeName: string;
  employeeNumber:string;
  allowedEquipment:string;
  allowedProcess:string;
};

type employeeResponse = {
  content: employee[];
  page:number;
  size:number;
  numberElement:number;
};

export default function employee() {
  const [employee, setemployee] = useState<employee[]>([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(true);

  const load = async () => {
    try {
      setErr("");
      setLoading(true);

      const res = await api.get<employeeResponse>("/employees", {
        params: {page:0, size:10},
      });
      console.log("인원 관리:", res.data.content);

      setemployee(res.data.content);
    } catch (err: unknown) {
      console.error("API 오류:", err);
      setemployee([]);
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
      <h2 className="text-xl font-semibold mb-4">작업자 관리</h2>
      <table className="w-full border-collapse border border-gray-300">
        <thead>
          <tr className="bg-gray-100">
            <th className="border px-2 py-1">employeeId</th>
            <th className="border px-2 py-1">employeeName</th>
            <th className="border px-2 py-1">employeeNumber</th>
            <th className="border px-2 py-1">allowedEquipment</th>
            <th className="border px-2 py-1">allowedProcess</th>
          </tr>
        </thead>
        <tbody>
          {employee.map((E) => (
            <tr key={E.employeeId}>
              <td className="border px-2 py-1">{E.employeeId}</td>
              <td className="border px-2 py-1">{E.employeeName}</td>
              <td className="border px-2 py-1">{E.employeeNumber}</td>
              <td className="border px-2 py-1">{E.allowedEquipment}</td>
              <td className="border px-2 py-1">{E.allowedProcess}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}