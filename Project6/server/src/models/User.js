const mongoose = require("mongoose");
const bcrypt = require("bcryptjs");

const userSchema = new mongoose.Schema(
  {
    username: {
      type: String,
      required: [true, "用户名不能为空"],
      unique: true,
      trim: true,
      minlength: [2, "用户名至少2个字符"],
      maxlength: [20, "用户名最多20个字符"],
    },
    email: {
      type: String,
      required: [true, "邮箱不能为空"],
      unique: true,
      trim: true,
      lowercase: true,
      match: [
        /^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/,
        "请输入有效的邮箱地址",
      ],
    },
    password: {
      type: String,
      required: [true, "密码不能为空"],
      minlength: [6, "密码至少6个字符"],
      select: false, // 查询时默认不返回密码
    },
    avatar: {
      type: String,
      default: "",
    },
    nickname: {
      type: String,
      default: "",
    },
    phone: {
      type: String,
      default: "",
    },
    bio: {
      type: String,
      maxlength: [200, "个人简介最多200个字符"],
      default: "",
    },
    isActive: {
      type: Boolean,
      default: true,
    },
  },
  {
    timestamps: true, // 自动添加 createdAt 和 updatedAt
    toJSON: { virtuals: true },
    toObject: { virtuals: true },
  },
);

// 密码加密中间件
userSchema.pre("save", async function (next) {
  // 只有在密码被修改时才加密
  if (!this.isModified("password")) {
    return next();
  }

  // 生成盐并加密密码
  const salt = await bcrypt.genSalt(10);
  this.password = await bcrypt.hash(this.password, salt);

  next();
});

// 比较密码的方法
userSchema.methods.comparePassword = async function (enteredPassword) {
  return await bcrypt.compare(enteredPassword, this.password);
};

// 虚拟字段：显示名称
userSchema.virtual("displayName").get(function () {
  return this.nickname || this.username;
});

module.exports = mongoose.model("User", userSchema);
