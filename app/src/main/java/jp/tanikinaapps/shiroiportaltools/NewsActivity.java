package jp.tanikinaapps.shiroiportaltools;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.List;
import java.util.Map;

public class NewsActivity extends AppCompatActivity {
    Toolbar toolbar;
    AsyncNews asyncNews;
    List<Map<String, String>> data;
    int executeCount;
    SwipeRefreshLayout swipeRefreshLayout;
    String accessLongUrl;
    SetAd setAd;
    boolean[] checkNew;

    private NewsOpenHelper helper;
    private SQLiteDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Intro);
        setContentView(R.layout.activity_news);

        swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeLayout);

        toolbar = (Toolbar)findViewById(R.id.toolbarNews);
        toolbar.setTitle(R.string.NowLoading);
        setSupportActionBar(toolbar);
        // Backボタンを有効にする
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //AdMobの設定
        setAd = new SetAd(this,(AdView)findViewById(R.id.adView));
        setAd.AdSetting();

        executeCount = 0;
        swipeRefresh();
        doExecute(executeCount);
    }
    private AsyncNews.Listener createListener() {
        return new AsyncNews.Listener() {
            @Override
            public void onSuccess(final List<Map<String, String>> data) {
                setData(data);
                final boolean[] checkNew = checkData(data);
                setCheckNew(checkNew);
                BaseAdapter adapter = new NewsAdapter(getApplicationContext(),R.layout.list_news_layout,data,checkNew);
                // ListViewにArrayAdapterを設定する
                ListView listView = (ListView)findViewById(R.id.listNews);
                registerForContextMenu(listView);
                listView.setAdapter(adapter);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String accessUrl = data.get(position).get("pageUrlString");
                        final String SHIROI_URL = "http://www.city.shiroi.chiba.jp/i/";
                        Intent intent = new Intent(getApplication(),MainActivity.class);
                        intent.putExtra("newsUrl",SHIROI_URL + accessUrl.substring(32));
                        startActivity(intent);

                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {

                        final String SHIROI_URL = "http://www.city.shiroi.chiba.jp/i/";
                        final String[] items = {"このアプリで開く", "外部ブラウザで開く",};
                        accessLongUrl = data.get(position).get("pageUrlString");
                        new AlertDialog.Builder(NewsActivity.this)
                                .setTitle("開く")
                                .setItems(items, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        switch(which){
                                            case 0:
                                                Intent intent = new Intent(getApplication(),MainActivity.class);
                                                intent.putExtra("newsUrl",SHIROI_URL + accessLongUrl.substring(32));
                                                startActivity(intent);
                                                break;
                                            case 1:
                                                Intent i = new Intent();
                                                i.setAction(Intent.ACTION_VIEW);
                                                i.setData(Uri.parse(SHIROI_URL + accessLongUrl.substring(32)));
                                                startActivity(i);
                                                break;
                                        }
                                    }
                                })
                                .show();

                        return true;
                    }
                });

                toolbar.setTitle(R.string.NewsActivityLabel);
                swipeRefreshLayout.setRefreshing(false);
            }
        };

    }

    private void doExecute(int executeCount){
        asyncNews = new AsyncNews(data);
        asyncNews.setListener(createListener());
        asyncNews.execute(executeCount);
        executeCount++;
    }

    private void swipeRefresh(){

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                toolbar.setTitle(R.string.NowLoading);
                doExecute(executeCount);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_intro_option,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.newsHelp:
                Toast.makeText(getApplicationContext(),"長押しで開くブラウザを選べます",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setData(List<Map<String, String>> data){
        this.data = data;
    }
    public void setCheckNew(boolean[] checkNew){this.checkNew = checkNew;}

    public boolean[] checkData(List<Map<String, String>> data){
        boolean[] checkNew = new boolean[data.size()];

        for(int i =0;i<data.size();i++){
            checkNew[i] = false;
        }
            return checkNew;

    }

    public String booleanToString(boolean checkNew){
        String check;
        if(checkNew){
            check = "1";
        } else{
            check = "0";
        }
        return check;
    }

    @Override
    public void onStop(){
        /*
        if(helper == null){
            helper = new NewsOpenHelper(getApplicationContext());
        }
        if(db == null){
            db = helper.getWritableDatabase();
        }

        db.delete("newsdb",null,null);

        ContentValues values = new ContentValues();
        for(int i=0;i<data.size();i++){
            values.put("pagetitle",data.get(i).get("pageTitleString"));
            values.put("pageupdate",data.get(i).get("pageDateString"));
            values.put("pageischecked",booleanToString(checkNew[i]));
            db.insert("newsdb",null,values);
        }

         */

        super.onStop();

    }


    }

