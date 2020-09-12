package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class AsyncDiaSearchR extends AsyncTask<Integer,Integer,String[]>  {
    String startPoint,arrivePoint;
    int searchHour,searchMinute;
    Context context;
    SQLiteDatabase db;
    DiagramOpenHelper helper;
    Date searchTime;
    Listener listener;
    RootSearch rootSearch;

    AsyncDiaSearchR(Context context,String startPoint,String arrivePoint,int searchHour,int searchMinute){
        this.startPoint = startPoint;
        this.arrivePoint = arrivePoint;
        this.searchHour = searchHour;
        this.searchMinute = searchMinute;
        this.context = context;
    }

    @Override
    protected String[] doInBackground(Integer... params) {
        String[] result;
        Calendar c = Calendar.getInstance();
        c.set(1988,7,13,searchHour,searchMinute,0);
        searchTime = c.getTime();

        result = rootSearch(startPoint,arrivePoint,searchTime,null);
        if(result != null){
            return result;  //直通便あり
        }
        String[] resultViaS,resultViaN;
        resultViaS = viaRoot("001白井市役所");
        resultViaN = viaRoot("007西白井駅");

        if(resultViaN == null){
            //西白井経由ルートなし、白井市役所経由ルートを返す
            return resultViaS;
        } else if(resultViaN == null){
            //白井市役所経由ルートなし、西白井駅経由ルートを返す
            return resultViaN;
        }

        //両経由地ありの場合、出発時間が遅い方を返す
        if(Long.parseLong(resultViaS[1])>Long.parseLong(resultViaN[1])){
            return resultViaS;
        } else{
            return resultViaN;
        }
    }

    private String[] rootSearch(String start,String arrive,Date time,String lastTime){
        if(helper == null){
            helper = new DiagramOpenHelper(context);
        }
        if(db == null){
            db = helper.getReadableDatabase();
        }

        Cursor cursor = db.query("diadb",new String[]{"startpoint","starthour","startminute","arrivepoint","arrivehour","arriveminute","root"},"startpoint like ?",new String[]{"%" + start +"%"},null,null,null);
        cursor.moveToFirst();

        Calendar c = Calendar.getInstance();

        List<String> startPoints = new ArrayList<>();
        List<Date> startTime = new ArrayList<>();
        List<String> arrivePoints = new ArrayList<>();
        List<Date> arriveTime = new ArrayList<>();
        List<String> roots = new ArrayList<>();

        for(int i=0;i<cursor.getCount();i++){
            startPoints.add(cursor.getString(0));
            c.set(1988,7,13,Integer.parseInt(cursor.getString(1)),Integer.parseInt(cursor.getString(2)),0);
            startTime.add(c.getTime());
            arrivePoints.add(cursor.getString(3));
            c.set(1988,7,13,Integer.parseInt(cursor.getString(4)),Integer.parseInt(cursor.getString(5)),0);
            arriveTime.add(c.getTime());
            roots.add(cursor.getString(6));
            cursor.moveToNext();
        }
        cursor.close();
        int pointAmount = arrivePoints.size();

        Log.d("進捗","出発地が" +start + "である：" + pointAmount);

        for(int i =0;i<startPoints.size();i++){
            if(!arrivePoints.get(i).equals(arrive)){
                startPoints.remove(i);
                startTime.remove(i);
                arrivePoints.remove(i);
                arriveTime.remove(i);
                roots.remove(i);
                i = -1;
            }
        }

        if(startPoints.size() == 0){
            Log.d("進捗",start+"を出発して" + arrive + "に行く路線は0である");
            return null;
        }

        Log.d("進捗",start+"を出発して" + arrive + "に行く路線は" + arrivePoints.size() + "あります");

        String arriveTimeString = null;
        String timeString = null;
        long arriveTimeLong,timeLong;
        for (int i =0;i<arrivePoints.size();i++){
            arriveTimeString = String.valueOf(arriveTime.get(i).getTime());
            timeString = String.valueOf(time.getTime());

            arriveTimeLong = Long.parseLong(arriveTimeString.substring(0,9))*1000;
            timeLong = Long.parseLong(timeString.substring(0,9))*1000;
            if(arriveTimeLong > timeLong){
                Log.d("進捗５" + i,"timeは" + time.getTime() + "、arriveTimeは" + arriveTime.get(i).getTime());
                startPoints.remove(i);
                startTime.remove(i);
                arrivePoints.remove(i);
                arriveTime.remove(i);
                roots.remove(i);
                i = -1;
            }
        }
        if(arrivePoints.size() == 0){
            Log.d("進捗",start+"を出発して" + arrive + "に行く路線は存在するが、ダイヤがない");
            //時間が存在しない
            return null;
        }

        Log.d("進捗3",start+"を出発して" + arrive + "に行って、時間もOKなのが" + arrivePoints.size());


        Log.d("進捗3","最後に残ったのは" + startPoints.size());

        long[] miliTime = new long[arrivePoints.size()];

        for(int i=0;i<arrivePoints.size();i++){
            miliTime[i] = startTime.get(i).getTime();
        }

        long max = miliTime[0];

        for(int i=0;i<miliTime.length;i++){
            max = Math.max(max,miliTime[i]);
        }

        String[] result = new String[5];
        for(int i=0;i<miliTime.length;i++){
            if(max == miliTime[i]){
                result[0] = startPoints.get(i);
                result[1] = String.valueOf(startTime.get(i).getTime());
                result[2] = arrivePoints.get(i);
                result[3] = String.valueOf(arriveTime.get(i).getTime());
                result[4] = roots.get(i);

                Log.d("進捗","最終的に" + i +"番目のレコード");
            }
        }
        return result;
    }

    private String[] viaRoot(String via){
        String[] toVia,fromVia,reFromVia;
        Date beforeSearchTime = new Date();

        fromVia = rootSearch(via,arrivePoint,searchTime,null);
        if(fromVia == null){
            return null;
        }
        Calendar cl = Calendar.getInstance();
        beforeSearchTime.setTime(Long.parseLong(fromVia[1]));
        cl.setTime(beforeSearchTime);
        cl.add(Calendar.MINUTE,-1);

        toVia = rootSearch(startPoint,via,cl.getTime(),null);

        if(toVia == null || fromVia == null){
            return null;
        }

        Date afterSearchTime = new Date();
        afterSearchTime.setTime(Long.parseLong(toVia[3]));
        cl.setTime(afterSearchTime);
        cl.add(Calendar.MINUTE,1);


        reFromVia = rootSearch.startRootSearch(context,via,arrivePoint,cl.getTime(),null);

        if(!fromVia.equals(reFromVia)){
            fromVia = reFromVia;
        }







        return rootSearch.connectStrings(toVia,fromVia);
    }

    @Override
    protected void onPostExecute(String[] result){
        if (listener != null) {
            listener.onSuccess(result);
        }
    }

    void setListener(Listener listener){
        this.listener = listener;
    }

    interface Listener{
        void onSuccess(String[] result);
    }
}
