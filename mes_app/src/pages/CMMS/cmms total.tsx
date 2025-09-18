import { useState } from "react";
import CmmsPmPlan from "./cmms pm-plan";
import CmmsWorkorder from "./cmms workorder";
import CmmsFaultlogs from "./cmms faultlogs";

export default function CmmsTotal() {
  const [activeTab, setActiveTab] = useState<"pm" | "wo" | "fault">("pm");

  return (
    <div className="p-4">
      {/* 탭 버튼 */}
      <div className="flex gap-2 mb-4">
        <button
          onClick={() => setActiveTab("pm")}
          className={`px-4 py-2 rounded ${
            activeTab === "pm" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
        >
          PM 계획
        </button>
        <button
          onClick={() => setActiveTab("wo")}
          className={`px-4 py-2 rounded ${
            activeTab === "wo" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
        >
          작업 지시서(WO)
        </button>
        <button
          onClick={() => setActiveTab("fault")}
          className={`px-4 py-2 rounded ${
            activeTab === "fault" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
        >
          고장 로그
        </button>
      </div>

      {/* 내용 */}
      <div className="border rounded p-4 bg-white shadow">
        {activeTab === "pm" && <CmmsPmPlan />}
        {activeTab === "wo" && <CmmsWorkorder />}
        {activeTab === "fault" && <CmmsFaultlogs />}
      </div>
    </div>
  );
}
