package jp.tanikinaapps.shiroiportaltools;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;


import android.view.KeyEvent;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;

import android.view.MenuItem;

import com.google.android.gms.ads.AdView;
import com.google.android.material.navigation.NavigationView;

import androidx.drawerlayout.widget.DrawerLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.view.Menu;
import android.webkit.DownloadListener;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import android.widget.ProgressBar;
import android.widget.Toast;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String SHIROI_TOPPAGE = "http://www.city.shiroi.chiba.jp/i/toppage.html";
    public static final String SHIROI_NEWS = "http://www.city.shiroi.chiba.jp/i/news.html";
    public static final String SHIROI_NEWS_PAPER = "http://www.city.shiroi.chiba.jp/i/shisei/koho/k02/1425802824257.html";
    public static final String SHIROI_GIKAI = "http://www.shiroi-city.stream.jfit.co.jp";
    public static final String SHIROI_MAIL = "http://city-shiroi.jp/nashibo/user/shiroi/blog/showDetail.do";
    public static final String SHIROI_BOUSAI = "http://city-shiroi.jp/nashibo/user/bosai/blog/showDetail.do";

    private WebView varWebView;
    private SharedPreferences sharedPreferences;
    private SwipeRefreshLayout swipeRefreshLayout;
    //お気に入りに登録する用
    private String favoriteTitle;
    private String favoriteUrl;
    //SQLite用
    private FavoriteOpenHelper helper;
    private SQLiteDatabase db;
    //お気に入りとのデータ渡し用
    public static final String EXTRA_MESSAGE = "jp.tanikinaapps.shiroiportaltools.MESSAGE";
    public final int RESULT_SUBACTIVITY = 1000;
    SharedPreferences useOtherAppSetting;

    ProgressBar pBar;
    Toolbar toolbar;
    SetAd setAd;

    private boolean useSmpView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setTheme(R.style.AppTheme_NoActionBar);
        setContentView(R.layout.activity_main);

        layoutSettings();
        webViewSettings();
        swipeRefresh();

        new SetAd(this,(AdView)findViewById(R.id.adView)).AdSetting();

        Intent intent = getIntent();
        if(intent.getStringExtra("newsUrl") != null){
            String newsUrl = intent.getStringExtra("newsUrl");
            loadUrl(newsUrl);
        }
    }

    private void layoutSettings(){
        toolbar = findViewById(R.id.toolbarDiagram);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);
    }

    public void webViewSettings(){
        boolean useExpansion;

        varWebView = findViewById(R.id.webView);
        pBar = findViewById(R.id.progressBar);

        sharedPreferences = getSharedPreferences("SETTINGS_SNS",MODE_PRIVATE);
        useExpansion = sharedPreferences.getBoolean("DataExpansion",false);
        useSmpView = sharedPreferences.getBoolean("DataSmpView",false);

        //拡大縮小機能
        if (useExpansion){
            varWebView.getSettings().setBuiltInZoomControls(true);
        }

        varWebView.getSettings().setJavaScriptEnabled(true);
        varWebView.setWebViewClient(new WebViewClient(){
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon){
                super.onPageStarted(view,url,favicon);

                if(!swipeRefreshLayout.isRefreshing()){
                    pBar.setVisibility(android.widget.ProgressBar.VISIBLE); //プログレスバー表示
                }
            }
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);

                if (pBar.getVisibility() == android.widget.ProgressBar.VISIBLE){
                    pBar.setVisibility(View.GONE);
                }
                if(swipeRefreshLayout.isRefreshing()){
                    swipeRefreshLayout.setRefreshing(false);
                }

            }
            @Override
            public boolean shouldOverrideUrlLoading(WebView view,String url){

                boolean useOtherApp;
                useOtherAppSetting = getSharedPreferences("SETTINGS_SNS",MODE_PRIVATE);
                useOtherApp = useOtherAppSetting.getBoolean("DataSns",false);
                if(url.startsWith("mailto:")){
                    Intent intent = new Intent(Intent.ACTION_SENDTO,Uri.parse(url));
                    startActivity(intent);
                    return true;
                }
                if(url.contains("youtube.com")){
                    Intent i = new Intent();
                    i.setAction(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                    return true;
                }

                if(useOtherApp){
                    if(url.contains("twitter.com") || url.contains("facebook.com") || url.contains("instagram.com") ||url.contains("maps.google.co.jp")){
                        Intent i = new Intent();
                        i.setAction(Intent.ACTION_VIEW);
                        i.setData(Uri.parse(url));
                        startActivity(i);
                        return true;
                    }
                }

                loadUrl(url);
                return true;
            }

        });
        varWebView.getSettings().setLoadWithOverviewMode(true);
        varWebView.getSettings().setUseWideViewPort(true);
        varWebView.loadUrl(SHIROI_TOPPAGE);
        //ダウンロードリスナー
        varWebView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    //戻るボタンを押すと左ドロワー閉じる→ページ戻る→アプリ閉じるの挙動
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK && varWebView.canGoBack()) {
            varWebView.goBack();
            return true;
        } else if(keyCode == KeyEvent.KEYCODE_BACK && !varWebView.canGoBack()){
            appFinish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case R.id.openOtherBrowser:
                Intent i = new Intent();
                i.setAction(Intent.ACTION_VIEW);
                i.setData(Uri.parse(varWebView.getUrl()));
                startActivity(i);
                return true;
            case R.id.goFavorite:
                if (helper == null) {
                    helper = new FavoriteOpenHelper(getApplicationContext());
                }
                if(db == null){
                    db = helper.getWritableDatabase();
                }
                favoriteTitle = varWebView.getTitle();
                favoriteUrl = varWebView.getUrl();

                insertData(db,favoriteTitle,favoriteUrl);
                Toast.makeText(this,"お気に入りに追加しました",Toast.LENGTH_LONG).show();
                return true;
            case R.id.search:
                FragmentManager helpFragmentManager = getSupportFragmentManager();
                DialogFragment searchDialogFragment = new SetDialog.searchDialogFragment(MainActivity.this);
                searchDialogFragment.show(helpFragmentManager,"search");
                break;
            case R.id.finishApp:
                appFinish();
                return true;
            case R.id.refreshApp:
                String urlNow = varWebView.getUrl();
                varWebView.loadUrl(urlNow);
                return true;
            case R.id.shareApp: //共有ボタン
                try {
                    String shareTitle = varWebView.getTitle();
                    String shareUrl = varWebView.getUrl();
                    String shareText = shareTitle + " " + shareUrl + " #白井市";
                    Intent shareButton = new Intent();
                    shareButton.setAction(Intent.ACTION_SEND);
                    shareButton.setType("text/plain");
                    shareButton.putExtra(Intent.EXTRA_TEXT, shareText);
                    startActivity(shareButton);
                } catch (Exception e){
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item){
        switch(item.getItemId()){
            case (R.id.nav_top): //トップページ
                loadUrl(SHIROI_TOPPAGE);
                break;
            case (R.id.nav_favorite): //お気に入り
                Intent intentFavo = new Intent(MainActivity.this,favorite.class);
                String str = varWebView.getUrl();
                intentFavo.putExtra(EXTRA_MESSAGE,str);
                startActivityForResult(intentFavo,RESULT_SUBACTIVITY);
                break;
            case (R.id.nav_news): //新着情報
                loadUrl(SHIROI_NEWS);
                break;
            case (R.id.nav_newspaper): //広報しろい
                loadUrl(SHIROI_NEWS_PAPER);
                break;
            case (R.id.nav_gikai): //議会中継
                loadUrl(SHIROI_GIKAI);
                break;
            case (R.id.nav_mail): //メール配信サービス
                loadUrl(SHIROI_MAIL);
                break;
            case (R.id.nav_bousai): //しろい防災ポータル
                loadUrl(SHIROI_BOUSAI);
                break;
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    //URL Loader
    public void loadUrl(String url){
        final String SHIROI_URL = "http://www.city.shiroi.chiba.jp/i/";
        sharedPreferences = getSharedPreferences("SETTINGS_SNS",MODE_PRIVATE);
        useSmpView = sharedPreferences.getBoolean("DataSmpView",false);

        if(useSmpView){
            if(url.startsWith("http://www.city.shiroi.chiba.jp/material/")){
                varWebView.loadUrl(url);
            } else if(url.contains("shiroi-nashibus")){
                varWebView.loadUrl(url);
            } else if(url.contains("feed.xml")){
                varWebView.loadUrl(url);
            }else if(url.startsWith("http://www.city.shiroi.chiba.jp/ikkrwebBrowse/")){
                varWebView.loadUrl(url);
            } else if(url.startsWith("http://www.city.shiroi.chiba.jp/i/")){
                varWebView.loadUrl(url);
            } else if(url.startsWith("http://www.city.shiroi.chiba.jp/")){
                varWebView.loadUrl(SHIROI_URL + url.substring(32));
            } else{
                varWebView.loadUrl(url);
            }
        } else{
            varWebView.loadUrl(url);
        }
    }

    //ダイアログ出してアプリを閉じるか聞く
    public void appFinish(){
        new AlertDialog.Builder(MainActivity.this)
                .setTitle(R.string.finishAlert)
                .setMessage(R.string.returnMessage)
                .setPositiveButton(R.string.returnYes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.returnNo, null)
                .show();
    }

    private void insertData(SQLiteDatabase db, String title, String url){

        ContentValues values = new ContentValues();
        values.put("pagetitle", title);
        values.put("pageurl", url);

        db.insert("testdb", null, values);
    }

    protected void onActivityResult(int requestCode,int resultCode,Intent intent){
        super.onActivityResult(requestCode,resultCode,intent);
        if(resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY && null != intent){
            String resUrl = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            loadUrl(resUrl);
        }
    }

    //引っ張って更新
    public void swipeRefresh(){
        swipeRefreshLayout = findViewById(R.id.swipeLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                String urlNow = varWebView.getUrl();
                varWebView.loadUrl(urlNow);
            }
        });
    }

    //youtubeバックグラウンド再生を止めるためのコード
    @Override
    public void onResume(){
        super.onResume();
        varWebView.onResume();
    }

    @Override
    public void onPause(){
        super.onPause();
        varWebView.onPause();
    }

    @Override
    public void onDestroy(){
        varWebView.destroy();
        super.onDestroy();
    }

}
