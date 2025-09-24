import { Outlet } from "react-router-dom";
import LeftSidebar from "./leftsidebar";
import ToastHost from "../common/ToastHost";
import { useState } from "react";

export default function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false); // 왼쪽 사이드바 기본 열림

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  return (
    <div className="flex min-h-screen bg-blue-50 relative">
      {/* 왼쪽 사이드바 */}
      <LeftSidebar
        isOpen={isSidebarOpen}
        onClose={() => setIsSidebarOpen(false)}
      />

      {/* 메인 콘텐츠 영역 */}
      <div className="flex-1 flex flex-col overflow-hidden">
        <header className="flex items-center justify-between p-4 bg-blue-100 border-b shadow">
          <button
            onClick={toggleSidebar}
            className="p-4 rounded hover:bg-gray-300 , py-2"
          >
            ≡
          </button>
          <div className="text-xl font-bold">GlobalMed</div>
          <div className="flex items-center space-x-4">
          </div>
        </header>

        <main className="flex-1 p-6 bg-gray-50 overflow-auto">
          <Outlet />
        </main>
      </div>
      {/* 토스트 알림 */}
      <ToastHost />
    </div>
  );
}
