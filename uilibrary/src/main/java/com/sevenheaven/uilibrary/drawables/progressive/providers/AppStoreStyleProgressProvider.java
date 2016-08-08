package com.sevenheaven.uilibrary.drawables.progressive.providers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.FloatRange;

import com.sevenheaven.uilibrary.drawables.progressive.ProgressiveDrawable;

/**
 * Created by 7heaven on 16/8/8.
 */
public class AppStoreStyleProgressProvider extends ProgressiveDrawable.DrawContentProvider {

    private Path path = new Path();
    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private RectF mOval = new RectF();
    private RectF mOuterBound = new RectF();

    private int mRadius;
    private int mProgressRadiusToFit;
    private int mFitRadius;

    private int mCenterX;
    private int mCenterY;

    private int mColor;
    private int mAlpha;

    public AppStoreStyleProgressProvider(int color){
        mColor = color;
        mAlpha = (color & 0xFF000000) >>> 24;
        mPaint.setColor(mColor);
    }

    @Override
    protected void onBoundsChange(Rect bounds){
        mCenterX = bounds.width() / 2;
        mCenterY = bounds.height() / 2;

        int size = mCenterX > mCenterY ? mCenterY : mCenterX;

        mFitRadius = (int) (size * 0.8F);

        //calculate the distance between outer radius and inner progress circle radius
        mProgressRadiusToFit = (int) (mFitRadius * 0.15F);

        mOuterBound.left = 0;
        mOuterBound.top = 0;
        mOuterBound.right = bounds.width();
        mOuterBound.bottom = bounds.height();
    }

    private void updateRadius(int radius){
        mRadius = radius;

        int progressRadius = mRadius < mProgressRadiusToFit ? 0 : mRadius - mProgressRadiusToFit;

        mOval.left = mCenterX - progressRadius;
        mOval.right = mCenterX + progressRadius;
        mOval.top = mCenterY - progressRadius;
        mOval.bottom = mCenterY + progressRadius;

        invalidateContent();
    }

    @Override
    protected void animationUpdate(@ProgressiveDrawable.AnimationType int type, @FloatRange(from=0.0F, to=1.0F) float value){
        switch(type){
            case ProgressiveDrawable.AnimationType.SHOW_ANIMATION:
            case ProgressiveDrawable.AnimationType.IDLE:
                int realAlpha = (int) (value * mAlpha);
                mPaint.setColor((realAlpha << 24) | (mColor & 0xFFFFFF));

                updateRadius((int) (value * mFitRadius));
                break;
            case ProgressiveDrawable.AnimationType.DISMISS_ANIMATION:
                updateRadius((int) (((value * 1.5) + 1) * mFitRadius));
                break;
        }
    }

    @Override
    protected void drawProgress(Canvas canvas, @FloatRange(from=0.0F, to=1.0F) float progress){
        path.reset();

        //add outer rect
        path.addRect(mOuterBound, Path.Direction.CCW);

        //punch a hole
        path.addCircle(mCenterX, mCenterY, mRadius, Path.Direction.CW);

        canvas.drawPath(path, mPaint);

        if(progress >= 0){
            float angle = (1.0F - progress) * 360;
            float startAngle = -90 + (360 - angle);
            canvas.drawArc(mOval, startAngle, angle, true, mPaint);
        }
    }
}
