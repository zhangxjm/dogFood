import { API_BASE_URL, REQUEST_TIMEOUT } from "../config/api";

// 基础请求配置
const defaultConfig = {
  headers: {
    "Content-Type": "application/json",
    Accept: "application/json",
  },
  timeout: REQUEST_TIMEOUT,
};

// 创建中止控制器
const createAbortController = (timeout) => {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), timeout);
  return { controller, timeoutId };
};

// API客户端
const apiClient = {
  // GET请求
  get: async (url, config = {}) => {
    return request(url, { ...config, method: "GET" });
  },

  // POST请求
  post: async (url, data, config = {}) => {
    return request(url, {
      ...config,
      method: "POST",
      body: JSON.stringify(data),
    });
  },

  // PUT请求
  put: async (url, data, config = {}) => {
    return request(url, {
      ...config,
      method: "PUT",
      body: JSON.stringify(data),
    });
  },

  // PATCH请求
  patch: async (url, data, config = {}) => {
    return request(url, {
      ...config,
      method: "PATCH",
      body: JSON.stringify(data),
    });
  },

  // DELETE请求
  delete: async (url, config = {}) => {
    return request(url, { ...config, method: "DELETE" });
  },

  // 上传文件
  upload: async (url, formData, config = {}) => {
    const uploadConfig = {
      ...config,
      method: "POST",
      headers: {
        ...config.headers,
        "Content-Type": "multipart/form-data",
      },
      body: formData,
    };

    return request(url, uploadConfig);
  },
};

// 统一请求处理函数
const request = async (url, config) => {
  const { controller, timeoutId } = createAbortController(
    config.timeout || REQUEST_TIMEOUT,
  );

  try {
    // 合并配置
    const requestConfig = {
      ...defaultConfig,
      ...config,
      headers: {
        ...defaultConfig.headers,
        ...config.headers,
      },
      signal: controller.signal,
    };

    // 发送请求
    console.log(`[API] ${config.method} ${url}`);
    const response = await fetch(url, requestConfig);

    // 清除超时定时器
    clearTimeout(timeoutId);

    // 处理响应
    const responseData = await handleResponse(response);

    return responseData;
  } catch (error) {
    clearTimeout(timeoutId);
    return handleError(error);
  }
};

// 处理响应
const handleResponse = async (response) => {
  let data;

  try {
    // 尝试解析JSON
    data = await response.json();
  } catch (error) {
    // 如果解析失败，尝试解析文本
    const text = await response.text();
    data = { message: text };
  }

  // 检查响应状态
  if (response.ok) {
    return {
      data,
      status: response.status,
      statusText: response.statusText,
      headers: response.headers,
    };
  } else {
    // 抛出错误
    const error = new Error(data.message || response.statusText);
    error.status = response.status;
    error.data = data;
    error.response = response;
    throw error;
  }
};

// 处理错误
const handleError = (error) => {
  // 网络错误
  if (error.message === "Network request failed") {
    return {
      error: true,
      message: "网络连接失败，请检查网络",
      status: 0,
    };
  }

  // 请求超时
  if (error.name === "AbortError") {
    return {
      error: true,
      message: "请求超时，请稍后重试",
      status: 408,
    };
  }

  // 其他错误
  return {
    error: true,
    message: error.message || "发生未知错误",
    status: error.status || 500,
    data: error.data,
  };
};

// 导出
export default apiClient;
