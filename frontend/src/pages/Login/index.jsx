/**
 * 登录页面
 */
import { useState } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { useUserStore } from '@/stores/userStore';
import './Login.css';

const Login = () => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { login } = useUserStore();

  /**
   * 处理登录提交
   */
  const handleSubmit = async (values) => {
    const { username, password } = values;
    
    setLoading(true);
    
    try {
      await login(username, password);
      message.success('登录成功');
      navigate('/dashboard');
    } catch (error) {
      // 错误已在 request 拦截器中处理
      console.error('登录失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="login-container">
      <Card className="login-card" title="用户登录">
        <Form
          form={form}
          name="login"
          onFinish={handleSubmit}
          autoComplete="off"
          size="large"
        >
          {/* 用户名 */}
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, message: '用户名至少 3 个字符' }
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入用户名"
              disabled={loading}
            />
          </Form.Item>

          {/* 密码 */}
          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码' },
              { min: 6, message: '密码至少 6 个字符' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请输入密码"
              disabled={loading}
            />
          </Form.Item>

          {/* 登录按钮 */}
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={loading}
              size="large"
            >
              登录
            </Button>
          </Form.Item>

          {/* 注册链接 */}
          <Form.Item style={{ marginBottom: 0, textAlign: 'center' }}>
            <span style={{ color: 'rgba(0, 0, 0, 0.45)' }}>还没有账号？</span>
            <Link to="/register" style={{ marginLeft: 4 }}>
              立即注册
            </Link>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Login;
