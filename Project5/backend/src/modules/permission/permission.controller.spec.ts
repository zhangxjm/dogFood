import { Test, TestingModule } from '@nestjs/testing';
import { PermissionController } from './permission.controller';
import { PermissionService } from './permission.service';
import { CreatePermissionDto, UpdatePermissionDto, QueryPermissionDto } from './dto/permission.dto';
import { Result } from '@/common/result';
import { Permission } from './permission.entity';

jest.mock('./permission.service');

describe('PermissionController', () => {
  let controller: PermissionController;
  let service: jest.Mocked<PermissionService>;

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
      controllers: [PermissionController],
      providers: [PermissionService],
    }).compile();

    controller = module.get<PermissionController>(PermissionController);
    service = module.get(PermissionService) as jest.Mocked<PermissionService>;
  });

  it('should be defined', () => {
    expect(controller).toBeDefined();
  });

  describe('findAll', () => {
    it('should return permission list', async () => {
      const query: QueryPermissionDto = { name: '系统' };
      const expectedResult = Result.success([mockPermission]);
      service.findAll.mockResolvedValue(expectedResult);

      const result = await controller.findAll(query);

      expect(result).toEqual(expectedResult);
      expect(service.findAll).toHaveBeenCalledWith(query);
    });
  });

  describe('findAllList', () => {
    it('should return all permission list', async () => {
      const expectedResult = Result.success([mockPermission]);
      service.findAllList.mockResolvedValue(expectedResult);

      const result = await controller.findAllList();

      expect(result).toEqual(expectedResult);
      expect(service.findAllList).toHaveBeenCalled();
    });
  });

  describe('findOne', () => {
    it('should return a permission by id', async () => {
      const expectedResult = Result.success(mockPermission);
      service.findOne.mockResolvedValue(expectedResult);

      const result = await controller.findOne('1');

      expect(result).toEqual(expectedResult);
      expect(service.findOne).toHaveBeenCalledWith(1);
    });
  });

  describe('create', () => {
    it('should create a permission', async () => {
      const createPermissionDto: CreatePermissionDto = {
        name: '新权限',
        code: 'new:permission',
        type: 1,
        parentId: 0,
      };
      const expectedResult = Result.success(mockPermission, '创建成功');
      service.create.mockResolvedValue(expectedResult);

      const result = await controller.create(createPermissionDto);

      expect(result).toEqual(expectedResult);
      expect(service.create).toHaveBeenCalledWith(createPermissionDto);
    });
  });

  describe('update', () => {
    it('should update a permission', async () => {
      const updatePermissionDto: UpdatePermissionDto = {
        name: '更新后的权限',
      };
      const expectedResult = Result.success(mockPermission, '更新成功');
      service.update.mockResolvedValue(expectedResult);

      const result = await controller.update('1', updatePermissionDto);

      expect(result).toEqual(expectedResult);
      expect(service.update).toHaveBeenCalledWith(1, updatePermissionDto);
    });
  });

  describe('remove', () => {
    it('should delete a permission', async () => {
      const expectedResult = Result.success(null, '删除成功');
      service.remove.mockResolvedValue(expectedResult);

      const result = await controller.remove('1');

      expect(result).toEqual(expectedResult);
      expect(service.remove).toHaveBeenCalledWith(1);
    });
  });
});
