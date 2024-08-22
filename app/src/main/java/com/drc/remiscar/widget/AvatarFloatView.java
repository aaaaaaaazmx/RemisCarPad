package com.drc.remiscar.widget;

import android.content.Context;
import android.view.View;
import android.view.ViewTreeObserver;

import com.drc.remiscar.R;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.shape.ShapeAppearanceModel;

/**
 * GitHub : https://github.com/yechaoa
 * CSDN : http://blog.csdn.net/yechaoa
 *
 * Created by yechao on 2022/7/31.
 * Describe :
 */
public class AvatarFloatView extends BaseFloatView {
    private int mAdsorbType = ADSORB_VERTICAL;

    public AvatarFloatView(Context context) {
        super(context);
    }

    @Override
    protected View getChildView() {
        final ShapeableImageView imageView = new ShapeableImageView(getContext());
        imageView.setImageResource(R.mipmap.phone);
        imageView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                imageView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                // 获取宽度设置圆角
                /*float radius = imageView.getWidth() / 2f;
                imageView.setShapeAppearanceModel(new ShapeAppearanceModel().toBuilder().setAllCornerSizes(radius).build());*/
            }
        });
        return imageView;
    }

    @Override
    protected boolean getIsCanDrag() {
        return true;
    }

    @Override
    protected int getAdsorbType() {
        return mAdsorbType;
    }

    @Override
    public long getAdsorbTime() {
        return 1000;
    }

    public void setAdsorbType(int type) {
        mAdsorbType = type;
    }
}