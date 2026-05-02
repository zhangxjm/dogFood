import { Test, TestingModule } from '@nestjs/testing';
import { RoleController } from './role.controller';
import { RoleService } from './role.service';
import { CreateRoleDto, UpdateRoleDto, QueryRoleDto } from './dto/role.dto';
import { Result } from '@/common/result';
import { Role } from './role.entity';
import { PageResultDto } from '@/common/dto/page.dto';

jest.mock('./role.service');

describe('RoleController', () => {
  let controller: RoleController;
  let service: jest.Mocked<RoleService>;

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
    const module: TestingModule = await Test.createTestingModule({
      controllers: [RoleController],
      providers: [RoleService],
    }).compile();

    controller = module.get<RoleController>(RoleController);
    service = module.get(RoleService) as jest.Mocked<RoleService>;
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('findAll', () => {
    it('should return role list', async () => {
      const query: QueryRoleDto = { page: 1, pageSize: 10 };
      const expectedResult = Result.success(
        new PageResultDto([mockRole], 1, 1, 10),
      );
      service.findAll.mockResolvedValue(expectedResult);

      const result = await controller.findAll(query);

      expect(result).toEqual(expectedResult);
      expect(service.findAll).toHaveBeenCalledWith(query);
    });
  });

  describe('findAllSimple', () => {
    it('should return all simple roles', async () => {
      const expectedResult = Result.success([mockRole]);
      service.findAllSimple.mockResolvedValue(expectedResult);

      const result = await controller.findAllSimple();

      expect(result).toEqual(expectedResult);
      expect(service.findAllSimple).toHaveBeenCalled();
    });
  });

  describe('findOne', () => {
    it('should return a role by id', async () => {
      const expectedResult = Result.success(mockRole);
      service.findOne.mockResolvedValue(expectedResult);

      const result = await controller.findOne('1');

      expect(result).toEqual(expectedResult);
      expect(service.findOne).toHaveBeenCalledWith(1);
    });
  });

  describe('create', () => {
    it('should create a role', async () => {
      const createRoleDto: CreateRoleDto = {
        name: '新角色',
        code: 'NEW_ROLE',
        description: '新角色描述',
      };
      const expectedResult = Result.success(mockRole, '创建成功');
      service.create.mockResolvedValue(expectedResult);

      const result = await controller.create(createRoleDto);

      expect(result).toEqual(expectedResult);
      expect(service.create).toHaveBeenCalledWith(createRoleDto);
    });
  });

  describe('update', () => {
    it('should update a role', async () => {
      const updateRoleDto: UpdateRoleDto = {
        name: '更新后的角色',
      };
      const expectedResult = Result.success(mockRole, '更新成功');
      service.update.mockResolvedValue(expectedResult);

      const result = await controller.update('1', updateRoleDto);

      expect(result).toEqual(expectedResult);
      expect(service.update).toHaveBeenCalledWith(1, updateRoleDto);
    });
  });

  describe('remove', () => {
    it('should delete a role', async () => {
      const expectedResult = Result.success(null, '删除成功');
      service.remove.mockResolvedValue(expectedResult);

      const result = await controller.remove('1');

      expect(result).toEqual(expectedResult);
      expect(service.remove).toHaveBeenCalledWith(1);
    });
  });
});
