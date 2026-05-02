/**
 * 仪表板页面
 */
import { Card, Row, Col, Statistic, Descriptions, Tag } from 'antd';
import {
  UserOutlined,
  TeamOutlined,
  LockOutlined,
  ClockCircleOutlined
} from '@ant-design/icons';
import { useUserStore } from '@/stores/userStore';
import './Dashboard.css';

const Dashboard = () => {
  const { user } = useUserStore();

  // 格式化日期
  const formatDate = (dateString) => {
    if (!dateString) return '-';
    return new Date(dateString).toLocaleString('zh-CN');
  };

  // 获取角色标签
  const getRoleTag = (role) => {
    const isAdmin = role === 'admin';
    return (
      <Tag color={isAdmin ? 'green' : 'blue'}>
        {isAdmin ? '管理员' : '普通用户'}
      </Tag>
    );
  };

  // 获取状态标签
  const getStatusTag = (status) => {
    return (
      <Tag color={status ? 'success' : 'error'}>
        {status ? '正常' : '禁用'}
      </Tag>
    );
  };

  return (
    <div className="dashboard-container">
      {/* 页面标题 */}
      <div className="page-header">
        <h2>仪表板</h2>
      </div>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: 24 }}>
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="用户状态"
              value={user?.status ? '正常' : '禁用'}
              prefix={user?.status ? <LockOutlined style={{ color: '#52c41a' }} /> : <LockOutlined style={{ color: '#ff4d4f' }} />}
              valueStyle={{ color: user?.status ? '#52c41a' : '#ff4d4f' }}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="用户角色"
              value={user?.role === 'admin' ? '管理员' : '普通用户'}
              prefix={<TeamOutlined />}
              valueStyle={{ color: user?.role === 'admin' ? '#52c41a' : '#1890ff' }}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="用户名"
              value={user?.username || '-'}
              prefix={<UserOutlined />}
            />
          </Card>
        </Col>
        
        <Col xs={24} sm={12} lg={6}>
          <Card hoverable>
            <Statistic
              title="最后登录"
              value={formatDate(user?.lastLoginAt)}
              prefix={<ClockCircleOutlined />}
              valueStyle={{ fontSize: 14 }}
            />
          </Card>
        </Col>
      </Row>

      {/* 用户信息卡片 */}
      <Card title="用户信息" className="info-card">
        <Descriptions column={1} bordered size="middle">
          <Descriptions.Item label="用户ID">
            {user?.id || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="用户名">
            {user?.username || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="邮箱">
            {user?.email || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="真实姓名">
            {user?.realName || '-'}
          </Descriptions.Item>
          
          <Descriptions.Item label="角色">
            {getRoleTag(user?.role)}
          </Descriptions.Item>
          
          <Descriptions.Item label="状态">
            {getStatusTag(user?.status)}
          </Descriptions.Item>
          
          <Descriptions.Item label="注册时间">
            {formatDate(user?.createdAt)}
          </Descriptions.Item>
          
          <Descriptions.Item label="最后更新">
            {formatDate(user?.updatedAt)}
          </Descriptions.Item>
          
          <Descriptions.Item label="最后登录">
            {formatDate(user?.lastLoginAt)}
          </Descriptions.Item>
        </Descriptions>
      </Card>

      {/* 提示信息 */}
      {user?.role === 'admin' && (
        <Card title="系统提示" style={{ marginTop: 24 }}>
          <p style={{ margin: 0, color: 'rgba(0, 0, 0, 0.65)' }}>
            您是管理员，可以通过左侧菜单的"用户管理"功能对用户进行增删改查操作。
          </p>
        </Card>
      )}
    </div>
  );
};

export default Dashboard;
