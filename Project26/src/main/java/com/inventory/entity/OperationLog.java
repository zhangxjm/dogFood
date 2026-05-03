package com.inventory.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_operation_log")
public class OperationLog extends BaseEntity {

    private Long userId;

    private String username;

    private String operation;

    private String method;

    private String params;

    private String result;

    private String ip;

    private Integer status;

    private String errorMsg;

    private LocalDateTime operationTime;
}
