import request from "@/utils/request";

export function login(username, password) {
  const formData = new FormData();
  formData.append("username", username);
  formData.append("password", password);
  return request.post("/auth/login", formData, {
    headers: {
      "Content-Type": "multipart/form-data",
    },
  });
}

export function getCurrentUser() {
  return request.get("/auth/me");
}
