import request from "@/utils/request";
import type { User, PageResult } from "@/types";

export interface UserListParams {
  page?: number;
  pageSize?: number;
  username?: string;
  status?: number;
}

export interface CreateUserParams {
  username: string;
  password: string;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  status?: number;
  roleIds?: number[];
}

export interface UpdateUserParams {
  id: number;
  nickname?: string;
  email?: string;
  phone?: string;
  avatar?: string;
  status?: number;
  roleIds?: number[];
}

export const getUserListApi = (params: UserListParams) => {
  return request.get<PageResult<User>>("/user/list", { params });
};

export const getUserDetailApi = (id: number) => {
  return request.get<User>(`/user/${id}`);
};

export const createUserApi = (data: CreateUserParams) => {
  return request.post("/user", data);
};

export const updateUserApi = (data: UpdateUserParams) => {
  return request.put("/user", data);
};

export const deleteUserApi = (id: number) => {
  return request.del(`/user/${id}`);
};

export const batchDeleteUserApi = (ids: number[]) => {
  return request.post("/user/batch-delete", { ids });
};

export const resetPasswordApi = (id: number, password: string) => {
  return request.put(`/user/reset-password/${id}`, { password });
};

export const updateStatusApi = (id: number, status: number) => {
  return request.put(`/user/status/${id}`, { status });
};
