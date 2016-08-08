package com.sevenheaven.uilibrary.drawables;

import android.graphics.Rect;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by 7heaven on 16/8/8.
 */
public class RoundEdgeDrawable extends GradientDrawable {

    @IntDef({EdgeMark.TOP_LEFT, EdgeMark.TOP_RIGHT, EdgeMark.BOTTOM_RIGHT, EdgeMark.BOTTOM_LEFT})
    @Retention(RetentionPolicy.SOURCE)
    public @interface EdgeMark{
        int TOP_LEFT = 0x1;
        int TOP_RIGHT = 0x2;
        int BOTTOM_RIGHT = 0x4;
        int BOTTOM_LEFT = 0x8;
    }

    private int mEdgeMark;

    public RoundEdgeDrawable(){
        super();
        mEdgeMark = 0x0;
    }

    public RoundEdgeDrawable(GradientDrawable.Orientation orientation, int[] colors){
        super(orientation, colors);
        mEdgeMark = 0x0;
    }

    public RoundEdgeDrawable(GradientDrawable.Orientation orientation, int[] colors, @EdgeMark int edgeMark){
        super(orientation, colors);
        mEdgeMark = edgeMark;
    }

    public void setEdgeMark(@EdgeMark int edgeMark){
        if(mEdgeMark != edgeMark){
            mEdgeMark = edgeMark;
            recalculateRadius(getBounds());
        }
    }

    @Override
    public void setCornerRadius(float radius){

    }

    @Override
    public void setCornerRadii(float[] radii){

    }

    private void recalculateRadius(Rect bounds){
        int verticalHalf = bounds.height() / 2;
        int horizontalHalf = bounds.width() / 2;

        float calculatedRadius = verticalHalf > horizontalHalf ? horizontalHalf : verticalHalf;
        if(mEdgeMark == 0x0 || (mEdgeMark & (EdgeMark.TOP_LEFT | EdgeMark.TOP_RIGHT | EdgeMark.BOTTOM_LEFT | EdgeMark.BOTTOM_RIGHT)) > 0){
            super.setCornerRadius(calculatedRadius);
        }else{
            float topLeftRadius = 0;
            float topRightRadius = 0;
            float bottomRightRadius = 0;
            float bottomLeftRadius = 0;

            if((mEdgeMark & EdgeMark.TOP_LEFT) > 0){
                topLeftRadius = calculatedRadius;
            }

            if((mEdgeMark & EdgeMark.TOP_RIGHT) > 0){
                topRightRadius = calculatedRadius;
            }

            if((mEdgeMark & EdgeMark.BOTTOM_LEFT) > 0){
                bottomLeftRadius = calculatedRadius;
            }

            if((mEdgeMark & EdgeMark.BOTTOM_RIGHT) > 0){
                bottomRightRadius = calculatedRadius;
            }

            super.setCornerRadii(new float[]{topLeftRadius,
                    topLeftRadius,
                    topRightRadius,
                    topRightRadius,
                    bottomRightRadius,
                    bottomRightRadius,
                    bottomLeftRadius,
                    bottomLeftRadius});
        }
    }

    @Override
    protected void onBoundsChange(Rect bounds){

        recalculateRadius(bounds);
    }
}
