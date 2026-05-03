import request from "@/utils/request";

export function uploadImage(file) {
  const formData = new FormData();
  formData.append("file", file);

  return request({
    url: "/file/upload",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function uploadMultipleImages(files) {
  const formData = new FormData();
  files.forEach((file) => {
    formData.append("files", file);
  });

  return request({
    url: "/file/upload-multiple",
    method: "post",
    data: formData,
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function deleteFile(url) {
  return request({
    url: "/file/delete",
    method: "post",
    data: { url },
  });
}
