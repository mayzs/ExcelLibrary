/*
 * Created by Engine100 on 2016-11-30 11:10:00.
 *
 *      https://github.com/engine100
 *
 */
package com.mayz.excellibrary.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * format the content.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({java.lang.annotation.ElementType.METHOD})
public @interface ExcelContentCellFormat {
    String titleName();
}