package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;

public class Serch_result_id_Activity extends AppCompatActivity{
    private Button serch_pw_bt,back_login_bt;
    private TextView result_id_tv;

    private long fir_time, sec_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_result_id);

        result_id_tv = (TextView)findViewById(R.id.result_id_tv);

        //비밀번호 찾기 버튼
        serch_pw_bt = (Button)findViewById(R.id.serch_pw_bt);
        serch_pw_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_result_id_Activity.this, Serch_pass_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //메인페이지로 가는 버튼(로그인 화면으로)
        back_login_bt = (Button)findViewById(R.id.back_login_bt);
        back_login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_result_id_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        Intent data = getIntent();
        result_id_tv.setText(data.getStringExtra("userids"));
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
