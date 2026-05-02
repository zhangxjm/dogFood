import axios, {
  type AxiosInstance,
  type AxiosRequestConfig,
  type AxiosResponse,
  type InternalAxiosRequestConfig,
} from "axios";
import { ElMessage, ElMessageBox } from "element-plus";
import { getToken, removeToken } from "./auth";
import router from "@/router";

const service: AxiosInstance = axios.create({
  baseURL: "/api",
  timeout: 30000,
  headers: {
    "Content-Type": "application/json;charset=utf-8",
  },
});

service.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    const token = getToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    console.error("Request error:", error);
    return Promise.reject(error);
  },
);

service.interceptors.response.use(
  (response: AxiosResponse) => {
    const res = response.data;

    if (res.code !== 0) {
      if (res.code === 401) {
        ElMessageBox.confirm("登录状态已过期，请重新登录", "提示", {
          confirmButtonText: "重新登录",
          cancelButtonText: "取消",
          type: "warning",
        }).then(() => {
          removeToken();
          router.push("/login");
        });
        return Promise.reject(new Error(res.message || "未授权"));
      }

      if (res.code === 403) {
        ElMessage.error("权限不足");
        return Promise.reject(new Error(res.message || "权限不足"));
      }

      ElMessage.error(res.message || "请求失败");
      return Promise.reject(new Error(res.message || "请求失败"));
    }

    return res;
  },
  (error) => {
    console.error("Response error:", error);

    if (error.response) {
      const { status } = error.response;

      switch (status) {
        case 401:
          ElMessageBox.confirm("登录状态已过期，请重新登录", "提示", {
            confirmButtonText: "重新登录",
            cancelButtonText: "取消",
            type: "warning",
          }).then(() => {
            removeToken();
            router.push("/login");
          });
          break;
        case 403:
          ElMessage.error("权限不足");
          break;
        case 404:
          ElMessage.error("请求的资源不存在");
          break;
        case 500:
          ElMessage.error("服务器内部错误");
          break;
        default:
          ElMessage.error(error.message || "请求失败");
      }
    } else if (error.code === "ECONNABORTED") {
      ElMessage.error("请求超时");
    } else {
      ElMessage.error("网络连接失败");
    }

    return Promise.reject(error);
  },
);

export interface RequestOptions extends AxiosRequestConfig {}

export function request<T = any>(config: RequestOptions): Promise<T> {
  return service.request(config);
}

export function get<T = any>(url: string, config?: RequestOptions): Promise<T> {
  return service.get(url, config);
}

export function post<T = any>(
  url: string,
  data?: any,
  config?: RequestOptions,
): Promise<T> {
  return service.post(url, data, config);
}

export function put<T = any>(
  url: string,
  data?: any,
  config?: RequestOptions,
): Promise<T> {
  return service.put(url, data, config);
}

export function del<T = any>(url: string, config?: RequestOptions): Promise<T> {
  return service.delete(url, config);
}

export default service;
