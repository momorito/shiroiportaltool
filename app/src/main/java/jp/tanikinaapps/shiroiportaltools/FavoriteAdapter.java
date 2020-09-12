package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class FavoriteAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    private List<Map<String,String>> data;

    static class ViewHolder{
        TextView title,id,url;
    }

    FavoriteAdapter(Context context,int itemLayout,List<Map<String,String>> data){
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
            holder.title = convertView.findViewById(R.id.title);
            holder.id = convertView.findViewById(R.id.id);
            holder.url = convertView.findViewById(R.id.url);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.title.setText(data.get(position).get("pageTitle"));
        holder.id.setText(data.get(position).get("pageId"));
        holder.url.setText(data.get(position).get("pageUrl"));
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
