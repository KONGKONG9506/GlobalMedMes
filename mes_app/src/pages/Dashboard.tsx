import { useEffect, useState } from "react";
import { ResponsiveContainer, PieChart, Pie, Cell, Tooltip, Legend } from "recharts";

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

type MenuItem = {
  id: string;
  label: string;
  subMenu?: MenuItem[];
};

export default function Dashboard() {
  const [userInfo, setUserInfo] = useState<UserInfo | null>(null);
  const [kpi, setKpi] = useState<KpiSummary | null>(null);
  const [isSidebarOpen, setIsSidebarOpen] = useState(true);
  const [isRightSidebarOpen, setIsRightSidebarOpen] = useState(false);
  const [openTabs, setOpenTabs] = useState<MenuItem[]>([]);
  const [activeTabId, setActiveTabId] = useState<string | null>(null);
 
  const [selectedYear, setSelectedYear] = useState<string>("2024");
  const [selectedMonth, setSelectedMonth] = useState<string>("01");

  const toggleRightSidebar = () => setIsRightSidebarOpen(!isRightSidebarOpen);
  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);


  // 가상 데이터
  const equipmentList: EquipmentStatus[] = [
    { id: "E-0001", status: "운영 중", production: 1200, target: 1500, oee: 85 },
    { id: "E-0002", status: "정지", production: 0, target: 1000, oee: 0 },
    { id: "E-0003", status: "경고", production: 700, target: 900, oee: 65 },
    { id: "E-0004", status: "운영 중", production: 1300, target: 1300, oee: 95 },
  ];

  const menuList: MenuItem[] = [
    { id: "dashboard", label: "대시보드" },
    {
      id: "category1",
      label: "메뉴",
      subMenu: [
        { id: "subcategory1", label: "하위1" },
        { id: "subcategory2", label: "하위2" },
        { id: "subcategory3", label: "하위3" },
        { id: "subcategory4", label: "하위4" },
      ],
    },
    { id: "logout", label: "로그아웃" },
  ];

  useEffect(() => {
    setUserInfo({ username: "beomsu", role: "user" });
    setKpi({ yield: 90, productivity: 75, oee: 80 });
  }, []);

  useEffect(()=>{
    console.log("데이터 요청:",selectedYear, selectedMonth);
    setKpi({
     yield:80 +Math.random()*20,
     productivity:60+Math.random()*20,
     oee:70+Math.random()*20,
    });
  },[selectedYear,selectedMonth]);

  const handleMenuClick = (menu: MenuItem) => {
    if (menu.id === "logout") {
      setUserInfo(null);
      setOpenTabs([]);
      setActiveTabId(null);
      return;
    }

  const exists = openTabs.find((tab) => tab.id === menu.id);
    if (!exists) {
    setOpenTabs([...openTabs, menu]);
    }
    setActiveTabId(menu.id); 
  };

  const handleTabClick = (tabId: string) => {
    setActiveTabId(tabId);
  };

  const handleTabClose = (tabId: string) => {
    const filtered = openTabs.filter((tab) => tab.id !== tabId);
    setOpenTabs(filtered);
    if (activeTabId === tabId) {
      const newActive = filtered.length > 0 ? filtered[0].id : null;
      setActiveTabId(newActive);
    }
  };

  const downtimeData = [
  { name: "생산", value: 120, color: "#f59e0b" },
  { name: "불량", value: 20, color: "#ef4444" }
  ];

  function MonthPicker({
    year,
    month,
    onYearChange,
    onMonthChange,
  }: {
    year: string;
     month: string;
    onYearChange: (value: string) => void;
    onMonthChange: (value: string) => void;
  }) {
    const years = ["2023", "2024", "2025"];
    const months = [
      { value: "01", label: "Jan" },
      { value: "02", label: "Feb" },
      { value: "03", label: "Mar" },
      { value: "04", label: "Apr" },
      { value: "05", label: "May" },
      { value: "06", label: "Jun" },
      { value: "07", label: "Jul" },
      { value: "08", label: "Aug" },
      { value: "09", label: "Sep" },
      { value: "10", label: "Oct" },
      { value: "11", label: "Nov" },
      { value: "12", label: "Dec" },
    ];
      return (
      <div className="flex space-x-3 mb-4">
        <select
          value={year}
          onChange={(e) => onYearChange(e.target.value)}
          className="border rounded px-3 py-1"
        >
          {years.map((y) => (
            <option key={y} value={y}>
              {y}년
            </option>
          ))}
        </select>
        <select
          value={month}
          onChange={(e) => onMonthChange(e.target.value)}
          className="border rounded px-3 py-1"
        >
          {months.map((m) => (
            <option key={m.value} value={m.value}>
              {m.label}
            </option>
          ))}
        </select>
      </div>
    );
  }

  function DashboardContent({
    kpi,
    equipmentList,
  }: {
    kpi: KpiSummary | null;
    equipmentList: EquipmentStatus[];
  }) {

    return (
      <>
        <MonthPicker
          year={selectedYear}
          month={selectedMonth}
          onYearChange={setSelectedYear}
          onMonthChange={setSelectedMonth}
        />
        {kpi && (
          <section>
            <h2 className="text-xl font-semibold mb-3">오늘의 KPI 요약</h2>
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
          </section>
        )}

        <section>
          <h2 className="text-xl font-semibold mt-7 mb-3">장비 상태</h2>
          <div className="grid grid-cols-4 gap-4">
            {equipmentList.map((eq) => {
          const statusColor =
             eq.status === "운영 중"
             ? "bg-green-100 border-green-400"
             : eq.status === "정지"
             ? "bg-red-100 border-red-400"
             : "bg-yellow-100 border-yellow-400";
                return(
                <div
                  key={eq.id}
                  className={`p-4 rounded shadow border flex flex-col ${statusColor}`}>
                  <div className="font-bold mb-2">{eq.id}</div>
                  <div
                    className={`font-semibold mb-1 ${
                      eq.status === "운영 중"
                        ? "text-green-600"
                        : eq.status === "정지"
                        ? "text-gray-500"
                        : "text-red-600"
                    }`}
                  >
                    {eq.status}
                  </div>
                  <div>
                    생산량: {eq.production} / {eq.target}
                  </div>
                  <div>OEE: {eq.oee}%</div>
                </div>
              );
           })}
          </div>
      {/* 품질 카드 */}
      <div className="mt-6"></div>
      <h2 className="text-xl font-semibold mb-2">품질 상태</h2>
      <div className="flex space-x-4">
      <div className="w-[248px] bg-white p-4 rounded shadow border">
      <div className="font-bold mb-2">제품 A</div>
      <div className="text-gray-500 text-sm mb-1">불량률</div>
      <div className="text-2xl font-bold text-red-600">5%</div>
      </div>
      <div className="w-[248px] bg-white p-4 rounded shadow border">
      <div className="font-bold mb-2">제품 B</div>
      <div className="text-gray-500 text-sm mb-1">합격률</div>
      <div className="text-2xl font-bold text-green-600">78%</div>
      </div>
      </div>

      {/* 차트 */}
      <div className="mt-6"></div>
      <h2 className="text-xl font-semibold mb-2">차트</h2>
      <div className="w-64 h-64 bg-white p-3 rounded shadow border mt-4">
        <ResponsiveContainer width="100%" height="100%">
          <PieChart>
            <Pie
              data={downtimeData}
              dataKey="value"
              nameKey="name"
              cx="50%"
              cy="50%"
              outerRadius={80}
            >
              {downtimeData.map((entry, index) => (
                <Cell key={`cell-${index}`} fill={entry.color} />
              ))}
            </Pie>
            <Tooltip />
            <Legend verticalAlign="bottom" />
          </PieChart>
        </ResponsiveContainer>
      </div>
        </section>
      </>
    );
  }

{/* 첫번째 하위 화면*/}
    function MachineAssignContent() {
  const data = [
    { id: 1357, name: "Brooklyn Simmons", machine: "M/C 01", shift: "Day", fabric: "Single Jersey (140 GSM)" },
    { id: 1358, name: "Dianne Russell", machine: "M/C 02", shift: "Day", fabric: "Rib (220 GSM)" },
    { id: 1359, name: "Marvin McKinney", machine: "M/C 03", shift: "Day", fabric: "Interlock (180 GSM)" },
    { id: 1360, name: "Cameron Williamson", machine: "M/C 04", shift: "Day", fabric: "Pique (240 GSM)" },
  ];
   return (
    <div className="space-y-4">
      {/* 헤더 */}
      <div className="flex items-center justify-between">
        <h2 className="text-lg font-semibold">Machine Assign by Operator</h2>
        <a href="#" className="text-blue-600 hover:underline text-sm">View all →</a>
      </div>
      {/* 검색 + 필터 */}
      <div className="flex space-x-3">
        <input
          type="text"
          placeholder="Search name..."
          className="border rounded px-3 py-1 flex-1"/>
        <select className="border rounded px-3 py-1">
          <option>Warehouse 01</option>
          <option>Warehouse 02</option>
        </select>
        <select className="border rounded px-3 py-1">
          <option>Floor 01</option>
          <option>Floor 02</option>
        </select>
      </div>
      {/* 테이블 */}
      <div className="overflow-x-auto">
        <table className="w-full border-collapse border text-sm">
          <thead>
            <tr className="bg-gray-100 text-left">
              <th className="p-2 border">Id</th>
              <th className="p-2 border">Operator Name</th>
              <th className="p-2 border">Machine</th>
              <th className="p-2 border">Shift</th>
              <th className="p-2 border">Fabric</th>
            </tr>
          </thead>
          <tbody>
            {data.map((row) => (
              <tr key={row.id}>
                <td className="p-2 border">{row.id}</td>
                <td className="p-2 border">{row.name}</td>
                <td className="p-2 border">{row.machine}</td>
                <td className="p-2 border">{row.shift}</td>
                <td className="p-2 border">{row.fabric}</td>
              </tr>
            ))}
               </tbody>
             </table>
            </div>
           </div>
            );
          }
          <main className="flex-1 p-6 bg-gray-50 overflow-auto">
  {activeTabId === "dashboard" && <DashboardContent kpi={kpi} equipmentList={equipmentList} />}
  {activeTabId === "subcategory1" && <MachineAssignContent />}
  </main>
  return (
    <div className="flex min-h-screen bg-gray-50 relative">
      {/* 왼쪽 사이드바 */}
      {isSidebarOpen && (
        <aside className="w-64 bg-white border-r shadow flex flex-col transition-width duration-300 z-10">
          <div className="p-4 text-2xl font-bold border-b">MES</div>
          <nav className="flex-1 overflow-y-auto p-4">
            {menuList.map((menu) => (
              <div key={menu.id} className="mb-3">
                <div
                  className="font-semibold cursor-pointer hover:text-blue-600"
                  onClick={() =>
                    !menu.subMenu ? handleMenuClick(menu) : undefined
                  }
                >
                  {menu.label}
                </div>
                {menu.subMenu && (
                  <div className="ml-4 mt-1">
                    {menu.subMenu.map((sub) => (
                      <div
                        key={sub.id}
                        className={`cursor-pointer ${
                          activeTabId === sub.id
                            ? "text-blue-600 font-bold"
                            : "text-gray-700 hover:text-blue-600"
                        }`}
                        onClick={() => handleMenuClick(sub)}
                      >
                        {sub.label}
                      </div>
                    ))}
                  </div>
                )}
              </div>
            ))}
          </nav>
        </aside>
      )}

      {/* 메인 콘텐츠 영역 */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="flex items-center justify-between p-4 bg-gray-200 border-b shadow">
          <div className="flex items-center space-x-4">
            <button
              onClick={toggleSidebar}
              className="p-2 rounded hover:bg-gray-300"
              aria-label="Toggle Sidebar"
            >
              <svg
                className="w-6 h-6 text-gray-700"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                viewBox="0 0 24 24"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <line x1="3" y1="12" x2="21" y2="12" />
                <line x1="3" y1="6" x2="21" y2="6" />
                <line x1="3" y1="18" x2="21" y2="18" />
              </svg>
            </button>
            <div className="text-xl font-bold">Main</div>
            <nav className="flex space-x-2 overflow-x-auto max-w-[600px]">
              {openTabs.map((tab) => (
                <div
                  key={tab.id}
                  className={`flex items-center space-x-1 px-3 py-1 rounded-t cursor-pointer ${
                    activeTabId === tab.id
                      ? "bg-white border-t border-x border-gray-300"
                      : "bg-gray-300 text-gray-600"
                  }`}
                  onClick={() => handleTabClick(tab.id)}
                >
                  <span>{tab.label}</span>
                  <button
                    onClick={(e) => {
                      e.stopPropagation();
                      handleTabClose(tab.id);
                    }}
                    className="ml-1 text-gray-500 hover:text-red-500"
                  >
                    ×
                  </button>
                </div>
              ))}
            </nav>
          </div>
          <div className="flex items-center space-x-4"></div>
          {/* 검색 바 */}
          <div className="relative">
          <input
          type="text"
          placeholder="Search"
          className="border rounded pl-8 pr-3 py-1 focus:outline-none focus:ring focus:border-blue-400"
          />
          <svg
          className="w-4 h-4 absolute left-2 top-1/2 transform -translate-y-1/2 text-gray-400"
          fill="none"
          stroke="currentColor"
          strokeWidth="2"
          viewBox="0 0 24 24"
          strokeLinecap="round"
          strokeLinejoin="round"
          >
          <circle cx="11" cy="11" r="8" />
          <line x1="21" y1="21" x2="16.65" y2="16.65" />
          </svg>
          </div>

          {/* 사용자 + 오른쪽 사이드바 버튼 */}
          <div className="flex items-center space-x-4">
            <span className="text-lg font-semibold">
              👤 {userInfo?.username ?? "사용자"} ({userInfo?.role ?? "역할"})
            </span>
            <button
              onClick={toggleRightSidebar}
              className="p-1 rounded hover:bg-gray-300"
              aria-label="Toggle Right Sidebar"
            >
              <svg
                className="w-5 h-5 text-gray-700"
                fill="none"
                stroke="currentColor"
                strokeWidth="2"
                viewBox="0 0 24 24"
                strokeLinecap="round"
                strokeLinejoin="round"
              >
                <circle cx="12" cy="12" r="3" />
                <path d="M19.4 15a1.65 1.65 0 0 0 .33 1.82l.06.06a2 2 0 0 1-2.83 2.83l-.06-.06a1.65 1.65 0 0 0-1.82-.33 1.65 1.65 0 0 0-1 1.51V21a2 2 0 0 1-4 0v-.09a1.65 1.65 0 0 0-1-1.51 1.65 1.65 0 0 0-1.82.33l-.06.06a2 2 0 1 1-2.83-2.83l.06-.06a1.65 1.65 0 0 0 .33-1.82 1.65 1.65 0 0 0-1.51-1H3a2 2 0 0 1 0-4h.09a1.65 1.65 0 0 0 1.51-1 1.65 1.65 0 0 0-.33-1.82l-.06-.06a2 2 0 1 1 2.83-2.83l.06.06a1.65 1.65 0 0 0 1.82.33H9a1.65 1.65 0 0 0 1-1.51V3a2 2 0 0 1 4 0v.09a1.65 1.65 0 0 0 1 1.51 1.65 1.65 0 0 0 1.82-.33l.06-.06a2 2 0 1 1 2.83 2.83l-.06.06a1.65 1.65 0 0 0-.33 1.82V9c0 .66.42 1.25 1.03 1.47.32.12.65.18.97.18h.09a2 2 0 0 1 0 4h-.09a1.65 1.65 0 0 0-1.51 1z" />
              </svg>
            </button>
          </div>
        </header>

        <main className="flex-1 p-6 bg-gray-50 overflow-auto">
          {activeTabId === "dashboard" ? (
            <DashboardContent
              kpi={kpi}
              equipmentList={equipmentList}
            />
          ) : (
            <div></div>
          )}
        </main>
      </div>

      {/* 오른쪽 사이드바 */}
      {isRightSidebarOpen && (
        <aside className="w-80 bg-white border-l shadow fixed right-0 top-0 bottom-0 z-50 flex flex-col">
          {/* 헤더 */}
          <div className="p-4 border-b flex items-center justify-between">
            <span className="text-lg font-semibold">Job Details</span>
            <button
              onClick={toggleRightSidebar}
              className="text-gray-500 hover:text-red-500 text-xl"
              aria-label="Close Right Sidebar"
            >
              ×
            </button>
          </div>

          {/* 폼 영역 */}
          <div className="p-4 flex-1 overflow-y-auto space-y-4">
              <div>
              <label className="block font-medium">
              작업 상태 <span className="text-red-500">*</span>
              </label></div>
              <select
              className="w-full border rounded px-2 py-1"
              defaultValue="Run"
              >
              <option value="Run">Run</option>
              <option value="IDLE">IDLE</option>
              <option value="Down">Down</option>
              </select>
  
            <div>
              <label className="block font-medium">
                시작 수량 <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                className="w-full border rounded px-2 py-1"
                defaultValue={100}
              />
            </div>

            <div>
              <label className="block font-medium">
                요청 수량 <span className="text-red-500">*</span>
              </label>
              <input
                type="number"
                className="w-full border rounded px-2 py-1"
                defaultValue={100}
              />
            </div>

            <div>
              <label className="block font-medium">시작 시간</label>
              <input
                type="datetime-local"
                className="w-full border rounded px-2 py-1"
              />
            </div>

            <div>
              <label className="block font-medium">마감일</label>
              <input
                type="datetime-local"
                className="w-full border rounded px-2 py-1"
              />
            </div>

            <div>
              <label className="block font-medium">설비 ID</label>
              <select className="w-full border rounded px-2 py-1">
                <option>E-0001</option>
                <option>E-0002</option>
                <option>E-0003</option>
                <option>E-0004</option>
              </select>
            </div>
          </div>

          {/* 하단 버튼 */}
          <div className="p-4 border-t flex justify-end space-x-2">
            <button className="px-4 py-2 rounded bg-gray-200 hover:bg-gray-300">
              취소
            </button>
            <button className="px-4 py-2 rounded bg-blue-600 text-white hover:bg-blue-700">
              저장
            </button>
          </div>
        </aside>
      )}
    </div>
  );
}