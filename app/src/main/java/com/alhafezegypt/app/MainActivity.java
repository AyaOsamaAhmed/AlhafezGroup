package com.alhafezegypt.app;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {

    public static WebView wv;
    public ImageView iv;
    int a;

    ProgressDialog progress;
    public static String site;

    public static Context activityContext;
    public static MainActivity mainActivity;

    // private ValueCallback<Uri> mUploadMessage;
    /**
     * File upload callback for platform versions prior to Android 5.0
     */
    protected ValueCallback<Uri> mFileUploadCallbackFirst;
    /**
     * File upload callback for Android 5.0+
     */
    protected ValueCallback<Uri[]> mFileUploadCallbackSecond;
    private static final int FILE_CHOOSER_RESULT_CODE = 1;


    @Override
    protected void onNewIntent(Intent intent) {

        String action = "";
        Bundle bn = intent.getExtras();
        if (bn != null) {
            action = bn.getString("action", "");
            if (!action.isEmpty()) {
                if ("fromReload".equals(action)) {
                    iv.setVisibility(View.INVISIBLE);
                    wv.setVisibility(View.VISIBLE);

                    wv.loadUrl(site);
                } else if ("fromNotification".equals(action)) {
                    iv.setVisibility(View.INVISIBLE);
                    wv.setVisibility(View.VISIBLE);
                    String link = bn.getString("link", "");
                    wv.loadUrl(link);
                }
            }
        } else {
            iv.setVisibility(View.VISIBLE);
            wv.setVisibility(View.INVISIBLE);

            wv.loadUrl(site);
        }

        super.onNewIntent(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityContext = MainActivity.this;
        mainActivity = this;

        site = "http://alhafez-egypt.com/mobile/Default.aspx?pid=1&Lang=en";
       //site ="https://demos.telerik.com/aspnet-ajax/asyncupload/examples/multiplefileselection/defaultcs.aspx";
        a = 0;

        progress = new ProgressDialog(this, R.style.MyTheme);

        iv = (ImageView) this.findViewById(R.id.imageLoading1);
        wv = (WebView) this.findViewById(R.id.webView1);

        setwebviewsetting();

        wv.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (URLUtil.isNetworkUrl(url)) {
                    return false;
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                    startActivity(intent);
                }
                return true;
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                progress.setCancelable(false);
                progress.setProgressStyle(android.R.style.Widget_ProgressBar_Small);
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                progress.show();
                super.onPageStarted(view, url, favicon);
            }

            @Override
            public void onPageFinished(WebView view, String url) {

                progress.dismiss();


                String title = view.getTitle().toString();
                if ("Webpage not available".equals(title)) {
                    openReloadActivity();
                    finish();

                } else {
                    iv.setVisibility(View.GONE);
                    wv.setVisibility(View.VISIBLE);
                }
            }
        });

        String action = "";
        Bundle bn = this.getIntent().getExtras();
        if (bn != null) {
            action = bn.getString("action", "");
            if (!action.isEmpty()) {
                if ("fromReload".equals(action)) {
                    iv.setVisibility(View.GONE);
                    wv.setVisibility(View.VISIBLE);

                    wv.loadUrl(site);
                } else if ("fromNotification".equals(action)) {
                    iv.setVisibility(View.GONE);
                    wv.setVisibility(View.VISIBLE);
                    String link = bn.getString("link", "");
                    wv.loadUrl(link);
                }
            } else {
                iv.setVisibility(View.VISIBLE);
                wv.setVisibility(View.GONE);

                wv.loadUrl(site);
            }
        } else {
            iv.setVisibility(View.VISIBLE);
            wv.setVisibility(View.GONE);

            wv.loadUrl(site);
        }
    }

    public void onBackPressed() {
        if (wv.canGoBack()) {
            a = 0;
            wv.goBack();
        } else {
            // Let the system handle the back button
            if (a == 2)
                super.onBackPressed();
            else {
                a = 2;
                Toast.makeText(getBaseContext(), "Please click again if you eant to exit !", Toast.LENGTH_LONG).show();
            }
        }
    }

    public static boolean isConnected(Context context) {

        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        if (null != cm) {
            NetworkInfo info = cm.getActiveNetworkInfo();

            return (info != null && info.isConnected());
        }
        return false;
    }

    private void openReloadActivity() {
        Intent intent = new Intent(this, ReloadActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("action", "fromMain");
        this.startActivity(intent);
    }


    void setwebviewsetting() {

        if (wv != null) {

            WebSettings webSettings = wv.getSettings();

            wv.getSettings().setJavaScriptEnabled(true);
            wv.getSettings().setLoadsImagesAutomatically(true);
            wv.getSettings().setAppCacheMaxSize(1024 * 1024 * 8);
            wv.getSettings().setAppCacheEnabled(true);
            wv.setWebViewClient(new WebViewClient());
            wv.setWebViewClient(new YourWebClient() );
            wv.setWebChromeClient(new openchooserfile());

            String userAgent = wv.getSettings().getUserAgentString();
            userAgent = userAgent + " 140Application";
            wv.getSettings().setUserAgentString(userAgent);

            wv.addJavascriptInterface(new WebViewMethods(this), "Android");

        }
    }

    public class openchooserfile extends WebChromeClient {

        // file upload callback (Android 2.2 (API level 8) -- Android 2.3 (API level 10)) (hidden method)
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, null);
        }

        // file upload callback (Android 3.0 (API level 11) -- Android 4.0 (API level 15)) (hidden method)
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            openFileChooser(uploadMsg, acceptType, null);
        }

        // file upload callback (Android 4.1 (API level 16) -- Android 4.3 (API level 18)) (hidden method)
        @SuppressWarnings("unused")
        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileInput(uploadMsg, null);
        }

        // file upload callback (Android 5.0 (API level 21) -- current) (public method)
        @SuppressWarnings("all")
        public boolean onShowFileChooser(WebView webView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            openFileInput(null, filePathCallback);
            return true;
        }
    }

    public class YourWebClient extends WebViewClient {

    }
    @SuppressLint("NewApi")
    protected void openFileInput(final ValueCallback<Uri> fileUploadCallbackFirst,
                                 final ValueCallback<Uri[]> fileUploadCallbackSecond) {
        if (mFileUploadCallbackFirst != null) {
            mFileUploadCallbackFirst.onReceiveValue(null);
        }
        mFileUploadCallbackFirst = fileUploadCallbackFirst;
        if (mFileUploadCallbackSecond != null) {
            mFileUploadCallbackSecond.onReceiveValue(null);
        }
        mFileUploadCallbackSecond = fileUploadCallbackSecond;
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        startActivityForResult(Intent.createChooser(i, "Choose a file"),
                FILE_CHOOSER_RESULT_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        if (requestCode == FILE_CHOOSER_RESULT_CODE) {
            if (resultCode == Activity.RESULT_OK) {
                if (intent != null) {
                    if (mFileUploadCallbackFirst != null) {
                        mFileUploadCallbackFirst.onReceiveValue(intent.getData());
                        mFileUploadCallbackFirst = null;
                    } else if (mFileUploadCallbackSecond != null) {
                        Uri[] dataUris;
                        try {
                            dataUris = new Uri[]{Uri.parse(intent.getDataString())};
                        } catch (Exception e) {
                            dataUris = null;
                        }
                        mFileUploadCallbackSecond.onReceiveValue(dataUris);
                        mFileUploadCallbackSecond = null;
                    }
                }
            } else {
                if (mFileUploadCallbackFirst != null) {
                    mFileUploadCallbackFirst.onReceiveValue(null);
                    mFileUploadCallbackFirst = null;
                } else if (mFileUploadCallbackSecond != null) {
                    mFileUploadCallbackSecond.onReceiveValue(null);
                    mFileUploadCallbackSecond = null;
                }
            }
        }
    }
}