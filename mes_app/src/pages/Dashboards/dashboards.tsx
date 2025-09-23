import { useState, useEffect } from "react";
// import DashboardContent from "../Dashboards/dashboardcontent";
import { api } from "../../lib/api";

type KpiSummary = {
  yield: number;
  productivity: number;
  oee: number;
};

type EquipmentStatus = {
  id: string;
  status: "운영 중" | "정지" | "경고";
  production: number;
  target: number;
  oee: number;
};

export default function Dashboard() {
  const [kpi, setKpi] = useState<KpiSummary | null>(null);
  const [equipmentList, setEquipmentList] = useState<EquipmentStatus[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedDate, setSelectedDate] = useState<string>(
    new Date().toISOString().slice(0, 10) // 기본 오늘 날짜
  );

  // 날짜 기준으로 데이터 로드
  useEffect(() => {
    const loadData = async () => {
      setLoading(true);
      try {
        // KPI 데이터 호출
        const kpiRes = await api.get("/kpi/actuals", {
          params: {
            kpiDate: selectedDate,
            equipmentId: "E-0001",
          },
        });

        setKpi({
          yield: Number(kpiRes.data.actualYield),
          productivity: Number(kpiRes.data.actualProductivity),
          oee: Number(kpiRes.data.actualOee),
        });

        // 장비 리스트 호출
        const eqRes = await api.get("/equipment/list"); // 실제 엔드포인트로 교체
        setEquipmentList(eqRes.data);
      } catch (err) {
        console.error("Dashboard API Error:", err);
        setKpi(null);
        setEquipmentList([]);
      } finally {
        setLoading(false);
      }
    };

    loadData();
  }, [selectedDate]);

  return (
    <div className="flex min-h-screen bg-gray-50 relative">
      <div className="flex-1 flex flex-col overflow-hidden">
        {/* 날짜 선택 */}
        <div className="p-6 bg-white shadow rounded-xl mb-4 flex items-center gap-4">
          <label htmlFor="kpi-date" className="font-semibold">
            KPI 날짜:
          </label>
          <input
            id="kpi-date"
            type="date"
            value={selectedDate}
            onChange={(e) => setSelectedDate(e.target.value)}
            className="border p-1 rounded"
          />
        </div>

        {/* <main className="flex-1 p-6 bg-gray-50 overflow-auto">
          <DashboardContent
            kpi={kpi}
            equipmentList={equipmentList}
            loading={loading}
          />
        </main> */}
      </div>
    </div>
  );
}