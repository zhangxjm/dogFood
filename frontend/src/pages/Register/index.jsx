/**
 * 注册页面
 */
import { useState } from 'react';
import { Form, Input, Button, Card, message } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { useUserStore } from '@/stores/userStore';
import './Register.css';

const Register = () => {
  const [loading, setLoading] = useState(false);
  const [form] = Form.useForm();
  const navigate = useNavigate();
  const { register } = useUserStore();

  /**
   * 处理注册提交
   */
  const handleSubmit = async (values) => {
    const { username, email, password, realName } = values;
    
    setLoading(true);
    
    try {
      await register({
        username,
        email,
        password,
        realName: realName || username
      });
      
      message.success('注册成功');
      navigate('/dashboard');
    } catch (error) {
      // 错误已在 request 拦截器中处理
      console.error('注册失败:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="register-container">
      <Card className="register-card" title="用户注册">
        <Form
          form={form}
          name="register"
          onFinish={handleSubmit}
          autoComplete="off"
          size="large"
        >
          {/* 用户名 */}
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名' },
              { min: 3, message: '用户名至少 3 个字符' },
              { max: 20, message: '用户名最多 20 个字符' },
              { 
                pattern: /^[a-zA-Z0-9_]+$/, 
                message: '用户名只能包含字母、数字和下划线' 
              }
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入用户名 (3-20个字符，字母/数字/下划线)"
              disabled={loading}
            />
          </Form.Item>

          {/* 邮箱 */}
          <Form.Item
            name="email"
            rules={[
              { required: true, message: '请输入邮箱' },
              { type: 'email', message: '请输入有效的邮箱地址' }
            ]}
          >
            <Input
              prefix={<MailOutlined />}
              placeholder="请输入邮箱地址"
              disabled={loading}
            />
          </Form.Item>

          {/* 真实姓名 */}
          <Form.Item
            name="realName"
            rules={[
              { max: 50, message: '真实姓名最多 50 个字符' }
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="请输入真实姓名 (可选)"
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
              placeholder="请输入密码 (至少 6 个字符)"
              disabled={loading}
            />
          </Form.Item>

          {/* 确认密码 */}
          <Form.Item
            name="confirmPassword"
            dependencies={['password']}
            rules={[
              { required: true, message: '请确认密码' },
              ({ getFieldValue }) => ({
                validator(_, value) {
                  if (!value || getFieldValue('password') === value) {
                    return Promise.resolve();
                  }
                  return Promise.reject(new Error('两次输入的密码不一致'));
                }
              })
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="请再次输入密码"
              disabled={loading}
            />
          </Form.Item>

          {/* 注册按钮 */}
          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              block
              loading={loading}
              size="large"
            >
              注册
            </Button>
          </Form.Item>

          {/* 登录链接 */}
          <Form.Item style={{ marginBottom: 0, textAlign: 'center' }}>
            <span style={{ color: 'rgba(0, 0, 0, 0.45)' }}>已有账号？</span>
            <Link to="/login" style={{ marginLeft: 4 }}>
              立即登录
            </Link>
          </Form.Item>
        </Form>
      </Card>
    </div>
  );
};

export default Register;
