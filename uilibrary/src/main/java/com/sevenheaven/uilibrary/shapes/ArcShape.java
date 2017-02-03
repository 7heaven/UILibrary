package com.sevenheaven.uilibrary.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;

import com.sevenheaven.uilibrary.utils.GeomUtil;

/**
 * Created by 7heaven on 2017/2/3.
 */

public class ArcShape extends Shape {

    private float mStartAngle;
    private float mEndAngle;

    private int mStrokeWidth;

    private Path mComputedPath;

    public ArcShape(float startAngle, float endAngle, int strokeWidth){
        mStartAngle = startAngle;
        mEndAngle = endAngle;

        mStrokeWidth = strokeWidth;

        recomputeShape();
    }

    public void setStartAngle(float startAngle){
        if(startAngle != mStartAngle){
            mStartAngle = startAngle;

            recomputeShape();
        }
    }

    public void setEndAngle(float endAngle){
        if(endAngle != mEndAngle){
            mEndAngle = endAngle;

            recomputeShape();
        }
    }

    public void setStrokeWidth(int strokeWidth){
        if(strokeWidth != mStrokeWidth){
            mStrokeWidth = strokeWidth;

            recomputeShape();
        }
    }

    private void recomputeShape(){
        float centerX = getWidth() / 2;
        float centerY = getHeight() / 2;

        int halfStrokeWidth = mStrokeWidth / 2;

        float radius = (centerX > centerY ? centerY : centerX) - halfStrokeWidth;

        float innerRadius = radius - halfStrokeWidth;
        float outerRadius = radius + halfStrokeWidth;

        if(mComputedPath == null) mComputedPath = new Path();
        mComputedPath.reset();

        RectF oval = new RectF();
        ovalSet(centerX, centerY, outerRadius, oval);

        mComputedPath.arcTo(oval, mStartAngle, mEndAngle - mStartAngle, true);

        float[] endPoint = GeomUtil.pointOnCircumference((int) centerX, (int) centerY, Math.toRadians(mEndAngle), radius);
        ovalSet(endPoint[0], endPoint[1], halfStrokeWidth, oval);

        mComputedPath.arcTo(oval, mEndAngle, 180, false);

        ovalSet(centerX, centerY, innerRadius, oval);

        mComputedPath.arcTo(oval, mEndAngle, mStartAngle - mEndAngle, false);

        float[] startPoint = GeomUtil.pointOnCircumference((int) centerX, (int) centerY, Math.toRadians(mStartAngle), radius);
        ovalSet(startPoint[0], startPoint[1], halfStrokeWidth, oval);

        mComputedPath.arcTo(oval, mStartAngle - 180, 180, false);

        mComputedPath.close();
    }

    private void ovalSet(float centerX, float centerY, float radius, RectF outOval){
        outOval.left = centerX - radius;
        outOval.top = centerY - radius;
        outOval.right = centerX + radius;
        outOval.bottom = centerY + radius;
    }

    @Override
    protected void onResize(float width, float height){

        recomputeShape();
    }

    @Override
    public void draw(Canvas canvas, Paint paint){
        if(mComputedPath != null) canvas.drawPath(mComputedPath, paint);
    }
}
