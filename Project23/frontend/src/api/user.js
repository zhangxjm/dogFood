import request from "@/utils/request";

export function login(data) {
  return request({
    url: "/user/login",
    method: "post",
    data,
  });
}

export function register(data) {
  return request({
    url: "/user/register",
    method: "post",
    data,
  });
}

export function getUserInfo() {
  return request({
    url: "/user/info",
    method: "get",
  });
}

export function updateUser(data) {
  return request({
    url: "/user/update",
    method: "put",
    data,
  });
}

export function updatePassword(data) {
  return request({
    url: "/user/update-password",
    method: "post",
    data,
  });
}

export function getAllUsers() {
  return request({
    url: "/admin/users",
    method: "get",
  });
}

export function updateUserStatus(id, data) {
  return request({
    url: `/admin/update-user-status/${id}`,
    method: "post",
    data,
  });
}

export function deleteUser(id) {
  return request({
    url: `/admin/delete-user/${id}`,
    method: "delete",
  });
}
