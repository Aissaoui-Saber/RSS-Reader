package com.example.saber.mplrss;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.webkit.WebView;

public class ItemWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item_web_view);
        String lien = getIntent().getStringExtra("link");
        WebView wv = (WebView)findViewById(R.id.webViewItem);
        wv.loadUrl(lien);
    }
}
