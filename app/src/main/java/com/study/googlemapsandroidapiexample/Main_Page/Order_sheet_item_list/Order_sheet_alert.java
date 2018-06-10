package com.study.googlemapsandroidapiexample.Main_Page.Order_sheet_item_list;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.DB_conn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

//작업지시서 만들어지는 class
public class Order_sheet_alert {

    //기본 변수들
    private Context                 context;
    private String                  user_login_id;
    private String                  date_String;           //특정날자를 검색 했을 시, 이 날자로 검색한다

    //테이블 속성 관련 변수 들
    private TableLayout             order_sheet_layout, title_sheet_layout;
    private TableRow                tr;
    private TextView                textView;
    private TableRow.LayoutParams   params;
    private Integer                 margin_size = 2;

    //자판기와 음료수들의 내용들이 들어가는 스크롤뷰
    private HorizontalScrollView    scroll_view_top, scroll_view_bottom;

    //음료 이름 및 아이콘설명이 들어가는 리스트 뷰
    private ListView                vd_item_list;

    //제품들의 리스트들을 보여주기 위한 어뎁터
    private Osil_Adapter            osil_adapter;

    //자동차 재고량
    private ArrayList<Integer>      car_stock       = new ArrayList<>();
    //제품 이름들이 들어갈 배열
    private ArrayList<String>       product_val     = new ArrayList<>();
    //제품 보충 필요량 합계가 들어갈 배열
    private ArrayList<Integer>      product_count   = new ArrayList<>();


    //-------------------------------------생성자----------------------------------
    public Order_sheet_alert(Context context, String user_login_id){
        this.context        = context;
        this.user_login_id  = user_login_id;
        this.date_String    = "";
    }

    //---생성자 오버로딩-- 특정 날짜를 클릭하여 보는 작업지시서의 경우
    public Order_sheet_alert(Context context, String user_login_id, String date){
        this.context               = context;
        this.user_login_id         = user_login_id;
        this.date_String           = date;
    }

    //테이블 생성!
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


        //작업지시서 왼쪽 맨위에 음료들의 설명이 들어가는 리스트뷰
        vd_item_list = dig.findViewById(R.id.vd_item_list);

        //작업지시서의 좌우 스크롤뷰(머리, 몸통 부분)
        scroll_view_top     = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_top);
        scroll_view_bottom  = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_bottom);

        //커스텀 다이얼로그가 보여진다
        dig.show();

        try {
            //DB에서 쿼리결과String을 받아올 애
            String result_str = "";

            //db 접속(try/catch 필수)
            DB_conn db_conn_obj = new DB_conn(context);
            //특정 날짜를 검색하지 않는 경우
            if(date_String.equals("") || date_String.equals(" ") || date_String == null) {

                //현재 날짜 구하는 함수 포멧은 ex) 2018-04-25 로 문자열로 변환되어 출력됨
                SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd", Locale.KOREA);
                //String str_date     = df.format(new Date());
                //String str_date     = "2018-05-16";
                String str_date     = "2018-06-10";

                //db에 접속하여 반환된 결과값 초기화
                result_str = db_conn_obj.execute("get_order_sheet", user_login_id, str_date).get();
            }

            //특정 날짜를 검색하는 경우
            else{
                //db에 접속하여 반환된 결과값 초기화
                result_str = db_conn_obj.execute("get_order_sheet", user_login_id, date_String).get();
            }

            //받아온 값이 없거나 mysql구문의 에러의 경우 아무것도 실행하지 않고 다음으로 넘어간다
            if (result_str.equals("no_marker") || result_str.equals("mysql_err") || result_str.equals("") || result_str == null) {
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
                        //draw_td(1,0, drk_name,false);
                        draw_td_image(1,0, drk_name);

                    }

                    //------작업지시 TD 넣는 공간------

                    //"작업 지시" TD 생성
                    draw_td(3,700, "작업 지시", false);

                    if(a==0){
                        //테이블에 TR을 적용시킨다.
                        order_sheet_layout.addView(tr);
                    }
                    else if(a==1){
                        //테이블에 TR을 적용시킨다.
                        title_sheet_layout.addView(tr);
                    }
                }

                //제품들의 이름및 아이콘을 나타내기 위한 어뎁터 생성
                osil_adapter = new Osil_Adapter(context, product_val);

                //어뎁터에 따른 리스트 뷰 생성
                vd_item_list.setAdapter(osil_adapter);

//--------------------------------------------몸통 부분 ---------------------------------------------
                //제품들 가져오기
                JSONArray  json_result = jsonObject.getJSONArray("result");
                //새로운 자판기의 이름이 나오면 TR을 새롭게 만들게 하기위한 비교용 String
                String before_vending  = "";

                //작업지시가 축적되어 출력될 공간
                String save_note       = "";

                //테이블의 TR태그를 초기화 한다
                tr = null;

                int sp_check_int    = 0;
                int last_check_int  = 99;

                //받아온 값만큼 반복한다.
                for(int i = 0; i < json_result.length(); i++){
                    //sp_name, vd_name, drink_name, drink_path, sp_val, drink_line, note, sp_check 가 저장되어 있음
                    JSONObject json_object  = json_result.getJSONObject(i);

                    //현재 자판기의 이름을 가져온다.
                    String now_vending      = json_object.getString("vd_name");

                    //각 음료 이름에 맞는 보충 필요량 받아 오기
                    String now_val          = json_object.getString("drink_name");

                    sp_check_int            = json_object.getInt("sp_check");

                    //만약 새로운 자판기 라면 TR태그를 새로만든다.
                    if(!before_vending.equals(now_vending)) {
//----------------------------------몸통)자판기 명 들어가는 곳----------------------------------------
                        //처음 통과시 tr이 준비되지 않았으므로 통과시키기 위함
                        if(tr != null){
//----------------------------------------작업지시서 들어가는 공간------------------------------------
                            //맨 마지막 td는 작업지시를 모아둔 String을 넣는 공간이다.
                            //만약 작업지시가 있는 자판기인 경우에 모아둔 String을 출력한다.
                            if(!save_note.equals("") && !save_note.equals("") && !save_note.equals("null") && save_note != null){

                                //이미 작업 완료된 자판기라면 가운데 줄을 긋는다
                                if(last_check_int == 1) {
                                    draw_td(2, json_result_set.length() + 1, save_note, true);
                                }

                                //작업이 완료되지 않은 경우 가운데 줄을 긋지 않는다
                                else{
                                    draw_td(2, json_result_set.length() + 1, save_note, false);
                                }

                                //다음 사용을 위해 모아놓는 String은 초기화 해둔다
                                save_note = "";
                            }

                            //만약 작업지시가 없는 자판기의경우 공백을 출력한다.
                            else{
                                //작업지시의 배경색을 회색으로 하느냐 흰색으로 하느냐 구분하기위한 if/else 문

                                //이미 작업 완료된 자판기라면 가운데 줄을 긋는다
                                if(last_check_int == 1) {
                                    draw_td(2, json_result_set.length() + 1, " ", true);
                                }

                                //작업이 완료되지 않은 경우 가운데 줄을 긋지 않는다
                                else{
                                    draw_td(2, json_result_set.length() + 1, save_note, false);
                                }

                                //다음 사용을 위해 모아놓는 String은 초기화 해둔다
                                save_note = "";

                            }
//-------------------------------------------------------------------------------------------------

                            //이때까지 TR에 저장해둔 TD들을 TABLE에 저장한다!
                            order_sheet_layout.addView(tr);

                        }
                        //테이블의 TR태그를 만든다
                        tr = new TableRow(context);

                        //이전 자판기 변수를 현재자판기로 바꾼다
                        before_vending = now_vending;

                        //자판기 이름 출력
                        //만약 보충 완료된 자판기라면 가운데줄 추가 / 아니라면 추가X
                        if(json_object.getInt("sp_check") == 1) {
                            draw_td(1,0,json_object.getString("vd_name"),true);
                        }else {
                            draw_td(1, 0, json_object.getString("vd_name"), false);
                        }
                    }

//---------------------------몸통)자판기 숫자(보충 필요 량) 들어가는 곳--------------------------------
                    //작업지시가 들어있는 테이블을 가져온다.
                    String note = json_object.getString("note");

                    //만약 작업지시가 있다면 작업지시 저장String에 저장 해 둔다.
                    if(note != null && !note.equals(" ") && !note.equals("null")) {

                        //작업지시가 여러번 있을수 있는데, 만약 그중 첫번째 작업 지시 인 경우
                        if(save_note.equals("") || save_note.equals(" ")){
                            save_note = json_object.getString("note");
                        }

                        //작업지시가 2개이상 있는경우, 개행을하여 추가한다
                        else {
                            save_note += "\r\n"+json_object.getString("note");
                        }

                    }
                    //만약 보충완료된 자판기라면 중앙선을 긋는다.
                    if(sp_check_int == 1) {

                        //보충 필요량 출력 TD 생성
                        draw_td(2,product_val.indexOf(now_val) + 1,json_object.getString("sp_val"),true);
                    }

                    else {

                        //보충 필요량 출력 TD 생성
                        draw_td(2, product_val.indexOf(now_val) + 1, json_object.getString("sp_val"),false);
                    }

                    //보충 완료된 자판기는 합계에서 뺀다
                    if(sp_check_int != 1) {
                        //해당 제품의 합계를 구한다

                        Integer before_count = product_count.get(product_val.indexOf(now_val));
                        product_count.set(product_val.indexOf(now_val), before_count + json_object.getInt("sp_val"));
                    }

                    last_check_int = sp_check_int;
                }
                //----------------------------------------작업지시서 들어가는 공간 2------------------------------------
                //이전에 반복문에서 처리하지 못한 마지막 행의 작업지시서를 초기화한다
                //함수로 해서 재사용 해도 되지만 2개 밖에 없기 때문에, 그리고 동적으로 값이 자주 바뀌지 않기 때문에
                //고정으로 2개를 만듦.

                //맨 마지막 td는 작업지시를 모아둔 String을 넣는 공간이다.
                //만약 작업지시가 있는 자판기인 경우에 모아둔 String을 출력한다.
                if(!save_note.equals("") && !save_note.equals("") && !save_note.equals("null") && save_note != null){

                    //이미 작업 완료된 자판기라면 가운데 줄을 긋는다
                    if(sp_check_int == 1) {
                        draw_td(2, json_result_set.length() + 1, save_note, true);
                    }

                    //작업이 완료되지 않은 경우 가운데 줄을 긋지 않는다
                    else{
                        draw_td(2, json_result_set.length() + 1, save_note, false);
                    }

                }

                //만약 작업지시가 없는 자판기의경우 공백을 출력한다.
                else{

                    //이미 작업 완료된 자판기라면 가운데 줄을 긋는다
                    if(sp_check_int == 1) {
                        draw_td(2, json_result_set.length() + 1, " ", true);
                    }

                    //작업이 완료되지 않은 경우 가운데 줄을 긋지 않는다
                    else{
                        draw_td(2, json_result_set.length() + 1, save_note, false);
                    }

                }
//-------------------------------------------------------------------------------------------------
                //TR이 하나라도 생성되어 있다면,
                //현재 테이블에 TR을 추가한다
                if(tr != null) {
                    order_sheet_layout.addView(tr);
                }
                //----합계 출력 공간----

                //테이블의 TR태그를 만든다
                tr = new TableRow(context);

                //합계 글자 TD 생성
                draw_td(1,0,"보충 필요량 합계",false);
                for(int i = 0; i<json_result_set.length(); i++){

                    //합계 TD 만들기
                    draw_td(2,i + 1, product_count.get(i)+"",false);

                }
                //테이블에 TR 적용
                order_sheet_layout.addView(tr);

                //테이블의 TR태그를 만든다
                tr = new TableRow(context);

                //합계 글자 TD 생성
                draw_td(1,0,"차량 재고",false);

                //자동차 재고를 차례차례 출력한다.
                for(int i = 0; i<car_stock.size(); i++) {
                    draw_td(2, i + 1,car_stock.get(i)+"",false);
                }

                //테이블에 TR 적용
                order_sheet_layout.addView(tr);

            }
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

        //보충 완료된 자판기는 배경색을 다르게 한다
        //가로선이 필요한 경우 가로선을 긋는다
        if(strike ==true){
            textView.setBackgroundColor(Color.parseColor("#707070"));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //보충완료가 되지 않았을 경우
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

    //td추가하기(선택자, 크기or위치, 출력할 문자열, 중앙선 긋기)
    public void draw_td_image(int select , int count, String img_select){
        //TextView 생성
        ImageView imageView = new ImageView(context);

        //여백 지정
        imageView.setPadding(5,5,5,5);


        //속성이 따로 없을 시,
        if(select == 1){
            //속성 생성
            params = new TableRow.LayoutParams(70,70);
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
        params.setMargins(25,margin_size,25,margin_size);

        //속성지정
        imageView.setLayoutParams(params);

        //값 넣기
        imageView.setImageResource(R.drawable.japangi2);

        tr.setBackgroundColor(Color.WHITE);

        //TR에 넣기
        tr.addView(imageView);
    }

}
