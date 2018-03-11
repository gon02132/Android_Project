package com.study.googlemapsandroidapiexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Serch_result_pw_Activity extends AppCompatActivity{
    private Button back_login_bt;
    private TextView result_pw_tv;

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
}
