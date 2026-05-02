import {
  Injectable,
  NotFoundException,
  ConflictException,
} from '@nestjs/common';
import { InjectRepository } from '@nestjs/typeorm';
import { Repository, In } from 'typeorm';
import * as bcrypt from 'bcrypt';
import { ConfigService } from '@nestjs/config';
import { User } from '../entities/user.entity';
import { Role } from '../entities/role.entity';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';

@Injectable()
export class UserService {
  private readonly saltRounds: number;

  constructor(
    @InjectRepository(User)
    private userRepository: Repository<User>,
    @InjectRepository(Role)
    private roleRepository: Repository<Role>,
    private configService: ConfigService,
  ) {
    this.saltRounds = this.configService.get('BCRYPT_SALT_ROUNDS') || 10;
  }

  async findByUsername(username: string): Promise<User | undefined> {
    return this.userRepository.findOne({
      where: { username },
      relations: ['roles', 'roles.permissions'],
    });
  }

  async findById(id: number): Promise<User> {
    const user = await this.userRepository.findOne({
      where: { id },
      relations: ['roles', 'roles.permissions'],
    });
    if (!user) {
      throw new NotFoundException(`用户 ID ${id} 不存在`);
    }
    return user;
  }

  async findAll(
    page: number = 1,
    pageSize: number = 10,
    username?: string,
    status?: number,
  ): Promise<{ list: User[]; total: number; page: number; pageSize: number }> {
    const queryBuilder = this.userRepository
      .createQueryBuilder('user')
      .leftJoinAndSelect('user.roles', 'role')
      .where('1=1');

    if (username) {
      queryBuilder.andWhere('user.username LIKE :username', {
        username: `%${username}%`,
      });
    }

    if (status !== undefined) {
      queryBuilder.andWhere('user.status = :status', { status });
    }

    const [list, total] = await queryBuilder
      .orderBy('user.createdAt', 'DESC')
      .skip((page - 1) * pageSize)
      .take(pageSize)
      .getManyAndCount();

    list.forEach((user) => delete user.password);

    return { list, total, page, pageSize };
  }

  async create(createUserDto: CreateUserDto): Promise<User> {
    const existingUser = await this.findByUsername(createUserDto.username);
    if (existingUser) {
      throw new ConflictException('用户名已存在');
    }

    const hashedPassword = await bcrypt.hash(
      createUserDto.password,
      this.saltRounds,
    );

    const user = this.userRepository.create({
      ...createUserDto,
      password: hashedPassword,
    });

    if (createUserDto.roleIds && createUserDto.roleIds.length > 0) {
      const roles = await this.roleRepository.findBy({
        id: In(createUserDto.roleIds),
      });
      user.roles = roles;
    }

    const savedUser = await this.userRepository.save(user);
    delete savedUser.password;
    return savedUser;
  }

  async update(id: number, updateUserDto: UpdateUserDto): Promise<User> {
    const user = await this.findById(id);

    if (updateUserDto.roleIds && updateUserDto.roleIds.length > 0) {
      const roles = await this.roleRepository.findBy({
        id: In(updateUserDto.roleIds),
      });
      user.roles = roles;
    }

    Object.assign(user, updateUserDto);
    const updatedUser = await this.userRepository.save(user);
    delete updatedUser.password;
    return updatedUser;
  }

  async delete(id: number): Promise<void> {
    const user = await this.findById(id);
    await this.userRepository.remove(user);
  }

  async resetPassword(id: number, newPassword: string): Promise<void> {
    const user = await this.findById(id);
    user.password = await bcrypt.hash(newPassword, this.saltRounds);
    await this.userRepository.save(user);
  }

  async getMenus(userId: number) {
    const user = await this.userRepository.findOne({
      where: { id: userId },
      relations: ['roles', 'roles.permissions'],
    });

    if (!user) {
      return [];
    }

    const permissionMap = new Map<number, any>();
    const menuPermissions = [];

    user.roles?.forEach((role) => {
      role.permissions?.forEach((perm) => {
        if (perm.type === 1 && !permissionMap.has(perm.id)) {
          permissionMap.set(perm.id, perm);
          menuPermissions.push(perm);
        }
      });
    });

    const menus = this.buildMenuTree(menuPermissions);
    return menus.sort((a, b) => a.sort - b.sort);
  }

  private buildMenuTree(permissions: any[]): any[] {
    const map = new Map<number, any>();
    const roots: any[] = [];

    permissions.forEach((p) => {
      map.set(p.id, {
        id: p.id,
        name: p.name,
        path: p.path,
        component: p.component,
        icon: p.icon,
        sort: p.sort,
        children: [],
      });
    });

    permissions.forEach((p) => {
      const menu = map.get(p.id);
      if (p.parentId === 0) {
        roots.push(menu);
      } else {
        const parent = map.get(p.parentId);
        if (parent) {
          parent.children.push(menu);
        }
      }
    });

    roots.forEach((root) => {
      root.children = root.children.sort((a: any, b: any) => a.sort - b.sort);
    });

    return roots;
  }
}
