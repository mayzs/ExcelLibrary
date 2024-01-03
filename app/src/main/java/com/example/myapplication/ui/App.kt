package com.example.myapplication.ui

import android.app.Application
import com.example.myapplication.utils.AppUtils

/**
 * @date: 2022-06-22 16:38
 * @author: mayz
 * @version: 1.0
 */
class App :Application(){
    override fun onCreate() {
        super.onCreate()
        AppUtils.init(this)
    }
}