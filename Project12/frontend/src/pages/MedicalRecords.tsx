import React, { useState, useEffect } from 'react'
import { Table, Button, Card, Tag, Modal, Form, Input, Select, DatePicker, message, Space } from 'antd'
import { PlusOutlined, EyeOutlined } from '@ant-design/icons'
import type { ColumnsType } from 'antd/es/table'
import api from '@/services/api'
import { MedicalRecord, Resident, User } from '@/types'
import { useAuthStore } from '@/store/authStore'

const MedicalRecords: React.FC = () => {
  const [records, setRecords] = useState<MedicalRecord[]>([])
  const [residents, setResidents] = useState<Resident[]>([])
  const [doctors, setDoctors] = useState<User[]>([])
  const [loading, setLoading] = useState(false)
  const [modalVisible, setModalVisible] = useState(false)
  const [viewingRecord, setViewingRecord] = useState<MedicalRecord | null>(null)
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
      const response = await api.get('/medical-records/')
      setRecords(response.data?.results || response.data || [])
    } catch (error) {
      console.error('Failed to fetch medical records:', error)
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

  const handleView = (record: MedicalRecord) => {
    setViewingRecord(record)
    setModalVisible(true)
  }

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields()
      const formattedValues = {
        ...values,
        visit_date: values.visit_date ? values.visit_date.format('YYYY-MM-DD') : null,
        next_visit_date: values.next_visit_date ? values.next_visit_date.format('YYYY-MM-DD') : null,
      }

      await api.post('/medical-records/', formattedValues)
      message.success('创建成功')
      setModalVisible(false)
      fetchRecords()
    } catch (error) {
      console.error('Submit failed:', error)
    }
  }

  const getVisitTypeColor = (type: string) => {
    const colorMap: Record<string, string> = {
      outpatient: 'blue',
      emergency: 'red',
      inpatient: 'purple',
      follow_up: 'green',
      telemedicine: 'cyan',
    }
    return colorMap[type] || 'default'
  }

  const columns: ColumnsType<MedicalRecord> = [
    {
      title: '居民',
      dataIndex: 'resident_name',
      key: 'resident_name',
    },
    {
      title: '就诊日期',
      dataIndex: 'visit_date',
      key: 'visit_date',
    },
    {
      title: '就诊类型',
      dataIndex: 'visit_type_display',
      key: 'visit_type',
      render: (_, record) => (
        <Tag color={getVisitTypeColor(record.visit_type)}>{record.visit_type_display}</Tag>
      ),
    },
    {
      title: '医生',
      dataIndex: 'doctor_name',
      key: 'doctor',
      render: (val) => val || '-',
    },
    {
      title: '科室',
      dataIndex: 'department',
      key: 'department',
      render: (val) => val || '-',
    },
    {
      title: '主诉',
      dataIndex: 'chief_complaint',
      key: 'chief_complaint',
      ellipsis: true,
    },
    {
      title: '诊断',
      dataIndex: 'diagnosis',
      key: 'diagnosis',
      ellipsis: true,
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
        <h2 className="page-title">就诊记录</h2>
        {isAdminOrDoctor && (
          <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
            新增就诊记录
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
        title={viewingRecord ? '查看就诊记录' : '新增就诊记录'}
        open={modalVisible}
        onOk={viewingRecord ? undefined : handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={800}
        okText="确定"
        cancelText="取消"
        footer={viewingRecord ? null : undefined}
      >
        {viewingRecord ? (
          <Form layout="vertical">
            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item label="居民" style={{ flex: 1 }}>
                <Input value={viewingRecord.resident_name} disabled />
              </Form.Item>
              <Form.Item label="就诊日期" style={{ flex: 1 }}>
                <Input value={viewingRecord.visit_date} disabled />
              </Form.Item>
              <Form.Item label="就诊类型" style={{ flex: 1 }}>
                <Tag color={getVisitTypeColor(viewingRecord.visit_type)}>
                  {viewingRecord.visit_type_display}
                </Tag>
              </Form.Item>
            </div>
            <Form.Item label="主诉">
              <Input.TextArea value={viewingRecord.chief_complaint} disabled rows={2} />
            </Form.Item>
            <Form.Item label="现病史">
              <Input.TextArea value={viewingRecord.present_illness || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="既往史">
              <Input.TextArea value={viewingRecord.past_history || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="体格检查">
              <Input.TextArea value={viewingRecord.physical_examination || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="辅助检查">
              <Input.TextArea value={viewingRecord.auxiliary_examination || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="诊断">
              <Input.TextArea value={viewingRecord.diagnosis} disabled rows={2} />
            </Form.Item>
            <Form.Item label="治疗方案">
              <Input.TextArea value={viewingRecord.treatment_plan || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="处方">
              <Input.TextArea value={viewingRecord.prescription || '无'} disabled rows={2} />
            </Form.Item>
            <Form.Item label="医生">
              <Input value={viewingRecord.doctor_name || '-'} disabled />
            </Form.Item>
            <Form.Item label="医院">
              <Input value={viewingRecord.hospital || '-'} disabled />
            </Form.Item>
          </Form>
        ) : (
          <Form form={form} layout="vertical">
            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item name="resident" label="居民" rules={[{ required: true, message: '请选择居民' }]} style={{ flex: 1 }}>
                <Select placeholder="请选择居民">
                  {residents.map((r) => (
                    <Select.Option key={r.id} value={r.id}>
                      {r.name}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="visit_date" label="就诊日期" rules={[{ required: true, message: '请选择日期' }]} style={{ flex: 1 }}>
                <DatePicker style={{ width: '100%' }} placeholder="请选择日期" />
              </Form.Item>
              <Form.Item name="visit_type" label="就诊类型" initialValue="outpatient" style={{ flex: 1 }}>
                <Select placeholder="请选择就诊类型">
                  <Select.Option value="outpatient">门诊</Select.Option>
                  <Select.Option value="emergency">急诊</Select.Option>
                  <Select.Option value="inpatient">住院</Select.Option>
                  <Select.Option value="follow_up">随访</Select.Option>
                  <Select.Option value="telemedicine">远程问诊</Select.Option>
                </Select>
              </Form.Item>
            </div>

            <Form.Item name="chief_complaint" label="主诉" rules={[{ required: true, message: '请输入主诉' }]}>
              <Input.TextArea rows={2} placeholder="请输入主诉" />
            </Form.Item>

            <Form.Item name="present_illness" label="现病史">
              <Input.TextArea rows={2} placeholder="请输入现病史" />
            </Form.Item>

            <Form.Item name="past_history" label="既往史">
              <Input.TextArea rows={2} placeholder="请输入既往史" />
            </Form.Item>

            <Form.Item name="physical_examination" label="体格检查">
              <Input.TextArea rows={2} placeholder="请输入体格检查" />
            </Form.Item>

            <Form.Item name="auxiliary_examination" label="辅助检查">
              <Input.TextArea rows={2} placeholder="请输入辅助检查结果" />
            </Form.Item>

            <Form.Item name="diagnosis" label="诊断" rules={[{ required: true, message: '请输入诊断' }]}>
              <Input.TextArea rows={2} placeholder="请输入诊断意见" />
            </Form.Item>

            <Form.Item name="treatment_plan" label="治疗方案">
              <Input.TextArea rows={2} placeholder="请输入治疗方案" />
            </Form.Item>

            <Form.Item name="prescription" label="处方">
              <Input.TextArea rows={2} placeholder="请输入处方" />
            </Form.Item>

            <div style={{ display: 'flex', gap: '16px' }}>
              <Form.Item name="doctor" label="医生" style={{ flex: 1 }}>
                <Select placeholder="请选择医生" allowClear>
                  {doctors.map((d) => (
                    <Select.Option key={d.id} value={d.id}>
                      {d.username}
                    </Select.Option>
                  ))}
                </Select>
              </Form.Item>
              <Form.Item name="hospital" label="医院" style={{ flex: 1 }}>
                <Input placeholder="请输入就诊医院" />
              </Form.Item>
            </div>
          </Form>
        )}
      </Modal>
    </div>
  )
}

export default MedicalRecords
