import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, In } from "typeorm";
import { Permission } from "./permission.entity";
import {
  CreatePermissionDto,
  UpdatePermissionDto,
  QueryPermissionDto,
} from "./dto/permission.dto";
import { Result } from "@/common/result";

@Injectable()
export class PermissionService {
  constructor(
    @InjectRepository(Permission)
    private permissionRepository: Repository<Permission>,
  ) {}

  async findAll(query: QueryPermissionDto): Promise<Result<Permission[]>> {
    const { name, type, status } = query;

    const qb = this.permissionRepository.createQueryBuilder("permission");

    if (name) {
      qb.andWhere("permission.name LIKE :name", { name: `%${name}%` });
    }

    if (type !== undefined) {
      qb.andWhere("permission.type = :type", { type });
    }

    if (status !== undefined) {
      qb.andWhere("permission.status = :status", { status });
    }

    qb.orderBy("permission.sort", "ASC").addOrderBy("permission.id", "ASC");

    const list = await qb.getMany();
    const tree = this.buildTree(list);

    return Result.success(tree);
  }

  async findAllList(): Promise<Result<Permission[]>> {
    const list = await this.permissionRepository.find({
      where: { status: 1 },
      order: { sort: "ASC", id: "ASC" },
    });

    const tree = this.buildTree(list);
    return Result.success(tree);
  }

  async findOne(id: number): Promise<Result<Permission>> {
    const permission = await this.permissionRepository.findOne({
      where: { id },
    });

    if (!permission) {
      throw new NotFoundException("权限不存在");
    }

    return Result.success(permission);
  }

  async create(
    createPermissionDto: CreatePermissionDto,
  ): Promise<Result<Permission>> {
    const { code, parentId = 0 } = createPermissionDto;

    const existingPermission = await this.permissionRepository.findOne({
      where: { code },
    });

    if (existingPermission) {
      throw new BadRequestException("权限编码已存在");
    }

    if (parentId > 0) {
      const parent = await this.permissionRepository.findOne({
        where: { id: parentId },
      });

      if (!parent) {
        throw new BadRequestException("父级权限不存在");
      }
    }

    const permission = this.permissionRepository.create({
      ...createPermissionDto,
      parentId: parentId,
    });

    const savedPermission = await this.permissionRepository.save(permission);

    return Result.success(savedPermission, "创建成功");
  }

  async update(
    id: number,
    updatePermissionDto: UpdatePermissionDto,
  ): Promise<Result<Permission>> {
    const permission = await this.permissionRepository.findOne({
      where: { id },
    });

    if (!permission) {
      throw new NotFoundException("权限不存在");
    }

    Object.assign(permission, updatePermissionDto);

    const updatedPermission = await this.permissionRepository.save(permission);

    return Result.success(updatedPermission, "更新成功");
  }

  async remove(id: number): Promise<Result<null>> {
    const permission = await this.permissionRepository.findOne({
      where: { id },
    });

    if (!permission) {
      throw new NotFoundException("权限不存在");
    }

    const children = await this.permissionRepository.find({
      where: { parentId: id },
    });

    if (children.length > 0) {
      throw new BadRequestException("存在子权限，无法删除");
    }

    await this.permissionRepository.delete(id);

    return Result.success(null, "删除成功");
  }

  private buildTree(items: Permission[]): Permission[] {
    const map = new Map<number, any>();
    const roots: any[] = [];

    items.forEach((item) => {
      map.set(item.id, { ...item, children: [] });
    });

    items.forEach((item) => {
      const node = map.get(item.id);
      if (item.parentId === 0 || !map.has(item.parentId)) {
        roots.push(node);
      } else {
        const parent = map.get(item.parentId);
        parent?.children.push(node);
      }
    });

    return roots;
  }
}
