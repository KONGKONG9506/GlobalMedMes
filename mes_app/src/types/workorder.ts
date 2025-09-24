export type WorkOrderItem = {
  workOrderId: string;
  workOrderNumber: string;
  orderQty: number;
  produceQty: number;
  statusCode: string;
  startTs:string | null;
  createdAt:string;
  modifiedAt:string | null;
  itemName: string;
  itemId:string;
  itemType: string;
  unit: string | null; // "P"|"R"|"C"|null
  itemDescription:string;
  processId: string;
  processName:string;
  processDescription:string;
  equipmentId: string;
  equipmentName: string;
  workcenterName: string | null; 
};

export type WorkOrderCreateReq = {
  workOrderNumber: string;
  itemId: string;
  processId: string;
  equipmentId: string;
  orderQty: number;
  createdBy?: string;
};

export type WorkOrderCreateRes = { workOrderId: string; status: string };
export type WorkOrderStatusReq = { toStatus: "R" | "C" };