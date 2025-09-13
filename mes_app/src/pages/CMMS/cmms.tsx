import { useMemo, useState, useEffect } from "react";
import { api } from "../../lib/cmms";

type Role = "viewer" | "technician" | "maintainer" | "admin";

type CmmsPmPlan = {
  id: string;
  equipmentId: string;
  equipmentName: string;
  cadenceHours?: number;
  cadenceCalendar?: string;
  nextDueAt: string; 
};

type WoStatus = "OPEN" | "ASSIGNED" | "IN_PROGRESS" | "COMPLETED";

type CmmsWo = {
  id: string;
  equipmentId: string;
  equipmentName: string;
  createdAt: string;
  assignedTo?: string;
  status: WoStatus;
  durationMinutes?: number;
  cost?: number;
  logs: Array<{ ts: string; text: string }>;
};

type CmmsFault = {
  id: string;
  equipmentId: string;
  equipmentName: string;
  category: string;
  symptom: string;
  actionTaken?: string;
  reportedAt: string;
};

const currentUser = {
  username: "park",
  role: "maintainer" as Role,
};

function computeMetrics(wos: CmmsWo[]) {
  const completed = wos.filter((w) => w.status === "COMPLETED");
  const failureCount = completed.length || 0;

  const observationDays = 90;
  const totalOperatingHours = observationDays * 24;

  const mtbfHours =
    failureCount > 0 ? totalOperatingHours / failureCount : totalOperatingHours;
  const mttrHours =
    failureCount > 0
      ? completed.reduce((s, w) => s + (w.durationMinutes || 0), 0) /
        (failureCount * 60)
      : 0;

  return {
    mtbfHours: Number(mtbfHours.toFixed(2)),
    mttrHours: Number(mttrHours.toFixed(2)),
    failureCount,
  };
}

// 메인 컴포넌트
export default function CmmsPage() {
  const [pmPlans, setPmPlans] = useState<CmmsPmPlan[]>([]);
  const [wos, setWos] = useState<CmmsWo[]>([]);
  const [faults, setFaults] = useState<CmmsFault[]>([]);
  const [loading, setLoading] = useState(true);
  const [selectedTab, setSelectedTab] = useState<
    "PM" | "WO" | "FAULT" | "METRICS"
  >("PM");

  // API 호출
  useEffect(() => {
    async function fetchData() {
      try {
        setLoading(true);
        const [pmRes, woRes, faultRes] = await Promise.all([
          api.get<CmmsPmPlan[]>("/api/cmms/pm-plans/due"),
          api.get<CmmsWo[]>("/cmms/work-orders"),
          api.get<CmmsFault[]>("/api/cmms/fault-logs"),
        ]);

        setPmPlans(pmRes.data);
        setWos(woRes.data);
        setFaults(faultRes.data);
      } catch (err) {
        console.error("API fetch error", err);
      } finally {
        setLoading(false);
      }
    }
    fetchData();
  }, []);

  const metrics = useMemo(() => computeMetrics(wos), [wos]);

  const upcomingPm = useMemo(() => {
    return [...pmPlans].sort(
      (a, b) => new Date(a.nextDueAt).getTime() - new Date(b.nextDueAt).getTime()
    );
  }, [pmPlans]);

  // WO 관련 함수
  const createWo = async (equipmentId: string, title: string) => {
    try {
      const res = await api.post<CmmsWo>("/work-orders", {
        equipmentId,
        title,
        priorityCodeId: 1,
        requestId: "CMMS-WO-TEST-001",
      });
      setWos((s) => [res.data, ...s]);
    } catch (err) {
      console.error("WO 생성 실패", err);
    }
  };

  const assignWo = async (id: string, assignee: string) => {
    try {
      const res = await api.post<CmmsWo>(`/work-orders/${id}/assign`, {
        assigneeUserId: assignee,
      });
      setWos((s) => s.map((w) => (w.id === id ? res.data : w)));
    } catch (err) {
      console.error("WO 할당 실패", err);
    }
  };

  const startWork = async (id: string) => {
    try {
      const res = await api.post<CmmsWo>(`/work-orders/${id}/start`, {
        startedAt: new Date().toISOString(),
      });
      setWos((s) => s.map((w) => (w.id === id ? res.data : w)));
    } catch (err) {
      console.error("작업 시작 실패", err);
    }
  };

  const completeWo = async (id: string, durationMinutes: number, cost = 0) => {
    try {
      const res = await api.post<CmmsWo>(`/work-orders/${id}/complete`, {
        finishedAt: new Date().toISOString(),
        actualMinutes: durationMinutes,
        partsCost: cost,
      });
      setWos((s) => s.map((w) => (w.id === id ? res.data : w)));
    } catch (err) {
      console.error("WO 완료 실패", err);
    }
  };

  const reportFault = async (f: Omit<CmmsFault, "id" | "reportedAt">) => {
    try {
      const res = await api.post<CmmsFault>("/fault-logs", {
        equipmentId: f.equipmentId,
        lossCategoryCodeId: 13, // 예시
        symptom: f.symptom,
        action: f.actionTaken,
        occurredAt: new Date().toISOString(),
        resolvedAt: null,
      });
      setFaults((s) => [res.data, ...s]);
    } catch (err) {
      console.error("Fault 등록 실패", err);
    }
  };

  return (
    <div className="min-h-screen p-6 bg-gray-50">
      <div className="max-w-7xl mx-auto">
        <header className="mb-6">
          <h1 className="text-2xl font-bold">CMMS</h1>
          <p className="text-sm text-gray-600">
            PM 계획 / 보전 WO / 고장 접수 / 지표(MTBF, MTTR)
          </p>
        </header>

        <div className="grid grid-cols-12 gap-6">
          {/* 사이드 탭 */}
          <aside className="col-span-2 bg-white p-3 rounded shadow-sm">
            <nav className="flex flex-col space-y-2">
              {["PM", "WO", "FAULT", "METRICS"].map((tab) => (
                <button
                  key={tab}
                  className={`text-left px-3 py-2 rounded ${
                    selectedTab === tab ? "bg-sky-100" : "hover:bg-gray-100"
                  }`}
                  onClick={() => setSelectedTab(tab as any)}
                >
                  {tab === "PM"
                    ? "PM 계획"
                    : tab === "WO"
                    ? "보전 WO"
                    : tab === "FAULT"
                    ? "고장 접수"
                    : "지표"}
                </button>
              ))}
            </nav>

            <div className="mt-4 text-xs text-gray-500">
              <div>현재 사용자: {currentUser.username}</div>
              <div>권한: {currentUser.role}</div>
            </div>
          </aside>

          {/* 메인 콘텐츠 */}
          <main className="col-span-10">
            <div className="bg-white p-4 rounded shadow-sm">
              {loading && (
                <div className="text-center text-gray-500">로딩 중...</div>
              )}

              {!loading && selectedTab === "PM" && (
                <section>
                  <h2 className="text-xl font-semibold mb-3">PM 계획</h2>
                  <div className="grid grid-cols-3 gap-4">
                    <div className="col-span-2">
                      <h3 className="font-medium mb-2">장비별 PM 목록</h3>
                      <div className="space-y-2">
                        {pmPlans.map((p) => (
                          <div
                            key={p.id}
                            className="border rounded p-3 flex justify-between items-center"
                          >
                            <div>
                                
                              <div className="font-semibold">
                                {p.equipmentName} · {p.equipmentId}
                              </div>
                              <div className="text-xs text-gray-500">
                                만료:{" "}
                                {new Date(p.nextDueAt).toLocaleDateString()}
                              </div>
                            </div>
                            <div className="flex gap-2">
                              <button
                                className="px-3 py-1 border rounded text-sm"
                                onClick={() =>
                                  createWo(p.equipmentId, "예시 WO 제목")
                                }
                              >
                                WO 생성
                              </button>
                            </div>
                          </div>
                        ))}
                      </div>
                    </div>

                    <div>
                      <h3 className="font-medium mb-2">도래 PM (우선순위)</h3>
                      <div className="space-y-2">
                        {upcomingPm.slice(0, 5).map((p) => (
                          <div key={p.id} className="border rounded p-2">
                            <div className="font-semibold text-sm">
                              {p.equipmentName}
                            </div>
                            <div className="text-xs text-gray-500">
                              만료:{" "}
                              {new Date(p.nextDueAt).toLocaleDateString()}
                            </div>
                          </div>
                        ))}
                        {upcomingPm.length === 0 && (
                          <div className="text-xs text-gray-500">
                            도래 PM 없음
                          </div>
                        )}
                      </div>
                    </div>
                  </div>
                </section>
              )}

              {!loading && selectedTab === "WO" && (
                <section>
                  <h2 className="text-xl font-semibold mb-3">보전 WO</h2>
                  {/* WO 목록 표시 */}
                  <div className="space-y-2 mt-2">
                    {wos.map((w) => (
                      <div key={w.id} className="border rounded p-3">
                        <div className="flex justify-between items-start">
                          <div>
                            <div className="font-semibold">
                              {w.equipmentName} · {w.equipmentId}
                            </div>
                            <div className="text-sm text-gray-600">
                              상태: {w.status}{" "}
                              {w.assignedTo ? `· 담당: ${w.assignedTo}` : ""}
                            </div>
                            <div className="text-xs text-gray-500">
                              생성: {new Date(w.createdAt).toLocaleString()}
                            </div>
                          </div>
                          <div className="flex gap-2">
                            {w.status === "OPEN" && (
                              <button
                                className="px-2 py-1 border rounded text-sm"
                                onClick={() => assignWo(w.id, "00000000-0000-0000-0000-0000000000OP")}
                              >
                                할당
                              </button>
                            )}
                            {w.status === "ASSIGNED" && (
                              <button
                                className="px-2 py-1 border rounded text-sm"
                                onClick={() => startWork(w.id)}
                              >
                                작업 시작
                              </button>
                            )}
                            {w.status === "IN_PROGRESS" && (
                              <button
                                className="px-2 py-1 border rounded text-sm"
                                onClick={() => completeWo(w.id, 60, 50)}
                              >
                                완료(예시)
                              </button>
                            )}
                            {w.status === "COMPLETED" && (
                              <span className="text-xs text-gray-500">
                                완료 · 소요: {w.durationMinutes ?? "-"} 분
                              </span>
                            )}
                          </div>
                        </div>
                      </div>
                    ))}
                  </div>
                </section>
              )}

              {!loading && selectedTab === "FAULT" && (
                <section>
                  <h2 className="text-xl font-semibold mb-3">고장 접수</h2>
                  <FaultForm onSubmit={(vals) => reportFault(vals)} />
                  <div className="space-y-2 mt-2">
                    {faults.map((f) => (
                      <div key={f.id} className="border rounded p-3">
                        <div className="font-semibold">
                          {f.equipmentName} · {f.equipmentId}
                        </div>
                        <div className="text-sm text-gray-600">
                          카테고리: {f.category} · 신고시각:{" "}
                          {new Date(f.reportedAt).toLocaleString()}
                        </div>
                        <div className="text-xs mt-1">증상: {f.symptom}</div>
                        <div className="text-xs text-gray-500">
                          조치: {f.actionTaken ?? "-"}
                        </div>
                      </div>
                    ))}
                  </div>
                </section>
              )}

              {!loading && selectedTab === "METRICS" && (
                <section>
                  <h2 className="text-xl font-semibold mb-3">
                    지표 (MTBF / MTTR)
                  </h2>
                  <div className="grid grid-cols-3 gap-4">
                    <div className="col-span-1 border rounded p-4">
                      <div className="text-sm text-gray-500">기간 (관찰)</div>
                      <div className="text-2xl font-bold mt-2">90일</div>
                    </div>
                    <div className="col-span-2 border rounded p-4 flex items-center justify-between">
                      <div>
                        <div className="text-xs text-gray-500">MTBF (시간)</div>
                        <div className="text-3xl font-bold">{metrics.mtbfHours}</div>
                      </div>
                      <div>
                        <div className="text-xs text-gray-500">MTTR (시간)</div>
                        <div className="text-3xl font-bold">{metrics.mttrHours}</div>
                        <div className="text-sm text-gray-500 mt-1">
                          고장 수: {metrics.failureCount}
                        </div>
                      </div>
                    </div>
                  </div>
                </section>
              )}
            </div>
          </main>
        </div>
      </div>
    </div>
  );
}

// FaultForm 컴포넌트
function FaultForm({
  onSubmit,
}: {
  onSubmit: (v: Omit<CmmsFault, "id" | "reportedAt">) => void;
}) {
  const [equipmentId, setEquipmentId] = useState("E-0001");
  const [equipmentName, setEquipmentName] = useState("압축기 A1");
  const [category, setCategory] = useState("Mechanical");
  const [symptom, setSymptom] = useState("");
  const [actionTaken, setActionTaken] = useState("");

  const lossCategories = ["Mechanical","Electrical","Process","Instrumentation","Operator"].slice(0,5);

  return (
    <form
      onSubmit={(e) => {
        e.preventDefault();
        if (!symptom) return alert("증상을 입력해주세요");
        onSubmit({ equipmentId, equipmentName, category, symptom, actionTaken });
        setSymptom("");
        setActionTaken("");
      }}
      className="space-y-2"
    >
      <label className="block text-xs">장비 ID</label>
      <input className="w-full border rounded px-2 py-1 text-sm" value={equipmentId} onChange={(e) => setEquipmentId(e.target.value)} />
      <label className="block text-xs">장비명</label>
      <input className="w-full border rounded px-2 py-1 text-sm" value={equipmentName} onChange={(e) => setEquipmentName(e.target.value)} />
      <label className="block text-xs">카테고리</label>
      <select className="w-full border rounded px-2 py-1 text-sm" value={category} onChange={(e) => setCategory(e.target.value)}>
        {lossCategories.map((c) => (
          <option key={c} value={c}>{c}</option>
        ))}
      </select>
      <label className="block text-xs">증상</label>
      <input className="w-full border rounded px-2 py-1 text-sm" value={symptom} onChange={(e) => setSymptom(e.target.value)} />
      <label className="block text-xs">조치</label>
      <input className="w-full border rounded px-2 py-1 text-sm" value={actionTaken} onChange={(e) => setActionTaken(e.target.value)} />
      <button type="submit" className="px-3 py-1 bg-sky-500 text-white rounded text-sm mt-1">등록</button>
    </form>
  );
}
