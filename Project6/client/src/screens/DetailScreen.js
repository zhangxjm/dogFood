import React, { useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  ScrollView,
  Image,
  TouchableOpacity,
  Alert,
  Share,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../../context/AuthContext';
import apiClient from '../../services/api';
import { API_ENDPOINTS } from '../../config/api';
import { Avatar } from '../../components/common/Cards';
import { Loading } from '../../components/common/Status';
import { colors, spacing, fontSizes, fontWeights, borderRadius, shadows, SCREEN_WIDTH } from '../../styles/global';

const DetailScreen = ({ route, navigation }) => {
  const { itemId, item: initialItem } = route.params;
  const { token } = useAuth();
  
  const [item, setItem] = useState(initialItem || null);
  const [loading, setLoading] = useState(!initialItem);
  const [error, setError] = useState(null);

  // 获取详情数据
  const fetchItemDetail = useCallback(async () => {
    if (!itemId) return;
    
    try {
      setLoading(true);
      setError(null);

      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const response = await apiClient.get(`${API_ENDPOINTS.ITEMS}/${itemId}`, { headers });
      
      if (response.data && response.data.success) {
        setItem(response.data.data);
      } else {
        setError(response.data?.message || '获取详情失败');
      }
    } catch (err) {
      console.error('获取详情失败:', err);
      setError(err.message || '网络错误，请稍后重试');
    } finally {
      setLoading(false);
    }
  }, [itemId, token]);

  // 初始加载
  useEffect(() => {
    if (!initialItem) {
      fetchItemDetail();
    }
  }, [initialItem, fetchItemDetail]);

  // 分享功能
  const handleShare = async () => {
    if (!item) return;
    
    try {
      const result = await Share.share({
        message: `${item.title}\n${item.description}`,
        title: item.title,
      });
      
      if (result.action === Share.sharedAction) {
        if (result.activityType) {
          console.log('分享到:', result.activityType);
        } else {
          console.log('分享成功');
        }
      }
    } catch (error) {
      console.error('分享失败:', error);
      Alert.alert('错误', '分享失败');
    }
  };

  // 收藏功能（模拟）
  const handleLike = () => {
    Alert.alert('提示', '收藏功能开发中...');
  };

  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return '';
    const date = new Date(dateString);
    return date.toLocaleDateString('zh-CN', {
      year: 'numeric',
      month: 'long',
      day: 'numeric',
    });
  };

  if (loading) {
    return <Loading message="加载中..." />;
  }

  if (error && !item) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.errorContainer}>
          <Ionicons name="alert-circle-outline" size={64} color={colors.error} />
          <Text style={styles.errorTitle}>加载失败</Text>
          <Text style={styles.errorMessage}>{error}</Text>
          <TouchableOpacity
            style={styles.retryButton}
            onPress={fetchItemDetail}
          >
            <Text style={styles.retryButtonText}>重试</Text>
          </TouchableOpacity>
        </View>
      </SafeAreaView>
    );
  }

  if (!item) {
    return (
      <SafeAreaView style={styles.container}>
        <View style={styles.emptyContainer}>
          <Ionicons name="document-outline" size={64} color={colors.gray300} />
          <Text style={styles.emptyTitle}>数据不存在</Text>
        </View>
      </SafeAreaView>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <ScrollView
        style={styles.scrollView}
        contentContainerStyle={styles.scrollContent}
        showsVerticalScrollIndicator={false}
      >
        {/* 图片区域 */}
        <View style={styles.imageContainer}>
          {item.image ? (
            <Image
              source={{ uri: item.image }}
              style={styles.image}
              resizeMode="cover"
            />
          ) : (
            <View style={styles.imagePlaceholder}>
              <Ionicons name="image-outline" size={80} color={colors.gray300} />
              <Text style={styles.imagePlaceholderText}>暂无图片</Text>
            </View>
          )}
        </View>

        {/* 内容区域 */}
        <View style={styles.contentContainer}>
          {/* 标题 */}
          <Text style={styles.title}>{item.title}</Text>

          {/* 元信息 */}
          <View style={styles.metaRow}>
            <View style={styles.metaItem}>
              <Ionicons name="pricetag-outline" size={14} color={colors.gray400} />
              <Text style={styles.metaText}>{item.category}</Text>
            </View>
            {item.price > 0 && (
              <View style={styles.metaItem}>
                <Ionicons name="cash-outline" size={14} color={colors.gray400} />
                <Text style={styles.metaText}>¥{item.price}</Text>
              </View>
            )}
            <View style={styles.metaItem}>
              <Ionicons name="time-outline" size={14} color={colors.gray400} />
              <Text style={styles.metaText}>{formatDate(item.createdAt)}</Text>
            </View>
          </View>

          {/* 分割线 */}
          <View style={styles.divider} />

          {/* 描述 */}
          <View style={styles.descriptionSection}>
            <Text style={styles.sectionTitle}>详情</Text>
            <Text style={styles.description}>{item.description}</Text>
          </View>

          {/* 标签 */}
          {item.tags && item.tags.length > 0 && (
            <View style={styles.tagsSection}>
              <Text style={styles.sectionTitle}>标签</Text>
              <View style={styles.tagsContainer}>
                {item.tags.map((tag, index) => (
                  <View key={index} style={styles.tag}>
                    <Text style={styles.tagText}>{tag}</Text>
                  </View>
                ))}
              </View>
            </View>
          )}

          {/* 分割线 */}
          <View style={styles.divider} />

          {/* 发布者信息 */}
          <View style={styles.authorSection}>
            <Text style={styles.sectionTitle}>发布者</Text>
            <View style={styles.authorCard}>
              <Avatar
                source={item.user?.avatar}
                name={item.user?.nickname || item.user?.username}
                size="md"
              />
              <View style={styles.authorInfo}>
                <Text style={styles.authorName}>
                  {item.user?.nickname || item.user?.username}
                </Text>
                {item.user?.bio && (
                  <Text style={styles.authorBio} numberOfLines={1}>
                    {item.user.bio}
                  </Text>
                )}
              </View>
              <TouchableOpacity style={styles.followButton} activeOpacity={0.7}>
                <Text style={styles.followButtonText}>关注</Text>
              </TouchableOpacity>
            </View>
          </View>

          {/* 统计信息 */}
          <View style={styles.statsSection}>
            <View style={styles.statCard}>
              <Ionicons name="eye-outline" size={24} color={colors.primary} />
              <Text style={styles.statValue}>{item.views || 0}</Text>
              <Text style={styles.statLabel}>浏览</Text>
            </View>
            <View style={styles.statCard}>
              <Ionicons name="heart-outline" size={24} color={colors.error} />
              <Text style={styles.statValue}>{item.likes || 0}</Text>
              <Text style={styles.statLabel}>喜欢</Text>
            </View>
            <View style={styles.statCard}>
              <Ionicons name="chatbubble-outline" size={24} color={colors.info} />
              <Text style={styles.statValue}>0</Text>
              <Text style={styles.statLabel}>评论</Text>
            </View>
          </View>
        </View>
      </ScrollView>

      {/* 底部操作栏 */}
      <View style={styles.bottomBar}>
        <TouchableOpacity style={styles.bottomButton} onPress={handleLike} activeOpacity={0.7}>
          <Ionicons name="heart-outline" size={24} color={colors.gray600} />
          <Text style={styles.bottomButtonText}>收藏</Text>
        </TouchableOpacity>
        
        <TouchableOpacity style={styles.bottomButton} onPress={handleShare} activeOpacity={0.7}>
          <Ionicons name="share-outline" size={24} color={colors.gray600} />
          <Text style={styles.bottomButtonText}>分享</Text>
        </TouchableOpacity>
        
        <View style={styles.bottomSpacer} />
        
        <TouchableOpacity style={styles.actionButton} activeOpacity={0.7}>
          <Ionicons name="chatbubble-ellipses-outline" size={20} color={colors.white} />
          <Text style={styles.actionButtonText}>联系卖家</Text>
        </TouchableOpacity>
      </View>
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  scrollView: {
    flex: 1,
  },
  scrollContent: {
    paddingBottom: 80,
  },
  // 图片区域
  imageContainer: {
    width: SCREEN_WIDTH,
    height: SCREEN_WIDTH * 0.75,
    backgroundColor: colors.gray100,
  },
  image: {
    width: '100%',
    height: '100%',
  },
  imagePlaceholder: {
    width: '100%',
    height: '100%',
    justifyContent: 'center',
    alignItems: 'center',
  },
  imagePlaceholderText: {
    marginTop: spacing.sm,
    fontSize: fontSizes.md,
    color: colors.gray400,
  },
  // 内容区域
  contentContainer: {
    backgroundColor: colors.white,
    padding: spacing.lg,
  },
  title: {
    fontSize: fontSizes.xxl,
    fontWeight: fontWeights.bold,
    color: colors.text,
    lineHeight: 32,
  },
  // 元信息
  metaRow: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    marginTop: spacing.md,
  },
  metaItem: {
    flexDirection: 'row',
    alignItems: 'center',
    marginRight: spacing.md,
    marginTop: spacing.xs,
  },
  metaText: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginLeft: spacing.xs,
  },
  // 分割线
  divider: {
    height: 1,
    backgroundColor: colors.gray100,
    marginVertical: spacing.lg,
  },
  // 描述
  descriptionSection: {
    marginBottom: spacing.md,
  },
  sectionTitle: {
    fontSize: fontSizes.lg,
    fontWeight: fontWeights.semibold,
    color: colors.text,
    marginBottom: spacing.sm,
  },
  description: {
    fontSize: fontSizes.md,
    color: colors.text,
    lineHeight: 26,
  },
  // 标签
  tagsSection: {
    marginBottom: spacing.md,
  },
  tagsContainer: {
    flexDirection: 'row',
    flexWrap: 'wrap',
  },
  tag: {
    backgroundColor: colors.primaryLight + '30',
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.xs,
    borderRadius: borderRadius.full,
    marginRight: spacing.sm,
    marginTop: spacing.sm,
  },
  tagText: {
    fontSize: fontSizes.sm,
    color: colors.primary,
  },
  // 发布者信息
  authorSection: {
    marginBottom: spacing.md,
  },
  authorCard: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.gray50,
    borderRadius: borderRadius.md,
    padding: spacing.md,
  },
  authorInfo: {
    flex: 1,
    marginLeft: spacing.md,
  },
  authorName: {
    fontSize: fontSizes.lg,
    fontWeight: fontWeights.semibold,
    color: colors.text,
  },
  authorBio: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  followButton: {
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    backgroundColor: colors.primary,
    borderRadius: borderRadius.sm,
  },
  followButtonText: {
    fontSize: fontSizes.sm,
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
  // 统计信息
  statsSection: {
    flexDirection: 'row',
    justifyContent: 'space-around',
    marginTop: spacing.md,
  },
  statCard: {
    alignItems: 'center',
    padding: spacing.md,
  },
  statValue: {
    fontSize: fontSizes.xl,
    fontWeight: fontWeights.bold,
    color: colors.text,
    marginTop: spacing.xs,
  },
  statLabel: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  // 错误状态
  errorContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
    padding: spacing.xl,
  },
  errorTitle: {
    fontSize: fontSizes.xl,
    fontWeight: fontWeights.bold,
    color: colors.text,
    marginTop: spacing.lg,
  },
  errorMessage: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
    marginTop: spacing.sm,
    textAlign: 'center',
  },
  retryButton: {
    marginTop: spacing.lg,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    backgroundColor: colors.primary,
    borderRadius: borderRadius.md,
  },
  retryButtonText: {
    fontSize: fontSizes.md,
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
  // 空状态
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  emptyTitle: {
    fontSize: fontSizes.lg,
    color: colors.textSecondary,
    marginTop: spacing.md,
  },
  // 底部操作栏
  bottomBar: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.white,
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    borderTopWidth: 1,
    borderTopColor: colors.gray100,
    ...shadows.sm,
  },
  bottomButton: {
    alignItems: 'center',
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.xs,
  },
  bottomButtonText: {
    fontSize: fontSizes.xs,
    color: colors.gray600,
    marginTop: spacing.xs / 2,
  },
  bottomSpacer: {
    flex: 1,
  },
  actionButton: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.primary,
    paddingHorizontal: spacing.lg,
    paddingVertical: spacing.md,
    borderRadius: borderRadius.md,
  },
  actionButtonText: {
    fontSize: fontSizes.md,
    color: colors.white,
    fontWeight: fontWeights.medium,
    marginLeft: spacing.xs,
  },
});

export default DetailScreen;
