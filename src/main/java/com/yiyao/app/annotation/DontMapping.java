package com.yiyao.app.annotation;

import java.lang.annotation.*;

/**
 * Created by @wb-sk288659
 *
 * @date: @2018/4/3.
 * @mail:
 * @description:
 */
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DontMapping {
}
