/**
 * 路由配置
 * 支持路由懒加载、权限控制
 */
import { lazy, Suspense } from 'react';
import { Navigate, useRoutes } from 'react-router-dom';
import { Spin } from 'antd';
import { LoadingOutlined } from '@ant-design/icons';
import MainLayout from '@/layouts/MainLayout';
import { AuthGuard, AdminGuard } from './guards';

// 懒加载页面组件
const Login = lazy(() => import('@/pages/Login'));
const Register = lazy(() => import('@/pages/Register'));
const Users = lazy(() => import('@/pages/Users'));
const Dashboard = lazy(() => import('@/pages/Dashboard'));

/**
 * 加载状态组件
 */
const PageLoading = () => (
  <div style={{ 
    display: 'flex', 
    alignItems: 'center', 
    justifyContent: 'center', 
    height: '100vh' 
  }}>
    <Spin indicator={<LoadingOutlined style={{ fontSize: 48 }} spin />} />
  </div>
);

/**
 * 懒加载包装器
 */
const LazyWrapper = ({ children }) => (
  <Suspense fallback={<PageLoading />}>
    {children}
  </Suspense>
);

/**
 * 路由配置
 */
const routes = [
  // 登录页 - 公开访问
  {
    path: '/login',
    element: (
      <AuthGuard requireAuth={false}>
        <LazyWrapper>
          <Login />
        </LazyWrapper>
      </AuthGuard>
    )
  },
  
  // 注册页 - 公开访问
  {
    path: '/register',
    element: (
      <AuthGuard requireAuth={false}>
        <LazyWrapper>
          <Register />
        </LazyWrapper>
      </AuthGuard>
    )
  },
  
  // 主布局 - 需要登录
  {
    path: '/',
    element: (
      <AuthGuard requireAuth={true}>
        <MainLayout />
      </AuthGuard>
    ),
    children: [
      // 默认重定向到仪表板
      {
        index: true,
        element: <Navigate to="/dashboard" replace />
      },
      
      // 仪表板
      {
        path: 'dashboard',
        element: (
          <LazyWrapper>
            <Dashboard />
          </LazyWrapper>
        )
      },
      
      // 用户管理 - 仅管理员
      {
        path: 'users',
        element: (
          <AdminGuard>
            <LazyWrapper>
              <Users />
            </LazyWrapper>
          </AdminGuard>
        )
      },
      
      // 404 页面
      {
        path: '*',
        element: <Navigate to="/dashboard" replace />
      }
    ]
  },
  
  // 根路径重定向
  {
    path: '*',
    element: <Navigate to="/login" replace />
  }
];

/**
 * 路由渲染组件
 */
const Router = () => {
  return useRoutes(routes);
};

export default Router;
