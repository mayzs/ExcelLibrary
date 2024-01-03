package com.mayz.excellibrary.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * content in excel
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.FIELD})
public @interface ExcelContent {

    /**
     * The name link to title in excel
     */
    String titleName();

    /**
     * titleIndex in excel
     */
    int index() default 0;
}