import React, { useState } from 'react'
import { Form, Input, Button, Card, message } from 'antd'
import { UserOutlined, LockOutlined } from '@ant-design/icons'
import { useNavigate, Navigate } from 'react-router-dom'
import axios from 'axios'
import { useAuthStore } from '@/store/authStore'
import { TokenResponse, LoginParams } from '@/types'

const Login: React.FC = () => {
  const [loading, setLoading] = useState(false)
  const { setAuth, isAuthenticated } = useAuthStore()
  const navigate = useNavigate()

  if (isAuthenticated) {
    return <Navigate to="/dashboard" replace />
  }

  const onFinish = async (values: LoginParams) => {
    setLoading(true)
    try {
      const response = await axios.post<TokenResponse>('/api/users/login/', {
        username: values.username,
        password: values.password,
      })
      
      setAuth(response.data)
      message.success('登录成功')
      navigate('/dashboard')
    } catch (error: unknown) {
      if (axios.isAxiosError(error)) {
        const errorMessage = error.response?.data?.error || '登录失败，请检查用户名和密码'
        message.error(errorMessage)
      } else {
        message.error('登录失败')
      }
    } finally {
      setLoading(false)
    }
  }

  return (
    <div className="login-container">
      <Card className="login-card">
        <h2 className="login-title">社区健康管理系统</h2>
        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[{ required: true, message: '请输入用户名' }]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[{ required: true, message: '请输入密码' }]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              block
              size="large"
            >
              登录
            </Button>
          </Form.Item>
        </Form>
        <div style={{ textAlign: 'center', color: '#999', fontSize: '12px' }}>
          <p>管理员：admin / admin123456</p>
          <p>医生账号：doctor1 / doctor123</p>
          <p>居民账号：resident1 / resident123</p>
        </div>
      </Card>
    </div>
  )
}

export default Login
