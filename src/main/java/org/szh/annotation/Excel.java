package org.szh.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface Excel {

    // 列名称
    String name() default "";

    // 忽略该字段默认没有忽略
    boolean skip() default false;

    // 序列化格式
    String format() default "";

    //字段值显示顺序(从左到右)
    int sort() default 20;

    //是否选择下拉
    boolean select() default false;
    
    //格式化价格
    boolean formatPrice() default false;
}
