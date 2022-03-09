package com.helpme.helpmemrboband;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;

import java.net.CookieManager;

public class MyPageWebView extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_page_web_view);

        Intent intent = getIntent();
        String id = intent.getStringExtra("id");

        if (id == null) {
            Intent intent1 = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent1);
        }
        else {

        }

        Button back = findViewById(R.id.btn_back);
        WebView webView = findViewById(R.id.web_view);

        WebSettings settings = webView.getSettings(); //세부 세팅 등록
        settings.setSupportMultipleWindows(false); //새창 띄우기 허용 여부
        settings.setJavaScriptCanOpenWindowsAutomatically(false); //자바스크립트 새창 띄우기(멀티뷰) 허용 여부
        settings.setLoadWithOverviewMode(true); //메타태그 허용 여부
        settings.setUseWideViewPort(true); //화면 사이즈 맞추기 허용 여부
        settings.setJavaScriptEnabled(true); //웹페이지 자바스크립트 허용 여부
        settings.setDisplayZoomControls(false); //화면에 줌컨트롤 띄울지 여부
        settings.setBuiltInZoomControls(false); //화면 확대 축소 허용 여부
        settings.setLoadsImagesAutomatically(true); //앱에 등록되어있는 이미지 리소스를 사용해야 할 경우 자동로드 여부

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("http://192.168.219.105:8081/helpmemrbob/mypageAnd.do");

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}