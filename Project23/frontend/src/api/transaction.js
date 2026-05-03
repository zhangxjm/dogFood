import request from "@/utils/request";

export function createTransaction(data) {
  return request({
    url: "/transaction/create",
    method: "post",
    data,
  });
}

export function getBuyerTransactions() {
  return request({
    url: "/transaction/buyer",
    method: "get",
  });
}

export function getSellerTransactions() {
  return request({
    url: "/transaction/seller",
    method: "get",
  });
}

export function updateTransactionStatus(id, data) {
  return request({
    url: `/transaction/update-status/${id}`,
    method: "post",
    data,
  });
}
