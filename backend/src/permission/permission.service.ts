import {
  Injectable,
  NotFoundException,
  ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository } from 'typeorm';
import { Permission } from '../entities/permission.entity';
import { CreatePermissionDto } from './dto/create-permission.dto';
import { UpdatePermissionDto } from './dto/update-permission.dto';

@Injectable()
export class PermissionService {
  constructor(
    @InjectRepository(Permission)
    private permissionRepository: Repository<Permission>,
  ) {}

  async findAll(type?: number): Promise<Permission[]> {
    const queryBuilder = this.permissionRepository
      .createQueryBuilder('permission')
      .where('1=1');

    if (type !== undefined) {
      queryBuilder.andWhere('permission.type = :type', { type });
    }

    const permissions = await queryBuilder
      .orderBy('permission.sort', 'ASC')
      .getMany();

    return permissions;
  }

  async findTree(): Promise<any[]> {
    const permissions = await this.permissionRepository.find({
      order: { sort: 'ASC' },
    });
    return this.buildTree(permissions);
  }

  private buildTree(permissions: Permission[]): any[] {
    const map = new Map<number, any>();
    const roots: any[] = [];

    permissions.forEach((p) => {
      map.set(p.id, {
        ...p,
        children: [],
      });
    });

    permissions.forEach((p) => {
      const node = map.get(p.id);
      if (p.parentId === 0) {
        roots.push(node);
      } else {
        const parent = map.get(p.parentId);
        if (parent) {
          parent.children.push(node);
        }
      }
    });

    return roots;
  }

  async findById(id: number): Promise<Permission> {
    const permission = await this.permissionRepository.findOne({
      where: { id },
    });
    if (!permission) {
      throw new NotFoundException(`权限 ID ${id} 不存在`);
    }
    return permission;
  }

  async create(createPermissionDto: CreatePermissionDto): Promise<Permission> {
    const existingPermission = await this.permissionRepository.findOne({
      where: { code: createPermissionDto.code },
    });
    if (existingPermission) {
      throw new ConflictException('权限代码已存在');
    }

    const permission = this.permissionRepository.create(createPermissionDto);
    return this.permissionRepository.save(permission);
  }

  async update(
    id: number,
    updatePermissionDto: UpdatePermissionDto,
  ): Promise<Permission> {
    const permission = await this.findById(id);

    if (updatePermissionDto.code && updatePermissionDto.code !== permission.code) {
      const existingPermission = await this.permissionRepository.findOne({
        where: { code: updatePermissionDto.code },
      });
      if (existingPermission) {
        throw new ConflictException('权限代码已存在');
      }
    }

    Object.assign(permission, updatePermissionDto);
    return this.permissionRepository.save(permission);
  }

  async delete(id: number): Promise<void> {
    const permission = await this.findById(id);
    
    const children = await this.permissionRepository.find({
      where: { parentId: id },
    });
    if (children.length > 0) {
      throw new ConflictException('存在子权限，无法删除');
    }

    await this.permissionRepository.remove(permission);
  }
}
