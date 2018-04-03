package com.wutaodsg.mvvm.bindingadapter;

import android.databinding.BindingAdapter;
import android.text.TextUtils;
import android.webkit.WebView;

/**
 * {@link WebView} 的 BindingAdapter。
 */

public class WebViewBindingAdapter {

    /**
     * 设置 WebView 的渲染内容。
     *
     * @param webView {@link WebView} 对象
     * @param html    WebView 显示的 html 页面
     */
    @BindingAdapter({"render"})
    public static void loadHtml(final WebView webView,
                                final String html) {
        if (!TextUtils.isEmpty(html)) {
            webView.loadDataWithBaseURL(null, html, "text/html", "UTF-8", null);
        }
    }
}
