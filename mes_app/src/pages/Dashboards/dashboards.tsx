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

type PerfItem = {
  performanceId: number;
  workOrderId: string;
  workOrderNumber: string;
  itemId: string;
  processId: string;
  equipmentId: string;
  producedQty: number;
  defectQty: number;
  startTime: string;
  endTime: string;
};

type WorkOrderCardItem = {
  workOrderId: string;
  itemName: string;
  processName: string;
  equipmentName: string;
  orderQty: number;
  statusCode: string;
};

export default function Dashboards() {
  // 오늘 날짜 (KPI 조회용) → 9시간 보정 필요 없다면 그대로 사용
  const today = new Date().toISOString().slice(0, 10);

  const [dataList, setDataList] = useState<KpiRes[]>([]);
  const [err, setErr] = useState<string>("");

  const [equipstatus, setEquipstatus] = useState<EquipStatusItem[]>([]);
  const [equipErr, setEquipErr] = useState<string>("");

  const [perfList, setPerfList] = useState<PerfItem[]>([]);
  const [perfErr, setPerfErr] = useState<string>("");

  const [workOrders, setWorkOrders] = useState<WorkOrderCardItem[]>([]);
  const [woErr, setWoErr] = useState<string>("");
 
  // **KPI 데이터 로드**
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
 // **설비상태 데이터 로드**
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

 // **실적 데이터 로드** 
 const loadPerformance = async () => {
    try {
      setPerfErr("");
      const fromIso = new Date(today).toISOString().replace(/\.\d{3}Z$/, "Z");
      const toIso = new Date(`${today}T23:59:59Z`).toISOString().replace(/\.\d{3}Z$/, "Z");

      const res = await api.get<{ content: PerfItem[] }>("/performances", {
        params: {
          from: fromIso,
          to: toIso,
          page: 0,
          size: 50,
          sort: "startTime,desc",
        },
      });
      setPerfList(res.data.content ?? []);
    } catch (err: unknown) {
      console.error("대시보드 실적 API 오류:", err);
      setPerfList([]);
      setPerfErr(getErrorMessage(err, "실적 조회 실패"));
    }
  };

  // 작업지시 데이터 로드
  const loadWorkOrders = async () => {
    try {
      setWoErr("");
      const fromIso = new Date(today).toISOString().replace(/\.\d{3}Z$/, "Z");
      const toIso = new Date(`${today}T23:59:59Z`).toISOString().replace(/\.\d{3}Z$/, "Z");

      const res = await api.get<{ content: WorkOrderCardItem[] }>("/work-orders", {
        params: { from: fromIso, to: toIso, page: 0, size: 50, sort: "createdAt,desc" },
      });
      setWorkOrders(res.data.content ?? []);
    } catch (err: unknown) {
      setWorkOrders([]);
      setWoErr(getErrorMessage(err, "작업지시 조회 실패"));
    }
  };

  useEffect(() => {
    load();
    loadEquip();
    loadPerformance();
    loadWorkOrders();
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

      <h2 className="text-xl font-semibold mb-4 mt-6">오늘의 설비 상태</h2>
      {equipErr && <div className="text-red-600 mb-4">{equipErr}</div>}
      {equipstatus.length === 0 && <div>오늘 등록된 설비 상태가 없습니다.</div>}
      {equipstatus.length > 0 && (
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
      {equipstatus.map((it) => {
       const statusClass =
       equipStatusColors[it.statusCode as keyof typeof equipStatusColors] ||
      "bg-gray-100 text-gray-800";

       const borderColors: Record<string, string> = {
       RUN: "border-green-400",
       IDLE: "border-yellow-400",
       DOWN: "border-red-400",
      };
      const borderClass =
      borderColors[it.statusCode as keyof typeof borderColors] || "border-gray-300";

      return (
      <div
        key={it.logId}
        className={`p-6 rounded-xl shadow-md bg-white flex flex-col items-center transition-all duration-300 border-2 ${borderClass}`}>
        {/* 설비명 */}
        <div className="text-lg font-bold text-gray-800 mb-2">
          {it.equipmentId}
        </div>

        {/* 상태 뱃지 */}
        <span
          className={`px-4 py-1.5 rounded-full font-semibold text-sm ${statusClass}`}>
          {it.statusCode}
        </span>
       </div>
        );
       })}
       </div>
      )}

      <h2 className="text-xl font-semibold mb-4 mt-6">오늘의 실적</h2>
      {perfErr && <div className="text-red-600 mb-4">{perfErr}</div>}
      {perfList.length === 0 && <div>오늘 등록된 실적 데이터가 없습니다.</div>}
      {perfList.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {perfList.map((p) => (
            <div key={p.performanceId} className="p-6 rounded-xl shadow-md bg-white flex flex-col items-center border-2 border-gray-300">
              <div className="text-lg font-bold text-gray-800 mb-2">{p.equipmentId}</div>
              <div className="text-sm text-gray-600 mb-1">생산량: <b className="text-blue-600">{p.producedQty}</b></div>
              <div className="text-sm text-gray-600">불량: <b className="text-red-600">{p.defectQty}</b></div>
            </div>
          ))}
        </div>
      )}

      <h2 className="text-xl font-semibold mb-4 mt-6">오늘의 작업지시</h2>
      {woErr && <div className="text-red-600 mb-4">{woErr}</div>}
      {workOrders.length === 0 && <div>오늘 등록된 작업지시가 없습니다.</div>}
      {workOrders.length > 0 && (
        <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
          {workOrders.map((wo) => (
            <div key={wo.workOrderId} className="p-6 rounded-xl shadow-md bg-white flex flex-col gap-2 border-2 border-gray-300">
              <div className="text-sm text-gray-600"><b>품목:</b> {wo.itemName}</div>
              <div className="text-sm text-gray-600"><b>공정:</b> {wo.processName}</div>
              <div className="text-sm text-gray-600"><b>설비:</b> {wo.equipmentName}</div>
              <div className="text-sm text-gray-600"><b>지시:</b> {wo.orderQty}</div>
              <div className="text-sm text-gray-600">
                <b>상태:</b> <span className={`px-2 py-1 rounded text-sm font-medium ${
                  wo.statusCode === "P" ? "bg-yellow-100 text-yellow-800" :
                  wo.statusCode === "R" ? "bg-blue-100 text-blue-800" :
                  wo.statusCode === "C" ? "bg-green-100 text-green-800" :
                  "bg-gray-100 text-gray-600"
                }`}>{wo.statusCode}</span>
              </div>
            </div>
          ))}
        </div>
      )}
    </div>
  );
}