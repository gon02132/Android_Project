package com.study.googlemapsandroidapiexample.Main_Page.Order_sheet_item_list;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.DB_conn;
import com.study.googlemapsandroidapiexample.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

//작업지시서 만들어지는 class
public class Order_sheet_alert{

    //기본 변수들
    private Context                 context;
    private String                  user_login_id;
    private String                  date_String;                        //특정날자를 검색 했을 시, 이 날자로 검색한다
    private boolean                 swap_bg_color           = true;     //한줄한줄 배경을 다르게 해주는 애
    private int                     now_page_print_val      = 0;        //현재 페이지에 가로로 몇개씩 출력되는지

    //테이블 속성 관련 변수 들
    private TextView                textView;
    private ImageButton             angle_left, angle_right, order_exit;
    private TableLayout             order_sheet_layout, title_sheet_layout, total_sheet_layout;
    private TableRow                tr;
    private TableRow.LayoutParams   params;
    private ImageView               now_page_icon;

    //현재 페이지의 위치를 위한 Integer, 테이블 td,tr 간의 간격을 조절 해주기 위한 Integer
    private Integer                 now_page, margin_size   = 2;

    //DB값을 최초한번 저장하고 재사용 하기위한 변수
    private String                  result_str              = "";

    //다이얼로그 관리 변수
    private Dialog                  dig;

    //자판기와 음료수들의 내용들이 들어가는 스크롤뷰 + 토탈정보들도 들어감
    private HorizontalScrollView    scroll_view_top, scroll_view_bottom, scroll_view_total;

    //음료 이름 및 아이콘설명이 들어가는 리스트 뷰
    private ListView                vd_item_list;

    //제품들의 리스트들을 보여주기 위한 어뎁터
    private Osil_Adapter            osil_adapter;

    //자동차 재고량
    private ArrayList<Integer>      car_stock               = new ArrayList<>();
    //제품 이름들이 들어갈 배열
    private ArrayList<String>       product_val             = new ArrayList<>();
    //제품 보충 필요량 합계가 들어갈 배열
    private ArrayList<Integer>      product_count           = new ArrayList<>();


    //-------------------------------------생성자----------------------------------
    public Order_sheet_alert(Context context, String user_login_id){
        this.context                = context;
        this.user_login_id          = user_login_id;
        this.date_String            = "";
    }

    //---생성자 오버로딩-- 특정 날짜를 클릭하여 보는 작업지시서의 경우
    public Order_sheet_alert(Context context, String user_login_id, String date){
        this.context                = context;
        this.user_login_id          = user_login_id;
        this.date_String            = date;
    }

    //테이블 생성!
    @SuppressLint("WrongViewCast")
    public void create_table(final int now_page, final int recycle) {
        if(recycle == 0) {
            //-----------------------------------초기화 부분 -------------------------------------------

            //class변수에 초기화 한다
            this.now_page = now_page;

            //기존에 이미 만들어져 있다면 지운다. -> 예외처리
            if (dig != null) {
                dig.cancel();
                dig = null;
            }

            //다이얼로그 생성(보여주는건 .show()로 보여줌)
            dig = new Dialog(context);

            //타이틀제거(타이틀의 공간차지 방지)
            dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

            //레이아웃 설정
            dig.setContentView(R.layout.orderseet_table_view);

            //배경을 투명색으로 바꾼다.
            dig.getWindow().setBackgroundDrawable(new ColorDrawable(Color.parseColor("#00000000")));

            //작업지시서 왼쪽 맨위에 음료들의 설명이 들어가는 리스트뷰
            vd_item_list        = dig.findViewById(R.id.vd_item_list);

            //작업지시서 레이아웃
            order_sheet_layout  = dig.findViewById(R.id.order_sheet_layout);
            title_sheet_layout  = dig.findViewById(R.id.title_sheet_layout);
            total_sheet_layout  = dig.findViewById(R.id.total_sheet_layout);

            //작업지시서의 좌우 스크롤뷰(머리, 몸통 부분)
            scroll_view_top     = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_top);
            scroll_view_bottom  = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_bottom);
            scroll_view_total   = (HorizontalScrollView) dig.findViewById(R.id.scroll_view_total);

            //페이지를 다가기위한 ImageButton
            order_exit          = (ImageButton) dig.findViewById(R.id.order_exit) ;

            //왼쪽으로 페이지 이동을 위한 ImageButton
            angle_left          = (ImageButton) dig.findViewById(R.id.angle_left);

            //오른쪽으로 페이지 이동을 위한 ImageButton
            angle_right         = (ImageButton) dig.findViewById(R.id.angle_right);

            //맨밑에 현재 페이지를 나타내는 Image view
            now_page_icon       = (ImageView) dig.findViewById(R.id.now_page_icon);

            //"X"버튼 클릭시,
            order_exit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //현재 화면을 나간다 -> 메인화면으로
                    dig.dismiss();
                }
            });

            //"<"버튼 클릭시,
            angle_left.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //테이블들의 모든 자식들을 지운다
                    order_sheet_layout.removeAllViews();
                    title_sheet_layout.removeAllViews();

                    //세로로 회색줄을 구분하기 위한 변수들
                    swap_bg_color   = true;

                    //예외처리) 광클시 1이하로 내려가는데 이를 막기위함, 이외에는 정상 접근이므로 --를 해준다
                    if(Order_sheet_alert.this.now_page < 1){
                        create_table(Order_sheet_alert.this.now_page = 1, 1);
                    }else {
                        create_table(Order_sheet_alert.this.now_page = Order_sheet_alert.this.now_page - 1, 1);
                    }

                }
            });

            //">" 버튼 클릭시,
            angle_right.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    //테이블들의 모든 자식들을 지운다
                    order_sheet_layout.removeAllViews();
                    title_sheet_layout.removeAllViews();

                    //세로로 회색줄을 구분하기 위한 변수들
                    swap_bg_color   = true;

                    //예외처리) 광클시 3이상 올라가는데 이를 막기위함, 이외에는 정상 접근이므로 ++를 해준다
                    if(Order_sheet_alert.this.now_page > 3){
                        create_table(Order_sheet_alert.this.now_page = 3, 1);
                    }else {
                        create_table(Order_sheet_alert.this.now_page = Order_sheet_alert.this.now_page + 1, 1);
                    }
                }
            });

            //커스텀 다이얼로그가 보여진다
            dig.show();
        }

        else{
            //테이블들의 모든 자식들을 삭제한다.
            order_sheet_layout.removeAllViews();
            title_sheet_layout.removeAllViews();
            total_sheet_layout.removeAllViews();
        }
//-----------------------------------초기화 부분 끝-------------------------------------------

        //현재 몇번째 페이지인지 나타내준다.
        switch (now_page){

            case 1:
                now_page_icon.setImageResource(R.drawable.fir_page);
                break;

            case 2:
                now_page_icon.setImageResource(R.drawable.sec_page);
                break;

            case 3:
                now_page_icon.setImageResource(R.drawable.thr_page);
                break;

        }

        //현재 페이지에 대한 뷰 숨김/나타냄
        switch (now_page){

            //맨 왼쪽 페이지 인 경우 "<" 이미지를 없앤다
            case 1:
                //"<" 버튼 숨기기
                if(angle_left.getVisibility() == View.VISIBLE)
                    angle_left.setVisibility(View.GONE);

                //">"버튼 활성화
                if(angle_right.getVisibility() == View.GONE)
                    angle_right.setVisibility(View.VISIBLE);
                break;

            //중간 페이지 인경우 "<" , ">" 둘다 활성화 시킨다.
            case 2:
                //"<"버튼 활성화
                if(angle_left.getVisibility() == View.GONE)
                    angle_left.setVisibility(View.VISIBLE);

                //">"버튼 활성화
                if(angle_right.getVisibility() == View.GONE)
                    angle_right.setVisibility(View.VISIBLE);

                break;

            //맨 오른쪽 페이지 인 경우 ">" 이미지를 없앤다
            case 3:
                //"<" 버튼 숨기기
                if(angle_right.getVisibility() == View.VISIBLE)
                    angle_right.setVisibility(View.GONE);

                //">"버튼 활성화
                if(angle_left.getVisibility() == View.GONE)
                    angle_left.setVisibility(View.VISIBLE);
                break;

        }

        try {

            //db 접속(try/catch 필수)
            DB_conn db_conn_obj = new DB_conn(context);

            //특정 날짜를 검색하지 않는 경우
            if (date_String.equals("") || date_String.equals(" ") || date_String == null) {

                //현재 날짜 구하는 함수 포멧은 ex) 2018-04-25 로 문자열로 변환되어 출력됨
                SimpleDateFormat df = new SimpleDateFormat("yyy-MM-dd", Locale.KOREA);
                //String str_date     = df.format(new Date());
                //String str_date = "2018-06-23";
                String str_date = "2018-06-27";

                //db에 접속하여 반환된 결과값 초기화
                result_str = db_conn_obj.execute("get_order_sheet", user_login_id, str_date).get();
            }

            //특정 날짜를 검색하는 경우
            else {
                //db에 접속하여 반환된 결과값 초기화
                result_str = db_conn_obj.execute("get_order_sheet", user_login_id, date_String).get();
            }


            //받아온 값이 없거나 mysql구문의 에러의 경우 아무것도 실행하지 않고 다음으로 넘어간다
            if (result_str.equals("no_marker") || result_str.equals("mysql_err") || result_str.equals("") || result_str == null) {
                Toast.makeText(context, "no marker or mysql_err", Toast.LENGTH_SHORT).show();

                //"<" 버튼 숨기기
                if(angle_right.getVisibility() == View.VISIBLE)
                    angle_right.setVisibility(View.GONE);

                //">"버튼 숨기기
                if(angle_left.getVisibility() == View.VISIBLE)
                    angle_left.setVisibility(View.GONE);

            }

            //받아온 값이 JSON객체로 있을 경우 -> 테이블을 만든다
            else {

                //제목, 몸통, 총합 순으로 테이블 생성
                draw_title(result_str, now_page);
                draw_body(result_str, now_page);
                draw_total(result_str, now_page);

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
    public void draw_td(int select , int count, String str, String bg_color, float text_size, String font, boolean strike){

        //TextView 생성
        textView = new TextView(context);
        textView.setTypeface(Typeface.createFromAsset(context.getAssets(), font));  //폰트 설정
        textView.setTextSize(text_size);                                            //텍스트 사이즈 설정
        textView.setLetterSpacing((float)-0.05);                                    //자간 설정
        textView.setBackgroundColor(Color.parseColor(bg_color));                    //배경색 설정

        //1,2페이지에서 각 칸마다 구별을 주기위해(세로로 배경색 색칠)
        if(!bg_color.equals("#0064c8") && str.length() < 4 && now_page != 3 && !str.equals("")){

            //흰색으로 색칠하기
            if(swap_bg_color == true){
                textView.setBackgroundColor(Color.parseColor("#FFFFFF"));
                swap_bg_color = !swap_bg_color;
            }

            //회색으로 색칠하기
            else{
                textView.setBackgroundColor(Color.parseColor("#F2F2F2"));
                swap_bg_color = !swap_bg_color;
            }


        }

        //고정 바인경우 색상을 흰색으로 바꾼다.
        if(bg_color.equals("#0064c8")){
            textView.setTextColor(Color.parseColor("#FFFFFF"));
        }

        //여백 지정
        textView.setPadding(5,5,5,5);

        //자판기 이름 출력시 오른쪽정렬
        if(str.length() > 3) {
            textView.setGravity(Gravity.RIGHT);
        }

        //수량 출력시 중앙정렬
        else {
            textView.setGravity(Gravity.CENTER);
        }

        //보충 완료된 자판기는 배경색을 다르게 한다
        //가로선이 필요한 경우 가로선을 긋는다
        if(strike ==true){
            textView.setBackgroundColor(Color.parseColor("#707070"));
            textView.setPaintFlags(textView.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
        }

        //속성이 따로 없을 시,
        if(select == 1){
            //속성 생성
            params = new TableRow.LayoutParams(125, TableRow.LayoutParams.WRAP_CONTENT);
        }

        //특정 위치에 추가하고 싶을 시,
        if(select == 2) {
            //속성 생성
            params = new TableRow.LayoutParams(count);
        }

        //특정 크기로 추가하고 싶을 시,
        if(select == 3){
            if(str.equals(context.getString(R.string.add_need_all))){
                params = new TableRow.LayoutParams(count, 130);
            }
            else{
                params = new TableRow.LayoutParams(count, TableRow.LayoutParams.WRAP_CONTENT);
            }

        }

        //마진 주기
        if(bg_color.equals("#0064c8")){
            params.setMargins(margin_size,margin_size,margin_size,margin_size);
        }else {
            //params.setMargins(0, margin_size, 0, margin_size);
        }

        //속성지정
        textView.setLayoutParams(params);

        //값 넣기
        textView.setText(str);

        //TR에 넣기
        tr.addView(textView);
    }

    //td추가하기(선택자, 크기or위치, 출력할 문자열, 중앙선 긋기)
    public void draw_td_image(int select , int count, String bg_color, String img_select){

        tr.setBackgroundColor(Color.parseColor(bg_color));

        //TextView 생성
        ImageView imageView = new ImageView(context);

        //여백 지정
        imageView.setPadding(5,5,5,5);


        //속성이 따로 없을 시,
        if(select == 1){
            //속성 생성
            params = new TableRow.LayoutParams(79,79);
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

        //세로 중앙정렬
        params.gravity = Gravity.CENTER_VERTICAL;

        //속성지정
        imageView.setLayoutParams(params);

        //사진 다 보이도록
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

        //배경만 출력하고 싶은 경우에 얘를 호출
        if(img_select.equals("back")){
            //값 넣기
            //imageView.setImageResource(R.drawable.japangi2);
            com.squareup.picasso.Picasso.with(context)
                    .load("http://52.78.198.67/images/drink/" + img_select + ".png")
                    .into(imageView);

        }

        //배경 + 음료까지 출력하고 싶을 경우 얘를 호출
        else {
            //값 넣기
            //imageView.setImageResource(R.drawable.japangi2);
            com.squareup.picasso.Picasso.with(context)
                    .load("http://52.78.198.67/images/drink/" + img_select + "_back.png")
                    .into(imageView);

        }
        //TR에 넣기
        tr.addView(imageView);
    }

    //제목 부분 생성 및 출력
    public void draw_title(String result_str, int now_page){

        //예외처리) 배열을 초기화함으로 무한 증식을 방지한다.
        car_stock.clear();
        product_val.clear();
        product_count.clear();

        try {
            //json 객체로 변환하여 json배열에 저장
            JSONObject jsonObject  = new JSONObject(result_str);

            //drk_name, stock -> 제품명, 갯수 반환
            JSONArray  json_result_set = jsonObject.getJSONArray("product_all");

            //구분자에 따른 다른 출력(1,2,3페이지)
            switch (now_page) {

                //첫번째,두번째 페이지
                case 1:
                case 2:

                    //고정바와 메인바 2개 반복하여 테이블 생성
                    for(int a=0; a<2; a++) {

                        //최초한번, 모든 음료 종류를 배열에 저장한다
                        if(a==0) {
                            for (int i = 0; i < json_result_set.length(); i++) {
                                //차량 재고를 배열에 저장해둔다(맨밑에 출력하기위해)

                                //drk_name, stock 가 저장되어 있음
                                JSONObject json_object = json_result_set.getJSONObject(i);

                                //현재 모든 제품들의 이름들을 가져온다.
                                String drk_name = json_object.getString("drk_name");

                                car_stock.add(json_object.getInt("stock"));

                                //제품들을 배열에 넣는다
                                product_val.add(drk_name);

                                //각 제품들의 보충 필요량들을 0으로 초기화한다
                                product_count.add(0);
                            }
                        }

                        //테이블의 TR태그를 만든다
                        tr = new TableRow(context);

                        //"자판기 명" TD 생성
                        draw_td(3,600, "Vending Machine\nName","#0064c8", 16, "fonts/Futura Heavy font.ttf", false);

                        //첫페이지냐 두번째 페이지냐에 따라 시작점이 틀려짐
                        int for_i = (now_page == 1) ? 0 : 6;

                        //한화면에 출력되는 양을 조절하기 위함
                        int result_arr_size = 0;

                        //첫번째 페이지 + 음료 종류가 6개 이상인경우 인경우
                        if(now_page == 1 && product_val.size() > 5){
                            result_arr_size = 6;
                        }

                        //두번째 페이지 혹은 종류가 6개 미만인경우 인경우
                        else if(now_page == 2 || product_val.size() < 5){
                            result_arr_size = product_val.size();
                        }

                        now_page_print_val  = result_arr_size-for_i;

                        //받아온 값만큼 반복한다.
                        for (int i = for_i; i < result_arr_size; i++) {
                            //drk_name, stock 가 저장되어 있음
                            JSONObject json_object = json_result_set.getJSONObject(i);

                            //현재 모든 제품들의 이름들을 가져온다.
                            String drk_name = json_object.getString("drk_name");

                            //아이콘 출력
                            draw_td_image(1,0, "#0064c8", drk_name);

                        }

                        //6개씩 맞춰서 출력하기위해 한페이지당 6개이하의 제품이 있다면 나머지는 공백으로 놔둔다
                        if(result_arr_size - for_i < 7){
                            for(int i = 0; i < (6-(result_arr_size - for_i)); i++){
                                //아이콘 출력
                                draw_td_image(1,0, "#0064c8", "back");
                            }
                        }

                        //반복문마다 다른 레이아아웃에 추가
                        if(a==0){
                            //테이블에 TR을 적용시킨다.
                            order_sheet_layout.addView(tr);

                        }
                        else if(a==1){
                            //테이블에 TR을 적용시킨다.
                            title_sheet_layout.addView(tr);
                        }
                    }
                    break;

                //마지막 페이지
                case 3:

                    //고정바와 메인바 2개 반복하여 테이블 생성
                    for(int a=0; a<2; a++) {
                        //테이블의 TR태그를 만든다
                        tr = new TableRow(context);

                        //"자판기 명" TD 생성
                        draw_td(3,600, "Vending Machine\nName","#0064c8", 16, "fonts/Futura Heavy font.ttf", false);

                        //"작업 지시" TD 생성
                        draw_td(3,770, context.getString(R.string.work_order),"#0064c8", 16, "fonts/Futura Heavy font.ttf", false);

                        //반복문마다 다른 레이아아웃에 추가
                        if(a==0){
                            //테이블에 TR을 적용시킨다.
                            order_sheet_layout.addView(tr);
                        }
                        else if(a==1){
                            //테이블에 TR을 적용시킨다.
                            title_sheet_layout.addView(tr);
                        }

                    }
                    break;

            }
        }catch (Exception e){
            Toast.makeText(context, "error!", Toast.LENGTH_SHORT).show();
            Log.e("<><>",e.toString());
        }

    }

    //몸통 부분 생성 및 출력
    public void draw_body(String result_str, int now_page){

        try {

            //json 객체로 변환하여 json배열에 저장
            JSONObject jsonObject  = new JSONObject(result_str);

            //제품들 가져오기
            JSONArray  json_result = jsonObject.getJSONArray("result");

            //구분자에 따른 다른 출력(1,2,3페이지)
            switch (now_page) {

                case 1:
                case 2:
                    //테이블의 TR태그를 초기화 한다
                    tr = null;

                    //한 행마다 제품 값이 들어갈 배열
                    ArrayList<Integer> one_line_list = new ArrayList<Integer>();

                    //음료 종류만큼 초기화(안할경우 에러남)
                    for(int i=0; i<product_val.size(); i++){
                        one_line_list.add(i, -1);
                    }

                    int    sp_check_int     = 1;    //보충이 완료된 자판기인지 아닌지 구분해준다.

                    String now_vending      = "";   //현재 자판기 명
                    String before_vending   = "";   //이전 자판기 명
                    String now_val          = "";   //음료이름


                    //받아온 값만큼 반복한다.
                    for(int i = 0; i < json_result.length(); i++){

                        //sp_name, vd_name, drink_name, drink_path, sp_val, drink_line, note, sp_check 가 저장되어 있음
                        JSONObject json_object  = json_result.getJSONObject(i);

                        //현재 자판기의 이름을 가져온다.
                        now_vending      = json_object.getString("vd_name");

                        //음료 이름가져오기
                        now_val          = json_object.getString("drink_name");

                        //만약 새로운 자판기 라면 행을 그린다 + 첫번째 반복문은 건너뛴다
                        if(!before_vending.equals(now_vending) && i > 0) {

                                //첫페이지냐 두번째 페이지냐에 따라 시작점이 틀려짐
                                int for_j = (now_page == 1) ? 0 : 6;

                                //한화면에 출력되는 양을 조절하기 위함
                                int result_arr_size = 0;

                                //첫번째 페이지 + 음료 종류가 6개 이상인경우 인경우
                                if(now_page == 1 && product_val.size() > 5){
                                    result_arr_size = 6;
                                }

                                //두번째 페이지 혹은 종류가 6개 미만인경우 인경우
                                else if(now_page == 2 || product_val.size() < 5){
                                    result_arr_size = product_val.size();
                                }

                                //테이블의 TR태그를 만든다
                                tr = new TableRow(context);

                                //보충완료했는 경우/ 하지않은경우를 나누어 자판기이름을 출력한다.
                                draw_td(1,0,before_vending,"#FFFFFF", 12,"fonts/YoonGothic750.ttf" ,(sp_check_int == 1) ? true : false);


                                //현 자판기의 음료수량들을 출력한다
                                for(int j=for_j; j<result_arr_size; j++){

                                    //만약 이 자판기에 반복문의 음료가 존재하지 않는경우 "-" 를 출력하고
                                    //존재할경우 값을 출력한다
                                    if(one_line_list.get(j) == -1){
                                        draw_td(1,0,"-","#FFFFFF", 16,"fonts/Futura Heavy Italic font.ttf",(sp_check_int == 1) ? true : false);
                                    }else{
                                        draw_td(1,0,one_line_list.get(j).toString(),"#FFFFFF", 16,"fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                                    }

                                }


                                //6개씩 맞춰서 출력하기위해 한페이지당 6개이하의 제품이 있다면 나머지는 공백으로 놔둔다
                                if(result_arr_size - for_j < 7){
                                    for(int j = 0; j < (6-(result_arr_size - for_j)); j++){
                                        //빈 수량 출력
                                        draw_td(1,0,"-","#FFFFFF", 16,"fonts/Futura Heavy Italic font.ttf",(sp_check_int == 1) ? true : false);
                                    }
                                }



                                //줄 긋기(위아래 TR 구분선)
                                View v = new View(context);
                                v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,3));
                                v.setBackgroundColor(Color.parseColor("#000000"));
                                order_sheet_layout.addView(v);

                                order_sheet_layout.addView(tr);

                                for(int j=0; j<product_val.size(); j++){
                                    one_line_list.set(j, -1);
                                }

                        }

                        //보충이 완료되었는지 체크
                        sp_check_int     = json_object.getInt("sp_check");

//---------------------------몸통)자판기 숫자(보충 필요 량) 들어가는 곳--------------------------------

                        one_line_list.set(product_val.indexOf(now_val), json_object.getInt("sp_val"));

                        //보충 미완료 자판기만 계속 합을 구한다.
                        if(sp_check_int != 1) {
                            Integer before_count = product_count.get(product_val.indexOf(now_val));
                            product_count.set(product_val.indexOf(now_val), before_count + json_object.getInt("sp_val"));
                        }

                        //이전 자판기 변수를 현재자판기로 바꾼다
                        before_vending = now_vending;

                        //값을 전부 받아 왔을 때(배열에 전부 저장 되었을 경우) 실행
                        if(i == json_result.length()-1){

                            //첫페이지냐 두번째 페이지냐에 따라 시작점이 틀려짐
                            int for_j = (now_page == 1) ? 0 : 6;

                            //한화면에 출력되는 양을 조절하기 위함
                            int result_arr_size = 0;

                            //첫번째 페이지 + 음료 종류가 6개 이상인경우 인경우
                            if(now_page == 1 && product_val.size() > 5){
                                result_arr_size = 6;
                            }

                            //두번째 페이지 혹은 종류가 6개 미만인경우 인경우
                            else if(now_page == 2 || product_val.size() < 5){
                                result_arr_size = product_val.size();
                            }

                            //테이블의 TR태그를 만든다
                            tr = new TableRow(context);

                            //자판기 이름부터 출력 / 보충완료했는지 안했는지에 따라 결과값이 바뀐다
                                draw_td(3,600,now_vending,"#FFFFFF", 12,"fonts/YoonGothic750.ttf", (sp_check_int == 1) ? true : false);


                            //받아온 값만큼 출력한다(제품 수량 부분)
                            for(int j=for_j; j<result_arr_size; j++){

                                if(one_line_list.get(j) == -1) {
                                    draw_td(1, 0, "-", "#FFFFFF", 16,"fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                                }else{
                                    draw_td(1, 0, one_line_list.get(j).toString(), "#FFFFFF", 16,"fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                                }

                            }

                            //6개씩 맞춰서 출력하기위해 한페이지당 6개이하의 제품이 있다면 나머지는 공백으로 놔둔다
                            if(result_arr_size - for_j < 7){
                                for(int j = 0; j < (6-(result_arr_size - for_j)); j++){
                                    //빈 수량 출력
                                    draw_td(1,0,"-","#FFFFFF", 16,"fonts/Futura Heavy Italic font.ttf",(sp_check_int == 1) ? true : false);
                                }
                            }


                            //줄 긋기(위아래 TR 구분선)
                            View v = new View(context);
                            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,3));
                            v.setBackgroundColor(Color.parseColor("#000000"));

                            order_sheet_layout.addView(v);

                            //테이블에 tr을 넣는다
                            order_sheet_layout.addView(tr);

                        }

                    }

                    //첫페이지냐 두번째 페이지냐에 따라 시작점이 틀려짐
                    int for_j = (now_page == 1) ? 0 : 6;

                    //한화면에 출력되는 양을 조절하기 위함
                    int result_arr_size = 0;

                    //첫번째 페이지 + 음료 종류가 6개 이상인경우 인경우
                    if(now_page == 1 && product_val.size() > 5){
                        result_arr_size = 6;
                    }

                    //두번째 페이지 혹은 종류가 6개 미만인경우 인경우
                    else if(now_page == 2 || product_val.size() < 5){
                        result_arr_size = product_val.size();
                    }

                    //고정바와 움직이는바를 동시에 만든다.
                    for(int i=0; i<2; i++) {

                        //테이블의 TR태그를 만든다
                        tr = new TableRow(context);

                        //움직이는 바는 안보이게, 고정바는 파란색배경으로 보이게 출력한다
                        if(i == 0){
                            //합계 글자 TD 생성
                            draw_td(3, 600, "", "#FFFFFF", 13, "fonts/YoonGothic760.ttf", false);
                        }else {
                            //합계 글자 TD 생성
                            draw_td(3, 600, context.getString(R.string.add_need_all), "#0064c8", 13, "fonts/YoonGothic760.ttf", false);
                        }

                        //지정된 횟수만큼 합계의 수량들을  출력한다.
                        for (int j = for_j; j < result_arr_size; j++) {

                            //움직이는 바는 안보이게, 고정바는 파란색배경으로 보이게 출력한다
                            if(i == 0){
                                //합계 TD 만들기
                                draw_td(1, 0, "", "#FFFFFF", 20, "fonts/Futura Heavy Italic font.ttf", false);

                            }else {
                                //합계 TD 만들기
                                draw_td(1, 0, product_count.get(j) + "", "#0064c8", 20, "fonts/Futura Heavy Italic font.ttf", false);
                            }

                        }

                        //6개씩 맞춰서 출력하기위해 한페이지당 6개이하의 제품이 있다면 나머지는 공백으로 놔둔다
                        if(result_arr_size - for_j < 7){
                            for(int j = 0; j < (6-(result_arr_size - for_j)); j++){

                                //움직이는 바는 안보이게, 고정바는 파란색배경으로 보이게 출력한다
                                if(i == 0){
                                    //합계 TD 만들기
                                    draw_td(1, 0, "", "#FFFFFF", 20, "fonts/Futura Heavy Italic font.ttf", false);

                                }else {
                                    //합계 TD 만들기
                                    draw_td(1, 0,  "-", "#0064c8", 20, "fonts/Futura Heavy Italic font.ttf", false);
                                }

                            }
                        }


                        //고정바와 메인바 둘 다 적용 시킨다
                        if(i == 0) {
                            //테이블에 TR 적용
                            order_sheet_layout.addView(tr);
                        }else if(i == 1){
                            //테이블에 TR 적용
                            total_sheet_layout.addView(tr);
                        }

                    }
                    break;

                case 3:
                    break;

            }

        }catch (Exception e){
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
            Log.e("<><>",e.toString());
        }

    }

    //토탈 부분 생성 및 출력
    public void draw_total(String result_str, int now_page){
        String before_vending   = "";
        String now_vending      = "";
        String save_note        = "";

        //보충이 완료된 자판기인지 아닌지 구분해준다.
        int    sp_check_int = 1;

        try {
            //json 객체로 변환하여 json배열에 저장
            JSONObject jsonObject  = new JSONObject(result_str);

            //구분자에 따른 다른 출력(1,2,3페이지)
            switch (now_page) {

                case 1:
                case 2:
                    //맨밑 고정바를 보이게 한다
                    if(total_sheet_layout.getVisibility() == View.GONE)
                        total_sheet_layout.setVisibility(View.VISIBLE);

                    break;

                case 3:
                    //맨 밑 고정바를 숨긴다.
                    if(total_sheet_layout.getVisibility() == View.VISIBLE)
                        total_sheet_layout.setVisibility(View.GONE);

                    //제품들 가져오기
                    JSONArray  json_result = jsonObject.getJSONArray("result");

                    //받아온 값만큼 반복한다.
                    for(int i = 0; i < json_result.length(); i++){

                        //sp_name, vd_name, drink_name, drink_path, sp_val, drink_line, note, sp_check 가 저장되어 있음
                        JSONObject json_object  = json_result.getJSONObject(i);

                        //현재 자판기의 이름을 가져온다.
                        now_vending             = json_object.getString("vd_name");

                        //작업지시가 들어있는 테이블을 가져온다.
                        String note             = json_object.getString("note");

                        //만약 새로운 자판기 라면 행을 그린다
                        if(!before_vending.equals(now_vending) && i > 0) {

                            //테이블의 TR태그를 만든다
                            tr = new TableRow(context);

                            //자판기 이름 출력
                            //보충완료 자판기인지 아닌자판기인지에 따라 결과값을 다르게 출력한다.
                            draw_td(1,0,before_vending,"#FFFFFF", 12,"fonts/YoonGothic750.ttf", (sp_check_int == 1) ? true : false);

                            //작업지시 내용 출력
                            if(save_note.equals("") || save_note.equals(" ")) {
                                draw_td(1, 0, "-", "#FFFFFF", 16, "fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                            }else {
                                draw_td(1, 0, save_note, "#FFFFFF", 16, "fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                            }

                            //줄 긋기(위아래 TR 구분선)
                            View v = new View(context);
                            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,3));
                            v.setBackgroundColor(Color.parseColor("#000000"));
                            order_sheet_layout.addView(v);

                            //tr을 테이블에 저장
                            order_sheet_layout.addView(tr);

                            //작업지시 문자열을 초기화한다
                            save_note = "";

                        }

                        //보충이 완료되었는지 체크
                        sp_check_int     = json_object.getInt("sp_check");

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

                        //반복문의 마지막 애한테 접근시
                        if(i == json_result.length()-1){

                            //테이블의 TR태그를 만든다
                            tr = new TableRow(context);

                            //자판기 이름 출력
                            //보충완료 자판기인지 아닌자판기인지에 따라 결과값을 다르게 출력한다.
                            draw_td(1,0,before_vending,"#FFFFFF", 12,"fonts/YoonGothic750.ttf", (sp_check_int == 1) ? true : false);

                            //작업지시 내용 출력
                            if(save_note.equals("") || save_note.equals(" ")) {
                                draw_td(1, 0, "-", "#FFFFFF", 16, "fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                            }else {
                                draw_td(1, 0, save_note, "#FFFFFF", 16, "fonts/Futura Heavy Italic font.ttf", (sp_check_int == 1) ? true : false);
                            }

                            //줄 긋기(위아래 TR 구분선)
                            View v = new View(context);
                            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,3));
                            v.setBackgroundColor(Color.parseColor("#000000"));
                            order_sheet_layout.addView(v);

                            //tr을 테이블에 저장
                            order_sheet_layout.addView(tr);
                        }

                        //이전 자판기를 업데이트한다
                        before_vending = now_vending;
                    }
                    break;

            }
        }catch (Exception e){
            Toast.makeText(context, "error", Toast.LENGTH_SHORT).show();
        }

    }

}
