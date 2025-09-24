type Option = { name: string; Id: string;};

type Props = {
  Id: string;        
  options: Option[];
  onChange: (v: string) => void;
};

export default function NamesSelect({ Id, options, onChange }: Props) {
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

export const nameOptions: Option[] = [
  { name: "Fixture Semi 4.0x10" ,Id: "SM-FIX-4010-001" },
  { name: "티타늄 Bar Grade5",Id: "RM-TI-G5-001" },
  { name: "SLA 표면처리용액" ,Id: "RM-ACD-SLA-001" },
  { name: "임플란트 Fixture S-Type 4.5x12" ,Id: "FG-FIX-4512S-001" },
  { name: "임플란트 Fixture S-Type 4.0x10" ,Id: "FG-FIX-4010S-001" }
];