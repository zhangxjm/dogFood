import {
  Controller,
  Get,
  Post,
  Put,
  Delete,
  Body,
  Param,
  Query,
  Request,
  UseGuards,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth, ApiQuery } from '@nestjs/swagger';
import { UserService } from './user.service';
import { CreateUserDto } from './dto/create-user.dto';
import { UpdateUserDto } from './dto/update-user.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@ApiTags('用户管理')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('users')
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Get()
  @ApiOperation({ summary: '获取用户列表' })
  @ApiQuery({ name: 'page', required: false, description: '页码' })
  @ApiQuery({ name: 'pageSize', required: false, description: '每页数量' })
  @ApiQuery({ name: 'username', required: false, description: '用户名搜索' })
  @ApiQuery({ name: 'status', required: false, description: '状态筛选' })
  async findAll(
    @Query('page') page: string = '1',
    @Query('pageSize') pageSize: string = '10',
    @Query('username') username?: string,
    @Query('status') status?: string,
  ) {
    return this.userService.findAll(
      parseInt(page),
      parseInt(pageSize),
      username,
      status !== undefined ? parseInt(status) : undefined,
    );
  }

  @Get('me')
  @ApiOperation({ summary: '获取当前用户信息' })
  async getProfile(@Request() req) {
    return this.userService.findById(req.user.id);
  }

  @Get('menus')
  @ApiOperation({ summary: '获取当前用户菜单' })
  async getMenus(@Request() req) {
    return this.userService.getMenus(req.user.id);
  }

  @Get(':id')
  @ApiOperation({ summary: '获取单个用户信息' })
  async findOne(@Param('id') id: string) {
    return this.userService.findById(parseInt(id));
  }

  @Post()
  @ApiOperation({ summary: '创建用户' })
  async create(@Body() createUserDto: CreateUserDto) {
    return this.userService.create(createUserDto);
  }

  @Put(':id')
  @ApiOperation({ summary: '更新用户信息' })
  async update(@Param('id') id: string, @Body() updateUserDto: UpdateUserDto) {
    return this.userService.update(parseInt(id), updateUserDto);
  }

  @Delete(':id')
  @ApiOperation({ summary: '删除用户' })
  async remove(@Param('id') id: string) {
    await this.userService.delete(parseInt(id));
    return { message: '删除成功' };
  }

  @Post(':id/reset-password')
  @ApiOperation({ summary: '重置用户密码' })
  async resetPassword(
    @Param('id') id: string,
    @Body('password') password: string,
  ) {
    await this.userService.resetPassword(parseInt(id), password);
    return { message: '密码重置成功' };
  }
}
