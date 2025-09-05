import React, { useState, useMemo } from "react";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  BarElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Bar } from "react-chartjs-2";

ChartJS.register(CategoryScale, LinearScale, BarElement, Title, Tooltip, Legend);

type Mode = "list" | "form";

type Inspection = {
  id: number;
  itemName: string;
  result: "합격" | "불량";
  inspector: string;
  date: string;
};

const initialData: Inspection[] = [
  { id: 1, itemName: "제품 A", result: "합격", inspector: "장범수", date: "2025-08-30" },
  { id: 2, itemName: "제품 B", result: "불량", inspector: "이상욱", date: "2025-08-29" },
  { id: 3, itemName: "제품 A", result: "합격", inspector: "서성우", date: "2025-08-28" },
  { id: 4, itemName: "제품 C", result: "불량", inspector: "김태형", date: "2025-08-28" },
];

const QualityPage: React.FC = () => {
  const [mode, setMode] = useState<Mode>("list");
  const [data, setData] = useState<Inspection[]>(initialData);

  const [itemName, setItemName] = useState("");
  const [result, setResult] = useState<"합격" | "불량">("합격");
  const [inspector, setInspector] = useState("");

  // 검색어 상태
  const [searchTerm, setSearchTerm] = useState("");

  // 검색된 데이터만 필터링
  const filteredData = useMemo(() => {
    if (!searchTerm) return data;
    return data.filter((d) =>
      d.itemName.toLowerCase().includes(searchTerm.toLowerCase())
    );
  }, [data, searchTerm]);

  // 불량률 계산 (제품별)
  const defectRates = useMemo(() => {
    const productMap: Record<string, { total: number; defect: number }> = {};

    data.forEach(({ itemName, result }) => {
      if (!productMap[itemName]) productMap[itemName] = { total: 0, defect: 0 };
      productMap[itemName].total += 1;
      if (result === "불량") productMap[itemName].defect += 1;
    });

    // 제품명, 불량률(%) 배열 반환
    return Object.entries(productMap).map(([item, stats]) => ({
      itemName: item,
      defectRate: (stats.defect / stats.total) * 100,
    }));
  }, [data]);

  // 차트 데이터 세팅
  const chartData = {
    labels: defectRates.map((d) => d.itemName),
    datasets: [
      {
        label: "불량률 (%)",
        data: defectRates.map((d) => d.defectRate.toFixed(2)),
        backgroundColor: "rgba(255, 99, 132, 0.6)",
      },
    ],
  };

  const chartOptions = {
    responsive: true,
    plugins: {
      legend: { position: "top" as const },
      title: {
        display: true,
        text: "제품별 불량률 현황",
        font: { size: 18 },
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 100,
        ticks: {
          callback: (value: any) => value + "%",
        },
      },
    },
  };

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    const newEntry: Inspection = {
      id: data.length + 1,
      itemName,
      result,
      inspector,
      date: new Date().toISOString().split("T")[0],
    };
    setData([newEntry, ...data]);
    setMode("list");
    setItemName("");
    setResult("합격");
    setInspector("");
  };

  return (
    <div className="min-h-screen bg-white-100 p-6">
      <h1 className="text-2xl font-bold mb-6">품질 관리</h1>

      {/* 검색/필터 */}
      {mode === "list" && (
        <div className="mb-6 flex justify-between items-center max-w-md">
          <input
            type="text"
            placeholder="제품명으로 검색..."
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            className="w-full px-4 py-2 border rounded shadow-sm focus:outline-none focus:ring-2 focus:ring-blue-500"
          />
       <button
       onClick={() => setSearchTerm("")}
       className="ml-2 bg-gray-300 hover:bg-gray-300 text-gray-600 px-5 py-2.5 rounded text-sm transition"
       >
       Reset
       </button>
        </div>
      )}

      {/* 검사 목록 */}
      {mode === "list" && (
        <div className="bg-white rounded shadow p-4 mb-8">
          <div className="flex justify-between items-center mb-4">
            <h2 className="text-lg font-semibold">검사 현황 목록</h2>
            <button
              className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700 transition"
              onClick={() => setMode("form")}
            >  
              + 신규 검사 등록
            </button>
          </div>

          <table className="w-full border text-center text-sm">
            <thead className="bg-gray-100">
              <tr>
                <th className="border px-2 py-1">ID</th>
                <th className="border px-2 py-1">제품명</th>
                <th className="border px-2 py-1">결과</th>
                <th className="border px-2 py-1">검사자</th>
                <th className="border px-2 py-1">날짜</th>
              </tr>
            </thead>
            <tbody>
              {filteredData.length === 0 ? (
                <tr>
                  <td colSpan={5} className="py-4">
                    결과가 없습니다.
                  </td>
                </tr>
              ) : (
                filteredData.map((item) => (
                  <tr key={item.id} className="hover:bg-blue-50 transition">
                    <td className="border px-2 py-1">{item.id}</td>
                    <td className="border px-2 py-1">{item.itemName}</td>
                    <td
                      className={`border px-2 py-1 ${
                        item.result === "불량" ? "text-red-600 font-semibold" : ""
                      }`}
                    >
                      {item.result}
                    </td>
                    <td className="border px-2 py-1">{item.inspector}</td>
                    <td className="border px-2 py-1">{item.date}</td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      )}

      {/* 차트 */}
      {mode === "list" && (
        <div className="bg-white rounded shadow p-4 max-w-xl mx-auto">
          <Bar data={chartData} options={chartOptions} />
        </div>
      )}

      {/* 신규 등록 */}
      {mode === "form" && (
        <div className="bg-white rounded shadow p-4 mt-4 max-w-lg mx-auto">
          <h2 className="text-lg font-semibold mb-4">📝 신규 검사 등록</h2>
          <form onSubmit={handleSubmit} className="space-y-4">
            <div>
              <label className="block font-medium mb-1">제품명</label>
              <input
                className="w-full border px-3 py-2 rounded"
                value={itemName}
                onChange={(e) => setItemName(e.target.value)}
                required
              />
            </div>

            <div>
              <label className="block font-medium mb-1">검사 결과</label>
              <select
                className="w-full border px-3 py-2 rounded"
                value={result}
                onChange={(e) => setResult(e.target.value as "합격" | "불량")}
              >
                <option value="합격">합격</option>
                <option value="불량">불량</option>
              </select>
            </div>

            <div>
              <label className="block font-medium mb-1">검사자</label>
              <input
                className="w-full border px-3 py-2 rounded"
                value={inspector}
                onChange={(e) => setInspector(e.target.value)}
                required
              />
            </div>

            <div className="flex gap-2">
              <button
                type="submit"
                className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
              >
                저장
              </button>
              <button
                type="button"
                className="bg-gray-500 text-white px-4 py-2 rounded hover:bg-gray-600"
                onClick={() => setMode("list")}
              >
                취소
              </button>
            </div>
          </form>
        </div>
      )}
    </div>
  );
};

export default QualityPage;