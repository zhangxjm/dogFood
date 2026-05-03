import request from "@/utils/request";

const userApi = {
  login: (data) => {
    return request({
      url: "/user/login",
      method: "post",
      data,
    });
  },

  register: (data) => {
    return request({
      url: "/user/register",
      method: "post",
      data,
    });
  },

  logout: () => {
    return request({
      url: "/user/logout",
      method: "post",
    });
  },

  getInfo: () => {
    return request({
      url: "/user/info",
      method: "get",
    });
  },

  updateInfo: (data) => {
    return request({
      url: "/user/update",
      method: "put",
      data,
    });
  },

  updatePassword: (data) => {
    return request({
      url: "/user/password",
      method: "put",
      data,
    });
  },

  adminLogin: (data) => {
    return request({
      url: "/admin/login",
      method: "post",
      data,
    });
  },

  getPage: (params) => {
    return request({
      url: "/admin/user/page",
      method: "get",
      params,
    });
  },

  updateStatus: (id, status) => {
    return request({
      url: `/admin/user/status/${id}`,
      method: "put",
      params: { status },
    });
  },

  resetPassword: (id) => {
    return request({
      url: `/admin/user/reset-password/${id}`,
      method: "post",
    });
  },
};

export default userApi;
