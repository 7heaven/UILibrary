package com.sevenheaven.uilibrarysample;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;

/**
 * Created by 7heaven on 16/8/25.
 */
public class ExampleDetailActivity extends Activity {

    public interface DetailContentProvider<T>{
        T provideInstance();

        void onGestureMove(float progress, int action);
    }

    private DetailContentProvider mContentProvider;

    private FrameLayout mRootLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_detail);

        mRootLayout = (FrameLayout) findViewById(R.id.root_layout);

        if(mContentProvider != null){
            Object providedInstance = mContentProvider.provideInstance();

            if(providedInstance instanceof View){

            }else if(providedInstance instanceof Drawable){
            }
        }
    }
}

