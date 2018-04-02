package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;


import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class sc_custom_listview {
    private ListView sc_lv;
    private sc_adapter sc_adapter;
    private ArrayList<sc_list_item> sc_list_items;
    private JSONObject json_obj;
    private Context context;

    public sc_custom_listview(Context context, JSONObject json_obj, ListView sc_lv) {
        this.json_obj = json_obj;
        this.context = context;
        this.sc_lv = sc_lv;
        sc_list_items = new ArrayList<sc_list_item>();
    }

    public void change_listview(){
        try {
            JSONArray json_result = json_obj.getJSONArray("result");
            //제목 적어야 함!!!
            //next_vending_result += json_result.getJSONObject(0).getString("vd_id") + "|" + json_result.getJSONObject(0).getString("vd_name") + "\n";
            //검색된 배열을 순차적으로 돈다
            for (int i = 0; i < json_result.length(); i++) {
                //[0]=vd_id [1]=vd_name [2]=drink_name [3]=drink_path [4]=drink_stook [5]=drink_line [6]=expiration_date
                JSONObject json_obj = json_result.getJSONObject(i);
                sc_list_items.add(new sc_list_item(json_obj.getString("drink_name"), json_obj.getString("drink_line"), json_obj.getString("drink_stook")));
                //|next_vending_result += json_obj.getString("drink_name") + "|" + json_obj.getString("drink_line") + "|" + json_obj.getString("drink_stook") + "\n";
            }
            sc_adapter = new sc_adapter(context, sc_list_items);
            sc_lv.setAdapter(sc_adapter);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(">>>>>>>",e.toString());
        }
    }

}
