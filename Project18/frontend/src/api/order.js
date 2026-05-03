import request from "@/utils/request";

const orderApi = {
  createOrder: (data) => {
    return request({
      url: "/order/create",
      method: "post",
      data,
    });
  },

  getList: (params) => {
    return request({
      url: "/order/list",
      method: "get",
      params,
    });
  },

  getDetail: (id) => {
    return request({
      url: `/order/${id}`,
      method: "get",
    });
  },

  pay: (id, payType) => {
    return request({
      url: `/order/pay/${id}`,
      method: "post",
      params: { payType },
    });
  },

  cancel: (id, reason) => {
    return request({
      url: `/order/cancel/${id}`,
      method: "post",
      params: { reason },
    });
  },

  confirm: (id) => {
    return request({
      url: `/order/confirm/${id}`,
      method: "post",
    });
  },

  getPage: (params) => {
    return request({
      url: "/admin/order/page",
      method: "get",
      params,
    });
  },

  ship: (id, data) => {
    return request({
      url: `/admin/order/ship/${id}`,
      method: "post",
      data,
    });
  },
};

export default orderApi;
