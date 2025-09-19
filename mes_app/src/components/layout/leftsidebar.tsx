import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { toMenuItems, toMenuNodesFromItems } from "../../adapters/menu";
import type { MenuItem } from "../../types/menu";
import { useMenusStore } from "../../store/menus";
import { Link, useLocation } from "react-router-dom";

type LeftSidebarProps = {
  isOpen: boolean;
  onClose?: () => void;
};

export default function LeftSidebar({ isOpen, onClose }: LeftSidebarProps) {
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const setMenusGlobal = useMenusStore((s) => s.setMenus);
  const loc = useLocation();

  useEffect(() => {
    api
      .get("/menus/my")
      .then((res) => {
        const uiTree = toMenuItems(res.data.menus);      // 서버 → UI 변환
        setMenus(uiTree);
        const nodes = toMenuNodesFromItems(uiTree);      // UI → 전역(MenuNode)
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
            className={`px-2 py-1 rounded ${
              loc.pathname.startsWith(m.path) ? "bg-gray-200 font-semibold" : ""
            }`}
            to={m.path}
          >
            {m.title}
          </Link>
        ))}
      </nav>
    </aside>
  );
}
