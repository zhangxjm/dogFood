import request from "@/utils/request";

const seckillApi = {
  getList: (params) => {
    return request({
      url: "/seckill/list",
      method: "get",
      params,
    });
  },

  getDetail: (id) => {
    return request({
      url: `/seckill/${id}`,
      method: "get",
    });
  },

  getStock: (id) => {
    return request({
      url: `/seckill/stock/${id}`,
      method: "get",
    });
  },

  execute: (data) => {
    return request({
      url: "/seckill/execute",
      method: "post",
      data,
    });
  },

  getResult: (activityId) => {
    return request({
      url: `/seckill/result/${activityId}`,
      method: "get",
    });
  },

  getPage: (params) => {
    return request({
      url: "/admin/seckill/page",
      method: "get",
      params,
    });
  },

  add: (data) => {
    return request({
      url: "/admin/seckill",
      method: "post",
      data,
    });
  },

  update: (data) => {
    return request({
      url: "/admin/seckill",
      method: "put",
      data,
    });
  },

  delete: (id) => {
    return request({
      url: `/admin/seckill/${id}`,
      method: "delete",
    });
  },

  start: (id) => {
    return request({
      url: `/admin/seckill/start/${id}`,
      method: "post",
    });
  },

  stop: (id) => {
    return request({
      url: `/admin/seckill/stop/${id}`,
      method: "post",
    });
  },
};

export default seckillApi;
