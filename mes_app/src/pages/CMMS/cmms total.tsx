import { useState } from "react";
import CmmsWorkorder from "./cmms workorder";
import CmmsFaultlogs from "./cmms faultlogs";
import CmmsPmPlan from "./cmms pm-plan";

export default function CmmsTotal() {
  const [activeTab, setActiveTab] = useState<"wo" | "fault" | "pm">("wo");

  return (
    <div className="p-4">
      {/* 탭 버튼 */}
      <div className="flex gap-2 mb-4">
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
          고장&수리
        </button>
        <button
          onClick={() => setActiveTab("pm")}
          className={`px-4 py-2 rounded ${
            activeTab === "pm" ? "bg-blue-500 text-white" : "bg-gray-200"
          }`}
        >
          PM 계획
        </button>
      </div>

      {/* 내용 */}
      <div className="border rounded p-4 bg-white shadow">
        {activeTab === "wo" && <CmmsWorkorder />}
        {activeTab === "fault" && <CmmsFaultlogs />}
        {activeTab === "pm" && <CmmsPmPlan />}
      </div>
    </div>
  );
}