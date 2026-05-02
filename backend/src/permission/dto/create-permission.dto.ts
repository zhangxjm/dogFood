import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import {
  IsNotEmpty,
  IsString,
  IsNumber,
  IsOptional,
  Min,
} from 'class-validator';

export class CreatePermissionDto {
  @ApiProperty({ description: '权限名称', example: '用户管理' })
  @IsNotEmpty({ message: '权限名称不能为空' })
  @IsString({ message: '权限名称必须是字符串' })
  name: string;

  @ApiProperty({ description: '权限代码', example: 'user:list' })
  @IsNotEmpty({ message: '权限代码不能为空' })
  @IsString({ message: '权限代码必须是字符串' })
  code: string;

  @ApiProperty({ description: '类型：1-菜单，2-按钮，3-接口', example: 1 })
  @IsNotEmpty({ message: '权限类型不能为空' })
  @IsNumber({}, { message: '权限类型必须是数字' })
  type: number;

  @ApiPropertyOptional({ description: '父级ID', example: 0 })
  @IsOptional()
  @IsNumber({}, { message: '父级ID必须是数字' })
  parentId?: number;

  @ApiPropertyOptional({ description: '路由路径', example: '/system/user' })
  @IsOptional()
  @IsString({ message: '路由路径必须是字符串' })
  path?: string;

  @ApiPropertyOptional({ description: '组件路径', example: 'system/user/index' })
  @IsOptional()
  @IsString({ message: '组件路径必须是字符串' })
  component?: string;

  @ApiPropertyOptional({ description: '图标', example: 'User' })
  @IsOptional()
  @IsString({ message: '图标必须是字符串' })
  icon?: string;

  @ApiPropertyOptional({ description: '排序', example: 0 })
  @IsOptional()
  @IsNumber({}, { message: '排序必须是数字' })
  sort?: number;

  @ApiPropertyOptional({ description: '状态：1-启用，0-禁用', example: 1 })
  @IsOptional()
  @IsNumber({}, { message: '状态必须是数字' })
  status?: number;
}
