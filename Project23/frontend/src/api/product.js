import request from "@/utils/request";

export function getProductList(params) {
  return request({
    url: "/product/list",
    method: "get",
    params,
  });
}

export function getProductDetail(id) {
  return request({
    url: `/product/detail/${id}`,
    method: "get",
  });
}

export function publishProduct(data) {
  return request({
    url: "/product/publish",
    method: "post",
    data,
  });
}

export function updateProduct(id, data) {
  return request({
    url: `/product/update/${id}`,
    method: "put",
    data,
  });
}

export function takeDownProduct(id) {
  return request({
    url: `/product/take-down/${id}`,
    method: "post",
  });
}

export function getMyProducts(params) {
  return request({
    url: "/product/my",
    method: "get",
    params,
  });
}

export function checkFavorite(id) {
  return request({
    url: `/product/check-favorite/${id}`,
    method: "get",
  });
}

export function getPendingProducts(params) {
  return request({
    url: "/admin/pending-products",
    method: "get",
    params,
  });
}

export function auditProduct(id, data) {
  return request({
    url: `/admin/audit-product/${id}`,
    method: "post",
    data,
  });
}
