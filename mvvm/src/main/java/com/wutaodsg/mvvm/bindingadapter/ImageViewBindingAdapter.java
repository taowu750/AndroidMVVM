package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

/**
 * Created by wutao on 2018/5/2.
 */

public class ImageViewBindingAdapter {

    @BindingAdapter("android:src")
    public static void setImageId(ImageView imageView, @DrawableRes int imageId) {
        imageView.setImageResource(imageId);
    }
}
