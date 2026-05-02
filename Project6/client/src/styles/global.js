import { StyleSheet, Dimensions, Platform } from "react-native";

// 屏幕尺寸
export const { width: SCREEN_WIDTH, height: SCREEN_HEIGHT } =
  Dimensions.get("window");

// 颜色配置
export const colors = {
  // 主色调
  primary: "#667eea",
  primaryDark: "#5a67d8",
  primaryLight: "#90cdf4",

  // 辅助色
  secondary: "#764ba2",

  // 状态色
  success: "#10b981",
  warning: "#f59e0b",
  error: "#ef4444",
  info: "#3b82f6",

  // 中性色
  white: "#ffffff",
  black: "#000000",
  gray50: "#f9fafb",
  gray100: "#f3f4f6",
  gray200: "#e5e7eb",
  gray300: "#d1d5db",
  gray400: "#9ca3af",
  gray500: "#6b7280",
  gray600: "#4b5563",
  gray700: "#374151",
  gray800: "#1f2937",
  gray900: "#111827",

  // 背景色
  background: "#f5f7fa",
  card: "#ffffff",

  // 文字色
  text: "#1f2937",
  textSecondary: "#6b7280",
  textLight: "#9ca3af",

  // 边框色
  border: "#e5e7eb",

  // 渐变色
  gradientPrimary: ["#667eea", "#764ba2"],
  gradientSuccess: ["#10b981", "#059669"],
  gradientDanger: ["#ef4444", "#dc2626"],
};

// 间距配置
export const spacing = {
  xs: 4,
  sm: 8,
  md: 16,
  lg: 24,
  xl: 32,
  xxl: 48,
};

// 字体大小配置
export const fontSizes = {
  xs: 10,
  sm: 12,
  md: 14,
  lg: 16,
  xl: 18,
  xxl: 24,
  xxxl: 32,
};

// 字体粗细配置
export const fontWeights = {
  thin: "200",
  light: "300",
  regular: "400",
  medium: "500",
  semibold: "600",
  bold: "700",
  extraBold: "800",
};

// 圆角配置
export const borderRadius = {
  sm: 4,
  md: 8,
  lg: 12,
  xl: 16,
  xxl: 24,
  full: 9999,
};

// 阴影配置
export const shadows = {
  sm: {
    shadowColor: colors.black,
    shadowOffset: { width: 0, height: 1 },
    shadowOpacity: 0.05,
    shadowRadius: 2,
    elevation: 1,
  },
  md: {
    shadowColor: colors.black,
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.1,
    shadowRadius: 4,
    elevation: 2,
  },
  lg: {
    shadowColor: colors.black,
    shadowOffset: { width: 0, height: 4 },
    shadowOpacity: 0.15,
    shadowRadius: 8,
    elevation: 4,
  },
  xl: {
    shadowColor: colors.black,
    shadowOffset: { width: 0, height: 8 },
    shadowOpacity: 0.2,
    shadowRadius: 16,
    elevation: 8,
  },
};

// 全局样式
export const globalStyles = StyleSheet.create({
  // 容器样式
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },

  safeArea: {
    flex: 1,
    backgroundColor: colors.background,
  },

  // 居中样式
  center: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
  },

  // 卡片样式
  card: {
    backgroundColor: colors.card,
    borderRadius: borderRadius.md,
    ...shadows.sm,
  },

  // 分割线
  divider: {
    height: 1,
    backgroundColor: colors.border,
    marginVertical: spacing.md,
  },

  // 文字样式
  title: {
    fontSize: fontSizes.xl,
    fontWeight: fontWeights.bold,
    color: colors.text,
  },

  subtitle: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
  },

  body: {
    fontSize: fontSizes.md,
    color: colors.text,
    lineHeight: 22,
  },

  caption: {
    fontSize: fontSizes.sm,
    color: colors.textLight,
  },

  // 按钮样式
  button: {
    height: 48,
    borderRadius: borderRadius.md,
    justifyContent: "center",
    alignItems: "center",
    backgroundColor: colors.primary,
  },

  buttonText: {
    fontSize: fontSizes.lg,
    fontWeight: fontWeights.medium,
    color: colors.white,
  },

  // 输入框样式
  input: {
    height: 48,
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: borderRadius.md,
    paddingHorizontal: spacing.md,
    fontSize: fontSizes.md,
    color: colors.text,
    backgroundColor: colors.white,
  },

  inputFocused: {
    borderColor: colors.primary,
  },

  // 图片样式
  image: {
    width: "100%",
    height: "100%",
  },

  // 行布局
  row: {
    flexDirection: "row",
    alignItems: "center",
  },

  rowBetween: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "space-between",
  },

  // 列布局
  column: {
    flexDirection: "column",
  },

  // 内边距
  paddingSm: {
    padding: spacing.sm,
  },

  paddingMd: {
    padding: spacing.md,
  },

  paddingLg: {
    padding: spacing.lg,
  },

  // 外边距
  marginSm: {
    margin: spacing.sm,
  },

  marginMd: {
    margin: spacing.md,
  },

  marginLg: {
    margin: spacing.lg,
  },

  // 水平居中
  mxAuto: {
    marginLeft: "auto",
    marginRight: "auto",
  },
});

export default {
  colors,
  spacing,
  fontSizes,
  fontWeights,
  borderRadius,
  shadows,
  globalStyles,
  SCREEN_WIDTH,
  SCREEN_HEIGHT,
};
