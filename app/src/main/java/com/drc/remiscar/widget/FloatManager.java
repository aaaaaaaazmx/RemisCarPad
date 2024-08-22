package com.drc.remiscar.widget;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.widget.FrameLayout;

import androidx.activity.ComponentActivity;
import androidx.core.view.ViewCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;

/**
 * GitHub : https://github.com/yechaoa
 * CSDN : http://blog.csdn.net/yechaoa
 *
 * Created by yechao on 2022/7/30.
 * Describe :
 */
@SuppressLint("StaticFieldLeak")
public class FloatManager {

    private static FrameLayout mContentView;
    private static Activity mActivity;
    private static BaseFloatView mFloatView;
    private static boolean mIsShowing = false;

    public static FloatManager with(Activity activity) {
        mContentView = (FrameLayout) activity.getWindow().getDecorView().findViewById(android.R.id.content);
        mActivity = activity;
        addLifecycle(mActivity);
        return new FloatManager();
    }

    public FloatManager add(BaseFloatView floatView) {
        if (mIsShowing) return this;
        mFloatView = floatView;
        return this;
    }

    public FloatManager setClick(BaseFloatView.OnFloatClickListener listener) {
        if (mFloatView != null) {
            mFloatView.setOnFloatClickListener(listener);
        }
        return this;
    }

    public void show() {
        checkParams();
        if (!mIsShowing) {
            mContentView.removeView(mFloatView);
            mContentView.addView(mFloatView);
            if (mFloatView != null) {
                mFloatView.bringToFront();
            }
            mIsShowing = true;
        }
    }

    private void checkParams() {
        if (mActivity == null) {
            throw new NullPointerException("You must set the 'Activity' params before the show()");
        }
        if (mFloatView == null) {
            throw new NullPointerException("You must set the 'FloatView' params before the show()");
        }
    }

    private static void addLifecycle(Activity activity) {
        if (activity instanceof ComponentActivity) {
            ((ComponentActivity) activity).getLifecycle().addObserver(mLifecycleEventObserver);
        }
    }

    private static LifecycleEventObserver mLifecycleEventObserver = new LifecycleEventObserver() {
        @Override
        public void onStateChanged(LifecycleOwner source, Lifecycle.Event event) {
            // 自动回收
            if (event == Lifecycle.Event.ON_DESTROY) {
                hide();
            }
        }
    };

    public static void hide() {
        mIsShowing = false;
        if (mContentView != null && mFloatView != null && ViewCompat.isAttachedToWindow(mFloatView)) {
            mContentView.removeView(mFloatView);
        }
        if (mFloatView != null) {
            mFloatView.release();
        }
        mFloatView = null;
        if (mActivity instanceof ComponentActivity) {
            ((ComponentActivity) mActivity).getLifecycle().removeObserver(mLifecycleEventObserver);
        }
        mActivity = null;
    }
}

