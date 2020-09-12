package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.os.AsyncTask;
import android.provider.DocumentsContract;
import android.util.Log;
import java.util.Calendar;
import java.util.Date;

public class AsyncDiaSearch extends AsyncTask<Integer,Integer,String[]> {
    String startPoint,arrivePoint;
    int searchHour,searchMinute;
    Context context;
    Date searchTime;
    Listener listener;
    RootSearch rootSearch;

    public AsyncDiaSearch(Context context,String startPoint,String arrivePoint,int searchHour,int searchMinute){
        this.startPoint = startPoint;
        this.arrivePoint = arrivePoint;
        this.searchHour = searchHour;
        this.searchMinute = searchMinute;
        this.context = context;
    }
    @Override
    protected String[] doInBackground(Integer... integers) {
        String[] result;
        Calendar c = Calendar.getInstance();

        c.set(1988,7,13,searchHour,searchMinute,0);
        searchTime = c.getTime();

        result = rootSearch.startRootSearch(context,startPoint,arrivePoint,searchTime,null);
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

        //両経由地ありの場合、到着時間が早い方を返す
        if(Long.parseLong(resultViaS[8])>Long.parseLong(resultViaN[8])){
            return resultViaN;
        } else{
            return resultViaS;
        }

    }


    private String[] viaRoot(String via){
        String[] toVia,fromVia,reToVia;
        Date afterSerachTime = new Date();

        toVia = RootSearch.startRootSearch(context,startPoint,via,searchTime,null);
        if(toVia == null){
            return null;
        }
        Calendar cl = Calendar.getInstance();
        afterSerachTime.setTime(Long.parseLong(toVia[3]));

        cl.setTime(afterSerachTime);
        cl.add(Calendar.MINUTE,1);
        fromVia = RootSearch.startRootSearch(context,via,arrivePoint,cl.getTime(),null);

        if(toVia == null || fromVia == null){
            return null;
        }
        reToVia = RootSearch.startRootSearch(context,startPoint,via,searchTime,fromVia[1]);

        if(!toVia.equals(reToVia)){
            toVia = reToVia;
        }

        return rootSearch.connectStrings(toVia,fromVia);
    }


    @Override
    protected void onPostExecute(String[] result){
        if (listener != null) {
            listener.onSuccess(result);
        }
    }
    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(String[] result);
    }
}
