package jp.tanikinaapps.shiroiportaltools;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SetDialog extends DialogFragment {

    public static class helpDialogFragment extends DialogFragment {
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        String version;
        Activity activity;

        helpDialogFragment(String version){
            this.version = version;
            this.activity = activity;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.activity_help,null);
            }

            TextView textTwitter = alertView.findViewById(R.id.textTwitter);
            textTwitter.setLinksClickable(true);
            textTwitter.setText(Html.fromHtml("<a href=\"https://twitter.com/momorito\">＠momorito<a/>" ));
            textTwitter.setMovementMethod(LinkMovementMethod.getInstance());

            TextView textPolicy = alertView.findViewById(R.id.textPolicy);
            textPolicy.setLinksClickable(true);
            textPolicy.setText(Html.fromHtml("<a href=\"https://momorito.github.io/privacy.html\">プライバシーポリシー<a/>" ));
            textPolicy.setMovementMethod((LinkMovementMethod.getInstance()));

            TextView versionInfo = alertView.findViewById(R.id.textVersion);
            versionInfo.setText(version);

            TextView helpClose = alertView.findViewById(R.id.helpClose);

            helpClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
    }

    public static class searchDialogFragment extends DialogFragment {
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        EditText editText;
        Activity activity;

        searchDialogFragment(Activity activity){
            this.activity = activity;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.search_dialog,null);
            }

            alert.setTitle(R.string.search_dialog_search);
            editText = alertView.findViewById(R.id.editText);
            editText.setHint(R.string.search_hint_hp);
            Button doSearch = alertView.findViewById(R.id.doSearch);
            Button cancelSearch = alertView.findViewById(R.id.searchCancel);

            doSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(editText.getText().toString().equals("")){
                        dismiss();
                        Toast.makeText(getContext(), "文字を入力しないと検索できません", Toast.LENGTH_SHORT).show();
                    } else{
                        dismiss();
                        Intent intent;
                        intent = new Intent(getActivity(),MainActivity.class);
                        intent.putExtra("newsUrl","http://www.city.shiroi.chiba.jp/material/template/result.html?q=" + editText.getText().toString());
                        startActivity(intent);
                        activity.finish();
                    }

                }
            });

            cancelSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
    }

    public static class callDialogFragment extends DialogFragment {
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        EditText editText;
        List<Map<String,String>> data,resultData;

        callDialogFragment(List<Map<String,String>> data){
            this.data = data;
            resultData = new ArrayList<Map<String,String>>();
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.search_dialog,null);
            }

            final ListView listResult = alertView.findViewById(R.id.listResult);
            alert.setTitle(R.string.search_dialog_search);
            editText = alertView.findViewById(R.id.editText);
            editText.setHint(R.string.search_hint_call);
            Button doSearch = alertView.findViewById(R.id.doSearch);
            Button cancelSearch = alertView.findViewById(R.id.searchCancel);

            doSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(resultData.size() != 0){
                        resultData.clear();
                    }
                    if(editText.getText().toString().equals("")){
                        dismiss();
                        Toast.makeText(getContext(), "文字を入力しないと検索できません", Toast.LENGTH_SHORT).show();
                    } else{

                        for(int i = 0;i<data.size();i++){
                            Map<String, String> item = new HashMap<String, String>();
                            if(data.get(i).get("callName").contains(editText.getText().toString())){
                                item.put("callName", data.get(i).get("callName"));
                                item.put("callNum", data.get(i).get("callNum"));
                                item.put("callWhere",data.get(i).get("callWhere"));
                                resultData.add(item);
                            }
                        }
                        BaseAdapter adapter = new CallAdapter(getContext(),R.layout.list_call,resultData);
                        // ListViewにArrayAdapterを設定する
                        listResult.setAdapter(adapter);
                        listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                Uri uri = Uri.parse("tel:" + resultData.get(position).get("callNum"));
                                Intent i = new Intent(Intent.ACTION_DIAL,uri);
                                startActivity(i);
                                dismiss();
                            }
                        });

                        Toast.makeText(getContext(),"検索結果：" + resultData.size() + "件",Toast.LENGTH_SHORT).show();

                    }

                }
            });

            cancelSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
    }

    public static class mapSearchFragment extends DialogFragment {
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        EditText editText;
        List<String> list,result;

        mapSearchFragment(List<String> list){
            this.list = list;
            result = new ArrayList<>();
        }


        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.search_dialog,null);
            }

            final ListView listResult = alertView.findViewById(R.id.listResult);
            alert.setTitle(R.string.busstop_search);
            editText = alertView.findViewById(R.id.editText);
            editText.setHint(R.string.busstop_hint);
            Button doSearch = alertView.findViewById(R.id.doSearch);
            Button cancelSearch = alertView.findViewById(R.id.searchCancel);



            doSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    result.clear();
                    if(editText.getText().toString().equals("")){
                        dismiss();
                        Toast.makeText(getContext(), "文字を入力しないと検索できません", Toast.LENGTH_SHORT).show();
                    }

                    for(int i=0;i<list.size();i++){
                        if(list.get(i).contains(editText.getText().toString())){

                            result.add(list.get(i));
                        }
                    }
                    String[] resultString = new String[result.size()];
                    for(int i =0;i<resultString.length;i++){
                        resultString[i] = result.get(i);
                    }
                    ArrayAdapter adapter = new ArrayAdapter(getContext(),android.R.layout.simple_list_item_1,resultString);
                    listResult.setAdapter(adapter);

                    Toast.makeText(getContext(),"検索結果：" + result.size() + "件",Toast.LENGTH_SHORT).show();

                    InputMethodManager imm = (InputMethodManager)getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                    listResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            dismiss();
                            MapsActivity mapsActivity = (MapsActivity)getActivity();
                            if(mapsActivity != null){
                                mapsActivity.zoomResult(result.get(position));
                            }
                        }
                    });
                }
            });

            cancelSearch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();

            return dialog;
        }
    }

    public static class DiagramFragment extends DialogFragment {
        AlertDialog dialog;
        AlertDialog.Builder alert;
        View alertView;
        String[] result;
        String searchType;
        TextView closeText,searchResult;

        DiagramFragment(String[] result,String searchType){
            this.searchType = searchType;
            this.result = result;
        }

        @Override
        @NonNull
        public Dialog onCreateDialog(Bundle savedInstanceState){
            alert = new AlertDialog.Builder(getActivity());


            if(getActivity() != null){
                alertView = getActivity().getLayoutInflater().inflate(R.layout.diagram_result,null);
            }

            searchResult = alertView.findViewById(R.id.searchResult);
            searchResult.setText("検索結果（" + searchType + "）");

            closeText = alertView.findViewById(R.id.closeText);

            closeText.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dismiss();
                }
            });

            ListView listDiagramResult = alertView.findViewById(R.id.listDiagramResult);
            BaseAdapter adapter = new DiagramAdapter(getContext(),R.layout.list_diagram,result);

            listDiagramResult.setAdapter(adapter);


            alert.setView(alertView);
            dialog = alert.create();
            dialog.show();
            return dialog;
        }
    }

}
