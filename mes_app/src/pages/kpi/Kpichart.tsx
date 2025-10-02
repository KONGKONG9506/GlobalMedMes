// src/pages/kpi/KpiChart.tsx
import { Line } from "react-chartjs-2";
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import type { KpiRes } from "../../types/kpi";

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

type Props = {
  dataList: KpiRes[];
};

export default function KpiChart({ dataList }: Props) {
  // 2025년 8월 1일부터 31일까지 데이터만 필터링
  const augustData = dataList.filter((item) => {
    const date = new Date(item.kpiDate);
    return date.getFullYear() === 2025 && date.getMonth() === 7; // 8월 → month index 7
  });

  // 날짜 레이블
  const labels = augustData.map((item) => item.kpiDate);

  // 차트 데이터셋
  const data = {
    labels,
    datasets: [
      {
        label: "Productivity (%)",
        data: augustData.map((item) => item.actualProductivity),
        borderColor: "orange",
        backgroundColor: "orange",
        tension: 0.3,
        fill: false,
        pointRadius: 4,
      },
      {
        label: "OEE (%)",
        data: augustData.map((item) => item.actualOee),
        borderColor: "skyblue",
        backgroundColor: "skyblue",
        tension: 0.3,
        fill: false,
        pointRadius: 4,
      },
      {
        label: "Yield (%)",
        data: augustData.map((item) => item.actualYield),
        borderColor: "green",
        backgroundColor: "green",
        tension: 0.3,
        fill: false,
        pointRadius: 4,
      },
      {
        label: "Defect Rate (%)",
        data: augustData.map((item) => item.actualDefectRate),
        borderColor: "yellow",
        backgroundColor: "yellow",
        tension: 0.3,
        fill: false,
        pointRadius: 4,
      },
    ],
  };

  // 차트 옵션
  const options = {
    responsive: true,
    plugins: {
      legend: {
        position: "bottom" as const,
      },
      title: {
        display: true,
        text: "KPI Trends (August 2025)",
        font: {
          size: 16,
        },
      },
    },
    scales: {
      y: {
        beginAtZero: true,
        max: 130,
        title: {
          display: true,
          text: "Percentage (%)",
        },
      },
      x: {
        title: {
          display: true,
          text: "Date",
        },
      },
    },
  };

  return (
    <div className="p-6 w-full min-h-[500px] bg-white rounded-xl shadow-md">
      <Line data={data} options={options} />
    </div>
  );
}