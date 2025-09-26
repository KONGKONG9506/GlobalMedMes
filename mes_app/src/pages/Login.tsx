import { useState } from "react";
import { api } from "../lib/api";
import { useAuthStore } from "../store/auth";
import { useNavigate, useLocation } from "react-router-dom";
import { isAxiosError } from "axios";
import bgImage from "../images/Teeth.jpg";

type LoginRes = { token: string; user: { userId: string } };

export default function Login() {
  const [username, setU] = useState("");
  const [password, setP] = useState("");
  const setAuth = useAuthStore((s) => s.setAuth);
  const nav = useNavigate();
  const loc = useLocation();
  const from = (loc.state as { from?: string } | null)?.from ?? "/";

  const submit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const { data } = await api.post<LoginRes>("/auth/login", { username, password });
      setAuth(data.token, data.user.userId);
      nav(from, { replace: true });
    } catch (err: unknown) {
      const msg = isAxiosError<{ message?: string }>(err)
        ? err.response?.data?.message ?? "로그인 실패"
        : "로그인 실패";
      alert(msg);
    }
  };

  return (
    <div className="min-h-screen flex items-center justify-center bg-gray-50">
      {/* 로그인 카드 */}
      <div className="flex w-[1100px] h-[550px] rounded-lg shadow-lg bg-white overflow-hidden">
        {/* 왼쪽 이미지 */}
        <div className="w-[65%] bg-gray-100 flex items-center justify-center">
          <img
            src={bgImage}
            alt="Teeth"
            className="w-full h-full object-cover"
          />
        </div>

        {/* 오른쪽 로그인 폼 */}
        <div className="w-[35%] flex items-center justify-center p-8">
          <form onSubmit={submit} className="w-full space-y-4">
            <div className="text-2xl font-bold text-center">로그인</div>
            <input
              className="w-full border px-3 py-2 rounded"
              placeholder="아이디"
              value={username}
              onChange={(e) => setU(e.target.value)}
            />
            <input
              className="w-full border px-3 py-2 rounded"
              type="password"
              placeholder="비밀번호"
              value={password}
              onChange={(e) => setP(e.target.value)}
            />
            <button className="w-full bg-black text-white py-2 rounded hover:bg-gray-800 transition">
              로그인
            </button>
          </form>
        </div>
      </div>
    </div>
  );
}