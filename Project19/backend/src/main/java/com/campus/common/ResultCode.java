package com.campus.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),
    FAILED(500, "操作失败"),
    VALIDATE_FAILED(400, "参数检验失败"),
    UNAUTHORIZED(401, "暂未登录或token已经过期"),
    FORBIDDEN(403, "没有相关权限"),
    NOT_FOUND(404, "资源不存在"),
    USERNAME_EXIST(1001, "用户名已存在"),
    USERNAME_NOT_EXIST(1002, "用户名不存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    USER_DISABLED(1004, "用户已被禁用"),
    CLUB_NAME_EXIST(2001, "社团名称已存在"),
    ALREADY_APPLIED(2002, "已申请加入该社团"),
    ALREADY_MEMBER(2003, "已是该社团成员"),
    CLUB_FULL(2004, "社团人数已满"),
    ACTIVITY_FULL(3001, "活动报名人数已满"),
    ALREADY_REGISTERED(3002, "已报名该活动"),
    PERMISSION_DENIED(9999, "权限不足");

    private final int code;
    private final String message;
}
