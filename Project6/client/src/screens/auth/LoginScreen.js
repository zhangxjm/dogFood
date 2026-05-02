import React, { useState } from "react";
import {
  View,
  Text,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  TouchableOpacity,
  Alert,
} from "react-native";
import { SafeAreaView } from "react-native-safe-area-context";
import { Ionicons } from "@expo/vector-icons";

import { useAuth } from "../../context/AuthContext";
import Button from "../../components/common/Button";
import Input from "../../components/common/Input";
import {
  colors,
  spacing,
  fontSizes,
  fontWeights,
  globalStyles,
} from "../../styles/global";

const LoginScreen = ({ navigation }) => {
  const { login, loading } = useAuth();

  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [errors, setErrors] = useState({});

  // 验证表单
  const validateForm = () => {
    const newErrors = {};

    if (!email.trim()) {
      newErrors.email = "请输入邮箱";
    } else if (!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
      newErrors.email = "请输入有效的邮箱地址";
    }

    if (!password) {
      newErrors.password = "请输入密码";
    } else if (password.length < 6) {
      newErrors.password = "密码至少6个字符";
    }

    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 处理登录
  const handleLogin = async () => {
    if (!validateForm()) {
      return;
    }

    const result = await login(email, password);

    if (result.success) {
      Alert.alert("成功", result.message || "登录成功");
    } else {
      Alert.alert("登录失败", result.message);
    }
  };

  // 跳转到注册
  const goToRegister = () => {
    navigation.navigate("Register");
  };

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        behavior={Platform.OS === "ios" ? "padding" : "height"}
        style={styles.keyboardView}
      >
        <View style={styles.content}>
          {/* Logo 区域 */}
          <View style={styles.logoSection}>
            <View style={styles.logoIcon}>
              <Ionicons name="rocket" size={64} color={colors.white} />
            </View>
            <Text style={styles.appTitle}>RN Express App</Text>
            <Text style={styles.appSubtitle}>
              React Native + Express 全栈应用
            </Text>
          </View>

          {/* 表单区域 */}
          <View style={styles.formSection}>
            <Text style={styles.formTitle}>登录</Text>

            <Input
              label="邮箱"
              placeholder="请输入邮箱地址"
              value={email}
              onChangeText={(text) => {
                setEmail(text);
                setErrors({ ...errors, email: null });
              }}
              keyboardType="email-address"
              error={!!errors.email}
              errorMessage={errors.email}
              leftIcon={
                <Ionicons
                  name="mail-outline"
                  size={20}
                  color={colors.gray500}
                />
              }
            />

            <Input
              label="密码"
              placeholder="请输入密码"
              value={password}
              onChangeText={(text) => {
                setPassword(text);
                setErrors({ ...errors, password: null });
              }}
              secureTextEntry
              error={!!errors.password}
              errorMessage={errors.password}
              leftIcon={
                <Ionicons
                  name="lock-closed-outline"
                  size={20}
                  color={colors.gray500}
                />
              }
            />

            <Button
              title="登录"
              onPress={handleLogin}
              loading={loading}
              fullWidth
              style={styles.loginButton}
            />

            <View style={styles.registerSection}>
              <Text style={styles.registerText}>还没有账号？</Text>
              <TouchableOpacity onPress={goToRegister} activeOpacity={0.7}>
                <Text style={styles.registerLink}>立即注册</Text>
              </TouchableOpacity>
            </View>
          </View>
        </View>
      </KeyboardAvoidingView>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  keyboardView: {
    flex: 1,
  },
  content: {
    flex: 1,
    paddingHorizontal: spacing.xl,
  },
  // Logo 区域
  logoSection: {
    alignItems: "center",
    paddingTop: spacing.xxl,
    paddingBottom: spacing.lg,
  },
  logoIcon: {
    width: 120,
    height: 120,
    borderRadius: 60,
    backgroundColor: colors.primary,
    justifyContent: "center",
    alignItems: "center",
    marginBottom: spacing.lg,
  },
  appTitle: {
    fontSize: fontSizes.xxl,
    fontWeight: fontWeights.bold,
    color: colors.text,
    marginBottom: spacing.sm,
  },
  appSubtitle: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
  },
  // 表单区域
  formSection: {
    backgroundColor: colors.white,
    borderRadius: 16,
    padding: spacing.lg,
    ...globalStyles.card,
  },
  formTitle: {
    fontSize: fontSizes.xl,
    fontWeight: fontWeights.bold,
    color: colors.text,
    marginBottom: spacing.lg,
    textAlign: "center",
  },
  loginButton: {
    marginTop: spacing.md,
  },
  // 注册区域
  registerSection: {
    flexDirection: "row",
    justifyContent: "center",
    alignItems: "center",
    marginTop: spacing.lg,
  },
  registerText: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
  },
  registerLink: {
    fontSize: fontSizes.md,
    color: colors.primary,
    fontWeight: fontWeights.medium,
    marginLeft: spacing.xs,
  },
});

export default LoginScreen;
