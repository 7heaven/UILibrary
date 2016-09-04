package com.sevenheaven.uilibrary.drawables;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.Shader;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.shapes.Shape;

/**
 * Convenient way to create Drawble with shape-cliped effect
 *
 * Created by 7heaven on 16/8/25.
 */
public class MaskDrawable extends Drawable {

    private Bitmap mBitmap;
    private Canvas mAssociateCanvas;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Drawable mDrawingContent;
    private Shape mMask;

    public MaskDrawable(Drawable content, Shape mask){
        mDrawingContent = content;
        mMask = mask;

        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(0xFFFFFFFF);
    }

    public void setContent(Drawable content){
        mDrawingContent = content;
        mDrawingContent.setBounds(getBounds());
        recreateBitmap();

        invalidateSelf();
    }

    public void setMask(Shape mask){
        mMask = mask;
        mMask.resize(getBounds().width(), getBounds().height());
        recreateBitmap();

        invalidateSelf();
    }

    public void recreateContent(){
        recreateBitmap();

        invalidateSelf();
    }

    @Override
    public void onBoundsChange(Rect bounds){
        if(mDrawingContent != null){
            mDrawingContent.setBounds(0, 0, bounds.width(), bounds.height());
        }
        if(mMask != null){
            mMask.resize(bounds.width(), bounds.height());
        }

        recreateBitmap();
    }

    private void recreateBitmap(){
        if(mDrawingContent != null && mMask != null){
            if(mBitmap != null && !mBitmap.isRecycled()){
                mBitmap.recycle();
                mBitmap = null;
            }
            mBitmap = Bitmap.createBitmap(getBounds().width() == 0 ? 1 : getBounds().width(), getBounds().height() == 0 ? 1 : getBounds().height(), Bitmap.Config.ARGB_8888);
            mAssociateCanvas = new Canvas(mBitmap);

            mDrawingContent.draw(mAssociateCanvas);

            mPaint.setShader(new BitmapShader(mBitmap, Shader.TileMode.REPEAT, Shader.TileMode.REPEAT));
        }
    }

    @Override
    public void draw(Canvas canvas){
        if(mMask != null){
            canvas.save();
            canvas.translate(-getBounds().left, -getBounds().top);

            mMask.draw(canvas, mPaint);
            canvas.restore();
        }
    }

    @Override
    public void setAlpha(int alpha) {
        mPaint.setAlpha(alpha);

        invalidateSelf();
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);

        invalidateSelf();
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    public void recycle(){
        mPaint.setShader(null);

        if(mBitmap != null && !mBitmap.isRecycled()){
            mBitmap.recycle();
            mBitmap = null;
        }
    }
}