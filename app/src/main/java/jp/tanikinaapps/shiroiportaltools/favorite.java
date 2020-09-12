package jp.tanikinaapps.shiroiportaltools;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.jsoup.Connection;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class favorite extends AppCompatActivity {

    private FavoriteOpenHelper helper;
    private SQLiteDatabase db;

    String[] pageTitles;
    String[] pageUrls;
    String[] pageId;
    String deleteId;

    //お気に入りとのデータ渡し用
    private String message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite);

        Intent intent = getIntent();
        message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);

        //右上戻るボタンを有効にした
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        //SQLiteを読み込む
        readData();


    }

    private void readData() {
        if (helper == null) {
            helper = new FavoriteOpenHelper(getApplicationContext());
        }

        if (db == null) {
            db = helper.getReadableDatabase();
        }

        Cursor cursor = db.query(
                "testdb",
                new String[]{"_id","pagetitle", "pageurl"},
                null,
                null,
                null,
                null,
                null
        );

        // リスト項目のもととなる値を準備する
        pageId = new String[cursor.getCount()];
        pageTitles = new String[cursor.getCount()];
        pageUrls = new String[cursor.getCount()];

        cursor.moveToFirst();

        for (int i = 0; i <cursor.getCount(); i++){
            pageId[i] = cursor.getString(0);
            pageTitles[i] = cursor.getString(1);
            pageUrls[i] = cursor.getString(2);
            cursor.moveToNext();
        }
        cursor.close();


        // ListViewに表示するリスト項目をArrayListで準備する
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();
        for (int i=0; i<pageTitles.length; i++){
            Map<String, String> item = new HashMap<String, String>();
            item.put("pageId",pageId[i]);
            item.put("pageTitle", pageTitles[i]);
            item.put("pageUrl", pageUrls[i]);
            data.add(item);
        }

        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        BaseAdapter adapter = new FavoriteAdapter(getApplicationContext(),R.layout.list_favorite,data);

        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);

        Toast.makeText(getApplicationContext(),"登録件数：" + data.size() + "件",Toast.LENGTH_SHORT).show();

        //リストがタップされたときの挙動
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                String jumpUrl = pageUrls[position];
                Intent intent = new Intent();
                intent.putExtra(MainActivity.EXTRA_MESSAGE,jumpUrl);
                setResult(RESULT_OK,intent);
                finish();
            }
        });

        //リストがロングタップされたときの挙動
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
                deleteId = ((TextView)view.findViewById(R.id.id)).getText().toString();

                new AlertDialog.Builder(favorite.this)
                        .setTitle(R.string.favoriteClearCheck)
                        .setMessage("「" + pageTitles[position] + "」" + " を削除しますか？")
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                db.delete("testdb","_id = \"" + deleteId + "\"",null);
                                readData();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                            }
                        })
                        .show();
                return true;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_favorite, menu);
        return true;
    }

    //左上戻るボタンの挙動
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case R.id.favoriteHelp:
                Toast.makeText(getApplicationContext(),"長押しで削除できます",Toast.LENGTH_SHORT).show();
                break;
            case android.R.id.home:
                finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
