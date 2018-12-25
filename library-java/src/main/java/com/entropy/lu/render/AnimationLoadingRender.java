package com.entropy.lu.render;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import com.entropy.lu.ILoadingRender;

/**
 * 动画类型的loading渲染器
 * <p>
 * @author kevinet created at 2018/10/18
 */
public abstract class AnimationLoadingRender extends BaseLoadingRender implements Animator.AnimatorListener {

    public static final String kImageDrawableId = "Loading::Animation::Image::Drawable::Id";

    protected ImageView mAnimationView;
    protected Drawable mAnimationDrawable;
    private ValueAnimator mRenderAnimation;

    @Override
    public View onCreateView(Context context, Bundle params) {
        mAnimationView = createImageView(context);
        mAnimationDrawable = createImageDrawable(context, params);

        mAnimationView.setBackgroundColor(params.getInt(kBackgroundColor, 0));

        mRenderAnimation = ValueAnimator.ofFloat(0f, 1f);
        mRenderAnimation.setRepeatCount(ValueAnimator.INFINITE);
        mRenderAnimation.setRepeatMode(ValueAnimator.RESTART);
        mRenderAnimation.setDuration(params.getLong(kDuration, 1200));
        mRenderAnimation.setInterpolator(AnimationUtils.loadInterpolator(context, params.getInt(kInterpolatorId, android.R.interpolator.linear)));
        mRenderAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                computeRender((float)animation.getAnimatedValue());
                invalidateRender();
            }
        });
        mRenderAnimation.addListener(this);

        return mAnimationView;
    }

    @Override
    public void onLayoutRender(View fallback, ViewGroup fallbackParent, ViewGroup maskLayer) {
        super.onLayoutRender(fallback, fallbackParent, maskLayer);
        mAnimationView.setImageDrawable(mAnimationDrawable);
        mAnimationDrawable.setBounds(getBorder());
    }

    /**
     * 更新边框
     * @param border
     */
    public void updateBorder(Rect border) {
        mAnimationDrawable.setBounds(border);
    }

    private void invalidateRender() {
        for (Callback it : mCallbacks) {
            it.invalidateDrawable(mAnimationDrawable);
        }
    }

    /**
     *  计算动画渲染进度
     *  @param progress [0-1]之间
     */
    protected abstract void computeRender(float progress);

    /**
     * 绘制
     */
    protected abstract void draw(Canvas canvas, Rect border);

    protected ImageView createImageView(Context context) {
        return new LoadingImageView(context);
    }

    protected Drawable createImageDrawable(Context context, Bundle params) {
        return new LoadingDrawable(this);
    }

    @Override
    public void onAnimationStart(Animator animation) {

    }

    @Override
    public void onAnimationEnd(Animator animation) {

    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    @Override
    public void onStartLoading() {
        mRenderAnimation.start();
    }

    @Override
    public void onStopLoading() {
        mRenderAnimation.cancel();
    }

    @Override
    public boolean isRunning() {
        return mRenderAnimation.isRunning();
    }

    class LoadingImageView extends android.support.v7.widget.AppCompatImageView {
        public LoadingImageView(Context context) {
            super(context);
        }
    }

    class LoadingDrawable extends Drawable implements Animatable {

        private ILoadingRender mLoadingRender;

        private ILoadingRender.Callback mCallback = new ILoadingRender.SimpleCallback() {
            @Override
            public void invalidateDrawable(Drawable who) {
                super.invalidateDrawable(who);
                invalidateSelf();
            }

            @Override
            public void scheduleDrawable(Drawable who, Runnable what, long when) {
                super.scheduleDrawable(who, what, when);
                scheduleSelf(what, when);
            }

            @Override
            public void unscheduleDrawable(Drawable who, Runnable what) {
                super.unscheduleDrawable(who, what);
                unscheduleSelf(what);
            }
        };

        public LoadingDrawable(ILoadingRender render) {
            this.mLoadingRender = render;
            mLoadingRender.addCallback(mCallback);
        }

        @Override
        public void start() {
            mLoadingRender.start();
        }

        @Override
        public void stop() {
            mLoadingRender.stop();
        }

        @Override
        public boolean isRunning() {
            return mLoadingRender.isRunning();
        }

        @Override
        public void draw(Canvas canvas) {
            if (!getBounds().isEmpty() && mLoadingRender instanceof AnimationLoadingRender) {
                ((AnimationLoadingRender)mLoadingRender).draw(canvas, getBounds());
            }
        }

        @Override
        public void setAlpha(int alpha) {

        }

        @Override
        public void setColorFilter(ColorFilter colorFilter) {

        }

        @Override
        public int getOpacity() {
            return PixelFormat.TRANSLUCENT;
        }

        @Override
        public int getIntrinsicWidth() {
            return mLoadingRender.getBorder().width();
        }

        @Override
        public int getIntrinsicHeight() {
            return mLoadingRender.getBorder().height();
        }
    }

}
