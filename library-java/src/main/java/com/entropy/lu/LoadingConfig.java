package com.entropy.lu;

import android.os.Bundle;
import android.view.Gravity;

import java.util.Arrays;

/**
 * Loading的配置模型
 *
 * @author kevinet created at 2018/11/01
 */
public class LoadingConfig {

    public static final int DEFAULT_MASK_LAYER_BACKGROUND_COLOR = 0x4CFFFFFF;

    /**
     * Loading渲染类型
     */
    private LoadingRenderType renderType;
    /**
     * 渲染类型参数
     */
    private Bundle renderParams;
    /**
     * 是否显示阻塞蒙层
     */
    private boolean withMaskLayer;

    /**
     * 阻塞蒙层的背景颜色
     */
    private int maskLayerBackgroundColor;

    /**
     * 配置loading位置
     */
    private int loadingGravity;

    /**
     * 配置延迟显示
     */
    private long startDelay;

    public LoadingConfig() {
    }

    public static Builder create() {
        // 默认设置
        LoadingConfig config = new LoadingConfig();
        config.setStartDelay(300);
        config.setLoadingGravity(Gravity.BOTTOM);
        config.setRenderType(LoadingRenderType.ANIMATION);
        config.setMaskBackgroundColor(DEFAULT_MASK_LAYER_BACKGROUND_COLOR);
        return new Builder(config);
    }

    /**
     * 合并配置
     * @param config
     */
    public void merge(LoadingConfig config) {
        if (config != null) {
            if (config.getStartDelay() > 0) {
                setStartDelay(config.getStartDelay());
            }
            setWithMaskLayer(config.isWithMaskLayer());
            if (config.getRenderType() != null) {
                setRenderType(config.getRenderType());
            }
            if (config.getMaskBackgroundColor() != 0) {
                setMaskBackgroundColor(config.getMaskBackgroundColor());
            }
            if (config.getLoadingGravity() != 0) {
                setLoadingGravity(config.getLoadingGravity());
            }
            if (config.getRenderParams() != null) {
                Bundle defaultParams = getRenderParams();
                if (defaultParams != null) {
                    defaultParams.putAll(config.getRenderParams());
                } else {
                    setRenderParams(config.getRenderParams());
                }
            }
        }
    }

    public static class Builder {
        private LoadingConfig config;

        Builder(LoadingConfig config) {
            this.config = config;
        }

        public Builder setStartDelay(long startDelay) {
            config.setStartDelay(startDelay);
            return this;
        }

        public Builder setRenderType(LoadingRenderType renderType) {
            config.setRenderType(renderType);
            return this;
        }

        public Builder setRenderParams(Bundle renderParams) {
            config.setRenderParams(renderParams);
            return this;
        }
        public Builder setWithMaskLayer(boolean withMaskLayer) {
            config.setWithMaskLayer(withMaskLayer);
            return this;
        }
        public Builder setLoadingGravity(int loadingGravity) {
            config.setLoadingGravity(loadingGravity);
            return this;
        }

        public Builder setMaskBackgroundColor(int color) {
            config.setMaskBackgroundColor(color);
            return this;
        }

        public LoadingConfig build(){
            return config;
        }
    }

    public long getStartDelay() {
        return startDelay;
    }

    private void setStartDelay(long startDelay) {
        this.startDelay = startDelay;
    }

    public LoadingRenderType getRenderType() {
        return renderType;
    }

    public void setRenderType(LoadingRenderType renderType) {
        this.renderType = renderType;
    }

    public Bundle getRenderParams() {
        return renderParams;
    }

    public void setRenderParams(Bundle renderParams) {
        this.renderParams = renderParams;
    }

    public void setMaskBackgroundColor(int color) {
        maskLayerBackgroundColor = color;
    }

    public int getMaskBackgroundColor() {
        return maskLayerBackgroundColor;
    }

    public boolean isWithMaskLayer() {
        return withMaskLayer;
    }

    public void setWithMaskLayer(boolean withMaskLayer) {
        this.withMaskLayer = withMaskLayer;
    }

    public int getLoadingGravity() {
        return loadingGravity;
    }

    public void setLoadingGravity(int loadingGravity) {
        if (loadingGravity == Gravity.TOP || loadingGravity == Gravity.BOTTOM) {
            this.loadingGravity = loadingGravity;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LoadingConfig config = (LoadingConfig) o;
        return withMaskLayer == config.withMaskLayer &&
                loadingGravity == config.loadingGravity &&
                startDelay == config.startDelay &&
                renderType == config.renderType &&
                equals(renderParams, config.renderParams);
    }

    private static boolean equals(Object a, Object b) {
        return (a == b) || (a != null && a.equals(b));
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(new Object[]{renderType, renderParams, withMaskLayer, loadingGravity, startDelay});
    }
}
