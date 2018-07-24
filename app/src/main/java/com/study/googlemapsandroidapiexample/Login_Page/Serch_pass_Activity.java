package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.DB_conn;

import org.json.JSONArray;
import org.json.JSONObject;

//PASSWORD 찾는 PAGE
public class Serch_pass_Activity extends AppCompatActivity{
    private Button      back_bt,     serch_bt;          //뒤로가기, 찾기 버튼
    private EditText    serch_id_et, serch_name_et;     //ID, name 입력 란
    private DB_conn     conn;                           //DB연결 변수

    private long        fir_time,    sec_time;         //2번눌러야 뒤로가기위한 변수
    private String      url;                           //서버 주소

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_pass);

        //intent의 값을 가져온다.
        Intent data = getIntent();

        //정상적으로 값을 가져왔다면
        if (data.getStringExtra("url") != null) {
            //유저 정보를 가져온다
            String str = data.getStringExtra("url");
            url = str;
        }

        serch_id_et     = (EditText)findViewById(R.id.serch_id_et);     //ID 입력란 가져오기
        serch_name_et   = (EditText)findViewById(R.id.serch_name_et);   //PASSWORD 입력란 가져오기

        //db 연결
        conn = new DB_conn(this, url);

        //뒤로가기 버튼 클릭시 -> 로그인 메인페이지로 이동
        back_bt = (Button) findViewById(R.id.back_bt);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_pass_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //찾기 버튼 클릭시 -> 결과 |페이지로 이동
        //db 접속하여 값을 가져온다
        serch_bt = (Button) findViewById(R.id.serch_bt);
        serch_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {

                    //입력된 ID와 NAME을 가져온다
                    String serch_id_str   = serch_id_et  .getText().toString();
                    String serch_name_str = serch_name_et.getText().toString();

                    //빈칸이 아닐경우
                    if((!serch_id_str.equals("")) && (!serch_name_str.equals("")) ) {

                        //doin함수 호출(구분자 serch_pass)
                        String result_String = conn.execute("serch_pass", serch_id_str, serch_name_str).get();

                        //받은 값이 없다면(입력한 값이랑 일치하는 id와 name이 없다면) -> 결과 페이지로 이동
                        if(result_String.equals("no_exist")){

                            //db에서 받아온 값을 결과 페이지에 넘겨준다.
                            Intent intent = new Intent(Serch_pass_Activity.this, Serch_result_pw_Activity.class);
                            intent.putExtra("user_info", getString(R.string.not_exist));
                            startActivity(intent);
                            finish();
                        }

                        //받은 값이 있다면(입력한 값이랑 일치하는 값이 있다면)
                        else{

                            //받아온 문자열을 json 객체로 변환한다.
                            JSONObject jsonObject = new JSONObject(result_String);

                            //변환된 JSONOBJ중 배열로된 객체를 꺼내온다.
                            JSONArray json_result   = jsonObject.getJSONArray("result");

                            //반복문을 돌며 id와 oassword, name을 추출해낸다.
                            String print_string ="";
                            for(int i=0; i<json_result.length(); i++){
                                JSONObject json_obj =  json_result.getJSONObject(i);
                                print_string += "id:"       +json_obj.getString("id")+"\n";
                                print_string += "password:" +json_obj.getString("password")+"\n";
                                print_string += "name:"     +json_obj.getString("name")+"\n";
                            }

                            //결과 화면으로 간다 -> 결과값을 넘겨주며
                            Intent intent = new Intent(Serch_pass_Activity.this, Serch_result_pw_Activity.class);
                            intent.putExtra("user_info", print_string);
                            startActivity(intent);
                            finish();
                        }

                    }else{
                        Toast.makeText(Serch_pass_Activity.this, getString(R.string.exist_blank), Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Toast.makeText(Serch_pass_Activity.this, "Serch_pass_Activity err", Toast.LENGTH_SHORT).show();
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
        Toast.makeText(this, getString(R.string.back_double), Toast.LENGTH_SHORT).show();
        fir_time = System.currentTimeMillis();
    }

}
