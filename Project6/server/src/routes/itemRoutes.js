const express = require("express");
const { body } = require("express-validator");
const itemController = require("../controllers/itemController");
const { auth, optionalAuth } = require("../middleware/auth");

const router = express.Router();

// 获取分类列表（公开）
router.get("/categories", itemController.getCategories);

// 获取项目列表（公开，支持分页和搜索）
router.get("/", optionalAuth, itemController.getItems);

// 获取我的项目列表（需要登录）
router.get("/my", auth, itemController.getMyItems);

// 获取单个项目详情（公开）
router.get("/:id", optionalAuth, itemController.getItemById);

// 创建新项目（需要登录）
router.post(
  "/",
  auth,
  [
    body("title")
      .notEmpty()
      .withMessage("标题不能为空")
      .isLength({ max: 100 })
      .withMessage("标题最多100个字符"),
    body("description")
      .notEmpty()
      .withMessage("描述不能为空")
      .isLength({ max: 1000 })
      .withMessage("描述最多1000个字符"),
    body("category").notEmpty().withMessage("分类不能为空"),
    body("price")
      .optional()
      .isNumeric()
      .withMessage("价格必须是数字")
      .isFloat({ min: 0 })
      .withMessage("价格不能为负数"),
  ],
  itemController.createItem,
);

// 更新项目（需要登录）
router.put("/:id", auth, itemController.updateItem);

// 删除项目（需要登录）
router.delete("/:id", auth, itemController.deleteItem);

module.exports = router;
