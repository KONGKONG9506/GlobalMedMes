import { useState, useEffect } from "react";
import { api } from "../../lib/api";
import { getErrorMessage } from "../../lib/error";
import { statusColor, statusDefectColor, fmtPercent, fmtNumber } from "../../lib/kpi";
import KpiCard from "../kpi/KpiCard";
import type { KpiRes, KpiApiResponse } from "../../types/kpi";
import KpisSelect,{KpiOptions} from "./Kpiequipment";
import KpiChart from "./Kpichart";

export default function KpiPage() {
  //fixedDate.setHours(fixedDate.getHours() + 9)
  const [date, setDate] = useState<string>(new Date().toISOString().slice(0, 10));
  const [eqp, setEqp] = useState<string>("");
  const [dataList, setDataList] = useState<KpiRes[]>([]);
  const [err, setErr] = useState<string>("");

   // **차트 전용 데이터**
  const [chartDataList, setChartDataList] = useState<KpiRes[]>([]);


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

  // **페이지 마운트 시 8월 데이터 자동 로드**
  useEffect(() => {
    const loadAugustData = async () => {
      try {
        const res = await api.get<KpiApiResponse>("/kpi/datalist", {
          params: { startDate: "2025-08-01", endDate: "2025-08-31" },
        });
        setChartDataList(res.data.content);
      } catch (err: unknown) {
        console.error(err);
      }
    };
    loadAugustData();
  }, []);

  return (
    <div className="p-6 max-w-4xl mx-auto">
      <h1 className="text-2xl font-bold mb-4">KPI</h1>

      <div className="flex gap-2 mb-5">
        <input
          className="border px-3 py-2 rounded w-40"
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
        />
        <KpisSelect
        equId={eqp}
        options={KpiOptions}
        onChange={(v)=>setEqp(v)}
        />
        <button
          className="bg-blue-600 text-white px-4 py-2 rounded hover:bg-blue-700"
          onClick={load}
        >
          조회
        </button>
      </div>

      {err && <div className="text-red-600 mb-3">{err}</div>}

      {dataList.length === 0 && <div></div>}

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
                status={statusColor(100, item.actualOee)}
              />
              <KpiCard
                title={`수율 (${item.equipmentId})`}
                actual={fmtPercent(item.actualYield)}
                status={statusColor(100, item.actualYield)}
              />
              <KpiCard
                title={`불량률 (${item.equipmentId})`}
                actual={fmtPercent(item.actualDefectRate)}
                status={statusDefectColor(0, item.actualDefectRate)}
              />
              <KpiCard
                title={`생산성 (${item.equipmentId})`}
                actual={fmtNumber(item.actualProductivity, 2)}
                status={statusColor(100, item.actualProductivity)}
              />
            </div>
          ))}
        </>
      )}
       {/* 조회와 무관하게 항상 표시되는 8월 차트 */}
      {chartDataList.length > 0 && (
        <div className="mt-8">
          <KpiChart dataList={chartDataList} />
        </div>
      )}
    </div>
  );
}