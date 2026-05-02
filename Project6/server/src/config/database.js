const mongoose = require("mongoose");

// 数据库连接函数
const connectDB = async () => {
  try {
    const conn = await mongoose.connect(
      process.env.MONGODB_URI || "mongodb://localhost:27017/rnapp",
    );

    console.log(`✅ MongoDB 连接成功: ${conn.connection.host}`);

    // 连接错误处理
    mongoose.connection.on("error", (err) => {
      console.error("❌ MongoDB 连接错误:", err);
    });

    // 断开连接处理
    mongoose.connection.on("disconnected", () => {
      console.log("⚠️  MongoDB 连接断开");
    });

    return conn;
  } catch (error) {
    console.error("❌ MongoDB 连接失败:", error.message);
    process.exit(1);
  }
};

// 立即连接数据库
connectDB();

module.exports = mongoose;
