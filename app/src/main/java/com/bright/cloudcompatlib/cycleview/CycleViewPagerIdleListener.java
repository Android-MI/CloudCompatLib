package com.bright.cloudcompatlib.cycleview;

import android.view.View;

/**
 * viewpager处于空闲状态监听器
 */
public interface CycleViewPagerIdleListener {
    void onPagerSelected(View view, int position);
}
