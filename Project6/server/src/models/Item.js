const mongoose = require("mongoose");

const itemSchema = new mongoose.Schema(
  {
    title: {
      type: String,
      required: [true, "标题不能为空"],
      trim: true,
      minlength: [1, "标题至少1个字符"],
      maxlength: [100, "标题最多100个字符"],
    },
    description: {
      type: String,
      required: [true, "描述不能为空"],
      trim: true,
      minlength: [1, "描述至少1个字符"],
      maxlength: [1000, "描述最多1000个字符"],
    },
    image: {
      type: String,
      default: "",
    },
    category: {
      type: String,
      required: [true, "分类不能为空"],
      default: "默认分类",
    },
    price: {
      type: Number,
      default: 0,
      min: [0, "价格不能为负数"],
    },
    status: {
      type: String,
      enum: ["active", "inactive", "draft"],
      default: "active",
    },
    views: {
      type: Number,
      default: 0,
    },
    likes: {
      type: Number,
      default: 0,
    },
    user: {
      type: mongoose.Schema.Types.ObjectId,
      ref: "User",
      required: true,
    },
    tags: [
      {
        type: String,
      },
    ],
  },
  {
    timestamps: true,
    toJSON: { virtuals: true },
    toObject: { virtuals: true },
  },
);

// 创建索引以提高查询性能
itemSchema.index({ user: 1 });
itemSchema.index({ category: 1 });
itemSchema.index({ status: 1 });
itemSchema.index({ createdAt: -1 });

// 虚拟字段：是否有图片
itemSchema.virtual("hasImage").get(function () {
  return this.image && this.image.length > 0;
});

module.exports = mongoose.model("Item", itemSchema);
