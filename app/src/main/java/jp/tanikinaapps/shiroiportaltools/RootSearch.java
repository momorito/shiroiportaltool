package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class RootSearch {
    public static SQLiteDatabase db;
    public static DiagramOpenHelper helper;

    public static String[] startRootSearch(Context context, String start, String arrive, Date time, String lastTime){
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
        int pointAmount = startPoints.size();
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

        Log.d("進捗",start+"を出発して" + arrive + "に行く路線は" + startPoints.size() + "あります");

        for (int i =0;i<startPoints.size();i++){
            if(startTime.get(i).before(time)){
                startPoints.remove(i);
                startTime.remove(i);
                arrivePoints.remove(i);
                arriveTime.remove(i);
                roots.remove(i);
                i = -1;
            }
        }

        if(startPoints.size() == 0){
            Log.d("進捗",start+"を出発して" + arrive + "に行く路線は存在するが、ダイヤがない");
            //時間が存在しない
            return null;
        }

        Log.d("進捗3",start+"を出発して" + arrive + "に行って、時間もOKなのが" + startPoints.size());

        if(lastTime != null){
            Date checkTime = new Date(Long.parseLong(lastTime.substring(0,9)) * 1000);
            Log.d("進捗2","どうだ" + checkTime);

            for (int i =0;i<startPoints.size();i++){
                if(arriveTime.get(i).after(checkTime)){
                    startPoints.remove(i);
                    startTime.remove(i);
                    arrivePoints.remove(i);
                    arriveTime.remove(i);
                    roots.remove(i);
                    i = -1;
                }
            }
            Log.d("進捗3","最後に残ったのは" + startPoints.size());

            if(startPoints.size() != 1){
                long[] miliTime = new long[startPoints.size()];
                for(int i=0;i<startPoints.size();i++){
                    miliTime[i] = startTime.get(i).getTime();
                }
                long max = miliTime[0];
                for(int i=0;i<miliTime.length;i++){
                    max = Math.max(max,miliTime[i]);
                }

                int saveNum = 0;
                for (int i =0;i<startPoints.size();i++){
                    if(max == miliTime[i]){
                        saveNum = i;
                    }
                }

                String result[] = new String[5];
                result[0] = startPoints.get(saveNum);
                result[1] = String.valueOf(startTime.get(saveNum).getTime());
                result[2] = arrivePoints.get(saveNum);
                result[3] = String.valueOf(arriveTime.get(saveNum).getTime());
                result[4] = roots.get(saveNum);
                return result;
            }
        }
        Log.d("進捗3","最後に残ったのは" + startPoints.size());


        long[] miliTime = new long[startPoints.size()];

        for(int i=0;i<startPoints.size();i++){
            miliTime[i] = arriveTime.get(i).getTime();
        }

        long min = miliTime[0];

        for(int i=0;i<miliTime.length;i++){
            min = Math.min(min,miliTime[i]);
        }

        String[] result = new String[5];
        for(int i=0;i<miliTime.length;i++){
            if(min == miliTime[i]){
                result[0] = startPoints.get(i);
                result[1] = String.valueOf(startTime.get(i).getTime());
                result[2] = arrivePoints.get(i);
                result[3] = String.valueOf(arriveTime.get(i).getTime());
                result[4] = roots.get(i);

                Log.d("進捗","最終的に" + i +"番目のレコード");
            }
        }
        return  result;
    }

    public static String[] arriveRootSearch(Context context, String start, String arrive, Date time, String lastTime){
        return null;
    }

    public static String[] connectStrings(String[] str1,String[] str2){
        String[] result = new String[str1.length + str2.length];

        for(int i=0;i< str1.length;i++){
            result[i] = str1[i];
        }
        int n = 0;
        for(int i = str1.length;i< str1.length + str2.length;i++){
            result[i] = str2[n];
            n++;
        }
        return result;
    }
}
