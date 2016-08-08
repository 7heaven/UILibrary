package com.sevenheaven.uilibrary.utils;

/**
 * Created by 7heaven on 16/8/5.
 */
public class GeomUtils {

    public static float[] segLine(float x0, float y0, float x1, float y1, float ratio){
        float dx = x1 - x0;
        float dy = y1 - y0;

        dx *= ratio;
        dy *= ratio;

        return new float[]{x0 + dx, y0 + dy};
    }

    public static float[] centerRadiusPoint(int centerX, int centerY, double angle, double radius){
        float x = (float) (radius * Math.cos(angle) + centerX);
        float y = (float) (radius * Math.sin(angle) + centerY);

        return new float[]{x, y};
    }
}
