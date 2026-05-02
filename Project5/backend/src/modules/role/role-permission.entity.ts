import {
  Entity,
  Column,
  ManyToOne,
  JoinColumn,
  PrimaryGeneratedColumn,
} from "typeorm";
import { Role } from "@/modules/role/role.entity";
import { Permission } from "@/modules/permission/permission.entity";

@Entity("sys_role_permission")
export class RolePermission {
  @PrimaryGeneratedColumn({ type: "bigint", comment: "主键ID" })
  id: number;

  @Column({ type: "bigint", name: "role_id", comment: "角色ID" })
  roleId: number;

  @Column({ type: "bigint", name: "permission_id", comment: "权限ID" })
  permissionId: number;

  @ManyToOne(() => Role, (role) => role.permissions)
  @JoinColumn({ name: "role_id" })
  role: Role;

  @ManyToOne(() => Permission, (permission) => permission.roles)
  @JoinColumn({ name: "permission_id" })
  permission: Permission;
}
