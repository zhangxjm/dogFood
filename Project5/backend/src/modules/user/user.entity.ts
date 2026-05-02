import { Entity, Column, ManyToMany, JoinTable } from "typeorm";
import { BaseEntity } from "@/common/entities/base.entity";
import { Role } from "@/modules/role/role.entity";

@Entity("sys_user")
export class User extends BaseEntity {
  @Column({ type: "varchar", length: 50, unique: true, comment: "用户名" })
  username: string;

  @Column({ type: "varchar", length: 255, comment: "密码(加密存储)" })
  password: string;

  @Column({ type: "varchar", length: 100, nullable: true, comment: "昵称" })
  nickname: string;

  @Column({ type: "varchar", length: 100, nullable: true, comment: "邮箱" })
  email: string;

  @Column({ type: "varchar", length: 20, nullable: true, comment: "手机号" })
  phone: string;

  @Column({ type: "varchar", length: 255, nullable: true, comment: "头像URL" })
  avatar: string;

  @Column({ type: "tinyint", default: 1, comment: "状态: 0-禁用, 1-启用" })
  status: number;

  @Column({
    type: "datetime",
    name: "last_login_time",
    nullable: true,
    comment: "最后登录时间",
  })
  lastLoginTime: Date;

  @ManyToMany(() => Role, (role) => role.users)
  @JoinTable({
    name: "sys_user_role",
    joinColumn: { name: "user_id", referencedColumnName: "id" },
    inverseJoinColumn: { name: "role_id", referencedColumnName: "id" },
  })
  roles: Role[];
}
