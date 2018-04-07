package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.db_conn;

import org.json.JSONArray;
import org.json.JSONObject;

//ID 찾는 PAGE
public class Serch_id_Activity extends AppCompatActivity{
    private Button      back_bt, serch_bt;  //뒤로가기, ID찾기 버튼
    private db_conn     conn;               //db 연결 변수
    private EditText    serch_name_et;      //찾고자 하는 이름 입력란

    private long fir_time, sec_time;        //뒤로가기 2번누르기를 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_id);

        //입력한 이름을 가져온다
        serch_name_et = (EditText)findViewById(R.id.serch_name_et);

        //db 연결
        conn = new db_conn(this);

        //뒤로가기 버튼 클릭 시 ->Main Page로 이동한다.
        back_bt = (Button)findViewById(R.id.back_bt);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_id_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //검색버튼 클릭 시 -> db에서 값을 가져온다.
        serch_bt = (Button)findViewById(R.id.serch_bt);
        serch_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //찾기 버튼 클릭시, doin함수에서 반환값을 받아온다
                try {
                    String serch_name_str = serch_name_et.getText().toString();
                    //빈칸이 아닐경우
                    if(!serch_name_str.equals("")) {
                        //doin함수 호출(구분자 serch_id)
                        //doin함수의 리턴값을 받아온다.
                        String result_String = conn.execute("serch_id", serch_name_str).get();

                        //받은 값이 없다면(입력한 값이랑 일치하는 name이 없다면)
                        if(result_String.equals("no_exist")){
                            //결과Activity로 이동(존재하지 않는문자열을 넘겨줌)
                            Intent intent = new Intent(Serch_id_Activity.this, Serch_result_id_Activity.class);
                            intent.putExtra("userids", "존재하지 않습니다.");
                            startActivity(intent);
                            finish();
                        }

                        //받은 값이 있다면(입력한 값이랑 일치하는 값이 있다면)
                        else{
                            //json 객체로 변환하여 json배열에 저장
                            JSONObject jsonObject = new JSONObject(result_String);
                            //계산 구분자
                            String json_select = jsonObject.getString("select");
                            JSONArray json_result = jsonObject.getJSONArray("result");

                            //출력할 문자열 생성
                            String print_string ="";
                            //검색된 모든 결과값을 문자열에 저장
                            for(int i=0; i<json_result.length(); i++){
                                print_string += (i+1)+":"+json_result.getString(i)+"\n";
                            }

                            //결과String값을 가지고 결과 Activity로 이동
                            Intent intent = new Intent(Serch_id_Activity.this, Serch_result_id_Activity.class);
                            intent.putExtra("userids", print_string);
                            startActivity(intent);
                            finish();
                        }

                    }

                }catch (Exception e){
                    Toast.makeText(Serch_id_Activity.this, "Serch_id Exception!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    //뒤로가기 두번 클릭시 나가지는 이벤트
    @Override
    public void onBackPressed() {
        sec_time = System.currentTimeMillis();
        if(sec_time - fir_time < 2000){
            super.onBackPressed();
            finishAffinity();
        }
        Toast.makeText(this, "한번더 뒤로가기 클릭 시 종료", Toast.LENGTH_SHORT).show();
        fir_time = System.currentTimeMillis();
    }

}
