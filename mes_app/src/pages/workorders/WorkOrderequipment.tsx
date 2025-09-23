type Option = { name: string; Id: string;};

type Props = {
  Id: string;        
  options: Option[];
  onChange: (v: string) => void;
};

export default function EquipmentsSelect({ Id, options, onChange }: Props) {
  return (
    <select
      className="border px-2 py-1"
      value={Id}
      onChange={(e) => onChange(e.target.value)}
    >
      {options.map((o) => (
        <option key={o.Id} value={o.Id}>{o.name}</option>
      ))}
    </select>
  );
}

export const equipmentOptions: Option[] = [
  { name: "1호기 블라스팅기" ,Id: "BLS-001" },
  { name: "초음파 정밀 세척기",Id: "CLN-001" },
  { name: "1호기 CNC 선반" ,Id: "CNC-001" },
  { name: "2호기 CNC 선반" ,Id: "CNC-002" },
  { name: "1호기 자동 에칭조" ,Id: "ETC-001" },
  { name: "3차원 비전 검사기" ,Id: "INS-001" }
];