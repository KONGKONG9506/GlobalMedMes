import { useEffect, useState } from "react";
import { api } from "../../lib/api";
import { toMenuItems, toMenuNodesFromItems } from "../../adapters/menu";
import type { MenuItem } from "../../types/menu";
import { useMenusStore } from "../../store/menus";
import { Link, useLocation } from "react-router-dom";

type LeftSidebarProps = {
  isOpen: boolean;
};

export default function LeftSidebar({ isOpen }: LeftSidebarProps) {
  const [menus, setMenus] = useState<MenuItem[]>([]);
  const setMenusGlobal = useMenusStore((s) => s.setMenus);
  const loc = useLocation();

  useEffect(() => {
    api
      .get("/menus/my")
      .then((res) => {
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

  return (
    <aside
      className={`fixed top-0 left-0 w-64 h-full bg-white border-r shadow z-30
      transform transition-transform duration-300
      ${isOpen ? "translate-x-0" : "-translate-x-64"}`}
    >
      {/* 상단 제목 */}
      <div className="p-4 border-b font-bold text-lg">GlobalMed MES</div>

      {/* 메뉴 리스트 */}
      <nav className="flex-1 overflow-y-auto p-4 flex flex-col gap-2">
        {menus.map((m) => (
          <Link
            key={m.key}
            className={`px-3 py-2 rounded-lg transition-colors ${
              loc.pathname.startsWith(m.path)
                ? "bg-blue-100 font-semibold text-blue-600"
                : "hover:bg-gray-100"
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