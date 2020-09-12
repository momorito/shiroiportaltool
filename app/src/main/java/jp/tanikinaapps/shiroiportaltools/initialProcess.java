package jp.tanikinaapps.shiroiportaltools;

import android.os.AsyncTask;
import android.util.Log;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class initialProcess extends AsyncTask <Integer,String,List<Map<String, String>>> {
    private Listener listener;
    private List<Map<String, String>> data;

    public initialProcess(List<Map<String, String>> data) {
        this.data = data;
    }

    @Override
    protected List<Map<String, String>> doInBackground(Integer... params) {
        data = new ArrayList<Map<String, String>>();
        try {

            Document document = Jsoup.connect("http://www.city.shiroi.chiba.jp/toppage.html").get();
            Elements title = document.select(".linkCom");
            Elements url = document.select(".linkCom");

            for(int i=0;i<title.size();i++){
                Map<String, String> item = new HashMap<String, String>();
                String titleUrl = url.get(i).toString();
                item.put("title", title.get(i).text());
                item.put("url",url.get(i).toString().substring(30,titleUrl.length()).substring(0,titleUrl.substring(30,titleUrl.length()).indexOf("class")-2));
                data.add(item);
            }

        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Error","failed to read Url");
        }

        return data;
    }

    @Override
    protected void onPostExecute(List<Map<String, String>> data){

        if (listener != null) {
            listener.onSuccess(data);
        }
    }

    void setListener(initialProcess.Listener listener) {
        this.listener = listener;
    }

    interface Listener {
        void onSuccess(List<Map<String, String>> data);
    }
}
