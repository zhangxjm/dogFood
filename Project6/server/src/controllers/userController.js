const User = require("../models/User");
const jwt = require("jsonwebtoken");
const { validationResult } = require("express-validator");

// 生成JWT token
const generateToken = (user) => {
  return jwt.sign(
    {
      id: user._id,
      username: user.username,
      email: user.email,
    },
    process.env.JWT_SECRET || "your-super-secret-jwt-key-change-in-production",
    { expiresIn: process.env.JWT_EXPIRE || "7d" },
  );
};

// 用户注册
const register = async (req, res, next) => {
  try {
    // 验证输入
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: "数据验证失败",
        errors: errors.array(),
      });
    }

    const { username, email, password } = req.body;

    // 检查用户名是否已存在
    const existingUsername = await User.findOne({ username });
    if (existingUsername) {
      return res.status(400).json({
        success: false,
        message: "用户名已存在",
      });
    }

    // 检查邮箱是否已存在
    const existingEmail = await User.findOne({ email });
    if (existingEmail) {
      return res.status(400).json({
        success: false,
        message: "邮箱已存在",
      });
    }

    // 创建新用户
    const user = await User.create({
      username,
      email,
      password,
    });

    // 生成token
    const token = generateToken(user);

    // 不返回密码
    const userWithoutPassword = {
      _id: user._id,
      username: user.username,
      email: user.email,
      avatar: user.avatar,
      nickname: user.nickname,
      bio: user.bio,
      createdAt: user.createdAt,
    };

    res.status(201).json({
      success: true,
      message: "注册成功",
      data: {
        token,
        user: userWithoutPassword,
      },
    });
  } catch (error) {
    next(error);
  }
};

// 用户登录
const login = async (req, res, next) => {
  try {
    // 验证输入
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: "数据验证失败",
        errors: errors.array(),
      });
    }

    const { email, password } = req.body;

    // 查找用户
    const user = await User.findOne({ email }).select("+password");

    if (!user) {
      return res.status(401).json({
        success: false,
        message: "邮箱或密码错误",
      });
    }

    // 检查账户是否激活
    if (!user.isActive) {
      return res.status(401).json({
        success: false,
        message: "账户已被禁用，请联系管理员",
      });
    }

    // 验证密码
    const isMatch = await user.comparePassword(password);
    if (!isMatch) {
      return res.status(401).json({
        success: false,
        message: "邮箱或密码错误",
      });
    }

    // 生成token
    const token = generateToken(user);

    // 不返回密码
    const userWithoutPassword = {
      _id: user._id,
      username: user.username,
      email: user.email,
      avatar: user.avatar,
      nickname: user.nickname,
      bio: user.bio,
      createdAt: user.createdAt,
    };

    res.status(200).json({
      success: true,
      message: "登录成功",
      data: {
        token,
        user: userWithoutPassword,
      },
    });
  } catch (error) {
    next(error);
  }
};

// 获取当前用户信息
const getCurrentUser = async (req, res, next) => {
  try {
    const user = await User.findById(req.user.id);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "用户不存在",
      });
    }

    const userInfo = {
      _id: user._id,
      username: user.username,
      email: user.email,
      avatar: user.avatar,
      nickname: user.nickname,
      phone: user.phone,
      bio: user.bio,
      displayName: user.displayName,
      createdAt: user.createdAt,
      updatedAt: user.updatedAt,
    };

    res.status(200).json({
      success: true,
      data: userInfo,
    });
  } catch (error) {
    next(error);
  }
};

// 更新用户信息
const updateUser = async (req, res, next) => {
  try {
    const { nickname, phone, bio, avatar } = req.body;

    const user = await User.findById(req.user.id);

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "用户不存在",
      });
    }

    // 更新允许修改的字段
    if (nickname !== undefined) user.nickname = nickname;
    if (phone !== undefined) user.phone = phone;
    if (bio !== undefined) user.bio = bio;
    if (avatar !== undefined) user.avatar = avatar;

    const updatedUser = await user.save();

    const userInfo = {
      _id: updatedUser._id,
      username: updatedUser.username,
      email: updatedUser.email,
      avatar: updatedUser.avatar,
      nickname: updatedUser.nickname,
      phone: updatedUser.phone,
      bio: updatedUser.bio,
      displayName: updatedUser.displayName,
      createdAt: updatedUser.createdAt,
      updatedAt: updatedUser.updatedAt,
    };

    res.status(200).json({
      success: true,
      message: "更新成功",
      data: userInfo,
    });
  } catch (error) {
    next(error);
  }
};

// 修改密码
const changePassword = async (req, res, next) => {
  try {
    const errors = validationResult(req);
    if (!errors.isEmpty()) {
      return res.status(400).json({
        success: false,
        message: "数据验证失败",
        errors: errors.array(),
      });
    }

    const { oldPassword, newPassword } = req.body;

    const user = await User.findById(req.user.id).select("+password");

    if (!user) {
      return res.status(404).json({
        success: false,
        message: "用户不存在",
      });
    }

    // 验证旧密码
    const isMatch = await user.comparePassword(oldPassword);
    if (!isMatch) {
      return res.status(400).json({
        success: false,
        message: "旧密码错误",
      });
    }

    // 更新密码
    user.password = newPassword;
    await user.save();

    res.status(200).json({
      success: true,
      message: "密码修改成功",
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  register,
  login,
  getCurrentUser,
  updateUser,
  changePassword,
};
