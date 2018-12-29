package com.uhf.uhf.util;

import android.Manifest;
import android.app.Activity;
import android.os.Build;
import com.uhf.uhf.interfaces.CommenInterface;
import com.yanzhenjie.permission.Action;
import com.yanzhenjie.permission.AndPermission;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by cqian on 2018/5/28.
 */

public class GetPermissionUtil {
    public static final String[] PERMISSIONS = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE};

    /**
     * 动态申请权限
     *
     * @param activity
     * @param index           0.相机 1.存储
     * @param commenInterface
     */
    @Deprecated
    public static void getPermission(Activity activity, int index, final CommenInterface commenInterface) {
        final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && index >= 0 && index < PERMISSIONS.length) {
            // 检查该权限是否已经获取
            //动态申请权限
            AndPermission.with(activity)
                    .runtime()
                    .permission(PERMISSIONS[index])
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            commenInterface.onSuccess();
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            commenInterface.onFailed();
                        }
                    })
                    .start();

        } else {
            commenInterface.onSuccess();
        }
    }
    /**
     * 动态申请权限
     *
     * @param activity
     * @param indexArr           0.相机 1.存储
     * @param commenInterface
     */
    public static void getPermission(Activity activity, int[] indexArr, final CommenInterface commenInterface) {
        final WeakReference<Activity> mActivity = new WeakReference<Activity>(activity);
        // 版本判断。当手机系统大于 23 时，才有必要去判断权限是否获取
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && indexArr.length >= 0 && indexArr.length <= PERMISSIONS.length) {
            // 检查该权限是否已经获取
            //动态申请权限
            String[] permission=new String[indexArr.length];
            for (int i = 0; i < indexArr.length; i++) {
                if (indexArr[i]<PERMISSIONS.length){
                    permission[i]=PERMISSIONS[indexArr[i]];
                }
            }
            AndPermission.with(mActivity.get())
                    .runtime()
                    .permission(permission)
                    .onGranted(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            commenInterface.onSuccess();
                        }
                    })
                    .onDenied(new Action<List<String>>() {
                        @Override
                        public void onAction(List<String> data) {
                            commenInterface.onFailed();
                        }
                    })
                    .start();

        } else {
            commenInterface.onSuccess();
        }
    }

}
