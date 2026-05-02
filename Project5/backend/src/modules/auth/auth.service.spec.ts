import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { JwtService } from '@nestjs/jwt';
import { UnauthorizedException } from '@nestjs/common';
import { Repository } from 'typeorm';
import * as bcrypt from 'bcryptjs';
import { AuthService } from './auth.service';
import { User } from '../user/user.entity';
import { UserRole } from '../user/user-role.entity';
import { Role } from '../role/role.entity';
import { RolePermission } from '../role/role-permission.entity';
import { Permission } from '../permission/permission.entity';
import { LoginParams } from './dto/login.dto';
import { Result } from '@/common/result';

jest.mock('bcryptjs');

describe('AuthService', () => {
  let service: AuthService;
  let userRepository: jest.Mocked<Repository<User>>;
  let userRoleRepository: jest.Mocked<Repository<UserRole>>;
  let roleRepository: jest.Mocked<Repository<Role>>;
  let rolePermissionRepository: jest.Mocked<Repository<RolePermission>>;
  let permissionRepository: jest.Mocked<Repository<Permission>>;
  let jwtService: jest.Mocked<JwtService>;

  const mockUser: User = {
    id: 1,
    username: 'admin',
    password: 'hashedpassword',
    nickname: '超级管理员',
    email: 'admin@example.com',
    phone: '13800138000',
    avatar: '',
    status: 1,
    lastLoginTime: null,
    roles: [],
    createTime: new Date(),
    updateTime: new Date(),
  };

  const mockDisabledUser: User = {
    ...mockUser,
    id: 2,
    username: 'disabled',
    status: 0,
  };

  const mockRole: Role = {
    id: 1,
    name: '超级管理员',
    code: 'SUPER_ADMIN',
    description: '超级管理员角色',
    status: 1,
    sort: 0,
    users: [],
    permissions: [],
    createTime: new Date(),
    updateTime: new Date(),
  };

  const mockPermission: Permission = {
    id: 1,
    name: '系统管理',
    code: 'system:manage',
    type: 1,
    parentId: 0,
    path: '/system',
    component: 'Layout',
    icon: 'Setting',
    sort: 1,
    status: 1,
    roles: [],
    children: [],
    createTime: new Date(),
    updateTime: new Date(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      providers: [
        AuthService,
        {
          provide: getRepositoryToken(User),
          useValue: {
            findOne: jest.fn(),
            find: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(UserRole),
          useValue: {
            find: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(Role),
          useValue: {
            find: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(RolePermission),
          useValue: {
            find: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(Permission),
          useValue: {
            find: jest.fn(),
          },
        },
        {
          provide: JwtService,
          useValue: {
            sign: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<AuthService>(AuthService);
    userRepository = module.get(getRepositoryToken(User)) as jest.Mocked<Repository<User>>;
    userRoleRepository = module.get(getRepositoryToken(UserRole)) as jest.Mocked<Repository<UserRole>>;
    roleRepository = module.get(getRepositoryToken(Role)) as jest.Mocked<Repository<Role>>;
    rolePermissionRepository = module.get(getRepositoryToken(RolePermission)) as jest.Mocked<Repository<RolePermission>>;
    permissionRepository = module.get(getRepositoryToken(Permission)) as jest.Mocked<Repository<Permission>>;
    jwtService = module.get(JwtService) as jest.Mocked<JwtService>;
  });

  describe('validateUser', () => {
    it('should return user when credentials are valid', async () => {
      userRepository.findOne.mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);

      const result = await service.validateUser('admin', 'password123');

      expect(result).toEqual(mockUser);
      expect(bcrypt.compare).toHaveBeenCalledWith('password123', 'hashedpassword');
    });

    it('should return null when password is invalid', async () => {
      userRepository.findOne.mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(false);

      const result = await service.validateUser('admin', 'wrongpassword');

      expect(result).toBeNull();
    });

    it('should return null when user not found', async () => {
      userRepository.findOne.mockResolvedValue(null);

      const result = await service.validateUser('nonexistent', 'password123');

      expect(result).toBeNull();
    });
  });

  describe('login', () => {
    it('should return login result with token', async () => {
      const loginParams: LoginParams = {
        username: 'admin',
        password: 'password123',
        remember: true,
      };

      userRepository.findOne.mockResolvedValue(mockUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);
      jwtService.sign.mockReturnValue('mock-jwt-token');
      userRoleRepository.find.mockResolvedValue([{ userId: 1, roleId: 1 } as UserRole]);
      roleRepository.find.mockResolvedValue([mockRole]);
      rolePermissionRepository.find.mockResolvedValue([{ roleId: 1, permissionId: 1 } as RolePermission]);
      permissionRepository.find.mockResolvedValue([mockPermission]);

      const result = await service.login(loginParams);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data?.token).toBe('mock-jwt-token');
      expect(jwtService.sign).toHaveBeenCalled();
    });

    it('should throw UnauthorizedException when credentials invalid', async () => {
      const loginParams: LoginParams = {
        username: 'admin',
        password: 'wrongpassword',
      };

      userRepository.findOne.mockResolvedValue(null);

      await expect(service.login(loginParams)).rejects.toThrow(UnauthorizedException);
    });

    it('should throw UnauthorizedException when user is disabled', async () => {
      const loginParams: LoginParams = {
        username: 'disabled',
        password: 'password123',
      };

      userRepository.findOne.mockResolvedValue(mockDisabledUser);
      (bcrypt.compare as jest.Mock).mockResolvedValue(true);

      await expect(service.login(loginParams)).rejects.toThrow(UnauthorizedException);
    });
  });

  describe('getUserInfo', () => {
    it('should return user info', async () => {
      userRepository.findOne.mockResolvedValue(mockUser);
      userRoleRepository.find.mockResolvedValue([{ userId: 1, roleId: 1 } as UserRole]);
      roleRepository.find.mockResolvedValue([mockRole]);
      rolePermissionRepository.find.mockResolvedValue([{ roleId: 1, permissionId: 1 } as RolePermission]);
      permissionRepository.find.mockResolvedValue([mockPermission]);

      const result = await service.getUserInfo(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data?.userInfo).toEqual(
        expect.objectContaining({ username: 'admin', nickname: '超级管理员' }),
      );
    });

    it('should throw UnauthorizedException when user not found', async () => {
      userRepository.findOne.mockResolvedValue(null);

      await expect(service.getUserInfo(999)).rejects.toThrow(UnauthorizedException);
    });
  });

  describe('getMenus', () => {
    it('should return menus for super admin', async () => {
      userRoleRepository.find.mockResolvedValue([{ userId: 1, roleId: 1 } as UserRole]);
      roleRepository.find.mockResolvedValue([mockRole]);
      permissionRepository.find.mockResolvedValue([mockPermission]);

      const result = await service.getMenus(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });
  });
});
