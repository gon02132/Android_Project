package com.study.googlemapsandroidapiexample.Main_Page;

import android.app.Dialog;
import android.content.Context;
import android.util.Log;
import android.view.Window;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.db_conn;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

public class Order_sheet_alert {
    private Context     context;
    private TableLayout order_sheet_layout;
    private String      user_login_id;
    private TableRow    tr;
    private TextView    textView;

    private ArrayList<String> product_val = new ArrayList<>();

    public Order_sheet_alert(Context context, String user_login_id){
        this.context = context;
        this.user_login_id = user_login_id;

    }

    public void create_table() {
        final Dialog dig = new Dialog(context);

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.orderseet_table_view);

        //작업지시서 레이아웃
        order_sheet_layout = dig.findViewById(R.id.order_sheet_layout);


        //커스텀 다이얼로그가 보여진다
        dig.show();

        try {
            //db 접속(try/catch 필수)
            db_conn db_conn_obj = new db_conn(context);

            //db에 접속하여 반환된 결과값 초기호
            String result_str = db_conn_obj.execute("get_order_sheet", user_login_id).get();


            //받아온 값이 없거나 mysql구문의 에러의 경우 아무것도 실행하지 않고 다음으로 넘어간다
            if (result_str.equals("no_marker") || result_str.equals("mysql_err")) {
                Toast.makeText(context, "no marker or mysql_err", Toast.LENGTH_SHORT).show();
            }

            //받아온 값이 JSON객체로 있을 경우 -> 테이블을 만든다
            else {
                //json 객체로 변환하여 json배열에 저장
                JSONObject jsonObject  = new JSONObject(result_str);

                //drk_name, stock -> 제품명, 갯수 반환
                JSONArray  json_result_set = jsonObject.getJSONArray("product_all");

                //테이블의 TR태그를 만든다
                tr = new TableRow(context);


                textView = new TextView(context);

                //속성 지정
                textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                //값 넣기
                textView.setText("자판기 명");

                //TR에 추가
                tr.addView(textView);


                //받아온 값만큼 반복한다.
                for(int i = 0; i < json_result_set.length(); i++){

                    //drk_name, stock 가 저장되어 있음
                    JSONObject json_object = json_result_set.getJSONObject(i);

                    //현재 모든 제품들의 이름들을 가져온다.
                    String drk_name = json_object.getString("drk_name");

                    //제품들을 배열에 넣는다
                    product_val.add(drk_name);

                    textView = new TextView(context);

                    //속성 지정
                    textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                    //값 넣기
                    textView.setText(drk_name);


                    //TR에 추가
                    tr.addView(textView);

                }

                //테이블에 TR을 적용시킨다.
                order_sheet_layout.addView(tr);

                //제품들 가져오기
                JSONArray  json_result = jsonObject.getJSONArray("result");


                //새로운 자판기의 이름이 나오면 TR을 새롭게 만들게 하기위한 비교용 String
                String before_vending = "";

                //테이블의 TR태그를 초기화 한다
                tr = null;

                //받아온 값만큼 반복한다.
                for(int i = 0; i < json_result.length(); i++){
        /*
        aaa.add("콜");
        aaa.add("사");
        aaa.add("식");
        aaa.add("초");//.indexof(~) -> 인덱스번호 / 실패시 -1
        //Toast.makeText(context, aaa.indexOf("식")+"//"+aaa.indexOf("쮸"), Toast.LENGTH_SHORT).show();
*/

                    //sp_name, vd_name, drink_name, drink_path, sp_val, drink_line, note 가 저장되어 있음
                    JSONObject json_object = json_result.getJSONObject(i);

                    //현재 자판기의 이름을 가져온다.
                    String now_vending = json_object.getString("vd_name");

                    //만약 새로운 자판기 라면 TR태그를 새로만든다.
                    if(!before_vending.equals(now_vending)) {

                        if(tr != null){
                            order_sheet_layout.addView(tr);
                        }

                        //테이블의 TR태그를 만든다
                        tr = new TableRow(context);

                        //이전 자판기 변수를 현재자판기로 바꾼다
                        before_vending = now_vending;

                        textView = new TextView(context);

                        //속성지정
                        textView.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

                        //값 넣기
                        textView.setText(json_object.getString("vd_name"));

                        //TR에 넣기
                        tr.addView(textView);

                    }

                    String now_val = json_object.getString("drink_name");
                    textView = new TextView(context);
                    textView.setLayoutParams(new TableRow.LayoutParams(product_val.indexOf(now_val)+1));
                    //값 넣기
                    textView.setText(json_object.getString("sp_val"));
                    //TR에 넣기
                    tr.addView(textView);


                }

                if(tr != null) {
                    order_sheet_layout.addView(tr);
                }

            }

        }catch (Exception e){
            Log.e("<<<<<<<<<<<",e.toString());
            Toast.makeText(context, "Order_sheet_alert Activty err", Toast.LENGTH_SHORT).show();
        }
    }
}
