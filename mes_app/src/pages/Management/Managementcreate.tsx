// src/pages/Management.tsx
import { useState } from "react";

const tabs = [
  { key: "equipment", label: "설비 관리" },
  { key: "process", label: "공정 관리" },
  { key: "item", label: "품목 관리" },
  { key: "operator", label: "작업자 관리" },
  { key: "user", label: "사용자/권한" },
];

export default function Management() {
  const [activeTab, setActiveTab] = useState("equipment");

  return (
    <div className="p-6 bg-gray-50 min-h-screen"  >
      <h1 className="text-3xl font-bold mb-6">관리 페이지</h1>

      {/* 탭 메뉴 */}
      <div className="flex space-x-3 border-b border-gray-300 mb-6">
        {tabs.map((tab) => (
          <button
            key={tab.key}
            className={`pb-2 px-4 text-sm font-medium transition-all duration-150 ${
              activeTab === tab.key
                ? "border-b-2 border-blue-600 text-blue-600"
                : "text-gray-500 hover:text-blue-500"
            }`}
            onClick={() => setActiveTab(tab.key)}
          >
            {tab.label}
          </button>
        ))}
      </div>

      {/* 탭 내용 */}
      <div className="bg-white border border-gray-200 rounded-lg p-6 shadow-sm">
        {activeTab === "equipment" && <EquipmentManager />}
        {activeTab === "process" && <ProcessManager />}
        {activeTab === "item" && <ItemManager />}
        {activeTab === "operator" && <OperatorManager />}
        {activeTab === "user" && <UserManager />}
      </div>
    </div>
  );
}

// 각 탭별 내용 - 카드 형태로 가독성 있게 구성
function EquipmentManager() {
  return (
    <div className="space-y-2">
      <h2 className="text-xl font-semibold mb-2">🔧 설비 관리</h2>
      <p className="text-gray-600">설비 목록 조회, 등록, 수정, 삭제 기능을 제공합니다.</p>
    </div>
  );
}

function ProcessManager() {
  return (
    <div className="space-y-2">
      <h2 className="text-xl font-semibold mb-2">⚙️ 공정 관리</h2>
      <p className="text-gray-600">공정별 정보 등록 및 관리가 가능합니다.</p>
    </div>
  );
}

function ItemManager() {
  return (
    <div className="space-y-2">
      <h2 className="text-xl font-semibold mb-2">📦 품목 관리</h2>
      <p className="text-gray-600">생산 품목에 대한 정보를 관리합니다.</p>
    </div>
  );
}

function OperatorManager() {
  return (
    <div className="space-y-2">
      <h2 className="text-xl font-semibold mb-2">👷 작업자 관리</h2>
      <p className="text-gray-600">작업자 정보 및 역할을 관리합니다.</p>
    </div>
  );
}

function UserManager() {
  return (
    <div className="space-y-2">
      <h2 className="text-xl font-semibold mb-2">👤 사용자/권한 관리</h2>
      <p className="text-gray-600">사용자 계정과 권한을 설정할 수 있습니다.</p>
    </div>
  );
}