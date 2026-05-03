import React, { useState, useEffect } from 'react'
import { Row, Col, Card, Statistic, Table, Tag, Button } from 'antd'
import {
  TeamOutlined, FileTextOutlined, CalendarOutlined,
  MedicineBoxOutlined, ClockCircleOutlined
} from '@ant-design/icons'
import { useNavigate } from 'react-router-dom'
import api from '@/services/api'
import { useAuthStore } from '@/store/authStore'
import { Resident, Appointment, HealthRecord, MedicationReminder } from '@/types'
import type { ColumnsType } from 'antd/es/table'

const Dashboard: React.FC = () => {
  const navigate = useNavigate()
  const { user } = useAuthStore()
  const [stats, setStats] = useState({
    residents: 0,
    appointments: 0,
    healthRecords: 0,
    medicationReminders: 0,
    pendingAppointments: 0,
  })
  const [recentAppointments, setRecentAppointments] = useState<Appointment[]>([])
  const [loading, setLoading] = useState(false)

  useEffect(() => {
    fetchDashboardData()
  }, [])

  const fetchDashboardData = async () => {
    setLoading(true)
    try {
      const [residentsRes, appointmentsRes, healthRecordsRes, remindersRes] = await Promise.all([
        api.get('/residents/').catch(() => ({ data: { results: [] } })),
        api.get('/appointments/').catch(() => ({ data: { results: [] } })),
        api.get('/health-records/').catch(() => ({ data: { results: [] } })),
        api.get('/medication-reminders/').catch(() => ({ data: { results: [] } })),
      ])

      const appointments = appointmentsRes.data?.results || []
      const pendingAppointments = appointments.filter(
        (a: Appointment) => a.status === 'pending' || a.status === 'confirmed'
      )

      setStats({
        residents: residentsRes.data?.count || residentsRes.data?.results?.length || 0,
        appointments: appointmentsRes.data?.count || appointments.length,
        healthRecords: healthRecordsRes.data?.count || healthRecordsRes.data?.results?.length || 0,
        medicationReminders: remindersRes.data?.count || remindersRes.data?.results?.length || 0,
        pendingAppointments: pendingAppointments.length,
      })

      setRecentAppointments(appointments.slice(0, 5))
    } catch (error) {
      console.error('Failed to fetch dashboard data:', error)
    } finally {
      setLoading(false)
    }
  }

  const getStatusColor = (status: string) => {
    const statusMap: Record<string, string> = {
      pending: 'orange',
      confirmed: 'blue',
      in_progress: 'processing',
      completed: 'success',
      cancelled: 'default',
      no_show: 'error',
    }
    return statusMap[status] || 'default'
  }

  const appointmentColumns: ColumnsType<Appointment> = [
    {
      title: '居民',
      dataIndex: 'resident_name',
      key: 'resident_name',
    },
    {
      title: '预约类型',
      dataIndex: 'appointment_type_display',
      key: 'appointment_type_display',
    },
    {
      title: '预约时间',
      key: 'appointment_time',
      render: (_, record) => (
        <span>{record.appointment_date} {record.appointment_time}</span>
      ),
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      render: (status: string, record) => (
        <Tag color={getStatusColor(status)}>{record.status_display}</Tag>
      ),
    },
    {
      title: '操作',
      key: 'action',
      render: (_, record) => (
        <Button
          type="link"
          onClick={() => navigate('/appointments')}
        >
          查看详情
        </Button>
      ),
    },
  ]

  const isAdminOrDoctor = user?.role === 'admin' || user?.role === 'doctor'

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">仪表盘</h2>
      </div>

      <div className="dashboard-stats">
        <Row gutter={[16, 16]}>
          {isAdminOrDoctor && (
            <Col xs={24} sm={12} md={8} lg={6}>
              <Card hoverable onClick={() => navigate('/residents')}>
                <Statistic
                  title="居民总数"
                  value={stats.residents}
                  prefix={<TeamOutlined style={{ color: '#1890ff' }} />}
                />
              </Card>
            </Col>
          )}
          
          <Col xs={24} sm={12} md={8} lg={6}>
            <Card hoverable onClick={() => navigate('/appointments')}>
              <Statistic
                title="待处理预约"
                value={stats.pendingAppointments}
                prefix={<ClockCircleOutlined style={{ color: '#faad14' }} />}
              />
            </Card>
          </Col>
          
          <Col xs={24} sm={12} md={8} lg={6}>
            <Card hoverable onClick={() => navigate('/health-records')}>
              <Statistic
                title="健康档案"
                value={stats.healthRecords}
                prefix={<FileTextOutlined style={{ color: '#52c41a' }} />}
              />
            </Card>
          </Col>
          
          <Col xs={24} sm={12} md={8} lg={6}>
            <Card hoverable onClick={() => navigate('/medication-reminders')}>
              <Statistic
                title="用药提醒"
                value={stats.medicationReminders}
                prefix={<MedicineBoxOutlined style={{ color: '#722ed1' }} />}
              />
            </Card>
          </Col>
        </Row>
      </div>

      <Card title="最近预约" extra={
        <Button type="link" onClick={() => navigate('/appointments')}>查看全部</Button>
      }>
        <Table
          columns={appointmentColumns}
          dataSource={recentAppointments}
          rowKey="id"
          loading={loading}
          pagination={false}
        />
      </Card>
    </div>
  )
}

export default Dashboard
