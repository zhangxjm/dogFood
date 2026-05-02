import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, In, Like } from "typeorm";
import { Role } from "./role.entity";
import { RolePermission } from "./role-permission.entity";
import { Permission } from "@/modules/permission/permission.entity";
import { CreateRoleDto, UpdateRoleDto, QueryRoleDto } from "./dto/role.dto";
import { Result } from "@/common/result";
import { PageResultDto } from "@/common/dto/page.dto";

@Injectable()
export class RoleService {
  constructor(
    @InjectRepository(Role)
    private roleRepository: Repository<Role>,
    @InjectRepository(RolePermission)
    private rolePermissionRepository: Repository<RolePermission>,
    @InjectRepository(Permission)
    private permissionRepository: Repository<Permission>,
  ) {}

  async findAll(query: QueryRoleDto): Promise<Result<PageResultDto<Role>>> {
    const { page = 1, pageSize = 10, name, code, status } = query;

    const qb = this.roleRepository.createQueryBuilder("role");

    if (name) {
      qb.andWhere("role.name LIKE :name", { name: `%${name}%` });
    }

    if (code) {
      qb.andWhere("role.code LIKE :code", { code: `%${code}%` });
    }

    if (status !== undefined) {
      qb.andWhere("role.status = :status", { status });
    }

    qb.orderBy("role.id", "DESC");
    qb.skip((page - 1) * pageSize).take(pageSize);

    const [list, total] = await qb.getManyAndCount();

    return Result.success(new PageResultDto(list, total, page, pageSize));
  }

  async findAllSimple(): Promise<Result<Role[]>> {
    const roles = await this.roleRepository.find({
      where: { status: 1 },
      order: { sort: "ASC", id: "ASC" },
    });
    return Result.success(roles);
  }

  async findOne(id: number): Promise<Result<Role>> {
    const role = await this.roleRepository.findOne({
      where: { id },
    });

    if (!role) {
      throw new NotFoundException("角色不存在");
    }

    const rolePermissions = await this.rolePermissionRepository.find({
      where: { roleId: id },
    });

    const permissionIds = rolePermissions.map((rp) => rp.permissionId);
    (role as any).permissionIds = permissionIds;

    return Result.success(role);
  }

  async create(createRoleDto: CreateRoleDto): Promise<Result<Role>> {
    const { code } = createRoleDto;

    const existingRole = await this.roleRepository.findOne({
      where: { code },
    });

    if (existingRole) {
      throw new BadRequestException("角色编码已存在");
    }

    const role = this.roleRepository.create(createRoleDto);
    const savedRole = await this.roleRepository.save(role);

    return Result.success(savedRole, "创建成功");
  }

  async update(
    id: number,
    updateRoleDto: UpdateRoleDto,
  ): Promise<Result<Role>> {
    const { permissionIds, ...rest } = updateRoleDto;

    const role = await this.roleRepository.findOne({
      where: { id },
    });

    if (!role) {
      throw new NotFoundException("角色不存在");
    }

    if (role.code === "SUPER_ADMIN") {
      throw new BadRequestException("超级管理员角色不可修改");
    }

    Object.assign(role, rest);

    const updatedRole = await this.roleRepository.save(role);

    if (permissionIds !== undefined) {
      await this.rolePermissionRepository.delete({ roleId: id });

      if (permissionIds.length > 0) {
        const rolePermissions = permissionIds.map((permissionId) => ({
          roleId: id,
          permissionId,
        }));
        await this.rolePermissionRepository.save(rolePermissions);
      }
    }

    return Result.success(updatedRole, "更新成功");
  }

  async remove(id: number): Promise<Result<null>> {
    const role = await this.roleRepository.findOne({
      where: { id },
    });

    if (!role) {
      throw new NotFoundException("角色不存在");
    }

    if (role.code === "SUPER_ADMIN") {
      throw new BadRequestException("超级管理员角色不可删除");
    }

    await this.rolePermissionRepository.delete({ roleId: id });
    await this.roleRepository.delete(id);

    return Result.success(null, "删除成功");
  }
}
