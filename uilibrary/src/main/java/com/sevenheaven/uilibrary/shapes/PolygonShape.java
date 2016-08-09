package com.sevenheaven.uilibrary.shapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;

import com.sevenheaven.uilibrary.utils.GeomUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Shape for create a regular polygon or star with round corner
 *
 * Created by 7heaven on 16/4/25.
 */
public class PolygonShape extends Shape {

    private int mVertexCount;
    private float mCornerRadius;
    private boolean mAsStar;

    private List<PointF> mControlPoints;

    private float mStarRatio = 0.65F;
    private static final double startAngle = -(Math.PI / 2);

    private Path starPath;

    public PolygonShape(int vertexCount, float cornerRadius){
        this(vertexCount, cornerRadius, false, -1);
    }

    public PolygonShape(int vertexCount, float cornerRadius, boolean star){
        this(vertexCount, cornerRadius, star, -1);
    }

    public PolygonShape(@IntRange(from=0, to=Integer.MAX_VALUE) int vertexCount, @FloatRange(from=0, to=0.5) float cornerRadius, boolean star, float starRatio){
        if(cornerRadius > 0.5F || cornerRadius < 0) throw new IllegalArgumentException("corners range is (0 - 0.5)");
        this.mVertexCount = vertexCount;
        this.mCornerRadius = cornerRadius;
        this.mAsStar = star;
        this.mStarRatio = starRatio == -1 ? this.mStarRatio : starRatio;

        starPath = new Path();

        mControlPoints = new ArrayList<>();

    }

    public void setVertexCount(int vertexCount){
        if(this.mVertexCount != vertexCount){
            this.mVertexCount = vertexCount;

            calculateOutline();
        }
    }

    public void setCornerRadius(@FloatRange(from=0, to=0.5) float cornerRadius){
        if(this.mCornerRadius != cornerRadius){
            this.mCornerRadius = cornerRadius;

            calculateOutline();
        }
    }

    public void setAsStar(boolean asStar){
        if(this.mAsStar != asStar){
            this.mAsStar = asStar;

            calculateOutline();
        }
    }

    @Override
    public void onResize(float width, float height){
        calculateOutline();
    }


    @Override
    public void draw(Canvas canvas, Paint paint){

        if(canvas != null) canvas.drawPath(starPath, paint);
    }

    private void calculateOutline(){
        starPath.reset();
        mControlPoints.clear();

        RectF bound = new RectF(0, 0, getWidth(), getHeight());
        int radius = (int) ((bound.width() > bound.height() ? bound.height() : bound.width()) * 0.5F);
        int centerX = (int) (bound.width() * 0.5F);
        int centerY = (int) (bound.height() * 0.5F);

        double angle = startAngle;
        double angleStep = Math.PI * 2 / mVertexCount;
        double halfStep = angleStep * 0.5F;
        float[] positions;

        //区分corner，corner为0时区分出来以避免不必要的曲线计算
        if(mCornerRadius == 0){
            positions = GeomUtils.pointOnCircumference(centerX, centerY, angle, radius);
            starPath.moveTo(positions[0], positions[1]);
            if(mAsStar){
                positions = GeomUtils.pointOnCircumference(centerX, centerY, angle + halfStep, radius * mStarRatio);
                starPath.lineTo(positions[0], positions[1]);
            }
            for(int i = 0; i < mVertexCount - 1; i++){
                angle += angleStep;
                positions = GeomUtils.pointOnCircumference(centerX, centerY, angle, radius);
                starPath.lineTo(positions[0], positions[1]);

                if(mAsStar){
                    positions = GeomUtils.pointOnCircumference(centerX, centerY, angle + halfStep, radius * mStarRatio);
                    starPath.lineTo(positions[0], positions[1]);
                }
            }

            starPath.close();
        }else{

            //
            //
            //    /\  <----    )  绘制圆角的多边形， 每个角包含一条曲线和一条直线，曲线用来实现圆角 直线用来连接下一个角，曲线的控制点为原角的顶点，
            //    \/          /    起点和终点分别是以corner为百分比取到得两边线段上的点（绘制星型多边形时 对 凹角做类似处理）
            //


            float[] startP = GeomUtils.pointOnCircumference(centerX, centerY, angle - (mAsStar ? halfStep : angleStep), mAsStar ? radius * mStarRatio : radius);
            float[] centerP = GeomUtils.pointOnCircumference(centerX, centerY, angle, radius);
            float[] endP = GeomUtils.pointOnCircumference(centerX, centerY, angle + (mAsStar ? halfStep : angleStep), mAsStar ? radius * mStarRatio : radius);

            float[] bezierStart = GeomUtils.segLine(centerP[0], centerP[1], startP[0], startP[1], this.mCornerRadius);
            float[] bezierEnd = GeomUtils.segLine(centerP[0], centerP[1], endP[0], endP[1], this.mCornerRadius);

            float[] nextStart = GeomUtils.segLine(endP[0], endP[1], centerP[0], centerP[1], this.mCornerRadius);

            starPath.moveTo(bezierStart[0], bezierStart[1]);
            starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
            starPath.lineTo(nextStart[0], nextStart[1]);

            int cpIndex = 0;

            mControlPoints.add(new PointF(bezierStart[0], bezierStart[1]));
            mControlPoints.add(new PointF(centerP[0], centerP[1]));
            mControlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));

            if(mAsStar){
                cpIndex += 3;
                centerP = endP.clone();
                endP = GeomUtils.pointOnCircumference(centerX, centerY, angle + angleStep, radius);

                bezierEnd = GeomUtils.segLine(centerP[0], centerP[1], endP[0], endP[1], this.mCornerRadius);

                mControlPoints.add(new PointF(nextStart[0], nextStart[1]));

                nextStart = GeomUtils.segLine(endP[0], endP[1], centerP[0], centerP[1], this.mCornerRadius);

                starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                starPath.lineTo(nextStart[0], nextStart[1]);

                mControlPoints.add(new PointF(centerP[0], centerP[1]));
                mControlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));
            }

            for(int i = 0; i < mVertexCount - 1; i++){
                cpIndex += 3;

                angle += angleStep;
                centerP = endP.clone();
                endP = GeomUtils.pointOnCircumference(centerX, centerY, angle + (mAsStar ? halfStep : angleStep), mAsStar ? radius * mStarRatio : radius);

                bezierEnd = GeomUtils.segLine(centerP[0], centerP[1], endP[0], endP[1], this.mCornerRadius);

                mControlPoints.add(new PointF(nextStart[0], nextStart[1]));

                nextStart = GeomUtils.segLine(endP[0], endP[1], centerP[0], centerP[1], this.mCornerRadius);

                starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                starPath.lineTo(nextStart[0], nextStart[1]);


                mControlPoints.add(new PointF(centerP[0], centerP[1]));
                mControlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));

                if(mAsStar){
                    cpIndex += 3;
                    centerP = endP;
                    endP = GeomUtils.pointOnCircumference(centerX, centerY, angle + angleStep, radius);

                    bezierEnd = GeomUtils.segLine(centerP[0], centerP[1], endP[0], endP[1], this.mCornerRadius);

                    mControlPoints.add(new PointF(nextStart[0], nextStart[1]));

                    nextStart = GeomUtils.segLine(endP[0], endP[1], centerP[0], centerP[1], this.mCornerRadius);

                    starPath.quadTo(centerP[0], centerP[1], bezierEnd[0], bezierEnd[1]);
                    starPath.lineTo(nextStart[0], nextStart[1]);

                    mControlPoints.add(new PointF(centerP[0], centerP[1]));
                    mControlPoints.add(new PointF(bezierEnd[0], bezierEnd[1]));
                }
            }

            starPath.close();
        }
    }

    public PointF[] getControlPoints(){
        return mControlPoints.toArray(new PointF[mControlPoints.size()]);
    }

    public Path getOutline(){
        return starPath;
    }
}
