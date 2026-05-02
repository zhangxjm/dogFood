import { Test, TestingModule } from '@nestjs/testing';
import { getRepositoryToken } from '@nestjs/typeorm';
import { NotFoundException, BadRequestException } from '@nestjs/common';
import { Repository, SelectQueryBuilder } from 'typeorm';
import { RoleService } from './role.service';
import { Role } from './role.entity';
import { RolePermission } from './role-permission.entity';
import { Permission } from '../permission/permission.entity';
import { CreateRoleDto, UpdateRoleDto, QueryRoleDto } from './dto/role.dto';
import { Result } from '@/common/result';
import { PageResultDto } from '@/common/dto/page.dto';

describe('RoleService', () => {
  let service: RoleService;
  let roleRepository: jest.Mocked<Repository<Role>>;
  let rolePermissionRepository: jest.Mocked<Repository<RolePermission>>;
  let permissionRepository: jest.Mocked<Repository<Permission>>;

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

  const mockSuperAdminRole: Role = {
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

  beforeEach(async () => {
    const queryBuilder = {
      andWhere: jest.fn().mockReturnThis(),
      orderBy: jest.fn().mockReturnThis(),
      skip: jest.fn().mockReturnThis(),
      take: jest.fn().mockReturnThis(),
      getManyAndCount: jest.fn().mockResolvedValue([[mockRole], 1]),
    } as unknown as jest.Mocked<SelectQueryBuilder<Role>>;

    const module: TestingModule = await Test.createTestingModule({
      providers: [
        RoleService,
        {
          provide: getRepositoryToken(Role),
          useValue: {
            createQueryBuilder: jest.fn().mockReturnValue(queryBuilder),
            findOne: jest.fn(),
            find: jest.fn(),
            create: jest.fn(),
            save: jest.fn(),
            delete: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(RolePermission),
          useValue: {
            find: jest.fn(),
            save: jest.fn(),
            delete: jest.fn(),
          },
        },
        {
          provide: getRepositoryToken(Permission),
          useValue: {},
        },
      ],
    }).compile();

    service = module.get<RoleService>(RoleService);
    roleRepository = module.get(getRepositoryToken(Role)) as jest.Mocked<Repository<Role>>;
    rolePermissionRepository = module.get(getRepositoryToken(RolePermission)) as jest.Mocked<Repository<RolePermission>>;
    permissionRepository = module.get(getRepositoryToken(Permission)) as jest.Mocked<Repository<Permission>>;
  });

  describe('findAll', () => {
    it('should return paginated role list', async () => {
      const query: QueryRoleDto = {
        page: 1,
        pageSize: 10,
        name: '管理员',
        code: 'ADMIN',
        status: 1,
      };

      const result = await service.findAll(query);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toBeInstanceOf(PageResultDto);
    });
  });

  describe('findAllSimple', () => {
    it('should return all active roles', async () => {
      roleRepository.find.mockResolvedValue([mockRole]);

      const result = await service.findAllSimple();

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toEqual([mockRole]);
    });
  });

  describe('findOne', () => {
    it('should return role by id', async () => {
      roleRepository.findOne.mockResolvedValue(mockRole);
      rolePermissionRepository.find.mockResolvedValue([
        { roleId: 1, permissionId: 1 } as RolePermission,
      ]);

      const result = await service.findOne(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(result.data).toEqual(expect.objectContaining({ id: 1, code: 'ADMIN' }));
    });

    it('should throw NotFoundException when role not found', async () => {
      roleRepository.findOne.mockResolvedValue(null);

      await expect(service.findOne(999)).rejects.toThrow(NotFoundException);
    });
  });

  describe('create', () => {
    it('should create a new role', async () => {
      const createRoleDto: CreateRoleDto = {
        name: '新角色',
        code: 'NEW_ROLE',
        description: '新角色描述',
      };

      roleRepository.findOne.mockResolvedValue(null);
      roleRepository.create.mockReturnValue(mockRole);
      roleRepository.save.mockResolvedValue(mockRole);

      const result = await service.create(createRoleDto);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });

    it('should throw BadRequestException when code already exists', async () => {
      const createRoleDto: CreateRoleDto = {
        name: '新角色',
        code: 'ADMIN',
      };

      roleRepository.findOne.mockResolvedValue(mockRole);

      await expect(service.create(createRoleDto)).rejects.toThrow(BadRequestException);
    });
  });

  describe('update', () => {
    it('should update role', async () => {
      const updateRoleDto: UpdateRoleDto = {
        name: '更新后的角色',
        description: '更新后的描述',
        status: 0,
        permissionIds: [1, 2, 3],
      };

      roleRepository.findOne.mockResolvedValue(mockRole);
      roleRepository.save.mockResolvedValue({ ...mockRole, ...updateRoleDto } as Role);

      const result = await service.update(1, updateRoleDto);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
    });

    it('should throw BadRequestException when updating super admin', async () => {
      roleRepository.findOne.mockResolvedValue(mockSuperAdminRole);

      await expect(service.update(1, {})).rejects.toThrow(BadRequestException);
    });

    it('should throw NotFoundException when role not found', async () => {
      roleRepository.findOne.mockResolvedValue(null);

      await expect(service.update(999, {})).rejects.toThrow(NotFoundException);
    });
  });

  describe('remove', () => {
    it('should delete role', async () => {
      roleRepository.findOne.mockResolvedValue(mockRole);

      const result = await service.remove(1);

      expect(result).toBeInstanceOf(Result);
      expect(result.code).toBe(0);
      expect(rolePermissionRepository.delete).toHaveBeenCalledWith({ roleId: 1 });
      expect(roleRepository.delete).toHaveBeenCalledWith(1);
    });

    it('should throw BadRequestException when deleting super admin', async () => {
      roleRepository.findOne.mockResolvedValue(mockSuperAdminRole);

      await expect(service.remove(1)).rejects.toThrow(BadRequestException);
    });

    it('should throw NotFoundException when role not found', async () => {
      roleRepository.findOne.mockResolvedValue(null);

      await expect(service.remove(999)).rejects.toThrow(NotFoundException);
    });
  });
});
