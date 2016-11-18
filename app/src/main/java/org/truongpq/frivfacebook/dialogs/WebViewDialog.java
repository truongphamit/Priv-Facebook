package org.truongpq.frivfacebook.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.truongpq.frivfacebook.R;

/**
 * Created by truongpq on 15/11/2016.
 */

public class WebViewDialog extends Dialog {
    private WebView webView;
    private ImageView imgClose;
    private TextView tvTitle, tvLink;
    private String url;

    public WebViewDialog(Context context, String url) {
        super(context, R.style.MaterialDialogSheet);
        this.url = url;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_webview);
        getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        getWindow().setGravity(Gravity.BOTTOM);

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvLink = (TextView) findViewById(R.id.tv_link);
        imgClose = (ImageView) findViewById(R.id.img_close);
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        webView = (WebView) findViewById(R.id.webView);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                webView.loadUrl(url);
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onLoadResource(WebView view, String url) {
                tvTitle.setText(webView.getTitle());
                tvLink.setText(webView.getUrl());
                super.onLoadResource(view, url);
            }
        });
        webView.loadUrl(url);
    }
}
