package com.study.googlemapsandroidapiexample.Main_Page;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.DB_conn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

//작업지시서 만들어지는 class
public class Order_sheet_alert {

    //기본 변수들
    private Context                 context;
    private String                  user_login_id;

    //테이블 속성 관련 변수 들
    private TableLayout             order_sheet_layout, title_sheet_layout;
    private TableRow                tr;
    private TextView                textView;
    private TableRow.LayoutParams   params;
    private Integer                 margin_size = 2;

    //스크롤뷰
    private HorizontalScrollView    scroll_view_top, scroll_view_bottom;

    //자동차 재고량
    private ArrayList<Integer>      car_stock       = new ArrayList<>();
    //제품 이름들이 들어갈 배열
    private ArrayList<String>       product_val     = new ArrayList<>();
    //제품 보충 필요량 합계가 들어갈 배열
    private ArrayList<Integer>      product_count   = new ArrayList<>();


    //생성자
    public Order_sheet_alert(Context context, String user_login_id){
        this.context        = context;
        this.user_login_id  = user_login_id;

    }

    @SuppressLint("WrongViewCast")
    public void create_table() {
        final Dialog dig = new Dialog(context);

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.orderseet_table_view);

        //작업지시서 레이아웃
        order_sheet_layout  = dig.findViewById(R.id.order_sheet_layout);
        title_sheet_layout  = dig.findViewById(R.id.title_sheet_layout);

        //작업지시서의 좌우 스크롤뷰(머리, 몸통 부분)
        scroll_view_top     = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_top);
        scroll_view_bottom  = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_bottom);

        //커스텀 다이얼로그가 보여진다
        dig.show();

        try {
            //db 접속(try/catch 필수)
            DB_conn db_conn_obj = new DB_conn(context);

            //db에 접속하여 반환된 결과값 초기화
            String result_str   = db_conn_obj.execute("get_order_sheet", user_login_id).get();

            //받아온 값이 없거나 mysql구문의 에러의 경우 아무것도 실행하지 않고 다음으로 넘어간다
            if (result_str.equals("no_marker") || result_str.equals("mysql_err")) {
                Toast.makeText(context, "no marker or mysql_err", Toast.LENGTH_SHORT).show();
            }

            //받아온 값이 JSON객체로 있을 경우 -> 테이블을 만든다
            else {
                //json 객체로 변환하여 json배열에 저장
                JSONObject jsonObject  = new JSONObject(result_str);

//--------------------------------------------제목 부분 ---------------------------------------------

                //drk_name, stock -> 제품명, 갯수 반환
                JSONArray  json_result_set = jsonObject.getJSONArray("product_all");
                for(int a=0; a<2; a++) {
                    //테이블의 TR태그를 만든다
                    tr = new TableRow(context);

                    //"자판기 명" TD 생성
                    draw_td(3,600, "자판기 명", false);

                    //------작업지시 TD 넣는 공간------

                    //"작업 지시" TD 생성
                    draw_td(3,700, "작업 지시", false);

                    //받아온 값만큼 반복한다.
                    for (int i = 0; i < json_result_set.length(); i++) {
                        //drk_name, stock 가 저장되어 있음
                        JSONObject json_object = json_result_set.getJSONObject(i);

                        //현재 모든 제품들의 이름들을 가져온다.
                        String drk_name = json_object.getString("drk_name");


                        //한번만 초기화 시킨다
                        if(a==0) {
                            //차량 재고를 배열에 저장해둔다(맨밑에 출력하기위해)
                            car_stock.add(json_object.getInt("stock"));

                            //제품들을 배열에 넣는다
                            product_val.add(drk_name);

                            //각 제품들의 보충 필요량들을 0으로 초기화한다
                            product_count.add(0);
                        }

                        //현재 소유중인 제품들의 명들을 차례차례 출력한다.
                        draw_td(1,0, drk_name,false);


                    }
                    if(a==0){
                        //테이블에 TR을 적용시킨다.
                        order_sheet_layout.addView(tr);
                    }
                    else if(a==1){
                        //테이블에 TR을 적용시킨다.
                        title_sheet_layout.addView(tr);
                    }
                }

//--------------------------------------------몸통 부분 ---------------------------------------------
                //제품들 가져오기
                JSONArray  json_result = jsonObject.getJSONArray("result");
                //새로운 자판기의 이름이 나오면 TR을 새롭게 만들게 하기위한 비교용 String
                String before_vending = "";

                //테이블의 TR태그를 초기화 한다
                tr = null;

                //받아온 값만큼 반복한다.
                for(int i = 0; i < json_result.length(); i++){
                    Log.e("<<<<",1+" ");
                    //sp_name, vd_name, drink_name, drink_path, sp_val, drink_line, note, sp_check 가 저장되어 있음
                    JSONObject json_object = json_result.getJSONObject(i);

                    //현재 자판기의 이름을 가져온다.
                    String now_vending = json_object.getString("vd_name");

                    //만약 새로운 자판기 라면 TR태그를 새로만든다.
                    if(!before_vending.equals(now_vending)) {
                        Log.e("<<<<",2+" ");
//----------------------------------몸통)자판기 명 들어가는 곳----------------------------------------
                        //처음 통과시 tr이 준비되지 않았으므로 통과시키기 위함
                        if(tr != null){
                            order_sheet_layout.addView(tr);
                        }

                        //테이블의 TR태그를 만든다
                        tr = new TableRow(context);

                        Log.e("<<<<",3+" ");
                        //이전 자판기 변수를 현재자판기로 바꾼다
                        before_vending = now_vending;

                        //자판기 이름 출력
                        //만약 보충 완료된 자판기라면 가운데줄 추가 / 아니라면 추가X
                        if(json_object.getInt("sp_check") == 1) {
                            draw_td(1,0,json_object.getString("vd_name"),true);
                        }else {
                            draw_td(1, 0, json_object.getString("vd_name"), false);
                        }
                        Log.e("<<<<",4+" ");
//------------------------------------몸통)자판기 작업지시 작성 공간----------------------------------

                        String note_str = json_object.getString("note");
                        int    sp_check = json_object.getInt("sp_check");

            //---------------------------------작업지시서--------------------------------------------
                        //작업지시가 없을경우 공백 TD 생성
                        if(note_str.equals("null")){

                            //보충이 완료된 자판기인 경우 -> 배경색 회색으로
                            if(sp_check == 1){
                                draw_td(1,0," ",true);
                            }

                            //보충해야될 자판기인 경우 -> 흰 배경색으로
                            else{
                                draw_td(1,0," ",false);
                            }
                            Log.e("<<<<",5+" ");
                        }

                        //작업지시가 있을경우 내용 TD 생성
                        else {

                            //만약 보충 완료된 자판기라면 가운데줄 추가 / 아니라면 추가X
                            if(sp_check == 1) {
                                draw_td(1, 0, note_str, true);
                            }else{
                                draw_td(1, 0, note_str, false);
                            }
                            Log.e("<<<<",6+" ");
                        }
                    }

//---------------------------몸통)자판기 숫자(보충 필요 량) 들어가는 곳--------------------------------

                    //각 음료 이름에 맞는 보충 필요량 받아 오기
                    String now_val      = json_object.getString("drink_name");

                    int sp_check_int    = json_object.getInt("sp_check");

                    //만약 보충완료된 자판기라면 중앙선을 긋는다.
                    if(sp_check_int == 1) {

                        //보충 필요량 출력 TD 생성
                        draw_td(2,product_val.indexOf(now_val)+2,json_object.getString("sp_val"),true);
                        Log.e("<<<<",7+" ");
                    }

                    else {

                        //보충 필요량 출력 TD 생성
                        draw_td(2, product_val.indexOf(now_val) + 2, json_object.getString("sp_val"),false);
                        Log.e("<<<<",8+" ");
                    }

                    //보충 완료된 자판기는 합계에서 뺀다
                    if(sp_check_int != 1) {
                        //해당 제품의 합계를 구한다
                        Log.e("<<<<","A");
                        Log.e("<<<<", product_count.size()+"//"+product_val.size());

                        Integer before_count = product_count.get(product_val.indexOf(now_val));
                        Log.e("<<<<","B");
                        product_count.set(product_val.indexOf(now_val), before_count + json_object.getInt("sp_val"));
                        Log.e("<<<<","C");
                    }
                    Log.e("<<<<",9+" ");
                }


                //TR이 하나라도 생성되어 있다면,
                //현재 테이블에 TR을 추가한다
                if(tr != null) {
                    order_sheet_layout.addView(tr);
                }
                Log.e("<<<<",10+" ");
                //----합계 출력 공간----

                //테이블의 TR태그를 만든다
                tr = new TableRow(context);

                //합계 글자 TD 생성
                draw_td(1,0,"보충 필요량 합계",false);

                for(int i = 0; i<json_result_set.length(); i++){

                    //합계 TD 만들기
                    draw_td(2,i+2, product_count.get(i)+"",false);

                }
                Log.e("<<<<",11+" ");
                //테이블에 TR 적용
                order_sheet_layout.addView(tr);

                Log.e("<<<<",12+" ");
                //테이블의 TR태그를 만든다
                tr = new TableRow(context);
                //합계 글자 TD 생성
                draw_td(1,0,"차량 재고",false);
                Log.e("<<<<",13+" ");
                //자동차 재고를 차례차례 출력한다.
                for(int i = 0; i<car_stock.size(); i++) {
                    draw_td(2, i+2,car_stock.get(i)+"",false);
                }
                //테이블에 TR 적용
                order_sheet_layout.addView(tr);

            }
            Log.e("<<<<",14+" ");
        }catch (Exception e){
            Log.e("<<<<<<<<<<<",e.toString());
            Toast.makeText(context, "Order_sheet_alert Activty err", Toast.LENGTH_SHORT).show();
        }

        //상하 스크롤 (제목과 몸통부분)같이 움직이도록 하는 구문
        scroll_view_bottom.getViewTreeObserver().addOnScrollChangedListener(new ViewTreeObserver.OnScrollChangedListener() {

            //스크롤 될때마다 불려지는 콜백함수
            @Override
            public void onScrollChanged() {

                //스크롤 할때 머리부분도 같이 돌아가도록 한다
                scroll_view_top.scrollTo(scroll_view_bottom.getScrollX(), scroll_view_bottom.getScrollY());

            }

        });
    }

    //td추가하기(선택자, 크기or위치, 출력할 문자열, 중앙선 긋기)
    public void draw_td(int select , int count, String str, boolean strike){
        //TextView 생성
        textView = new TextView(context);

        //여백 지정
        textView.setPadding(5,5,5,5);

        //문자 중앙정렬
        textView.setGravity(Gravity.CENTER);

        //매진임박 자판기인 경우, 배경색을 다르게 한다
        //가로선이 필요한 경우 가로선을 긋는다
        if(strike ==true){
            textView.setBackgroundColor(Color.parseColor("#707070"));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //매진임박이 아닌경우
        else{
            textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
        }

        //속성이 따로 없을 시,
        if(select == 1){
            //속성 생성
            params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
        }

        //특정 위치에 추가하고 싶을 시,
        if(select == 2) {
            //속성 생성
            params = new TableRow.LayoutParams(count);
        }

        //특정 크기로 추가하고 싶을 시,
        if(select == 3){
            params = new TableRow.LayoutParams(count, TableRow.LayoutParams.WRAP_CONTENT);
        }

        //마진 주기
        params.setMargins(margin_size,margin_size,margin_size,margin_size);

        //속성지정
        textView.setLayoutParams(params);

        //값 넣기
        textView.setText(str);

        //TR에 넣기
        tr.addView(textView);
    }
}
