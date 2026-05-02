const jwt = require("jsonwebtoken");

// JWT认证中间件
const auth = async (req, res, next) => {
  try {
    // 从请求头获取token
    let token = req.header("Authorization");

    if (!token) {
      return res.status(401).json({
        success: false,
        message: "请先登录，无访问权限",
      });
    }

    // 移除 Bearer 前缀
    if (token.startsWith("Bearer ")) {
      token = token.slice(7, token.length).trimLeft();
    }

    // 验证token
    const decoded = jwt.verify(
      token,
      process.env.JWT_SECRET ||
        "your-super-secret-jwt-key-change-in-production",
    );

    // 将用户信息添加到请求对象
    req.user = decoded;

    next();
  } catch (error) {
    console.error("JWT验证错误:", error);

    // 区分不同类型的token错误
    if (error.name === "TokenExpiredError") {
      return res.status(401).json({
        success: false,
        message: "登录已过期，请重新登录",
      });
    }

    if (error.name === "JsonWebTokenError") {
      return res.status(401).json({
        success: false,
        message: "无效的token，请重新登录",
      });
    }

    return res.status(500).json({
      success: false,
      message: "服务器错误",
    });
  }
};

// 可选认证中间件（用于不需要强制登录但需要识别用户的接口）
const optionalAuth = async (req, res, next) => {
  try {
    let token = req.header("Authorization");

    if (!token) {
      req.user = null;
      return next();
    }

    if (token.startsWith("Bearer ")) {
      token = token.slice(7, token.length).trimLeft();
    }

    const decoded = jwt.verify(
      token,
      process.env.JWT_SECRET ||
        "your-super-secret-jwt-key-change-in-production",
    );
    req.user = decoded;

    next();
  } catch (error) {
    req.user = null;
    next();
  }
};

module.exports = { auth, optionalAuth };
