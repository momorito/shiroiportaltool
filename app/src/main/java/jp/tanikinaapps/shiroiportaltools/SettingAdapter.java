package jp.tanikinaapps.shiroiportaltools;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import static android.content.Context.BIND_NOT_FOREGROUND;
import static android.content.Context.MODE_PRIVATE;

public class SettingAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private int layoutID;
    SharedPreferences data;

    SettingAdapter(Context context,int itemLayoutId){
        inflater = LayoutInflater.from(context);
        layoutID = itemLayoutId;
        data = context.getSharedPreferences("SETTINGS_SNS",MODE_PRIVATE);
    }
    class ViewHolder{
        TextView title,explanation;
        Switch settingSwitch;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null){
            convertView = inflater.inflate(layoutID,null);
            holder = new ViewHolder();
            holder.title = convertView.findViewById(R.id.title);
            holder.explanation = convertView.findViewById(R.id.explanation);
            holder.settingSwitch = convertView.findViewById(R.id.settingSwitch);
            convertView.setTag(holder);
        } else{
            holder = (ViewHolder)convertView.getTag();
        }

        switch(position){
            case 0:
                Boolean useOtherApp;
                useOtherApp = data.getBoolean("DataSns",false);
                holder.title.setText(R.string.setting_otherApps);
                holder.explanation.setText(R.string.setting_otherAppsInst);
                holder.settingSwitch.setChecked(useOtherApp);
                holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = data.edit();
                        editor.putBoolean("DataSns",isChecked);
                        editor.apply();
                    }
                });
                break;
            case 1:
                Boolean useExpansion;
                useExpansion = data.getBoolean("DataExpansion",false);
                holder.title.setText(R.string.setting_expansion);
                holder.explanation.setText(R.string.setting_expansionInst);
                holder.settingSwitch.setChecked(useExpansion);
                holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = data.edit();
                        editor.putBoolean("DataExpansion",isChecked);
                        editor.apply();
                    }
                });
                break;
            case 2:
                Boolean forceSmfView;
                forceSmfView = data.getBoolean("DataSmpView",false);
                holder.title.setText(R.string.force_SmartPhoneView);
                holder.explanation.setText(R.string.force_SmartPhoneViewInst);
                holder.settingSwitch.setChecked(forceSmfView);
                holder.settingSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        SharedPreferences.Editor editor = data.edit();
                        editor.putBoolean("DataSmpView",isChecked);
                        editor.apply();
                    }
                });
                break;
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return 3;
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
