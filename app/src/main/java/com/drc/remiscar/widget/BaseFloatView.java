package com.drc.remiscar.widget;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;


public abstract class BaseFloatView extends FrameLayout implements View.OnTouchListener {

    private int mViewWidth = 0;
    private int mViewHeight = 0;
    private int mToolBarHeight = dp2px(56F); // toolbar默认高度
    private double mDragDistance = 0.5; // 默认吸边需要的拖拽距离为屏幕的一半

    public static final int ADSORB_VERTICAL = 1001;
    public static final int ADSORB_HORIZONTAL = 1002;

    private boolean mIsInside = false;
    private Handler mHandler = new Handler(Looper.getMainLooper());

    private float mDownX = 0F;
    private float mDownY = 0F;
    private int mFirstY;
    private int mFirstX;
    private boolean isMove = false;

    public BaseFloatView(Context context) {
        this(context, null);
    }

    public BaseFloatView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public BaseFloatView(Context context, AttributeSet attributeSet, int defStyle) {
        super(context, attributeSet, defStyle);
        initView();
    }

    private void initView() {
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.topMargin = mToolBarHeight;
        setLayoutParams(lp);

        View childView = getChildView();
        addView(childView);
        setOnTouchListener(this);

        post(() -> {
            // 获取一下view宽高，方便后面计算，省的bottom-top麻烦
            mViewWidth = this.getWidth();
            mViewHeight = this.getHeight();
        });
    }

    /**
     * 获取子view
     */
    protected abstract View getChildView();

    /**
     * 是否可以拖拽
     */
    protected abstract boolean getIsCanDrag();

    /**
     * 吸边的方式
     */
    protected abstract int getAdsorbType();

    /**
     * 多久自动缩一半
     * 默认：3000，单位：毫秒，小于等于0则不自动缩
     */
    public long getAdsorbTime() {
        return 3000;
    }

    private final Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            if (getX() > getScreenWidth() / 2) {
                // 右边
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).alpha(0.5f).x((getScreenWidth() - mViewWidth / 2)).start();
            } else {
                // 左边
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).alpha(0.5f).x((-getWidth() / 2)).start();
            }
            mIsInside = true;
        }
    };

    private static final int CLICK_THRESHOLD = 5; // 定义点击的阈值，单位是像素

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mFirstY = Math.round(event.getRawY());
                mFirstX = Math.round(event.getRawX());

                mHandler.removeCallbacksAndMessages(mRunnable);

                resetStatus();
                isMove = false; // 重置移动标志
                break;

            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(x - mDownX);
                float deltaY = Math.abs(y - mDownY);
                if (deltaX > CLICK_THRESHOLD || deltaY > CLICK_THRESHOLD) {
                    isMove = true;
                    offsetTopAndBottom((int) (y - mDownY));
                    offsetLeftAndRight((int) (x - mDownX));
                }
                break;

            case MotionEvent.ACTION_UP:
                if (!isMove) {
                    // 如果没有移动，则认为是点击
                    if (mOnFloatClickListener != null) {
                        mOnFloatClickListener.onClick(v);
                    }
                } else {
                    // 如果移动了，则执行吸边逻辑
                    if (getAdsorbType() == ADSORB_VERTICAL) {
                        adsorbTopAndBottom(event);
                    } else if (getAdsorbType() == ADSORB_HORIZONTAL) {
                        adsorbLeftAndRight(event);
                    }
                }

                if (getAdsorbTime() > 0) {
                    mHandler.postDelayed(mRunnable, getAdsorbTime());
                }
                break;
        }
        return getIsCanDrag();
    }

    private void resetStatus() {
        if (mIsInside) {
            if (getX() > getScreenWidth() / 2) {
                // 右边
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).alpha(1f).x((getScreenWidth() - mViewWidth)).start();
            } else {
                // 左边
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).alpha(1f).x(0F).start();
            }
            mIsInside = false;
        }
    }

    /**
     * 上下吸边
     */
    private void adsorbTopAndBottom(MotionEvent event) {
        if (isOriginalFromTop()) {
            // 上半屏
            double centerY = mViewHeight / 2 + Math.abs(event.getRawY() - mFirstY);
            if (centerY < getAdsorbHeight()) {
                //滑动距离<半屏=吸顶
                float topY = 0f + mToolBarHeight;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).y(topY).start();
            } else {
                //滑动距离>半屏=吸底
                float bottomY = getContentHeight() - mViewHeight;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).y(bottomY).start();
            }
        } else {
            // 下半屏
            double centerY = mViewHeight / 2 + Math.abs(event.getRawY() - mFirstY);
            if (centerY < getAdsorbHeight()) {
                //滑动距离<半屏=吸底
                float bottomY = getContentHeight() - mViewHeight;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).y(bottomY).start();
            } else {
                //滑动距离>半屏=吸顶
                float topY = 0f + mToolBarHeight;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).y(topY).start();
            }
        }
        resetHorizontal(event);
    }

    /**
     * 上下拖拽时，如果横向拖拽也超出屏幕，则上下吸边时左右也吸边
     */
    private void resetHorizontal(MotionEvent event) {
        if (event.getRawX() < mViewWidth) {
            animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).x(0F).start();
        } else if (event.getRawX() > getScreenWidth() - mViewWidth) {
            animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).x(getScreenWidth() - mViewWidth).start();
        }
    }

    /**
     * 左右吸边
     */
    private void adsorbLeftAndRight(MotionEvent event) {
        if (isOriginalFromLeft()) {
            // 左半屏
            double centerX = mViewWidth / 2 + Math.abs(event.getRawX() - mFirstX);
            if (centerX < getAdsorbWidth()) {
                //滑动距离<半屏=吸左
                float leftX = 0f;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).x(leftX).start();
            } else {
                //滑动距离<半屏=吸右
                float rightX = getScreenWidth() - mViewWidth;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).x(rightX).start();
            }
        } else {
            // 右半屏
            double centerX = mViewWidth / 2 + Math.abs(event.getRawX() - mFirstX);
            if (centerX < getAdsorbWidth()) {
                //滑动距离<半屏=吸右
                float rightX = getScreenWidth() - mViewWidth;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).x(rightX).start();
            } else {
                //滑动距离<半屏=吸左
                float leftX = 0f;
                animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).x(leftX).start();
            }
        }
        resetVertical(event);
    }

    /**
     * 左右拖拽时，如果纵向拖拽也超出屏幕，则左右吸边时上下也吸边
     */
    private void resetVertical(MotionEvent event) {
        if (event.getRawY() < mViewHeight) {
            animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).y(0F + mToolBarHeight).start();
        } else if (event.getRawY() > getContentHeight() - mViewHeight) {
            animate().setInterpolator(new DecelerateInterpolator()).setDuration(300).y(getContentHeight() - mViewHeight).start();
        }
    }

    /**
     * 是否缩到屏幕内
     */
    public boolean isInside() {
        return mIsInside;
    }

    /**
     * 初始位置是否在顶部
     */
    private boolean isOriginalFromTop() {
        return mFirstY < getScreenHeight() / 2;
    }

    /**
     * 初始位置是否在左边
     */
    private boolean isOriginalFromLeft() {
        return mFirstX < getScreenWidth() / 2;
    }

    /**
     * 获取上下吸边时需要拖拽的距离
     */
    private double getAdsorbHeight() {
        return getScreenHeight() * mDragDistance;
    }

    /**
     * 获取左右吸边时需要拖拽的距离
     */
    private double getAdsorbWidth() {
        return getScreenWidth() * mDragDistance;
    }

    /**
     * dp2px
     */
    private int dp2px(float dp) {
        float density = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * density);
    }

    /**
     * 获取屏幕高度
     */
    private int getScreenHeight() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * 获取屏幕宽度
     */
    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        ((Activity) getContext()).getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取页面内容区高度
     */
    private int getContentHeight() {
        int height = 0;
        View view = ((Activity) getContext()).getWindow().getDecorView().findViewById(android.R.id.content);
        if (view != null) {
            height = view.getBottom();
        }
        return height;
    }

    /**
     * 回收
     */
    public void release() {
        // do something
    }

    protected OnFloatClickListener mOnFloatClickListener;

    public interface OnFloatClickListener {
        void onClick(View view);
    }

    public void setOnFloatClickListener(OnFloatClickListener listener) {
        this.mOnFloatClickListener = listener;
    }

    public void setDragDistance(double distance) {
        this.mDragDistance = distance;
    }
}
