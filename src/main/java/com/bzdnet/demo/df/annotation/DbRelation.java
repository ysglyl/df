package com.bzdnet.demo.df.annotation;

import java.lang.annotation.*;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface DbRelation {

    Type type() default Type.One2One;
    String secondaryTable() default "";
    String refTable() default "";
    String primary() default "";
    String refPrimary() default "";
    String refSecondary() default "";
    String secondary() default "";

    enum Type {
        One2One,
        One2Many,
        Many2Many
    }

}
