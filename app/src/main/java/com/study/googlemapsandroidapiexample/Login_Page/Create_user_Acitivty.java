package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.db_conn;


public class Create_user_Acitivty extends AppCompatActivity{
    private Button exist_id_check_bt, create_user_bt, create_cancel_bt;
    private EditText id_input_et, pass_fir_et, pass_sec_et, name_et, email_et,phone_et, address_et;
    private TextView serch_result, two_pass_check;
    private db_conn conn;
    private Integer count = 0;
    private long fir_time, sec_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        id_input_et = (EditText)findViewById(R.id.id_input_tv);
        name_et = (EditText)findViewById(R.id.name_et);
        email_et = (EditText)findViewById(R.id.emil_et);
        phone_et = (EditText)findViewById(R.id.phone_et);
        address_et = (EditText)findViewById(R.id.address_et);

        two_pass_check = (TextView)findViewById(R.id.two_pass_check);
        serch_result = (TextView)findViewById(R.id.serch_result);



        exist_id_check_bt = (Button)findViewById(R.id.exist_id_check_bt);
        exist_id_check_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conn = new db_conn();
                try {
                    //중복확인 버튼 클릭시, doin함수에서 반환값을 받아온다
                    String s = conn.execute("exist_id_check",id_input_et.getText().toString()).get();
                    if(s.equals("exist")){

                    //존재할 경우
                        serch_result.setText("존재하는 id입니다!");
                        serch_result.setTextColor(Color.parseColor("#FF0000"));
                    }

                    //존재하지 않을경우
                    else if(s.equals("no_exist")){
                        serch_result.setText("사용가능한 id입니다!");
                        serch_result.setTextColor(Color.parseColor("#0000FF"));
                    }

                    //예외 발생 시
                    else{
                        Toast.makeText(Create_user_Acitivty.this, "nononononono", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        id_input_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                serch_result.setText("");
                serch_result.setTextColor(Color.parseColor("#FF0000"));
            }
        });

        create_cancel_bt = (Button)findViewById(R.id.create_cancel_bt);
        create_cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //뒤로가기 버튼을 눌렀을 시,
                Intent intent = new Intent(Create_user_Acitivty.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

//-------------------------------사용자가 올바르게 적었는지 확인--------------------------------------
        pass_fir_et = (EditText)findViewById(R.id.pass_fir_et);
        pass_fir_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //비밀번호칸이 2개맞는지 확인
                if(s.toString().equals(pass_sec_et.getText().toString())){
                    //빈칸일경우 예외처리
                    if(s.toString().equals("")){
                        two_pass_check.setText("");
                        two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                    }
                    //빈칸이아니고 같을경우
                    else {
                        two_pass_check.setText("두개의 비밀번호가 일치합니다.");
                        two_pass_check.setTextColor(Color.parseColor("#0000FF"));
                    }
                }else{
                    two_pass_check.setText("두개의 비밀번호가 일치하지 않습니다.");
                    two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        });
        pass_sec_et = (EditText)findViewById(R.id.pass_sec_et);
        pass_sec_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                //비밀번호칸이 2개맞는지 확인
                if(s.toString().equals(pass_fir_et.getText().toString())){
                    //빈칸일경우 예외처리
                    if(s.toString().equals("")){
                        two_pass_check.setText("");
                        two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                    }
                    //빈칸이아니고 같을경우
                    else {
                        two_pass_check.setText("두개의 비밀번호가 일치합니다.");
                        two_pass_check.setTextColor(Color.parseColor("#0000FF"));
                    }
                }else{
                    two_pass_check.setText("두개의 비밀번호가 일치하지 않습니다.");
                    two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        });
//------------------------------------------------------------------------------------------------
        create_user_bt = (Button)findViewById(R.id.create_user_bt);
        create_user_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //가입하기 버튼 클릭 시
                //textView의 색을 가져오며 정수형색상을 문자로 바꾼다
                //substring으로 필요없는 앞 2글자는 잘라준다 ->또한 영문일경우 소문자로 표현된다
                String hexColor1 = "#"+Integer.toHexString(two_pass_check.getCurrentTextColor()).substring(2);
                String hexColor2 = "#"+Integer.toHexString(serch_result.getCurrentTextColor()).substring(2);

                //사용가능한 상태라면 가입하기!
                //인자만큼 db에 저장되도록!!
                if(hexColor1.equals("#0000ff")
                        && hexColor2.equals("#0000ff")
                        && name_et.getText().toString().length()>0
                        && email_et.getText().toString().length()>0){
                    conn = new db_conn();
                    //인자:id,pass
                    try {
                        //.get()을 할경우 doIn..함수에서 반환값이 돌아온다(하지만 처리량이 많을경우) 리턴값이 늦게받아질수도 있다
                        String s = conn.execute("create_user_ok",
                                id_input_et.getText().toString(),
                                pass_sec_et.getText().toString(),
                                name_et.getText().toString(),
                                email_et.getText().toString(),
                                phone_et.getText().toString(),
                                address_et.getText().toString()).get();

                        //DB에 insert되었다면 생성완료 알림 후 Mainpage로 이동
                        if(s.equals("insert_OK")){
                            Toast.makeText(Create_user_Acitivty.this, "생성완료!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Create_user_Acitivty.this, Login_page_Activity.class);
                            startActivity(intent);
                            finish();
                        }
                        else{
                            Toast.makeText(Create_user_Acitivty.this, "mysql err", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                }else{
                    Toast.makeText(Create_user_Acitivty.this, "누락된부분 올바르게 적으세요", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    public int get_count(){
        return count;
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
