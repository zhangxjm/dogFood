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
import { PermissionService } from './permission.service';
import { CreatePermissionDto } from './dto/create-permission.dto';
import { UpdatePermissionDto } from './dto/update-permission.dto';
import { JwtAuthGuard } from '../auth/guards/jwt-auth.guard';

@ApiTags('权限管理')
@ApiBearerAuth()
@UseGuards(JwtAuthGuard)
@Controller('permissions')
export class PermissionController {
  constructor(private readonly permissionService: PermissionService) {}

  @Get()
  @ApiOperation({ summary: '获取权限列表' })
  @ApiQuery({ name: 'type', required: false, description: '权限类型：1-菜单，2-按钮，3-接口' })
  async findAll(@Query('type') type?: string) {
    return this.permissionService.findAll(
      type !== undefined ? parseInt(type) : undefined,
    );
  }

  @Get('tree')
  @ApiOperation({ summary: '获取权限树形结构' })
  async findTree() {
    return this.permissionService.findTree();
  }

  @Get(':id')
  @ApiOperation({ summary: '获取单个权限信息' })
  async findOne(@Param('id') id: string) {
    return this.permissionService.findById(parseInt(id));
  }

  @Post()
  @ApiOperation({ summary: '创建权限' })
  async create(@Body() createPermissionDto: CreatePermissionDto) {
    return this.permissionService.create(createPermissionDto);
  }

  @Put(':id')
  @ApiOperation({ summary: '更新权限信息' })
  async update(
    @Param('id') id: string,
    @Body() updatePermissionDto: UpdatePermissionDto,
  ) {
    return this.permissionService.update(parseInt(id), updatePermissionDto);
  }

  @Delete(':id')
  @ApiOperation({ summary: '删除权限' })
  async remove(@Param('id') id: string) {
    await this.permissionService.delete(parseInt(id));
    return { message: '删除成功' };
  }
}
