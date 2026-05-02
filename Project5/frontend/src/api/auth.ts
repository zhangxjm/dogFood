import request from "@/utils/request";
import type { LoginParams, LoginResult, UserInfo } from "@/types";

export const loginApi = (data: LoginParams) => {
  return request.post<LoginResult>("/auth/login", data);
};

export const logoutApi = () => {
  return request.post("/auth/logout");
};

export const getUserInfoApi = () => {
  return request.get<UserInfo>("/auth/userinfo");
};

export const getPermissionsApi = () => {
  return request.get<string[]>("/auth/permissions");
};

export const getMenusApi = () => {
  return request.get<any[]>("/auth/menus");
};
