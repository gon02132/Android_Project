package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;


import android.content.Context;
import android.util.Log;
import android.widget.ListView;

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

    //생성자
    public Sc_custom_listview(Context context, JSONObject json_obj, ListView sc_lv) {
        this.json_obj           = json_obj;
        this.context            = context;
        this.sc_lv              = sc_lv;
        this.sc_list_items      = new ArrayList<Sc_list_item>();
    }

    //호출 함수
    public void change_listview(){
        //db접속은 try/catch 필수
        try {

            //제품들이 저장되어있는 JSON배열을 가져온다.
            JSONArray json_result = json_obj.getJSONArray("result");

            //작업지시가 하나라도 있는경우 true로 바뀐다
            Boolean note_check = false;

            //작업 지시가 있다면 맨위에 먼저 추가한다
            for (int i = 0; i < json_result.length(); i++) {
                //검색된 배열을 순차적으로 돈다

                String note_str = json_result.getJSONObject(i).getString("note");

                //만약 작업지시가 있는 경우, 맨 윗부분들에는 작업지시내용을 보여준다.
                if(!note_str.equals("null")){
                    sc_list_items.add(new Sc_list_item(note_str,"!!", ""));

                    //작업 지시가 있다는 것을 알려줌
                    note_check = true;
                }
            }

            //작업지시가 없는 자판기의 경우 맨위에 작업지시가 없는 자판기임을 알려준다.
            if(note_check == false){
                sc_list_items.add(new Sc_list_item("작업 지시가 없는 자판기","X", ""));
            }

            //실제 내용들이 들어가는 반복문
            for (int i = 0; i < json_result.length(); i++) {
                //[0]=vd_id [1]=vd_name [2]=drink_name [3]=drink_path [4]=drink_stook [5]=drink_line [6]=note
                JSONObject json_obj = json_result.getJSONObject(i);

                sc_list_items.add(new Sc_list_item(json_obj.getString("drink_name"), json_obj.getString("drink_line"), json_obj.getString("drink_stook")));
            }

            //custom_listview 생성
            sc_adapter = new Sc_adapter(context, sc_list_items);
            sc_lv.setAdapter(sc_adapter);
        }catch (Exception e){
            e.printStackTrace();
            Log.e(">>>>>>>",e.toString());
        }
    }

}
