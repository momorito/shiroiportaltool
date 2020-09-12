package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class CallAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    List<Map<String,String>> data;

    static class ViewHolder{
        TextView callName,callWhere,callNum;
    }

    CallAdapter(Context context, int itemLayout, List<Map<String,String>> data){
        inflater = LayoutInflater.from(context);
        layoutID = itemLayout;
        this.data = data;

    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(layoutID,null);
            holder = new ViewHolder();
            holder.callName = convertView.findViewById(R.id.callName);
            holder.callWhere = convertView.findViewById(R.id.callWhere);
            holder.callNum = convertView.findViewById(R.id.callNum);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }


        holder.callName.setText(data.get(position).get("callName"));
        holder.callWhere.setText(data.get(position).get("callWhere"));
        holder.callNum.setText(data.get(position).get("callNum"));
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


}
