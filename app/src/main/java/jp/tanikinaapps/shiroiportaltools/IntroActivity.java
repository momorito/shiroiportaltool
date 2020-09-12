package jp.tanikinaapps.shiroiportaltools;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.res.TypedArray;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.google.android.gms.ads.AdView;

import com.google.firebase.analytics.FirebaseAnalytics;

import java.util.List;
import java.util.Map;

import de.psdev.licensesdialog.LicensesDialogFragment;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class IntroActivity extends AppCompatActivity implements View.OnTouchListener,View.OnClickListener {
    ImageButton buttonHp,buttonNews,buttonTel,buttonBusStop,buttonTwitter,buttonFb,buttonInsta,buttonYoutube;
    Toolbar toolbar;
    SharedPreferences AccessSns;
    private FirebaseAnalytics mFirebaseAnalytics;
    private List<Map<String, String>> data;
    private initialProcess initPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
        }
        setTheme(R.style.AppTheme_Intro);
        setContentView(R.layout.activity_intro);

        toolbar = (Toolbar)findViewById(R.id.toolbarCall);
        setSupportActionBar(toolbar);
        toolbar.setTitle(R.string.app_name);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        //adMobの設定
        new SetAd(this,(AdView)findViewById(R.id.adView)).AdSetting();

        initPro = new initialProcess(data);
        initPro.setListener(createListener());
        initPro.execute(0);

        buttonSetUp();

    }
    @Override
    public boolean onTouch(View v,MotionEvent event){
        float buttonOn = 0.95f;
        float buttonOff = 1.00f;

        switch(event.getAction()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                v.setScaleY(buttonOn);
                v.setScaleX(buttonOn);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                v.setScaleY(buttonOff);
                v.setScaleX(buttonOff);
                break;
        }
        return false;
    }

    @Override
    public void onClick(View v){
        Intent i;
        switch(v.getId()){
            case R.id.buttonHp:
                i = new Intent(IntroActivity.this,MainActivity.class);
                startActivity(i);
                break;
            case R.id.buttonNews:
                i = new Intent(IntroActivity.this,NewsActivity.class);
                startActivity(i);
                break;
            case R.id.bus:
                String[] items ={"時刻表検索","バス停検索"};
                new AlertDialog.Builder(IntroActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i;
                                switch(which){
                                    case 0:
                                        i = new Intent(IntroActivity.this,DiagramActivity.class);
                                        startActivity(i);
                                        break;
                                    case 1:
                                        i = new Intent(IntroActivity.this,MapsActivity.class);
                                        startActivity(i);
                                        break;
                                }
                            }
                        })
                        .show();
                break;
            case R.id.buttonTel:
                i = new Intent(IntroActivity.this,CallActivity.class);
                startActivity(i);
                break;
            case R.id.buttonTwitter:
                String[] items2 ={"なし坊ツイッター","白井市公式ツイッター"};
                new AlertDialog.Builder(IntroActivity.this)
                        .setItems(items2, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent i = new Intent();
                                i.setAction(Intent.ACTION_VIEW);
                                switch(which){
                                    case 0:
                                        snsSelector(R.id.buttonTwitter,"https://twitter.com/nashibo_shiroi");
                                        break;
                                    case 1:
                                        snsSelector(R.id.buttonTwitter,"https://twitter.com/shiroi_city");
                                        break;
                                }
                            }
                        }).show();
                break;
            case R.id.buttonFb:
                snsSelector(R.id.buttonFb,null);
                break;
            case R.id.buttonInsta:
                snsSelector(R.id.buttonInsta,null);
                break;
            case R.id.buttonYoutube:
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.setData(Uri.parse("https://www.youtube.com/channel/UC5O21usiaJUMHwvSuAm1Wng"));
                startActivity(intent);
                break;
        }
        scaleChange(v);
    }

    private void scaleChange(View v){
        v.setScaleY(1.00f);
        v.setScaleX(1.00f);
    }

    private void buttonSetUp(){
        buttonHp = (ImageButton)findViewById(R.id.buttonHp);
        buttonHp.setOnClickListener(this);
        buttonHp.setOnTouchListener(this);

        buttonNews = (ImageButton)findViewById(R.id.buttonNews);
        buttonNews.setOnClickListener(this);
        buttonNews.setOnTouchListener(this);

        buttonBusStop = (ImageButton)findViewById(R.id.bus);
        buttonBusStop.setOnClickListener(this);
        buttonBusStop.setOnTouchListener(this);

        buttonTel = (ImageButton)findViewById(R.id.buttonTel);
        buttonTel.setOnClickListener(this);
        buttonTel.setOnTouchListener(this);

        buttonTwitter = (ImageButton)findViewById(R.id.buttonTwitter);
        buttonTwitter.setOnClickListener(this);
        buttonTwitter.setOnTouchListener(this);

        buttonFb = (ImageButton)findViewById(R.id.buttonFb);
        buttonFb.setOnClickListener(this);
        buttonFb.setOnTouchListener(this);

        buttonInsta = (ImageButton)findViewById(R.id.buttonInsta);
        buttonInsta.setOnClickListener(this);
        buttonInsta.setOnTouchListener(this);

        buttonYoutube= (ImageButton)findViewById(R.id.buttonYoutube);
        buttonYoutube.setOnClickListener(this);
        buttonYoutube.setOnTouchListener(this);
    }

    private void snsSelector(int buttonId,String url){
        boolean useOtherApp;
        AccessSns = getSharedPreferences("SETTINGS_SNS",MODE_PRIVATE);
        useOtherApp = AccessSns.getBoolean("DataSns",false);

        if(useOtherApp){
            Intent i = new Intent();
            i.setAction(Intent.ACTION_VIEW);
            switch(buttonId){
                case R.id.buttonTwitter:
                    i.setData(Uri.parse(url));
                    break;
                case R.id.buttonFb:
                    i.setData(Uri.parse("https://ja-jp.facebook.com/shiroinomiryoku/"));
                    break;
                case R.id.buttonInsta:
                    i.setData(Uri.parse("https://www.instagram.com/kaori.shiroi/"));
                    break;
            }
            startActivity(i);

        } else{
            Intent intent;
            intent = new Intent(getApplication(),MainActivity.class);
            switch(buttonId){
                case R.id.buttonTwitter:
                    intent.putExtra("newsUrl",url);
                    break;
                case R.id.buttonFb:
                    intent.putExtra("newsUrl","https://ja-jp.facebook.com/shiroinomiryoku/");
                    break;
                case R.id.buttonInsta:
                    intent.putExtra("newsUrl","https://www.instagram.com/kaori.shiroi/");
                    break;
            }
            startActivity(intent);
        }

    }

    private initialProcess.Listener createListener(){
        return new initialProcess.Listener() {
            @Override
            public void onSuccess(List<Map<String, String>> data) {
                int totalHeight = 0;
                String[] covidTitle = new String[data.size()];
                final String[] covidUrl = new String[data.size()];

                for(int i=0;i<data.size();i++){
                    covidTitle[i] = data.get(i).get("title");
                }
                for(int i=0;i<data.size();i++){
                    covidUrl[i] = data.get(i).get("url");
                }

                ArrayAdapter<String> arrayAdapter =
                        new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, covidTitle);

                ListView listView = (ListView)findViewById(R.id.listCovid);
                registerForContextMenu(listView);
                listView.setAdapter(arrayAdapter);

                for(int i =0;i<arrayAdapter.getCount();i++){
                    View listItem = arrayAdapter.getView(i,null,listView);
                    listItem.measure(0,0);
                    totalHeight += listItem.getMeasuredHeight();
                }

                ViewGroup.LayoutParams params = listView.getLayoutParams();
                params.height = totalHeight + (listView.getDividerHeight()*(arrayAdapter.getCount() - 1));
                listView.setLayoutParams(params);

                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        String accessUrl = covidUrl[position];
                        final String SHIROI_URL = "http://www.city.shiroi.chiba.jp/i/";

                        Intent intent;
                        intent = new Intent(getApplication(),MainActivity.class);
                        intent.putExtra("newsUrl",SHIROI_URL + accessUrl.substring(32));
                        startActivity(intent);
                    }
                });
            }
        };
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_intro,menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case R.id.aboutApp:
                String version = null;

                try {
                    String packageName = getPackageName();
                    PackageInfo packageInfo = getPackageManager().getPackageInfo(packageName, 0);
                    version = packageInfo.versionName;
                } catch(Exception e){
                    e.printStackTrace();
                }

                FragmentManager helpFragmentManager = getSupportFragmentManager();
                DialogFragment helpDialogFragment = new SetDialog.helpDialogFragment(version);
                helpDialogFragment.show(helpFragmentManager,"help");
                return true;
            case R.id.Settings:
                Intent intentSettings = new Intent(IntroActivity.this,Settings.class);
                startActivity(intentSettings);
                return true;
            case R.id.openSourceLicense:
                final Notices notices = new Notices();
                notices.addNotice(new Notice("jsoup Java HTML Parser", "https://jsoup.org/", "Copyright  2009– Jonathan Hedley", new MITLicense()));
                notices.addNotice(new Notice("Maps SDK for Android Utility Library","https://github.com/googlemaps/android-maps-utils","Copyright",new ApacheSoftwareLicense20()));

                final LicensesDialogFragment fragment = new LicensesDialogFragment.Builder(getApplicationContext())
                        .setNotices(notices)
                        .setShowFullLicenseText(false)
                        .setIncludeOwnLicense(true)
                        .build();

                fragment.show(getSupportFragmentManager(), null);
                break;
            case R.id.finishApp:
                new AlertDialog.Builder(IntroActivity.this)
                        .setTitle(R.string.finishAlert)
                        .setMessage(R.string.returnMessage_end)
                        .setPositiveButton(R.string.returnYes, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        })
                        .setNegativeButton(R.string.returnNo, null)
                        .show();
        }
        return super.onOptionsItemSelected(item);
    }

}
