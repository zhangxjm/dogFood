import { Injectable, UnauthorizedException } from '@nestjs/common';
import { JwtService } from '@nestjs/jwt';
import * as bcrypt from 'bcrypt';
import { UserService } from '../user/user.service';
import { User } from '../entities/user.entity';

@Injectable()
export class AuthService {
  constructor(
    private userService: UserService,
    private jwtService: JwtService,
  ) {}

  async validateUser(username: string, password: string): Promise<any> {
    const user = await this.userService.findByUsername(username);
    if (user && (await bcrypt.compare(password, user.password))) {
      const { password: _, ...result } = user;
      return result;
    }
    return null;
  }

  async login(user: User) {
    const payload = { username: user.username, sub: user.id };
    const userInfo = await this.userService.findById(user.id);
    
    const roles = userInfo.roles?.map((r) => r.code) || [];
    const permissions = this.extractPermissions(userInfo);

    return {
      accessToken: this.jwtService.sign(payload),
      userInfo: {
        id: userInfo.id,
        username: userInfo.username,
        nickname: userInfo.nickname,
        avatar: userInfo.avatar,
        email: userInfo.email,
        phone: userInfo.phone,
        roles,
        permissions,
      },
    };
  }

  private extractPermissions(user: User): string[] {
    const permissions = new Set<string>();
    user.roles?.forEach((role) => {
      role.permissions?.forEach((perm) => {
        if (perm.code) {
          permissions.add(perm.code);
        }
      });
    });
    return Array.from(permissions);
  }

  async getProfile(userId: number) {
    const user = await this.userService.findById(userId);
    const { password: _, ...result } = user;
    return result;
  }
}
