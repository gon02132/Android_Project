package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;


import android.content.Context;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import com.study.googlemapsandroidapiexample.Main_Page.MainActivity;
import com.study.googlemapsandroidapiexample.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//custom_listview를 만들기위해 MainActivity에서 접근하는 class
public class Sc_custom_listview {
    private ListView                sc_lv;          //제품 리스트들이 출력될 저장소
    private Sc_adapter              sc_adapter;     //custom_listview가 만들어지는 곳
    private ArrayList<Sc_list_item> sc_list_items;  //db에서 가져온 제품 리스트들이 저장될 곳
    private JSONObject              json_obj;       //db에서 가져온 제품 리스트들의 JSONObj
    private Context                 context;        //MatinActivty this
    private String                  url;            //서버 주소

    //생성자
    public Sc_custom_listview(Context context, JSONObject json_obj, ListView sc_lv, String url) {
        this.json_obj           = json_obj;
        this.context            = context;
        this.sc_lv              = sc_lv;
        this.sc_list_items      = new ArrayList<Sc_list_item>();
        this.url                = url;
    }

    //호출 함수
    public void change_listview(){
        //db접속은 try/catch 필수
        try {

            //제품들이 저장되어있는 JSON배열을 가져온다.
            JSONArray json_result   = json_obj.getJSONArray("result");

            //작업지시서들이 들어가는 문자열
            String note_str_all = "";

            //다음 가야할 자판기를 표시해주는 TextView
            TextView tv = (TextView)((MainActivity) context).findViewById(R.id.next_vd_order);

            //작업 지시가 있다면 맨위에 먼저 추가한다
            for (int i = 0; i < json_result.length(); i++) {
                //검색된 배열을 순차적으로 돈다

                //작업지시열을 자겨온다
                String note_str = json_result.getJSONObject(i).getString("note");

                //만약 작업지시가 있는 경우, 문자열에 저장한다
                if(!note_str.equals("null")){
                    note_str_all += note_str;
                }

            }

            //작업지시서가 있는 경우 내용을 출력한다
            if(note_str_all.length() > 1){
                tv.setText(note_str_all);
            }

            //작업지시서가 없는 경우 없다는 문자열을 출력한다
            else{
                tv.setText(context.getString(R.string.no_order_sheet));
            }


            //실제 내용들이 들어가는 반복문
            for (int i = 0; i < json_result.length(); i++) {
                //[0]=vd_id [1]=vd_name [2]=drink_name [3]=drink_path [4]=drink_stook [5]=drink_line [6]=note
                JSONObject json_obj = json_result.getJSONObject(i);

                Integer stock_int = 10-json_obj.getInt("drink_stook");
                sc_list_items.add(new Sc_list_item(json_obj.getString("drink_name"), json_obj.getString("drink_line"), stock_int.toString()));
            }

            //custom_listview 생성
            sc_adapter = new Sc_adapter(context, sc_list_items, url);
            sc_lv.setAdapter(sc_adapter);

        }catch (Exception e){
            e.printStackTrace();
            Log.e(">>>>>>>",e.toString());
        }
    }

}
