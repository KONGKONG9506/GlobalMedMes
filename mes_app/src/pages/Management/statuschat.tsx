import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import StatusChat from "./statuschat";

type Employee = {
  employeeId: string;
  employeeName: string;
  employeeNumber: string;
  allowedEquipment: string;
  allowedProcess: string;
  departmentId: string; // 직원이 속한 부서
};

type EmployeeResponse = {
  content: Employee[];
  page: number;
  size: number;
  numberElement: number;
};

export default function EmployeePage() {
  const [employees, setEmployees] = useState<Employee[]>([]);
  const [err, setErr] = useState("");
  const [loading, setLoading] = useState(true);
  const [selectedDept, setSelectedDept] = useState<string>("company");

  const load = async () => {
    try {
      setErr("");
      setLoading(true);
      const res = await api.get<EmployeeResponse>("/employees", {
        params: { page: 0, size: 50 },
      });
      setEmployees(res.data.content);
    } catch (err: unknown) {
      console.error("API 오류:", err);
      setEmployees([]);
      setErr("직원 조회 실패");
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    load();
  }, []);

  const filtered = employees.filter((e) => e.departmentId === selectedDept);

  return (
    <div className="flex">
      {/* 왼쪽 조직도 */}
      <StatusChat onSelectDepartment={setSelectedDept} />

      {/* 오른쪽 직원 테이블 */} 
      <div className="p-4 flex-1">
        <h2 className="text-xl font-semibold mb-4">작업자 관리</h2>
        {loading ? (
          <div>불러오는 중...</div>
        ) : err ? (
          <div className="text-red-500">{err}</div>
        ) : (
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
              {filtered.map((e) => (
                <tr key={e.employeeId}>
                  <td className="border px-2 py-1">{e.employeeId}</td>
                  <td className="border px-2 py-1">{e.employeeName}</td>
                  <td className="border px-2 py-1">{e.employeeNumber}</td>
                  <td className="border px-2 py-1">{e.allowedEquipment}</td>
                  <td className="border px-2 py-1">{e.allowedProcess}</td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>
    </div>
  );
}