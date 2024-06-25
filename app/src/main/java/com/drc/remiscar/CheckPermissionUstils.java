package com.drc.remiscar;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class CheckPermissionUstils {

    public static final int REQUEST_CALL_PERMISSION = 10100; //拨号请求码
    public static final int REQUEST_LOCATION_PERMISSION = 10101; //获取位置码
    public static final int REQUEST_WRITE_PERMISSION = 1; //读写请求码
    public static final int REQUEST_CAMERA_PERMISSION = 10103; //相机请求码
    public static final int REQUEST_SHARE_PERMISSION = 10104; //分享请求码


    /**
     * 判断是否有某项权限
     *
     * @param string_permission 权限
     * @param request_code      请求码
     * @return
     */
    public static boolean checkReadPermission(Activity context, String[] string_permission, int request_code) {
        boolean flag = true;
        for (String permission : string_permission) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    // 只要有一个权限没有被授予, 则直接返回 false
                    ActivityCompat.requestPermissions(context, string_permission, request_code);
                    return false;
                }
            }
        }
        return flag;
    }

}
