import {
  Controller,
  Get,
  Post,
  Put,
  Delete,
  Body,
  Param,
  Query,
  UseGuards,
} from '@nestjs/common';
import { ApiTags, ApiOperation, ApiBearerAuth, ApiQuery } from '@nestjs/swagger';
import { RoleService } from './role.service';
import { CreateRoleDto } from './dto/create-role.dto';
import { UpdateRoleDto } from './dto/update-role.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@ApiTags('角色管理')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('roles')
export class RoleController {
  constructor(private readonly roleService: RoleService) {}

  @Get()
  @ApiOperation({ summary: '获取角色列表' })
  @ApiQuery({ name: 'page', required: false, description: '页码' })
  @ApiQuery({ name: 'pageSize', required: false, description: '每页数量' })
  @ApiQuery({ name: 'name', required: false, description: '角色名称搜索' })
  @ApiQuery({ name: 'status', required: false, description: '状态筛选' })
  async findAll(
    @Query('page') page: string = '1',
    @Query('pageSize') pageSize: string = '10',
    @Query('name') name?: string,
    @Query('status') status?: string,
  ) {
    return this.roleService.findAll(
      parseInt(page),
      parseInt(pageSize),
      name,
      status !== undefined ? parseInt(status) : undefined,
    );
  }

  @Get('simple')
  @ApiOperation({ summary: '获取角色简单列表（用于下拉选择）' })
  async findAllSimple() {
    return this.roleService.findAllSimple();
  }

  @Get(':id')
  @ApiOperation({ summary: '获取单个角色信息' })
  async findOne(@Param('id') id: string) {
    return this.roleService.findById(parseInt(id));
  }

  @Post()
  @ApiOperation({ summary: '创建角色' })
  async create(@Body() createRoleDto: CreateRoleDto) {
    return this.roleService.create(createRoleDto);
  }

  @Put(':id')
  @ApiOperation({ summary: '更新角色信息' })
  async update(@Param('id') id: string, @Body() updateRoleDto: UpdateRoleDto) {
    return this.roleService.update(parseInt(id), updateRoleDto);
  }

  @Delete(':id')
  @ApiOperation({ summary: '删除角色' })
  async remove(@Param('id') id: string) {
    await this.roleService.delete(parseInt(id));
    return { message: '删除成功' };
  }

  @Post(':id/permissions')
  @ApiOperation({ summary: '分配角色权限' })
  async assignPermissions(
    @Param('id') id: string,
    @Body('permissionIds') permissionIds: number[],
  ) {
    return this.roleService.assignPermissions(parseInt(id), permissionIds);
  }
}
