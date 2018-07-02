package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;

//찾고자하는 ID 출력 PAGE
public class Serch_result_id_Activity extends AppCompatActivity{
    private Button      serch_pw_bt,back_login_bt; //비밀번호 찾기, 뒤로가기 버튼
    private TextView    result_id_tv;              //결과값이 출력되는 TextView

    private long        fir_time,   sec_time;      //2번눌러야 뒤로가기위한 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_result_id);

        //결과값이 출력될 공간을 가져온다.
        result_id_tv = (TextView)findViewById(R.id.result_id_tv);

        //비밀번호 찾기 버튼 -> 클릭시 비밀번호 찾는 PAGE로 간다.
        serch_pw_bt = (Button)findViewById(R.id.serch_pw_bt);
        serch_pw_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_result_id_Activity.this, Serch_pass_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //메인페이지로 가는 버튼(로그인 화면으로) -> 로그인 페이지로 이동
        back_login_bt = (Button)findViewById(R.id.back_login_bt);
        back_login_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_result_id_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //이전 activity에서 보낸 결과값을 받아온다
        Intent data = getIntent();

        //받아온 결과값을 TextView에 보여준다.
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
        Toast.makeText(this, getString(R.string.back_double), Toast.LENGTH_SHORT).show();
        fir_time = System.currentTimeMillis();
    }
}
