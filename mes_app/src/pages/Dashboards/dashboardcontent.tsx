// type KpiSummary = {
//   yield: number;
//   productivity: number;
//   oee: number;
// };

// type EquipmentStatus = {
//   id: string;
//   status: "운영 중" | "정지" | "경고";
// };

// type DashboardContentProps = {
//   kpi: KpiSummary | null;
//   equipmentList: EquipmentStatus[];
// };

// export default function DashboardContent({ kpi, equipmentList }: DashboardContentProps) {
//   return (
//     <div className="p-6 bg-gray-50 min-h-screen">
//       {/* 헤더 */}
//       <div className="flex justify-between items-center mb-6">
//         <div>
//           <h1 className="text-2xl font-bold">Dashboard</h1>
//         </div>
//       </div>

//       {/* 메인 레이아웃 */}
//       <div className="grid grid-cols-3 gap-6">
//         {/* 왼쪽 (2열) */}
//         <div className="col-span-2 space-y-6">
//           {/* KPI 카드 */}
//           <div className="bg-white rounded-xl shadow p-6">
//             <h2 className="text-lg font-semibold mb-4">Production Overview</h2>
//             {kpi ? (
//               <div className="grid grid-cols-3 gap-4">
//                 <div className="p-4 bg-yellow-50 rounded-lg text-center">
//                   <p className="text-sm text-gray-600">Yield</p>
//                   <p className="text-xl font-bold">{kpi.yield.toFixed(1)}%</p>
//                 </div>
//                 <div className="p-4 bg-green-50 rounded-lg text-center">
//                   <p className="text-sm text-gray-600">Productivity</p>
//                   <p className="text-xl font-bold">{kpi.productivity.toFixed(1)}%</p>
//                 </div>
//                 <div className="p-4 bg-red-50 rounded-lg text-center">
//                   <p className="text-sm text-gray-600">OEE</p>
//                   <p className="text-xl font-bold">{kpi.oee.toFixed(1)}%</p>
//                 </div>
//               </div>
//             ) : (
//               <p className="text-gray-400">Loading KPI...</p>
//             )}
//           </div>

//           {/* 차트 카드 */}
//           <div className="bg-white rounded-xl shadow p-6">
//             <h2 className="text-lg font-semibold mb-4">Production Cost</h2>
//             <div className="h-40 flex items-center justify-center text-gray-400">
//               📊 Chart Placeholder

//             </div>
//           </div>

//           {/* 다운타임 원인 */}
//           <div className="bg-white rounded-xl shadow p-6">
//             <h2 className="text-lg font-semibold mb-4">Downtimes by Cause</h2>
//             <div className="h-32 flex items-center justify-center text-gray-400">
//               🥧 PieChart Placeholder
//             </div>
//           </div>
//         </div>

//         {/* 오른쪽 (1열) */}
//         <div className="col-span-1 space-y-6">
//           {/* 장비 상태 */}
//           <div className="bg-white rounded-xl shadow p-6">
//             <h2 className="text-lg font-semibold mb-4">Machine Setup</h2>
//             <div className="grid grid-cols-2 gap-2">
//               {equipmentList.length > 0 ? (
//                 equipmentList.map((eq) => (
//                   <div
//                     key={eq.id}
//                     className={`p-2 rounded-lg text-sm text-center ${
//                       eq.status === "운영 중"
//                         ? "bg-green-100 text-green-700"
//                         : eq.status === "정지"
//                         ? "bg-red-100 text-red-700"
//                         : "bg-yellow-100 text-yellow-700"
//                     }`}
//                   >
//                     {eq.id} <br /> {eq.status}
//                   </div>
//                 ))
//               ) : (
//                 <p className="col-span-2 text-gray-400">No Equipment Data</p>
//               )}
//             </div>
//           </div>

//           {/* 작업자 할당 */}
//           <div className="bg-white rounded-xl shadow p-6">
//             <h2 className="text-lg font-semibold mb-4">Machine Assign by Operator</h2>
//             <div className="h-32 flex items-center justify-center text-gray-400">
//               👷 Operator Table Placeholder
//             </div>
//           </div>

//           {/* 품질 & 효율 */}
//           <div className="bg-white rounded-xl shadow p-6">
//             <h2 className="text-lg font-semibold mb-4">Quality & Efficiency</h2>
//             <div className="grid grid-cols-2 gap-4 text-center">
//               <div className="p-2 bg-blue-50 rounded-lg">
//                 <p className="text-sm">Quality</p>
//                 <p className="text-lg font-bold">98.5%</p>
//               </div>
//               <div className="p-2 bg-green-50 rounded-lg">
//                 <p className="text-sm">Efficiency</p>
//                 <p className="text-lg font-bold">90%</p>
//               </div>
//             </div>
//           </div>
//         </div>
//       </div>
//     </div>
//   );
// }