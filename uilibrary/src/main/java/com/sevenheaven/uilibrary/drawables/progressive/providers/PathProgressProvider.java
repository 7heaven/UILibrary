package com.sevenheaven.uilibrary.drawables.progressive.providers;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;

import com.sevenheaven.uilibrary.drawables.progressive.ProgressiveDrawable;
import com.sevenheaven.uilibrary.utils.PathMeasurement;

/**
 * Created by 7heaven on 16/8/8.
 */
public class PathProgressProvider extends ProgressiveDrawable.DrawContentProvider {

    private Path[] mDrawingProgressPaths;
    private Path[] mDrawingAnimationPaths;

    private PathMeasurement mProgressPathMeasurement;
    private PathMeasurement mAnimationPathMeasurement;

    private Paint mPaint;

    /**
     * path description
     */
    public static class PathDesc{
        Path mPath;

        int mGravity;

        RectF mBounds;

        boolean mKeepAspect = true;
        boolean mScaleFollowBounds = true;

        public PathDesc(Path path, int gravity){
            mPath = path;
            mBounds = new RectF();

            mGravity = gravity;

            computeBounds();
        }

        void computeBounds(){
            mPath.computeBounds(mBounds, false);
        }
    }

    private PathDesc mProgressPathDesc;
    private PathDesc mAnimationPathDesc;

    public PathProgressProvider(PathDesc progressPathDesc, PathDesc animationPathDesc){
        mProgressPathDesc = progressPathDesc;
        mAnimationPathDesc = animationPathDesc;

        if(mProgressPathDesc != null) mProgressPathMeasurement = new PathMeasurement(mProgressPathDesc.mPath);
        if(mAnimationPathDesc != null) mAnimationPathMeasurement = new PathMeasurement(mAnimationPathDesc.mPath);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeWidth(30);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void transformPath(PathDesc pathDesc, Rect targetBounds){
        if((pathDesc.mGravity & Gravity.CENTER) > 0){

        }
    }

    @Override
    protected void onBoundsChange(Rect bounds){
        if(mAnimationPathDesc != null) transformPath(mAnimationPathDesc, bounds);
        if(mProgressPathDesc != null) transformPath(mProgressPathDesc, bounds);
    }

    /**
     * call when path's position changed
     * @param distance
     * @param pos
     * @param tan
     */
    protected void onPathPositionUpdate(Path invokedPath, float distance, float[] pos, float[] tan){}

    protected void updateProgressPaint(Paint paint){
        paint.setColor(0xFF0099CC);
    }
    protected void updateAnimationPaint(Paint paint){
        paint.setColor(0xFFCC9900);
    }

    @Override
    protected void animationUpdate(@ProgressiveDrawable.AnimationType int type, float value){
        if(mAnimationPathMeasurement != null){
            switch(type){
                case ProgressiveDrawable.AnimationType.IDLE:
                case ProgressiveDrawable.AnimationType.SHOW_ANIMATION:
                    mDrawingAnimationPaths = mAnimationPathMeasurement.updatePhare(value);
                    break;
                case ProgressiveDrawable.AnimationType.DISMISS_ANIMATION:
                    mDrawingAnimationPaths = mAnimationPathMeasurement.updatePhare(1 - value);
                    break;
            }

            invalidateContent();
        }
    }

    @Override
    protected void drawProgress(Canvas canvas, float progress){
        if(mDrawingAnimationPaths != null){
            updateAnimationPaint(mPaint);
            for(int i = 0; i < mDrawingAnimationPaths.length; i++){
                Path path = mDrawingAnimationPaths[i];
                if(path != null) canvas.drawPath(path, mPaint);
            }
        }

        if(mProgressPathMeasurement != null){

            mDrawingProgressPaths = mProgressPathMeasurement.updatePhare(progress);

            if(mDrawingProgressPaths != null){
                updateProgressPaint(mPaint);
                for(int i = 0; i < mDrawingProgressPaths.length; i++){
                    Path path = mDrawingProgressPaths[i];
                    if(path != null) canvas.drawPath(path, mPaint);
                }
            }
        }
    }
}
