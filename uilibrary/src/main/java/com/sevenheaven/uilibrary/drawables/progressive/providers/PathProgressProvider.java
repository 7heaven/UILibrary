package com.sevenheaven.uilibrary.drawables.progressive.providers;

import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.Gravity;

import com.sevenheaven.uilibrary.drawables.progressive.ProgressiveDrawable;
import com.sevenheaven.uilibrary.utils.GeomUtils;
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

        final int mGravity;

        RectF mBounds;
        Rect mBoundsInt;

        boolean mSubPathsProgressiveAsync;
        final boolean mKeepAspect;
        final boolean mScaleForBounds;

        private PathMeasurement mAssociatePathMeasurement;

        public PathDesc(Path path, int gravity, boolean subPathsProgressiveAsync){
            this(path, gravity, subPathsProgressiveAsync, true, true);
        }

        public PathDesc(Path path, int gravity, boolean subPathsProgressiveAsync, boolean keepAspect, boolean scaleForBounds){
            mPath = path;
            mBounds = new RectF();
            mBoundsInt = new Rect();

            mGravity = gravity;
            mKeepAspect = keepAspect;
            mScaleForBounds = scaleForBounds;
            mSubPathsProgressiveAsync = subPathsProgressiveAsync;

            computeBounds();
        }

        void computeBounds(){
            mPath.computeBounds(mBounds, false);
            mBoundsInt.left = (int) mBounds.left;
            mBoundsInt.top = (int) mBounds.top;
            mBoundsInt.right = (int) mBounds.right;
            mBoundsInt.bottom = (int) mBounds.bottom;
        }
    }

    private PathDesc mProgressPathDesc;
    private PathDesc mAnimationPathDesc;

    private Rect pathDescOutRect;

    public PathProgressProvider(PathDesc progressPathDesc, PathDesc animationPathDesc){
        mProgressPathDesc = progressPathDesc;
        mAnimationPathDesc = animationPathDesc;

        if(mProgressPathDesc != null){
            mProgressPathMeasurement = new PathMeasurement(mProgressPathDesc.mPath);
            mProgressPathDesc.mAssociatePathMeasurement = mProgressPathMeasurement;
        }
        if(mAnimationPathDesc != null){
            mAnimationPathMeasurement = new PathMeasurement(mAnimationPathDesc.mPath);
            mAnimationPathDesc.mAssociatePathMeasurement = mAnimationPathMeasurement;
        }

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    //TODO check if correct
    private void transformPath(PathDesc pathDesc, Rect targetBounds){
        pathDesc.computeBounds();

        float scaleX = 1;
        float scaleY = 1;

        if(pathDesc.mScaleForBounds){
            if(pathDesc.mKeepAspect){
                float oRatio = pathDesc.mBounds.width() / pathDesc.mBounds.height();
                float tRatio = (float) targetBounds.width() / (float) targetBounds.height();

                if(oRatio > tRatio){
                    float scale = (float) targetBounds.width() / pathDesc.mBounds.width();

                    scaleX = scale;
                    scaleY = scale;
                }else{
                    float scale = (float) targetBounds.height() / pathDesc.mBounds.height();

                    scaleX = scale;
                    scaleY = scale;
                }
            }else{
                scaleX = (float) targetBounds.width() / pathDesc.mBounds.width();
                scaleY = (float) targetBounds.height() / pathDesc.mBounds.height();
            }
        }

        int w = (int) (pathDesc.mBounds.width() * scaleX);
        int h = (int) (pathDesc.mBounds.height() * scaleY);

        if(pathDescOutRect == null) pathDescOutRect = new Rect();

        Gravity.apply(pathDesc.mGravity, w, h, targetBounds, pathDescOutRect);

        Matrix transformMatrix = new Matrix();
        GeomUtils.getTransformationMatrix(pathDesc.mBoundsInt, pathDescOutRect, transformMatrix);
        pathDesc.mPath.transform(transformMatrix);

        pathDesc.computeBounds();

        if(pathDesc.mAssociatePathMeasurement != null){
            pathDesc.mAssociatePathMeasurement.setPath(pathDesc.mPath);
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
                    mDrawingAnimationPaths = mAnimationPathMeasurement.updatePhare(value, mAnimationPathDesc.mSubPathsProgressiveAsync);
                    break;
                case ProgressiveDrawable.AnimationType.DISMISS_ANIMATION:
                    mDrawingAnimationPaths = mAnimationPathMeasurement.updatePhare(1 - value, mAnimationPathDesc.mSubPathsProgressiveAsync);
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

            mDrawingProgressPaths = mProgressPathMeasurement.updatePhare(progress, mProgressPathDesc.mSubPathsProgressiveAsync);

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
