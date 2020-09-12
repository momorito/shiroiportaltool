package jp.tanikinaapps.shiroiportaltools;


import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button button;
    List<String> listName,listIdo,listKeido,listUrl;
    String mapMode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent intent = getIntent();
        mapMode = intent.getStringExtra(DiagramActivity.PASS_DATA);
        if(mapMode == null){
            mapMode = "search";
        }
        Log.d("進捗",mapMode);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);




    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        listName = new ArrayList<>();
        mMap = googleMap;
        double latitude,longitude;

        latitude =35.797092;
        longitude = 140.06147;

        ModeSetting();



        CsvReader.BusStopList csvReader = new CsvReader.BusStopList();
        listName = csvReader.reader(getApplicationContext(),0); //バス停名
        listKeido= csvReader.reader(getApplicationContext(),1); //経度
        listIdo = csvReader.reader(getApplicationContext(),2);  //緯度
        listUrl = csvReader.reader(getApplicationContext(),3);  //時刻表ホームページアドレス


        // Add a marker in Sydney and move the camera
        for(int i =0;i<listName.size();i++){
            mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(listIdo.get(i)),Double.parseDouble(listKeido.get(i))))
                                                .title(listName.get(i))
                                                .icon(setIcon()))
                                                .setTag(listUrl.get(i));
        }

        LatLng shiroi = new LatLng(latitude, longitude);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(shiroi));
        zoomMap(latitude,longitude,0.0003);

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Intent intent;
                switch(mapMode){
                    case "start":
                    case "arrive":
                        intent = new Intent();
                        intent.putExtra(DiagramActivity.PASS_DATA,marker.getTitle());
                        setResult(RESULT_OK,intent);
                        finish();
                        break;
                    case "search":
                        intent = new Intent(getApplication(),MainActivity.class);
                        intent.putExtra("newsUrl",marker.getTag().toString());
                        startActivity(intent);
                        break;
                }

            }
        });

        button = (Button)findViewById(R.id.mapMenuButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager helpFragmentManager = getSupportFragmentManager();
                DialogFragment mapDialogFragment = new SetDialog.mapSearchFragment(listName);
                mapDialogFragment.show(helpFragmentManager,"mapSearch");

            }
        });


    }
    public void zoomMap(double latitude, double longitude,double num){
        // 表示する東西南北の緯度経度を設定
        double south = latitude * (1-num);
        double west = longitude * (1-num);
        double north = latitude * (1+num);
        double east = longitude * (1+num);

        // LatLngBounds (LatLng southwest, LatLng northeast)
        LatLngBounds bounds = LatLngBounds.builder()
                .include(new LatLng(south , west))
                .include(new LatLng(north, east))
                .build();

        int width = getResources().getDisplayMetrics().widthPixels;
        int height = getResources().getDisplayMetrics().heightPixels;

        // static CameraUpdate.newLatLngBounds(LatLngBounds bounds, int width, int height, int padding)
        mMap.moveCamera(CameraUpdateFactory.
                newLatLngBounds(bounds, width, height, 0));

    }

    public void zoomResult(String result){
        double num = 0.00001;
        for(int i =0;i<listName.size();i++){
            if(result == listName.get(i)){
                zoomMap(Double.parseDouble(listIdo.get(i)),Double.parseDouble(listKeido.get(i)),num);
            }
        }
    }

    private BitmapDescriptor setIcon(){
        BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.bustop);
        return descriptor;
    }

    private void ModeSetting(){
        TextView mapInst = findViewById(R.id.mapInst);
        switch (mapMode){
            case "start":
                mapInst.setText(R.string.bus_inst_start);
                break;
            case "arrive":
                mapInst.setText(R.string.bus_inst_arrive);
                break;
            case "search":
                mapInst.setText(R.string.bus_inst_search);
                break;
        }
    }

}