import React, { useState, useEffect } from 'react'
import { Table, Button, Card, Tag, Modal, Form, Input, Select, DatePicker, message, Space } from 'antd'
import { PlusOutlined, EyeOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import api from '@/services/api'
import { MedicationReminder, Resident, User } from '@/types'
import dayjs from 'dayjs'
import { useAuthStore } from '@/store/authStore'

const MedicationReminders: React.FC = () => {
  const [reminders, setReminders] = useState<MedicationReminder[]>([])
  const [residents, setResidents] = useState<Resident[]>([])
  const [doctors, setDoctors] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [viewingRecord, setViewingRecord] = useState<MedicationReminder | null>(null)
  const [form] = Form.useForm()
  const { user } = useAuthStore()

  const isAdminOrDoctor = user?.role === 'admin' || user?.role === 'doctor'

  useEffect(() => {
    fetchReminders()
    fetchResidents()
    fetchDoctors()
  }, [])

  const fetchReminders = async () => {
    setLoading(true)
    try {
      const response = await api.get('/medication-reminders/')
      setReminders(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch medication reminders:', error)
    } finally {
      setLoading(false)
    }
  }

  const fetchResidents = async () => {
    try {
      const response = await api.get('/residents/')
      setResidents(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch residents:', error)
    }
  }

  const fetchDoctors = async () => {
    try {
      const response = await api.get('/users/')
      const allUsers = response.data?.results || response.data || []
      setDoctors(allUsers.filter((u: User) => u.role === 'doctor'))
    } catch (error) {
      console.error('Failed to fetch doctors:', error)
    }
  }

  const handleAdd = () => {
    form.resetFields()
    setViewingRecord(null)
    setModalVisible(true)
  }

  const handleView = (record: MedicationReminder) => {
    setViewingRecord(record)
    setModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const formattedValues = {
        ...values,
        start_date: values.start_date ? values.start_date.format('YYYY-MM-DD') : null,
        end_date: values.end_date ? values.end_date.format('YYYY-MM-DD') : null,
      }

      await api.post('/medication-reminders/', formattedValues)
      message.success('创建成功')
      setModalVisible(false)
      fetchReminders()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      active: 'green',
      paused: 'orange',
      completed: 'blue',
      cancelled: 'default',
    }
    return colorMap[status] || 'default'
  }

  const columns: ColumnsType<MedicationReminder> = [
    {
      title: '居民',
      dataIndex: 'resident_name',
      key: 'resident_name',
    },
    {
      title: '药品名称',
      dataIndex: 'medication_name',
      key: 'medication_name',
    },
    {
      title: '药品类型',
      dataIndex: 'medication_type',
      key: 'medication_type',
      render: (val) => val || '-',
    },
    {
      title: '剂量',
      dataIndex: 'dosage',
      key: 'dosage',
    },
    {
      title: '频次',
      dataIndex: 'frequency_display',
      key: 'frequency',
    },
    {
      title: '提醒时间',
      dataIndex: 'reminder_times',
      key: 'reminder_times',
    },
    {
      title: '开始日期',
      dataIndex: 'start_date',
      key: 'start_date',
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
        <Button type="link" icon={<EyeOutlined />} onClick={() => handleView(record)}>
          查看详情
        </Button>
      ),
    },
  ]

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">用药提醒</h2>
        {isAdminOrDoctor && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增用药提醒
          </Button>
        )}
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={reminders}
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
        title={viewingRecord ? '查看用药提醒' : '新增用药提醒'}
        open={modalVisible}
        onOk={viewingRecord ? undefined : handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        okText="确定"
        cancelText="取消"
        footer={viewingRecord ? null : undefined}
      >
        {viewingRecord ? (
          <Form layout="vertical">
            <Form.Item label="居民">
              <Input value={viewingRecord.resident_name} disabled />
            </Form.Item>
            <Form.Item label="药品名称">
              <Input value={viewingRecord.medication_name} disabled />
            </Form.Item>
            <Form.Item label="药品类型">
              <Input value={viewingRecord.medication_type || '-'} disabled />
            </Form.Item>
            <Form.Item label="剂量">
              <Input value={viewingRecord.dosage} disabled />
            </Form.Item>
            <Form.Item label="频次">
              <Input value={viewingRecord.frequency_display} disabled />
            </Form.Item>
            <Form.Item label="提醒时间">
              <Input value={viewingRecord.reminder_times} disabled />
            </Form.Item>
            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item label="开始日期" style={{ flex: 1 }}>
                <Input value={viewingRecord.start_date} disabled />
              </Form.Item>
              <Form.Item label="结束日期" style={{ flex: 1 }}>
                <Input value={viewingRecord.end_date || '长期'} disabled />
              </Form.Item>
            </div>
            <Form.Item label="用药说明">
              <Input.TextArea value={viewingRecord.instructions || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="注意事项">
              <Input.TextArea value={viewingRecord.precautions || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="状态">
              <Tag color={getStatusColor(viewingRecord.status)}>
                {viewingRecord.status_display}
              </Tag>
            </Form.Item>
          </Form>
        ) : (
          <Form form={form} layout="vertical">
            <Form.Item name="resident" label="居民" rules={[{ required: true, message: '请选择居民' }]}>
              <Select placeholder="请选择居民">
                {residents.map((r) => (
                  <Select.Option key={r.id} value={r.id}>
                    {r.name}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item name="medication_name" label="药品名称" rules={[{ required: true, message: '请输入药品名称' }]}>
              <Input placeholder="请输入药品名称" />
            </Form.Item>

            <Form.Item name="medication_type" label="药品类型">
              <Input placeholder="请输入药品类型（如：降压药、降糖药等）" />
            </Form.Item>

            <Form.Item name="dosage" label="剂量" rules={[{ required: true, message: '请输入剂量' }]}>
              <Input placeholder="请输入剂量（如：1片、10mg等）" />
            </Form.Item>

            <Form.Item name="frequency" label="用药频次" initialValue="once">
              <Select placeholder="请选择用药频次">
                <Select.Option value="once">每日一次</Select.Option>
                <Select.Option value="twice">每日两次</Select.Option>
                <Select.Option value="three_times">每日三次</Select.Option>
                <Select.Option value="four_times">每日四次</Select.Option>
                <Select.Option value="as_needed">按需</Select.Option>
                <Select.Option value="custom">自定义</Select.Option>
              </Select>
            </Form.Item>

            <Form.Item name="reminder_times" label="提醒时间" rules={[{ required: true, message: '请输入提醒时间' }]} help="用逗号分隔，如：08:00,12:00,20:00">
              <Input placeholder="08:00,12:00,20:00" />
            </Form.Item>

            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item name="start_date" label="开始日期" rules={[{ required: true, message: '请选择开始日期' }]} style={{ flex: 1 }}>
                <DatePicker style={{ width: '100%' }} placeholder="请选择开始日期" />
              </Form.Item>
              <Form.Item name="end_date" label="结束日期" help="为空表示长期用药" style={{ flex: 1 }}>
                <DatePicker style={{ width: '100%' }} placeholder="请选择结束日期" />
              </Form.Item>
            </div>

            <Form.Item name="instructions" label="用药说明">
              <Input.TextArea rows={2} placeholder="请输入用药说明" />
            </Form.Item>

            <Form.Item name="precautions" label="注意事项">
              <Input.TextArea rows={2} placeholder="请输入注意事项" />
            </Form.Item>

            <Form.Item name="prescribing_doctor" label="开单医生">
              <Select placeholder="请选择医生" allowClear>
                {doctors.map((d) => (
                  <Select.Option key={d.id} value={d.id}>
                    {d.username}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>
          </Form>
        )}
      </Modal>
    </div>
  )
}

export default MedicationReminders
