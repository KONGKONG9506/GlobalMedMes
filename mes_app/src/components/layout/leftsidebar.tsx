import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { toMenuItems, toMenuNodesFromItems } from "../../adapters/menu";
import type { MenuItem } from "../../types/menu";
import { useMenusStore } from "../../store/menus";
import { Link, useLocation } from "react-router-dom";
import {
  LayoutDashboard,
  ClipboardList,
  Wrench,
  TrendingUp,
  LineChart,
  Warehouse,
  CheckCircle,
  Settings,
} from 'lucide-react';

type LeftSidebarProps = {
  isOpen: boolean;
  onClose?: () => void;
};

// 메뉴의 'key'에 해당하는 아이콘을 매핑하는 객체
const menuIcons = {
  'DASH': <LayoutDashboard size={20} />,
  'WO': <ClipboardList size={20} />,
  'EQPSTAT': <Wrench size={20} />,
  'PERF': <TrendingUp size={20} />,
  'KPI': <LineChart size={20} />,
  'CMMS': <Warehouse size={20} />,
  'QUALITY': <CheckCircle size={20} />,
  'ADMIN': <Settings size={20} />,
};

// ... (기존 코드 생략)

export default function LeftSidebar({ isOpen, onClose }: LeftSidebarProps) {
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const setMenusGlobal = useMenusStore((s) => s.setMenus);
  const loc = useLocation();

  useEffect(() => {
    api
      .get("/menus/my")
      .then((res) => {
        // toMenuItems 함수를 수정하여 아이콘 정보를 추가합니다.
        const uiTree = toMenuItems(res.data.menus);
        setMenus(uiTree);
        const nodes = toMenuNodesFromItems(uiTree);
        setMenusGlobal(nodes);
      })
      .catch(() => {
        setMenus([]);
        setMenusGlobal([]);
      });
  }, [setMenusGlobal]);

  if (!isOpen) return null;

  return (
    <aside className="absolute top-0 left-0 w-64 h-full bg-white border-r shadow z-30 flex flex-col transition-transform duration-300">
      {/* 상단 제목 + 닫기 버튼 */}
      <div className="p-4 border-b flex justify-between items-center">
        <span className="font-bold text-lg">GlobalMed MES</span>
        {onClose && (
          <button
            onClick={onClose}
            className="text-gray-500 hover:text-red-500 text-xl"
            aria-label="Close Sidebar"
          >
            ×
          </button>
        )}
      </div>

      {/* 메뉴 리스트 */}
      <nav className="flex-1 overflow-y-auto p-4 flex flex-col gap-2">
        {menus.map((m) => (
          <Link
            key={m.key}
            className={`px-2 py-1 rounded flex items-center gap-2 ${
              loc.pathname.startsWith(m.path) ? "bg-gray-200 font-semibold" : ""
            }`}
            to={m.path}
          >
            {menuIcons[m.key]} {/* DB 정보의 'code' 값으로 아이콘을 매핑 */}
            <span>{m.title}</span>
          </Link>
        ))}
      </nav>
    </aside>
  );
}
