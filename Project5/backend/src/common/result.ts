export enum StatusCode {
  SUCCESS = 0,
  ERROR = -1,
  UNAUTHORIZED = 401,
  FORBIDDEN = 403,
  NOT_FOUND = 404,
  INTERNAL_SERVER_ERROR = 500,
}

export interface ApiResponse<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;
}

export class Result<T = any> {
  code: number;
  message: string;
  data: T;
  timestamp: number;

  constructor(code: number, message: string, data: T) {
    this.code = code;
    this.message = message;
    this.data = data;
    this.timestamp = Date.now();
  }

  static success<T>(data?: T, message = "操作成功"): Result<T> {
    return new Result(StatusCode.SUCCESS, message, data);
  }

  static error(message = "操作失败", code = StatusCode.ERROR): Result<null> {
    return new Result(code, message, null);
  }

  static unauthorized(message = "未授权访问"): Result<null> {
    return new Result(StatusCode.UNAUTHORIZED, message, null);
  }

  static forbidden(message = "权限不足"): Result<null> {
    return new Result(StatusCode.FORBIDDEN, message, null);
  }
}
