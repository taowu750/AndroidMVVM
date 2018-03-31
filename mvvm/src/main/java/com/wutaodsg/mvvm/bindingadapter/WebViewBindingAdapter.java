package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.webkit.WebView;

/**
 * Created by wutao on 2018/3/8.
 */

public class WebViewBindingAdapter {

    @BindingAdapter({"render"})
    public static void loadHtml(final WebView webView,
                                final String html) {
        if (!TextUtils.isEmpty(html)) {
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }
}
