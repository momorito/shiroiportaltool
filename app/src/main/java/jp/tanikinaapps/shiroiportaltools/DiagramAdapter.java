package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DiagramAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    private String[] result;

    static class ViewHolder{
        TextView turn,time,point;
    }

    DiagramAdapter(Context context,int itemLayoutId,String[] result){
        inflater = LayoutInflater.from(context);
        layoutID = itemLayoutId;
        this.result = result;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            convertView = inflater.inflate(layoutID,null);
            holder = new ViewHolder();
            holder.turn = convertView.findViewById(R.id.turn);
            holder.time = convertView.findViewById(R.id.time);
            holder.point = convertView.findViewById(R.id.point);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        sdf.setTimeZone(TimeZone.getTimeZone("GMT+9"));

        switch (result.length){


            case 1:
                if(position == 0){
                    holder.turn.setText("検索時間で到着できる路線はありません。時間を変更して再度検索してください。");
                    holder.time.setVisibility(View.INVISIBLE);
                    holder.point.setVisibility(View.INVISIBLE);
                }
                break;
            case 5:
                if(position == 0){
                    convertView.setBackgroundColor(Color.rgb(255,255,255));
                    holder.turn.setText("■出発");
                    holder.time.setText(sdf.format(new Date(Long.parseLong(result[1].substring(0,9)) * 1000)));
                    holder.point.setText(result[0]);
                }
                if(position == 1){
                    convertView.setBackgroundColor(Color.rgb(220,220,220));
                    holder.turn.setText("↓");
                    holder.time.setText("（" +String.valueOf((Long.parseLong(result[3].substring(0,9)) - Long.parseLong(result[1].substring(0,9))) / 60) + "分）");
                    holder.point.setText("["+result[4]+"ルート]");
                }
                if(position == 2){
                    convertView.setBackgroundColor(Color.rgb(255,255,255));
                    holder.turn.setText("■到着");
                    holder.time.setText(sdf.format(new Date(Long.parseLong(result[3].substring(0,9)) * 1000)));
                    holder.point.setText(result[2]);
                }


                break;
            case 10:
                if(position == 0){
                    convertView.setBackgroundColor(Color.rgb(255,255,255));
                    holder.turn.setText("■出発");
                    holder.time.setText(sdf.format(new Date(Long.parseLong(result[1].substring(0,9)) * 1000)));
                    holder.point.setText(result[0]);
                    holder.point.setTextSize(18.0f);
                }
                if(position == 1){
                    convertView.setBackgroundColor(Color.rgb(220,220,220));
                    holder.turn.setText("↓");
                    holder.time.setText("（" +String.valueOf((Long.parseLong(result[3].substring(0,9)) - Long.parseLong(result[1].substring(0,9))) / 60) + "分）");
                    holder.point.setText("["+result[4]+"]");
                    holder.point.setTextSize(10.0f);
                }
                if(position == 2){
                    holder.turn.setText("■経由地着");
                    holder.time.setText(sdf.format(new Date(Long.parseLong(result[3].substring(0,9)) * 1000)));
                    holder.point.setText(result[2]);
                    holder.point.setTextSize(18.0f);
                }
                if(position ==3){
                    holder.turn.setText("■経由地発");
                    holder.time.setText(sdf.format(new Date(Long.parseLong(result[6].substring(0,9)) * 1000)));
                    holder.point.setText(result[5]);
                    holder.point.setTextSize(18.0f);
                }
                if(position == 4){
                    convertView.setBackgroundColor(Color.rgb(220,220,220));
                    holder.turn.setText("↓");
                    holder.time.setText("（" +String.valueOf((Long.parseLong(result[8].substring(0,9)) - Long.parseLong(result[6].substring(0,9))) / 60) + "分）");
                    holder.point.setText("["+result[9]+"]");
                    holder.point.setTextSize(10.0f);
                }

                if(position == 5){
                    convertView.setBackgroundColor(Color.rgb(255,255,255));
                    holder.turn.setText("■到着");
                    holder.time.setText(sdf.format(new Date(Long.parseLong(result[8].substring(0,9)) * 1000)));
                    holder.point.setText(result[7]);
                    holder.point.setTextSize(18.0f);
                }
        }

        return convertView;
    }

    @Override
    public int getCount() {
        switch (result.length){
            case 1:
                return 1;
            case 5:
                return  3;
            case 10:
                return 6;
            default:
                return 0;
        }

    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


}
