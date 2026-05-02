import request from "@/utils/request";
import type { Role, PageResult } from "@/types";

export interface RoleListParams {
  page?: number;
  pageSize?: number;
  name?: string;
  code?: string;
  status?: number;
}

export interface CreateRoleParams {
  name: string;
  code: string;
  description?: string;
  status?: number;
  sort?: number;
  permissionIds?: number[];
}

export interface UpdateRoleParams {
  id: number;
  name?: string;
  description?: string;
  status?: number;
  sort?: number;
  permissionIds?: number[];
}

export const getRoleListApi = (params: RoleListParams) => {
  return request.get<PageResult<Role>>("/role/list", { params });
};

export const getRoleDetailApi = (id: number) => {
  return request.get<Role>(`/role/${id}`);
};

export const createRoleApi = (data: CreateRoleParams) => {
  return request.post("/role", data);
};

export const updateRoleApi = (data: UpdateRoleParams) => {
  return request.put("/role", data);
};

export const deleteRoleApi = (id: number) => {
  return request.del(`/role/${id}`);
};

export const batchDeleteRoleApi = (ids: number[]) => {
  return request.post("/role/batch-delete", { ids });
};

export const updateRoleStatusApi = (id: number, status: number) => {
  return request.put(`/role/status/${id}`, { status });
};

export const assignPermissionsApi = (
  roleId: number,
  permissionIds: number[],
) => {
  return request.put(`/role/assign-permissions/${roleId}`, { permissionIds });
};

export const getAllRolesApi = () => {
  return request.get<Role[]>("/role/all");
};
