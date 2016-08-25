package com.sevenheaven.uilibrary.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.sevenheaven.uilibrary.drawables.GroupedAvatarDrawable;

import java.security.acl.Group;

/**
 * Created by 7heaven on 16/8/13.
 */
public class GroupedAvatarImageView extends ImageView {

    private GroupedAvatarDrawable mDrawable;

    public GroupedAvatarImageView(Context context){
        this(context, null);
    }

    public GroupedAvatarImageView(Context context, AttributeSet attrs){
        this(context, attrs, 0);
    }

    public GroupedAvatarImageView(Context context, AttributeSet attrs, int defStyle){
        super(context, attrs, defStyle);
    }

    public void setAvatars(Bitmap... bitmaps){

    }

    @Override
    public void setImageDrawable(Drawable drawable){
        if(drawable instanceof GroupedAvatarDrawable){
            super.setImageDrawable(drawable);
            mDrawable = (GroupedAvatarDrawable) drawable;
        }
    }

    @Override
    public void setImageBitmap(Bitmap bitmap){
        super.setImageBitmap(bitmap);
    }
}
