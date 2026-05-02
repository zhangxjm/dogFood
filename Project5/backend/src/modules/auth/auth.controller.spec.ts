import { Test, TestingModule } from '@nestjs/testing';
import { AuthController } from './auth.controller';
import { AuthService } from './auth.service';
import { LoginDto, LoginParams } from './dto/login.dto';
import { Result } from '@/common/result';

jest.mock('./auth.service');

describe('AuthController', () => {
  let controller: AuthController;
  let service: jest.Mocked<AuthService>;

  const mockLoginResult = {
    token: 'mock-jwt-token',
    userInfo: {
      id: 1,
      username: 'admin',
      nickname: '超级管理员',
      avatar: '',
      email: 'admin@example.com',
      phone: '13800138000',
    },
    roles: ['SUPER_ADMIN'],
    permissions: ['system:manage'],
  };

  const mockUserInfo = {
    userInfo: {
      id: 1,
      username: 'admin',
      nickname: '超级管理员',
      avatar: '',
      email: 'admin@example.com',
      phone: '13800138000',
    },
    roles: ['SUPER_ADMIN'],
    permissions: ['system:manage'],
  };

  const mockMenus = [
    {
      id: 1,
      path: '/system',
      name: '系统管理',
      component: 'Layout',
      meta: { title: '系统管理', icon: 'Setting' },
      children: [],
    },
  ];

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [AuthController],
      providers: [AuthService],
    }).compile();

    controller = module.get<AuthController>(AuthController);
    service = module.get(AuthService) as jest.Mocked<AuthService>;
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('login', () => {
    it('should return login result', async () => {
      const loginDto: LoginDto = {
        username: 'admin',
        password: 'password123',
        remember: true,
      };

      const expectedResult = Result.success(mockLoginResult, '登录成功');
      service.login.mockResolvedValue(expectedResult);

      const mockReq = { user: { id: 1, username: 'admin' } };
      const result = await controller.login(loginDto, mockReq);

      expect(result).toEqual(expectedResult);
      expect(service.login).toHaveBeenCalledWith(
        expect.objectContaining({
          username: 'admin',
          password: 'password123',
          remember: true,
        }),
      );
    });
  });

  describe('logout', () => {
    it('should return logout result', async () => {
      const result = await controller.logout({});

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.message).toBe('退出成功');
    });
  });

  describe('getUserInfo', () => {
    it('should return user info', async () => {
      const expectedResult = Result.success(mockUserInfo);
      service.getUserInfo.mockResolvedValue(expectedResult);

      const mockReq = { user: { userId: 1, username: 'admin' } };
      const result = await controller.getUserInfo(mockReq);

      expect(result).toEqual(expectedResult);
      expect(service.getUserInfo).toHaveBeenCalledWith(1);
    });
  });

  describe('getMenus', () => {
    it('should return menus', async () => {
      const expectedResult = Result.success(mockMenus);
      service.getMenus.mockResolvedValue(expectedResult);

      const mockReq = { user: { userId: 1, username: 'admin' } };
      const result = await controller.getMenus(mockReq);

      expect(result).toEqual(expectedResult);
      expect(service.getMenus).toHaveBeenCalledWith(1);
    });
  });

  describe('getPermissions', () => {
    it('should return permissions', async () => {
      const result = await controller.getPermissions({});

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toEqual([]);
    });
  });
});
