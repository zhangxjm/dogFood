import {
  PrimaryGeneratedColumn,
  CreateDateColumn,
  UpdateDateColumn,
} from "typeorm";

export abstract class BaseEntity {
  @PrimaryGeneratedColumn({ type: "bigint", comment: "主键ID" })
  id: number;

  @CreateDateColumn({
    type: "datetime",
    name: "create_time",
    comment: "创建时间",
  })
  createTime: Date;

  @UpdateDateColumn({
    type: "datetime",
    name: "update_time",
    comment: "更新时间",
  })
  updateTime: Date;
}
