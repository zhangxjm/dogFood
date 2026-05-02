import {
  Injectable,
  NotFoundException,
  BadRequestException,
} from "@nestjs/common";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, In, Like } from "typeorm";
import * as bcrypt from "bcryptjs";
import { User } from "./user.entity";
import { UserRole } from "./user-role.entity";
import { Role } from "@/modules/role/role.entity";
import { CreateUserDto, UpdateUserDto, QueryUserDto } from "./dto/user.dto";
import { Result } from "@/common/result";
import { PageResultDto } from "@/common/dto/page.dto";

@Injectable()
export class UserService {
  constructor(
    @InjectRepository(User)
    private userRepository: Repository<User>,
    @InjectRepository(UserRole)
    private userRoleRepository: Repository<UserRole>,
    @InjectRepository(Role)
    private roleRepository: Repository<Role>,
  ) {}

  async findAll(query: QueryUserDto): Promise<Result<PageResultDto<User>>> {
    const { page = 1, pageSize = 10, username, nickname, status } = query;

    const qb = this.userRepository
      .createQueryBuilder("user")
      .leftJoinAndSelect("user_roles", "ur", "ur.user_id = user.id")
      .leftJoinAndMapMany("user.roles", Role, "role", "role.id = ur.role_id");

    if (username) {
      qb.andWhere("user.username LIKE :username", {
        username: `%${username}%`,
      });
    }

    if (nickname) {
      qb.andWhere("user.nickname LIKE :nickname", {
        nickname: `%${nickname}%`,
      });
    }

    if (status !== undefined) {
      qb.andWhere("user.status = :status", { status });
    }

    qb.orderBy("user.id", "DESC");
    qb.skip((page - 1) * pageSize).take(pageSize);

    const [list, total] = await qb.getManyAndCount();

    return Result.success(new PageResultDto(list, total, page, pageSize));
  }

  async findOne(id: number): Promise<Result<User>> {
    const user = await this.userRepository.findOne({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException("用户不存在");
    }

    const userRoles = await this.userRoleRepository.find({
      where: { userId: id },
    });

    const roleIds = userRoles.map((ur) => ur.roleId);
    const roles = await this.roleRepository.find({
      where: { id: In(roleIds) },
    });

    (user as any).roleIds = roleIds;
    (user as any).roles = roles;

    return Result.success(user);
  }

  async create(createUserDto: CreateUserDto): Promise<Result<User>> {
    const { username, password, roleIds, ...rest } = createUserDto;

    const existingUser = await this.userRepository.findOne({
      where: { username },
    });

    if (existingUser) {
      throw new BadRequestException("用户名已存在");
    }

    const hashedPassword = await bcrypt.hash(password, 10);

    const user = this.userRepository.create({
      username,
      password: hashedPassword,
      ...rest,
    });

    const savedUser = await this.userRepository.save(user);

    if (roleIds && roleIds.length > 0) {
      const userRoles = roleIds.map((roleId) => ({
        userId: savedUser.id,
        roleId,
      }));
      await this.userRoleRepository.save(userRoles);
    }

    return Result.success(savedUser, "创建成功");
  }

  async update(
    id: number,
    updateUserDto: UpdateUserDto,
  ): Promise<Result<User>> {
    const { password, roleIds, ...rest } = updateUserDto;

    const user = await this.userRepository.findOne({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException("用户不存在");
    }

    if (password) {
      user.password = await bcrypt.hash(password, 10);
    }

    Object.assign(user, rest);

    const updatedUser = await this.userRepository.save(user);

    if (roleIds !== undefined) {
      await this.userRoleRepository.delete({ userId: id });

      if (roleIds.length > 0) {
        const userRoles = roleIds.map((roleId) => ({
          userId: id,
          roleId,
        }));
        await this.userRoleRepository.save(userRoles);
      }
    }

    return Result.success(updatedUser, "更新成功");
  }

  async remove(id: number): Promise<Result<null>> {
    const user = await this.userRepository.findOne({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException("用户不存在");
    }

    if (user.username === "admin") {
      throw new BadRequestException("超级管理员不可删除");
    }

    await this.userRoleRepository.delete({ userId: id });
    await this.userRepository.delete(id);

    return Result.success(null, "删除成功");
  }

  async toggleStatus(id: number): Promise<Result<User>> {
    const user = await this.userRepository.findOne({
      where: { id },
    });

    if (!user) {
      throw new NotFoundException("用户不存在");
    }

    if (user.username === "admin") {
      throw new BadRequestException("超级管理员状态不可修改");
    }

    user.status = user.status === 1 ? 0 : 1;
    const updatedUser = await this.userRepository.save(user);

    return Result.success(updatedUser, "状态更新成功");
  }
}
