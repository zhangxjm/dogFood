import React, { useState, useEffect } from 'react'
import { Table, Button, Card, Tag, Modal, Form, Input, Select, DatePicker, InputNumber, message, Space } from 'antd'
import { PlusOutlined, EyeOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import api from '@/services/api'
import { HealthRecord, Resident, User } from '@/types'
import dayjs from 'dayjs'
import { useAuthStore } from '@/store/authStore'

const HealthRecords: React.FC = () => {
  const [records, setRecords] = useState<HealthRecord[]>([])
  const [residents, setResidents] = useState<Resident[]>([])
  const [doctors, setDoctors] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [viewingRecord, setViewingRecord] = useState<HealthRecord | null>(null)
  const [form] = Form.useForm()
  const { user } = useAuthStore()

  const isAdminOrDoctor = user?.role === 'admin' || user?.role === 'doctor'

  useEffect(() => {
    fetchRecords()
    fetchResidents()
    fetchDoctors()
  }, [])

  const fetchRecords = async () => {
    setLoading(true)
    try {
      const response = await api.get('/health-records/')
      setRecords(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch health records:', error)
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

  const handleView = (record: HealthRecord) => {
    setViewingRecord(record)
    setModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const formattedValues = {
        ...values,
        record_date: values.record_date ? values.record_date.format('YYYY-MM-DD') : null,
        next_checkup_date: values.next_checkup_date ? values.next_checkup_date.format('YYYY-MM-DD') : null,
      }

      await api.post('/health-records/', formattedValues)
      message.success('创建成功')
      setModalVisible(false)
      fetchRecords()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const getRecordTypeColor = (type: string) => {
    const colorMap: Record<string, string> = {
      annual_checkup: 'blue',
      routine_check: 'green',
      special_check: 'orange',
      follow_up: 'purple',
    }
    return colorMap[type] || 'default'
  }

  const columns: ColumnsType<HealthRecord> = [
    {
      title: '居民',
      dataIndex: 'resident_name',
      key: 'resident_name',
    },
    {
      title: '记录日期',
      dataIndex: 'record_date',
      key: 'record_date',
    },
    {
      title: '记录类型',
      dataIndex: 'record_type_display',
      key: 'record_type',
      render: (_, record) => (
        <Tag color={getRecordTypeColor(record.record_type)}>{record.record_type_display}</Tag>
      ),
    },
    {
      title: '身高(cm)',
      dataIndex: 'height',
      key: 'height',
    },
    {
      title: '体重(kg)',
      dataIndex: 'weight',
      key: 'weight',
    },
    {
      title: 'BMI',
      dataIndex: 'bmi',
      key: 'bmi',
    },
    {
      title: '血压(mmHg)',
      key: 'blood_pressure',
      render: (_, record) => (
        <span>
          {record.blood_pressure_systolic}/{record.blood_pressure_diastolic || '-'}
        </span>
      ),
    },
    {
      title: '医生',
      dataIndex: 'doctor_name',
      key: 'doctor',
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
        <h2 className="page-title">健康档案</h2>
        {isAdminOrDoctor && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增健康档案
          </Button>
        )}
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={records}
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
        title={viewingRecord ? '查看健康档案' : '新增健康档案'}
        open={modalVisible}
        onOk={viewingRecord ? undefined : handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
        okText="确定"
        cancelText="取消"
        footer={viewingRecord ? null : undefined}
      >
        {viewingRecord ? (
          <div>
            <Form layout="vertical">
              <Form.Item label="居民">
                <Input value={viewingRecord.resident_name} disabled />
              </Form.Item>
              <Form.Item label="记录日期">
                <Input value={viewingRecord.record_date} disabled />
              </Form.Item>
              <Form.Item label="记录类型">
                <Input value={viewingRecord.record_type_display} disabled />
              </Form.Item>
              <div style={{ display: 'flex', gap: '16px' }}>
                <Form.Item label="身高(cm)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.height || '-'} disabled />
                </Form.Item>
                <Form.Item label="体重(kg)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.weight || '-'} disabled />
                </Form.Item>
                <Form.Item label="BMI" style={{ flex: 1 }}>
                  <Input value={viewingRecord.bmi || '-'} disabled />
                </Form.Item>
              </div>
              <div style={{ display: 'flex', gap: '16px' }}>
                <Form.Item label="收缩压(mmHg)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.blood_pressure_systolic || '-'} disabled />
                </Form.Item>
                <Form.Item label="舒张压(mmHg)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.blood_pressure_diastolic || '-'} disabled />
                </Form.Item>
                <Form.Item label="心率(次/分)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.heart_rate || '-'} disabled />
                </Form.Item>
              </div>
              <div style={{ display: 'flex', gap: '16px' }}>
                <Form.Item label="血糖(mmol/L)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.blood_glucose || '-'} disabled />
                </Form.Item>
                <Form.Item label="体温(℃)" style={{ flex: 1 }}>
                  <Input value={viewingRecord.body_temperature || '-'} disabled />
                </Form.Item>
              </div>
              <Form.Item label="异常发现">
                <Input.TextArea value={viewingRecord.abnormal_findings || '无'} disabled rows={2} />
              </Form.Item>
              <Form.Item label="诊断意见">
                <Input.TextArea value={viewingRecord.diagnosis || '无'} disabled rows={2} />
              </Form.Item>
              <Form.Item label="健康建议">
                <Input.TextArea value={viewingRecord.recommendations || '无'} disabled rows={2} />
              </Form.Item>
              <Form.Item label="医生">
                <Input value={viewingRecord.doctor_name || '-'} disabled />
              </Form.Item>
              <Form.Item label="检查医院">
                <Input value={viewingRecord.hospital || '-'} disabled />
              </Form.Item>
            </Form>
          </div>
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

            <Form.Item name="record_date" label="记录日期" rules={[{ required: true, message: '请选择日期' }]}>
              <DatePicker style={{ width: '100%' }} placeholder="请选择记录日期" />
            </Form.Item>

            <Form.Item name="record_type" label="记录类型" initialValue="routine_check">
              <Select placeholder="请选择记录类型">
                <Select.Option value="annual_checkup">年度体检</Select.Option>
                <Select.Option value="routine_check">常规检查</Select.Option>
                <Select.Option value="special_check">专项检查</Select.Option>
                <Select.Option value="follow_up">随访记录</Select.Option>
              </Select>
            </Form.Item>

            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item name="height" label="身高(cm)" style={{ flex: 1 }}>
                <InputNumber style={{ width: '100%' }} placeholder="请输入身高" min={0} max={300} />
              </Form.Item>
              <Form.Item name="weight" label="体重(kg)" style={{ flex: 1 }}>
                <InputNumber style={{ width: '100%' }} placeholder="请输入体重" min={0} max={500} step={0.1} />
              </Form.Item>
            </div>

            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item name="blood_pressure_systolic" label="收缩压(mmHg)" style={{ flex: 1 }}>
                <InputNumber style={{ width: '100%' }} placeholder="高压" min={0} max={300} />
              </Form.Item>
              <Form.Item name="blood_pressure_diastolic" label="舒张压(mmHg)" style={{ flex: 1 }}>
                <InputNumber style={{ width: '100%' }} placeholder="低压" min={0} max={200} />
              </Form.Item>
              <Form.Item name="heart_rate" label="心率(次/分)" style={{ flex: 1 }}>
                <InputNumber style={{ width: '100%' }} placeholder="心率" min={0} max={300} />
              </Form.Item>
            </div>

            <Form.Item name="blood_glucose" label="血糖(mmol/L)">
              <InputNumber style={{ width: '100%' }} placeholder="请输入血糖" min={0} max={50} step={0.1} />
            </Form.Item>

            <Form.Item name="body_temperature" label="体温(℃)">
              <InputNumber style={{ width: '100%' }} placeholder="请输入体温" min={30} max={45} step={0.1} />
            </Form.Item>

            <Form.Item name="doctor" label="医生">
              <Select placeholder="请选择医生" allowClear>
                {doctors.map((d) => (
                  <Select.Option key={d.id} value={d.id}>
                    {d.username}
                  </Select.Option>
                ))}
              </Select>
            </Form.Item>

            <Form.Item name="hospital" label="检查医院">
              <Input placeholder="请输入检查医院" />
            </Form.Item>

            <Form.Item name="abnormal_findings" label="异常发现">
              <Input.TextArea rows={2} placeholder="请输入异常发现" />
            </Form.Item>

            <Form.Item name="diagnosis" label="诊断意见">
              <Input.TextArea rows={2} placeholder="请输入诊断意见" />
            </Form.Item>

            <Form.Item name="recommendations" label="健康建议">
              <Input.TextArea rows={2} placeholder="请输入健康建议" />
            </Form.Item>

            <Form.Item name="next_checkup_date" label="下次检查日期">
              <DatePicker style={{ width: '100%' }} placeholder="请选择下次检查日期" />
            </Form.Item>
          </Form>
        )}
      </Modal>
    </div>
  )
}

export default HealthRecords
