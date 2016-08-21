package com.sevenheaven.uilibrary.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sevenheaven.uilibrary.R;

/**
 * PageIndicator that can easily customize contents, with many pre-defined style such as circle, square, alphabet, etc.
 * Created by 7heaven on 16/5/25.
 */
public class PageIndicator extends View {

    public enum BlockType{

        SQUARE(0),
        CIRCLE(1),
        VERTICAL_LINE(2),
        HORIZONTAL_LINE(3),
        ALPHABET(4),
        NUMMERIC(5),
        CUSTOM_CHAR(6);

        private int mValue;

        BlockType(int value){
            mValue = value;
        }
    }

    private BlockType mCurrentBlockType;
    private int mBlockGap;
    private int mBlockSize;
    private int mBlockColor;
    private int mHighlightColor;

    private int mHalfBlockSize;

    private int mPageCount;
    private int mTotalContentWidth;

    /**
     * For recording content coordinate after measurement
     */
    private int mContentX;
    private int mContentY;

    /**
     * Canvas's drawText do not accept single char,
     * so we define a char array here for char content drawing
     */
    private char[] mAlphabetStore = new char[1];
    private char[] mCustomChar;

    private int mCurrentSelection = 0;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /**
     * FontMetric for positioning Text when drawText method is call
     */
    private Paint.FontMetrics mAlphabetFM = new Paint.FontMetrics();

    public PageIndicator(Context context){
        this(context, null);
    }

    public PageIndicator(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public PageIndicator(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);

        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.PageIndicator);
        setBlockType(BlockType.values()[ta.getInt(R.styleable.PageIndicator_blockType, BlockType.CIRCLE.mValue)]);
        setBlockSize(ta.getDimensionPixelSize(R.styleable.PageIndicator_blockSize, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics())));
        setBlockGap(ta.getDimensionPixelSize(R.styleable.PageIndicator_gap,  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, context.getResources().getDisplayMetrics())));
        setBlockColor(ta.getColor(R.styleable.PageIndicator_blockColor, 0xFFF5F5F5));
        setHighlightColor(ta.getColor(R.styleable.PageIndicator_highlightColor, 0xFFFFFFFF));
        setTotalPageCount(ta.getInt(R.styleable.PageIndicator_totalCount, 3));

        String customChars = ta.getString(R.styleable.PageIndicator_customChar);
        if(customChars != null) setCustomChar(customChars.toCharArray());

        ta.recycle();

    }

    public void setCustomChar(char... customChar){
        mCustomChar = customChar;
        if(mCurrentBlockType == BlockType.CUSTOM_CHAR){
            invalidate();
        }
    }

    public void setTotalPageCount(int pageCount){
        if(mPageCount != pageCount){
            mPageCount = pageCount;

            requestLayout();
        }
    }

    public void setCurrentSelection(int current){
        if(current != mCurrentSelection){
            mCurrentSelection = current;
            invalidate();
        }
    }

    public void setBlockType(BlockType type){
        mCurrentBlockType = type;
    }

    public void setBlockSize(int size){
        if(mBlockSize != size){
            mBlockSize = size;

            mHalfBlockSize = size / 2;

            //for convenient when BlockType being set to ALPHABET
            mPaint.setTextSize(size);
            mPaint.getFontMetrics(mAlphabetFM);

            requestLayout();
        }
    }

    public void setBlockGap(int gap){
        if(mBlockGap != gap){
            mBlockGap = gap;

            requestLayout();
        }
    }

    public void setBlockColor(int blockColor){
        if(mBlockColor != blockColor){
            mBlockColor = blockColor;

            invalidate();
        }
    }

    public void setHighlightColor(int highlightColor){
        if(mHighlightColor != highlightColor){
            mHighlightColor = highlightColor;

            invalidate();
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        if(mPageCount > 0){
            /**
             * calculate minimum content width based on block size and block gap
             */
            mTotalContentWidth = mPageCount * mBlockSize + (mPageCount - 1) * mBlockGap;

            final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
            final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
            final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
            final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

            int width;
            int height;

            if(widthMode == MeasureSpec.EXACTLY){
                width = widthSize;
            }else{
                width = mTotalContentWidth;
            }

            if(heightMode == MeasureSpec.EXACTLY){
                height = heightSize;
            }else{
                height = mBlockSize;
            }

            setMeasuredDimension(width, height);
        }else{
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh){
        int cX = w / 2;
        int cY = h / 2;

        mContentX = cX - mTotalContentWidth / 2;
        mContentY = cY - mHalfBlockSize;
    }

    @Override
    public void onDraw(Canvas canvas){
        int stepX = mContentX;
        for(int i = 0; i < mPageCount; i++){
            if(i == mCurrentSelection){
                mPaint.setColor(mHighlightColor);
            }else{
                mPaint.setColor(mBlockColor);
            }

            drawContent(canvas, stepX, mContentY, i);
            stepX += mBlockSize + mBlockGap;
        }
    }

    private void drawContent(Canvas canvas, int x, int y, int index){
        switch(mCurrentBlockType){
            case SQUARE:
                canvas.drawRect(x, y, x + mBlockSize, y + mBlockSize, mPaint);
                break;
            case CIRCLE:
                canvas.drawCircle(x + mHalfBlockSize, y + mHalfBlockSize, mHalfBlockSize, mPaint);
                break;
            case VERTICAL_LINE:
                canvas.drawLine(x + mHalfBlockSize, y, x + mHalfBlockSize, y + mBlockSize, mPaint);
                break;
            case HORIZONTAL_LINE:
                canvas.drawLine(x, y + mHalfBlockSize, x + mBlockSize, y + mHalfBlockSize, mPaint);
                break;
            case ALPHABET:
                mAlphabetStore[0] = (char) ((index % 26) + 'A');
                canvas.drawText(mAlphabetStore, 0, 1, x, y + mBlockSize - mAlphabetFM.descent, mPaint);
                break;
            case NUMMERIC:
                mAlphabetStore[0] = (char) ((index % 9) + '1');
                canvas.drawText(mAlphabetStore, 0, 1, x, y + mBlockSize - mAlphabetFM.descent, mPaint);
                break;
            case CUSTOM_CHAR:
                if(mCustomChar != null){
                    mAlphabetStore[0] = mCustomChar[(index % mCustomChar.length)];
                    canvas.drawText(mAlphabetStore, 0, 1, x, y + mBlockSize - mAlphabetFM.descent, mPaint);
                }
                break;

        }
    }
}

