import React, { useState, useEffect } from 'react'
import { Table, Button, Card, Tag, Modal, Form, Input, Select, DatePicker, message, Space, Popconfirm } from 'antd'
import { PlusOutlined, EditOutlined, DeleteOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import api from '@/services/api'
import { Resident, Community } from '@/types'
import dayjs from 'dayjs'

const Residents: React.FC = () => {
  const [residents, setResidents] = useState<Resident[]>([])
  const [communities, setCommunities] = useState<Community[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [editingRecord, setEditingRecord] = useState<Resident | null>(null)
  const [form] = Form.useForm()

  useEffect(() => {
    fetchResidents()
    fetchCommunities()
  }, [])

  const fetchResidents = async () => {
    setLoading(true)
    try {
      const response = await api.get('/residents/')
      setResidents(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch residents:', error)
    } finally {
      setLoading(false)
    }
  }

  const fetchCommunities = async () => {
    try {
      const response = await api.get('/communities/')
      setCommunities(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch communities:', error)
    }
  }

  const handleAdd = () => {
    setEditingRecord(null)
    form.resetFields()
    setModalVisible(true)
  }

  const handleEdit = (record: Resident) => {
    setEditingRecord(record)
    form.setFieldsValue({
      ...record,
      birth_date: record.birth_date ? dayjs(record.birth_date) : null,
      community: record.community || undefined,
    })
    setModalVisible(true)
  }

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/residents/${id}/`)
      message.success('删除成功')
      fetchResidents()
    } catch (error) {
      message.error('删除失败')
    }
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const formattedValues = {
        ...values,
        birth_date: values.birth_date ? values.birth_date.format('YYYY-MM-DD') : null,
      }

      if (editingRecord) {
        await api.patch(`/residents/${editingRecord.id}/`, formattedValues)
        message.success('更新成功')
      } else {
        await api.post('/residents/', formattedValues)
        message.success('创建成功')
      }

      setModalVisible(false)
      fetchResidents()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const getGenderColor = (gender: string) => {
    const colorMap: Record<string, string> = {
      male: 'blue',
      female: 'magenta',
      unknown: 'default',
    }
    return colorMap[gender] || 'default'
  }

  const columns: ColumnsType<Resident> = [
    {
      title: '姓名',
      dataIndex: 'name',
      key: 'name',
    },
    {
      title: '性别',
      dataIndex: 'gender_display',
      key: 'gender',
      render: (_, record) => (
        <Tag color={getGenderColor(record.gender)}>{record.gender_display}</Tag>
      ),
    },
    {
      title: '年龄',
      dataIndex: 'age',
      key: 'age',
    },
    {
      title: '联系电话',
      dataIndex: 'phone',
      key: 'phone',
    },
    {
      title: '社区',
      dataIndex: 'community_name',
      key: 'community',
    },
    {
      title: '是否参保',
      dataIndex: 'is_insured',
      key: 'is_insured',
      render: (val) => <Tag color={val ? 'green' : 'orange'}>{val ? '是' : '否'}</Tag>,
    },
    {
      title: '状态',
      dataIndex: 'is_active',
      key: 'is_active',
      render: (val) => <Tag color={val ? 'blue' : 'default'}>{val ? '在籍' : '已迁出'}</Tag>,
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
        <h2 className="page-title">居民管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增居民
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={residents}
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
        title={editingRecord ? '编辑居民' : '新增居民'}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="name"
            label="姓名"
            rules={[{ required: true, message: '请输入姓名' }]}
          >
            <Input placeholder="请输入姓名" />
          </Form.Item>

          <Form.Item
            name="id_card"
            label="身份证号"
            rules={[{ required: true, message: '请输入身份证号' }]}
          >
            <Input placeholder="请输入身份证号" />
          </Form.Item>

          <Form.Item
            name="gender"
            label="性别"
            rules={[{ required: true, message: '请选择性别' }]}
          >
            <Select placeholder="请选择性别">
              <Select.Option value="male">男</Select.Option>
              <Select.Option value="female">女</Select.Option>
              <Select.Option value="unknown">未知</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="birth_date" label="出生日期">
            <DatePicker style={{ width: '100%' }} placeholder="请选择出生日期" />
          </Form.Item>

          <Form.Item name="phone" label="联系电话">
            <Input placeholder="请输入联系电话" />
          </Form.Item>

          <Form.Item name="emergency_contact" label="紧急联系人">
            <Input placeholder="请输入紧急联系人" />
          </Form.Item>

          <Form.Item name="emergency_phone" label="紧急联系电话">
            <Input placeholder="请输入紧急联系电话" />
          </Form.Item>

          <Form.Item name="community" label="所属社区">
            <Select placeholder="请选择社区" allowClear>
              {communities.map((c) => (
                <Select.Option key={c.id} value={c.id}>
                  {c.name}
                </Select.Option>
              ))}
            </Select>
          </Form.Item>

          <Form.Item name="address" label="详细地址">
            <Input.TextArea rows={2} placeholder="请输入详细地址" />
          </Form.Item>

          <Form.Item name="is_insured" label="是否参保" initialValue={true}>
            <Select placeholder="请选择">
              <Select.Option value={true}>是</Select.Option>
              <Select.Option value={false}>否</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="allergy_history" label="过敏史">
            <Input.TextArea rows={2} placeholder="请输入过敏史" />
          </Form.Item>

          <Form.Item name="chronic_diseases" label="慢性病史">
            <Input.TextArea rows={2} placeholder="请输入慢性病史" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  )
}

export default Residents
