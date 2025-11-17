import { useMutation } from "@tanstack/react-query";
import { loginAPI } from "../api/loginAPI";
import { authStore } from "../store/authStore";
import { useNavigate } from "react-router-dom";

export const useLogin = () => {
  const navigate = useNavigate();
  const { setLogin } = authStore();

  return useMutation({
    mutationFn: async ({ mode, credentials }) => {
      // buyer/seller/admin 모드별 로그인 요청
      const payload = {
        loginId: credentials.username,
        password: credentials.password,
      };
      return await loginAPI.postLogin(mode, payload);
    },

    onSuccess: async (data, variables) => 
        {
      // JWT 토큰 저장
      const token = data.content.token; 
      console.log("로그인 성공, 토큰:", token);

      // Zustand에 토큰만 저장
      //랜더링 최소화 위해서 user 부분 null처리
      setLogin(null, token);

      try {
        // 유저 정보 요청
        const user = await loginAPI.getUserInfo(variables.mode);
        console.log("사용자 정보:", user);

        // Zustand에 user + token 저장
        setLogin(user, token);

        console.log("로그인 성공!");
        if (variables.mode === "admin") navigate("/");
        else if (variables.mode === "seller") navigate("/");
        else navigate(-1);

      } catch (err) {
        console.error("유저 정보 불러오기 실패:", err);
        alert("로그인은 성공했지만 사용자 정보를 불러오지 못했습니다.");
      }
    },

    onError: (error) => {
        console.error("로그인 실패:", error);
        alert(`로그인 실패: ${error.content || "서버 오류"}`);
    },
  });
};
