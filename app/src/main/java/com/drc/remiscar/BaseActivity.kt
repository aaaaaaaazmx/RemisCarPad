package com.drc.remiscar

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import androidx.annotation.RequiresApi

open class BaseActivity: Activity() {
    // 用于保存当前 AlertDialog 的引用
    private var currentDialog: AlertDialog? = null

    // 显示弹窗并可选立刻关闭
    open fun alert(msg: String?, activity: BaseActivity) {
        val builder = AlertDialog.Builder(activity)
        builder.setMessage(msg)
        builder.setTitle("信息")
        builder.setPositiveButton("确定") { dialog, which -> }

        // 创建并保存 AlertDialog
        currentDialog = builder.create()

        // 如果显示，那么就先隐藏
        if (currentDialog?.isShowing == true) {
            currentDialog?.dismiss()
        }

        // 显示弹窗
        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                !activity.isFinishing && !activity.isDestroyed
            } else {
                false
            }
        ) {
            currentDialog?.show()
        }
    }

    // 外部调用此方法来判断弹窗是否显示
    fun isDialogShowing(): Boolean {
        return currentDialog?.isShowing == true
    }

    // 外部调用此方法来关闭弹窗
    fun dismissDialog() {
        currentDialog?.dismiss()
    }
}