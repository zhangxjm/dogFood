import { Entity, Column, ManyToMany } from "typeorm";
import { BaseEntity } from "@/common/entities/base.entity";
import { User } from "@/modules/user/user.entity";
import { Permission } from "@/modules/permission/permission.entity";

@Entity("sys_role")
export class Role extends BaseEntity {
  @Column({ type: "varchar", length: 100, comment: "角色名称" })
  name: string;

  @Column({ type: "varchar", length: 100, unique: true, comment: "角色编码" })
  code: string;

  @Column({ type: "varchar", length: 255, nullable: true, comment: "角色描述" })
  description: string;

  @Column({ type: "tinyint", default: 1, comment: "状态: 0-禁用, 1-启用" })
  status: number;

  @Column({ type: "int", default: 0, comment: "排序" })
  sort: number;

  @ManyToMany(() => User, (user) => user.roles)
  users: User[];

  @ManyToMany(() => Permission, (permission) => permission.roles)
  permissions: Permission[];
}
