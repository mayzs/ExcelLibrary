package com.example.myapplication.utils

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.provider.Settings
import android.text.TextUtils
import java.util.*


/**
 * @date: 2022-07-13 09:37
 * @author: mayz
 * @version: 1.0
 */
class AppUtils {
    companion object{
        @SuppressLint("StaticFieldLeak")
        private lateinit var context: Context
        @JvmStatic
        fun init(context: Context){
            this.context=context
        }

        @JvmStatic
        fun getContext(): Context {
            return context
        }
        @SuppressLint("HardwareIds")
        private fun getAndroidID(): String {
            val id = Settings.Secure.getString(
                getContext().contentResolver,
                Settings.Secure.ANDROID_ID
            )
            return if ("9774d56d682e549c" == id) "" else id ?: ""
        }
        /**
         * 通过读取设备的ROM版本号、厂商名、CPU型号和其他硬件信息来组合出一串15位的号码
         * 其中“Build.SERIAL”这个属性来保证ID的独一无二，当API < 9 无法读取时，使用AndroidId
         *
         * @return 伪唯一ID
         */
        @JvmStatic
        fun getDeviceID(): String {
            val mSzDevIDShort =
                "35" + Build.BOARD.length % 10
            + Build.BRAND.length % 10
            + Build.CPU_ABI.length % 10
            + Build.DEVICE.length % 10
            + Build.DISPLAY.length % 10
            + Build.HOST.length % 10
            + Build.ID.length % 10
            + Build.MANUFACTURER.length % 10
            + Build.MODEL.length % 10
            + Build.PRODUCT.length % 10
            + Build.TAGS.length % 10
            + Build.TYPE.length % 10
            + Build.USER.length % 10
            var serial: String
            try {
                serial = Build::class.java.getField("SERIAL").get(null)?.toString() ?: "serial"
            } catch (e: Exception) {
                //获取失败，使用AndroidId
                serial = getAndroidID()
                if (TextUtils.isEmpty(serial)) {
                    serial = "serial"
                }
            }
            return UUID(mSzDevIDShort.hashCode().toLong(), serial.hashCode().toLong()).toString()
        }
    }
}