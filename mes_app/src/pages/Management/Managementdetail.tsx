// import { useState, useEffect } from "react";
// import { api } from "../../lib/api";

// type Employee = {
//   employeeId: string;
//   employeeName: string;
//   employeeNumber: string;
//   allowedEquipment: string;
//   allowedProcess: string;
// };

// export default function ManagementDetail() {
//   const [employees, setEmployees] = useState<Employee[]>([]);
//   const [loading, setLoading] = useState(true);
//   const [err, setErr] = useState("");
//   const [selectedEmployee, setSelectedEmployee] = useState<Employee | null>(null);

//   // 데이터 불러오기
//   const load = async () => {
//     try {
//       setErr("");
//       setLoading(true);
//       const res = await api.get("/employees", { params: { page: 0, size: 50 } });
//       setEmployees(res.data.content);
//     } catch (err: unknown) {
//       console.error(err);
//       setErr("인원 조회 실패");
//       setEmployees([]);
//     } finally {
//       setLoading(false);
//     }
//   };

//   useEffect(() => {
//     load();
//   }, []);

//   if (loading) return <div>불러오는 중...</div>;
//   if (err) return <div className="text-red-500">{err}</div>;

//   return (
//     <div className="p-4">
//       <h2 className="text-xl font-semibold mb-4">작업자 관리</h2>

//       <table className="w-full border-collapse border border-gray-300">
//         <thead>
//           <tr className="bg-gray-100">
//             <th className="border px-2 py-1">employeeId</th>
//             <th className="border px-2 py-1">employeeName</th>
//             <th className="border px-2 py-1">employeeNumber</th>
//             <th className="border px-2 py-1">allowedEquipment</th>
//             <th className="border px-2 py-1">allowedProcess</th>
//             <th className="border px-2 py-1">Actions</th>
//           </tr>
//         </thead>
//         <tbody>
//           {employees.map((e) => (
//             <tr key={e.employeeId}>
//               <td className="border px-2 py-1">{e.employeeId}</td>
//               <td className="border px-2 py-1">{e.employeeName}</td>
//               <td className="border px-2 py-1">{e.employeeNumber}</td>
//               <td className="border px-2 py-1">{e.allowedEquipment}</td>
//               <td className="border px-2 py-1">{e.allowedProcess}</td>
//               <td className="border px-2 py-1">
//                 <button
//                   className="px-2 py-1 bg-blue-500 text-white rounded hover:bg-blue-600"
//                   onClick={() =>
//                     setSelectedEmployee(
//                       selectedEmployee?.employeeId === e.employeeId ? null : e
//                     )
//                   }
//                 >
//                   {selectedEmployee?.employeeId === e.employeeId ? "닫기" : "상세보기"}
//                 </button>
//               </td>
//             </tr>
//           ))}
//         </tbody>
//       </table>

//       {/* 선택된 직원 모달 */}
//       {selectedEmployee && (
//         <div className="bg-white border p-4 rounded shadow mt-2">
//           <h3 className="text-lg font-semibold mb-2">직원 상세 정보</h3>
//           <p><strong>이름:</strong> {selectedEmployee.employeeName}</p>
//           <p><strong>사번:</strong> {selectedEmployee.employeeNumber}</p>
//           <p><strong>허용 장비:</strong> {selectedEmployee.allowedEquipment}</p>
//           <p><strong>허용 공정:</strong> {selectedEmployee.allowedProcess}</p>

//           <div className="mt-2 flex justify-end gap-2">
//             <button
//               className="px-2 py-1 bg-gray-300 rounded hover:bg-gray-400"
//               onClick={() => setSelectedEmployee(null)}
//             >
//               닫기
//             </button>
//             <button
//               className="px-2 py-1 bg-green-500 text-white rounded hover:bg-green-600"
//               onClick={() => alert("수정 기능 연결 가능")}
//             >
//               수정
//             </button>
//           </div>
//         </div>
//       )}
//     </div>
//   );
// }