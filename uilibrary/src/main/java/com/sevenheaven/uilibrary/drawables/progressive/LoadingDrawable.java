package com.sevenheaven.uilibrary.drawables.progressive;

import android.animation.Animator;
import android.animation.ValueAnimator;

/**
 * Created by 7heaven on 16/8/8.
 */
public abstract class LoadingDrawable extends ProgressiveDrawable {

    private Animator.AnimatorListener startAnimationListener = new Animator.AnimatorListener(){
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
            performProgressiveAnimation();

            mAnimator.removeListener(this);
        }

        @Override
        public void onAnimationCancel(Animator animator) {

        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private Animator.AnimatorListener progressiveAnimationListener = new Animator.AnimatorListener(){
        @Override
        public void onAnimationStart(Animator animator) {

        }

        @Override
        public void onAnimationEnd(Animator animator) {
        }

        @Override
        public void onAnimationCancel(Animator animator) {
        }

        @Override
        public void onAnimationRepeat(Animator animator) {

        }
    };

    private ValueAnimator mProgressiveAnimator;

    public LoadingDrawable(DrawContentProvider contentProvider){
        super(contentProvider);
    }

    public void setInvertLoadingAnimation(boolean invert){

    }

    public void startLoading(){
        mAnimator.addListener(startAnimationListener);
        performStartAnimation();
    }

    public void stopLoading(){
        mProgressiveAnimator.end();

        performEndAnimation();
    }

    private void performProgressiveAnimation(){

    }
}
