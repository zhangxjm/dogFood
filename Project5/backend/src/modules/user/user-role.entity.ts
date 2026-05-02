import {
  Entity,
  Column,
  ManyToOne,
  JoinColumn,
  PrimaryGeneratedColumn,
} from "typeorm";
import { User } from "@/modules/user/user.entity";
import { Role } from "@/modules/role/role.entity";

@Entity("sys_user_role")
export class UserRole {
  @PrimaryGeneratedColumn({ type: "bigint", comment: "主键ID" })
  id: number;

  @Column({ type: "bigint", name: "user_id", comment: "用户ID" })
  userId: number;

  @Column({ type: "bigint", name: "role_id", comment: "角色ID" })
  roleId: number;

  @ManyToOne(() => User, (user) => user.roles)
  @JoinColumn({ name: "user_id" })
  user: User;

  @ManyToOne(() => Role, (role) => role.users)
  @JoinColumn({ name: "role_id" })
  role: Role;
}
