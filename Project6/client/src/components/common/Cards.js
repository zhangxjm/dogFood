import React from "react";
import { View, Text, StyleSheet, TouchableOpacity, Image } from "react-native";
import { Ionicons } from "@expo/vector-icons";
import {
  colors,
  fontSizes,
  spacing,
  borderRadius,
  shadows,
  fontWeights,
  SCREEN_WIDTH,
} from "../../styles/global";

// 卡片组件
const Card = ({ children, style, onPress, shadow = "sm", padding = "md" }) => {
  const getPaddingStyle = () => {
    switch (padding) {
      case "none":
        return { padding: 0 };
      case "sm":
        return { padding: spacing.sm };
      case "md":
        return { padding: spacing.md };
      case "lg":
        return { padding: spacing.lg };
      default:
        return { padding: spacing.md };
    }
  };

  const getShadowStyle = () => {
    switch (shadow) {
      case "none":
        return {};
      case "sm":
        return shadows.sm;
      case "md":
        return shadows.md;
      case "lg":
        return shadows.lg;
      default:
        return shadows.sm;
    }
  };

  const Container = onPress ? TouchableOpacity : View;

  return (
    <Container
      style={[styles.card, getPaddingStyle(), getShadowStyle(), style]}
      onPress={onPress}
      activeOpacity={onPress ? 0.7 : 1}
    >
      {children}
    </Container>
  );
};

// 列表项组件
const ListItem = ({
  title,
  subtitle,
  leftIcon,
  rightIcon,
  onPress,
  showArrow = false,
  style,
  titleStyle,
  subtitleStyle,
}) => {
  return (
    <TouchableOpacity
      style={[styles.listItem, style]}
      onPress={onPress}
      activeOpacity={0.7}
      disabled={!onPress}
    >
      {leftIcon && <View style={styles.listItemLeft}>{leftIcon}</View>}

      <View style={styles.listItemContent}>
        <Text style={[styles.listItemTitle, titleStyle]} numberOfLines={1}>
          {title}
        </Text>
        {subtitle && (
          <Text
            style={[styles.listItemSubtitle, subtitleStyle]}
            numberOfLines={1}
          >
            {subtitle}
          </Text>
        )}
      </View>

      <View style={styles.listItemRight}>
        {rightIcon}
        {showArrow && (
          <Ionicons name="chevron-forward" size={20} color={colors.gray400} />
        )}
      </View>
    </TouchableOpacity>
  );
};

// 项目卡片组件
const ItemCard = ({ item, onPress, style }) => {
  return (
    <TouchableOpacity
      style={[styles.itemCard, style]}
      onPress={onPress}
      activeOpacity={0.7}
    >
      {item.image ? (
        <Image
          source={{ uri: item.image }}
          style={styles.itemCardImage}
          resizeMode="cover"
        />
      ) : (
        <View style={styles.itemCardPlaceholder}>
          <Ionicons name="image-outline" size={40} color={colors.gray300} />
        </View>
      )}

      <View style={styles.itemCardContent}>
        <Text style={styles.itemCardTitle} numberOfLines={2}>
          {item.title}
        </Text>
        <Text style={styles.itemCardDescription} numberOfLines={2}>
          {item.description}
        </Text>
        <View style={styles.itemCardFooter}>
          <View style={styles.itemCardTag}>
            <Ionicons
              name="pricetag-outline"
              size={12}
              color={colors.gray400}
            />
            <Text style={styles.itemCardTagText}>{item.category}</Text>
          </View>
          <View style={styles.itemCardStats}>
            <Ionicons name="eye-outline" size={14} color={colors.gray400} />
            <Text style={styles.itemCardStatText}>{item.views || 0}</Text>
            <Ionicons
              name="heart-outline"
              size={14}
              color={colors.gray400}
              style={{ marginLeft: spacing.sm }}
            />
            <Text style={styles.itemCardStatText}>{item.likes || 0}</Text>
          </View>
        </View>
      </View>
    </TouchableOpacity>
  );
};

// 头像组件
const Avatar = ({ source, size = "md", name, style }) => {
  const getSizeStyle = () => {
    switch (size) {
      case "xs":
        return { width: 24, height: 24, fontSize: fontSizes.xs };
      case "sm":
        return { width: 32, height: 32, fontSize: fontSizes.sm };
      case "md":
        return { width: 48, height: 48, fontSize: fontSizes.lg };
      case "lg":
        return { width: 64, height: 64, fontSize: fontSizes.xxl };
      case "xl":
        return { width: 80, height: 80, fontSize: fontSizes.xxxl };
      default:
        return { width: 48, height: 48, fontSize: fontSizes.lg };
    }
  };

  const sizeStyle = getSizeStyle();
  const initials = name ? name.charAt(0).toUpperCase() : "?";

  return (
    <View style={[styles.avatar, sizeStyle, style]}>
      {source ? (
        <Image
          source={{ uri: source }}
          style={[styles.avatarImage, sizeStyle]}
          resizeMode="cover"
        />
      ) : (
        <View style={[styles.avatarPlaceholder, sizeStyle]}>
          <Text style={[styles.avatarText, { fontSize: sizeStyle.fontSize }]}>
            {initials}
          </Text>
        </View>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  // Card 样式
  card: {
    backgroundColor: colors.card,
    borderRadius: borderRadius.md,
  },
  // ListItem 样式
  listItem: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: colors.white,
    paddingVertical: spacing.md,
    paddingHorizontal: spacing.md,
    borderBottomWidth: 1,
    borderBottomColor: colors.gray100,
  },
  listItemLeft: {
    marginRight: spacing.md,
  },
  listItemContent: {
    flex: 1,
  },
  listItemTitle: {
    fontSize: fontSizes.md,
    color: colors.text,
    fontWeight: fontWeights.medium,
  },
  listItemSubtitle: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginTop: spacing.xs,
  },
  listItemRight: {
    flexDirection: "row",
    alignItems: "center",
    marginLeft: spacing.sm,
  },
  // ItemCard 样式
  itemCard: {
    backgroundColor: colors.white,
    borderRadius: borderRadius.md,
    overflow: "hidden",
    marginBottom: spacing.md,
    ...shadows.sm,
  },
  itemCardImage: {
    width: "100%",
    height: 180,
  },
  itemCardPlaceholder: {
    width: "100%",
    height: 180,
    backgroundColor: colors.gray100,
    justifyContent: "center",
    alignItems: "center",
  },
  itemCardContent: {
    padding: spacing.md,
  },
  itemCardTitle: {
    fontSize: fontSizes.lg,
    fontWeight: fontWeights.semibold,
    color: colors.text,
    marginBottom: spacing.xs,
  },
  itemCardDescription: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
    lineHeight: 20,
    marginBottom: spacing.sm,
  },
  itemCardFooter: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  itemCardTag: {
    flexDirection: "row",
    alignItems: "center",
    backgroundColor: colors.gray100,
    paddingHorizontal: spacing.sm,
    paddingVertical: spacing.xs,
    borderRadius: borderRadius.sm,
  },
  itemCardTagText: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginLeft: spacing.xs,
  },
  itemCardStats: {
    flexDirection: "row",
    alignItems: "center",
  },
  itemCardStatText: {
    fontSize: fontSizes.sm,
    color: colors.textSecondary,
    marginLeft: spacing.xs,
  },
  // Avatar 样式
  avatar: {
    borderRadius: borderRadius.full,
    overflow: "hidden",
  },
  avatarImage: {
    width: "100%",
    height: "100%",
  },
  avatarPlaceholder: {
    backgroundColor: colors.primary,
    justifyContent: "center",
    alignItems: "center",
  },
  avatarText: {
    color: colors.white,
    fontWeight: fontWeights.bold,
  },
});

export { Card, ListItem, ItemCard, Avatar };
