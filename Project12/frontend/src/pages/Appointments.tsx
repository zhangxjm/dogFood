import React, { useState, useEffect } from 'react'
import { Table, Button, Card, Tag, Modal, Form, Input, Select, DatePicker, TimePicker, message, Space } from 'antd'
import { PlusOutlined, EyeOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import api from '@/services/api'
import { Appointment, Resident, User } from '@/types'
import dayjs from 'dayjs'
import { useAuthStore } from '@/store/authStore'

const Appointments: React.FC = () => {
  const [appointments, setAppointments] = useState<Appointment[]>([])
  const [residents, setResidents] = useState<Resident[]>([])
  const [doctors, setDoctors] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [viewingRecord, setViewingRecord] = useState<Appointment | null>(null)
  const [form] = Form.useForm()
  const { user } = useAuthStore()

  const isAdminOrDoctor = user?.role === 'admin' || user?.role === 'doctor'

  useEffect(() => {
    fetchAppointments()
    fetchResidents()
    fetchDoctors()
  }, [])

  const fetchAppointments = async () => {
    setLoading(true)
    try {
      const response = await api.get('/appointments/')
      setAppointments(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch appointments:', error)
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

  const handleView = (record: Appointment) => {
    setViewingRecord(record)
    setModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const formattedValues = {
        ...values,
        appointment_date: values.appointment_date ? values.appointment_date.format('YYYY-MM-DD') : null,
        appointment_time: values.appointment_time ? values.appointment_time.format('HH:mm:ss') : null,
      }

      await api.post('/appointments/', formattedValues)
      message.success('创建成功')
      setModalVisible(false)
      fetchAppointments()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const getStatusColor = (status: string) => {
    const colorMap: Record<string, string> = {
      pending: 'orange',
      confirmed: 'blue',
      in_progress: 'processing',
      completed: 'success',
      cancelled: 'default',
      no_show: 'error',
    }
    return colorMap[status] || 'default'
  }

  const getAppointmentTypeColor = (type: string) => {
    const colorMap: Record<string, string> = {
      annual_checkup: 'blue',
      routine_check: 'green',
      follow_up: 'purple',
      special_exam: 'orange',
      vaccination: 'cyan',
    }
    return colorMap[type] || 'default'
  }

  const columns: ColumnsType<Appointment> = [
    {
      title: '居民',
      dataIndex: 'resident_name',
      key: 'resident_name',
    },
    {
      title: '预约类型',
      dataIndex: 'appointment_type_display',
      key: 'appointment_type',
      render: (_, record) => (
        <Tag color={getAppointmentTypeColor(record.appointment_type)}>
          {record.appointment_type_display}
        </Tag>
      ),
    },
    {
      title: '预约日期',
      dataIndex: 'appointment_date',
      key: 'appointment_date',
    },
    {
      title: '预约时间',
      dataIndex: 'appointment_time',
      key: 'appointment_time',
    },
    {
      title: '医生',
      dataIndex: 'doctor_name',
      key: 'doctor',
      render: (val) => val || '-',
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
        <h2 className="page-title">体检预约</h2>
        {isAdminOrDoctor && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增预约
          </Button>
        )}
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={appointments}
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
        title={viewingRecord ? '查看预约详情' : '新增预约'}
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
            <Form.Item label="预约类型">
              <Input value={viewingRecord.appointment_type_display} disabled />
            </Form.Item>
            <Form.Item label="预约日期">
              <Input value={viewingRecord.appointment_date} disabled />
            </Form.Item>
            <Form.Item label="预约时间">
              <Input value={viewingRecord.appointment_time} disabled />
            </Form.Item>
            <Form.Item label="医生">
              <Input value={viewingRecord.doctor_name || '-'} disabled />
            </Form.Item>
            <Form.Item label="科室">
              <Input value={viewingRecord.department || '-'} disabled />
            </Form.Item>
            <Form.Item label="预约说明">
              <Input.TextArea value={viewingRecord.description || '无'} disabled rows={2} />
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

            <Form.Item name="appointment_type" label="预约类型" initialValue="routine_check">
              <Select placeholder="请选择预约类型">
                <Select.Option value="annual_checkup">年度体检</Select.Option>
                <Select.Option value="routine_check">常规检查</Select.Option>
                <Select.Option value="follow_up">复诊</Select.Option>
                <Select.Option value="special_exam">专项检查</Select.Option>
                <Select.Option value="vaccination">疫苗接种</Select.Option>
              </Select>
            </Form.Item>

            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item name="appointment_date" label="预约日期" rules={[{ required: true, message: '请选择日期' }]} style={{ flex: 1 }}>
                <DatePicker style={{ width: '100%' }} placeholder="请选择日期" />
              </Form.Item>
              <Form.Item name="appointment_time" label="预约时间" rules={[{ required: true, message: '请选择时间' }]} style={{ flex: 1 }}>
                <TimePicker style={{ width: '100%' }} format="HH:mm" placeholder="请选择时间" />
              </Form.Item>
            </div>

            <Form.Item name="doctor" label="预约医生">
              <Select placeholder="请选择医生" allowClear>
                {doctors.map((d) => (
                  <Select.Option key={d.id} value={d.id}>
                    {d.username}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item name="department" label="科室">
              <Input placeholder="请输入科室" />
            </Form.Item>

            <Form.Item name="description" label="预约说明">
              <Input.TextArea rows={3} placeholder="请输入预约说明" />
            </Form.Item>
          </Form>
        )}
      </Modal>
    </div>
  )
}

export default Appointments
