package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.Main_Page.MainActivity;
import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.DB_conn;

import org.json.JSONArray;
import org.json.JSONObject;

//로그인 MAIN PAGE
public class Login_page_Activity extends AppCompatActivity implements View.OnClickListener{

    Button           login_bt,  id_serch_bt, pass_serch_bt, create_id_bt; //로그인, 로그인찾기, 비밀번호찾기, 아이디 생성 버튼
    EditText         id_et,     pass_et;                                  //ID, 비밀번호 입력란
    DB_conn          test_obj;                                            //db연결 object
    Share_login_info share_login_info_obj;                                //연결 유지 함수

    private long     fir_time,  sec_time;                                 //뒤로가기 2번누르기를 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //이전에 로그인 했는지 확인 하기 위한 class 생성
        share_login_info_obj = new Share_login_info(this);

        //이전에 로그인을 했는지 확인한다.(문자열 길이 확인)
        if(share_login_info_obj.get_login_info().length() > 0){

            //로그인 되어있다면 바로 다음페이지로 이동(로그인된 정보와 같이)
            Intent intent = new Intent(Login_page_Activity.this, MainActivity.class);

            //저장되어있는 사용자 정보와함께 다음 페이지로 이동한다.
            intent.putExtra("user_info", share_login_info_obj.get_login_info());
            startActivity(intent);
            finish();
        }
        //이전에 로그인 기록이 없다면(로그아웃 혹은 새로 킬경우)
        else{
            //textView.setText(share_login_info_obj.get_login_info());
        }

        login_bt        = (Button)findViewById(R.id.login_bt);          //로그인 버튼
        id_serch_bt     = (Button)findViewById(R.id.id_serch_bt);       //ID찾기 버튼
        pass_serch_bt   = (Button)findViewById(R.id.pass_serch_bt);     //비밀번호 찾기 버튼
        create_id_bt    = (Button)findViewById(R.id.create_id_bt);      //ID생성 버튼

        //리스너 등록
        login_bt.setOnClickListener(this);
        id_serch_bt.setOnClickListener(this);
        pass_serch_bt.setOnClickListener(this);
        create_id_bt.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        //다른 페이지로 가기위한 intent
        Intent intent;

        switch (v.getId()){

            //아이디 찾기 버튼
            case R.id.id_serch_bt:
                intent = new Intent(Login_page_Activity.this, Serch_id_Activity.class);
                startActivity(intent);
                finish();
                break;

            //비밀번호 찾기 버튼
            case R.id.pass_serch_bt:
                intent = new Intent(Login_page_Activity.this, Serch_pass_Activity.class);
                startActivity(intent);
                finish();
                break;

            //회원가입 버튼
            case R.id.create_id_bt:
                intent = new Intent(Login_page_Activity.this, Create_user_Acitivty.class);
                startActivity(intent);
                finish();
                break;

            //로그인 버튼
            case R.id.login_bt:
                //ID와 PASSWORD를 서버에 날려 결과값을 받아온다.
                id_et       = (EditText)findViewById(R.id.id_et);
                pass_et     = (EditText)findViewById(R.id.pass_et);
                test_obj    = new DB_conn(Login_page_Activity.this);
                //doInBackground 실행(인자를 2개로 넘겨준다 // ID,비밀번호)
                try {
                    //doin함수에서 반환되는 값을 가져와서 에러가 있을 경우 처리를 한다.
                    String result_String = test_obj.execute("login", id_et.getText().toString(), pass_et.getText().toString()).get();
                    switch (result_String){
                        //id랑 password 중 하나라도 미입력 시
                        case "no_full":
                            Toast.makeText(this, "빈 칸이 있습니다", Toast.LENGTH_SHORT).show();
                            return;

                        //id가 없을 시,
                        case "no_id":
                            Toast.makeText(this, "찾는 id가 없습니다", Toast.LENGTH_SHORT).show();
                            return;

                        //비밀번호가 틀릴 시,
                        case "no_pass":
                            Toast.makeText(this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                            return;

                        //이외(mysql_err ....)
                        case "mysql_err":
                            Toast.makeText(this, "mysql에러! 구문을 재확인 하십쇼", Toast.LENGTH_SHORT).show();
                            return;

                        default:
                            //json 객체로 변환하여 json배열에 저장
                            JSONObject jsonObject = new JSONObject(result_String);

                            //json 배열로 받아온다.
                            JSONArray json_result = jsonObject.getJSONArray("result");

                            //받아온 결과값이 없거나, 올바르지 않은 값이 들어가 있을 시,
                            if(json_result == null || json_result.length() == 0){
                                Toast.makeText(this, "데이터베이스 입력값 오류", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //모든 예외처리 통과시 이 구문이 실행됨
                            int i = 0;
                            //id,name,email.imgsrc
                            JSONObject c = json_result.getJSONObject(i);
                            String print_string = "";
                            print_string += c.getString("login_id")+"/br/"; //로그인 id
                            print_string += c.getString("name")+"/br/";     //보충기사 이름
                            print_string += c.getString("email")+"/br/";    //이메일
                            print_string += c.getString("imgsrc")+"/br/";   //보충기사 사진

                            //얘는 휴대폰을 꺼도 접속유지를위한애
                            share_login_info_obj.set_login_info(print_string);
                            Intent intent_2 = new Intent(this, MainActivity.class);
                            //user_info파일에 보충기사의 정보를 저장한다.
                            intent_2.putExtra("user_info", print_string);
                            startActivity(intent_2);
                            finish();

                            return;
                    }
                }catch (Exception e){
                    //conn 에러 잡는 부분
                    Toast.makeText(this, "Login_Page_Activity err", Toast.LENGTH_SHORT).show();
                }
                break;
        }


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

