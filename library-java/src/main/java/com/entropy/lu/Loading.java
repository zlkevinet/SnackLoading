package com.entropy.lu;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.*;
import android.widget.FrameLayout;
import com.entropy.lu.loading.R;
import com.entropy.lu.render.BaseLoadingRender;
import com.entropy.lu.render.GradientLoadingRender;
import com.entropy.lu.render.LottieLoadingRender;
import com.entropy.lu.render.ProgressBarLoadingRender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Loading工具类
 *
 * @author kevinet created at 2018/10/29
 */
public class Loading {
    private static final Loading INS = new Loading();

    private Map<LoadingRenderType, Class<? extends ILoadingRender>> mRenderMap = new HashMap<>();
    private Map<LoadingRenderType, Bundle> mRenderParams = new HashMap<>();
    private Map<View, ViewRecord> mRenderViewMap = new HashMap<>();

    static {
        reset();
    }

    private Loading() {
    }

    static class ViewRecord {
        LoadingConfig mConfig;
        ILoadingRender mRender;
    }


    /**
     * 默认以LoadingRenderType.ANIMATION类型显示loading
     *
     * @param context  上下文
     * @param fallback 绑定View
     * @return
     */
    public static Loading make(Context context, View fallback) {
        return make(context, LoadingRenderType.ANIMATION, null, fallback, false);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context  上下文
     * @param render   render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param fallback 绑定View
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, View fallback) {
        return make(context, render, null, fallback, false);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context  上下文
     * @param render   render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param fallback 绑定View
     * @param gravity  设置loading的位置，仅支持Gravity.Top和Gravity.Bottom，默认是Gravity.Bottom
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, View fallback, int gravity) {
        Bundle params = new Bundle();
        if (gravity == Gravity.TOP || gravity == Gravity.BOTTOM) {
            params.putInt(BaseLoadingRender.kGravity, gravity);
        }
        return make(context, render, params, fallback, false);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context  上下文
     * @param render   render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param params   render的参数配置
     * @param fallback 绑定View
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, Bundle params, View fallback) {
        return make(context, render, params, fallback, false);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context  上下文
     * @param render   render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param params   render的参数配置
     * @param fallback 绑定View
     * @param gravity  设置loading的位置，仅支持Gravity.Top和Gravity.Bottom，默认是Gravity.Bottom
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, Bundle params, View fallback, int gravity) {
        return make(context, render, params, fallback, false, gravity);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context      上下文
     * @param render       render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param fallback     绑定View
     * @param useMaskLayer 是否支持用户操作界面 默认情况支持
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, View fallback, boolean useMaskLayer) {
        return make(context, render, null, fallback, useMaskLayer);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context      上下文
     * @param render       render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param fallback     绑定View
     * @param useMaskLayer 是否支持用户操作界面 默认情况支持
     * @param gravity      设置loading的位置，仅支持Gravity.Top和Gravity.Bottom，默认是Gravity.Bottom
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, View fallback, boolean useMaskLayer, int gravity) {
        return make(context, render, null, fallback, useMaskLayer, gravity);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context      上下文
     * @param render       render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param params       render的参数配置
     * @param fallback     绑定View
     * @param useMaskLayer 是否支持用户操作界面 默认情况支持
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, Bundle params, View fallback, boolean useMaskLayer) {
        return make(context, render, params, fallback, useMaskLayer, Gravity.BOTTOM);
    }

    /**
     * 指定某一种LoadingRenderType的loading加入
     *
     * @param context      上下文
     * @param render       render类型 {@link LoadingRenderType#PROGRESS},{@link LoadingRenderType#ANIMATION},{@link LoadingRenderType#LOTTIE}
     * @param params       render的参数配置
     * @param fallback     绑定View
     * @param useMaskLayer 是否支持用户操作界面 默认情况支持
     * @param gravity      设置loading的位置，仅支持Gravity.Top和Gravity.Bottom，默认是Gravity.Bottom
     * @return
     */
    public static Loading make(Context context, LoadingRenderType render, Bundle params, View fallback, boolean useMaskLayer, int gravity) {
        LoadingConfig config = LoadingConfig.create()
                .setLoadingGravity(gravity)
                .setRenderType(render)
                .setWithMaskLayer(useMaskLayer)
                .setRenderParams(params)
                .build();
        return make(context, fallback, config);
    }

    /**
     * 指定配置的loading加入
     *
     * @param context
     * @param fallback
     * @param config
     * @return
     */
    public static Loading make(Context context, View fallback, LoadingConfig config) {
        LoadingRenderType render = config.getRenderType();
        Bundle params = config.getRenderParams();
        int gravity = config.getLoadingGravity();
        if (gravity == Gravity.TOP || gravity == Gravity.BOTTOM) {
            if (params == null) {
                params = new Bundle();
            }
            params.putInt(BaseLoadingRender.kGravity, gravity);
        }
        ViewRecord oldRecord = INS.mRenderViewMap.remove(fallback);
        if (oldRecord != null && oldRecord.mRender != null) {
            oldRecord.mRender.stop();
        }

        ViewRecord viewRecord = new ViewRecord();
        viewRecord.mRender = INS.newRender(context, render, params);
        viewRecord.mConfig = config;
        INS.mRenderViewMap.put(fallback, viewRecord);
        return INS;
    }

    /**
     * 显示所有
     */
    public void show() {
        for (Map.Entry<View, ViewRecord> it : INS.mRenderViewMap.entrySet()) {
            show(it.getKey());
        }
    }

    /**
     * 指定View显示loading
     *
     * @param fallback 绑定View
     */
    public static void show(final View fallback) {
        if (fallback == null || fallback.getParent() == null) {
            return;
        }

        Object renderRunner = fallback.getTag(R.id.id_fallback_render_runner);
        if (renderRunner instanceof Runnable) {
            fallback.removeCallbacks((Runnable) renderRunner);
        }
        renderRunner = new Runnable() {
            @Override
            public void run() {
                if (fallback.getParent() == null) {
                    return;
                }
                ViewRecord viewRecord = INS.mRenderViewMap.get(fallback);
                if (viewRecord != null && viewRecord.mConfig != null) {
                    ILoadingRender render = viewRecord.mRender;
                    boolean useMaskLayer = viewRecord.mConfig.isWithMaskLayer();
                    renderStart(fallback, render, useMaskLayer);
                }
            }
        };
        ViewRecord viewRecord = INS.mRenderViewMap.get(fallback);
        if (viewRecord != null && viewRecord.mConfig != null) {
            fallback.setTag(R.id.id_fallback_render_runner, renderRunner);
            fallback.postDelayed((Runnable) renderRunner, viewRecord.mConfig.getStartDelay());
        }
    }

    private static void renderStart(final View fallback, final ILoadingRender render, boolean useMaskLayer) {
        if (useMaskLayer) {
            final ViewGroup maskLayer = INS.addMaskLayer(fallback);
            final InterceptableDialog maskDialog = new InterceptableDialog(fallback.getContext());
            render.layoutRender(fallback, maskLayer);
            render.addCallback(new ILoadingRender.SimpleCallback() {
                @Override
                public void onStart() {
                    super.onStart();
                    if (maskLayer != null) {
                        maskLayer.setVisibility(View.VISIBLE);
                    }
                    Context context = fallback.getContext();
                    if (context instanceof Activity
                            && !((Activity) context).isFinishing()
                            && Looper.myLooper() == Looper.getMainLooper()) {
                        maskDialog.show();
                        Log.d("zl-loading", "mask dialog show on fallback " + fallback);
                    }
                }

                @Override
                public void onStop() {
                    super.onStop();
                    if (maskLayer != null) {
                        maskLayer.setVisibility(View.GONE);
                    }
                    maskDialog.dismiss();
                    Log.d("zl-loading", "mask dialog hide on fallback " + fallback);
                }

            });
        } else {
            render.layoutRender(fallback, null);
        }

        render.start();
        Log.d("zl-loading", "render[" + render + "] start on fallback " + fallback);
    }

    private ViewGroup addMaskLayer(View fallback) {
        // 用户未提供蒙层或者提供的蒙层没有在view tree中，将自动添加到DecorView中
        ViewGroup decorContentView = null;
        try {
            decorContentView = findDecorContentView(fallback);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (decorContentView == null) {
            return null;
        }

        ViewRecord viewRecord = INS.mRenderViewMap.get(fallback);
        int maskBackgroundColor = LoadingConfig.DEFAULT_MASK_LAYER_BACKGROUND_COLOR;
        if (viewRecord != null && viewRecord.mConfig != null) {
            maskBackgroundColor = viewRecord.mConfig.getMaskBackgroundColor();
        }
        InterceptableFrameLayer loadedMaskLayer = decorContentView.findViewWithTag("progress_mask_layer");
        if (loadedMaskLayer == null) {
            //第一次添加
            InterceptableFrameLayer interceptableFrameLayer = new InterceptableFrameLayer(decorContentView.getContext());
            interceptableFrameLayer.setTag("progress_mask_layer");
            interceptableFrameLayer.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
            interceptableFrameLayer.setInterceptable(true);
            loadedMaskLayer = interceptableFrameLayer;
            decorContentView.addView(loadedMaskLayer);
        } else {
            loadedMaskLayer.setInterceptable(true);
        }
        loadedMaskLayer.setBackgroundColor(maskBackgroundColor);
        return loadedMaskLayer;
    }


    /**
     * 找到DecorView容器
     */
    private ViewGroup findDecorContentView(View view) throws IllegalArgumentException {
        View curView = view;
        do {
            if (curView instanceof FrameLayout && curView.getId() == android.R.id.content) {
                return (FrameLayout) curView;
            }

            if (curView != null) {
                ViewParent parent = curView.getParent();
                if (parent instanceof View) {
                    curView = (View) parent;
                } else {
                    curView = null;
                }
            }
        } while (curView != null);

        throw new IllegalArgumentException("ContentView(android.R.id.content) not found!");
    }

    /**
     * 隐藏loading
     *
     * @param fallback 绑定View
     */
    public static void hide(View fallback) {
        if (fallback == null) {
            return;
        }

        ViewRecord record = INS.mRenderViewMap.remove(fallback);
        if (record != null && record.mRender != null) {
            record.mRender.stop();
            Log.d("zl-loading", "render[" + record.mRender + "] stop on fallback " + fallback);
        }
    }

    /**
     * 隐藏所有
     */
    public static void hide() {
        for (Iterator<Map.Entry<View, ViewRecord>> iterator = INS.mRenderViewMap.entrySet().iterator(); iterator.hasNext(); ) {
            Map.Entry<View, ViewRecord> it = iterator.next();
            View fallback = it.getKey();
            ViewRecord record = INS.mRenderViewMap.get(fallback);
            if (record != null && record.mRender != null) {
                record.mRender.stop();
                iterator.remove();
                Log.d("zl-loading", "render[" + record.mRender + "] stop on fallback " + fallback);
            }
        }
    }

    /**
     * 是否显示
     *
     * @param fallback 绑定View
     */
    public static boolean isShowing(View fallback) {
        ViewRecord record = INS.mRenderViewMap.get(fallback);
        if (record == null || record.mRender == null) {
            return false;
        }

        return record.mRender.isRunning();
    }

    /**
     * 是否显示
     */
    public static boolean isShowing() {
        boolean isRunning = false;

        for (Map.Entry<View, ViewRecord> it : INS.mRenderViewMap.entrySet()) {

            ILoadingRender render = it.getValue().mRender;
            if (render != null && render.isRunning()) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }

    /**
     * 设置渲染器对应类
     *
     * @param renderType   类型
     * @param renderClass  实现类
     * @param renderParams 参数列表
     */
    public static void configRender(LoadingRenderType renderType, Class<? extends ILoadingRender> renderClass, Bundle renderParams) {
        INS.mRenderMap.put(renderType, renderClass);
        if (renderParams != null) {
            Bundle bundle = INS.mRenderParams.get(renderType);
            if (bundle != null) {
                bundle.putAll(renderParams);
            } else {
                INS.mRenderParams.put(renderType, renderParams);
            }
        }
    }

    /**
     * 构建渲染器
     */
    private ILoadingRender newRender(Context context, LoadingRenderType renderType, Bundle renderParams) {
        Class<? extends ILoadingRender> renderClass = mRenderMap.get(renderType);
        ILoadingRender render = null;
        if (renderClass != null) {
            try {
                render = renderClass.newInstance();
                Bundle params = mRenderParams.get(renderType);
                if (params != null) {
                    params = (Bundle) params.clone();
                    if (renderParams != null) {
                        params.putAll(renderParams);
                    }
                    render.createRender(context, params);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }

            return render;
        }

        throw new IllegalStateException("The RenderType[$renderType] isn't supported! ");
    }

    /**
     * 重置默认
     */
    private static void reset() {
        Bundle progressParams = new Bundle();
        progressParams.putInt(ProgressBarLoadingRender.kIndeterminateDrawableId, R.drawable.progress_indeterminate_horizontal_holo);
        progressParams.putInt(BaseLoadingRender.kInterpolatorId, android.R.interpolator.linear);
        progressParams.putInt(BaseLoadingRender.kDuration, 1200);
        configRender(LoadingRenderType.PROGRESS, ProgressBarLoadingRender.class, progressParams);

        Bundle animationParams = new Bundle();
        animationParams.putInt(GradientLoadingRender.kHeadImageResourceId, R.drawable.img_loading_head);
        animationParams.putInt(GradientLoadingRender.kBodyImageResourceId, R.drawable.img_loading_body);
        configRender(LoadingRenderType.ANIMATION, GradientLoadingRender.class, animationParams);

        Bundle lottieParams = new Bundle();
        lottieParams.putString(LottieLoadingRender.kAnimatorAssetFileName, "loading.json");

        ArrayList<LottieLoadingRender.LoopRange> loopRanges = new ArrayList<>();
        loopRanges.add(new LottieLoadingRender.LoopRange(30, Integer.MAX_VALUE, new int[]{1, Integer.MAX_VALUE}));
        lottieParams.putParcelableArrayList(LottieLoadingRender.kAnimatorLoopRanges, loopRanges);
        configRender(LoadingRenderType.LOTTIE, LottieLoadingRender.class, lottieParams);
    }

    /**
     * 内部蒙层
     */
    class InterceptableFrameLayer extends FrameLayout {
        private boolean isInterceptEnable = false;

        public InterceptableFrameLayer(Context context) {
            super(context);
        }

        void setInterceptable(boolean enable) {
            isInterceptEnable = enable;
        }

        @Override
        public boolean dispatchKeyEvent(KeyEvent event) {
            if (isInterceptEnable) {
                return true;
            }
            return super.dispatchKeyEvent(event);
        }


        @Override
        public boolean dispatchTouchEvent(MotionEvent ev) {
            if (isInterceptEnable) {
                return true;
            }
            return super.dispatchTouchEvent(ev);
        }

        @Override
        public boolean onKeyDown(int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && isInterceptEnable) {
                return true;
            }
            return super.onKeyDown(keyCode, event);
        }

        @Override
        public boolean dispatchKeyEventPreIme(KeyEvent event) {
            if (isInterceptEnable) {
                return true;
            }
            return super.dispatchKeyEventPreIme(event);
        }

        @Override
        public boolean dispatchKeyShortcutEvent(KeyEvent event) {
            if (isInterceptEnable) {
                return true;
            }
            return super.dispatchKeyShortcutEvent(event);
        }

    }

    static class InterceptableDialog extends Dialog {
        InterceptableDialog(@NonNull Context context) {
            super(context, R.style.Transparent);
            setCancelable(false);
            setCanceledOnTouchOutside(false);
            getWindow().setDimAmount(0f);
        }

    }


}
