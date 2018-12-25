package com.entropy.lu;

/**
 * 具有Loading能力
 *
 * @author kevinet created at 2018/11/01
 */
public interface ILoadingable {
    /**
     * 展示Loading
     */
    void showLoading();

    /**
     * 展示Loading
     */
    void showLoading(LoadingConfig config);

    /**
     * 隐藏Loading
     */
    void hideLoading();

    /**
     * 是否正在Loading
     *
     * @return loading状态 true:loading中 false:未loading
     */
    boolean isLoading();

}
