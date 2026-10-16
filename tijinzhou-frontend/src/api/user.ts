import { get, post } from "@/libs/request";
import type { LoginUser, PageData } from "@/types/api";

export type UserQuery = {
  current?: number;
  pageSize?: number;
  userAccount?: string;
  userName?: string;
};

export type UserForm = {
  id?: string;
  userAccount?: string;
  userName?: string;
  userAvatar?: string;
  userProfile?: string;
  userRole?: string;
  userStatus?: number;
};

export function register(body: {
  userAccount: string;
  userPassword: string;
  checkPassword: string;
}) {
  return post<LoginUser>("/api/app_user/register", body);
}

export function login(body: { userAccount: string; userPassword: string }) {
  return post<LoginUser>("/api/app_user/login", body);
}

export function logout() {
  return post<boolean>("/api/app_user/logout");
}

export function getLoginUser() {
  return get<LoginUser>("/api/app_user/get/login");
}

export function listUserByPage(body: UserQuery) {
  return post<PageData<LoginUser>>("/api/app_user/list/page", body);
}

export function addUser(body: UserForm) {
  return post<string>("/api/app_user/add", body);
}

export function updateUser(body: UserForm) {
  return post<boolean>("/api/app_user/update", body);
}

export function deleteUser(id: string) {
  return post<boolean>("/api/app_user/delete", { id });
}
