import { useState } from "react";
import { api } from "../../lib/api";
import { getErrorMessage } from "../../lib/error";
import { statusColor, fmtPercent, fmtNumber } from "../../lib/kpi";
import KpiCard from "../kpi/KpiCard";
import type { KpiRes, KpiApiResponse } from "../../types/kpi";

export default function KpiPage() {
  const [date, setDate] = useState<string>(new Date().toISOString().slice(0, 10));
  const [eqp, setEqp] = useState<string>("E-0001");
  const [dataList, setDataList] = useState<KpiRes[]>([]);
  const [err, setErr] = useState<string>("");

  const load = async () => {
    try {
      setErr("");
      const res = await api.get<KpiApiResponse>("/kpi/datalist", {
        params: { kpiDate: date, equipmentId: eqp },
      });
      console.log("KPI 데이터:", res.data);
      setDataList(res.data.content);
    } catch (err: unknown) {
      console.error("API 오류:", err);
      setDataList([]);
      setErr(getErrorMessage(err, "조회 실패"));
    }
  };

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">KPI 대시보드</h1>

      <div className="flex gap-2 mb-5">
        <input
          className="border px-3 py-2 rounded w-40"
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
        />
        <input
          className="border px-3 py-2 rounded w-40"
          placeholder="설비 ID"
          value={eqp}
          onChange={(e) => setEqp(e.target.value)}
        />
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          onClick={load}
        >
          조회
        </button>
      </div>

      {err && <div className="text-red-600 mb-3">{err}</div>}

      {dataList.length === 0 && <div>데이터가 없습니다.</div>}

      {dataList.length > 0 && (
        <>
          {dataList.map((item) => (
            <div
              key={item.kpiId}
              className="mb-6 p-4 border rounded shadow-sm grid grid-cols-1 md:grid-cols-2 gap-4"
            >
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
                title={`불량률 (${item.equipmentId})`}
                actual={fmtPercent(item.actualDefectRate)}
                status={statusColor(undefined, item.actualDefectRate)}
              />
              <KpiCard
                title={`생산성 (${item.equipmentId})`}
                actual={fmtNumber(item.actualProductivity, 2)}
                status={statusColor(undefined, item.actualProductivity)}
              />
            </div>
          ))}
        </>
      )}
    </div>
  );
}