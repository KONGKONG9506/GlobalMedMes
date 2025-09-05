import { useEffect, useState } from "react";

type UserInfo = {
  username: string;
  role: string;
};

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

type Alert = {
  id: number;
  message: string;
  severity: "info" | "warning" | "error";
};

export default function Dashboard() {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [kpi, setKpi] = useState<KpiSummary | null>(null);

  // 가상 데이터 - 장비 상태
  const equipmentList: EquipmentStatus[] = [
    { id: "E-0001", status: "운영 중", production: 1200, target: 1500, oee: 85 },
    { id: "E-0002", status: "정지", production: 0, target: 1000, oee: 0 },
    { id: "E-0003", status: "경고", production: 700, target: 900, oee: 65 },
    { id: "E-0004", status: "운영 중", production: 1300, target: 1300, oee: 95 },
  ];

  // 가상 데이터 - 알림
  const alerts: Alert[] = [
    { id: 1, message: "E-0003 장비 경고 발생", severity: "warning" },
    { id: 2, message: "불량률 급증 감지", severity: "error" },
    { id: 3, message: "정기 점검 예정 알림", severity: "info" },
  ];

  useEffect(() => {
    // 임시 사용자, KPI 설정
    setUserInfo({ username: "beomsu", role: "user" });
    setKpi({ yield: 90, productivity: 75, oee: 80 });
  }, []);

  return (
    <div className="space-y-6 p-6 bg-gray-50 min-h-screen">
      {/* 사용자 정보 + 알림 */}
      <header className="flex items-center justify-between p-4 bg-gray-200 rounded shadow">
        <div>
          <div className="text-lg font-semibold">
            👤 {userInfo?.username ?? "사용자"} ({userInfo?.role ?? "역할"})
          </div>
        </div>
        <div className="flex space-x-3">
          {alerts.map((alert) => (
            <div
              key={alert.id}
              className={`px-3 py-1 rounded text-sm font-semibold ${
                alert.severity === "error"
                  ? "bg-yellow-200 text-yellow-800"
                  : alert.severity === "warning"
                  ? "bg-red-200 text-red-800"
                  : "bg-blue-200 text-blue-800"
              }`}
            >
              {alert.message}
            </div>
          ))}
        </div>
      </header>

      {/* KPI 요약 카드 */}
      <section>
        <h2 className="text-xl font-semibold mb-3">오늘의 KPI 요약</h2>
        {kpi ? (
          <div className="grid grid-cols-3 gap-4">
            <div className="bg-white shadow p-4 rounded border">
              <div className="text-gray-500 text-sm mb-1">수율</div>
              <div className="text-2xl font-bold text-green-600">
                {kpi.yield.toFixed(2)}%
              </div>
            </div>
            <div className="bg-white shadow p-4 rounded border">
              <div className="text-gray-500 text-sm mb-1">생산성</div>
              <div className="text-2xl font-bold text-yellow-600">
                {kpi.productivity.toFixed(2)}%
              </div>
            </div>
            <div className="bg-white shadow p-4 rounded border">
              <div className="text-gray-500 text-sm mb-1">OEE</div>
              <div className="text-2xl font-bold text-red-600">
                {kpi.oee.toFixed(2)}%
              </div>
            </div>
          </div>
        ) : (
          <div>로딩 중...</div>
        )}
      </section>

      {/* 실시간 장비 상태 */}
      <section>
        <h2 className="text-xl font-semibold mb-3">장비 상태</h2>
        <div className="grid grid-cols-4 gap-4">
          {equipmentList.map((eq) => (
            <div
              key={eq.id}
              className="bg-white p-4 rounded shadow border flex flex-col"
            >
              <div className="font-bold mb-2">{eq.id}</div>
            <div
            className={`font-semibold mb-1 ${
            eq.status === "운영 중"
            ? "text-green-600"
            : eq.status === "정지"
            ? "text-gray-500"
            : eq.status === "경고"
            ? "text-red-600"
            : "text-black" 
            }`}
            >
            {eq.status}
            </div>
              <div>생산량: {eq.production} / {eq.target}</div>
              <div>OEE: {eq.oee}%</div>
            </div>
          ))}
        </div>
      </section>

      {/* 생산 계획 (간단한 진행률) */}
      <section>
        <h2 className="text-xl font-semibold mb-3">생산 계획 현황</h2>
        <div className="bg-white p-4 rounded shadow border max-w-md">
          <div className="mb-2 font-semibold">오늘 목표 생산량: 5000개</div>
          <div className="w-full bg-gray-200 rounded h-6">
            <div
              className="bg-blue-600 h-6 rounded"
              style={{ width: "65%" }}
            />
          </div>
          <div className="mt-1 text-sm text-gray-600">65% 달성</div>
        </div>
      </section>

      {/* 품질 현황 */}
      <section>
        <h2 className="text-xl font-semibold mb-3">품질 현황</h2>
        <div className="bg-white p-4 rounded shadow border max-w-md">
          <div className="mb-2">최근 불량률: <span className="font-semibold text-red-600">2.5%</span></div>
          <div>불량 원인: 부품 불량</div>
        </div>
      </section>
    </div>
  );
}
