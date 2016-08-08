package com.sevenheaven.uilibrary.drawables.progressive;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 7heaven on 16/8/5.
 */
public class ProgressiveDrawable extends Drawable{

    private float mProgress = 0;
    private float mAnimatedValue = 0;

    ValueAnimator mAnimator;
    private long mShowDuration = -1;
    private static final long DEFAULT_ANIMATION_DURATION = 400L;
    private long mDismissDuration = -1;
    private Interpolator mShowInterpolator;
    private static final Interpolator DEFAULT_ANIMATION_INTERPOLATOR = new DecelerateInterpolator();
    private Interpolator mDismissInterpolator;
    @AnimationType int mCurrentAnimationType;

    private DrawContentProvider mDrawContentProvider;

    /**
     * class for provide progressive and animation content change
     */
    public static abstract class DrawContentProvider{

        private ProgressiveDrawable mHost;

        /**
         * call when the host's bound changed
         * @param bounds
         */
        protected abstract void onBoundsChange(Rect bounds);

        /**
         * draw progressive content and animatable content if need.
         * @param canvas the canvas which the content should be draw on.
         * @param progress current progress
         */
        protected abstract void drawProgress(Canvas canvas, @FloatRange(from=0.0F, to=1.0F) float progress);

        /**
         * update the value for animation
         *
         * @param type AnimationType
         * @param value animated value
         */
        protected abstract void animationUpdate(@AnimationType int type, @FloatRange(from=0.0F, to=1.0F) float value);

        final protected void invalidateContent(){
            if(mHost != null){
                mHost.invalidateSelf();
            }
        }

        void setHost(ProgressiveDrawable hostDrawable){
            if(hostDrawable == null){
                mHost = null;
            }else{
                mHost = hostDrawable;
            }
        }
    }

    @IntDef({AnimationType.SHOW_ANIMATION, AnimationType.DISMISS_ANIMATION, AnimationType.IDLE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface AnimationType{
        int SHOW_ANIMATION = 0;
        int DISMISS_ANIMATION = 1;
        int IDLE = 2;
    }

    public ProgressiveDrawable(@NonNull DrawContentProvider contentProvider){
        if(contentProvider == null) throw new IllegalArgumentException("content provider must not be null!");

        mDrawContentProvider = contentProvider;
        mDrawContentProvider.setHost(this);

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(DEFAULT_ANIMATION_DURATION);
        mAnimator.setInterpolator(DEFAULT_ANIMATION_INTERPOLATOR);
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatedValue = (float) valueAnimator.getAnimatedValue();

                mDrawContentProvider.animationUpdate(mCurrentAnimationType, mAnimatedValue);
            }
        });
        mAnimator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {

            }

            @Override
            public void onAnimationEnd(Animator animator) {
                mCurrentAnimationType = AnimationType.IDLE;
                if(mProgress > 0 && mProgress < 1){
                    mAnimatedValue = 1;
                    mDrawContentProvider.animationUpdate(mCurrentAnimationType, mAnimatedValue);
                }
            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    /**
     * set the interpolator for progress bar init animation
     * @param interpolator
     */
    public void setShowAnimationInterpolator(Interpolator interpolator){
        mShowInterpolator = interpolator;
    }

    /**
     * set the interpolator for progress bar dismiss animation
     * @param interpolator
     */
    public void setDismissAnimationInterpolator(Interpolator interpolator){
        mDismissInterpolator = interpolator;
    }

    /**
     * set the duration for progress bar init animation
     * @param duration
     */
    public void setShowAnimationDuration(long duration){
        mShowDuration = duration;
    }

    /**
     * set the duration for progress bar dismiss duration
     * @param duration
     */
    public void setDismissAnimationDuration(long duration){
        mDismissDuration = duration;
    }

    /**
     * set the progress
     * @param progress
     */
    public void setProgress(float progress){
        if(progress == 0){
            performStartAnimation();
        }else if(progress == -1){
            performEndAnimation();
        }else if(mAnimatedValue != 1 && !mAnimator.isRunning()){
            mAnimatedValue = 1;
            mDrawContentProvider.animationUpdate(AnimationType.IDLE, mAnimatedValue);
        }

        mProgress = progress;

        invalidateSelf();
    }

    void performStartAnimation(){
        if(mAnimator.isRunning()){
            mAnimator.end();
            mAnimator.cancel();
        }

        if(mShowInterpolator != null) mAnimator.setInterpolator(mShowInterpolator);
        if(mShowDuration != -1) mAnimator.setDuration(mShowDuration);
        mCurrentAnimationType = AnimationType.SHOW_ANIMATION;
        mAnimator.start();
    }

    void performEndAnimation(){
        if(mAnimator.isRunning()){
            mAnimator.end();
            mAnimator.cancel();
        }
        if(mDismissInterpolator != null) mAnimator.setInterpolator(mDismissInterpolator);
        if(mDismissDuration != -1) mAnimator.setDuration(mDismissDuration);
        mCurrentAnimationType = AnimationType.DISMISS_ANIMATION;
        mAnimator.start();
    }

    @Override
    public void setAlpha(int alpha){

    }

    @Override
    public void setColorFilter(ColorFilter filter){

    }

    @Override
    public int getOpacity(){
        return PixelFormat.TRANSPARENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds){
        mDrawContentProvider.onBoundsChange(bounds);
    }

    @Override
    public void draw(Canvas canvas){
        mDrawContentProvider.drawProgress(canvas, mProgress);
    }

}
