package com.study.googlemapsandroidapiexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

public class Login_ok_Activity extends AppCompatActivity{
    private Button logout_bt;
    private TextView username_tv;

    private Share_login_info share_login_info_obj;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_ok);
        username_tv = (TextView)findViewById(R.id.username_tv);

        //이전 activity에서 보낸 값들을 받아온다
        Intent data = getIntent();
        //그중에서 username이름을 가진 문자열 Extra를 가져온다.
        String username = data.getStringExtra("username");
        username_tv.setText(username);

        logout_bt = (Button)findViewById(R.id.logout_bt);
        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 버튼 클릭시,
                //현재 저장된 정보들을 전부 초기화하며 다시 메인페이지로 돌아간다.
                share_login_info_obj = new Share_login_info(Login_ok_Activity.this);
                share_login_info_obj.remove_all();
                Intent intent = new Intent(Login_ok_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
