package com.study.googlemapsandroidapiexample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.study.googlemapsandroidapiexample.Login_Page.Login_page_Activity;

//메인페이지 로딩시, 보여지는 화면
public class splash_activity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        setTheme(R.style.SplashTheme);

        super.onCreate(savedInstanceState);

        //보내는 Intent와 값을 받아오는 Intent로 나눠서 관리한다
        Intent send_intent = new Intent(getApplicationContext(), Login_page_Activity.class);
        Intent get_intent  = getIntent();

        //푸쉬메세지로 접근시 값을 다음 엑티비티에 넘겨주기위해 저장해 둔다
        String check       = get_intent.getStringExtra("go_order_sheet");

        //푸쉬메세지로 접근이 아닐경우에는 값을 넘겨주지 않고
        //푸쉬메세지로 접근시(값이 있으므로) 그 값을 다음 액티비티에 넘겨준다
        if(check != null){
            send_intent.putExtra("go_order_sheet", "go");
        }

        //다음 엑티비티를 활성화한다
        startActivity(send_intent);

        //로고의 생명주기를 정한다
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //10초후 현재 페이지는 닫는다
                finish();
            }
        },10000);
    }
}
