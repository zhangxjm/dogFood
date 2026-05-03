import request from "@/utils/request";

export function getCategoryList() {
  return request({
    url: "/category/list",
    method: "get",
  });
}

export function getAllCategories() {
  return request({
    url: "/category/all",
    method: "get",
  });
}

export function addCategory(data) {
  return request({
    url: "/category/add",
    method: "post",
    data,
  });
}

export function updateCategory(id, data) {
  return request({
    url: `/category/update/${id}`,
    method: "put",
    data,
  });
}

export function deleteCategory(id) {
  return request({
    url: `/category/delete/${id}`,
    method: "delete",
  });
}
