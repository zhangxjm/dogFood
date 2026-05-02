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
import { PermissionService } from "./permission.service";
import {
  CreatePermissionDto,
  UpdatePermissionDto,
  QueryPermissionDto,
} from "./dto/permission.dto";
import { Permissions } from "@/common/decorators/permissions.decorator";

@Controller("permissions")
export class PermissionController {
  constructor(private readonly permissionService: PermissionService) {}

  @Get()
  @Permissions("system:permission:query")
  async findAll(@Query() query: QueryPermissionDto) {
    return this.permissionService.findAll(query);
  }

  @Get("list")
  @Permissions("system:permission:query")
  async findAllList() {
    return this.permissionService.findAllList();
  }

  @Get(":id")
  @Permissions("system:permission:query")
  async findOne(@Param("id") id: string) {
    return this.permissionService.findOne(+id);
  }

  @Post()
  @Permissions("system:permission:add")
  async create(@Body() createPermissionDto: CreatePermissionDto) {
    return this.permissionService.create(createPermissionDto);
  }

  @Put(":id")
  @Permissions("system:permission:edit")
  async update(
    @Param("id") id: string,
    @Body() updatePermissionDto: UpdatePermissionDto,
  ) {
    return this.permissionService.update(+id, updatePermissionDto);
  }

  @Delete(":id")
  @Permissions("system:permission:delete")
  async remove(@Param("id") id: string) {
    return this.permissionService.remove(+id);
  }
}
