import React, { useState } from 'react';
import {
  View,
  Text,
  StyleSheet,
  KeyboardAvoidingView,
  Platform,
  ScrollView,
  TouchableOpacity,
  Alert,
  TextInput,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../../context/AuthContext';
import Button from '../../components/common/Button';
import Input from '../../components/common/Input';
import { Avatar } from '../../components/common/Cards';
import { colors, spacing, fontSizes, fontWeights } from '../../styles/global';

const EditProfileScreen = ({ navigation }) => {
  const { user, updateUser, loading } = useAuth();
  
  const [nickname, setNickname] = useState(user?.nickname || '');
  const [phone, setPhone] = useState(user?.phone || '');
  const [bio, setBio] = useState(user?.bio || '');
  const [errors, setErrors] = useState({});

  // 验证表单
  const validateForm = () => {
    const newErrors = {};
    
    if (nickname && (nickname.length < 2 || nickname.length > 20)) {
      newErrors.nickname = '昵称需在2-20个字符之间';
    }
    
    if (phone && !/^1[3-9]\d{9}$/.test(phone)) {
      newErrors.phone = '请输入有效的手机号';
    }
    
    if (bio && bio.length > 200) {
      newErrors.bio = '个人简介最多200个字符';
    }
    
    setErrors(newErrors);
    return Object.keys(newErrors).length === 0;
  };

  // 处理保存
  const handleSave = async () => {
    if (!validateForm()) {
      return;
    }

    const result = await updateUser({
      nickname: nickname || undefined,
      phone: phone || undefined,
      bio: bio || undefined,
    });
    
    if (result.success) {
      Alert.alert('成功', result.message || '保存成功', [
        { text: '确定', onPress: () => navigation.goBack() }
      ]);
    } else {
      Alert.alert('保存失败', result.message);
    }
  };

  // 选择头像（模拟）
  const handleChooseAvatar = () => {
    Alert.alert('提示', '头像选择功能开发中...');
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
        >
          {/* 头像区域 */}
          <View style={styles.avatarSection}>
            <TouchableOpacity
              style={styles.avatarWrapper}
              onPress={handleChooseAvatar}
              activeOpacity={0.7}
            >
              <Avatar
                source={user?.avatar}
                name={user?.displayName || user?.username}
                size="xl"
              />
              <View style={styles.avatarEditIcon}>
                <Ionicons name="camera-outline" size={16} color={colors.white} />
              </View>
            </TouchableOpacity>
            <Text style={styles.avatarHint}>点击更换头像</Text>
          </View>

          {/* 表单区域 */}
          <View style={styles.formSection}>
            <Input
              label="用户名"
              placeholder="用户名"
              value={user?.username || ''}
              disabled
              leftIcon={
                <Ionicons name="person-outline" size={20} color={colors.gray500} />
              }
            />

            <Input
              label="邮箱"
              placeholder="邮箱地址"
              value={user?.email || ''}
              disabled
              keyboardType="email-address"
              leftIcon={
                <Ionicons name="mail-outline" size={20} color={colors.gray500} />
              }
            />

            <Input
              label="昵称"
              placeholder="请输入昵称（选填）"
              value={nickname}
              onChangeText={(text) => {
                setNickname(text);
                setErrors({ ...errors, nickname: null });
              }}
              error={!!errors.nickname}
              errorMessage={errors.nickname}
              leftIcon={
                <Ionicons name="happy-outline" size={20} color={colors.gray500} />
              }
            />

            <Input
              label="手机号"
              placeholder="请输入手机号（选填）"
              value={phone}
              onChangeText={(text) => {
                setPhone(text);
                setErrors({ ...errors, phone: null });
              }}
              keyboardType="phone-pad"
              error={!!errors.phone}
              errorMessage={errors.phone}
              leftIcon={
                <Ionicons name="call-outline" size={20} color={colors.gray500} />
              }
            />

            <View style={styles.bioInputWrapper}>
              <Text style={styles.label}>个人简介</Text>
              <View style={[
                styles.bioInputContainer,
                errors.bio && styles.inputContainerError,
              ]}>
                <Ionicons name="document-text-outline" size={20} color={colors.gray500} style={styles.bioInputIcon} />
                <View style={styles.bioInputContent}>
                  <TextInput
                    style={styles.bioInput}
                    placeholder="介绍一下自己吧（最多200字）"
                    placeholderTextColor={colors.gray400}
                    value={bio}
                    onChangeText={(text) => {
                      setBio(text);
                      setErrors({ ...errors, bio: null });
                    }}
                    multiline
                    numberOfLines={4}
                    maxLength={200}
                    textAlignVertical="top"
                  />
                  <Text style={[
                    styles.charCount,
                    bio.length >= 180 && styles.charCountWarning,
                  ]}>
                    {bio.length}/200
                  </Text>
                </View>
              </View>
              {errors.bio && (
                <Text style={styles.errorText}>{errors.bio}</Text>
              )}
            </View>
          </View>

          {/* 保存按钮 */}
          <View style={styles.buttonSection}>
            <Button
              title="保存"
              onPress={handleSave}
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
  // 头像区域
  avatarSection: {
    alignItems: 'center',
    paddingVertical: spacing.lg,
  },
  avatarWrapper: {
    position: 'relative',
  },
  avatarEditIcon: {
    position: 'absolute',
    bottom: 0,
    right: 0,
    width: 32,
    height: 32,
    borderRadius: 16,
    backgroundColor: colors.primary,
    justifyContent: 'center',
    alignItems: 'center',
    borderWidth: 3,
    borderColor: colors.white,
  },
  avatarHint: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginTop: spacing.sm,
  },
  // 表单区域
  formSection: {
    backgroundColor: colors.white,
    borderRadius: 16,
    padding: spacing.md,
  },
  // 个人简介输入框
  bioInputWrapper: {
    marginBottom: spacing.md,
  },
  label: {
    fontSize: fontSizes.md,
    fontWeight: fontWeights.medium,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  bioInputContainer: {
    flexDirection: 'row',
    alignItems: 'flex-start',
    borderWidth: 1,
    borderColor: colors.border,
    borderRadius: 12,
    backgroundColor: colors.white,
    padding: spacing.sm,
  },
  inputContainerError: {
    borderColor: colors.error,
  },
  bioInputIcon: {
    marginTop: spacing.xs,
  },
  bioInputContent: {
    flex: 1,
    marginLeft: spacing.xs,
  },
  bioInput: {
    fontSize: fontSizes.md,
    color: colors.text,
    minHeight: 80,
  },
  charCount: {
    fontSize: fontSizes.xs,
    color: colors.textLight,
    textAlign: 'right',
    marginTop: spacing.xs,
  },
  charCountWarning: {
    color: colors.warning,
  },
  errorText: {
    fontSize: fontSizes.sm,
    color: colors.error,
    marginTop: spacing.xs,
  },
  // 按钮区域
  buttonSection: {
    marginTop: spacing.xl,
  },
});

export default EditProfileScreen;
