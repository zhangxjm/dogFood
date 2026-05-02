import {
  Controller,
  Get,
  Post,
  Put,
  Delete,
  Body,
  Param,
  Query,
} from "@nestjs/common";
import { RoleService } from "./role.service";
import { CreateRoleDto, UpdateRoleDto, QueryRoleDto } from "./dto/role.dto";
import { Permissions } from "@/common/decorators/permissions.decorator";

@Controller("roles")
export class RoleController {
  constructor(private readonly roleService: RoleService) {}

  @Get()
  @Permissions("system:role:query")
  async findAll(@Query() query: QueryRoleDto) {
    return this.roleService.findAll(query);
  }

  @Get("all")
  @Permissions("system:role:query")
  async findAllSimple() {
    return this.roleService.findAllSimple();
  }

  @Get(":id")
  @Permissions("system:role:query")
  async findOne(@Param("id") id: string) {
    return this.roleService.findOne(+id);
  }

  @Post()
  @Permissions("system:role:add")
  async create(@Body() createRoleDto: CreateRoleDto) {
    return this.roleService.create(createRoleDto);
  }

  @Put(":id")
  @Permissions("system:role:edit")
  async update(@Param("id") id: string, @Body() updateRoleDto: UpdateRoleDto) {
    return this.roleService.update(+id, updateRoleDto);
  }

  @Delete(":id")
  @Permissions("system:role:delete")
  async remove(@Param("id") id: string) {
    return this.roleService.remove(+id);
  }
}
