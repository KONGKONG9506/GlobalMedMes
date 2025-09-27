export type StatusColor = "ok" | "warn" | "bad" | "none";

export function statusColor(target?: number | null, actual?: number | null): StatusColor {
  if (target == null || actual == null) return "none";
  if (actual >= target) return "ok"; // 100%
  if (actual >= target * 0.95) return "warn"; // 95% 이상 ~ 100% 미만
  return "bad"; // 그 외
}

export function statusDefectColor(target?: number | null, actual?: number | null): StatusColor {
  if (target == null || actual == null) return "none";
  if (actual <= 5) return "ok";  // 0~5%
  if (actual <= 10) return "warn"; // 5~10%
  return "bad";
}

export function fmtPercent(v?: number | null): string {
  if (v == null) return "-";
  return `${v.toFixed(2)}%`;
}

export function fmtNumber(v?: number | null, digits = 0): string {
  if (v == null) return "-";
  return v.toFixed(digits);
}