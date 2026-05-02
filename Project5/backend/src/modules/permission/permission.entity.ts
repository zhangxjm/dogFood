import { Entity, Column, ManyToMany, JoinTable } from "typeorm";
import { BaseEntity } from "@/common/entities/base.entity";
import { Role } from "@/modules/role/role.entity";

@Entity("sys_permission")
export class Permission extends BaseEntity {
  @Column({ type: "varchar", length: 100, comment: "权限名称" })
  name: string;

  @Column({ type: "varchar", length: 100, unique: true, comment: "权限编码" })
  code: string;

  @Column({
    type: "tinyint",
    default: 1,
    comment: "权限类型: 1-菜单, 2-按钮, 3-接口",
  })
  type: number;

  @Column({ type: "bigint", name: "parent_id", default: 0, comment: "父级ID" })
  parentId: number;

  @Column({ type: "varchar", length: 255, nullable: true, comment: "路由路径" })
  path: string;

  @Column({ type: "varchar", length: 255, nullable: true, comment: "组件路径" })
  component: string;

  @Column({ type: "varchar", length: 100, nullable: true, comment: "菜单图标" })
  icon: string;

  @Column({ type: "int", default: 0, comment: "排序" })
  sort: number;

  @Column({ type: "tinyint", default: 1, comment: "状态: 0-禁用, 1-启用" })
  status: number;

  @ManyToMany(() => Role, (role) => role.permissions)
  @JoinTable({
    name: "sys_role_permission",
    joinColumn: { name: "permission_id", referencedColumnName: "id" },
    inverseJoinColumn: { name: "role_id", referencedColumnName: "id" },
  })
  roles: Role[];

  children: Permission[];
}
