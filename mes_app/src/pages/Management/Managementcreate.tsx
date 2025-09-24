import { useEffect, useState } from "react";
import { api } from "../../lib/api";
// import ManagementDetail from "./Managementdetail";

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
  const [search,setSearch] = useState(""); // 검색 상태
  const [selectedEmployee, setSelectedEmployee] = useState<employee | null>(null); // 모달 대상

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

  const filteredemployees = employee.filter((e) =>
    e.employeeId.slice(-2).toLowerCase().includes(search.toLowerCase())
  );


  return (
    <div className="p-4">
      <h2 className="text-xl font-semibold mb-4">작업자 관리</h2>
      {/* 🔍 검색창 */}
      <div className="mb-4 flex items-center gap-2">
        <span className="text-gray-500 font-mono">
          00000000-0000-0000-0000-0000000000
        </span>
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="employeeId 검색"
          className="border border-gray-300 rounded px-2 py-1 w-16 text-center focus:outline-none focus:ring-2 focus:ring-blue-400"
          maxLength={2} // 두 자리가지만 입력 가능 
        />
        <button
          onClick={() => setSearch("")}
          className="px-3 py-1 bg-blue-500 text-white rounded hover:bg-blue-600 transition"
        >
          초기화
        </button>
      </div>
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
          {filteredemployees.map((E) => (
            <tr key={E.employeeId}>
              <td className="border px-2 py-1">{E.employeeId}</td>
              <td className="border px-2 py-1">{E.employeeName}</td>
              <td className="border px-2 py-1">{E.employeeNumber}</td>
              <td className="border px-2 py-1">{E.allowedEquipment}</td>
              <td className="border px-2 py-1">{E.allowedProcess}</td>
              <td className="border px-2 py-1 text-center">
              <button
                className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
                 onClick={() => setSelectedEmployee(E)}
              >
                상세보기
              </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
      </div>
  );
}