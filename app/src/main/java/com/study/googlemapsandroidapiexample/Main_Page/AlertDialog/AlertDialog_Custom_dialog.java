package com.study.googlemapsandroidapiexample.Main_Page.AlertDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;


public class AlertDialog_Custom_dialog {
    private Context context;
    private TextView title;
    private ListView item_list;
    private ArrayList<AlertDialog_list_item> list_itemArrayList;
    private Button ok_bt, cancel_bt;
    private String vending_name;
    private AlertDialog_MyListAdapter myListAdapter;

    public AlertDialog_Custom_dialog(Context context, ArrayList<AlertDialog_list_item> list_itemArrayList, String vending_name) {
        this.context = context;
        this.list_itemArrayList = list_itemArrayList;
        this.vending_name = vending_name;
    }

    public void callFunction() {
        final Dialog dig = new Dialog(context);

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.alertdialog_custom_dialog);

        //커스텀 다이얼로그가 보여진다
        dig.show();

        item_list = (ListView) dig.findViewById(R.id.item_list);
        myListAdapter = new AlertDialog_MyListAdapter(context, list_itemArrayList);
        item_list.setAdapter(myListAdapter);

        //커스텀 다이얼로그의 타이틀 설정
        title = (TextView)dig.findViewById(R.id.vending_name);
        title.setText(vending_name);

        //확인버튼 클릭 시
        ok_bt = (Button)dig.findViewById(R.id.okButton);
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dig.dismiss();
            }
        });

        //취소 버튼 클릭 시
        cancel_bt = (Button)dig.findViewById(R.id.cancelButton);
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dig.dismiss();
            }
        });


    }
}
