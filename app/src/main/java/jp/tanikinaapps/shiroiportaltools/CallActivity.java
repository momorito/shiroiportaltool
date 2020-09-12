package jp.tanikinaapps.shiroiportaltools;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.telecom.Call;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CallActivity extends AppCompatActivity {
    Toolbar toolbar;
    String[] callName;
    String[] callNum;
    String[] callWhere;
    SetAd setAd;
    List<Map<String, String>> data;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme_Intro);
        setContentView(R.layout.activity_call);

        toolbar = (Toolbar)findViewById(R.id.toolbarCall);
        toolbar.setTitle(R.string.CallActivity_name);
        setSupportActionBar(toolbar);
        // Backボタンを有効にする
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //AdMobの設定
        setAd = new SetAd(this,(AdView)findViewById(R.id.adView));
        setAd.AdSetting();

        TypedArray typedArray = getResources().obtainTypedArray(R.array.call_list);
        int length = typedArray.length();

        callName = new String[length];
        callNum = new String[length];
        callWhere = new String[length];

        for(int i=0;i<length;i++){
            int resourceId = typedArray.getResourceId(i,0);
            String[] array = getResources().getStringArray(resourceId);
            callName[i] = array[0];
            callNum[i]=array[1];
            callWhere[i]=array[2];
        }

        // ListViewに表示するリスト項目をArrayListで準備する
        data = new ArrayList<Map<String, String>>();
        for (int i=0; i<callName.length; i++){
            Map<String, String> item = new HashMap<String, String>();
            item.put("callName", callName[i]);
            item.put("callNum", callNum[i]);
            item.put("callWhere",callWhere[i]);
            data.add(item);
        }

        // リスト項目とListViewを対応付けるArrayAdapterを用意する
        BaseAdapter adapter = new CallAdapter(getApplicationContext(),R.layout.list_call,data);
        // ListViewにArrayAdapterを設定する
        ListView listView = (ListView)findViewById(R.id.listTel);
        listView.setAdapter(adapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Uri uri = Uri.parse("tel:" + callNum[position]);
                Intent i = new Intent(Intent.ACTION_DIAL,uri);
                startActivity(i);
            }
        });

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_call, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.callHelp:
                Toast.makeText(getApplicationContext(),"タップして電話画面に移動します",Toast.LENGTH_SHORT).show();
                break;
            case R.id.callSearch:
                FragmentManager helpFragmentManager = getSupportFragmentManager();
                DialogFragment callDialogFragment = new SetDialog.callDialogFragment(data);
                callDialogFragment.show(helpFragmentManager,"call");
                break;
            case android.R.id.home:
                finish();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
