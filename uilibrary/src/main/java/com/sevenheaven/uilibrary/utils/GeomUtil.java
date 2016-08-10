package com.sevenheaven.uilibrary.utils;

import android.graphics.Matrix;
import android.graphics.Rect;

/**
 * Utility for geometry calculation
 *
 * Created by 7heaven on 16/8/5.
 */
public class GeomUtil {

    /**
     * Get the position on line(x0,y0-x1,y1) based on ratio
     *
     * @param x0
     * @param y0
     * @param x1
     * @param y1
     * @param ratio
     * @return position on line based on ratio float[]{x, y}
     */
    public static float[] pointOnLine(float x0, float y0, float x1, float y1, float ratio){
        float[] result = new float[2];

        pointOnLine(x0, y0, x1, y1, ratio, result);

        return result;
    }

    public static void pointOnLine(float x0, float y0, float x1, float y1, float ratio, float[] position){
        if(position != null && position.length == 2){
            float dx = x1 - x0;
            float dy = y1 - y0;

            dx *= ratio;
            dy *= ratio;

            position[0] = x0 + dx;
            position[1] = y0 + dy;
        }
    }

    /**
     * Calculate point on circumference for circle(centerX, centerY, radius) with angle in radians
     *
     * @param centerX x of the center of the circle
     * @param centerY y of the center of the circle
     * @param angle angle in radians
     * @param radius radius of the circle
     * @return point on circumference float[]{x, y}
     */
    public static float[] pointOnCircumference(int centerX, int centerY, double angle, double radius){
        float[] result = new float[2];

        pointOnCircumference(centerX, centerY, angle, radius, result);

        return result;
    }

    public static void pointOnCircumference(int centerX, int centerY, double angle, double radius, float[] outPoint){
        if(outPoint != null && outPoint.length == 2){
            float x = (float) (radius * Math.cos(angle) + centerX);
            float y = (float) (radius * Math.sin(angle) + centerY);

            outPoint[0] = x;
            outPoint[1] = y;
        }
    }

    /**
     * Get the transformation matrix from the original rectangle to the target rectangle
     *
     * @param originalRect
     * @param targetRect
     * @param outMatrix
     */
    public static void getTransformationMatrix(Rect originalRect, Rect targetRect, Matrix outMatrix){
        if(outMatrix != null){
            int transformX = targetRect.left - originalRect.left;
            int transformY = targetRect.top - originalRect.top;

            float scaleX = (float) targetRect.width() / (float) originalRect.width();
            float scaleY = (float) targetRect.height() / (float) originalRect.height();

            outMatrix.setScale(scaleX, scaleY, originalRect.left, originalRect.top);
            outMatrix.postTranslate(transformX, transformY);
        }
    }
}
