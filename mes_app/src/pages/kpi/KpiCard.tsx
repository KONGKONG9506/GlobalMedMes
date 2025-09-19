type Props = {
  title: string;
  targetLabel?: string;
  target?: number | string; // 숫자 기반으로 비교 가능하도록 number도 허용
  actualLabel?: string;
  actual?: number | string;
};

export default function KpiCard({ title, targetLabel, target, actualLabel, actual }: Props) {
  // 숫자로 변환 (문자열로 들어올 수도 있으므로)
  const actualNum = typeof actual === "string" ? parseFloat(actual) : actual ?? 0;
  const targetNum = typeof target === "string" ? parseFloat(target) : target ?? 0;

  // 색상 규칙 적용
  const status =
    actualNum >= targetNum ? "ok"
    : actualNum >= targetNum * 0.95 ? "warn"
    : "bad";

  const color =
    status === "ok" ? "border-green-600 text-green-700 bg-green-50"
    : status === "warn" ? "border-amber-500 text-amber-700 bg-amber-50"
    : status === "bad" ? "border-red-600 text-red-700 bg-red-50"
    : "border-gray-300 text-gray-700 bg-white";

  return (
    <div className={`border rounded p-3 ${color}`}>
      <div className="text-sm text-gray-600">{title}</div>
      <div className="mt-2 flex items-baseline gap-3">
        <div className="text-2xl font-semibold">{actual ?? "-"}</div>
        {target != null && (
          <div className="text-sm text-gray-600">
            {targetLabel ?? "목표"}: <span className="font-medium">{target}</span>
          </div>
        )}
      </div>
      {actualLabel && <div className="text-xs text-gray-500 mt-1">{actualLabel}</div>}
    </div>
  );
}
