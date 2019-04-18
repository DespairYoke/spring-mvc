package com.zwd.web.annotation;

import java.lang.annotation.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-18
 **/
@Target({ ElementType.METHOD }) // 在方法上的注解
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestMapping {
    String value() default "";
}