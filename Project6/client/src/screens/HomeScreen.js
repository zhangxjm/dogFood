import React, { useState, useEffect, useCallback } from 'react';
import {
  View,
  Text,
  StyleSheet,
  FlatList,
  TouchableOpacity,
  RefreshControl,
  ActivityIndicator,
  TextInput,
} from 'react-native';
import { SafeAreaView } from 'react-native-safe-area-context';
import { Ionicons } from '@expo/vector-icons';

import { useAuth } from '../../context/AuthContext';
import apiClient from '../../services/api';
import { API_ENDPOINTS, PAGINATION } from '../../config/api';
import { ItemCard } from '../../components/common/Cards';
import { Loading, Empty, Error } from '../../components/common/Status';
import { colors, spacing, fontSizes, fontWeights, borderRadius } from '../../styles/global';

const HomeScreen = ({ navigation }) => {
  const { token } = useAuth();
  
  const [items, setItems] = useState([]);
  const [loading, setLoading] = useState(true);
  const [refreshing, setRefreshing] = useState(false);
  const [loadingMore, setLoadingMore] = useState(false);
  const [error, setError] = useState(null);
  const [searchText, setSearchText] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('');
  const [categories, setCategories] = useState([]);
  
  // 分页状态
  const [pagination, setPagination] = useState({
    page: PAGINATION.DEFAULT_PAGE,
    limit: PAGINATION.DEFAULT_LIMIT,
    total: 0,
    hasMore: false,
  });

  // 获取分类列表
  const fetchCategories = useCallback(async () => {
    try {
      const response = await apiClient.get(API_ENDPOINTS.ITEM_CATEGORIES);
      
      if (response.data && response.data.success) {
        setCategories(['全部', ...response.data.data]);
      }
    } catch (error) {
      console.error('获取分类失败:', error);
    }
  }, []);

  // 获取数据列表
  const fetchItems = useCallback(async (page = 1, isRefresh = false) => {
    try {
      if (isRefresh) {
        setRefreshing(true);
      } else if (page > 1) {
        setLoadingMore(true);
      } else {
        setLoading(true);
      }
      
      setError(null);

      // 构建查询参数
      let url = `${API_ENDPOINTS.ITEMS}?page=${page}&limit=${pagination.limit}`;
      
      if (searchText) {
        url += `&search=${encodeURIComponent(searchText)}`;
      }
      
      if (selectedCategory && selectedCategory !== '全部') {
        url += `&category=${encodeURIComponent(selectedCategory)}`;
      }

      const headers = token ? { Authorization: `Bearer ${token}` } : {};
      const response = await apiClient.get(url, { headers });
      
      if (response.data && response.data.success) {
        const { items: newItems, pagination: paginationInfo } = response.data.data;
        
        if (isRefresh || page === 1) {
          setItems(newItems);
        } else {
          setItems(prev => [...prev, ...newItems]);
        }
        
        setPagination({
          page: paginationInfo.page,
          limit: paginationInfo.limit,
          total: paginationInfo.total,
          hasMore: paginationInfo.hasNext,
        });
      } else {
        setError(response.data?.message || '获取数据失败');
      }
    } catch (err) {
      console.error('获取数据失败:', err);
      setError(err.message || '网络错误，请稍后重试');
    } finally {
      setLoading(false);
      setRefreshing(false);
      setLoadingMore(false);
    }
  }, [token, searchText, selectedCategory, pagination.limit]);

  // 初始加载
  useEffect(() => {
    fetchCategories();
  }, [fetchCategories]);

  // 监听搜索和分类变化，重新获取数据
  useEffect(() => {
    const debounceTimer = setTimeout(() => {
      fetchItems(1, true);
    }, 300);

    return () => clearTimeout(debounceTimer);
  }, [searchText, selectedCategory]);

  // 下拉刷新
  const onRefresh = () => {
    fetchItems(1, true);
  };

  // 加载更多
  const loadMore = () => {
    if (pagination.hasMore && !loadingMore) {
      fetchItems(pagination.page + 1, false);
    }
  };

  // 渲染列表项
  const renderItem = ({ item }) => (
    <View style={styles.itemContainer}>
      <ItemCard
        item={item}
        onPress={() => navigation.navigate('Detail', { itemId: item._id, item })}
      />
    </View>
  );

  // 渲染列表底部
  const renderFooter = () => {
    if (!loadingMore) return null;
    
    return (
      <View style={styles.footerContainer}>
        <ActivityIndicator size="small" color={colors.primary} />
        <Text style={styles.footerText}>加载中...</Text>
      </View>
    );
  };

  // 渲染空状态
  const renderEmpty = () => {
    if (loading) return null;
    
    if (error) {
      return (
        <View style={styles.emptyContainer}>
          <Error
            title="加载失败"
            description={error}
            buttonText="重试"
            onButtonPress={() => fetchItems(1, true)}
          />
        </View>
      );
    }
    
    return (
      <View style={styles.emptyContainer}>
        <Empty
          icon="inbox-outline"
          title="暂无数据"
          description={searchText || selectedCategory ? '没有找到匹配的数据' : '快来添加第一条数据吧'}
          buttonText="刷新"
          onButtonPress={() => fetchItems(1, true)}
        />
      </View>
    );
  };

  // 渲染分类标签
  const renderCategory = (category) => {
    const isSelected = (category === '全部' && !selectedCategory) || category === selectedCategory;
    
    return (
      <TouchableOpacity
        key={category}
        style={[
          styles.categoryTag,
          isSelected && styles.categoryTagActive,
        ]}
        onPress={() => setSelectedCategory(isSelected ? '' : category)}
        activeOpacity={0.7}
      >
        <Text style={[
          styles.categoryTagText,
          isSelected && styles.categoryTagTextActive,
        ]}>
          {category}
        </Text>
      </TouchableOpacity>
    );
  };

  if (loading && items.length === 0) {
    return <Loading message="加载中..." />;
  }

  return (
    <SafeAreaView style={styles.container}>
      {/* 搜索栏 */}
      <View style={styles.searchContainer}>
        <View style={styles.searchInputWrapper}>
          <Ionicons name="search-outline" size={20} color={colors.gray400} />
          <TextInput
            style={styles.searchInput}
            placeholder="搜索..."
            placeholderTextColor={colors.gray400}
            value={searchText}
            onChangeText={setSearchText}
            clearButtonMode="while-editing"
          />
          {searchText.length > 0 && (
            <TouchableOpacity
              style={styles.searchClearButton}
              onPress={() => setSearchText('')}
            >
              <Ionicons name="close-circle" size={18} color={colors.gray400} />
            </TouchableOpacity>
          )}
        </View>
      </View>

      {/* 分类标签 */}
      {categories.length > 0 && (
        <View style={styles.categoriesContainer}>
          <FlatList
            horizontal
            data={categories}
            renderItem={({ item }) => renderCategory(item)}
            keyExtractor={(item) => item}
            showsHorizontalScrollIndicator={false}
            contentContainerStyle={styles.categoriesList}
          />
        </View>
      )}

      {/* 数据列表 */}
      <FlatList
        data={items}
        renderItem={renderItem}
        keyExtractor={(item) => item._id}
        contentContainerStyle={styles.listContent}
        showsVerticalScrollIndicator={false}
        refreshControl={
          <RefreshControl
            refreshing={refreshing}
            onRefresh={onRefresh}
            colors={[colors.primary]}
            tintColor={colors.primary}
          />
        }
        ListEmptyComponent={renderEmpty}
        ListFooterComponent={renderFooter}
        onEndReached={loadMore}
        onEndReachedThreshold={0.1}
      />
    </SafeAreaView>
  );
};

const styles = StyleSheet.create({
  container: {
    flex: 1,
    backgroundColor: colors.background,
  },
  // 搜索栏
  searchContainer: {
    padding: spacing.md,
    backgroundColor: colors.white,
    borderBottomWidth: 1,
    borderBottomColor: colors.gray100,
  },
  searchInputWrapper: {
    flexDirection: 'row',
    alignItems: 'center',
    backgroundColor: colors.gray100,
    borderRadius: borderRadius.full,
    paddingHorizontal: spacing.md,
    height: 40,
  },
  searchInput: {
    flex: 1,
    height: '100%',
    marginLeft: spacing.sm,
    fontSize: fontSizes.md,
    color: colors.text,
  },
  searchClearButton: {
    padding: spacing.xs,
  },
  // 分类标签
  categoriesContainer: {
    backgroundColor: colors.white,
    paddingVertical: spacing.sm,
    borderBottomWidth: 1,
    borderBottomColor: colors.gray100,
  },
  categoriesList: {
    paddingHorizontal: spacing.md,
  },
  categoryTag: {
    paddingHorizontal: spacing.md,
    paddingVertical: spacing.sm,
    borderRadius: borderRadius.full,
    backgroundColor: colors.gray100,
    marginRight: spacing.sm,
  },
  categoryTagActive: {
    backgroundColor: colors.primary,
  },
  categoryTagText: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
  },
  categoryTagTextActive: {
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
  // 列表
  listContent: {
    padding: spacing.md,
    flexGrow: 1,
  },
  itemContainer: {
    marginBottom: spacing.md,
  },
  // 空状态
  emptyContainer: {
    flex: 1,
    justifyContent: 'center',
    alignItems: 'center',
  },
  // 底部加载
  footerContainer: {
    flexDirection: 'row',
    justifyContent: 'center',
    alignItems: 'center',
    paddingVertical: spacing.lg,
  },
  footerText: {
    marginLeft: spacing.sm,
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
  },
});

export default HomeScreen;
