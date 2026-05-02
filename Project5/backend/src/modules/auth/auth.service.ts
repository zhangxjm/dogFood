import { Injectable, UnauthorizedException } from "@nestjs/common";
import { JwtService } from "@nestjs/jwt";
import { InjectRepository } from "@nestjs/typeorm";
import { Repository, In } from "typeorm";
import * as bcrypt from "bcryptjs";
import { User } from "@/modules/user/user.entity";
import { UserRole } from "@/modules/user/user-role.entity";
import { Role } from "@/modules/role/role.entity";
import { RolePermission } from "@/modules/role/role-permission.entity";
import { Permission } from "@/modules/permission/permission.entity";
import { LoginParams, LoginResult, UserInfo } from "./dto/login.dto";
import { Result } from "@/common/result";

@Injectable()
export class AuthService {
  constructor(
    private jwtService: JwtService,
    @InjectRepository(User)
    private userRepository: Repository<User>,
    @InjectRepository(UserRole)
    private userRoleRepository: Repository<UserRole>,
    @InjectRepository(Role)
    private roleRepository: Repository<Role>,
    @InjectRepository(RolePermission)
    private rolePermissionRepository: Repository<RolePermission>,
    @InjectRepository(Permission)
    private permissionRepository: Repository<Permission>,
  ) {}

  async validateUser(username: string, password: string): Promise<User | null> {
    const user = await this.userRepository.findOne({
      where: { username },
    });

    if (user && (await bcrypt.compare(password, user.password))) {
      return user;
    }
    return null;
  }

  async login(loginParams: LoginParams): Promise<Result<LoginResult>> {
    const { username, password } = loginParams;

    const user = await this.validateUser(username, password);

    if (!user) {
      throw new UnauthorizedException("用户名或密码错误");
    }

    if (user.status !== 1) {
      throw new UnauthorizedException("账号已被禁用");
    }

    const payload = { username: user.username, sub: user.id };
    const token = this.jwtService.sign(payload);

    const userRoles = await this.getUserRoles(user.id);
    const userPermissions = await this.getUserPermissions(userRoles);

    const userInfo: UserInfo = {
      id: user.id,
      username: user.username,
      nickname: user.nickname || user.username,
      avatar: user.avatar || "",
      email: user.email || "",
      phone: user.phone || "",
    };

    return Result.success(
      {
        token,
        userInfo,
        roles: userRoles.map((r) => r.code),
        permissions: userPermissions.map((p) => p.code),
      },
      "登录成功",
    );
  }

  async getUserInfo(userId: number): Promise<Result<any>> {
    const user = await this.userRepository.findOne({
      where: { id: userId },
    });

    if (!user) {
      throw new UnauthorizedException("用户不存在");
    }

    const userRoles = await this.getUserRoles(user.id);
    const userPermissions = await this.getUserPermissions(userRoles);

    const userInfo: UserInfo = {
      id: user.id,
      username: user.username,
      nickname: user.nickname || user.username,
      avatar: user.avatar || "",
      email: user.email || "",
      phone: user.phone || "",
    };

    return Result.success({
      userInfo,
      roles: userRoles.map((r) => r.code),
      permissions: userPermissions.map((p) => p.code),
    });
  }

  async getMenus(userId: number): Promise<Result<any[]>> {
    const userRoles = await this.getUserRoles(userId);

    let menus: Permission[];

    if (userRoles.some((r) => r.code === "SUPER_ADMIN")) {
      menus = await this.permissionRepository.find({
        where: { type: 1, status: 1 },
        order: { sort: "ASC", id: "ASC" },
      });
    } else {
      const roleIds = userRoles.map((r) => r.id);
      const rolePermissions = await this.rolePermissionRepository.find({
        where: { roleId: In(roleIds) },
      });

      const permissionIds = rolePermissions.map((rp) => rp.permissionId);
      menus = await this.permissionRepository.find({
        where: { id: In(permissionIds), type: 1, status: 1 },
        order: { sort: "ASC", id: "ASC" },
      });
    }

    const menuTree = this.buildMenuTree(menus);

    return Result.success(menuTree);
  }

  private async getUserRoles(userId: number): Promise<Role[]> {
    const userRoles = await this.userRoleRepository.find({
      where: { userId },
    });

    if (userRoles.length === 0) {
      return [];
    }

    const roleIds = userRoles.map((ur) => ur.roleId);
    return this.roleRepository.find({
      where: { id: In(roleIds), status: 1 },
    });
  }

  private async getUserPermissions(roles: Role[]): Promise<Permission[]> {
    if (roles.length === 0) {
      return [];
    }

    if (roles.some((r) => r.code === "SUPER_ADMIN")) {
      return this.permissionRepository.find({
        where: { status: 1 },
      });
    }

    const roleIds = roles.map((r) => r.id);
    const rolePermissions = await this.rolePermissionRepository.find({
      where: { roleId: In(roleIds) },
    });

    if (rolePermissions.length === 0) {
      return [];
    }

    const permissionIds = rolePermissions.map((rp) => rp.permissionId);
    return this.permissionRepository.find({
      where: { id: In(permissionIds), status: 1 },
    });
  }

  private buildMenuTree(menus: Permission[]): any[] {
    const menuMap = new Map<number, any>();
    const roots: any[] = [];

    menus.forEach((menu) => {
      menuMap.set(menu.id, {
        path: menu.path || "",
        name: menu.name,
        component: menu.component || "",
        meta: {
          title: menu.name,
          icon: menu.icon || "",
        },
        children: [],
        parentId: menu.parentId,
      });
    });

    menus.forEach((menu) => {
      const node = menuMap.get(menu.id);
      if (menu.parentId === 0 || !menuMap.has(menu.parentId)) {
        roots.push(node);
      } else {
        const parent = menuMap.get(menu.parentId);
        parent?.children.push(node);
      }
    });

    return this.sortMenus(roots);
  }

  private sortMenus(menus: any[]): any[] {
    return menus
      .sort((a, b) => (a.sort || 0) - (b.sort || 0))
      .map((menu) => ({
        ...menu,
        children:
          menu.children?.length > 0 ? this.sortMenus(menu.children) : undefined,
      }));
  }
}
