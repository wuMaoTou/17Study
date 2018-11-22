package com.maotou.mtvideo;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebResourceRequest;
import android.widget.ProgressBar;
import android.widget.Toast;

import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    private WebView mWebView;
    private static final String HOME_URL = "http://go.uc.cn/page/subpage/shipin?uc_param_str=dnfrpfbivecpbtntla";
    private static final String TENCENT_URL = "https://v.qq.com";
    private static final String AIQIYI_URL = "https://www.iqiyi.com";
    private static final String YOUKU_URL = "http://www.youku.com";
    private static final String MGTV_URL = "https://www.mgtv.com";
    private ProgressBar mProgressBar;
    private AlertDialog exitDialog;
    private boolean needClearHistory;
    private String mCurrentUrl = HOME_URL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        mProgressBar = findViewById(R.id.progressBar1);
        mWebView = findViewById(R.id.webview);
        initWebView();
    }

    private void initWebView() {
        WebSettings webSetting = mWebView.getSettings();
        webSetting.setJavaScriptEnabled(true);
        webSetting.setLoadWithOverviewMode(true);
        webSetting.setJavaScriptCanOpenWindowsAutomatically(true);
        webSetting.setAllowFileAccess(true);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
            webSetting.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);
        }
        webSetting.setBlockNetworkImage(false);
        webSetting.setSupportZoom(true);
        webSetting.setBuiltInZoomControls(true);
        webSetting.setUseWideViewPort(true);
        webSetting.setAppCacheEnabled(true);
        webSetting.setDomStorageEnabled(true);
        webSetting.setAppCacheMaxSize(Long.MAX_VALUE);
        webSetting.setCacheMode(WebSettings.LOAD_NO_CACHE);
        mWebView.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                if (newProgress == 100) {
                    // 网页加载完成
                    mProgressBar.setVisibility(View.GONE);
                } else {
                    // 加载中
                    mProgressBar.setVisibility(View.VISIBLE);
                    mProgressBar.setProgress(newProgress);
                }
                super.onProgressChanged(view, newProgress);
            }
        });

        mWebView.setWebViewClient(new WebViewClient() {
            @Override
            public void doUpdateVisitedHistory(WebView view, String url, boolean isReload) {
                if (needClearHistory) {
                    view.clearHistory();
                    needClearHistory = false;
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                Log.d("Main-shouldOverrideUrl", url);
                mCurrentUrl = url;
                return super.shouldOverrideUrlLoading(view, url);
            }

            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                String url = request.getUrl().toString();
                Log.d("Main-shouldOverrideUrl", "L---" + url);
                mCurrentUrl = url;
                return super.shouldOverrideUrlLoading(view, request);
            }
        });
        mWebView.loadUrl(HOME_URL);
    }

    @Override
    public void onBackPressed() {
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        } else {
            if (mWebView != null && mWebView.canGoBack()) {
                mWebView.goBack();
            } else {
                if (exitDialog == null) {
                    initDialog();
                }
                exitDialog.show();
            }
        }
    }

    private void initDialog() {
        exitDialog = new AlertDialog.Builder(MainActivity.this)
                .setMessage("是否要退出？")
                .setPositiveButton("是", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                        System.exit(0);
                    }
                })
                .setNegativeButton("否", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .create();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        needClearHistory = true;
        if (id == R.id.nav_home) {
            mWebView.loadUrl(HOME_URL);
        } else if (id == R.id.nav_tencent) {
            mWebView.loadUrl(TENCENT_URL);
        } else if (id == R.id.nav_aiqiyi) {
            mWebView.loadUrl(AIQIYI_URL);
        } else if (id == R.id.nav_youku) {
            mWebView.loadUrl(YOUKU_URL);
        } else if (id == R.id.nav_mgtv) {
            mWebView.loadUrl(MGTV_URL);
        }
        mWebView.scrollTo(0, 0);
        if (mDrawer != null && mDrawer.isDrawerOpen(GravityCompat.START)) {
            mDrawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.right_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_refresh && mWebView != null) {
            mWebView.reload();
            return true;
        } else if (id == R.id.action_vip) {
            Intent intent = new Intent(this, VipPlayActivity.class);
            intent.putExtra(VipPlayActivity.URL_KEY, mCurrentUrl);
            startActivity(intent);
        } else if (id == R.id.action_copy) {
            ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(mCurrentUrl);
            Toast.makeText(this,"复制成功",Toast.LENGTH_SHORT).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWebView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mWebView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWebView.destroy();
    }
}
