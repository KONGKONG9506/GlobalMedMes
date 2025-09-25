import { Outlet } from "react-router-dom";
import LeftSidebar from "./leftsidebar";
import ToastHost from "../common/ToastHost";
import { useState } from "react";
import { Menu } from "lucide-react";

export default function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);

  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100">
      {/* 왼쪽 사이드바 */}
      <LeftSidebar isOpen={isSidebarOpen} />

      {/* 메인 콘텐츠 영역 */}
      <div
          className={`flex-1 flex flex-col transition-all duration-300`}
        style={{
          marginLeft: isSidebarOpen ? 256 : 0,   // 왼쪽 사이드바 width 64 * 4 = 256px
          // marginRight: isRightOpen ? 384 : 0, // 오른쪽 사이드바 width 96 * 4 = 384px
        }}
      >
        {/* 헤더 */}
        <header className="flex items-center justify-between px-6 py-4 bg-gradient-to-r from-blue-500 to-indigo-300 text-white shadow-md">
          <div className="flex items-center gap-3">
            <button
              onClick={toggleSidebar}
              className="p-2 rounded-lg hover:bg-white/20 transition-colors"
            >
              <Menu size={24} />
            </button>
            <span className="text-xl font-bold tracking-wide">
              GlobalMed MES
            </span>
          </div>
         <div className="flex items-center gap-4">
          </div>
        </header>

        {/* 메인 */}
        <main className="flex-1 p-6 bg-gray-50 overflow-auto">
          <div className="bg-white rounded-2xl shadow-lg p-6 h-full">
            <Outlet/>
          </div>
        </main>
      </div>

      {/* 토스트 알림 */}
      <ToastHost />
    </div>
  );
}