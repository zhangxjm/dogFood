import React, { createContext, useContext, useState, useCallback } from "react";
import { Appearance, useColorScheme } from "react-native";
import { colors } from "../styles/global";

// 创建Context
const ThemeContext = createContext();

// 自定义Hook
export const useTheme = () => {
  const context = useContext(ThemeContext);
  if (!context) {
    throw new Error("useTheme must be used within a ThemeProvider");
  }
  return context;
};

// 深色主题颜色
const darkColors = {
  ...colors,
  background: "#111827",
  card: "#1f2937",
  text: "#f9fafb",
  textSecondary: "#d1d5db",
  textLight: "#9ca3af",
  border: "#374151",
  gray50: "#111827",
  gray100: "#1f2937",
  gray200: "#374151",
  gray300: "#4b5563",
  gray400: "#6b7280",
  gray500: "#9ca3af",
  gray600: "#d1d5db",
  gray700: "#e5e7eb",
  gray800: "#f3f4f6",
  gray900: "#f9fafb",
};

// Provider 组件
export const ThemeProvider = ({ children }) => {
  const systemColorScheme = useColorScheme();
  const [themeMode, setThemeMode] = useState("system"); // 'light' | 'dark' | 'system'

  // 获取当前主题颜色
  const getColors = useCallback(() => {
    if (themeMode === "dark") {
      return darkColors;
    }
    if (themeMode === "light") {
      return colors;
    }
    // 跟随系统
    return systemColorScheme === "dark" ? darkColors : colors;
  }, [themeMode, systemColorScheme]);

  // 当前颜色
  const currentColors = getColors();

  // 当前是否为深色模式
  const isDark =
    themeMode === "dark" ||
    (themeMode === "system" && systemColorScheme === "dark");

  // 切换主题
  const toggleTheme = () => {
    if (themeMode === "system") {
      setThemeMode("light");
    } else if (themeMode === "light") {
      setThemeMode("dark");
    } else {
      setThemeMode("system");
    }
  };

  // 设置主题
  const setTheme = (mode) => {
    if (["light", "dark", "system"].includes(mode)) {
      setThemeMode(mode);
    }
  };

  // Context值
  const value = {
    colors: currentColors,
    isDark,
    themeMode,
    toggleTheme,
    setTheme,
  };

  return (
    <ThemeContext.Provider value={value}>{children}</ThemeContext.Provider>
  );
};
