import { Test, TestingModule } from '@nestjs/testing';
import { UserController } from './user.controller';
import { UserService } from './user.service';
import { CreateUserDto, UpdateUserDto, QueryUserDto } from './dto/user.dto';
import { Result } from '@/common/result';
import { User } from './user.entity';
import { PageResultDto } from '@/common/dto/page.dto';

jest.mock('./user.service');

describe('UserController', () => {
  let controller: UserController;
  let service: jest.Mocked<UserService>;

  const mockUser: User = {
    id: 1,
    username: 'testuser',
    password: 'hashedpassword',
    nickname: 'Test User',
    email: 'test@example.com',
    phone: '13800138000',
    avatar: '',
    status: 1,
    lastLoginTime: null,
    roles: [],
    createTime: new Date(),
    updateTime: new Date(),
  };

  beforeEach(async () => {
    const module: TestingModule = await Test.createTestingModule({
      controllers: [UserController],
      providers: [UserService],
    }).compile();

    controller = module.get<UserController>(UserController);
    service = module.get(UserService) as jest.Mocked<UserService>;
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('findAll', () => {
    it('should return user list', async () => {
      const query: QueryUserDto = { page: 1, pageSize: 10 };
      const expectedResult = Result.success(
        new PageResultDto([mockUser], 1, 1, 10),
      );
      service.findAll.mockResolvedValue(expectedResult);

      const result = await controller.findAll(query);

      expect(result).toEqual(expectedResult);
      expect(service.findAll).toHaveBeenCalledWith(query);
    });
  });

  describe('findOne', () => {
    it('should return a user by id', async () => {
      const expectedResult = Result.success(mockUser);
      service.findOne.mockResolvedValue(expectedResult);

      const result = await controller.findOne('1');

      expect(result).toEqual(expectedResult);
      expect(service.findOne).toHaveBeenCalledWith(1);
    });
  });

  describe('create', () => {
    it('should create a user', async () => {
      const createUserDto: CreateUserDto = {
        username: 'newuser',
        password: 'password123',
      };
      const expectedResult = Result.success(mockUser, '创建成功');
      service.create.mockResolvedValue(expectedResult);

      const result = await controller.create(createUserDto);

      expect(result).toEqual(expectedResult);
      expect(service.create).toHaveBeenCalledWith(createUserDto);
    });
  });

  describe('update', () => {
    it('should update a user', async () => {
      const updateUserDto: UpdateUserDto = {
        nickname: 'Updated User',
      };
      const expectedResult = Result.success(mockUser, '更新成功');
      service.update.mockResolvedValue(expectedResult);

      const result = await controller.update('1', updateUserDto);

      expect(result).toEqual(expectedResult);
      expect(service.update).toHaveBeenCalledWith(1, updateUserDto);
    });
  });

  describe('remove', () => {
    it('should delete a user', async () => {
      const expectedResult = Result.success(null, '删除成功');
      service.remove.mockResolvedValue(expectedResult);

      const result = await controller.remove('1');

      expect(result).toEqual(expectedResult);
      expect(service.remove).toHaveBeenCalledWith(1);
    });
  });

  describe('toggleStatus', () => {
    it('should toggle user status', async () => {
      const expectedResult = Result.success(mockUser, '状态更新成功');
      service.toggleStatus.mockResolvedValue(expectedResult);

      const result = await controller.toggleStatus('1');

      expect(result).toEqual(expectedResult);
      expect(service.toggleStatus).toHaveBeenCalledWith(1);
    });
  });
});
