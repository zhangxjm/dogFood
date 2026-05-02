import React from "react";
import {
  TouchableOpacity,
  Text,
  StyleSheet,
  ActivityIndicator,
  View,
} from "react-native";
import {
  colors,
  fontSizes,
  fontWeights,
  borderRadius,
  spacing,
} from "../../styles/global";

const Button = ({
  title,
  onPress,
  variant = "primary",
  size = "md",
  disabled = false,
  loading = false,
  fullWidth = false,
  style,
  textStyle,
  leftIcon,
  rightIcon,
  ...props
}) => {
  // 按钮样式变体
  const getVariantStyle = () => {
    switch (variant) {
      case "primary":
        return {
          container: styles.primaryContainer,
          text: styles.primaryText,
        };
      case "secondary":
        return {
          container: styles.secondaryContainer,
          text: styles.secondaryText,
        };
      case "outline":
        return {
          container: styles.outlineContainer,
          text: styles.outlineText,
        };
      case "ghost":
        return {
          container: styles.ghostContainer,
          text: styles.ghostText,
        };
      case "danger":
        return {
          container: styles.dangerContainer,
          text: styles.dangerText,
        };
      default:
        return {
          container: styles.primaryContainer,
          text: styles.primaryText,
        };
    }
  };

  // 按钮尺寸
  const getSizeStyle = () => {
    switch (size) {
      case "sm":
        return {
          container: styles.containerSm,
          text: styles.textSm,
        };
      case "lg":
        return {
          container: styles.containerLg,
          text: styles.textLg,
        };
      default:
        return {
          container: styles.containerMd,
          text: styles.textMd,
        };
    }
  };

  const variantStyle = getVariantStyle();
  const sizeStyle = getSizeStyle();

  return (
    <TouchableOpacity
      onPress={onPress}
      disabled={disabled || loading}
      activeOpacity={0.7}
      style={[
        styles.container,
        sizeStyle.container,
        variantStyle.container,
        fullWidth && styles.fullWidth,
        (disabled || loading) && styles.disabled,
        style,
      ]}
      {...props}
    >
      {loading ? (
        <ActivityIndicator
          size="small"
          color={
            variant === "outline" || variant === "ghost"
              ? colors.primary
              : colors.white
          }
        />
      ) : (
        <View style={styles.content}>
          {leftIcon}
          <Text
            style={[styles.text, sizeStyle.text, variantStyle.text, textStyle]}
          >
            {title}
          </Text>
          {rightIcon}
        </View>
      )}
    </TouchableOpacity>
  );
};

const styles = StyleSheet.create({
  container: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
  },
  content: {
    flexDirection: "row",
    alignItems: "center",
    justifyContent: "center",
    gap: spacing.xs,
  },
  fullWidth: {
    width: "100%",
  },
  disabled: {
    opacity: 0.6,
  },
  // 尺寸样式
  containerSm: {
    height: 36,
    paddingHorizontal: spacing.sm,
    borderRadius: borderRadius.sm,
  },
  containerMd: {
    height: 48,
    paddingHorizontal: spacing.md,
    borderRadius: borderRadius.md,
  },
  containerLg: {
    height: 56,
    paddingHorizontal: spacing.lg,
    borderRadius: borderRadius.lg,
  },
  textSm: {
    fontSize: fontSizes.sm,
  },
  textMd: {
    fontSize: fontSizes.lg,
  },
  textLg: {
    fontSize: fontSizes.xl,
  },
  // 变体样式 - Primary
  primaryContainer: {
    backgroundColor: colors.primary,
  },
  primaryText: {
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
  // Secondary
  secondaryContainer: {
    backgroundColor: colors.secondary,
  },
  secondaryText: {
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
  // Outline
  outlineContainer: {
    backgroundColor: "transparent",
    borderWidth: 1,
    borderColor: colors.primary,
  },
  outlineText: {
    color: colors.primary,
    fontWeight: fontWeights.medium,
  },
  // Ghost
  ghostContainer: {
    backgroundColor: "transparent",
  },
  ghostText: {
    color: colors.primary,
    fontWeight: fontWeights.medium,
  },
  // Danger
  dangerContainer: {
    backgroundColor: colors.error,
  },
  dangerText: {
    color: colors.white,
    fontWeight: fontWeights.medium,
  },
});

export default Button;
