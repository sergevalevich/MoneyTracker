package com.valevich.moneytracker.utils.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.valevich.moneytracker.R;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;

/**
 * Created by NotePad.by on 28.05.2016.
 */
@EBean
public class ImageLoader {
    @RootContext
    Activity mActivity;
    public void loadRoundedUserImage(final ImageView profileImage, String imageUrl) {
        Glide.with(mActivity)
                .load(imageUrl)
                .asBitmap()
                .placeholder(R.drawable.dummy_profile)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(profileImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profileImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }
    public void loadRoundedUserImage(final ImageView profileImage, int resourceId) {
        Glide.with(mActivity)
                .load(resourceId)
                .asBitmap()
                .placeholder(R.drawable.dummy_profile)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(new BitmapImageViewTarget(profileImage) {
                    @Override
                    protected void setResource(Bitmap resource) {
                        RoundedBitmapDrawable circularBitmapDrawable =
                                RoundedBitmapDrawableFactory.create(mActivity.getResources(), resource);
                        circularBitmapDrawable.setCircular(true);
                        profileImage.setImageDrawable(circularBitmapDrawable);
                    }
                });
    }
}
