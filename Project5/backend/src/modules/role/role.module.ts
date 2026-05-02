import { Module } from "@nestjs/common";
import { TypeOrmModule } from "@nestjs/typeorm";
import { RoleController } from "./role.controller";
import { RoleService } from "./role.service";
import { Role } from "./role.entity";
import { RolePermission } from "./role-permission.entity";
import { Permission } from "@/modules/permission/permission.entity";

@Module({
  imports: [TypeOrmModule.forFeature([Role, RolePermission, Permission])],
  controllers: [RoleController],
  providers: [RoleService],
  exports: [RoleService],
})
export class RoleModule {}
