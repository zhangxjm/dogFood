import React, { useState } from 'react'
import { Layout, Menu, Dropdown, Avatar, Button } from 'antd'
import type { MenuProps } from 'antd'
import { Outlet, useNavigate, useLocation } from 'react-router-dom'
import {
  DashboardOutlined, TeamOutlined, FileTextOutlined, CalendarOutlined,
  MedicineBoxOutlined, BellOutlined, HomeOutlined, UserOutlined,
  LogoutOutlined
} from '@ant-design/icons'
import { useAuthStore } from '@/store/authStore'

const { Header, Sider, Content } = Layout

const AppLayout: React.FC = () => {
  const [collapsed, setCollapsed] = useState(false)
  const { user, logout } = useAuthStore()
  const navigate = useNavigate()
  const location = useLocation()

  const getMenuItems = (): MenuProps['items'] => {
    const baseItems: MenuProps['items'] = [
      {
        key: '/dashboard',
        icon: <DashboardOutlined />,
        label: '仪表盘',
      },
      {
        key: '/health-records',
        icon: <FileTextOutlined />,
        label: '健康档案',
      },
      {
        key: '/appointments',
        icon: <CalendarOutlined />,
        label: '体检预约',
      },
      {
        key: '/medical-records',
        icon: <MedicineBoxOutlined />,
        label: '就诊记录',
      },
      {
        key: '/medication-reminders',
        icon: <BellOutlined />,
        label: '用药提醒',
      },
    ]

    if (user?.role === 'admin' || user?.role === 'doctor') {
      baseItems.splice(1, 0, {
        key: '/residents',
        icon: <TeamOutlined />,
        label: '居民管理',
      })
    }

    if (user?.role === 'admin') {
      baseItems.push(
        {
          key: '/communities',
          icon: <HomeOutlined />,
          label: '社区管理',
        },
        {
          key: '/users',
          icon: <UserOutlined />,
          label: '用户管理',
        }
      )
    }

    return baseItems
  }

  const handleMenuClick: MenuProps['onClick'] = ({ key }) => {
    navigate(key)
  }

  const handleLogout = () => {
    logout()
    navigate('/login')
  }

  const userDropdownItems: MenuProps['items'] = [
    {
      key: 'user-info',
      label: `${user?.username}`,
      disabled: true,
    },
    {
      key: 'role',
      label: `角色：${user?.role_display}`,
      disabled: true,
    },
    {
      type: 'divider',
    },
    {
      key: 'logout',
      icon: <LogoutOutlined />,
      label: '退出登录',
      onClick: handleLogout,
    },
  ]

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={(value) => setCollapsed(value)}
        theme="dark"
      >
        <div style={{
          height: '64px',
          display: 'flex',
          alignItems: 'center',
          justifyContent: 'center',
          color: '#fff',
          fontSize: collapsed ? '14px' : '18px',
          fontWeight: 'bold',
        }}>
          {collapsed ? '健康' : '社区健康管理'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[location.pathname]}
          items={getMenuItems()}
          onClick={handleMenuClick}
        />
      </Sider>
      <Layout>
        <Header style={{ padding: 0, background: '#001529' }}>
          <div className="header-content">
            <h1 className="header-title">社区健康管理系统</h1>
            <div className="user-info">
              <Dropdown menu={{ items: userDropdownItems }} placement="bottomRight">
                <Button type="text" style={{ color: '#fff', display: 'flex', alignItems: 'center', gap: '8px' }}>
                  <Avatar size="small" icon={<UserOutlined />} />
                  <span>{user?.username}</span>
                </Button>
              </Dropdown>
            </div>
          </div>
        </Header>
        <Content className="site-layout-background">
          <div className="site-layout-content">
            <Outlet />
          </div>
        </Content>
      </Layout>
    </Layout>
  )
}

export default AppLayout
