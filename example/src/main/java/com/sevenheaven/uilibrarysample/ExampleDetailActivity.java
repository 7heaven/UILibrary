package com.sevenheaven.uilibrarysample;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.SeekBar;

/**
 * Created by 7heaven on 16/8/25.
 */
public class ExampleDetailActivity extends Activity {

    public interface DetailContentProvider<T>{
        T provideInstance();

        void onGestureMove(float x, float y, int action);

        void destroy();
    }

    public static DetailContentProvider mContentProvider;

    private FrameLayout mRootLayout;

    private SeekBar xAxisSeekBar;
    private SeekBar yAxisSeekBar;

    private int mScreenWidth;
    private int mScreenHeight;
    public static final int PROGRESS_INIT = -2;
    public static final int PROGRESS_DONE = -1;

    public static final String PROVIDER_EXTRA = "";

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_detail);

        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mScreenHeight = getResources().getDisplayMetrics().heightPixels;

        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);
        xAxisSeekBar = (SeekBar) findViewById(R.id.x_axis_seekbar);
        yAxisSeekBar = (SeekBar) findViewById(R.id.y_axis_seekbar);

        if(mContentProvider != null){
            Object providedInstance = mContentProvider.provideInstance();

            if(providedInstance instanceof View){
                View instance = (View) providedInstance;
                ViewGroup.LayoutParams params = instance.getLayoutParams();
                if(params == null){
                    params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                    instance.setLayoutParams(params);
                }

                mRootLayout.addView(instance);

            }else if(providedInstance instanceof Drawable){
                Drawable instance = (Drawable) providedInstance;

                View view = new View(this);
                ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

                if(Build.VERSION.SDK_INT > 16){
                    view.setBackground(instance);
                }else{
                    view.setBackgroundDrawable(instance);
                }

                mRootLayout.addView(view);
            }

            xAxisSeekBar.setMax(100);
            yAxisSeekBar.setMax(100);

            xAxisSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mContentProvider.onGestureMove((float) progress / 100.0F, (float) yAxisSeekBar.getProgress() / 100.0F, MotionEvent.ACTION_MOVE);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mContentProvider.onGestureMove(PROGRESS_INIT, PROGRESS_INIT, MotionEvent.ACTION_DOWN);
                    mContentProvider.onGestureMove(0, (float) yAxisSeekBar.getProgress() / 100.0F, MotionEvent.ACTION_DOWN);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mContentProvider.onGestureMove(PROGRESS_DONE, PROGRESS_DONE, MotionEvent.ACTION_UP);
                }
            });

            yAxisSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    mContentProvider.onGestureMove((float) xAxisSeekBar.getProgress() / 100.0F, (float) progress / 100.0F, MotionEvent.ACTION_MOVE);
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {
                    mContentProvider.onGestureMove(PROGRESS_INIT, PROGRESS_INIT, MotionEvent.ACTION_DOWN);
                    mContentProvider.onGestureMove((float) xAxisSeekBar.getProgress() / 100.0F, 0, MotionEvent.ACTION_DOWN);
                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {
                    mContentProvider.onGestureMove(PROGRESS_DONE, PROGRESS_DONE, MotionEvent.ACTION_UP);
                }
            });
        }
    }

//    @Override
//    public boolean onTouchEvent(MotionEvent event){
//        if(mContentProvider != null) {
//            int action = event.getActionMasked();
//            switch (action) {
//                case MotionEvent.ACTION_DOWN:
//                    mContentProvider.onGestureMove(PROGRESS_INIT, PROGRESS_INIT, action);
//                    mContentProvider.onGestureMove(0, 0, action);
//                    break;
//                case MotionEvent.ACTION_MOVE:
//                    mContentProvider.onGestureMove(event.getX() / mScreenWidth, event.getY() / mScreenHeight, action);
//                    break;
//                case MotionEvent.ACTION_UP:
//                    mContentProvider.onGestureMove(PROGRESS_DONE, PROGRESS_DONE, action);
//                    break;
//            }
//            return true;
//        }else{
//            return super.onTouchEvent(event);
//        }
//    }

    @Override
    protected  void onDestroy(){
        super.onDestroy();
        if(mRootLayout.getChildCount() > 0){
            mRootLayout.removeAllViews();
        }
        if(mContentProvider != null){
            mContentProvider.destroy();
            mContentProvider = null;
        }
    }
}

