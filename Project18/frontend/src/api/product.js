import request from "@/utils/request";

const productApi = {
  getList: (params) => {
    return request({
      url: "/product/list",
      method: "get",
      params,
    });
  },

  getDetail: (id) => {
    return request({
      url: `/product/${id}`,
      method: "get",
    });
  },

  getCategoryList: () => {
    return request({
      url: "/product/category/list",
      method: "get",
    });
  },

  getPage: (params) => {
    return request({
      url: "/admin/product/page",
      method: "get",
      params,
    });
  },

  add: (data) => {
    return request({
      url: "/admin/product",
      method: "post",
      data,
    });
  },

  update: (data) => {
    return request({
      url: "/admin/product",
      method: "put",
      data,
    });
  },

  delete: (id) => {
    return request({
      url: `/admin/product/${id}`,
      method: "delete",
    });
  },
};

export default productApi;
