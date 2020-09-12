package jp.tanikinaapps.shiroiportaltools;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.List;
import java.util.Map;

public class AsyncDiagram extends AsyncTask<Integer,Integer,Integer> {
    SQLiteDatabase db;
    DiagramOpenHelper helper;
    private Listener listener;
    Context context;

    public AsyncDiagram(Context context){
        this.context = context;
    }

    @Override
    protected Integer doInBackground(Integer... integers) {
        List<String> startPoint,arrivePoint,selectRoot;
        List<String> startHour,startMinute,arriveHour,arriveMinute;

        CsvReader.DiaList diaList = new CsvReader.DiaList();
        startPoint = diaList.reader(context,1); //出発地点
        startHour = diaList.reader(context,2);
        startMinute = diaList.reader(context,3);
        arrivePoint = diaList.reader(context,5); //到着地点
        arriveHour = diaList.reader(context,6);
        arriveMinute = diaList.reader(context,7);
        selectRoot = diaList.reader(context,8);  //東西南北ルート

        if(helper == null){
            helper = new DiagramOpenHelper(context);
        }

        if(db == null){
            db = helper.getWritableDatabase();
        }

        db.delete("diadb",null,null);
        ContentValues values = new ContentValues();
        for(int i =0;i<startPoint.size();i++){
            values.put("startpoint",startPoint.get(i));
            values.put("starthour",Integer.valueOf(startHour.get(i)));
            values.put("startminute",Integer.valueOf(startMinute.get(i)));
            values.put("arrivepoint",arrivePoint.get(i));
            values.put("arrivehour",Integer.valueOf(arriveHour.get(i)));
            values.put("arriveminute",Integer.valueOf(arriveMinute.get(i)));
            values.put("root",selectRoot.get(i));
            db.insert("diadb",null,values);
        }
        Log.d("進捗","db is created");

        return null;
    }

    @Override
    protected void onPostExecute(Integer params){
        if (listener != null) {
            listener.onSuccess();
        }
    }

    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess();
    }
}
