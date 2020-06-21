package com.company.bdktaliparamba;


import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class HomeActivity extends AppCompatActivity {
    private WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        webView = (WebView) findViewById(R.id.webview);
        webView.setWebViewClient(new WebViewClient() {
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith("tel:")) {
                    Intent intent = new Intent(Intent.ACTION_DIAL,
                            Uri.parse(url));
                    startActivity(intent);
                }else if(url.startsWith("sms:")){
                    Intent intent = new Intent(Intent.ACTION_SENDTO);
                    String phoneNumber = url.split("[:?]")[1];

                    if(!TextUtils.isEmpty(phoneNumber)){
                        // Set intent data
                        // This ensures only SMS apps respond
                        intent.setData(Uri.parse("smsto:" + phoneNumber));

                        // Alternate data scheme
                        //intent.setData(Uri.parse("sms:" + phoneNumber));
                    }else {
                        // If the sms link built without phone number
                        intent.setData(Uri.parse("smsto:"));

                        // Alternate data scheme
                        //intent.setData(Uri.parse("sms:" + phoneNumber));
                    }


                    // Extract the sms body from sms url
                    if(url.contains("body=")){
                        String smsBody = url.split("body=")[1];

                        // Encode the sms body
                        try{
                            smsBody = URLDecoder.decode(smsBody,"UTF-8");
                        }catch (UnsupportedEncodingException e){
                            e.printStackTrace();
                        }

                        if(!TextUtils.isEmpty(smsBody)){
                            // Set intent body
                            intent.putExtra("sms_body",smsBody);
                        }
                    }

                    if(intent.resolveActivity(getPackageManager())!=null){
                        // Start the sms app
                        startActivity(intent);
                    }

                }

                else if (url.startsWith("http:") || url.startsWith("https:")) {
                    view.loadUrl(url);
                }
                return true;
            }

            public void onReceivedError(WebView webView, int errorCode, String description, String failingUrl) {
                try {
                    webView.stopLoading();
                } catch (Exception e) {
                }

                if (webView.canGoBack()) {
                    webView.goBack();
                }

                webView.loadUrl("about:blank");
                AlertDialog alertDialog = new AlertDialog.Builder(HomeActivity.this).create();
                alertDialog.setTitle("Error");
                alertDialog.setMessage("Check your internet connection and try again.");
                alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Try Again", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        startActivity(getIntent());
                    }
                });

                alertDialog.show();
                super.onReceivedError(webView, errorCode, description, failingUrl);
            }
        });

        webView.loadUrl("http://www.bdktaliparamba.epizy.com");
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setAppCacheEnabled(true);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.getLoadsImagesAutomatically();


         webSettings.setLoadWithOverviewMode(true);
          webSettings.setUseWideViewPort(true);
        // webSettings.setAllowFileAccess(true);
        // webSettings.setAllowContentAccess(true);


        // webView.setWebChromeClient(new WebChromeClient());


    }

    @Override
    public void onBackPressed () {
        if (webView.canGoBack()) {
            webView.goBack();

        } else {
            super.onBackPressed();
        }
    }
}