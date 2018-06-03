package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.graphics.Bitmap;
import android.support.annotation.DrawableRes;
import android.widget.ImageView;

/**
 * {@link ImageView} 的 BindingAdapter。
 */

public class ImageViewBindingAdapter {

    /**
     * 设置 ImageView android:src 属性值为一个 Drawable 的资源 id。
     *
     * @param imageView {@link ImageView} 对象
     * @param imageId   Image 的资源 id，属于 R.drawable 下
     */
    @BindingAdapter("android:src")
    public static void setImageId(ImageView imageView, @DrawableRes int imageId) {
        imageView.setImageResource(imageId);
    }

    /**
     * 设置 ImageView android:src 属性值为一个 Bitmap。
     *
     * @param imageView {@link ImageView} 对象
     * @param bitmap    {@link Bitmap} 对象
     */
    @BindingAdapter("android:src")
    public static void setImageBitmap(ImageView imageView, Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }
}
