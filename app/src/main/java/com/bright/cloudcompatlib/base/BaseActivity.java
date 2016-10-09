package com.bright.cloudcompatlib.base;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.bright.cloudcompatlib.R;

/**
 * Created by tci_mi on 16/10/8.
 */

public abstract class BaseActivity extends AppCompatActivity {

    public boolean isLandscape = false;
    protected BaseActivity mBaseActivity = null;
    protected BaseApplication mApplication = null;
    protected boolean mIsCancelAction = false;
    protected boolean mIsLogoutAction = false;
    Toast mToast = null;
    private long waitTime = 2000;
    private long touchTime = 0;

    @Override
    protected void onCreate(@NonNull Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        mApplication = (BaseApplication) this.getApplication();
        mApplication.add(this);
        mBaseActivity = this;
        SupportDisplay.initLayoutSetParams(BaseActivity.this);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        SupportDisplay.initLayoutSetParams(BaseActivity.this);
        super.onConfigurationChanged(newConfig);
    }

    @Override
    protected void onResume() {
        isLandscape = this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
        SupportDisplay.initLayoutSetParams(BaseActivity.this);
        super.onResume();
        if (mApplication != null && !mApplication.isNetworkConnected()) {
            Toast.makeText(BaseActivity.this, R.string.common_mi_network_disabled, Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 子类Activity必须实现的方法，用于初始化控件，重新适配等操作
     */
    protected abstract void resetLayout();

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        resetLayout();
    }

    @Override
    public void setContentView(int layoutResID) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.setContentView(layoutResID);
        resetLayout();
    }

    public void baseStartActivity(Context packageContext, Class<?> cls) {
        Intent intent = new Intent(packageContext, cls);
        baseStartActivity(intent);
    }

    public void baseStartActivity(Intent intent) {
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP
                | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        this.startActivity(intent);

    }

    protected void baseAddFragment(int container, Fragment fragment) {
        FragmentManager fragmentManager = this.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager
                .beginTransaction();
        if (fragment != null) {
            if (fragment.isAdded()) {
                fragmentTransaction.show(fragment);
            } else {
                fragmentTransaction.replace(container, fragment);
            }
        }
        fragmentTransaction.commit();

    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            onKeyCodeBackListener();
            return false;
        }
        if (keyCode == KeyEvent.KEYCODE_SEARCH) {
            onSearchButtonListener();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 按下安卓系统返回键的监听
     *
     * @Title onKeyCodeBackListener
     */
    @SuppressLint("SimpleDateFormat")
    private void onKeyCodeBackListener() {
        // 子类如果不是直接finish（），想自己处理的话，首先赋值 mIsCancelAction=true
        if (mIsCancelAction) {
            // 子类Activity覆盖此方法可以拦截返回键监听，在自己的页面中处理操作
            onCancelButtonListener();

        } else if (mIsLogoutAction) {// 这个值在具体的主页面设定为true，可以从这点击两次退出，其他页面自行处理

            long currentTime = System.currentTimeMillis();
            if ((currentTime - touchTime) >= waitTime) {
                Toast.makeText(this, "请再按一次退出程序!!", Toast.LENGTH_SHORT).show();
                touchTime = currentTime;
            } else {
                // 默认提示日期 为当天日期
//                SimpleDateFormat format = new SimpleDateFormat(
//                        "yyyy/MM/dd HH:mm:ss");
//                String loginTime = format.format(new Date());
//                mLoginDataManager.setLastLoginTime(loginTime);
                mApplication.finishAll();
            }

        } else {// 其他情况，默认关闭页面
            finish();
        }

    }

    /**
     * android系统返回键按下后，各页面独自处理方法
     */
    protected void onCancelButtonListener() {
    }

    protected void onSearchButtonListener() {
    }

    protected void shakeLayout(View view) {
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.common_mi_shake_layout);
        if (view != null && view.getVisibility() == View.VISIBLE) {
            view.startAnimation(shake);
        }

    }

    public String getStringExtra(String key) {
        String result = "";
        if (getIntent().getStringExtra(key) != null) {
            result = getIntent().getStringExtra(key);
        }
        return result;
    }

    public boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) this
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo ni = cm.getActiveNetworkInfo();
        return ni != null && ni.isConnectedOrConnecting();
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public void backgroundAlpha(float bgAlpha) {
        WindowManager.LayoutParams lp = getWindow().getAttributes();
        lp.alpha = bgAlpha;
        getWindow().setAttributes(lp);
    }

    /**
     * 拨打电话号码
     *
     * @param telNo 电话号码
     */
    public void callTelPhone(String telNo) {
        if (telNo != null && !telNo.equals("")) {
            Intent intent = new Intent("android.intent.action.CALL",
                    Uri.parse("tel:" + telNo));
            startActivity(intent);
        }
    }

    /**
     * 提示拨打电话对话框
     */
    public android.support.v7.app.AlertDialog telDialog(String message, final String telNo) {

        return showDialog(mBaseActivity, null, message, null, "确定", "取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                callTelPhone(telNo);
            }
        }, null, true);

    }

    /**
     * 公用提示框
     */
    public void showToast(final String info) {

        runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (mToast != null && !isFinishing()) {
                    mToast.setText(info);
                    mToast.show();
                    return;
                }
                mToast = Toast.makeText(getApplicationContext(), info,
                        Toast.LENGTH_SHORT);
                mToast.show();
            }
        });
    }

    // ************　共通组件相关　*************** //
    private android.support.v7.app.AlertDialog showDialog(Context context, String title, String message, View contentView,
                                                          String positiveBtnText, String negativeBtnText,
                                                          DialogInterface.OnClickListener positiveCallback,
                                                          DialogInterface.OnClickListener negativeCallback,
                                                          boolean cancelable) {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(context, R.style.AppCompatAlertDialogStyle);
        builder.setTitle(title == null ? "提示" : title);
        if (message != null) {
            builder.setMessage(message);
        }

        if (contentView != null) {
            builder.setView(contentView);
        }

        if (positiveBtnText != null) {
            builder.setPositiveButton(positiveBtnText, positiveCallback);
        }

        if (negativeBtnText != null) {
            builder.setNegativeButton(negativeBtnText, negativeCallback);
        }

        builder.setCancelable(cancelable);
        return builder.show();
    }

    //普通对话框
    public android.support.v7.app.AlertDialog showSimpleDialog(Context context, String title, String message,
                                                               String positiveBtnText, String negativeBtnText,
                                                               DialogInterface.OnClickListener positiveCallback,
                                                               DialogInterface.OnClickListener negativeCallback,
                                                               boolean cancelable) {

        return showDialog(context, title, message, null, positiveBtnText, negativeBtnText, positiveCallback, negativeCallback, cancelable);
    }

    public interface PressOKButtonListener {
        void onOKButtonPressed();
    }


    public interface PressCancelButtonListener {
        void onCancelButtonPressed();
    }

    /**
     * 添加弹出的popWin关闭的事件，主要是为了将背景透明度改回来
     */
    class popwindowOnDismissListener implements
            android.widget.PopupWindow.OnDismissListener {
        @Override
        public void onDismiss() {
            backgroundAlpha(1f);
        }
    }

}
