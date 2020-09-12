package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class NewsAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    private List<Map<String, String>> data;
    private boolean[] checkNew;

    static class ViewHolder{
        TextView title,date,newSign;
    }

    NewsAdapter(Context context,int itemLayoutId,List<Map<String, String>> data,boolean[] checkNew){
        inflater = LayoutInflater.from(context);
        layoutID = itemLayoutId;
        this.data = data;
        this.checkNew = checkNew;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(layoutID,null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.date = convertView.findViewById(R.id.date);
            holder.newSign = convertView.findViewById(R.id.newSign);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }
        blinkText(holder.newSign,100,500);
        holder.title.setText(data.get(position).get("pageTitleString"));
        holder.date.setText(data.get(position).get("pageDateString"));

        if(checkNew[position] == true){
            holder.newSign.setText(" New!");
        } else{
            holder.newSign.setText("");
        }

        if(holder.title.getText().length() > 40){
            holder.title.setTextSize(14);
        } else {
            holder.title.setTextSize(18);
        }
        return convertView;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void blinkText(TextView textView,long duration,long offset){
        Animation anim = new AlphaAnimation(0.0f,1.0f);
        anim.setDuration(duration);
        anim.setStartOffset(offset);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        textView.startAnimation(anim);
    }
}
