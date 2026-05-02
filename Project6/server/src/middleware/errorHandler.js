// 统一错误处理中间件
const errorHandler = (err, req, res, next) => {
  console.error("❌ 错误:", err);

  // 默认错误信息
  let statusCode = 500;
  let message = "服务器内部错误";
  let errors = [];

  // 处理不同类型的错误
  switch (err.name) {
    case "ValidationError":
      statusCode = 400;
      message = "数据验证失败";
      errors = Object.values(err.errors).map((error) => error.message);
      break;

    case "CastError":
      statusCode = 400;
      message = `无效的ID格式: ${err.value}`;
      break;

    case "MongoServerError":
      // MongoDB重复键错误
      if (err.code === 11000) {
        statusCode = 400;
        const field = Object.keys(err.keyValue)[0];
        message = `${field}已存在，请使用其他值`;
      }
      break;

    case "JsonWebTokenError":
      statusCode = 401;
      message = "无效的token";
      break;

    case "TokenExpiredError":
      statusCode = 401;
      message = "token已过期";
      break;

    case "NotBeforeError":
      statusCode = 401;
      message = "token尚未生效";
      break;
  }

  // 如果错误有statusCode，使用它
  if (err.statusCode) {
    statusCode = err.statusCode;
  }

  // 如果错误有message，使用它
  if (err.message) {
    message = err.message;
  }

  // 开发环境返回详细错误
  if (process.env.NODE_ENV === "development") {
    return res.status(statusCode).json({
      success: false,
      message,
      errors,
      stack: err.stack,
    });
  }

  // 生产环境返回简洁错误
  res.status(statusCode).json({
    success: false,
    message,
    errors,
  });
};

module.exports = errorHandler;
