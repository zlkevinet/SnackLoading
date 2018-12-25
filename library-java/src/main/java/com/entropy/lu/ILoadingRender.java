package com.entropy.lu;

import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;

/**
 * <p>
 *     Loading动效渲染接口
 *
 * @author kevinet created at 2018/10/29
 */
public interface ILoadingRender extends Animatable {
    /**
     * 创建render
     */
    void createRender(Context context, Bundle params);

    /**
     * 布局render
     */
    void layoutRender(View fallback, ViewGroup maskLayer);

    /**
     * 获取render view
     */
    View getView();

    /**
     * 面积
     */
    Rect getBorder();

    /**
     * 设置监听
     */
    void addCallback(Callback callback);

    interface Callback extends Drawable.Callback {
        void onCreate(Bundle params);

        void onLayout();

        void onStart();

        void onStop();
    }

    class SimpleCallback implements Callback {
        @Override
        public void invalidateDrawable(Drawable who) {

        }

        @Override
        public void scheduleDrawable(Drawable who, Runnable what, long when) {

        }

        public void unscheduleDrawable(Drawable who, Runnable what) {
        }


        @Override
        public void onCreate(Bundle params) {

        }

        @Override
        public void onLayout() {

        }

        @Override
        public void onStart() {

        }

        @Override
        public void onStop() {

        }
    }
}
