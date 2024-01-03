package com.example.myapplication

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.io.Serializable

/**
 * @date: 2022-06-06 10:54
 * @author: mayz
 * @version: 1.0
 */
@Parcelize
data class Test(val name:String,val age:Int): Parcelable, Serializable

@Parcelize
data class ScreeningConditionOptionVo(
    /**
     * text	String	文本
     * value	String	值
     * selected	int	是否选中，0：否， 1：是
     */
    val text: String? = null,
    val value: String? = "",
    val selected: String? = null,
) : Parcelable, Serializable