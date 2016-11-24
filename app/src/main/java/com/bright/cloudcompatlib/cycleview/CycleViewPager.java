package com.bright.cloudcompatlib.cycleview;

import android.app.Fragment;
import android.os.Bundle;
import android.os.Message;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by tci_mi on 16/11/24 下午5:46.
 *
 * @Description
 */
public class CycleViewPager extends Fragment implements ViewPager.OnPageChangeListener {

    private List<ImageView> imageViews = new ArrayList();
    private ImageView[] indicators;
    private FrameLayout viewPagerFragmentLayout;
    private LinearLayout indicatorLayout;
    private BaseViewPager viewPager;
    private BaseViewPager parentViewPager;
    private CycleViewPager.ViewPagerAdapter adapter;
    private CycleViewPageHandler.UnleakHandler handler;
    private int time = 2500;
    private int currentPosition = 0;
    private boolean isScrolling = false;
    private boolean isCycle = true;
    private boolean isWheel = true;
    private long releaseTime = 0L;
    private int WHEEL = 100;
    private int WHEEL_WAIT = 101;
    final Runnable runnable = new Runnable() {
        public void run() {
            if (CycleViewPager.this.getActivity() != null && !CycleViewPager.this.getActivity().isFinishing() && CycleViewPager.this.isWheel) {
                long now = System.currentTimeMillis();
                if (now - CycleViewPager.this.releaseTime > (long) (CycleViewPager.this.time - 500)) {
                    CycleViewPager.this.handler.sendEmptyMessage(CycleViewPager.this.WHEEL);
                } else {
                    CycleViewPager.this.handler.sendEmptyMessage(CycleViewPager.this.WHEEL_WAIT);
                }
            }

        }
    };
    private CycleViewPager.ImageCycleViewListener mImageCycleViewListener;
    private List<Object> infos;

    public CycleViewPager() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = LayoutInflater.from(this.getActivity()).inflate(this.getViewId("view_cycle_viewpager_contet", "layout"), null);
        this.viewPager = (BaseViewPager) view.findViewById(this.getViewId("viewPager", "id"));
        this.indicatorLayout = (LinearLayout) view.findViewById(this.getViewId("layout_viewpager_indicator", "id"));
        this.viewPagerFragmentLayout = (FrameLayout) view.findViewById(this.getViewId("layout_viewager_content", "id"));

        handler = new CycleViewPageHandler.UnleakHandler(getActivity()) {
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == CycleViewPager.this.WHEEL && CycleViewPager.this.imageViews.size() != 0) {
                    if (!CycleViewPager.this.isScrolling) {
                        int max = CycleViewPager.this.imageViews.size() + 1;
                        int position = (CycleViewPager.this.currentPosition + 1) % CycleViewPager.this.imageViews.size();
                        CycleViewPager.this.viewPager.setCurrentItem(position, true);
                        if (position == max) {
                            CycleViewPager.this.viewPager.setCurrentItem(1, false);
                        }
                    }

                    CycleViewPager.this.releaseTime = System.currentTimeMillis();
                    CycleViewPager.this.handler.removeCallbacks(CycleViewPager.this.runnable);
                    CycleViewPager.this.handler.postDelayed(CycleViewPager.this.runnable, (long) CycleViewPager.this.time);
                } else {
                    if (msg.what == CycleViewPager.this.WHEEL_WAIT && CycleViewPager.this.imageViews.size() != 0) {
                        CycleViewPager.this.handler.removeCallbacks(CycleViewPager.this.runnable);
                        CycleViewPager.this.handler.postDelayed(CycleViewPager.this.runnable, (long) CycleViewPager.this.time);
                    }

                }
            }
        };

        return view;
    }

    public void setData(List<ImageView> views, List<Object> list, CycleViewPager.ImageCycleViewListener listener) {
        this.setData(views, list, listener, 0);
    }

    public void setData(List<ImageView> views, List<Object> list, CycleViewPager.ImageCycleViewListener listener, int showPosition) {
        this.mImageCycleViewListener = listener;
        this.infos = list;
        this.imageViews.clear();
        if (views.size() == 0) {
            this.viewPagerFragmentLayout.setVisibility(View.GONE);
        } else {
            Iterator i = views.iterator();

            while (i.hasNext()) {
                ImageView ivSize = (ImageView) i.next();
                this.imageViews.add(ivSize);
            }

            int var8 = views.size();
            this.indicators = new ImageView[var8];
            if (this.isCycle) {
                this.indicators = new ImageView[var8 - 2];
            }

            this.indicatorLayout.removeAllViews();

            for (int var9 = 0; var9 < this.indicators.length; ++var9) {
                View view = LayoutInflater.from(this.getActivity()).inflate(this.getViewId("view_cycle_viewpager_indicator", "layout"), null);
                this.indicators[var9] = (ImageView) view.findViewById(this.getViewId("image_indicator", "id"));
                this.indicatorLayout.addView(view);
            }

            this.adapter = new CycleViewPager.ViewPagerAdapter();
            this.setIndicator(0);
            this.viewPager.setOffscreenPageLimit(3);
            this.viewPager.addOnPageChangeListener(this);
            this.viewPager.setAdapter(this.adapter);
            if (showPosition < 0 || showPosition >= views.size()) {
                showPosition = 0;
            }

            if (this.isCycle) {
                ++showPosition;
            }

            this.viewPager.setCurrentItem(showPosition);
        }
    }

    public void setIndicatorCenter() {
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(-2, -2);
        params.addRule(12);
        params.addRule(14);
        this.indicatorLayout.setLayoutParams(params);
    }

    public boolean isCycle() {
        return this.isCycle;
    }

    public void setCycle(boolean isCycle) {
        this.isCycle = isCycle;
    }

    public boolean isWheel() {
        return this.isWheel;
    }

    public void setWheel(boolean isWheel) {
        this.isWheel = isWheel;
        this.isCycle = true;
        if (isWheel) {
            this.handler.postDelayed(this.runnable, (long) this.time);
        }

    }

    public void releaseHeight() {
        this.getView().getLayoutParams().height = -1;
        this.refreshData();
    }

    public void setTime(int time) {
        this.time = time;
    }

    public void refreshData() {
        if (this.adapter != null) {
            this.adapter.notifyDataSetChanged();
        }

    }

    public void hide() {
        this.viewPagerFragmentLayout.setVisibility(View.GONE);
    }

    public BaseViewPager getViewPager() {
        return this.viewPager;
    }

    public void onPageScrollStateChanged(int arg0) {
        if (arg0 == 1) {
            this.isScrolling = true;
        } else {
            if (arg0 == 0) {
                if (this.parentViewPager != null) {
                    this.parentViewPager.setScrollable(true);
                }

                this.releaseTime = System.currentTimeMillis();
                this.viewPager.setCurrentItem(this.currentPosition, false);
            }

            this.isScrolling = false;
        }
    }

    public void onPageScrolled(int arg0, float arg1, int arg2) {
    }

    public void onPageSelected(int arg0) {
        int max = this.imageViews.size() - 1;
        int position = arg0;
        this.currentPosition = arg0;
        if (this.isCycle) {
            if (arg0 == 0) {
                this.currentPosition = max - 1;
            } else if (arg0 == max) {
                this.currentPosition = 1;
            }

            position = this.currentPosition - 1;
        }

        this.setIndicator(position);
    }

    public void setScrollable(boolean enable) {
        this.viewPager.setScrollable(enable);
    }

    public int getCurrentPostion() {
        return this.currentPosition;
    }

    private void setIndicator(int selectedPosition) {
        for (int i = 0; i < this.indicators.length; ++i) {
            this.indicators[i].setBackgroundResource(this.getViewId("icon_point", "drawable"));
        }

        if (this.indicators.length > selectedPosition) {
            this.indicators[selectedPosition].setBackgroundResource(this.getViewId("icon_point_pre", "drawable"));
        }

    }

    public void disableParentViewPagerTouchEvent(BaseViewPager parentViewPager) {
        if (parentViewPager != null) {
            parentViewPager.setScrollable(false);
        }

    }

    private int getViewId(String resCode, String type) {
        int resId = this.getResources().getIdentifier(resCode, type, this.getActivity().getApplication().getPackageName());
        return resId;
    }

    public interface ImageCycleViewListener {
        void onImageClick(Object var1, int var2, View var3);
    }

    private class ViewPagerAdapter extends PagerAdapter {
        private ViewPagerAdapter() {
        }

        public int getCount() {
            return CycleViewPager.this.imageViews.size();
        }

        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        public View instantiateItem(ViewGroup container, int position) {
            ImageView v = CycleViewPager.this.imageViews.get(position);
            if (CycleViewPager.this.mImageCycleViewListener != null) {
                v.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        CycleViewPager.this.mImageCycleViewListener.onImageClick(CycleViewPager.this.infos.get(CycleViewPager.this.currentPosition - 1), CycleViewPager.this.currentPosition, v);
                    }
                });
            }

            container.addView(v);
            return v;
        }

        public int getItemPosition(Object object) {
            return -2;
        }
    }
}
