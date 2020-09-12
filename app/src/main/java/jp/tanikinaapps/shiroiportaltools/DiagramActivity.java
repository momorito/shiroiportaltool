package jp.tanikinaapps.shiroiportaltools;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.ads.AdView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import de.psdev.licensesdialog.LicensesDialogFragment;
import de.psdev.licensesdialog.licenses.ApacheSoftwareLicense20;
import de.psdev.licensesdialog.licenses.MITLicense;
import de.psdev.licensesdialog.model.Notice;
import de.psdev.licensesdialog.model.Notices;

public class DiagramActivity extends AppCompatActivity implements View.OnClickListener , TimePickerDialog.OnTimeSetListener{
    SetAd setAd;
    Spinner startSpin,arriveSpin;
    String selectHour,selectMinute;
    ProgressDialog initialDialog;
    String[] busStopName;
    Button startMapButton,arriveMapButton,selectTimeButton,searchDiagram,startArrive;
    private SharedPreferences data;
    private SQLiteDatabase db;
    private DiagramOpenHelper helper;
    boolean isDiagramRead,isStart;
    int searchHour,searchMinute;
    public static final String PASS_DATA = "jp.tanikinaapps.shiroiportaltools.DATA";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);
        Toolbar toolbar = findViewById(R.id.toolbarDiagram);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

        //adMobの設定
        setAd = new SetAd(this,(AdView)findViewById(R.id.adView));
        setAd.AdSetting();

        TextView diagramInst = findViewById(R.id.diagramInst);
        diagramInst.setLinksClickable(true);
        diagramInst.setText(Html.fromHtml("<a href=\"http://www.city.shiroi.chiba.jp/kurashi/norimono/nashigo/index.html\">※時刻表や運行状況等については、市の公式ホームページを確認してください。<a/>" ));
        diagramInst.setMovementMethod((LinkMovementMethod.getInstance()));

        data = getSharedPreferences("SETTINGS_SNS",MODE_PRIVATE);
        isDiagramRead = data.getBoolean("isDiagramRead",false);
        if(!isDiagramRead){


            initialDialog = new ProgressDialog(this);
            initialDialog.setTitle("初期処理中");
            initialDialog.setMessage("時刻表を読み込んでいます（この処理には数分かかることがあります）");
            initialDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            initialDialog.show();

            AsyncDiagram diagram = new AsyncDiagram(getApplicationContext());
            diagram.setListener(createListener());
            diagram.execute(0);
        }

        Calendar c = Calendar.getInstance();
        searchHour = c.get(Calendar.HOUR_OF_DAY);
        searchMinute = c.get(Calendar.MINUTE);

        setSpinner();
        setButtons();

        searchDiagram = findViewById(R.id.searchDiagram);
        searchDiagram.setOnClickListener(this);

        isStart = true;

    }

    private AsyncDiagram.Listener createListener(){
        return new AsyncDiagram.Listener() {
            @Override
            public void onSuccess() {
                initialDialog.dismiss();
                SharedPreferences.Editor editor = data.edit();
                editor.putBoolean("isDiagramRead",true);
                editor.apply();
                Toast.makeText(getApplicationContext(),"初期処理完了",Toast.LENGTH_SHORT).show();
            }
        };
    }
    public void setSpinner(){

        List<String> list = new ArrayList<>();

        startSpin = (Spinner)findViewById(R.id.startSpin);
        arriveSpin =(Spinner)findViewById(R.id.arriveSpin);
        CsvReader.BusStopList busStopList = new CsvReader.BusStopList();
        list = busStopList.reader(getApplicationContext(),0);

        busStopName = new String[list.size()];

        for(int i=0;i<list.size();i++){
            busStopName[i] = list.get(i);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,busStopName);
        startSpin.setAdapter(adapter);
        arriveSpin.setAdapter(adapter);

    }

    private void setButtons(){
        startMapButton = findViewById(R.id.startMapButton);
        startMapButton.setOnClickListener(this);
        arriveMapButton = findViewById(R.id.arriveMapButton);
        arriveMapButton.setOnClickListener(this);
        selectTimeButton  = findViewById(R.id.selectTimeButton);
        selectTimeButton.setOnClickListener(this);
        startArrive = findViewById(R.id.startArrive);
        startArrive.setOnClickListener(this);



    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            case R.id.reloadDiagram:
                initialDialog = new ProgressDialog(this);
                initialDialog.setTitle("再読み込み");
                initialDialog.setMessage("時刻表を読み込んでいます（この処理には数分かかることがあります）");
                initialDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                initialDialog.show();

                AsyncDiagram diagram = new AsyncDiagram(getApplicationContext());
                diagram.setListener(createListener());
                diagram.execute(1);
                break;
            case android.R.id.home:
                finish();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private AsyncDiaSearchR.Listener createListenerAgainR(){
        return new AsyncDiaSearchR.Listener() {
            @Override
            public void onSuccess(String[] result) {
                searchDiagram.setEnabled(true);
                FragmentManager diagramFragmentManager = getSupportFragmentManager();
                if(result != null){
                    DialogFragment diagramFragment = new SetDialog.DiagramFragment(result,"到着検索");
                    diagramFragment.show(diagramFragmentManager,"diagram");
                    Log.d("進捗","渡されたresultのlengthは" + result.length);
                } else{
                    DialogFragment diagramFragment = new SetDialog.DiagramFragment(new String[]{"要素なし"},"到着検索");
                    diagramFragment.show(diagramFragmentManager,"diagram");
                    Log.d("進捗","便は存在しない");
                }
            }
        };
    }

    private AsyncDiaSearch.Listener createListenerAgain(){
        return new AsyncDiaSearch.Listener() {
            @Override
            public void onSuccess(String[] result) {
                searchDiagram.setEnabled(true);
                FragmentManager diagramFragmentManager = getSupportFragmentManager();
                if(result != null){
                    DialogFragment diagramFragment = new SetDialog.DiagramFragment(result,"出発検索");
                    diagramFragment.show(diagramFragmentManager,"diagram");
                    Log.d("進捗","渡されたresultのlengthは" + result.length);
                } else{
                    DialogFragment diagramFragment = new SetDialog.DiagramFragment(new String[]{"要素なし"},"出発検索");
                    diagramFragment.show(diagramFragmentManager,"diagram");
                    Log.d("進捗","便は存在しない");
                }

            }

        };
    }
    @Override
    public void onClick(View v) {
        Intent intent = new Intent(getApplicationContext(),MapsActivity.class);
        switch (v.getId()){
            case R.id.startMapButton:
                intent.putExtra(PASS_DATA,"start");
                startActivityForResult(intent,1000);
                break;
            case R.id.arriveMapButton:
                intent.putExtra(PASS_DATA,"arrive");
                startActivityForResult(intent,2000);
                break;
            case R.id.selectTimeButton:
                DialogFragment newFragment = new TimePick();
                newFragment.show(getSupportFragmentManager(), "timePicker");
                break;
            case R.id.searchDiagram:
                String startPoint = startSpin.getSelectedItem().toString();
                String arrivePoint = arriveSpin.getSelectedItem().toString();

                if(startPoint.equals(arrivePoint)){
                    Toast.makeText(getApplicationContext(),"出発地点と到着地点が同じです！",Toast.LENGTH_SHORT).show();
                } else{
                    searchDiagram.setEnabled(false);
                    if(isStart){
                        AsyncDiaSearch diaSearch = new AsyncDiaSearch(getApplicationContext(),startPoint,arrivePoint,searchHour,searchMinute);
                        diaSearch.setListener(createListenerAgain());
                        diaSearch.execute(0);
                    } else{
                        AsyncDiaSearchR diaSearch = new AsyncDiaSearchR(getApplicationContext(),startPoint,arrivePoint,searchHour,searchMinute);
                        diaSearch.setListener(createListenerAgainR());
                        diaSearch.execute(0);
                    }

                }
                break;
            case R.id.startArrive:
                String[] items ={"出発","到着"};
                new AlertDialog.Builder(DiagramActivity.this)
                        .setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch(which){
                                    case 0:
                                        isStart = true;
                                        startArrive.setText("出発");
                                        break;
                                    case 1:
                                        isStart = false;
                                        startArrive.setText("到着");
                                        break;
                                }
                            }
                        })
                        .show();

                break;
        }
    }


    protected void onActivityResult(int requestCode,int resultCode,Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        String result;

        if(resultCode == RESULT_OK && requestCode == 1000 && null != intent){
            result = intent.getStringExtra(PASS_DATA);
            for (int i =0;i<busStopName.length;i++){
                if(result.equals(busStopName[i])){
                    startSpin.setSelection(i);
                }
            }
        } else if(resultCode == RESULT_OK && requestCode == 2000 && null != intent){
            result = intent.getStringExtra(PASS_DATA);
            for (int i =0;i<busStopName.length;i++){
                if(result.equals(busStopName[i])){
                    arriveSpin.setSelection(i);
                }
            }
        }
    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        searchHour = hourOfDay;
        searchMinute = minute;
        selectHour= String.format(Locale.US, "%d", hourOfDay);

        if(minute == 0){
            selectMinute = "00";
        } else if(minute < 10){
            selectMinute = "0" + String.format(Locale.US,"%d",minute);
        } else {
            selectMinute = String.format(Locale.US,"%d",minute);
        }
        selectTimeButton.setText(selectHour +":"+selectMinute);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_diagram,menu);
        return true;
    }

}