import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  TouchableOpacity,
  Alert,
  ScrollView,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../../context/AuthContext';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { colors, spacing, fontSizes, fontWeights, globalStyles } from '../../styles/global';

const RegisterScreen = ({ navigation }) => {
  const { register, loading } = useAuth();
  
  const [username, setUsername] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [errors, setErrors] = useState({});

  // 验证表单
  const validateForm = () => {
    const newErrors = {};
    
    if (!username.trim()) {
      newErrors.username = '请输入用户名';
    } else if (username.length < 2 || username.length > 20) {
      newErrors.username = '用户名需在2-20个字符之间';
    }
    
    if (!email.trim()) {
      newErrors.email = '请输入邮箱';
    } else if (!/^\w+([\.-]?\w+)*@\w+([\.-]?\w+)*(\.\w{2,3})+$/.test(email)) {
      newErrors.email = '请输入有效的邮箱地址';
    }
    
    if (!password) {
      newErrors.password = '请输入密码';
    } else if (password.length < 6) {
      newErrors.password = '密码至少6个字符';
    }
    
    if (!confirmPassword) {
      newErrors.confirmPassword = '请确认密码';
    } else if (password !== confirmPassword) {
      newErrors.confirmPassword = '两次输入的密码不一致';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 处理注册
  const handleRegister = async () => {
    if (!validateForm()) {
      return;
    }

    const result = await register(username, email, password);
    
    if (result.success) {
      Alert.alert('成功', result.message || '注册成功');
    } else {
      Alert.alert('注册失败', result.message);
    }
  };

  // 返回登录
  const goToLogin = () => {
    navigation.goBack();
  };

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.keyboardView}
      >
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          keyboardShouldPersistTaps="handled"
          showsVerticalScrollIndicator={false}
        >
          <View style={styles.content}>
            {/* 标题区域 */}
            <View style={styles.headerSection}>
              <Text style={styles.headerTitle}>创建账号</Text>
              <Text style={styles.headerSubtitle}>填写信息完成注册</Text>
            </View>

            {/* 表单区域 */}
            <View style={styles.formSection}>
              <Input
                label="用户名"
                placeholder="请输入用户名"
                value={username}
                onChangeText={(text) => {
                  setUsername(text);
                  setErrors({ ...errors, username: null });
                }}
                autoCapitalize="none"
                error={!!errors.username}
                errorMessage={errors.username}
                leftIcon={
                  <Ionicons name="person-outline" size={20} color={colors.gray500} />
                }
              />

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
                  <Ionicons name="mail-outline" size={20} color={colors.gray500} />
                }
              />

              <Input
                label="密码"
                placeholder="请输入密码（至少6位）"
                value={password}
                onChangeText={(text) => {
                  setPassword(text);
                  setErrors({ ...errors, password: null });
                }}
                secureTextEntry
                error={!!errors.password}
                errorMessage={errors.password}
                leftIcon={
                  <Ionicons name="lock-closed-outline" size={20} color={colors.gray500} />
                }
              />

              <Input
                label="确认密码"
                placeholder="请再次输入密码"
                value={confirmPassword}
                onChangeText={(text) => {
                  setConfirmPassword(text);
                  setErrors({ ...errors, confirmPassword: null });
                }}
                secureTextEntry
                error={!!errors.confirmPassword}
                errorMessage={errors.confirmPassword}
                leftIcon={
                  <Ionicons name="shield-checkmark-outline" size={20} color={colors.gray500} />
                }
              />

              <Button
                title="注册"
                onPress={handleRegister}
                loading={loading}
                fullWidth
                style={styles.registerButton}
              />

              <View style={styles.loginSection}>
                <Text style={styles.loginText}>已有账号？</Text>
                <TouchableOpacity onPress={goToLogin} activeOpacity={0.7}>
                  <Text style={styles.loginLink}>立即登录</Text>
                </TouchableOpacity>
              </View>
            </View>

            {/* 提示信息 */}
            <View style={styles.tipsSection}>
              <Text style={styles.tipsText}>
                注册即表示您同意我们的
                <Text style={styles.tipsLink}> 用户协议 </Text>
                和
                <Text style={styles.tipsLink}> 隐私政策</Text>
              </Text>
            </View>
          </View>
        </ScrollView>
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
  scrollContent: {
    flexGrow: 1,
  },
  content: {
    flex: 1,
    paddingHorizontal: spacing.xl,
    paddingBottom: spacing.xl,
  },
  // 标题区域
  headerSection: {
    paddingTop: spacing.xl,
    paddingBottom: spacing.lg,
  },
  headerTitle: {
    fontSize: fontSizes.xxl,
    fontWeight: fontWeights.bold,
    color: colors.text,
    marginBottom: spacing.sm,
  },
  headerSubtitle: {
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
  registerButton: {
    marginTop: spacing.md,
  },
  // 登录区域
  loginSection: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    marginTop: spacing.lg,
  },
  loginText: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
  },
  loginLink: {
    fontSize: fontSizes.md,
    color: colors.primary,
    fontWeight: fontWeights.medium,
    marginLeft: spacing.xs,
  },
  // 提示区域
  tipsSection: {
    marginTop: spacing.lg,
    alignItems: 'center',
  },
  tipsText: {
    fontSize: fontSizes.sm,
    color: colors.textLight,
    textAlign: 'center',
  },
  tipsLink: {
    color: colors.primary,
  },
});

export default RegisterScreen;
