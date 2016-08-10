package com.sevenheaven.uilibrary.views;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

/**
 * A ImageView that animated scaleType change
 *
 * Created by 7heaven on 16/7/30.
 */
public class AnimatedImageView extends ImageView {

    private Matrix mProgressiveMatrix;
    private float[] mInitialMatrixValues;
    private float[] mProgressingMatrixValues;
    private float[] mTargetMatrixValues;

    private ValueAnimator mAnimator;

    private boolean mAnimationMark = false;
    private boolean mIgnoreMatrixAnimation = true;

    public AnimatedImageView(Context context){
        this(context, null);
    }

    public AnimatedImageView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public AnimatedImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        Matrix targetMatrix = getImageMatrix();
        mProgressiveMatrix = new Matrix(targetMatrix);
        mInitialMatrixValues = new float[9];
        mProgressingMatrixValues = new float[9];
        mTargetMatrixValues = new float[9];

        mAnimator = ValueAnimator.ofFloat(0, 1);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new DecelerateInterpolator());
        mAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                float value = (float) valueAnimator.getAnimatedValue();
                updateProgressiveMatrix(value);
            }
        });
    }

    @Override
    public void setScaleType(ScaleType scaleType){
        super.setScaleType(scaleType);

        mIgnoreMatrixAnimation = true;
    }

    /**
     * setScaleType with animated transformation
     * @param scaleType
     */
    public void setScaleTypeAnimated(ScaleType scaleType){
        super.setScaleType(scaleType);

        mIgnoreMatrixAnimation = false;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom){
        Matrix targetMatrix = getImageMatrix();

        if(targetMatrix != null){
            if(!targetMatrix.equals(mProgressiveMatrix)){
                if(mIgnoreMatrixAnimation){
                    mProgressiveMatrix = new Matrix(targetMatrix);
                }else{
                    mAnimationMark = true;
                }
            }
        }
    }

    private void startProgressiveMatrixTransform(){
        if(mAnimator.isRunning()) mAnimator.end();

        Matrix targetMatrix = getImageMatrix();
        if(targetMatrix != null){
            targetMatrix.getValues(mTargetMatrixValues);
        }

        if(mProgressiveMatrix != null){
            mProgressiveMatrix.getValues(mInitialMatrixValues);
        }

        for(int i = 0; i < 9; i++){
            mProgressingMatrixValues[i] = 0;
        }

        mAnimator.start();
    }

    private void updateProgressiveMatrix(float progress){
        for(int i = 0; i < 9; i++){
            float dValue = mTargetMatrixValues[i] - mInitialMatrixValues[i];
            mProgressingMatrixValues[i] = dValue * progress + mInitialMatrixValues[i];
        }

        mProgressiveMatrix.setValues(mProgressingMatrixValues);

        invalidate();
    }

    @Override
    public void onDraw(Canvas canvas){
        if(mAnimationMark){
            startProgressiveMatrixTransform();
            mAnimationMark = false;
        }

        Drawable drawable = getDrawable();
        if(drawable != null){
            int saveCount = canvas.getSaveCount();
            canvas.save();
            boolean cropToPadding = Build.VERSION.SDK_INT > 16 && getCropToPadding();
            if(cropToPadding){
                final int scrollX = getScrollX();
                final int scrollY = getScrollY();

                canvas.clipRect(scrollX + getPaddingLeft(), scrollY + getPaddingTop(),
                        scrollX + getRight() - getLeft() - getPaddingRight(),
                        scrollY + getBottom() - getTop() - getPaddingBottom());
            }

            canvas.translate(getPaddingLeft(), getPaddingTop());

            if(mProgressiveMatrix != null){
                canvas.concat(mProgressiveMatrix);
            }

            drawable.draw(canvas);
            canvas.restoreToCount(saveCount);
        }
    }

}
