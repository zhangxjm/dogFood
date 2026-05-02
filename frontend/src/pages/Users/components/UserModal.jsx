/**
 * 用户新增/编辑弹窗组件
 */
import { Modal, Form, Input, Select, Switch } from 'antd';

const { Option } = Select;

const UserModal = ({ visible, title, form, modalType, onOk, onCancel }) => {
  return (
    <Modal
      open={visible}
      title={title}
      onOk={onOk}
      onCancel={onCancel}
      okText="确定"
      cancelText="取消"
      width={500}
      destroyOnClose
    >
      <Form
        form={form}
        layout="vertical"
        autoComplete="off"
      >
        {/* 用户名 */}
        <Form.Item
          name="username"
          label="用户名"
          rules={[
            { required: true, message: '请输入用户名' },
            { min: 3, message: '用户名至少 3 个字符' },
            { max: 20, message: '用户名最多 20 个字符' },
            { 
              pattern: /^[a-zA-Z0-9_]+$/, 
              message: '用户名只能包含字母、数字和下划线' 
            }
          ]}
        >
          <Input placeholder="请输入用户名" />
        </Form.Item>

        {/* 邮箱 */}
        <Form.Item
          name="email"
          label="邮箱"
          rules={[
            { required: true, message: '请输入邮箱' },
            { type: 'email', message: '请输入有效的邮箱地址' }
          ]}
        >
          <Input placeholder="请输入邮箱地址" />
        </Form.Item>

        {/* 真实姓名 */}
        <Form.Item
          name="realName"
          label="真实姓名"
          rules={[
            { max: 50, message: '真实姓名最多 50 个字符' }
          ]}
        >
          <Input placeholder="请输入真实姓名 (可选)" />
        </Form.Item>

        {/* 密码 */}
        <Form.Item
          name="password"
          label="密码"
          rules={[
            // 新增时必填，编辑时可选
            ...(modalType === 'create' 
              ? [
                  { required: true, message: '请输入密码' },
                  { min: 6, message: '密码至少 6 个字符' }
                ]
              : [
                  { min: 6, message: '密码至少 6 个字符' }
                ]
            )
          ]}
          extra={modalType === 'edit' ? '留空则不修改密码' : ''}
        >
          <Input.Password placeholder={modalType === 'edit' ? '请输入新密码 (留空不修改)' : '请输入密码'} />
        </Form.Item>

        {/* 确认密码 */}
        <Form.Item
          name="confirmPassword"
          label="确认密码"
          dependencies={['password']}
          rules={[
            ...(modalType === 'create' 
              ? [{ required: true, message: '请确认密码' }]
              : []
            ),
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('两次输入的密码不一致'));
              }
            })
          ]}
          extra={modalType === 'edit' ? '与密码保持一致' : ''}
        >
          <Input.Password placeholder="请再次输入密码" />
        </Form.Item>

        {/* 角色 */}
        <Form.Item
          name="role"
          label="角色"
          rules={[
            { required: true, message: '请选择角色' }
          ]}
          initialValue="user"
        >
          <Select placeholder="请选择角色">
            <Option value="user">普通用户</Option>
            <Option value="admin">管理员</Option>
          </Select>
        </Form.Item>

        {/* 状态 */}
        <Form.Item
          name="status"
          label="状态"
          valuePropName="checked"
          initialValue={true}
        >
          <Switch
            checkedChildren="启用"
            unCheckedChildren="禁用"
          />
        </Form.Item>
      </Form>
    </Modal>
  );
};

export default UserModal;
