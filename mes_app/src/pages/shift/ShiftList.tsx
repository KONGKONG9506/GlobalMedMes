
type ShiftEquipList = {
  equId: string;         // 설비 ID
  name: string;          // 설비 이름
  workcenterId: string;  // 설비가 속한 워크센터 ID
};

export const ShiftEquipLists: ShiftEquipList[] = [
  { equId: "CNC-001", name: "1호기 CNC 선반", workcenterId: "WCR-001" },
  { equId: "CNC-002", name: "2호기 CNC 선반", workcenterId: "WCR-001" },
  { equId: "BLS-001", name: "1호기 블라스팅기", workcenterId: "WCR-002" },
  { equId: "ETC-001", name: "1호기 자동 에칭조", workcenterId: "WCR-002" },
  { equId: "CLN-001", name: "초음파 정밀 세척기", workcenterId: "WCR-002" },
  { equId: "INS-001", name: "3차원 비전 검사기", workcenterId: "QZR-001" }
];

// 워크센터 정보 매핑
export const WorkcenterMap: Record<string, string> = {
  "WCR-001": "정밀가공실",
  "WCR-002": "표면처리실",
  "WCR-003": "클린룸 및 포장실",
  "QZR-001": "품질보증실"
};
