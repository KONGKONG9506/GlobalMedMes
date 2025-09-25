export type MenuItem = {
  key: string;
  title: string;
  path: string;
  perms: { read: boolean; write: boolean; exec: boolean };
  // 아이콘 속성을 추가하지 않습니다.
  children: MenuItem[];
};