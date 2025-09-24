export type StatusColor = "ok" | "warn" | "bad" | "none";

export function statusColor(target?: number | null, actual?: number | null): StatusColor {
  if (target == null || actual == null) return "none";
  if (actual >= target) return "ok";
  if (actual >= target * 0.95) return "warn"; // 95% 이상 ~ 100% 미만
  return "bad"; // 그 외
}

export function fmtPercent(v?: number | null): string {
  if (v == null) return "-";
  return `${v.toFixed(2)}%`;
}

export function fmtNumber(v?: number | null, digits = 0): string {
  if (v == null) return "-";
  return v.toFixed(digits);
}