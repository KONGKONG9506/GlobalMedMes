type Option = { name: string; Id: string;};

type Props = {
  Id: string;        
  options: Option[];
  onChange: (v: string) => void;
};

export default function ProcessingSelect({ Id, options, onChange }: Props) {
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

export const processOptions: Option[] = [
  { name: "CNC 가공 공정" ,Id: "P-100" },
  { name: "블라스팅 공정",Id: "P-200" },
  { name: "에칭 공정" ,Id: "P-300" },
  { name: "세척 및 건조 공정" ,Id: "P-400" },
  { name: "포장 및 멸균 공정" ,Id: "P-500" },
  { name: "원자재 검사" ,Id: "Q-100" },
  { name: "최종 형상 검사",Id: "Q-200" },
  { name: "자재 입고" ,Id: "W-100" },
  { name: "완제품 출하" ,Id: "W-200" }
];