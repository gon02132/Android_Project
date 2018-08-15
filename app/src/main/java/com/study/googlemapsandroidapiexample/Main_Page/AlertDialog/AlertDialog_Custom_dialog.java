package com.study.googlemapsandroidapiexample.Main_Page.AlertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.DB_conn;
import com.study.googlemapsandroidapiexample.Main_Page.Get_set_package;
import com.study.googlemapsandroidapiexample.Main_Page.MainActivity;
import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;


public class AlertDialog_Custom_dialog {
    private Context                             context;                        //main_context
    private TextView                            title, vd_id, order_list_tv;    //제목, vd_id(hide) 저장공간
    private ListView                            item_list;                      //상품 목록
    private ArrayList<AlertDialog_list_item>    list_itemArrayList;             //배열(상품 목록 출력에 관한)
    private ImageButton                         cancel_bt, ok_bt;               //취소, 보충완료 버튼
    private String                              vending_name, vd_id_str;        //자판기 이름, 자판기 id
    private String                              user_login_id, order_sheet_str; //보충기사 로그인 아이디, 작업지시들 문자열
    private AlertDialog_MyListAdapter           myListAdapter;                  //custom어뎁터
    private DB_conn                             db_conn;                        //DB연결자
    private Get_set_package                     get_set_package;                //겟셋 클래스 변수
    private String                              url;                            //서버 주소

    //생성자
    public AlertDialog_Custom_dialog(Context context, Get_set_package get_set_package, String order_sheet_str, ArrayList<AlertDialog_list_item> list_itemArrayList, String vending_name, String vd_id, String user_login_id, String url) {
        this.context            = context;                          //mainActivity this
        this.get_set_package    = get_set_package;                  //get_set변수 가져오기
        this.list_itemArrayList = list_itemArrayList;               //item list(array)
        this.vending_name       = vending_name;                     //자판기 이름
        this.vd_id_str          = vd_id;                            //자판기 id
        this.db_conn            = new DB_conn(context, url);        //db연결자
        this.user_login_id      = user_login_id;                    //보충기사 로그인 아이디
        this.order_sheet_str    = order_sheet_str;                  //작업지시서 내용들
        this.url                = url;                              //서버 주소 초기화
    }

    //함수 실행
    public void callFunction() {

        //Dialog 객체 생성
        final Dialog dig = new Dialog(context);

        //배경을 투명색으로 바꾼다.
        dig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.alertdialog_custom_dialog);

        //커스텀 다이얼로그가 보여진다
        dig.show();

        //custom listview 만드는 과정
        //만든 BaseAdapt class 생성
        myListAdapter   = new AlertDialog_MyListAdapter(context, list_itemArrayList, url);

        order_list_tv = (TextView)dig.findViewById(R.id.order_list_tv);
        //order_list_tv.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/YoonGothic770.ttf"));  //폰트 설정
        order_list_tv.setText(order_sheet_str);

        //listview에 적용
        item_list       = (ListView) dig.findViewById(R.id.item_list);
        item_list.setAdapter(myListAdapter);

        //커스텀 다이얼로그의 타이틀 설정
        title = (TextView)dig.findViewById(R.id.vending_name);
        title.setText(vending_name);

        //자판기 id 저장
        vd_id = (TextView)dig.findViewById(R.id.vd_id);
        vd_id.setText(vd_id_str);

        //강제 갱신버튼 클릭 시
        ok_bt = (ImageButton)dig.findViewById(R.id.okButton);
        ok_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //기본으로 제공하는 alertDialog 생성 ->
                //실수로 강제 갱신을 누를 수도 있기 때문에 한번더 물어본다.
                AlertDialog.Builder builder = new AlertDialog.Builder(context);

                //제목 지정
                builder.setTitle(vending_name);

                //내용 지정 -> 버튼 2개생성
                builder.setMessage(context.getString(R.string.real_full_ok)).

                        //갱신 취소 기능
                        setPositiveButton(context.getString(R.string.no),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Toast.makeText(context, context.getString(R.string.the_canceled), Toast.LENGTH_SHORT).show();
                                    }
                                })

                        //강제 갱신 기능
                        .setNegativeButton(context.getString(R.string.yes),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            //값을 최신화하고 마커를 다시 그린다 자판기 id / 보충기사 login_id 를 인자로
                                            db_conn.execute("insert_vending", vd_id_str, user_login_id).get();

                                            //마커 최신화(갱신)
                                            ((MainActivity)context).draw_marker();

                                        } catch (Exception e) {
                                            e.printStackTrace();
                                        }


                                        Toast.makeText(context, context.getString(R.string.full_ok), Toast.LENGTH_SHORT).show();

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
        cancel_bt = (ImageButton)dig.findViewById(R.id.cancelButton);
        cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dig.dismiss();
            }
        });

    }
}
