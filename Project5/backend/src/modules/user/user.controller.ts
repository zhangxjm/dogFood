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
import { UserService } from "./user.service";
import { CreateUserDto, UpdateUserDto, QueryUserDto } from "./dto/user.dto";
import { Permissions } from "@/common/decorators/permissions.decorator";

@Controller("users")
export class UserController {
  constructor(private readonly userService: UserService) {}

  @Get()
  @Permissions("system:user:query")
  async findAll(@Query() query: QueryUserDto) {
    return this.userService.findAll(query);
  }

  @Get(":id")
  @Permissions("system:user:query")
  async findOne(@Param("id") id: string) {
    return this.userService.findOne(+id);
  }

  @Post()
  @Permissions("system:user:add")
  async create(@Body() createUserDto: CreateUserDto) {
    return this.userService.create(createUserDto);
  }

  @Put(":id")
  @Permissions("system:user:edit")
  async update(@Param("id") id: string, @Body() updateUserDto: UpdateUserDto) {
    return this.userService.update(+id, updateUserDto);
  }

  @Delete(":id")
  @Permissions("system:user:delete")
  async remove(@Param("id") id: string) {
    return this.userService.remove(+id);
  }

  @Put(":id/status")
  @Permissions("system:user:edit")
  async toggleStatus(@Param("id") id: string) {
    return this.userService.toggleStatus(+id);
  }
}
