import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { NotFoundException, BadRequestException } from '@nestjs/common';
import { Repository, SelectQueryBuilder } from 'typeorm';
import { PermissionService } from './permission.service';
import { Permission } from './permission.entity';
import { CreatePermissionDto, UpdatePermissionDto, QueryPermissionDto } from './dto/permission.dto';
import { Result } from '@/common/result';

describe('PermissionService', () => {
  let service: PermissionService;
  let permissionRepository: jest.Mocked<Repository<Permission>>;

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

  const mockChildPermission: Permission = {
    id: 2,
    name: '用户管理',
    code: 'system:user:list',
    type: 1,
    parentId: 1,
    path: '/system/user',
    component: 'system/user/index',
    icon: 'User',
    sort: 1,
    status: 1,
    roles: [],
    children: [],
    createTime: new Date(),
    updateTime: new Date(),
  };

  beforeEach(async () => {
    const queryBuilder = {
      andWhere: jest.fn().mockReturnThis(),
      orderBy: jest.fn().mockReturnThis(),
      addOrderBy: jest.fn().mockReturnThis(),
      getMany: jest.fn().mockResolvedValue([mockPermission]),
    } as unknown as jest.Mocked<SelectQueryBuilder<Permission>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        PermissionService,
        {
          provide: getRepositoryToken(Permission),
          useValue: {
            createQueryBuilder: jest.fn().mockReturnValue(queryBuilder),
            findOne: jest.fn(),
            find: jest.fn(),
            create: jest.fn(),
            save: jest.fn(),
            delete: jest.fn(),
          },
        },
      ],
    }).compile();

    service = module.get<PermissionService>(PermissionService);
    permissionRepository = module.get(getRepositoryToken(Permission)) as jest.Mocked<Repository<Permission>>;
  });

  describe('findAll', () => {
    it('should return permission tree', async () => {
      const query: QueryPermissionDto = {
        name: '系统',
        type: 1,
        status: 1,
      };

      const result = await service.findAll(query);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(Array.isArray(result.data)).toBe(true);
    });
  });

  describe('findAllList', () => {
    it('should return all active permissions as tree', async () => {
      permissionRepository.find.mockResolvedValue([mockPermission]);

      const result = await service.findAllList();

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });
  });

  describe('findOne', () => {
    it('should return permission by id', async () => {
      permissionRepository.findOne.mockResolvedValue(mockPermission);

      const result = await service.findOne(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toEqual(expect.objectContaining({ id: 1, code: 'system:manage' }));
    });

    it('should throw NotFoundException when permission not found', async () => {
      permissionRepository.findOne.mockResolvedValue(null);

      await expect(service.findOne(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('create', () => {
    it('should create a new permission', async () => {
      const createPermissionDto: CreatePermissionDto = {
        name: '新权限',
        code: 'new:permission',
        type: 1,
        parentId: 0,
      };

      permissionRepository.findOne.mockResolvedValue(null);
      permissionRepository.create.mockReturnValue(mockPermission);
      permissionRepository.save.mockResolvedValue(mockPermission);

      const result = await service.create(createPermissionDto);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });

    it('should throw BadRequestException when code already exists', async () => {
      const createPermissionDto: CreatePermissionDto = {
        name: '新权限',
        code: 'system:manage',
        type: 1,
        parentId: 0,
      };

      permissionRepository.findOne.mockResolvedValue(mockPermission);

      await expect(service.create(createPermissionDto)).rejects.toThrow(BadRequestException);
    });

    it('should throw BadRequestException when parent not found', async () => {
      const createPermissionDto: CreatePermissionDto = {
        name: '新权限',
        code: 'new:permission',
        type: 1,
        parentId: 999,
      };

      permissionRepository.findOne
        .mockResolvedValueOnce(null)
        .mockResolvedValueOnce(null);

      await expect(service.create(createPermissionDto)).rejects.toThrow(BadRequestException);
    });
  });

  describe('update', () => {
    it('should update permission', async () => {
      const updatePermissionDto: UpdatePermissionDto = {
        name: '更新后的权限',
        status: 0,
      };

      permissionRepository.findOne.mockResolvedValue(mockPermission);
      permissionRepository.save.mockResolvedValue({ ...mockPermission, ...updatePermissionDto } as Permission);

      const result = await service.update(1, updatePermissionDto);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });

    it('should throw NotFoundException when permission not found', async () => {
      permissionRepository.findOne.mockResolvedValue(null);

      await expect(service.update(999, {})).rejects.toThrow(NotFoundException);
    });
  });

  describe('remove', () => {
    it('should delete permission', async () => {
      permissionRepository.findOne.mockResolvedValue(mockPermission);
      permissionRepository.find.mockResolvedValue([]);

      const result = await service.remove(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(permissionRepository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw BadRequestException when has children', async () => {
      permissionRepository.findOne.mockResolvedValue(mockPermission);
      permissionRepository.find.mockResolvedValue([mockChildPermission]);

      await expect(service.remove(1)).rejects.toThrow(BadRequestException);
    });

    it('should throw NotFoundException when permission not found', async () => {
      permissionRepository.findOne.mockResolvedValue(null);

      await expect(service.remove(999)).rejects.toThrow(NotFoundException);
    });
  });
});
