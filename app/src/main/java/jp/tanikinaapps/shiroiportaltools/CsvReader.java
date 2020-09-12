package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class CsvReader {

    public static class BusStopList{
        public List<String> reader(Context context,Integer i){
            AssetManager assetManager = context.getResources().getAssets();
            List<String> listName;
            listName = new ArrayList<>();

            try{
                InputStream inputStream = assetManager.open("busstoplist.csv");

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;

                while ((line = bufferedReader.readLine()) != null){
                    String[] RowData = line.split(",");
                    listName.add(RowData[i]);

                }
                bufferedReader.close();

            } catch(IOException e){
                Log.d("進捗","失敗している");
                e.printStackTrace();
            }

            return listName;
        }
    }

    public static class DiaList{
        public List<String> reader(Context context,Integer i){
            AssetManager assetManager = context.getResources().getAssets();
            List<String> listName;
            listName = new ArrayList<>();

            try{
                InputStream inputStream = assetManager.open("busdia.csv");

                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String line;

                while ((line = bufferedReader.readLine()) != null){
                    String[] RowData = line.split(",");
                    listName.add(RowData[i]);

                }
                bufferedReader.close();

            } catch(IOException e){
                Log.d("進捗","失敗している");
                e.printStackTrace();
            }

            return listName;
        }
    }


}
