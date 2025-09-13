export type KpiRes = {
  kpiId: number;
  kpiDate: string;
  equipmentId: string;
  processId: string;
  itemId: string;
  actualOee?: number | null;
  actualProductivity?: number | null;
  actualYield?: number | null;
  actualDefectRate?: number | null;
  aggregationType?: 'REALTIME' | 'DAILY' | 'MONTHLY' | string;
  startTime?: string;
  endTime?: string;
};

export type KpiApiResponse = {
  content: KpiRes[];
  page: number;
  size: number;
  totalElements: number;
};
