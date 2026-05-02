import { Injectable, Logger } from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import * as bcrypt from 'bcrypt';
import { User } from '../entities/user.entity';
import { Role } from '../entities/role.entity';
import { Permission } from '../entities/permission.entity';

@Injectable()
export class SeedService {
  private readonly logger = new Logger(SeedService.name);
  private readonly saltRounds = 10;

  constructor(
    @InjectRepository(User)
    private userRepository: Repository<User>,
    @InjectRepository(Role)
    private roleRepository: Repository<Role>,
    @InjectRepository(Permission)
    private permissionRepository: Repository<Permission>,
  ) {}

  async initData() {
    this.logger.log('开始初始化数据...');

    const permissionCount = await this.permissionRepository.count();
    if (permissionCount === 0) {
      await this.initPermissions();
      this.logger.log('权限数据初始化完成');
    } else {
      this.logger.log('权限数据已存在，跳过初始化');
    }

    const roleCount = await this.roleRepository.count();
    if (roleCount === 0) {
      await this.initRoles();
      this.logger.log('角色数据初始化完成');
    } else {
      this.logger.log('角色数据已存在，跳过初始化');
    }

    const userCount = await this.userRepository.count();
    if (userCount === 0) {
      await this.initAdminUser();
      this.logger.log('管理员用户初始化完成');
    } else {
      this.logger.log('用户数据已存在，跳过初始化');
    }

    this.logger.log('数据初始化完成');
  }

  private async initPermissions() {
    const permissions = [
      { name: '系统管理', code: 'system', type: 1, parentId: 0, path: '/system', component: 'Layout', icon: 'Setting', sort: 1 },
      { name: '用户管理', code: 'system:user', type: 1, parentId: 0, path: '/system/user', component: 'system/user/index', icon: 'User', sort: 1 },
      { name: '角色管理', code: 'system:role', type: 1, parentId: 0, path: '/system/role', component: 'system/role/index', icon: 'UserFilled', sort: 2 },
      { name: '权限管理', code: 'system:permission', type: 1, parentId: 0, path: '/system/permission', component: 'system/permission/index', icon: 'Lock', sort: 3 },
      { name: '用户列表', code: 'user:list', type: 2, parentId: 2, sort: 1 },
      { name: '新增用户', code: 'user:add', type: 2, parentId: 2, sort: 2 },
      { name: '编辑用户', code: 'user:edit', type: 2, parentId: 2, sort: 3 },
      { name: '删除用户', code: 'user:delete', type: 2, parentId: 2, sort: 4 },
      { name: '重置密码', code: 'user:resetPwd', type: 2, parentId: 2, sort: 5 },
      { name: '角色列表', code: 'role:list', type: 2, parentId: 3, sort: 1 },
      { name: '新增角色', code: 'role:add', type: 2, parentId: 3, sort: 2 },
      { name: '编辑角色', code: 'role:edit', type: 2, parentId: 3, sort: 3 },
      { name: '删除角色', code: 'role:delete', type: 2, parentId: 3, sort: 4 },
      { name: '分配权限', code: 'role:assign', type: 2, parentId: 3, sort: 5 },
      { name: '权限列表', code: 'permission:list', type: 2, parentId: 4, sort: 1 },
      { name: '新增权限', code: 'permission:add', type: 2, parentId: 4, sort: 2 },
      { name: '编辑权限', code: 'permission:edit', type: 2, parentId: 4, sort: 3 },
      { name: '删除权限', code: 'permission:delete', type: 2, parentId: 4, sort: 4 },
      { name: '获取用户列表', code: 'api:user:list', type: 3, parentId: 5, sort: 1 },
      { name: '获取用户详情', code: 'api:user:get', type: 3, parentId: 5, sort: 2 },
      { name: '创建用户', code: 'api:user:create', type: 3, parentId: 6, sort: 1 },
      { name: '更新用户', code: 'api:user:update', type: 3, parentId: 7, sort: 1 },
      { name: '删除用户', code: 'api:user:delete', type: 3, parentId: 8, sort: 1 },
    ];

    for (const perm of permissions) {
      const permission = this.permissionRepository.create(perm);
      await this.permissionRepository.save(permission);
    }
  }

  private async initRoles() {
    const permissions = await this.permissionRepository.find();
    
    const adminRole = this.roleRepository.create({
      name: '超级管理员',
      code: 'admin',
      description: '拥有所有权限',
      status: 1,
      permissions,
    });
    await this.roleRepository.save(adminRole);

    const normalPermissions = permissions.filter((p) =>
      ['system:user', 'system:role', 'user:list', 'user:add', 'user:edit', 'role:list'].includes(p.code),
    );
    const userRole = this.roleRepository.create({
      name: '普通用户',
      code: 'user',
      description: '拥有部分权限',
      status: 1,
      permissions: normalPermissions,
    });
    await this.roleRepository.save(userRole);
  }

  private async initAdminUser() {
    const adminRole = await this.roleRepository.findOne({
      where: { code: 'admin' },
    });

    if (!adminRole) {
      this.logger.error('管理员角色不存在');
      return;
    }

    const hashedPassword = await bcrypt.hash('123456', this.saltRounds);

    const adminUser = this.userRepository.create({
      username: 'admin',
      password: hashedPassword,
      nickname: '超级管理员',
      email: 'admin@example.com',
      phone: '13800138000',
      status: 1,
      roles: [adminRole],
    });

    await this.userRepository.save(adminUser);

    this.logger.log('管理员用户已创建: admin / 123456');
  }
}
