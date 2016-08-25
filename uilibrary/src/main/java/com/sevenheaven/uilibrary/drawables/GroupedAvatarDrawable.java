package com.sevenheaven.uilibrary.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;

import com.sevenheaven.uilibrary.utils.GeomUtil;

/**
 * Drawable that draw a group of images as a circle
 *
 * Created by 7heaven on 16/8/13.
 */
public class GroupedAvatarDrawable extends Drawable {

    /**
     * Cached bitmaps for bounds change recalculation
     */
    private Bitmap[] mInputBitmaps;
    private Bitmap mSourceBitmap;

    private int mRadiusStrokeWidth;

    private static final float SINGLE_ITEM_OVERLAY_FACTOR = 0.08F;

    private int mCenterX;
    private int mCenterY;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public GroupedAvatarDrawable(){
        mRadiusStrokeWidth = 10;
    }

    @Override
    public int getOpacity(){
        return PixelFormat.TRANSPARENT;
    }

    @Override
    public void setAlpha(int alpha){
        mPaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter filter){
        mPaint.setColorFilter(filter);
    }

    /**
     * set avatar bitmaps for drawing
     * @param inputBitmaps
     */
    public void setAvatars(Bitmap... inputBitmaps){
        mInputBitmaps = inputBitmaps;

        if(getBounds().width() > 0 && getBounds().height() > 0){
            mCenterX = getBounds().width() / 2;
            mCenterY = getBounds().height() / 2;
            generateSourceBitmap(getBounds());

            invalidateSelf();
        }
    }

    /**
     * set stroke width for for single image
     * @param strokeWidth
     */
    public void setStrokeWidth(int strokeWidth){
        if(mRadiusStrokeWidth != strokeWidth){
            mRadiusStrokeWidth = strokeWidth;

            generateSourceBitmap(getBounds());

            invalidateSelf();
        }
    }

    /**
     * Generate source bitmap for drawable drawing
     *
     * @param bounds
     */
    private void generateSourceBitmap(Rect bounds){
        if(mInputBitmaps != null && mInputBitmaps.length > 0){
            final int bitmapCount = mInputBitmaps.length;

            mSourceBitmap = Bitmap.createBitmap(bounds.width(), bounds.height(), Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mSourceBitmap);
            Paint drawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            drawPaint.setFilterBitmap(true);

            float fitBoundsRadius = (bounds.width() > bounds.height() ? bounds.height() : bounds.width()) / 2;
            if(bitmapCount > 1){
                /**
                 * To ensure when bitmapCount == 3, each item's radius will equals to fitBoundsRadius / 2
                 * and radius of single item will decrease when bitmapCount increase
                 */
                float scaleFactor = bitmapCount < 7 ? (1.0F + SINGLE_ITEM_OVERLAY_FACTOR) - (bitmapCount - 2) * SINGLE_ITEM_OVERLAY_FACTOR : 1.0F * (5.0F / bitmapCount);
                float singleRadius = (fitBoundsRadius * scaleFactor) / 2 - mRadiusStrokeWidth;
                float outerCircleRadius = fitBoundsRadius - singleRadius - mRadiusStrokeWidth;

                double step = Math.PI * 2.0 / bitmapCount;

                double startAngle;
                if((bitmapCount & 1) == 0){
                    startAngle = -Math.PI * 0.5 - (bitmapCount == 2 ? Math.PI * 0.25 : step / 2);
                }else{
                    startAngle = -Math.PI * 0.5F;
                }

                float[] centerPoint = new float[2];
                for(int i = 0; i < bitmapCount; i++){
                    GeomUtil.pointOnCircumference(mCenterX, mCenterY, startAngle + i * step, outerCircleRadius, centerPoint);

                    if(mInputBitmaps[i] != null && !mInputBitmaps[i].isRecycled()) drawBitmapWithStroke(canvas, mInputBitmaps[i], (int) centerPoint[0], (int) centerPoint[1], singleRadius, drawPaint);
                }

                if(bitmapCount > 2){
                    float outerClipRadius = outerCircleRadius * 2;
                    RectF outerClipBounds = new RectF();
                    outerClipBounds.left = mCenterX - outerClipRadius;
                    outerClipBounds.top = mCenterY - outerClipRadius;
                    outerClipBounds.right = mCenterX + outerClipRadius;
                    outerClipBounds.bottom = mCenterY + outerClipRadius;

                    final int savedCount = canvas.save();
                    Path clipPath = new Path();
                    clipPath.moveTo(mCenterX, mCenterY);
                    GeomUtil.pointOnCircumference(mCenterX, mCenterY, startAngle + (mInputBitmaps.length - 1) * step, outerClipRadius, centerPoint);
                    clipPath.arcTo(outerClipBounds, (float) Math.toDegrees((startAngle + (mInputBitmaps.length - 1) * step)), (float) Math.toDegrees(step));
                    clipPath.close();
                    canvas.clipPath(clipPath);

                    GeomUtil.pointOnCircumference(mCenterX, mCenterY, startAngle, outerCircleRadius, centerPoint);
                    drawBitmapWithStroke(canvas, mInputBitmaps[0], (int) centerPoint[0], (int) centerPoint[1], singleRadius, drawPaint);


                    canvas.restoreToCount(savedCount);
                }
            }else{
                drawBitmapWithStroke(canvas, mInputBitmaps[0], mCenterX, mCenterY, fitBoundsRadius - mRadiusStrokeWidth, drawPaint);
            }
        }
    }

    /**
     * Draw single bitmap with the giving centerX, centerY and radius
     * @param canvas
     * @param bitmap
     * @param centerX
     * @param centerY
     * @param radius
     * @param drawPaint
     */
    private void drawBitmapWithStroke(Canvas canvas, Bitmap bitmap, int centerX, int centerY, float radius, Paint drawPaint){
        drawPaint.setStyle(Paint.Style.FILL);

        float strokeRadius = radius + mRadiusStrokeWidth;
        drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        canvas.drawCircle(centerX, centerY, strokeRadius, drawPaint);
        drawPaint.setXfermode(null);

        final int halfBitmapWidth = bitmap.getWidth() / 2;
        final int halfBitmapHeight = bitmap.getHeight() / 2;

        BitmapShader bitmapShader = new BitmapShader(bitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
        Matrix shaderMatrix = new Matrix();
        float minSize = bitmap.getWidth() > bitmap.getHeight() ? bitmap.getHeight() : bitmap.getWidth();
        float scale = radius * 2 / minSize;
        shaderMatrix.setScale(scale, scale);
        shaderMatrix.postTranslate(centerX - (halfBitmapWidth * scale), centerY - (halfBitmapHeight * scale));
        bitmapShader.setLocalMatrix(shaderMatrix);

        drawPaint.setShader(bitmapShader);
        canvas.drawCircle(centerX, centerY, radius, drawPaint);
        drawPaint.setShader(null);
    }

    @Override
    protected void onBoundsChange(Rect bounds){
        mCenterX = bounds.width() / 2;
        mCenterY = bounds.height() / 2;

        if(bounds.width() > 0 && bounds.height() > 0) generateSourceBitmap(bounds);
    }

    @Override
    public void draw(Canvas canvas){
        if(mSourceBitmap != null && !mSourceBitmap.isRecycled()){
            canvas.drawBitmap(mSourceBitmap, 0, 0, mPaint);
        }
    }

    public void recycle(){
        if(mInputBitmaps != null){
            for(int i = 0; i < mInputBitmaps.length; i++){
                Bitmap bitmap = mInputBitmaps[i];
                if(bitmap != null && !bitmap.isRecycled()){
                    bitmap.recycle();
                }
            }

            mInputBitmaps = null;
        }

        if(mSourceBitmap != null && !mSourceBitmap.isRecycled()){
            mSourceBitmap.recycle();
            mSourceBitmap = null;
        }
    }
}
