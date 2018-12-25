package com.entropy.lu.render;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import com.entropy.lu.loading.R;

/**
 * <p>
 *     通用ProgressBar样式Loading
 * @author kevinet created at 2018/10/18
 */
public class ProgressBarLoadingRender extends BaseLoadingRender {

    public static String kIndeterminateDrawableId = "Loading::ProgressBar::Indeterminate::Drawable::Id";

    private ProgressBar mProgressBar;

    @Override
    View onCreateView(Context context, Bundle params) {
        mProgressBar = (ProgressBar) LayoutInflater.from(context).inflate(R.layout.loading_progressbar, null);
        int indeterminateDrawableId = params.getInt(kIndeterminateDrawableId);
        if (indeterminateDrawableId != 0) {
            mProgressBar.setIndeterminateDrawable(context.getResources().getDrawable(indeterminateDrawableId));
        }

        if (params.getInt(kInterpolatorId) != 0) {
            mProgressBar.setInterpolator(context, params.getInt(kInterpolatorId));
        }

        mProgressBar.setBackgroundColor(params.getInt(kBackgroundColor, 0));

        return mProgressBar;
    }

    @Override
    void onStartLoading() {
        mProgressBar.setVisibility(View.VISIBLE);
    }

    @Override
    void onStopLoading() {
        mProgressBar.setVisibility(View.GONE);
    }

    @Override
    public boolean isRunning() {
        return mProgressBar.isIndeterminate() && mProgressBar.getWindowVisibility() == View.VISIBLE && mProgressBar.isShown();
    }
}