import { ApiProperty, ApiPropertyOptional } from '@nestjs/swagger';
import {
  IsNotEmpty,
  IsString,
  IsOptional,
  IsArray,
} from 'class-validator';

export class CreateRoleDto {
  @ApiProperty({ description: '角色名称', example: '管理员' })
  @IsNotEmpty({ message: '角色名称不能为空' })
  @IsString({ message: '角色名称必须是字符串' })
  name: string;

  @ApiProperty({ description: '角色代码', example: 'admin' })
  @IsNotEmpty({ message: '角色代码不能为空' })
  @IsString({ message: '角色代码必须是字符串' })
  code: string;

  @ApiPropertyOptional({ description: '角色描述' })
  @IsOptional()
  @IsString({ message: '角色描述必须是字符串' })
  description?: string;

  @ApiPropertyOptional({ description: '状态：1-启用，0-禁用', example: 1 })
  @IsOptional()
  status?: number;

  @ApiPropertyOptional({ description: '权限ID列表', example: [1, 2, 3] })
  @IsOptional()
  @IsArray({ message: '权限ID必须是数组' })
  permissionIds?: number[];
}
