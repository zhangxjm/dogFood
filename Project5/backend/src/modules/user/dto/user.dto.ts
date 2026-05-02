import {
  IsString,
  IsOptional,
  IsEmail,
  IsPhoneNumber,
  IsInt,
  Min,
} from "class-validator";
import { Type } from "class-transformer";

export class CreateUserDto {
  @IsString()
  username: string;

  @IsString()
  password: string;

  @IsOptional()
  @IsString()
  nickname?: string;

  @IsOptional()
  @IsEmail()
  email?: string;

  @IsOptional()
  @IsPhoneNumber("CN")
  phone?: string;

  @IsOptional()
  @Type(() => Number)
  @IsInt({ each: true })
  roleIds?: number[];
}

export class UpdateUserDto {
  @IsOptional()
  @IsString()
  password?: string;

  @IsOptional()
  @IsString()
  nickname?: string;

  @IsOptional()
  @IsEmail()
  email?: string;

  @IsOptional()
  @IsPhoneNumber("CN")
  phone?: string;

  @IsOptional()
  @IsInt()
  status?: number;

  @IsOptional()
  @Type(() => Number)
  @IsInt({ each: true })
  roleIds?: number[];
}

export class QueryUserDto {
  @IsOptional()
  @IsString()
  username?: string;

  @IsOptional()
  @IsString()
  nickname?: string;

  @IsOptional()
  @Type(() => Number)
  @IsInt()
  status?: number;

  @IsOptional()
  @Type(() => Number)
  @IsInt()
  @Min(1)
  page?: number;

  @IsOptional()
  @Type(() => Number)
  @IsInt()
  @Min(1)
  pageSize?: number;
}
