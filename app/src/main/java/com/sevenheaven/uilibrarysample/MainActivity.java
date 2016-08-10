package com.sevenheaven.uilibrarysample;

import android.graphics.Paint;
import android.graphics.Path;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;

import com.sevenheaven.uilibrary.drawables.progressive.ProgressiveDrawable;
import com.sevenheaven.uilibrary.drawables.progressive.providers.AppStoreStyleProgressProvider;
import com.sevenheaven.uilibrary.drawables.progressive.providers.PathProgressProvider;

public class MainActivity extends AppCompatActivity {

    ProgressiveDrawable progressiveDrawable;

    ProgressiveDrawable.DrawContentProvider pathProgressProvider;
    ProgressiveDrawable.DrawContentProvider appStoreProvider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View view = new View(this);

        Path path = new Path();
//        path.moveTo(0, 200);
//        path.addArc(0, 0, 200, 200, 180, -180);
//        path.moveTo(100, 100);
//        path.lineTo(100, 200);
//        path.moveTo(100, 100);
//        path.lineTo(100, 0);
//        path.addArc(0, 0, 200, 200, 180, 180);

        Paint paint = new Paint();
        paint.setTextSize(100);


        String text = "Android N";
        paint.getTextPath(text, 0, text.length(), 0, paint.getTextSize(), path);
        PathProgressProvider.PathDesc pathDesc = new PathProgressProvider.PathDesc(path, Gravity.CENTER, true, false, true);

        pathProgressProvider = new PathProgressProvider(pathDesc, null){
            @Override
            protected void updateProgressPaint(Paint paint){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(3);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setColor(0xFF44AEFF);
            }
        };

        appStoreProvider = new AppStoreStyleProgressProvider(0x9944AEFF);

        progressiveDrawable = new ProgressiveDrawable(pathProgressProvider);
        view.setBackground(progressiveDrawable);

        setContentView(view);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
                progressiveDrawable.setProgress(0);
                break;
            case MotionEvent.ACTION_MOVE:
                float progress = event.getX() / (float) getResources().getDisplayMetrics().widthPixels;

                progressiveDrawable.setProgress(progress);
                break;
            case MotionEvent.ACTION_UP:
                progressiveDrawable.setProgress(-1);
                break;
        }

        return true;
    }
}
