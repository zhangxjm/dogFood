package com.seckill.common.annotation;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PreventDuplicateSubmit {

    int interval() default 5;

    String message() default "请勿重复提交";
}
