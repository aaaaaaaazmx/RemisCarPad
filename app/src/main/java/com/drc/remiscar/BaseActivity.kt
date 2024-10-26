package com.drc.remiscar

import android.app.Activity
import android.app.AlertDialog
import android.os.Build
import androidx.annotation.RequiresApi

open class BaseActivity : Activity() {
    // 用于保存当前 AlertDialog 的引用
    private var currentDialog: AlertDialog? = null

    // 显示弹窗
    open fun alert(msg: String?) {
        val builder = AlertDialog.Builder(this)
        builder.setMessage(msg)
        builder.setTitle("信息")
        builder.setPositiveButton("确定") { dialog, which -> }

        // 如果当前没有 Dialog，则创建新的 Dialog
        if (currentDialog == null) {
            currentDialog = builder.create()
        }

        // 如果 Dialog 正在显示，先将其关闭
        if (currentDialog?.isShowing == true) {
            currentDialog?.dismiss()
        }

        // 检查 Activity 是否有效，并显示 Dialog
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (!isFinishing && !isDestroyed) {
                currentDialog?.show()
            }
        } else {
            if (!isFinishing) {
                currentDialog?.show()
            }
        }
    }

    // 在 Activity 暂停时关闭弹窗
    override fun onPause() {
        super.onPause()
        currentDialog?.dismiss()
    }

    // 在 Activity 销毁时关闭弹窗并释放引用
    override fun onDestroy() {
        super.onDestroy()
        currentDialog?.dismiss()
        currentDialog = null
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
