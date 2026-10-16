import axios from "axios";
import type { BaseResponse } from "@/types/api";

const request = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_BASE ?? "http://localhost:8101",
  timeout: 10000,
  withCredentials: true,
});

request.interceptors.response.use(
  (response) => {
    const data = response.data as { code?: number; message?: string };
    const requestUrl = `${response.config.url ?? ""}${response.request?.responseURL ?? ""}`;
    if (data?.code === 40100 && typeof window !== "undefined") {
      const onLoginPage = window.location.pathname.includes("/user/login");
      const isLoginUserRequest = requestUrl.includes("app_user/get/login");
      if (!onLoginPage && !isLoginUserRequest) {
        const loginUrl = new URL("/user/login", window.location.origin);
        loginUrl.searchParams.set("redirect", window.location.href);
        window.location.assign(loginUrl.toString());
      }
    } else if (data?.code !== 0) {
      throw new Error(data?.message || "服务器错误");
    }
    return response;
  },
  (error) => Promise.reject(error),
);

export function post<T>(url: string, body?: unknown) {
  return request.post<BaseResponse<T>>(url, body).then((res) => res.data);
}

export function get<T>(url: string, params?: Record<string, string>) {
  return request.get<BaseResponse<T>>(url, { params }).then((res) => res.data);
}
