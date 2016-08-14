package com.sevenheaven.uilibrary.views;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntDef;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 7heaven on 16/8/13.
 */
public class GestureImageView extends View {

    private Drawable mSourceDrawable;
    private Matrix mDrawingMatrix;
    private Matrix mLastMatrix;
    private float[] mLastValues;

    private static final int MAX_POINTER_ACCEPTED = 2;
    private int[] mCachedPointersId;
    private MotionEvent.PointerCoords[] mLastPointerCoords;

    @IntDef({GestureMode.MOVE, GestureMode.DOUBLE_TAP, GestureMode.SCALE, GestureMode.ROTATE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GestureMode{
        int MOVE       = 0x01;
        int DOUBLE_TAP = 0x02;
        int SCALE      = 0x04;
        int ROTATE     = 0x08;
    }

    public GestureImageView(Context context){
        this(context, null);
    }

    public GestureImageView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public GestureImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        mDrawingMatrix = new Matrix();
        mLastMatrix = new Matrix();
        mLastValues = new float[9];

        mCachedPointersId = new int[MAX_POINTER_ACCEPTED];
        mLastPointerCoords = new MotionEvent.PointerCoords[MAX_POINTER_ACCEPTED];
        for(int i = 0; i < MAX_POINTER_ACCEPTED; i++){
            mCachedPointersId[i] = -1;
            mLastPointerCoords[i] = new MotionEvent.PointerCoords();
        }
    }

    public void setImageDrawable(Drawable drawable){
        mSourceDrawable = drawable;

        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        final int pointerCount = event.getPointerCount();

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                cachePointersId(event);

                final int minPointerCount = pointerCount > MAX_POINTER_ACCEPTED ? MAX_POINTER_ACCEPTED : pointerCount;
                for(int i = 0; i < minPointerCount; i++){
                    MotionEvent.PointerCoords coords = mLastPointerCoords[i];
                    coords.clear();

                    event.getPointerCoords(i, coords);
                }

                break;
            case MotionEvent.ACTION_MOVE:
                mDrawingMatrix.set(mLastMatrix);
                mLastMatrix.getValues(mLastValues);
                if(pointerCount == 1){
                    float dx = event.getX() - mLastPointerCoords[0].x;
                    float dy = event.getY() - mLastPointerCoords[0].y;

                    mLastValues[2] += dx;
                    mLastValues[5] += dy;

                    mDrawingMatrix.setValues(mLastValues);

                }else if(pointerCount == 2){
                    float lastMiddleX = mLastPointerCoords[0].x + (mLastPointerCoords[1].x - mLastPointerCoords[0].x) / 2;
                    float lastMiddleY = mLastPointerCoords[0].y + (mLastPointerCoords[1].y - mLastPointerCoords[0].y) / 2;
                    float newMiddleX = event.getX(0) + (event.getX(1) - event.getX(0)) / 2;
                    float newMiddleY = event.getY(0) + (event.getY(1) - event.getY(0)) / 2;

                    float dx = newMiddleX - lastMiddleX;
                    float dy = newMiddleY - lastMiddleY;

                    float nDx = event.getX(1) - event.getX(0);
                    float nDy = event.getY(1) - event.getY(0);
                    float oDx = mLastPointerCoords[1].x - mLastPointerCoords[0].x;
                    float oDy = mLastPointerCoords[1].y - mLastPointerCoords[0].y;

                    double oldDistance = Math.sqrt(oDx * oDx + oDy * oDy);
                    double newDistance = Math.sqrt(nDx * nDx + nDy * nDy);

                    float scale = (float) (newDistance / oldDistance);

                    float xOffset = (lastMiddleX - mLastValues[2]) * (scale - 1);
                    float yOffset = (lastMiddleY - mLastValues[4]) * (scale - 1);

                    mLastValues[0] = scale * mLastValues[0];
                    mLastValues[4] = scale * mLastValues[4];
                    mLastValues[2] += dx - xOffset;
                    mLastValues[5] += dy - yOffset;

                    mDrawingMatrix.setValues(mLastValues);
                }

                Log.d("position", "x:" + mLastValues[2] + ",y:" + mLastValues[5]);


                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                mLastMatrix.set(mDrawingMatrix);
                cachePointersId(event);
                break;
            case MotionEvent.ACTION_CANCEL:
                cachePointersId(event);
                break;
        }

        invalidate();

        return true;
    }

    private void cachePointersId(MotionEvent event){
        final int pointerCount = event.getPointerCount();
        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                for(int i = 0; i < MAX_POINTER_ACCEPTED; i++){
                    int cachedId = mCachedPointersId[i];
                    boolean idExists = event.findPointerIndex(cachedId) != -1;
                }
                break;
            case MotionEvent.ACTION_CANCEL:
                for(int i = 0; i < MAX_POINTER_ACCEPTED; i++){
                    mCachedPointersId[i] = -1;
                }
                break;
        }

    }

    @Override
    public void onDraw(Canvas canvas){
        if(mSourceDrawable != null){
            final int savedCount = canvas.save();
            canvas.setMatrix(mDrawingMatrix);

            mSourceDrawable.draw(canvas);

            canvas.restoreToCount(savedCount);
        }
    }
}
