package com.entropy.lu.render;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.View;
import com.airbnb.lottie.LottieAnimationView;
import com.airbnb.lottie.LottieComposition;
import com.airbnb.lottie.LottieDrawable;
import com.airbnb.lottie.LottieOnCompositionLoadedListener;

import java.util.ArrayList;

/**
 * 支持Lottie功能的loading渲染器
 * <p>
 * @author kevinet created at 2018/10/23
 */
public class LottieLoadingRender extends BaseLoadingRender {

    public static final String kAnimatorAssetFileName = "Loading::Animation::Lottie::Asset::File::Name";
    public static final String kAnimatorAssetImagesPath = "Loading::Animation::Lottie::Asset::Images::Path";
    public static final String kAnimatorLoopRanges = "Loading::Animation::Lottie::LoopRanges";

    private LottieAnimationView mLottieAnimationView;
    private int mRepeatCount = 0;
    private int mMaxFrame = 0;
    private int mMinFrame = 0;
    private ArrayList<LoopRange> loopRanges;

    private void updateFrame(ArrayList<LoopRange> loopRanges) {
        LoopRange loopRange = null;
        if (loopRanges != null) {
            for (LoopRange it : loopRanges) {
                if (it.loopIndex != null && it.loopIndex[0] <= mRepeatCount && it.loopIndex[1] >= mRepeatCount) {
                    loopRange = it;
                }
            }
        }

        if (loopRange != null) {
            mLottieAnimationView.setMinAndMaxFrame(Math.max(loopRange.minFrame, mMinFrame), Math.min(loopRange.maxFrame, mMaxFrame));
        } else if (mLottieAnimationView.getMinFrame() != mMinFrame || mLottieAnimationView.getMaxFrame() != mMaxFrame) {
            mLottieAnimationView.setMinAndMaxFrame(mMinFrame, mMaxFrame);
        }
        mRepeatCount++;
    }

    @Override
    View onCreateView(Context context, Bundle params) {
        mLottieAnimationView = new LottieAnimationView(context);
        mLottieAnimationView.setImageAssetsFolder(params.getString(kAnimatorAssetImagesPath, ""));
        mLottieAnimationView.setRepeatCount(LottieDrawable.INFINITE);
        mLottieAnimationView.setRepeatMode(LottieDrawable.RESTART);
        mLottieAnimationView.setBackgroundColor(params.getInt(kBackgroundColor, 0xffffffff));
        String assetFileName = params.getString(kAnimatorAssetFileName, null);
        if (assetFileName != null) {
            mLottieAnimationView.setAnimation(assetFileName);
            mLottieAnimationView.addLottieOnCompositionLoadedListener(new LottieOnCompositionLoadedListener() {
                @Override
                public void onCompositionLoaded(LottieComposition composition) {
                    mMinFrame = (int) mLottieAnimationView.getMinFrame();
                    mMaxFrame = (int) mLottieAnimationView.getMaxFrame();
                }
            });
        }
        loopRanges = params.getParcelableArrayList(kAnimatorLoopRanges);
        return mLottieAnimationView;
    }

    @Override
    public Rect getBorder() {
        LottieComposition composition = mLottieAnimationView.getComposition();
        if (composition != null) {
            return mLottieAnimationView.getComposition().getBounds();
        } else {
            return super.getBorder();
        }
    }

    @Override
    void onStartLoading() {
        if (loopRanges != null) {
            mRepeatCount = 0;
            updateFrame(loopRanges);
            mLottieAnimationView.addAnimatorListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationRepeat(Animator animation) {
                    super.onAnimationRepeat(animation);
                    updateFrame(loopRanges);
                }

            });
        }
        mLottieAnimationView.playAnimation();
    }

    @Override
    void onStopLoading() {
        mLottieAnimationView.setMinAndMaxFrame(mMinFrame, mMaxFrame);
        mLottieAnimationView.removeAllAnimatorListeners();
        mLottieAnimationView.cancelAnimation();
    }

    @Override
    public boolean isRunning() {
        return mLottieAnimationView.isAnimating();
    }


    public static class LoopRange implements Parcelable {
        int minFrame = 0;
        int maxFrame = 0;
        int[] loopIndex;

        public LoopRange(int minFrame, int maxFrame, int[] loopIndex) {
            this.minFrame = minFrame;
            this.maxFrame = maxFrame;
            this.loopIndex = loopIndex;
        }

        public LoopRange(Parcel parcel) {
            minFrame = parcel.readInt();
            maxFrame = parcel.readInt();
            loopIndex = parcel.createIntArray();
        }

        LoopRange setLoopRange(int[] index, int start, int end) {
            loopIndex = index;
            minFrame = start;
            maxFrame = end;
            return this;
        }

        public void writeToParcel(Parcel parcel, int flags) {
            parcel.writeInt(minFrame);
            parcel.writeInt(maxFrame);
            parcel.writeIntArray(loopIndex);
        }

        public int describeContents() {
            return 0;
        }

        public final Parcelable.Creator<LoopRange> CREATOR = new Parcelable.Creator<LoopRange>() {
            @Override
            public LoopRange createFromParcel(Parcel source) {
                return new LoopRange(source);
            }

            @Override
            public LoopRange[] newArray(int size) {
                return new LoopRange[size];
            }
        };
    }

}