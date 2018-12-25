package com.entropy.lu.render;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import com.entropy.lu.ILoadingRender;

import java.util.ArrayList;
import java.util.List;

/**
 * 渲染器抽象基础实现
 * <p>
 *
 * @author kevinet created at 2018/10/18
 */
public abstract class BaseLoadingRender implements ILoadingRender {

    public static final int MAX_PARENT_LEVEL = 1;
    public static final String kDuration = "Loading::Duration";
    public static final String kInterpolatorId = "Loading::Interpolator::Id";
    public static final String kBackgroundColor = "Loading::Background::Color";
    public static final String kGravity = "Loading::Gravity";

    public static final int STATUS_RENDER_HIDE = 0;
    public static final int STATUS_RENDER_CREATED = 1;
    public static final int STATUS_RENDER_LAYOUT = 2;
    public static final int STATUS_RENDER_SHOWING = 3;

    public int mRenderStatus = STATUS_RENDER_HIDE;

    List<Callback> mCallbacks = new ArrayList<>();
    private int mSearchParentLevel = 0;
    private View mRenderView;
    private int mGravity = Gravity.BOTTOM;

    private boolean moveStatus(int status) {
        boolean validStatusChange = false;
        switch (mRenderStatus) {
            case STATUS_RENDER_HIDE: {
                switch (status) {
                    case STATUS_RENDER_CREATED:
                    case STATUS_RENDER_HIDE:
                        validStatusChange = true;
                        break;
                }
                break;
            }
            case STATUS_RENDER_CREATED: {
                switch (status) {
                    case STATUS_RENDER_LAYOUT:
                    case STATUS_RENDER_HIDE:
                        validStatusChange = true;
                        break;
                }
            }
            case STATUS_RENDER_LAYOUT: {
                switch (status) {
                    case STATUS_RENDER_SHOWING:
                        validStatusChange = true;
                        break;
                }
            }
            case STATUS_RENDER_SHOWING: {
                switch (status) {
                    case STATUS_RENDER_HIDE:
                        validStatusChange = true;
                        break;
                }
            }
        }
        if (validStatusChange) {
            mRenderStatus = status;
            return false;
        }

        Log.e("zl-loading", "[ERROR] status " + mRenderStatus + "can't move to " + status);
        return true;
    }

    @Override
    public void createRender(Context context, Bundle params) {
        if (moveStatus(STATUS_RENDER_CREATED)) {
            return;
        }

        mRenderView = onCreateView(context, params);
        mGravity = params.getInt(kGravity, Gravity.BOTTOM);
        for (Callback it : mCallbacks) {
            it.onCreate(params);
        }
    }

    @Override
    final public void layoutRender(final View fallback, ViewGroup maskLayer) {
        if (moveStatus(STATUS_RENDER_LAYOUT)) {
            return;
        }
//        if (fallback != null) {
//            fallback.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//                @Override
//                public void onGlobalLayout() {
//                    View rootView = fallback.getRootView();
//                    int heightDiff = rootView.getHeight() - fallback.getHeight();
//                    Log.d("zl-loading", "onGlobalLayout .. heightDiff = " + heightDiff);
//                }
//            });
//        }
        mSearchParentLevel = 0;
        ViewGroup fallbackParent = findSuitableParent(fallback);
        onLayoutRender(fallback, fallbackParent, maskLayer);

        for (Callback it : mCallbacks) {
            it.onLayout();
        }
    }

    /**
     * 布局Render在容器中的位置
     *  @param fallback       可依靠的view
     * @param fallbackParent 可依靠的view的容器
     * @param maskLayer  浮层容器
     */
    public void onLayoutRender(View fallback, ViewGroup fallbackParent, ViewGroup maskLayer) {
        if (mRenderView != null && mRenderView.getParent() != null) {
            ((ViewGroup)mRenderView.getParent()).removeView(mRenderView);
        }

        if (maskLayer != null) {
            layoutRenderInMaskLayer(fallback, maskLayer);
        } else if(fallbackParent != null) {
            layoutRenderInFallbackView(fallback, fallbackParent);
        }
    }

    /**
     * 在MaskLayer中添加RenderView
     * @param fallback
     * @param maskLayer
     */
    private void layoutRenderInMaskLayer(View fallback, ViewGroup maskLayer) {
        int renderViewWidth = fallback.getMeasuredWidth();
        int[] screenLocation = new int[2];
        fallback.getLocationOnScreen(screenLocation);
        int fallbackX = screenLocation[0];
        int fallbackY = screenLocation[1];

        maskLayer.getLocationOnScreen(screenLocation);
        int targetX = screenLocation[0];
        int targetY = screenLocation[1];

        mRenderView.setX(((float) (fallbackX - targetX)));
        if(mGravity == Gravity.TOP) {
            mRenderView.setY(((float) (fallbackY - targetY)));
        } else {
            mRenderView.setY(((float) (fallbackY - targetY)) + fallback.getMeasuredHeight());
        }
        maskLayer.addView(mRenderView, new ViewGroup.LayoutParams(renderViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    /**
     * 在FallbackLayer中添加RenderView
     * @param fallback
     * @param fallbackParent
     */
    private void layoutRenderInFallbackView(View fallback, ViewGroup fallbackParent) {
        int renderViewWidth = fallback.getMeasuredWidth();
        ViewGroup.LayoutParams layoutParam = buildLayoutParams(fallback, fallbackParent,
                new ViewGroup.LayoutParams(renderViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT));
        if (fallbackParent.getLayoutParams() != null
                && fallbackParent == fallback
                && mGravity == Gravity.BOTTOM
                && fallbackParent.getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            //如果父类高度为Wrap_content，直接添加一个Bottom位置将导致父类高度变化，此处进行固定FrameLayout的包装，修复此类问题
            FrameLayout renderViewHolder = new FrameLayout(fallback.getContext());
            int fallbackHeight = fallbackParent.getMeasuredHeight();
            if (fallbackHeight > 0) {
                renderViewHolder.setLayoutParams(new FrameLayout.LayoutParams(renderViewWidth,fallbackHeight));
            } else {
                renderViewHolder.setLayoutParams(new FrameLayout.LayoutParams(renderViewWidth,getBorder().height()));
            }
            FrameLayout.LayoutParams newLayoutParam = new FrameLayout.LayoutParams(renderViewWidth, getBorder().height());
            newLayoutParam.gravity = Gravity.BOTTOM;
            renderViewHolder.addView(mRenderView, newLayoutParam);
            fallbackParent.addView(renderViewHolder);
        } else {
            fallbackParent.addView(mRenderView, layoutParam);
        }
    }

    /**
     * 适应各类型ViewGroup的LayoutParams
     * @param fallback
     * @param fallbackParent
     * @param layoutParam
     * @return
     */
    private ViewGroup.LayoutParams buildLayoutParams(View fallback, ViewGroup fallbackParent, ViewGroup.LayoutParams layoutParam) {
        int renderViewWidth = fallback.getMeasuredWidth();
        if (fallbackParent instanceof FrameLayout) {
            layoutParam = new FrameLayout.LayoutParams(renderViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (fallback == fallbackParent) {
                if(mGravity == Gravity.TOP) {
                    ((FrameLayout.LayoutParams) layoutParam).gravity = Gravity.TOP;
                } else {
                    ((FrameLayout.LayoutParams) layoutParam).gravity = Gravity.BOTTOM;
                }
            } else {
                if(mGravity == Gravity.TOP) {
                    ((FrameLayout.MarginLayoutParams) layoutParam).topMargin = 0;
                } else {
                    ((FrameLayout.MarginLayoutParams) layoutParam).topMargin = fallback.getMeasuredHeight();
                }
            }
        } else if (fallbackParent instanceof ConstraintLayout) {
            layoutParam = new ConstraintLayout.LayoutParams(renderViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (fallback == fallbackParent) {
                if(mGravity == Gravity.TOP) {
                    ((ConstraintLayout.LayoutParams)layoutParam).topToTop = fallback.getId();
                } else {
                    ((ConstraintLayout.LayoutParams)layoutParam).bottomToBottom = fallback.getId();
                }
            } else {
                if(mGravity == Gravity.TOP) {
                    ((ConstraintLayout.LayoutParams)layoutParam).topToTop = fallback.getId();
                } else {
                    ((ConstraintLayout.LayoutParams)layoutParam).topToBottom = fallback.getId();
                }
            }
        } else if (fallbackParent instanceof RelativeLayout) {
            layoutParam = new RelativeLayout.LayoutParams(renderViewWidth, ViewGroup.LayoutParams.WRAP_CONTENT);
            if (fallback == fallbackParent) {
                if(mGravity == Gravity.TOP) {
                    ((RelativeLayout.LayoutParams) layoutParam).addRule(RelativeLayout.ALIGN_PARENT_TOP);
                } else {
                    ((RelativeLayout.LayoutParams) layoutParam).addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                }

            } else {
                if(mGravity == Gravity.TOP) {
                    ((RelativeLayout.LayoutParams) layoutParam).addRule(RelativeLayout.ALIGN_TOP, fallback.getId());
                } else {
                    ((RelativeLayout.LayoutParams) layoutParam).addRule(RelativeLayout.BELOW, fallback.getId());
                }
            }
        }
        return layoutParam;
    }

    /**
     * 获得边框大小
     */
    @Override
    public Rect getBorder() {
        return new Rect(0, 0, mRenderView.getMeasuredWidth(), mRenderView.getMeasuredHeight());
    }

    /**
     * 找到合适的父类
     */
    private ViewGroup findSuitableParent(View fallback) {
        if (mSearchParentLevel > maxParentSearchLevel()) {
            return null;
        }

        mSearchParentLevel++;
        ViewParent parent = fallback.getParent();
        if (chooseSuitableParent(fallback)) {
            return (ViewGroup) fallback;
        } else if (parent instanceof ViewGroup) {
            return findSuitableParent((ViewGroup) parent);
        }
        return null;
    }

    /**
     * 获得内部RenderView
     */
    @Override
    public View getView() {
        return mRenderView;
    }

    /**
     * 默认最大父级搜索层级为1层
     */
    protected int maxParentSearchLevel() {
        return MAX_PARENT_LEVEL;
    }

    /**
     * 找到合适的父类容器
     */
    private boolean chooseSuitableParent(View fallback) {
        return fallback instanceof FrameLayout || fallback instanceof ConstraintLayout || fallback instanceof RelativeLayout;
    }

    @Override
    final public void start() {
        if (moveStatus(STATUS_RENDER_SHOWING)) {
            return;
        }
        Log.d("zl-loading", "base render start");
        onStartLoading();
        for (Callback it : mCallbacks) {
            it.onStart();
        }
    }

    @Override
    final public void stop() {
        if (moveStatus(STATUS_RENDER_HIDE)) {
            return;
        }
        Log.d("zl-loading", "base render stop");
        onStopLoading();
        if (mRenderView != null && mRenderView.getParent() != null) {
            ((ViewGroup) mRenderView.getParent()).removeView(mRenderView);
        }
        for (Callback it : mCallbacks) {
            it.onStop();
        }
        mCallbacks.clear();
    }

    @Override
    final public void addCallback(Callback callback) {
        this.mCallbacks.add(callback);
    }

    /**
     * 初始化Render内容
     *
     * @param context 上下文
     * @param params  Render的初始化参数
     * @return 带有loading的RenderView视图
     */
    abstract View onCreateView(Context context, Bundle params);

    /**
     * 显示渲染
     */
    abstract void onStartLoading();

    /**
     * 隐藏渲染
     */
    abstract void onStopLoading();
}