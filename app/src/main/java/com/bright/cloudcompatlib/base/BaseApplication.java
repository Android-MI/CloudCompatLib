package com.bright.cloudcompatlib.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * 全局应用程序类
 *
 * @author CollCloud_小米
 * @ClassName AppApplacation
 */
public class BaseApplication extends Application {

    private static BaseApplication mInstance = null;
    private List<BaseActivity> mActivityList = null;
    private List<Activity> mDefinedActivityList = null;

    public static BaseApplication getInstance() {
        return mInstance;
    }

    /**
     * 判断当前版本是否兼容目标版本的方法
     */
    public static boolean isMethodsCompat(int VersionCode) {
        int currentVersion = android.os.Build.VERSION.SDK_INT;
        return currentVersion >= VersionCode;
    }

    public void add(BaseActivity baseActivity) {
        if (mActivityList == null) {
            mActivityList = new ArrayList<BaseActivity>();
        }
        mActivityList.add(baseActivity);
    }

    public void remove(BaseActivity activity) {
        if (mActivityList == null) {
            return;
        }
        if (mActivityList.contains(activity)) {
            mActivityList.remove(activity);
        }
    }

    public void finishAll() {
        for (int i = 0; i < mActivityList.size(); i++) {
            mActivityList.get(i).finish();
        }
        mActivityList.clear();
    }

    public void addDefinedActivity(Activity activity) {
        if (mDefinedActivityList == null) {
            mDefinedActivityList = new ArrayList<Activity>();
        }
        mDefinedActivityList.add(activity);
    }

    public synchronized void unRegisterActivity(Activity activity) {

        if (mDefinedActivityList.size() != 0) {
            mDefinedActivityList.remove(activity);
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    public void finishDefinedActivity() {
        for (int i = 0; i < mDefinedActivityList.size(); i++) {
            mDefinedActivityList.get(i).finish();
        }
        mDefinedActivityList.clear();
    }

    public List<BaseActivity> getmActivityList() {
        return mActivityList;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
    }

    /**
     * 检测当前系统声音是否为正常模式
     */
    public boolean isAudioNormal() {
        AudioManager mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        return mAudioManager.getRingerMode() == AudioManager.RINGER_MODE_NORMAL;
    }

    /**
     * 检测网络是否可用
     *
     * @return
     */
    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 获取App安装包信息
     *
     * @return
     */
    public PackageInfo getPackageInfo() {
        PackageInfo info = null;
        try {
            info = getPackageManager().getPackageInfo(getPackageName(), 0);
        } catch (NameNotFoundException e) {
            e.printStackTrace(System.err);
        }
        if (info == null)
            info = new PackageInfo();
        return info;
    }

}
