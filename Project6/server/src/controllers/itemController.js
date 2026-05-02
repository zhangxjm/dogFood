const Item = require("../models/Item");
const { validationResult } = require("express-validator");

// 获取列表（支持分页、搜索、分类筛选）
const getItems = async (req, res, next) => {
  try {
    // 分页参数
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const skip = (page - 1) * limit;

    // 搜索参数
    const search = req.query.search || "";
    const category = req.query.category || "";
    const status = req.query.status || "active";

    // 构建查询条件
    let query = { status };

    if (search) {
      query.$or = [
        { title: { $regex: search, $options: "i" } },
        { description: { $regex: search, $options: "i" } },
      ];
    }

    if (category) {
      query.category = category;
    }

    // 计算总数
    const total = await Item.countDocuments(query);

    // 查询数据并关联用户信息
    const items = await Item.find(query)
      .populate("user", "username avatar nickname")
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: {
        items,
        pagination: {
          page,
          limit,
          total,
          pages: Math.ceil(total / limit),
          hasNext: page < Math.ceil(total / limit),
          hasPrev: page > 1,
        },
      },
    });
  } catch (error) {
    next(error);
  }
};

// 获取单个详情
const getItemById = async (req, res, next) => {
  try {
    const item = await Item.findById(req.params.id).populate(
      "user",
      "username avatar nickname",
    );

    if (!item) {
      return res.status(404).json({
        success: false,
        message: "数据不存在",
      });
    }

    // 增加浏览量
    item.views += 1;
    await item.save();

    res.status(200).json({
      success: true,
      data: item,
    });
  } catch (error) {
    if (error.name === "CastError") {
      return res.status(400).json({
        success: false,
        message: "无效的ID格式",
      });
    }
    next(error);
  }
};

// 创建新项目
const createItem = async (req, res, next) => {
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

    const { title, description, image, category, price, tags } = req.body;

    const item = await Item.create({
      title,
      description,
      image,
      category,
      price,
      tags: tags || [],
      user: req.user.id,
    });

    // 关联用户信息返回
    const createdItem = await Item.findById(item._id).populate(
      "user",
      "username avatar nickname",
    );

    res.status(201).json({
      success: true,
      message: "创建成功",
      data: createdItem,
    });
  } catch (error) {
    next(error);
  }
};

// 更新项目
const updateItem = async (req, res, next) => {
  try {
    const { title, description, image, category, price, status, tags } =
      req.body;

    // 查找项目
    const item = await Item.findById(req.params.id);

    if (!item) {
      return res.status(404).json({
        success: false,
        message: "数据不存在",
      });
    }

    // 检查是否是创建者
    if (item.user.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: "无权限修改此数据",
      });
    }

    // 更新字段
    if (title !== undefined) item.title = title;
    if (description !== undefined) item.description = description;
    if (image !== undefined) item.image = image;
    if (category !== undefined) item.category = category;
    if (price !== undefined) item.price = price;
    if (status !== undefined) item.status = status;
    if (tags !== undefined) item.tags = tags;

    const updatedItem = await item.save();

    // 关联用户信息返回
    const resultItem = await Item.findById(updatedItem._id).populate(
      "user",
      "username avatar nickname",
    );

    res.status(200).json({
      success: true,
      message: "更新成功",
      data: resultItem,
    });
  } catch (error) {
    if (error.name === "CastError") {
      return res.status(400).json({
        success: false,
        message: "无效的ID格式",
      });
    }
    next(error);
  }
};

// 删除项目
const deleteItem = async (req, res, next) => {
  try {
    const item = await Item.findById(req.params.id);

    if (!item) {
      return res.status(404).json({
        success: false,
        message: "数据不存在",
      });
    }

    // 检查是否是创建者
    if (item.user.toString() !== req.user.id) {
      return res.status(403).json({
        success: false,
        message: "无权限删除此数据",
      });
    }

    await item.deleteOne();

    res.status(200).json({
      success: true,
      message: "删除成功",
    });
  } catch (error) {
    if (error.name === "CastError") {
      return res.status(400).json({
        success: false,
        message: "无效的ID格式",
      });
    }
    next(error);
  }
};

// 获取我的项目列表
const getMyItems = async (req, res, next) => {
  try {
    const page = parseInt(req.query.page) || 1;
    const limit = parseInt(req.query.limit) || 10;
    const skip = (page - 1) * limit;
    const status = req.query.status || "";

    let query = { user: req.user.id };
    if (status) {
      query.status = status;
    }

    const total = await Item.countDocuments(query);

    const items = await Item.find(query)
      .populate("user", "username avatar nickname")
      .sort({ createdAt: -1 })
      .skip(skip)
      .limit(limit);

    res.status(200).json({
      success: true,
      data: {
        items,
        pagination: {
          page,
          limit,
          total,
          pages: Math.ceil(total / limit),
          hasNext: page < Math.ceil(total / limit),
          hasPrev: page > 1,
        },
      },
    });
  } catch (error) {
    next(error);
  }
};

// 获取分类列表
const getCategories = async (req, res, next) => {
  try {
    const categories = await Item.distinct("category", { status: "active" });

    res.status(200).json({
      success: true,
      data: categories,
    });
  } catch (error) {
    next(error);
  }
};

module.exports = {
  getItems,
  getItemById,
  createItem,
  updateItem,
  deleteItem,
  getMyItems,
  getCategories,
};
