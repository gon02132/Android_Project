package com.study.googlemapsandroidapiexample.Main_Page.AlertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;


public class AlertDialog_Custom_dialog {
    private Context                             context;                    //main_context
    private TextView                            title, vd_id;               //제목, vd_id(hide) 저장공간
    private ListView                            item_list;                  //상품 목록
    private ArrayList<AlertDialog_list_item>    list_itemArrayList;         //배열(상품 목록 출력에 관한)
    private Button                              ok_bt, cancel_bt;           //갱신 과 취소버튼
    private String                              vending_name, vd_id_string; //자판기 이름, 자판기 id 문자열
    private AlertDialog_MyListAdapter           myListAdapter;              //custom어뎁터

    //생성자
    public AlertDialog_Custom_dialog(Context context, ArrayList<AlertDialog_list_item> list_itemArrayList, String vending_name, String vd_id) {
        this.context            = context;              //mainActivity this
        this.list_itemArrayList = list_itemArrayList;   //item list(array)
        this.vending_name       = vending_name;         //자판기 이름
        this.vd_id_string       = vd_id;                //자판기 id
    }

    public void callFunction() {
        //Dialog 객체 생성
        final Dialog dig = new Dialog(context);

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.alertdialog_custom_dialog);

        //커스텀 다이얼로그가 보여진다
        dig.show();

        //custom listview 만드는 과정
        //만든 BaseAdapt class 생성
        myListAdapter   = new AlertDialog_MyListAdapter(context, list_itemArrayList);

        //listview에 적용
        item_list       = (ListView) dig.findViewById(R.id.item_list);
        item_list.setAdapter(myListAdapter);

        //커스텀 다이얼로그의 타이틀 설정
        title = (TextView)dig.findViewById(R.id.vending_name);
        title.setText(vending_name);

        //자판기 id 저장
        vd_id = (TextView)dig.findViewById(R.id.vd_id);
        vd_id.setText(vd_id_string);

        //강제 갱신버튼 클릭 시
        ok_bt = (Button)dig.findViewById(R.id.okButton);
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //기본으로 제공하는 alertDialog 생성 ->
                //실수로 강제 갱신을 누를 수도 있기 때문에 한번더 물어본다.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //제목 지정
                builder.setTitle(vending_name);

                //내용 지정 -> 버튼 2개생성
                builder.setMessage("정말로 강제 갱신을 하시 겠습니까?").

                        //갱신 취소 기능
                        setPositiveButton("아니오",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "취소 되었습니다", Toast.LENGTH_SHORT).show();
                                    }
                                })

                        //갱신 기능
                        .setNegativeButton("예",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, "강제 갱신되었습니다.", Toast.LENGTH_SHORT).show();

                                        //현재 열려있는 자판기 정보도 닫는다.
                                        dig.dismiss();
                                    }
                                });

                //만든 alertdialog를 만들어서 보여준다.
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });

        //취소 버튼 클릭 시 -> 현재 보고 있는 창을 닫는다.
        cancel_bt = (Button)dig.findViewById(R.id.cancelButton);
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dig.dismiss();
            }
        });

    }
}
