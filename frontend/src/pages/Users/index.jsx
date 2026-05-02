/**
 * 用户管理页面
 */
import { useState, useEffect, useCallback } from 'react';
import {
  Table,
  Button,
  Input,
  Select,
  Space,
  Tag,
  Popconfirm,
  Modal,
  Form,
  message,
  Card,
  Row,
  Col,
  Switch,
  Tooltip
} from 'antd';
import {
  PlusOutlined,
  SearchOutlined,
  EditOutlined,
  DeleteOutlined,
  ReloadOutlined
} from '@ant-design/icons';
import { getUsers, createUser, updateUser, deleteUser, updateUserStatus } from '@/services/user';
import UserModal from './components/UserModal';
import './Users.css';

const { Search } = Input;
const { Option } = Select;

const Users = () => {
  const [loading, setLoading] = useState(false);
  const [data, setData] = useState([]);
  const [pagination, setPagination] = useState({
    current: 1,
    pageSize: 10,
    total: 0
  });
  const [searchParams, setSearchParams] = useState({
    search: '',
    role: '',
    status: ''
  });
  const [modalVisible, setModalVisible] = useState(false);
  const [modalType, setModalType] = useState('create');
  const [editingUser, setEditingUser] = useState(null);
  const [form] = Form.useForm();

  // 获取用户列表
  const fetchUsers = useCallback(async () => {
    setLoading(true);
    
    try {
      const params = {
        page: pagination.current,
        pageSize: pagination.pageSize,
        ...searchParams
      };
      
      // 移除空值参数
      Object.keys(params).forEach(key => {
        if (params[key] === '' || params[key] === null || params[key] === undefined) {
          delete params[key];
        }
      });
      
      const result = await getUsers(params);
      
      setData(result.list || []);
      setPagination(prev => ({
        ...prev,
        total: result.pagination?.total || 0
      }));
    } catch (error) {
      console.error('获取用户列表失败:', error);
    } finally {
      setLoading(false);
    }
  }, [pagination.current, pagination.pageSize, searchParams]);

  // 初始加载
  useEffect(() => {
    fetchUsers();
  }, [fetchUsers]);

  // 表格列定义
  const columns = [
    {
      title: '用户名',
      dataIndex: 'username',
      key: 'username',
      width: 150
    },
    {
      title: '邮箱',
      dataIndex: 'email',
      key: 'email',
      width: 200
    },
    {
      title: '真实姓名',
      dataIndex: 'realName',
      key: 'realName',
      width: 120
    },
    {
      title: '角色',
      dataIndex: 'role',
      key: 'role',
      width: 100,
      render: (role) => (
        <Tag color={role === 'admin' ? 'green' : 'blue'}>
          {role === 'admin' ? '管理员' : '普通用户'}
        </Tag>
      )
    },
    {
      title: '状态',
      dataIndex: 'status',
      key: 'status',
      width: 100,
      render: (status, record) => (
        <Switch
          checked={status}
          checkedChildren="启用"
          unCheckedChildren="禁用"
          size="small"
          onChange={(checked) => handleUserStatusChange(record._id, checked)}
        />
      )
    },
    {
      title: '最后登录',
      dataIndex: 'lastLoginAt',
      key: 'lastLoginAt',
      width: 180,
      render: (date) => date ? new Date(date).toLocaleString('zh-CN') : '-'
    },
    {
      title: '创建时间',
      dataIndex: 'createdAt',
      key: 'createdAt',
      width: 180,
      render: (date) => date ? new Date(date).toLocaleString('zh-CN') : '-'
    },
    {
      title: '操作',
      key: 'actions',
      width: 150,
      fixed: 'right',
      render: (_, record) => (
        <Space size="small">
          <Tooltip title="编辑">
            <Button
              type="link"
              size="small"
              icon={<EditOutlined />}
              onClick={() => handleEdit(record)}
            />
          </Tooltip>
          <Popconfirm
            title="确定要删除该用户吗？"
            onConfirm={() => handleDelete(record._id)}
            okText="确定"
            cancelText="取消"
            okButtonProps={{ danger: true }}
          >
            <Tooltip title="删除">
              <Button
                type="link"
                size="small"
                danger
                icon={<DeleteOutlined />}
              />
            </Tooltip>
          </Popconfirm>
        </Space>
      )
    }
  ];

  // 处理搜索
  const handleSearch = (value) => {
    setSearchParams(prev => ({ ...prev, search: value }));
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  // 处理角色筛选
  const handleRoleChange = (value) => {
    setSearchParams(prev => ({ ...prev, role: value }));
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  // 处理状态筛选
  const handleStatusFilterChange = (value) => {
    setSearchParams(prev => ({ ...prev, status: value }));
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  // 处理表格分页
  const handleTableChange = (pag) => {
    setPagination(prev => ({
      ...prev,
      current: pag.current,
      pageSize: pag.pageSize
    }));
  };

  // 处理状态切换（启用/禁用用户）
  const handleUserStatusChange = async (id, status) => {
    try {
      await updateUserStatus(id, status);
      message.success(`用户已${status ? '启用' : '禁用'}`);
      fetchUsers();
    } catch (error) {
      console.error('更新状态失败:', error);
    }
  };

  // 处理新增
  const handleCreate = () => {
    setModalType('create');
    setEditingUser(null);
    form.resetFields();
    setModalVisible(true);
  };

  // 处理编辑
  const handleEdit = (record) => {
    setModalType('edit');
    setEditingUser(record);
    form.setFieldsValue({
      username: record.username,
      email: record.email,
      realName: record.realName || '',
      role: record.role,
      status: record.status
    });
    setModalVisible(true);
  };

  // 处理删除
  const handleDelete = async (id) => {
    try {
      await deleteUser(id);
      message.success('删除成功');
      fetchUsers();
    } catch (error) {
      console.error('删除失败:', error);
    }
  };

  // 处理模态框提交
  const handleModalSubmit = async () => {
    try {
      const values = await form.validateFields();
      
      if (modalType === 'create') {
        await createUser(values);
        message.success('创建成功');
      } else {
        // 编辑时如果没有修改密码，不传递 password 字段
        if (!values.password) {
          delete values.password;
        }
        await updateUser(editingUser._id, values);
        message.success('更新成功');
      }
      
      setModalVisible(false);
      fetchUsers();
    } catch (error) {
      console.error('提交失败:', error);
    }
  };

  // 重置搜索
  const handleReset = () => {
    setSearchParams({
      search: '',
      role: '',
      status: ''
    });
    setPagination(prev => ({ ...prev, current: 1 }));
  };

  return (
    <div className="users-container">
      {/* 页面标题 */}
      <div className="page-header">
        <h2>用户管理</h2>
        <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleCreate}
        >
          新增用户
        </Button>
      </div>

      {/* 搜索区域 */}
      <Card className="search-section">
        <Row gutter={[16, 16]} align="middle">
          <Col xs={24} sm={12} md={8} lg={6}>
            <Search
              placeholder="搜索用户名、邮箱、真实姓名"
              allowClear
              value={searchParams.search}
              onChange={(e) => setSearchParams(prev => ({ ...prev, search: e.target.value }))}
              onSearch={handleSearch}
              enterButton={<SearchOutlined />}
            />
          </Col>
          
          <Col xs={24} sm={12} md={8} lg={5}>
            <Select
              placeholder="筛选角色"
              allowClear
              style={{ width: '100%' }}
              value={searchParams.role || undefined}
              onChange={handleRoleChange}
            >
              <Option value="admin">管理员</Option>
              <Option value="user">普通用户</Option>
            </Select>
          </Col>
          
          <Col xs={24} sm={12} md={8} lg={5}>
            <Select
              placeholder="筛选状态"
              allowClear
              style={{ width: '100%' }}
              value={searchParams.status !== '' ? searchParams.status : undefined}
              onChange={handleStatusFilterChange}
            >
              <Option value="true">启用</Option>
              <Option value="false">禁用</Option>
            </Select>
          </Col>
          
          <Col xs={24} sm={12} md={8} lg={4}>
            <Space>
              <Button
                type="primary"
                icon={<SearchOutlined />}
                onClick={() => fetchUsers()}
              >
                搜索
              </Button>
              <Button
                icon={<ReloadOutlined />}
                onClick={handleReset}
              >
                重置
              </Button>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 表格 */}
      <Card>
        <Table
          columns={columns}
          dataSource={data}
          rowKey="_id"
          loading={loading}
          pagination={{
            ...pagination,
            showSizeChanger: true,
            showQuickJumper: true,
            showTotal: (total) => `共 ${total} 条记录`,
            pageSizeOptions: ['10', '20', '50', '100']
          }}
          onChange={handleTableChange}
          scroll={{ x: 1200 }}
        />
      </Card>

      {/* 新增/编辑弹窗 */}
      <UserModal
        visible={modalVisible}
        title={modalType === 'create' ? '新增用户' : '编辑用户'}
        form={form}
        modalType={modalType}
        onOk={handleModalSubmit}
        onCancel={() => setModalVisible(false)}
      />
    </div>
  );
};

export default Users;
