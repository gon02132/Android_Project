package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;

public class Serch_result_pw_Activity extends AppCompatActivity{
    private Button back_login_bt;
    private TextView result_pw_tv;

    private long fir_time, sec_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_result_pw);

        Intent data = getIntent();

        result_pw_tv = (TextView)findViewById(R.id.result_pw_tv);
        result_pw_tv.setText(data.getStringExtra("user_info"));

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
