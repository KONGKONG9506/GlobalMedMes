import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { getErrorMessage } from "../../lib/error";
import { statusColor, fmtPercent, fmtNumber } from "../../lib/kpi";
import KpiCard from "../kpi/KpiCard";
import type { KpiRes, KpiApiResponse } from "../../types/kpi";

export default function Dashboards() {
  // 오늘 날짜 (KPI 조회용) → 9시간 보정 필요 없다면 그대로 사용
  const today = new Date().toISOString().slice(0, 10);

  const [dataList, setDataList] = useState<KpiRes[]>([]);
  const [err, setErr] = useState<string>("");

  const load = async () => {
    try {
      setErr("");
      const res = await api.get<KpiApiResponse>("/kpi/datalist", {
        params: { kpiDate: today },
      });
      console.log("대시보드 KPI 데이터:", res.data);
      setDataList(res.data.content);
    } catch (err: unknown) {
      console.error("대시보드 API 오류:", err);
      setDataList([]);
      setErr(getErrorMessage(err, "조회 실패"));
    }
  };

  useEffect(() => {
    load();
  }, []);

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">대시보드</h1>

      {err && <div className="text-red-600 mb-4">{err}</div>}

      {dataList.length === 0 && <div>오늘 등록된 KPI 데이터가 없습니다.</div>}

      {dataList.length > 0 && (
        <div className="flex flex-col gap-6">
          {dataList.map((item) => (
            <div key={item.kpiId} className="grid grid-cols-1 md:grid-cols-3 gap-4">
              <KpiCard
                title={`OEE (${item.equipmentId})`}
                actual={fmtPercent(item.actualOee)}
                status={statusColor(undefined, item.actualOee)}
              />
              <KpiCard
                title={`수율 (${item.equipmentId})`}
                actual={fmtPercent(item.actualYield)}
                status={statusColor(undefined, item.actualYield)}
              />
              <KpiCard
                title={`생산성 (${item.equipmentId})`}
                actual={fmtNumber(item.actualProductivity, 2)}
                status={statusColor(undefined, item.actualProductivity)}
              />
            </div>
          ))}
        </div>
      )}
    </div>
  );
}