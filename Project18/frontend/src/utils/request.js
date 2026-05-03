import axios from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { useUserStore } from "@/stores/user";

const service = axios.create({
  baseURL: "/api",
  timeout: 30000,
  headers: {
    "Content-Type": "application/json",
  },
});

service.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem("token");
    if (token) {
      config.headers["Authorization"] = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("Request error:", error);
    return Promise.reject(error);
  },
);

service.interceptors.response.use(
  (response) => {
    const res = response.data;
    if (res.code !== 200 && res.code !== 0) {
      ElMessage({
        message: res.message || "请求失败",
        type: "error",
        duration: 3000,
      });

      if (res.code === 401) {
        ElMessageBox.confirm("登录状态已过期，请重新登录", "提示", {
          confirmButtonText: "重新登录",
          cancelButtonText: "取消",
          type: "warning",
        }).then(() => {
          const userStore = useUserStore();
          userStore.logout();
          location.reload();
        });
      }
      return Promise.reject(new Error(res.message || "请求失败"));
    } else {
      return res;
    }
  },
  (error) => {
    console.error("Response error:", error);
    let message = "网络错误";
    if (error.response) {
      switch (error.response.status) {
        case 400:
          message = "请求参数错误";
          break;
        case 401:
          message = "未授权，请登录";
          const userStore = useUserStore();
          userStore.logout();
          break;
        case 403:
          message = "拒绝访问";
          break;
        case 404:
          message = "请求地址不存在";
          break;
        case 500:
          message = "服务器内部错误";
          break;
        default:
          message =
            error.response.data?.message || `连接错误${error.response.status}`;
      }
    } else if (error.message.includes("timeout")) {
      message = "请求超时";
    }
    ElMessage({
      message: message,
      type: "error",
      duration: 3000,
    });
    return Promise.reject(error);
  },
);

export default service;
