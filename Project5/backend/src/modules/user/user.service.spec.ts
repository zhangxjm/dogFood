import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { NotFoundException, BadRequestException } from '@nestjs/common';
import { Repository, SelectQueryBuilder } from 'typeorm';
import * as bcrypt from 'bcryptjs';
import { UserService } from './user.service';
import { User } from './user.entity';
import { UserRole } from './user-role.entity';
import { Role } from '../role/role.entity';
import { CreateUserDto, UpdateUserDto, QueryUserDto } from './dto/user.dto';
import { Result } from '@/common/result';
import { PageResultDto } from '@/common/dto/page.dto';

jest.mock('bcryptjs');

describe('UserService', () => {
  let service: UserService;
  let userRepository: jest.Mocked<Repository<User>>;
  let userRoleRepository: jest.Mocked<Repository<UserRole>>;
  let roleRepository: jest.Mocked<Repository<Role>>;

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

  const mockRole: Role = {
    id: 1,
    name: '管理员',
    code: 'ADMIN',
    description: '管理员角色',
    status: 1,
    sort: 1,
    users: [],
    permissions: [],
    createTime: new Date(),
    updateTime: new Date(),
  };

  beforeEach(async () => {
    const queryBuilder = {
      leftJoinAndSelect: jest.fn().mockReturnThis(),
      leftJoinAndMapMany: jest.fn().mockReturnThis(),
      andWhere: jest.fn().mockReturnThis(),
      orderBy: jest.fn().mockReturnThis(),
      skip: jest.fn().mockReturnThis(),
      take: jest.fn().mockReturnThis(),
      getManyAndCount: jest.fn().mockResolvedValue([[mockUser], 1]),
    } as unknown as jest.Mocked<SelectQueryBuilder<User>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        UserService,
        {
          provide: getRepositoryToken(User),
          useValue: {
            createQueryBuilder: jest.fn().mockReturnValue(queryBuilder),
            findOne: jest.fn(),
            create: jest.fn(),
            save: jest.fn(),
            delete: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(UserRole),
          useValue: {
            find: jest.fn(),
            save: jest.fn(),
            delete: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(Role),
          useValue: {
            find: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<UserService>(UserService);
    userRepository = module.get(getRepositoryToken(User)) as jest.Mocked<Repository<User>>;
    userRoleRepository = module.get(getRepositoryToken(UserRole)) as jest.Mocked<Repository<UserRole>>;
    roleRepository = module.get(getRepositoryToken(Role)) as jest.Mocked<Repository<Role>>;
  });

  describe('findAll', () => {
    it('should return paginated user list', async () => {
      const query: QueryUserDto = {
        page: 1,
        pageSize: 10,
        username: 'test',
        nickname: 'Test',
        status: 1,
      };

      const result = await service.findAll(query);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toBeInstanceOf(PageResultDto);
    });
  });

  describe('findOne', () => {
    it('should return user by id', async () => {
      userRepository.findOne.mockResolvedValue(mockUser);
      userRoleRepository.find.mockResolvedValue([{ userId: 1, roleId: 1 } as UserRole]);
      roleRepository.find.mockResolvedValue([mockRole]);

      const result = await service.findOne(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toEqual(expect.objectContaining({ id: 1, username: 'testuser' }));
    });

    it('should throw NotFoundException when user not found', async () => {
      userRepository.findOne.mockResolvedValue(null);

      await expect(service.findOne(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('create', () => {
    it('should create a new user', async () => {
      const createUserDto: CreateUserDto = {
        username: 'newuser',
        password: 'password123',
        nickname: 'New User',
        email: 'new@example.com',
        phone: '13800138001',
        roleIds: [1],
      };

      userRepository.findOne.mockResolvedValue(null);
      (bcrypt.hash as jest.Mock).mockResolvedValue('hashedpassword');
      userRepository.create.mockReturnValue({ ...mockUser, username: 'newuser' } as User);
      userRepository.save.mockResolvedValue({ ...mockUser, id: 2, username: 'newuser' } as User);

      const result = await service.create(createUserDto);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(bcrypt.hash).toHaveBeenCalledWith('password123', 10);
    });

    it('should throw BadRequestException when username already exists', async () => {
      const createUserDto: CreateUserDto = {
        username: 'existinguser',
        password: 'password123',
      };

      userRepository.findOne.mockResolvedValue(mockUser);

      await expect(service.create(createUserDto)).rejects.toThrow(BadRequestException);
    });
  });

  describe('update', () => {
    it('should update user', async () => {
      const updateUserDto: UpdateUserDto = {
        nickname: 'Updated User',
        status: 0,
        roleIds: [2],
      };

      userRepository.findOne.mockResolvedValue(mockUser);
      userRepository.save.mockResolvedValue({ ...mockUser, ...updateUserDto } as User);

      const result = await service.update(1, updateUserDto);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });

    it('should update password when provided', async () => {
      const updateUserDto: UpdateUserDto = {
        password: 'newpassword',
      };

      userRepository.findOne.mockResolvedValue(mockUser);
      (bcrypt.hash as jest.Mock).mockResolvedValue('newhashedpassword');
      userRepository.save.mockResolvedValue(mockUser);

      await service.update(1, updateUserDto);

      expect(bcrypt.hash).toHaveBeenCalledWith('newpassword', 10);
    });

    it('should throw NotFoundException when user not found', async () => {
      userRepository.findOne.mockResolvedValue(null);

      await expect(service.update(999, {})).rejects.toThrow(NotFoundException);
    });
  });

  describe('remove', () => {
    it('should delete user', async () => {
      userRepository.findOne.mockResolvedValue({ ...mockUser, username: 'testuser' } as User);

      const result = await service.remove(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(userRoleRepository.delete).toHaveBeenCalledWith({ userId: 1 });
      expect(userRepository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw BadRequestException when deleting super admin', async () => {
      userRepository.findOne.mockResolvedValue({ ...mockUser, username: 'admin' } as User);

      await expect(service.remove(1)).rejects.toThrow(BadRequestException);
    });

    it('should throw NotFoundException when user not found', async () => {
      userRepository.findOne.mockResolvedValue(null);

      await expect(service.remove(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('toggleStatus', () => {
    it('should toggle user status from 1 to 0', async () => {
      const userWithStatus1 = { ...mockUser, status: 1 } as User;
      userRepository.findOne.mockResolvedValue(userWithStatus1);
      userRepository.save.mockResolvedValue({ ...userWithStatus1, status: 0 } as User);

      const result = await service.toggleStatus(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(userRepository.save).toHaveBeenCalledWith(expect.objectContaining({ status: 0 }));
    });

    it('should throw BadRequestException when toggling super admin status', async () => {
      userRepository.findOne.mockResolvedValue({ ...mockUser, username: 'admin' } as User);

      await expect(service.toggleStatus(1)).rejects.toThrow(BadRequestException);
    });

    it('should throw NotFoundException when user not found', async () => {
      userRepository.findOne.mockResolvedValue(null);

      await expect(service.toggleStatus(999)).rejects.toThrow(NotFoundException);
    });
  });
});
