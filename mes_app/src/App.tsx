import { BrowserRouter, Routes, Route, Navigate } from "react-router-dom";
import ProtectedRoute from "./routes/ProtectedRoute";
import AppLayout from "./components/layout/AppLayout";
import Login from "./pages/Login";
import WorkOrdersList from "./pages/workorders/WorkOrdersList";
import PerformancesList from "./pages/performances/PerformancesList";
import EquipStatusPage from "./pages/equipstatus/EquipStatusPage";
import KpiPage from "./pages/kpi/KpiPage";
import WorkOrderCreate from "./pages/workorders/WorkOrderCreate";
import PerformanceCreate from "./pages/performances/PerformanceCreate";
import PermRoute from "./routes/PermRoute";
import Forbidden from "./pages/Forbidden";
import Management from "./pages/Management/Managementcreate";
import CmmsTotal from "./pages/CMMS/cmms total";
import Dashboards from "./pages/Dashboards/dashboards";
import Shift from "./pages/shift/Shift";
import PlanList from "./pages/plan/PlanList";
import PlanDetail from "./pages/plan/PlanDetail";
import ProcessList from "./pages/process/ProcessListPage";

export default function App() {
  return (
    <BrowserRouter>
      <Routes>
        {/* 로그인은 레이아웃 없이 별도 */}
        <Route path="/login" element={<Login />} />

        {/* ProtectedRoute + AppLayout 안에 들어가는 페이지들 */}
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <AppLayout />
            </ProtectedRoute>
          }
        >
          <Route index element={<Navigate to="dashboard" replace />} />
          {/* Dashboard */}
          <Route path="dashboard" element={<Dashboards />} />

          {/* planList */}
          <Route path="plan" element={<PlanList />} />
          <Route path="plans/:planId" element={<PlanDetail />} />

          {/* Work Orders */}
          <Route path="work-orders" element={<WorkOrdersList />} />
          <Route
            path="work-orders/new"
            element={
              <PermRoute require="write">
                <WorkOrderCreate />
              </PermRoute>
            }
          />

          {/* Performances */}
          <Route path="performances" element={<PerformancesList />} />
          <Route
            path="performances/new"
            element={
              <PermRoute require="write">
                <PerformanceCreate />
              </PermRoute>
            }
          />

          {/* Management */}
          <Route
            path="management"
            element={
              <PermRoute require="write">
                <Management />
              </PermRoute>
            }
          />

          {/* CMMS */}
          <Route path="cmms">
            <Route
              index
              element={
                <PermRoute require="write">
                  <CmmsTotal />
                </PermRoute>
              }
            />
          </Route>

          <Route path="process">
            <Route
              index
              element={
                <PermRoute require="write">
                  <ProcessList />
                </PermRoute>
              }
            />
          </Route>

          <Route path="shift">
            <Route
              index
              element={
                <PermRoute require="write">
                  <Shift />
                  
                </PermRoute>
              }
            />
          </Route>

          {/* 기타 페이지 */}
          <Route path="equip-status" element={<EquipStatusPage />} />
          <Route path="kpi" element={<KpiPage />} />
          <Route path="403" element={<Forbidden />} />
        </Route>
      </Routes>
    </BrowserRouter>
  );
}