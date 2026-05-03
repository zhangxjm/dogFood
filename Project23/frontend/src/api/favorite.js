import request from "@/utils/request";

export function addFavorite(productId) {
  return request({
    url: `/favorite/add/${productId}`,
    method: "post",
  });
}

export function removeFavorite(productId) {
  return request({
    url: `/favorite/remove/${productId}`,
    method: "post",
  });
}

export function getMyFavorites() {
  return request({
    url: "/favorite/my",
    method: "get",
  });
}

export function checkFavorite(productId) {
  return request({
    url: `/favorite/check/${productId}`,
    method: "get",
  });
}
