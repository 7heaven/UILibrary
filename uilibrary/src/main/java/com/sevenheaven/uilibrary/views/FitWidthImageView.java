package com.sevenheaven.uilibrary.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * TODO reconstrution
 * Created by 7heaven on 16/8/9.
 */
public class FitWidthImageView extends ImageView {

    private int width;
    private int height;

    private int intrinsicWidth;
    private int intrinsicHeight;

    private float scaleRate;

    public FitWidthImageView(Context context) {
        this(context, null);
    }

    public FitWidthImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FitWidthImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setScaleType(ScaleType.MATRIX);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);



        width = getMeasuredWidth();

        if(heightMode == MeasureSpec.EXACTLY){
            height = heightSize;
        }else{
            height = heightSize - getPaddingTop() - getPaddingBottom();
        }

        float rate = (float) width / (float) intrinsicWidth;
        height = (int) ((float) intrinsicHeight * rate);
        setMeasuredDimension(width, height);

        scaleToFit(false);
    }



    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        super.onSizeChanged(w, h, oldw, oldh);

        width = w;
        height = h;
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        free();
        if (drawable != null) {
            intrinsicWidth = drawable.getIntrinsicWidth();
            intrinsicHeight = drawable.getIntrinsicHeight();
            super.setImageDrawable(drawable);
            scaleToFit();
        } else {
            super.setImageDrawable(null);
        }
    }

    @Override
    public void setImageResource(int resourceId) {
        free();
        super.setImageResource(resourceId);

        Drawable d = this.getDrawable();

        if (d != null) {
            intrinsicWidth = d.getIntrinsicWidth();
            intrinsicHeight = d.getIntrinsicHeight();
            scaleToFit();
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap) {
        free();
        if (bitmap != null) {
            intrinsicWidth = bitmap.getWidth();
            intrinsicHeight = bitmap.getHeight();
            super.setImageBitmap(bitmap);
            scaleToFit();
        } else {
            super.setImageBitmap(null);
        }
    }

    public float getScaleRate() {
        return scaleRate;
    }

    public int getIntrinsicWidth() {
        return intrinsicWidth;
    }

    public int getIntrinsicHeight() {
        return intrinsicHeight;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        free();
    }

    private void free() {
        if (getDrawingCache() != null) {
            Bitmap b = getDrawingCache();
            setImageBitmap(null);
            b.recycle();
            b = null;
        }

    }

    private void scaleToFit() {
        scaleToFit(true);
    }

    private void scaleToFit(boolean forceLayout){
        Matrix matrix = new Matrix();
        float rate = (float) width / (float) intrinsicWidth;
        scaleRate = rate;
        matrix.postScale(rate, rate);
        setImageMatrix(matrix);
        if(forceLayout) requestLayout();
    }
}
