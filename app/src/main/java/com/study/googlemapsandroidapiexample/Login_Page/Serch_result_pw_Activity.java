package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;

//찾고자하는 비밀번호 출력 page
public class Serch_result_pw_Activity extends AppCompatActivity{
    private Button      back_login_bt;      //뒤로가기 버튼(메인페이지로 감)
    private TextView    result_pw_tv;       //비밀번호가 출력될 공간

    private long        fir_time, sec_time; //2번눌러야 뒤로가기위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_result_pw);

        //이전 페이지에서 보낸 값을 받아온다.
        Intent data = getIntent();

        //결과 출력 Textview에 이전페이지에서 보낸 값을 넣는다.
        result_pw_tv = (TextView)findViewById(R.id.result_pw_tv);
        result_pw_tv.setText(data.getStringExtra("user_info"));

        //뒤로가기 버튼 클릭시 로그인(메인 페이지)로 간다.
        back_login_bt = (Button)findViewById(R.id.back_login_bt);
        back_login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_result_pw_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
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
