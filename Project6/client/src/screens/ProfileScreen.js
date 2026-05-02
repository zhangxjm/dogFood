import React, { useState, useEffect } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  TouchableOpacity,
  Alert,
  RefreshControl,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../../context/AuthContext';
import { Avatar } from '../../components/common/Cards';
import { ListItem } from '../../components/common/Cards';
import { Card } from '../../components/common/Cards';
import { colors, spacing, fontSizes, fontWeights, globalStyles } from '../../styles/global';

const ProfileScreen = ({ navigation }) => {
  const { user, logout, getCurrentUser, loading } = useAuth();
  const [refreshing, setRefreshing] = useState(false);

  // 下拉刷新
  const onRefresh = async () => {
    setRefreshing(true);
    await getCurrentUser();
    setRefreshing(false);
  };

  // 退出登录确认
  const handleLogout = () => {
    Alert.alert(
      '退出登录',
      '确定要退出登录吗？',
      [
        {
          text: '取消',
          style: 'cancel',
        },
        {
          text: '确定',
          style: 'destructive',
          onPress: async () => {
            const result = await logout();
            if (!result.success) {
              Alert.alert('错误', result.message);
            }
          },
        },
      ],
      { cancelable: true }
    );
  };

  // 跳转到编辑资料
  const goToEditProfile = () => {
    navigation.navigate('EditProfile');
  };

  // 跳转到修改密码
  const goToChangePassword = () => {
    navigation.navigate('ChangePassword');
  };

  // 菜单项目
  const menuItems = [
    {
      id: 'editProfile',
      icon: 'person-outline',
      title: '编辑资料',
      subtitle: '修改个人信息',
      onPress: goToEditProfile,
      showArrow: true,
    },
    {
      id: 'changePassword',
      icon: 'lock-closed-outline',
      title: '修改密码',
      subtitle: '更新登录密码',
      onPress: goToChangePassword,
      showArrow: true,
    },
    {
      id: 'settings',
      icon: 'settings-outline',
      title: '设置',
      subtitle: '应用设置',
      onPress: () => Alert.alert('提示', '功能开发中...'),
      showArrow: true,
    },
    {
      id: 'about',
      icon: 'information-circle-outline',
      title: '关于我们',
      subtitle: '了解更多信息',
      onPress: () => Alert.alert('关于', 'RN Express App v1.0.0\nReact Native + Express 全栈应用'),
      showArrow: true,
    },
  ];

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView
        contentContainerStyle={styles.scrollContent}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={[colors.primary]}
          />
        }
      >
        {/* 用户信息卡片 */}
        <Card style={styles.profileCard}>
          <View style={styles.profileHeader}>
            <Avatar
              source={user?.avatar}
              name={user?.displayName || user?.username}
              size="lg"
            />
            <View style={styles.profileInfo}>
              <Text style={styles.profileName}>
                {user?.displayName || user?.nickname || user?.username}
              </Text>
              <Text style={styles.profileEmail}>{user?.email}</Text>
              <Text style={styles.profileUsername}>@{user?.username}</Text>
            </View>
            <TouchableOpacity
              style={styles.editButton}
              onPress={goToEditProfile}
              activeOpacity={0.7}
            >
              <Ionicons name="create-outline" size={20} color={colors.primary} />
            </TouchableOpacity>
          </View>

          {/* 统计信息 */}
          <View style={styles.statsRow}>
            <View style={styles.statItem}>
              <Text style={styles.statValue}>0</Text>
              <Text style={styles.statLabel}>发布</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statItem}>
              <Text style={styles.statValue}>0</Text>
              <Text style={styles.statLabel}>收藏</Text>
            </View>
            <View style={styles.statDivider} />
            <View style={styles.statItem}>
              <Text style={styles.statValue}>0</Text>
              <Text style={styles.statLabel}>关注</Text>
            </View>
          </View>

          {/* 个人简介 */}
          {user?.bio && (
            <View style={styles.bioSection}>
              <Text style={styles.bioText}>{user.bio}</Text>
            </View>
          )}
        </Card>

        {/* 菜单列表 */}
        <Card style={styles.menuCard} padding="none">
          {menuItems.map((item, index) => (
            <ListItem
              key={item.id}
              leftIcon={
                <View style={[styles.menuIcon, { backgroundColor: getIconBackground(item.icon) }]}>
                  <Ionicons name={item.icon} size={20} color={colors.white} />
                </View>
              }
              title={item.title}
              subtitle={item.subtitle}
              onPress={item.onPress}
              showArrow={item.showArrow}
              style={index < menuItems.length - 1 ? styles.menuItem : styles.menuItemLast}
            />
          ))}
        </Card>

        {/* 退出登录按钮 */}
        <TouchableOpacity
          style={styles.logoutButton}
          onPress={handleLogout}
          activeOpacity={0.7}
        >
          <Ionicons name="log-out-outline" size={20} color={colors.error} />
          <Text style={styles.logoutText}>退出登录</Text>
        </TouchableOpacity>

        {/* 版本信息 */}
        <Text style={styles.versionText}>版本 v1.0.0</Text>
      </ScrollView>
    </SafeAreaView>
  );
};

// 获取图标背景色
const getIconBackground = (iconName) => {
  const colorsMap = {
    'person-outline': colors.primary,
    'lock-closed-outline': colors.secondary,
    'settings-outline': colors.warning,
    'information-circle-outline': colors.info,
  };
  return colorsMap[iconName] || colors.gray400;
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollContent: {
    padding: spacing.md,
  },
  // 用户信息卡片
  profileCard: {
    marginBottom: spacing.md,
  },
  profileHeader: {
    flexDirection: 'row',
    alignItems: 'flex-start',
  },
  profileInfo: {
    flex: 1,
    marginLeft: spacing.md,
  },
  profileName: {
    fontSize: fontSizes.xl,
    fontWeight: fontWeights.bold,
    color: colors.text,
  },
  profileEmail: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  profileUsername: {
    fontSize: fontSizes.sm,
    color: colors.textLight,
    marginTop: spacing.xs,
  },
  editButton: {
    padding: spacing.xs,
  },
  // 统计信息
  statsRow: {
    flexDirection: 'row',
    marginTop: spacing.lg,
    paddingTop: spacing.md,
    borderTopWidth: 1,
    borderTopColor: colors.gray100,
  },
  statItem: {
    flex: 1,
    alignItems: 'center',
  },
  statValue: {
    fontSize: fontSizes.xl,
    fontWeight: fontWeights.bold,
    color: colors.text,
  },
  statLabel: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  statDivider: {
    width: 1,
    backgroundColor: colors.gray100,
  },
  // 个人简介
  bioSection: {
    marginTop: spacing.md,
    paddingTop: spacing.md,
    borderTopWidth: 1,
    borderTopColor: colors.gray100,
  },
  bioText: {
    fontSize: fontSizes.md,
    color: colors.text,
    lineHeight: 22,
  },
  // 菜单卡片
  menuCard: {
    marginBottom: spacing.md,
  },
  menuItem: {
    borderBottomWidth: 1,
    borderBottomColor: colors.gray100,
  },
  menuItemLast: {
    borderBottomWidth: 0,
  },
  menuIcon: {
    width: 36,
    height: 36,
    borderRadius: 8,
    justifyContent: 'center',
    alignItems: 'center',
  },
  // 退出登录
  logoutButton: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    backgroundColor: colors.white,
    borderRadius: 12,
    paddingVertical: spacing.md,
    marginTop: spacing.sm,
    ...globalStyles.card,
  },
  logoutText: {
    fontSize: fontSizes.md,
    color: colors.error,
    fontWeight: fontWeights.medium,
    marginLeft: spacing.xs,
  },
  // 版本信息
  versionText: {
    textAlign: 'center',
    fontSize: fontSizes.sm,
    color: colors.textLight,
    marginTop: spacing.xl,
  },
});

export default ProfileScreen;
