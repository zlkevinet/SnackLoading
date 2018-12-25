package com.entropy.lu.render;


import android.animation.Animator;
import android.content.Context;
import android.graphics.*;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.view.ViewGroup;
import com.entropy.lu.loading.R;

/**
 * <p>
 *     渐变长条Loading
 *
 * @author kevinet created at 2018/10/19
 */
public class GradientLoadingRender extends AnimationLoadingRender {

    public static final String kHeadImageResourceId = "Loading::Gradient::Head::Image::ID";
    public static final String kBodyImageResourceId = "Loading::Gradient::Body::Image::ID";

    private final static int NONE_FRAME = 0;
    private final static int FIRST_SHOW_FRAME = 1;
    private final static int LOOP_FRAME = 2;

    private int mCurrentFrame = NONE_FRAME;

    private Bitmap mImgHead;
    private Bitmap mImgBody;

    private Rect mHeadRawRect;
    private Rect mBodyRawRect;

    private RectF mHeadFrameRect;
    private RectF mBodyFrameRect;

    private int mHeadWidth;
    private int mHeadHeight;
    private int mBodyWidth;
    private int mBodyHeight;

    private int mLoadingWidth;
    private int mLoadingHeight;

    @Override
    public View onCreateView(Context context, Bundle params) {
        View view = super.onCreateView(context, params);

        mImgHead = BitmapFactory.decodeResource(context.getResources(), params.getInt(kHeadImageResourceId, R.drawable.img_loading_head));
        mImgBody = BitmapFactory.decodeResource(context.getResources(), params.getInt(kBodyImageResourceId, R.drawable.img_loading_body));

        if (mImgHead != null) {
            mHeadWidth = mImgHead.getWidth();
            mHeadHeight = mImgHead.getHeight();

            mHeadRawRect = new Rect(0, 0, mHeadWidth, mHeadHeight);
            mHeadFrameRect = new RectF(mHeadRawRect);

            if (mImgBody != null) {
                mBodyWidth = mImgBody.getWidth();
                mBodyHeight = mImgBody.getHeight();

                mBodyRawRect = new Rect(0, 0, mBodyWidth, mBodyHeight);
                mBodyFrameRect = new RectF(mBodyRawRect);
            }
        }

        return view;
    }

    @Override
    public void onLayoutRender(View fallback, @Nullable ViewGroup fallbackParent, @Nullable ViewGroup maskLayer) {
        mLoadingWidth = fallback.getMeasuredWidth();
        mLoadingHeight = Math.max(mBodyHeight, mHeadHeight);
        super.onLayoutRender(fallback, fallbackParent, maskLayer);
    }

    @Override
    public void computeRender(float progress) {
        float xOffset = (float) (mLoadingWidth + mHeadWidth) * progress;
        if (mCurrentFrame == FIRST_SHOW_FRAME) {
            mHeadFrameRect.offsetTo(-mHeadWidth + xOffset, 0);
            mBodyFrameRect.offsetTo(-(mLoadingWidth + mHeadWidth) + xOffset, 0);
        } else if (mCurrentFrame == LOOP_FRAME) {
            mHeadFrameRect.offsetTo(-mHeadWidth + xOffset, 0);
        }
    }

    @Override
    public Rect getBorder() {
        return new Rect(0 , 0, mLoadingWidth, mLoadingHeight);
    }

    @Override
    public void draw(Canvas canvas, Rect border) {
        if (mImgBody == null || mImgHead == null) {
            return;
        }
        canvas.save();
        canvas.drawBitmap(mImgBody, mBodyRawRect, mBodyFrameRect, null);
        canvas.drawBitmap(mImgHead, mHeadRawRect, mHeadFrameRect, null);
        canvas.restore();
    }

    @Override
    public void onStartLoading() {
        updateFrameState(FIRST_SHOW_FRAME);
        super.onStartLoading();
    }

    @Override
    public void onStopLoading() {
        updateFrameState(NONE_FRAME);
        super.onStopLoading();
    }

    @Override
    public void onAnimationRepeat(@Nullable Animator animation) {
        super.onAnimationRepeat(animation);
        updateFrameState(LOOP_FRAME);
    }

    @Override
    public void onAnimationCancel(@Nullable Animator animation) {
        super.onAnimationCancel(animation);
        updateFrameState(NONE_FRAME);
    }

    private void updateFrameState(int state) {
        switch (state) {
            case NONE_FRAME:
                mHeadFrameRect.left = -mHeadWidth;
                mHeadFrameRect.right = 0;

                mBodyFrameRect.left = -mBodyWidth;
                mBodyFrameRect.right = 0;
                break;
            case FIRST_SHOW_FRAME:
                mHeadFrameRect.left = -mHeadWidth;
                mHeadFrameRect.right = 0;

                mBodyFrameRect.left = -(mLoadingWidth + mHeadWidth);
                mBodyFrameRect.right = -mHeadWidth;
                break;
            case LOOP_FRAME:
                mHeadFrameRect.left = -mHeadWidth;
                mHeadFrameRect.right = 0;

                mBodyFrameRect.left = 0;
                mBodyFrameRect.right = mLoadingWidth;
                break;
        }

        mHeadFrameRect.top = 0;
        mHeadFrameRect.bottom = mLoadingHeight;
        mBodyFrameRect.top = 0;
        mBodyFrameRect.bottom = mLoadingHeight;

        mCurrentFrame = state;
    }
}
