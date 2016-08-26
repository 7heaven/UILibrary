package com.sevenheaven.uilibrarysample;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import com.sevenheaven.uilibrary.drawables.GroupedAvatarDrawable;
import com.sevenheaven.uilibrary.drawables.MaskDrawable;
import com.sevenheaven.uilibrary.drawables.progressive.ProgressiveDrawable;
import com.sevenheaven.uilibrary.drawables.progressive.providers.AppStoreStyleProgressProvider;
import com.sevenheaven.uilibrary.drawables.progressive.providers.PathProgressProvider;
import com.sevenheaven.uilibrary.shapes.PolygonShape;
import com.sevenheaven.uilibrary.views.AnimatedImageView;
import com.sevenheaven.uilibrarysample.ExampleListAdapter.ExampleItem;

/**
 * Created by 7heaven on 16/8/25.
 */
public class ExampleListActivity extends Activity {

    private RecyclerView mRecyclerView;
    private ExampleListAdapter mAdapter;
    private List<ExampleItem> mExampleItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_example_list);

        mExampleItemList = new ArrayList<>();
        mAdapter = new ExampleListAdapter(mExampleItemList);

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
    }

    @Override
    protected void onResume(){
        super.onResume();

        mExampleItemList.clear();
        addGroupedAvatarDrawbleToList();
        addAppStoreStyleDrawableToList();
        addPathProgressDrawableToList();
        addMaskDrawableToList();
        addPolygonShapeInteractToList();
        addAnimatedImageViewToList();
    }

    private void addAnimatedImageViewToList(){
        final AnimatedImageView imageView = new AnimatedImageView(this);
        ViewGroup.MarginLayoutParams layoutParams = new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(50, 50, 50, 50);
        imageView.setLayoutParams(layoutParams);
        imageView.setImageResource(R.drawable.test2);

        final ImageView.ScaleType[] allScaleTypes = new ImageView.ScaleType[]{
                ImageView.ScaleType.CENTER,
                ImageView.ScaleType.CENTER_CROP,
                ImageView.ScaleType.CENTER_INSIDE,
                ImageView.ScaleType.FIT_CENTER,
                ImageView.ScaleType.FIT_END,
                ImageView.ScaleType.FIT_START,
                ImageView.ScaleType.FIT_XY
        };

        mExampleItemList.add(new ExampleItem("AnimatedImageView", new ExampleDetailActivity.DetailContentProvider() {
            @Override
            public Object provideInstance() {
                return imageView;
            }

            @Override
            public void onGestureMove(float x, float y, int action) {
                if(x >= 0 && x <= 1){
                    imageView.setScaleTypeAnimated(allScaleTypes[(int) (x * allScaleTypes.length)]);
                }
            }

            @Override
            public void destroy(){
            }
        }));
    }

    private void addPolygonShapeInteractToList(){
        final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        final int padding = 30;
        final PolygonShape shape = new PolygonShape(6, 0.2F, true, 0.5F);
        final Drawable contentDrawable = new Drawable() {
            @Override
            public void draw(Canvas canvas) {
                canvas.save();
                canvas.translate(padding, padding);
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(10);
                paint.setColor(0xFF32ADFF);

                shape.draw(canvas, paint);

                canvas.restore();
            }

            @Override
            public void setAlpha(int i) {

            }

            @Override
            public void setColorFilter(ColorFilter colorFilter) {

            }

            @Override
            public int getOpacity() {
                return PixelFormat.TRANSPARENT;
            }

            @Override
            public void onBoundsChange(Rect bounds){
                shape.resize(bounds.width() - padding - padding, bounds.height() - padding - padding);
            }
        };
        mExampleItemList.add(new ExampleItem("PolygonShapeInteract", new ExampleDetailActivity.DetailContentProvider() {
            @Override
            public Object provideInstance() {
                return contentDrawable;
            }

            @Override
            public void onGestureMove(float x, float y, int action) {
                if(x >= 0 && x <= 1 && y >= 0 && y <= 1){
                    shape.setCornerRadius(x / 2);
                    shape.setVertexCount((int) (y * 10) + 3);
                    contentDrawable.invalidateSelf();
                }
            }

            @Override
            public void destroy(){

            }
        }));
    }

    private void addMaskDrawableToList(){
        final Drawable contentDrawable = ContextCompat.getDrawable(this, R.drawable.test1);
        final PolygonShape shape = new PolygonShape(6, 0.2F);
        final MaskDrawable drawable = new MaskDrawable(contentDrawable, shape);
        mExampleItemList.add(new ExampleItem("MaskDrawable+PolygonShape", new ExampleDetailActivity.DetailContentProvider() {
            @Override
            public Object provideInstance() {
                return drawable;
            }

            @Override
            public void onGestureMove(float x, float y, int action) {
                if(x >= 0 && x <= 1){
                    shape.setVertexCount((int) (x * 6) + 3);
                    drawable.recreateContent();
                }
            }

            @Override
            public void destroy(){

            }
        }));
    }

    private void addPathProgressDrawableToList(){
        Paint paint = new Paint();
        paint.setTextSize(100);

        Path animatePath = new Path();
        String text = "Android N";
        paint.getTextPath(text, 0, text.length(), 0, paint.getTextSize(), animatePath);
        PathProgressProvider.PathDesc animateDesc = new PathProgressProvider.PathDesc(animatePath, Gravity.CENTER, true, false, false);

        RectF animatePathBounds = new RectF();
        animatePath.computeBounds(animatePathBounds, false);

        Path progressPath = new Path();
        progressPath.addCircle(100, 100, animatePathBounds.width() / 2 + 50, Path.Direction.CW);
        progressPath.addCircle(100, 100, animatePathBounds.width() / 2 + 80, Path.Direction.CW);
        PathProgressProvider.PathDesc pathDesc = new PathProgressProvider.PathDesc(progressPath, Gravity.CENTER, true, true, false);

        ProgressiveDrawable.DrawContentProvider pathProgressProvider = new PathProgressProvider(pathDesc, animateDesc){
            @Override
            protected void updateProgressPaint(Paint paint){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(5);
                paint.setStrokeCap(Paint.Cap.ROUND);
                paint.setColor(0xFF44AEFF);
            }

            @Override
            protected void updateAnimationPaint(Paint paint){
                paint.setStyle(Paint.Style.STROKE);
                paint.setStrokeWidth(2);
                paint.setColor(0xFF44AEFF);
            }
        };

        final ProgressiveDrawable drawable = new ProgressiveDrawable(pathProgressProvider);
        mExampleItemList.add(new ExampleItem("PathProgressDrawable", new ExampleDetailActivity.DetailContentProvider() {
            @Override
            public Object provideInstance() {
                return drawable;
            }

            @Override
            public void onGestureMove(float x, float y, int action) {
                if(x >= -1 && x <= 1){
                    drawable.setProgress(x);
                }
            }

            @Override
            public void destroy(){

            }
        }));
    }

    private void addAppStoreStyleDrawableToList(){
        ProgressiveDrawable.DrawContentProvider appStoreProvider = new AppStoreStyleProgressProvider(0x99000000);

        final ProgressiveDrawable drawable = new ProgressiveDrawable(appStoreProvider);
        final Drawable backgroundDrawable = ContextCompat.getDrawable(this, R.drawable.test1);
        final LayerDrawable contentDrawalbe = new LayerDrawable(new Drawable[]{backgroundDrawable, drawable});
        mExampleItemList.add(new ExampleItem("AppStoreStyleDrawable", new ExampleDetailActivity.DetailContentProvider() {
            @Override
            public Object provideInstance() {
                return contentDrawalbe;
            }

            @Override
            public void onGestureMove(float x, float y, int action) {
                if(x >= -1 && x <= 1){
                    drawable.setProgress(x);
                }
            }

            @Override
            public void destroy(){

            }
        }));
    }

    private void addGroupedAvatarDrawbleToList(){
        final Bitmap[] avatars = new Bitmap[]{BitmapFactory.decodeResource(getResources(), R.drawable.test0),
                BitmapFactory.decodeResource(getResources(), R.drawable.test1),
                BitmapFactory.decodeResource(getResources(), R.drawable.test2),
                BitmapFactory.decodeResource(getResources(), R.drawable.test0),
                BitmapFactory.decodeResource(getResources(), R.drawable.test1),
                BitmapFactory.decodeResource(getResources(), R.drawable.test2),
                BitmapFactory.decodeResource(getResources(), R.drawable.test0),
                BitmapFactory.decodeResource(getResources(), R.drawable.test1),
                BitmapFactory.decodeResource(getResources(), R.drawable.test2)};
        final GroupedAvatarDrawable avatarDrawable = new GroupedAvatarDrawable();
        avatarDrawable.setStrokeWidth(20);
        mExampleItemList.add(new ExampleItem("GroupedAvatarDrawable", new ExampleDetailActivity.DetailContentProvider<GroupedAvatarDrawable>() {
            @Override
            public GroupedAvatarDrawable provideInstance() {
                return avatarDrawable;
            }

            @Override
            public void onGestureMove(float x, float y, int action) {
                if(x >= 0 && x <= 1){
                    int index = (int) (x * avatars.length);
                    int count = index < avatars.length ? index : avatars.length;
                    Bitmap[] input = new Bitmap[count];
                    for(int i = 0; i < count; i++){
                        input[i] = avatars[i];
                    }

                    avatarDrawable.setAvatars(input);
                }
            }

            @Override
            public void destroy(){

            }
        }));
    }
}
