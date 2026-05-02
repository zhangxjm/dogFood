import request from "@/utils/request";
import type { Permission } from "@/types";

export interface PermissionListParams {
  name?: string;
  status?: number;
}

export interface CreatePermissionParams {
  name: string;
  code: string;
  type: number;
  parentId: number;
  path?: string;
  component?: string;
  icon?: string;
  sort?: number;
  status?: number;
}

export interface UpdatePermissionParams {
  id: number;
  name?: string;
  type?: number;
  parentId?: number;
  path?: string;
  component?: string;
  icon?: string;
  sort?: number;
  status?: number;
}

export const getPermissionTreeApi = () => {
  return request.get<Permission[]>("/permission/tree");
};

export const getPermissionListApi = (params?: PermissionListParams) => {
  return request.get<Permission[]>("/permission/list", { params });
};

export const getPermissionDetailApi = (id: number) => {
  return request.get<Permission>(`/permission/${id}`);
};

export const createPermissionApi = (data: CreatePermissionParams) => {
  return request.post("/permission", data);
};

export const updatePermissionApi = (data: UpdatePermissionParams) => {
  return request.put("/permission", data);
};

export const deletePermissionApi = (id: number) => {
  return request.del(`/permission/${id}`);
};

export const updatePermissionStatusApi = (id: number, status: number) => {
  return request.put(`/permission/status/${id}`, { status });
};

export const getPermissionsByRoleIdApi = (roleId: number) => {
  return request.get<number[]>(`/permission/by-role/${roleId}`);
};
