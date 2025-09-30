import { Outlet, useNavigate } from "react-router-dom";
import LeftSidebar from "./leftsidebar";
import ToastHost from "../common/ToastHost";
import { useState } from "react";
import { Menu, LogOut} from "lucide-react";
import { useAuthStore } from "../../store/auth";

export default function AppLayout() {
  const [isSidebarOpen, setIsSidebarOpen] = useState(false);
  const toggleSidebar = () => setIsSidebarOpen(!isSidebarOpen);

  const logout = useAuthStore((s) => s.logout);
  const nav = useNavigate();

  const handleLogout = () =>{
    logout();
    nav("/login",{replace:true});
  };

  return (
    <div className="flex min-h-screen bg-gradient-to-br from-blue-50 via-white to-blue-100">
      {/* 왼쪽 사이드바 */}
      <LeftSidebar isOpen={isSidebarOpen} />

      {/* 메인 콘텐츠 영역 */}
      <div
          className={`flex-1 flex flex-col transition-all duration-300`}
        style={{
          marginLeft: isSidebarOpen ? 170 : 0,   // 왼쪽 사이드바 width 64 * 4 = 256px
          // marginRight: isRightOpen ? 384 : 0, // 오른쪽 사이드바 width 96 * 4 = 384px
        }}
      >
        {/* 헤더 */}
        <header className="flex items-center justify-between px-6 py-4 bg-gradient-to-r from-blue-500 to-indigo-300">
          <div className="flex items-center gap-3">
            <button
              onClick={toggleSidebar}
              className="p-2 rounded-lg" //hover:bg-white/20
            >
              <Menu size={24} />
            </button>
            <span className="text-xl font-bold tracking-wide text-white">
              GlobalMed MES
            </span>
          </div>
           {/* 오른쪽 상단 영역 */}
         <div className="flex items-center gap-4">
          <button
          onClick={handleLogout}
          className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-white text-blue-600 hover:bg-gray-100 font-medium shadow transitio"
          >
            <LogOut size={18} className="text-blue-600"/>
            로그아웃
           </button>
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