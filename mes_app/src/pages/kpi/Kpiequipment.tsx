type Option = { name: string; equId: string; };

type Props = {
  equId: string;        
  options: Option[];
  onChange: (v: string) => void;
};

export default function KpisSelect({ equId, options, onChange }: Props) {
  return (
    <select
      className="border px-2 py-1"
      value={equId}
      onChange={(e) => onChange(e.target.value)}
    >
      {options.map((o) => (
        <option key={o.equId} value={o.equId}>{o.name}</option>
      ))}
    </select>
  );
}

export const KpiOptions: Option[] = [
  { name: "1호기 블라스팅기" ,equId: "BLS-001" },
  { name: "초음파 정밀 세척기",equId: "CLN-001" },
  { name: "1호기 CNC 선반" ,equId: "CNC-001" },
  { name: "2호기 CNC 선반" ,equId: "CNC-002" },
  { name: "1호기 자동 에칭조" ,equId: "ETC-001" },
  { name: "3차원 비전 검사기" ,equId: "INS-001" }
];