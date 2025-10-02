
// 설비 DTO
type Equipment = {
  equipmentId: string;
  equipmentName: string;
  statusCode: string;
  workcenterId: string;
};

// 자격증 DTO
type Cert = {
  certCode: string;
  certName: string;
  certDescription: string;
};

// Process 상세 DTO
export type ProcessDetailDto = {
  id: string;
  name: string;
  description: string;
  equipments: Equipment[];
  requiredCerts: Cert[];
  LastmodAt: string | null;
  LastmodBy: string | null;
};
