import { useState } from "react";
import ShiftCalendar from "./ShiftCalendar";
import ShiftAssignmentList from "./ShiftAssignment";

export default function Shift(){
  const [activeTab, setActiveTab] = useState<"Calendar" | "Assignment">("Calendar");
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
              달력 생성
            </button>
            <button
              onClick={() => setActiveTab("Assignment")}
              className={`px-4 py-2 rounded ${
                activeTab === "Assignment" ? "bg-blue-500 text-white" : "bg-gray-200"
              }`}
            >
              교대 목록
            </button>
          </div>
    
          {/* 내용 */}
          <div className="border rounded p-4 bg-white shadow">
            {activeTab === "Calendar" && <ShiftCalendar />}
            {activeTab === "Assignment" && <ShiftAssignmentList />}
          </div>
        </div>
    );
}