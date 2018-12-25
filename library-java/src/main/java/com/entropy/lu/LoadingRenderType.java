package com.entropy.lu;

/**
 *
 * <p>
 * @author kevinet created at 2018/10/19
 */
public enum LoadingRenderType {
    /**
     * 使用动画方案渲染
     */
    ANIMATION,
    /**
     * 传统ProgressBar实现方案
     */
    PROGRESS,
    /**
     * 使用Lottie动画库
     */
    LOTTIE
}