package com.drc.remiscar

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication

open class BaseApplication : MultiDexApplication() {

    override fun onCreate() {
        super.onCreate()
        context = this
    }

    companion object {
        private var context: BaseApplication? = null
        @JvmStatic
        fun getContext(): Context {
            return context!!
        }
    }
}