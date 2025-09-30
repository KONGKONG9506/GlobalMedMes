import { useState } from "react";
import ShiftCalendar from "./ShiftCalendar";

export default function Shift(){
  const [activeTab, setActiveTab] = useState<"Calendar" >("Calendar");
    return(
        <div className="p-4">
          {/* 탭 버튼 */}
          <div className="flex gap-2 mb-4">
            <button
              onClick={() => setActiveTab("Calendar")}
              className={`px-4 py-2 rounded ${
                activeTab === "Calendar" ? "bg-blue-500 text-white" : "bg-gray-200"
              }`}
            >
              캘린더 생성
            </button>
          </div>
    
          {/* 내용 */}
          <div className="border rounded p-4 bg-white shadow">
            {activeTab === "Calendar" && <ShiftCalendar />}
          </div>
        </div>
    );
}