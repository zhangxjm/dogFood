/**
 * 路由守卫组件
 * 用于权限控制
 */
import { useEffect, useState } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { Spin } from 'antd';
import { useUserStore } from '@/stores/userStore';

/**
 * 认证守卫
 * 控制页面是否需要登录才能访问
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - 子组件
 * @param {boolean} props.requireAuth - 是否需要认证
 */
export const AuthGuard = ({ children, requireAuth = true }) => {
  const { token, user, fetchCurrentUser } = useUserStore();
  const [loading, setLoading] = useState(false);
  const location = useLocation();

  useEffect(() => {
    // 如果有 token 但没有用户信息，尝试获取
    if (token && !user) {
      setLoading(true);
      fetchCurrentUser()
        .catch(() => {
          // 获取失败，token 可能无效，由 store 处理
        })
        .finally(() => {
          setLoading(false);
        });
    }
  }, [token, user, fetchCurrentUser]);

  // 加载中
  if (loading) {
    return (
      <div style={{ 
        display: 'flex', 
        alignItems: 'center', 
        justifyContent: 'center', 
        height: '100vh' 
      }}>
        <Spin size="large" />
      </div>
    );
  }

  // 需要认证但未登录
  if (requireAuth && !token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 不需要认证但已登录（如登录页、注册页），重定向到首页
  if (!requireAuth && token) {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

/**
 * 管理员守卫
 * 控制页面是否需要管理员权限才能访问
 * 
 * @param {Object} props
 * @param {React.ReactNode} props.children - 子组件
 */
export const AdminGuard = ({ children }) => {
  const { user, token } = useUserStore();
  const location = useLocation();

  // 未登录
  if (!token) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  // 已登录但不是管理员
  if (user && user.role !== 'admin') {
    return <Navigate to="/dashboard" replace />;
  }

  return children;
};

export default {
  AuthGuard,
  AdminGuard
};
