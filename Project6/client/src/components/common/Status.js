import React from "react";
import {
  View,
  Text,
  ActivityIndicator,
  StyleSheet,
  TouchableOpacity,
} from "react-native";
import { Ionicons } from "@expo/vector-icons";
import { colors, fontSizes, spacing, fontWeights } from "../../styles/global";

// 加载组件
const Loading = ({
  size = "large",
  color = colors.primary,
  message,
  style,
}) => {
  return (
    <View style={[styles.container, style]}>
      <ActivityIndicator size={size} color={color} />
      {message && <Text style={styles.message}>{message}</Text>}
    </View>
  );
};

// 空状态组件
const Empty = ({
  icon = "inbox-outline",
  title = "暂无数据",
  description,
  buttonText,
  onButtonPress,
  style,
}) => {
  return (
    <View style={[styles.emptyContainer, style]}>
      <Ionicons name={icon} size={64} color={colors.gray300} />
      <Text style={styles.emptyTitle}>{title}</Text>
      {description && (
        <Text style={styles.emptyDescription}>{description}</Text>
      )}
      {buttonText && onButtonPress && (
        <TouchableOpacity
          style={styles.emptyButton}
          onPress={onButtonPress}
          activeOpacity={0.7}
        >
          <Text style={styles.emptyButtonText}>{buttonText}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};

// 错误状态组件
const Error = ({
  icon = "alert-circle-outline",
  title = "出错了",
  description = "请稍后重试",
  buttonText = "重试",
  onButtonPress,
  style,
}) => {
  return (
    <View style={[styles.errorContainer, style]}>
      <Ionicons name={icon} size={64} color={colors.error} />
      <Text style={styles.errorTitle}>{title}</Text>
      {description && (
        <Text style={styles.errorDescription}>{description}</Text>
      )}
      {buttonText && onButtonPress && (
        <TouchableOpacity
          style={styles.errorButton}
          onPress={onButtonPress}
          activeOpacity={0.7}
        >
          <Text style={styles.errorButtonText}>{buttonText}</Text>
        </TouchableOpacity>
      )}
    </View>
  );
};

const styles = StyleSheet.create({
  // Loading 样式
  container: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: spacing.lg,
  },
  message: {
    marginTop: spacing.md,
    fontSize: fontSizes.md,
    color: colors.textSecondary,
    textAlign: "center",
  },
  // Empty 样式
  emptyContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: spacing.xl,
  },
  emptyTitle: {
    fontSize: fontSizes.lg,
    fontWeight: fontWeights.medium,
    color: colors.text,
    marginTop: spacing.md,
    textAlign: "center",
  },
  emptyDescription: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
    marginTop: spacing.sm,
    textAlign: "center",
  },
  emptyButton: {
    marginTop: spacing.lg,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.lg,
    borderWidth: 1,
    borderColor: colors.primary,
    borderRadius: 8,
  },
  emptyButtonText: {
    fontSize: fontSizes.md,
    color: colors.primary,
    fontWeight: fontWeights.medium,
  },
  // Error 样式
  errorContainer: {
    flex: 1,
    justifyContent: "center",
    alignItems: "center",
    padding: spacing.xl,
  },
  errorTitle: {
    fontSize: fontSizes.lg,
    fontWeight: fontWeights.medium,
    color: colors.error,
    marginTop: spacing.md,
    textAlign: "center",
  },
  errorDescription: {
    fontSize: fontSizes.md,
    color: colors.textSecondary,
    marginTop: spacing.sm,
    textAlign: "center",
  },
  errorButton: {
    marginTop: spacing.lg,
    paddingVertical: spacing.sm,
    paddingHorizontal: spacing.lg,
    backgroundColor: colors.primary,
    borderRadius: 8,
  },
  errorButtonText: {
    fontSize: fontSizes.md,
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
});

export { Loading, Empty, Error };
