package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;


import android.content.Context;
import android.util.Log;
import android.widget.ListView;

import org.json.JSONArray;
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
            //검색된 배열을 순차적으로 돈다
            String note_str = json_result.getJSONObject(0).getString("note");
            if(!note_str.equals("null")){
                sc_list_items.add(new sc_list_item(note_str,"!!", ""));
            }
            for (int i = 0; i < json_result.length(); i++) {
                //[0]=vd_id [1]=vd_name [2]=drink_name [3]=drink_path [4]=drink_stook [5]=drink_line [6]=note
                JSONObject json_obj = json_result.getJSONObject(i);
                sc_list_items.add(new sc_list_item(json_obj.getString("drink_name"), json_obj.getString("drink_line"), json_obj.getString("drink_stook")));
            }
            sc_adapter = new sc_adapter(context, sc_list_items);
            sc_lv.setAdapter(sc_adapter);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(">>>>>>>",e.toString());
        }
    }

}
