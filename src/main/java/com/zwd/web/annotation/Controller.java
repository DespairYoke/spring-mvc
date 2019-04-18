package com.zwd.web.annotation;

import java.lang.annotation.*;

/**
 * TODO...
 *
 * @author zwd
 * @since 2019-04-18
 **/
@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Controller {
    String value() default "";
}
