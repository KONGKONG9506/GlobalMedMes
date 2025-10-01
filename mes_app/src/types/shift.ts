
export type ShiftCalander = {
  calendarId: Number;
  shiftDate: Date;
  shiftName: String;
  equipmentId: String;
  equipmentName: String;
  workcenterId: String;
  workcenterName: String;
  // workerName: String;
  // workerAmount: Number;
  startTs : String;
  endTs : String;
};

export type ShiftAssignment = {
  shiftDate: Date;
  shiftName: String;
  equipmentId: String;
  equipmentName: String;
  workcenterId: String;
  workcenterName: String;
  workerId: String;
  startTs : String;
  endTs : String;
};

export type ShiftAssignmentView = {
  startTs: string;          // ISO 문자열로 받음
  endTs: string;            // ISO 문자열로 받음
  shiftName: string;
  equipmentId: string;
  equipmentName: string;
  workcenterId: string;
  workcenterName: string;
  workerDisplay: string;    // 대표 이름 + 외 n명
  workerNames: string[];    // 개별 이름 배열
  workerCount: number;
};
