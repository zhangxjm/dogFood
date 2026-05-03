import React, { useState, useEffect } from 'react'
import { Table, Button, Card, Tag, Modal, Form, Input, Select, message, Space, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import api from '@/services/api'
import { Community, User } from '@/types'

const Communities: React.FC = () => {
  const [communities, setCommunities] = useState<Community[]>([])
  const [users, setUsers] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Community | null>(null)
  const [form] = Form.useForm()

  useEffect(() => {
    fetchCommunities()
    fetchUsers()
  }, [])

  const fetchCommunities = async () => {
    setLoading(true)
    try {
      const response = await api.get('/communities/')
      setCommunities(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch communities:', error)
    } finally {
      setLoading(false)
    }
  }

  const fetchUsers = async () => {
    try {
      const response = await api.get('/users/')
      const allUsers: User[] = response.data?.results || response.data || []
      setUsers(allUsers.filter((u) => u.role === 'admin'))
    } catch (error) {
      console.error('Failed to fetch users:', error)
    }
  }

  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: Community) => {
    setEditingRecord(record)
    form.setFieldsValue({
      ...record,
      manager: record.manager || undefined,
    })
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/communities/${id}/`)
      message.success('删除成功')
      fetchCommunities()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()

      if (editingRecord) {
        await api.patch(`/communities/${editingRecord.id}/`, values)
        message.success('更新成功')
      } else {
        await api.post('/communities/', values)
        message.success('创建成功')
      }

      setModalVisible(false)
      fetchCommunities()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      active: 'green',
      suspended: 'orange',
      closed: 'default',
    }
    return colorMap[status] || 'default'
  }

  const columns: ColumnsType<Community> = [
    {
      title: '社区编号',
      dataIndex: 'code',
      key: 'code',
    },
    {
      title: '社区名称',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '地址',
      dataIndex: 'full_address',
      key: 'address',
      ellipsis: true,
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '负责人',
      dataIndex: 'manager_name',
      key: 'manager',
    },
    {
      title: '居民数',
      dataIndex: 'resident_count',
      key: 'resident_count',
    },
    {
      title: '状态',
      dataIndex: 'status_display',
      key: 'status',
      render: (_, record) => (
        <Tag color={getStatusColor(record.status)}>{record.status_display}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Space>
          <Button type="link" icon={<EditOutlined />} onClick={() => handleEdit(record)}>
            编辑
          </Button>
          <Popconfirm
            title="确定要删除吗？"
            onConfirm={() => handleDelete(record.id)}
            okText="确定"
            cancelText="取消"
          >
            <Button type="link" danger icon={<DeleteOutlined />}>
              删除
            </Button>
          </Popconfirm>
        </Space>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">社区管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增社区
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={communities}
          rowKey="id"
          loading={loading}
          pagination={{
            pageSize: 20,
            showSizeChanger: true,
            showTotal: (total) => `共 ${total} 条记录`,
          }}
        />
      </Card>

      <Modal
        title={editingRecord ? '编辑社区' : '新增社区'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={700}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="code"
            label="社区编号"
            rules={[{ required: true, message: '请输入社区编号' }]}
          >
            <Input placeholder="请输入社区编号" />
          </Form.Item>

          <Form.Item
            name="name"
            label="社区名称"
            rules={[{ required: true, message: '请输入社区名称' }]}
          >
            <Input placeholder="请输入社区名称" />
          </Form.Item>

          <Form.Item name="province" label="省份">
            <Input placeholder="请输入省份" />
          </Form.Item>

          <Form.Item name="city" label="城市">
            <Input placeholder="请输入城市" />
          </Form.Item>

          <Form.Item name="district" label="区县">
            <Input placeholder="请输入区县" />
          </Form.Item>

          <Form.Item name="address" label="详细地址">
            <Input.TextArea rows={2} placeholder="请输入详细地址" />
          </Form.Item>

          <Form.Item name="phone" label="联系电话">
            <Input placeholder="请输入联系电话" />
          </Form.Item>

          <Form.Item name="manager" label="负责人">
            <Select placeholder="请选择负责人" allowClear>
              {users.map((u) => (
                <Select.Option key={u.id} value={u.id}>
                  {u.username}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="status" label="状态" initialValue="active">
            <Select placeholder="请选择状态">
              <Select.Option value="active">正常运营</Select.Option>
              <Select.Option value="suspended">暂停服务</Select.Option>
              <Select.Option value="closed">已关闭</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="description" label="描述">
            <Input.TextArea rows={2} placeholder="请输入社区简介或备注" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default Communities
