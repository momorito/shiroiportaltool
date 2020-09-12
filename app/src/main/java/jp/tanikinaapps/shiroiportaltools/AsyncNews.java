package jp.tanikinaapps.shiroiportaltools;

import android.os.AsyncTask;

import android.util.Log;
import android.util.Xml;
import android.widget.ArrayAdapter;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import org.xmlpull.v1.XmlPullParser;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AsyncNews extends AsyncTask<Integer,Integer,List<Map<String, String>>> {

    private Listener listener;
    private List<Map<String, String>> data;

    public AsyncNews(List<Map<String, String>> data) {
        this.data = data;
    }


    @Override
    protected List<Map<String, String>> doInBackground(Integer... params){
        ArrayList<String> pageTitle = new ArrayList<String>();
        ArrayList<String> pageUrl = new ArrayList<String>();
        ArrayList<String> pageDate = new ArrayList<String>();
        List<Map<String, String>> data = new ArrayList<Map<String, String>>();

        try{
            XmlPullParser xmlPullParser = Xml.newPullParser();

            //TITLE
            URL url = new URL("http://www.city.shiroi.chiba.jp/feed.xml?type=rss_2.0&new1=1");
            URLConnection connection = url.openConnection();
            xmlPullParser.setInput(connection.getInputStream(), "UTF-8");
            int eventType;

            while ((eventType = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG && "title".equals(xmlPullParser.getName())) {
                    pageTitle.add(xmlPullParser.nextText());
                }
            }
            String[] pageTitleString = new String[pageTitle.size()];
            for(int i =1;i<pageTitle.size();i++){
                pageTitleString[i-1] = pageTitle.get(i);
            }

            //URL
            URLConnection connection2 = url.openConnection();
            xmlPullParser.setInput(connection2.getInputStream(), "UTF-8");
            int eventType2;

            while ((eventType2 = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                if (eventType2 == XmlPullParser.START_TAG && "link".equals(xmlPullParser.getName())) {
                    pageUrl.add(xmlPullParser.nextText());
                }
            }
            String[] pageUrlString = new String[pageTitle.size()];
            for(int i =1;i<pageUrl.size();i++){
                pageUrlString[i-1] = pageUrl.get(i);
            }

            //Date
            URLConnection connection3 = url.openConnection();
            xmlPullParser.setInput(connection3.getInputStream(), "UTF-8");
            int eventType3;

            //pubDate
            while ((eventType3 = xmlPullParser.next()) != XmlPullParser.END_DOCUMENT) {
                if (eventType3 == XmlPullParser.START_TAG && "pubDate".equals(xmlPullParser.getName())) {
                    String pubDate = xmlPullParser.nextText();
                    pageDate.add(pubDate.substring(12,16)+"年"+ returnDayOfWeek(pubDate.substring(8,11)) + "月" +pubDate.substring(5,7) + "日 " + pubDate.substring(17,25));
                }
            }

            String[] pageDateString = new String[pageDate.size()];
            for(int i =0;i<pageDate.size();i++){
                pageDateString[i] = pageDate.get(i);
            }

            data = new ArrayList<Map<String, String>>();
            for (int i=0; i<pageTitleString.length -1; i++){
                Map<String, String> item = new HashMap<String, String>();
                item.put("pageTitleString", pageTitleString[i]);
                item.put("pageUrlString", pageUrlString[i]);
                item.put("pageDateString",pageDateString[i]);
                data.add(item);
            }


        } catch (Exception e){
        }
        return data;
    }

    private String returnDayOfWeek(String weekDay){
        switch(weekDay){
            case "Jan":
                return "1";
            case "Feb":
                return "2";
            case "Mar":
                return "3";
            case "Apr":
                return "4";
            case "May":
                return "5";
            case "Jun":
                return "6";
            case "Jul":
                return "7";
            case "Aug":
                return "8";
            case "Sep":
                return "9";
            case "Oct":
                return "10";
            case "Nov":
                return "11";
            case "Dec":
                return "12";
        }
        return "-";
    }
    @Override
    protected void onPostExecute(List<Map<String, String>> data){
        if (listener != null) {
            listener.onSuccess(data);
        }
    }
    void setListener(Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(List<Map<String, String>> data);
    }

}