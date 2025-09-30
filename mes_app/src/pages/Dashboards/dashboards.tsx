import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { getErrorMessage } from "../../lib/error";
import { statusColor, fmtPercent, fmtNumber } from "../../lib/kpi";
import KpiCard from "../kpi/KpiCard";
import type { KpiRes, KpiApiResponse } from "../../types/kpi";
import type { EquipStatusItem } from "../../types/equip";
import { fetchEquipStatus } from "../../lib/equip";

const equipStatusColors: Record<string, string> = {
  RUN: "bg-green-100 text-green-800",
  IDLE: "bg-yellow-100 text-yellow-800",
  DOWN: "bg-red-100 text-red-800",
};

export default function Dashboards() {
  // 오늘 날짜 (KPI 조회용) → 9시간 보정 필요 없다면 그대로 사용
  const today = new Date().toISOString().slice(0, 10);

  const [dataList, setDataList] = useState<KpiRes[]>([]);
  const [err, setErr] = useState<string>("");

  const [equipstatus, setEquipstatus] = useState<EquipStatusItem[]>([]);
  const [equipErr, setEquipErr] = useState<string>("");

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

 const loadEquip = async () => {
    try {
      setEquipErr("");
      
      const fromIso = new Date(today).toISOString().replace(/\.\d{3}Z$/, "Z");
      const toIso = new Date(`${today}T23:59:59Z`).toISOString().replace(/\.\d{3}Z$/, "Z");
console.log(fromIso+"////////////////"+toIso);
      const res = await fetchEquipStatus({
        equipmentId: "INS-001", // 전체 설비 조회
        from: fromIso,
        to: toIso,
        page: 0,
        size: 20,
        sort: "startTime,desc",
      });
      // 현재 설비상태 넘겨받을때 장비명이 백엔드에는 하나씩만 받아서 나오게 되어있는구조 
      // 즉 프론트에서는 당일날의 여러 장비를 받아서 한번에 띄워야 하는데 서로 구조가 안맞는상태->이러면 프론트에서 일단은 하드코딩 구조로 가야함
      console.log("설비 리스트:", res); // 구조 확인용
      console.log("설비 리스트 content:", res.content);
      setEquipstatus(res.content ?? []);
    } catch (err: unknown) {
      console.error("대시보드 설비상태 API 오류:", err);
      setEquipstatus([]);
      setEquipErr(getErrorMessage(err, "설비 상태 조회 실패"));
    }
  };

  useEffect(() => {
    load();
    loadEquip();
  }, []);

  return (
    <div className="p-6 max-w-6xl mx-auto">
      <h1 className="text-2xl font-bold mb-6">대시보드</h1>
      <h2 className="text-xl font-semibold mb-4">오늘의 KPI</h2>

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
      <h2 className="text-xl font-semibold mb-4">설비 상태</h2>
      {equipErr && <div className="text-red-600 mb-4">{equipErr}</div>}
      {equipstatus.length === 0 && <div>오늘 등록된 설비 상태가 없습니다.</div>}
      {equipstatus.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-4">
          {equipstatus.map((it) => {
            const statusClass =
              equipStatusColors[it.statusCode as keyof typeof equipStatusColors] ||
              "bg-gray-100 text-gray-800";
            return (
              <div
                key={it.logId}
                className="p-4 rounded-lg shadow border flex flex-col items-center"
              >
                <div className="text-lg font-bold mb-2">{it.equipmentId}</div>
                <span className={`px-3 py-1 rounded-full font-semibold text-sm ${statusClass}`}>
                  {it.statusCode}
                </span>
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}