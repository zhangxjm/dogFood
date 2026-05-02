import {
  Controller,
  Post,
  Body,
  Get,
  Request,
  UseGuards,
} from "@nestjs/common";
import { AuthGuard } from "@nestjs/passport";
import { AuthService } from "./auth.service";
import { LoginDto, LoginParams } from "./dto/login.dto";
import { Public } from "@/common/decorators/public.decorator";
import { Result } from "@/common/result";

@Controller("auth")
export class AuthController {
  constructor(private readonly authService: AuthService) {}

  @Public()
  @UseGuards(AuthGuard("local"))
  @Post("login")
  async login(@Body() loginDto: LoginDto, @Request() req) {
    const loginParams: LoginParams = {
      username: loginDto.username,
      password: loginDto.password,
      remember: loginDto.remember,
    };
    return this.authService.login(loginParams);
  }

  @Post("logout")
  async logout(@Request() req) {
    return Result.success(null, "退出成功");
  }

  @Get("userinfo")
  async getUserInfo(@Request() req) {
    const userId = req.user?.userId;
    return this.authService.getUserInfo(userId);
  }

  @Get("menus")
  async getMenus(@Request() req) {
    const userId = req.user?.userId;
    return this.authService.getMenus(userId);
  }

  @Get("permissions")
  async getPermissions(@Request() req) {
    return Result.success([]);
  }
}
