import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  Alert,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../../context/AuthContext';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { colors, spacing, fontSizes, fontWeights } from '../../styles/global';

const ChangePasswordScreen = ({ navigation }) => {
  const { changePassword, loading } = useAuth();
  
  const [oldPassword, setOldPassword] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [confirmPassword, setConfirmPassword] = useState('');
  const [showOldPassword, setShowOldPassword] = useState(false);
  const [showNewPassword, setShowNewPassword] = useState(false);
  const [showConfirmPassword, setShowConfirmPassword] = useState(false);
  const [errors, setErrors] = useState({});

  // 验证表单
  const validateForm = () => {
    const newErrors = {};
    
    if (!oldPassword) {
      newErrors.oldPassword = '请输入原密码';
    }
    
    if (!newPassword) {
      newErrors.newPassword = '请输入新密码';
    } else if (newPassword.length < 6) {
      newErrors.newPassword = '新密码至少6个字符';
    } else if (newPassword === oldPassword) {
      newErrors.newPassword = '新密码不能与原密码相同';
    }
    
    if (!confirmPassword) {
      newErrors.confirmPassword = '请确认新密码';
    } else if (newPassword !== confirmPassword) {
      newErrors.confirmPassword = '两次输入的新密码不一致';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 处理修改密码
  const handleChangePassword = async () => {
    if (!validateForm()) {
      return;
    }

    const result = await changePassword(oldPassword, newPassword);
    
    if (result.success) {
      Alert.alert('成功', result.message || '密码修改成功', [
        { 
          text: '确定', 
          onPress: () => {
            // 清空表单并返回
            setOldPassword('');
            setNewPassword('');
            setConfirmPassword('');
            navigation.goBack();
          }
        }
      ]);
    } else {
      Alert.alert('修改失败', result.message);
    }
  };

  // 密码强度指示器
  const getPasswordStrength = () => {
    if (!newPassword) return { level: 0, label: '', color: colors.gray300 };
    
    let score = 0;
    if (newPassword.length >= 6) score += 1;
    if (newPassword.length >= 8) score += 1;
    if (/[a-z]/.test(newPassword) && /[A-Z]/.test(newPassword)) score += 1;
    if (/\d/.test(newPassword)) score += 1;
    if (/[!@#$%^&*(),.?":{}|<>]/.test(newPassword)) score += 1;
    
    if (score <= 1) return { level: 1, label: '弱', color: colors.error };
    if (score <= 2) return { level: 2, label: '一般', color: colors.warning };
    if (score <= 3) return { level: 3, label: '中等', color: colors.info };
    return { level: 4, label: '强', color: colors.success };
  };

  const passwordStrength = getPasswordStrength();

  return (
    <SafeAreaView style={styles.container}>
      <KeyboardAvoidingView
        behavior={Platform.OS === 'ios' ? 'padding' : 'height'}
        style={styles.keyboardView}
      >
        <ScrollView
          contentContainerStyle={styles.scrollContent}
          keyboardShouldPersistTaps="handled"
        >
          {/* 提示信息 */}
          <View style={styles.tipCard}>
            <Ionicons name="shield-checkmark-outline" size={24} color={colors.primary} />
            <Text style={styles.tipText}>
              为了您的账户安全，建议使用包含大小写字母、数字和特殊字符的强密码
            </Text>
          </View>

          {/* 表单区域 */}
          <View style={styles.formSection}>
            <Input
              label="原密码"
              placeholder="请输入原密码"
              value={oldPassword}
              onChangeText={(text) => {
                setOldPassword(text);
                setErrors({ ...errors, oldPassword: null });
              }}
              secureTextEntry={!showOldPassword}
              error={!!errors.oldPassword}
              errorMessage={errors.oldPassword}
              leftIcon={
                <Ionicons name="lock-closed-outline" size={20} color={colors.gray500} />
              }
              rightIcon={
                <Ionicons
                  name={showOldPassword ? 'eye-off-outline' : 'eye-outline'}
                  size={20}
                  color={colors.gray500}
                  onPress={() => setShowOldPassword(!showOldPassword)}
                />
              }
            />

            <Input
              label="新密码"
              placeholder="请输入新密码（至少6位）"
              value={newPassword}
              onChangeText={(text) => {
                setNewPassword(text);
                setErrors({ ...errors, newPassword: null });
              }}
              secureTextEntry={!showNewPassword}
              error={!!errors.newPassword}
              errorMessage={errors.newPassword}
              leftIcon={
                <Ionicons name="key-outline" size={20} color={colors.gray500} />
              }
              rightIcon={
                <Ionicons
                  name={showNewPassword ? 'eye-off-outline' : 'eye-outline'}
                  size={20}
                  color={colors.gray500}
                  onPress={() => setShowNewPassword(!showNewPassword)}
                />
              }
            />

            {/* 密码强度指示器 */}
            {newPassword.length > 0 && (
              <View style={styles.strengthSection}>
                <View style={styles.strengthBars}>
                  {[1, 2, 3, 4].map((level) => (
                    <View
                      key={level}
                      style={[
                        styles.strengthBar,
                        level <= passwordStrength.level && {
                          backgroundColor: passwordStrength.color,
                        },
                      ]}
                    />
                  ))}
                </View>
                {passwordStrength.label && (
                  <Text style={[styles.strengthLabel, { color: passwordStrength.color }]}>
                    密码强度: {passwordStrength.label}
                  </Text>
                )}
              </View>
            )}

            <Input
              label="确认新密码"
              placeholder="请再次输入新密码"
              value={confirmPassword}
              onChangeText={(text) => {
                setConfirmPassword(text);
                setErrors({ ...errors, confirmPassword: null });
              }}
              secureTextEntry={!showConfirmPassword}
              error={!!errors.confirmPassword}
              errorMessage={errors.confirmPassword}
              leftIcon={
                <Ionicons name="shield-checkmark-outline" size={20} color={colors.gray500} />
              }
              rightIcon={
                <Ionicons
                  name={showConfirmPassword ? 'eye-off-outline' : 'eye-outline'}
                  size={20}
                  color={colors.gray500}
                  onPress={() => setShowConfirmPassword(!showConfirmPassword)}
                />
              }
            />
          </View>

          {/* 保存按钮 */}
          <View style={styles.buttonSection}>
            <Button
              title="确认修改"
              onPress={handleChangePassword}
              loading={loading}
              fullWidth
            />
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
    padding: spacing.md,
  },
  // 提示卡片
  tipCard: {
    flexDirection: 'row',
    backgroundColor: colors.primaryLight + '20',
    borderRadius: 12,
    padding: spacing.md,
    marginBottom: spacing.md,
  },
  tipText: {
    flex: 1,
    fontSize: fontSizes.sm,
    color: colors.primary,
    marginLeft: spacing.sm,
    lineHeight: 18,
  },
  // 表单区域
  formSection: {
    backgroundColor: colors.white,
    borderRadius: 16,
    padding: spacing.md,
  },
  // 密码强度
  strengthSection: {
    marginBottom: spacing.md,
  },
  strengthBars: {
    flexDirection: 'row',
    gap: spacing.xs,
    marginBottom: spacing.xs,
  },
  strengthBar: {
    flex: 1,
    height: 4,
    borderRadius: 2,
    backgroundColor: colors.gray200,
  },
  strengthLabel: {
    fontSize: fontSizes.xs,
    fontWeight: fontWeights.medium,
  },
  // 按钮区域
  buttonSection: {
    marginTop: spacing.xl,
  },
});

export default ChangePasswordScreen;
