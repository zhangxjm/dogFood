import React, { useState, useEffect } from "react";
import {
  Table,
  Button,
  Card,
  Tag,
  Modal,
  Form,
  Input,
  Select,
  message,
  Space,
  Popconfirm,
  Switch,
} from "antd";
import { PlusOutlined, EditOutlined, DeleteOutlined } from "@ant-design/icons";
import type { ColumnsType } from "antd/es/table";
import api from "@/services/api";
import { User } from "@/types";

const UserManagement: React.FC = () => {
  const [users, setUsers] = useState<User[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [editingRecord, setEditingRecord] = useState<User | null>(null);
  const [form] = Form.useForm();

  useEffect(() => {
    fetchUsers();
  }, []);

  const fetchUsers = async () => {
    setLoading(true);
    try {
      const response = await api.get("/users/");
      setUsers(response.data?.results || response.data || []);
    } catch (error) {
      console.error("Failed to fetch users:", error);
    } finally {
      setLoading(false);
    }
  };

  const handleAdd = () => {
    setEditingRecord(null);
    form.resetFields();
    setModalVisible(true);
  };

  const handleEdit = (record: User) => {
    setEditingRecord(record);
    form.setFieldsValue({
      ...record,
    });
    setModalVisible(true);
  };

  const handleDelete = async (id: number) => {
    try {
      await api.delete(`/users/${id}/`);
      message.success("删除成功");
      fetchUsers();
    } catch (error) {
      message.error("删除失败");
    }
  };

  const handleToggleActive = async (record: User) => {
    try {
      await api.patch(`/users/${record.id}/`, {
        is_active: !record.is_active,
      });
      message.success(record.is_active ? "已禁用" : "已启用");
      fetchUsers();
    } catch (error) {
      message.error("操作失败");
    }
  };

  const handleSubmit = async () => {
    try {
      const values = await form.validateFields();

      if (editingRecord) {
        const updateData: Record<string, unknown> = {
          email: values.email,
          phone: values.phone,
          first_name: values.first_name,
          last_name: values.last_name,
          role: values.role,
          is_active: values.is_active,
          is_verified: values.is_verified,
        };
        if (values.password) {
          updateData.password = values.password;
        }
        await api.patch(`/users/${editingRecord.id}/`, updateData);
        message.success("更新成功");
      } else {
        await api.post("/users/", values);
        message.success("创建成功");
      }

      setModalVisible(false);
      fetchUsers();
    } catch (error) {
      console.error("Submit failed:", error);
    }
  };

  const getRoleColor = (role: string) => {
    const colorMap: Record<string, string> = {
      admin: "purple",
      doctor: "blue",
      resident: "green",
    };
    return colorMap[role] || "default";
  };

  const columns: ColumnsType<User> = [
    {
      title: "用户名",
      dataIndex: "username",
      key: "username",
    },
    {
      title: "姓名",
      key: "name",
      render: (_, record) =>
        `${record.last_name || ""}${record.first_name || ""}` || "-",
    },
    {
      title: "角色",
      dataIndex: "role_display",
      key: "role",
      render: (_, record) => (
        <Tag color={getRoleColor(record.role)}>{record.role_display}</Tag>
      ),
    },
    {
      title: "手机号",
      dataIndex: "phone",
      key: "phone",
    },
    {
      title: "邮箱",
      dataIndex: "email",
      key: "email",
    },
    {
      title: "实名认证",
      dataIndex: "is_verified",
      key: "is_verified",
      render: (val) => (
        <Tag color={val ? "green" : "orange"}>{val ? "已认证" : "未认证"}</Tag>
      ),
    },
    {
      title: "状态",
      dataIndex: "is_active",
      key: "is_active",
      render: (_, record) => (
        <Switch
          checked={record.is_active}
          checkedChildren="启用"
          unCheckedChildren="禁用"
          onChange={() => handleToggleActive(record)}
        />
      ),
    },
    {
      title: "操作",
      key: "action",
      render: (_, record) => (
        <Space>
          <Button
            type="link"
            icon={<EditOutlined />}
            onClick={() => handleEdit(record)}
          >
            编辑
          </Button>
          {record.username !== "admin" && (
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
          )}
        </Space>
      ),
    },
  ];

  return (
    <div>
      <div className="page-header">
        <h2 className="page-title">用户管理</h2>
        <Button type="primary" icon={<PlusOutlined />} onClick={handleAdd}>
          新增用户
        </Button>
      </div>

      <Card>
        <Table
          columns={columns}
          dataSource={users}
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
        title={editingRecord ? "编辑用户" : "新增用户"}
        open={modalVisible}
        onOk={handleSubmit}
        onCancel={() => setModalVisible(false)}
        width={600}
        okText="确定"
        cancelText="取消"
      >
        <Form form={form} layout="vertical">
          <Form.Item
            name="username"
            label="用户名"
            rules={[{ required: true, message: "请输入用户名" }]}
          >
            <Input placeholder="请输入用户名" disabled={!!editingRecord} />
          </Form.Item>

          <Form.Item
            name="password"
            label="密码"
            rules={[
              { required: !editingRecord, message: "请输入密码" },
              { min: 6, message: "密码至少6个字符" },
            ]}
            extra={editingRecord ? "留空则不修改密码" : ""}
          >
            <Input.Password
              placeholder={editingRecord ? "留空则不修改密码" : "请输入密码"}
            />
          </Form.Item>

          <Form.Item name="first_name" label="名">
            <Input placeholder="请输入名" />
          </Form.Item>

          <Form.Item name="last_name" label="姓">
            <Input placeholder="请输入姓" />
          </Form.Item>

          <Form.Item
            name="role"
            label="角色"
            rules={[{ required: true, message: "请选择角色" }]}
            initialValue="resident"
          >
            <Select placeholder="请选择角色">
              <Select.Option value="admin">管理员</Select.Option>
              <Select.Option value="doctor">医生</Select.Option>
              <Select.Option value="resident">居民</Select.Option>
            </Select>
          </Form.Item>

          <Form.Item name="phone" label="手机号">
            <Input placeholder="请输入手机号" />
          </Form.Item>

          <Form.Item name="email" label="邮箱">
            <Input placeholder="请输入邮箱" />
          </Form.Item>

          {editingRecord && (
            <>
              <Form.Item
                name="is_verified"
                label="实名认证"
                initialValue={false}
              >
                <Select placeholder="请选择">
                  <Select.Option value={true}>已认证</Select.Option>
                  <Select.Option value={false}>未认证</Select.Option>
                </Select>
              </Form.Item>

              <Form.Item name="is_active" label="状态" initialValue={true}>
                <Select placeholder="请选择">
                  <Select.Option value={true}>启用</Select.Option>
                  <Select.Option value={false}>禁用</Select.Option>
                </Select>
              </Form.Item>
            </>
          )}
        </Form>
      </Modal>
    </div>
  );
};

export default UserManagement;
