import { IsNotEmpty, IsString, IsOptional, IsBoolean } from "class-validator";

export class LoginDto {
  @IsNotEmpty({ message: "用户名不能为空" })
  @IsString()
  username: string;

  @IsNotEmpty({ message: "密码不能为空" })
  @IsString()
  password: string;

  @IsOptional()
  @IsBoolean()
  remember?: boolean;
}

export interface LoginParams {
  username: string;
  password: string;
  remember?: boolean;
}

export interface UserInfo {
  id: number;
  username: string;
  nickname: string;
  avatar: string;
  email: string;
  phone: string;
}

export interface LoginResult {
  token: string;
  userInfo: UserInfo;
  roles: string[];
  permissions: string[];
}
