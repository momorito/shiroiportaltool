package jp.tanikinaapps.shiroiportaltools;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;

public class Settings extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        //右上戻るボタンを有効にした
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);

        ListView listView = findViewById(R.id.settingList);
        BaseAdapter adapter = new SettingAdapter(getApplicationContext(),R.layout.list_setting);
        listView.setAdapter(adapter);
    }



    //左上戻るボタンの挙動
    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch(item.getItemId()){
            case android.R.id.home:
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

}
