/**
 * 主布局组件
 * 包含侧边栏、顶部导航和内容区域
 */
import { useState } from 'react';
import { Layout, Menu, Avatar, Dropdown, Button, theme } from 'antd';
import {
  MenuFoldOutlined,
  MenuUnfoldOutlined,
  DashboardOutlined,
  UserOutlined,
  LogoutOutlined,
  SettingOutlined
} from '@ant-design/icons';
import { Outlet, useNavigate, useLocation } from 'react-router-dom';
import { useUserStore } from '@/stores/userStore';
import './MainLayout.css';

const { Header, Sider, Content } = Layout;

const MainLayout = () => {
  const [collapsed, setCollapsed] = useState(false);
  const {
    token: { colorBgContainer, borderRadiusLG }
  } = theme.useToken();
  const navigate = useNavigate();
  const location = useLocation();
  const { user, logout } = useUserStore();

  // 菜单项
  const menuItems = [
    {
      key: '/dashboard',
      icon: <DashboardOutlined />,
      label: '仪表板'
    },
    // 仅管理员显示用户管理
    ...(user?.role === 'admin'
      ? [
          {
            key: '/users',
            icon: <UserOutlined />,
            label: '用户管理'
          }
        ]
      : [])
  ];

  // 用户下拉菜单
  const userMenuItems = [
    {
      key: 'profile',
      icon: <UserOutlined />,
      label: '个人信息',
      disabled: true
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '设置',
      disabled: true
    },
    {
      type: 'divider'
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      danger: true
    }
  ];

  // 处理菜单点击
  const handleMenuClick = ({ key }) => {
    navigate(key);
  };

  // 处理用户菜单点击
  const handleUserMenuClick = ({ key }) => {
    if (key === 'logout') {
      logout();
      navigate('/login');
    }
  };

  // 获取当前选中的菜单项
  const getSelectedKeys = () => {
    const path = location.pathname;
    return [path];
  };

  return (
    <Layout className="main-layout">
      {/* 侧边栏 */}
      <Sider trigger={null} collapsible collapsed={collapsed} theme="dark">
        <div className="logo">
          {collapsed ? (
            <span className="logo-text-collapsed">FS</span>
          ) : (
            <span className="logo-text">全栈管理系统</span>
          )}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={getSelectedKeys()}
          items={menuItems}
          onClick={handleMenuClick}
        />
      </Sider>

      <Layout>
        {/* 顶部导航栏 */}
        <Header style={{ background: colorBgContainer, padding: '0 24px' }}>
          <div className="header-left">
            <Button
              type="text"
              icon={collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
              onClick={() => setCollapsed(!collapsed)}
              style={{
                fontSize: '16px',
                width: 64,
                height: 64
              }}
            />
          </div>

          <div className="header-right">
            <Dropdown
              menu={{
                items: userMenuItems,
                onClick: handleUserMenuClick
              }}
              placement="bottomRight"
            >
              <div className="user-dropdown">
                <Avatar
                  size="large"
                  icon={<UserOutlined />}
                  className="user-avatar"
                />
                <span className="user-name">{user?.realName || user?.username}</span>
              </div>
            </Dropdown>
          </div>
        </Header>

        {/* 内容区域 */}
        <Content
          style={{
            margin: '24px',
            padding: 24,
            minHeight: 280,
            background: colorBgContainer,
            borderRadius: borderRadiusLG
          }}
        >
          <Outlet />
        </Content>
      </Layout>
    </Layout>
  );
};

export default MainLayout;
