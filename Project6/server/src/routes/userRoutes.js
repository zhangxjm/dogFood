const express = require("express");
const { body } = require("express-validator");
const userController = require("../controllers/userController");
const { auth } = require("../middleware/auth");

const router = express.Router();

// 用户注册
router.post(
  "/register",
  [
    body("username")
      .notEmpty()
      .withMessage("用户名不能为空")
      .isLength({ min: 2, max: 20 })
      .withMessage("用户名需在2-20个字符之间"),
    body("email")
      .notEmpty()
      .withMessage("邮箱不能为空")
      .isEmail()
      .withMessage("请输入有效的邮箱地址"),
    body("password")
      .notEmpty()
      .withMessage("密码不能为空")
      .isLength({ min: 6 })
      .withMessage("密码至少6个字符"),
  ],
  userController.register,
);

// 用户登录
router.post(
  "/login",
  [
    body("email")
      .notEmpty()
      .withMessage("邮箱不能为空")
      .isEmail()
      .withMessage("请输入有效的邮箱地址"),
    body("password").notEmpty().withMessage("密码不能为空"),
  ],
  userController.login,
);

// 获取当前用户信息（需要登录）
router.get("/profile", auth, userController.getCurrentUser);

// 更新用户信息（需要登录）
router.put("/profile", auth, userController.updateUser);

// 修改密码（需要登录）
router.put(
  "/password",
  auth,
  [
    body("oldPassword").notEmpty().withMessage("旧密码不能为空"),
    body("newPassword")
      .notEmpty()
      .withMessage("新密码不能为空")
      .isLength({ min: 6 })
      .withMessage("新密码至少6个字符"),
  ],
  userController.changePassword,
);

module.exports = router;
