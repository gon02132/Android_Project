package com.study.googlemapsandroidapiexample.Login_Page;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;
import com.study.googlemapsandroidapiexample.Main_Page.MainActivity;
import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.DB_conn;

import org.json.JSONArray;
import org.json.JSONObject;

//로그인 MAIN PAGE
public class Login_page_Activity extends AppCompatActivity implements View.OnClickListener{

    private Button           login_bt,  id_serch_bt, pass_serch_bt, create_id_bt; //로그인, 로그인찾기, 비밀번호찾기, 아이디 생성 버튼
    private EditText         id_et,     pass_et;                                  //ID, 비밀번호 입력란
    private DB_conn          test_obj;                                            //db연결 object
    private Share_login_info share_login_info_obj;                                //연결 유지 함수

    private String           user_token;                                          //현재 유저의 토큰 저장소
    private long             fir_time,  sec_time;                                 //뒤로가기 2번누르기를 위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NotificationManager notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);
        //파이어베이스에 토큰을 저장(알림을 위한 기능)
        FirebaseMessaging.getInstance().subscribeToTopic("news");
        NotificationChannel mChannel = new NotificationChannel("my_channel_01",getString(R.string.common_google_play_services_notification_channel_name), NotificationManager.IMPORTANCE_LOW);
        mChannel.setDescription("channel description");
        mChannel.enableLights(true);
        mChannel.setLightColor(Color.RED);
        mChannel.enableVibration(true);
        mChannel.setVibrationPattern(new long[]{100, 200, 300, 400, 500, 400, 300, 200, 400});
        mChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
        notificationManager.createNotificationChannel(mChannel);

        //이전에 로그인 했는지 확인 하기 위한 class 생성
        share_login_info_obj = new Share_login_info(this);

        //이전에 로그인을 했는지 확인한다.(문자열 길이 확인)
        if(share_login_info_obj.get_login_info().length() > 0){

            //로그인 되어있다면 바로 다음페이지로 이동(로그인된 정보와 같이)
            Intent intent = new Intent(Login_page_Activity.this, MainActivity.class);

            //저장되어있는 사용자 정보와함께 다음 페이지로 이동한다.
            intent.putExtra("user_info" , share_login_info_obj.get_login_info());
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
        login_bt        .setOnClickListener(this);
        id_serch_bt     .setOnClickListener(this);
        pass_serch_bt   .setOnClickListener(this);
        create_id_bt    .setOnClickListener(this);


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

                        //성공적으로 값을 받아왔다면 다음 페이지로 넘어간다.
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

                            //로그인후 사용, 토큰 저장때 사용을 하기위해 문자열로 일단 저장 해 둔다
                            String user_login_id = c.getString("login_id");
                            String user_name     = c.getString("name");

                            //로그인 후 사용할 유저 정보를 모아두는 String
                            String print_string = "";

                            print_string += user_login_id              +"/br/";   //로그인 id
                            print_string += user_name                  +"/br/";   //보충기사 이름
                            print_string += c.getString("email") +"/br/";   //이메일
                            print_string += c.getString("imgsrc")+"/br/";   //보충기사 사진

                            //현재 토큰을 검색하여 저장한다
                            user_token = FirebaseInstanceId.getInstance().getToken();

                            //검색된 토큰을 토대로 token_info 테이블을 최신화 하는 과정
                            //시작 시 초기화를 할 경우, 토큰이 미처 생성되지 않으므로 로그인 버튼을 누를시,
                            //현재 정보를 가지고 생성된 토큰 테이블을 다시 초기화한다.
                            //P.S execute는 객체를 항상 새로 생성해야 된다! -> 아니면 에러뜸
                            test_obj    = new DB_conn(Login_page_Activity.this);
                            test_obj.execute("token", user_token, user_login_id, user_name);


                            //얘는 휴대폰을 꺼도 접속유지를위한애
                            share_login_info_obj.set_login_info(print_string);
                            Intent intent_2 = new Intent(this, MainActivity.class);
                            //user_info파일에 보충기사의 정보를 저장한다.
                            intent_2.putExtra("user_info" , print_string);
                            intent_2.putExtra("user_token", user_token);
                            startActivity(intent_2);
                            finish();

                            return;
                    }
                }catch (Exception e){
                    //conn 에러 잡는 부분
                    Toast.makeText(this, "Login_Page_Activity err", Toast.LENGTH_SHORT).show();
                    Log.e("<><>",e.toString());
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

