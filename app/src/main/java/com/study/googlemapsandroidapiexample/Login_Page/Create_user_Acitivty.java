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
import com.study.googlemapsandroidapiexample.DB_conn;

//유저 생성 PAGE
public class Create_user_Acitivty extends AppCompatActivity{

    private Button      exist_id_check_bt, create_user_bt, create_cancel_bt;
    private EditText    id_input_et, pass_fir_et, pass_sec_et, name_et, email_et,phone_et, address_et;
    private TextView    serch_result, two_pass_check;
    private DB_conn     conn;
    private Integer     count = 0;
    private long        fir_time, sec_time;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_user);

        id_input_et = (EditText)findViewById(R.id.id_input_tv); //ID 입력 란
        name_et     = (EditText)findViewById(R.id.name_et);     //name 입력 란
        email_et    = (EditText)findViewById(R.id.emil_et);     //email 입력 란
        phone_et    = (EditText)findViewById(R.id.phone_et);    //전화번호 입력 란
        address_et  = (EditText)findViewById(R.id.address_et);  //주소 입력 란

        two_pass_check  = (TextView)findViewById(R.id.two_pass_check);      //두개의 비밀번호가 일치하는지 보여주는 란
        serch_result    = (TextView)findViewById(R.id.serch_result);        //ID 중복 검색 결과 보여주는 란

        exist_id_check_bt = (Button)findViewById(R.id.exist_id_check_bt);   //ID 중복 검사 버튼
        exist_id_check_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    conn = new DB_conn();
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

                    //예외 발생 시, Toast 발생
                    else{
                        Toast.makeText(Create_user_Acitivty.this, "Err 다시 실행 해 주세요", Toast.LENGTH_SHORT).show();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        //ID입력란이 바뀌었을 경우(입력, 삭제) 이벤트 발생
        id_input_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            //실제로 바뀌고 난뒤의 함수만 필요하기 때문에 이것만 씀
            @Override
            public void afterTextChanged(Editable s) {
                //ID중복 확인을 다시 하기위해 결과 란을 초기화 한다.
                serch_result.setText("");
                serch_result.setTextColor(Color.parseColor("#FF0000"));
            }
        });

        //뒤로가기 버튼 클릭 시,
        create_cancel_bt = (Button)findViewById(R.id.create_cancel_bt);
        create_cancel_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //메인 화면으로 돌아가며 현재 화면을 끈다.
                Intent intent = new Intent(Create_user_Acitivty.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

//-------------------------------사용자가 올바르게 적었는지 확인--------------------------------------

        //첫번째 비밀번호와 두번째 비밀번호를 비교한다 -> 입력 값이 변할때 마다 매번 호출이 된다
        pass_fir_et = (EditText)findViewById(R.id.pass_fir_et);
        pass_fir_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            //바뀐 뒤의 결과값만 필요
            @Override
            public void afterTextChanged(Editable s) {

                //비밀번호 2개의 문자열이 일치 하는가?
                if(s.toString().equals(pass_sec_et.getText().toString())){

                    //빈칸일경우 예외처리 -> 가입 못하게 조건을 준다.
                    if(s.toString().equals("")){
                        two_pass_check.setText("");
                        two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                    }

                    //빈칸이아니고 같을경우 -> 가입을 할 수 있는 조건 중 하나를 만족 시켜 준다.
                    else {
                        two_pass_check.setText("두개의 비밀번호가 일치합니다.");
                        two_pass_check.setTextColor(Color.parseColor("#0000FF"));
                    }

                }

                //두개의 비밀번호가 일치하지 않을 시, 가입을 못하게 조건을 준다.
                else{
                    two_pass_check.setText("두개의 비밀번호가 일치하지 않습니다.");
                    two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        });

        //두번째 비밀번호와 첫번째 비밀번호를 비교한다 -> 입력 값이 변할때 마다 매번 호출이 된다
        //앞의 비밀번호와 두번째 비밀번호를 서로 비교하며 예외상황을 없앤다.
        pass_sec_et = (EditText)findViewById(R.id.pass_sec_et);
        pass_sec_et.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {

                //비밀번호 2개의 문자열이 일치 하는가?
                if(s.toString().equals(pass_fir_et.getText().toString())){

                    //빈칸일경우 예외처리 -> 가입 못하게 조건을 준다.
                    if(s.toString().equals("")){
                        two_pass_check.setText("");
                        two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                    }

                    //빈칸이아니고 같을경우 -> 가입을 할 수 있는 조건 중 하나를 만족 시켜 준다.
                    else {
                        two_pass_check.setText("두개의 비밀번호가 일치합니다.");
                        two_pass_check.setTextColor(Color.parseColor("#0000FF"));
                    }
                }

                //두개의 비밀번호가 일치하지 않을 시, 가입을 못하게 조건을 준다.
                else{
                    two_pass_check.setText("두개의 비밀번호가 일치하지 않습니다.");
                    two_pass_check.setTextColor(Color.parseColor("#FF0000"));
                }

            }
        });
//------------------------------------------------------------------------------------------------
        //가입할수 있는 버튼을 가져온다.
        create_user_bt = (Button)findViewById(R.id.create_user_bt);
        create_user_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //가입하기 버튼 클릭 시 가입할 수 있는 조건을 검색한다.
                //textView의 색을 가져오며 정수형색상을 문자로 바꾼다
                //substring으로 필요없는 앞 2글자는 잘라준다 ->또한 영문일경우 소문자로 표현된다
                String hexColor1 = "#"+Integer.toHexString(two_pass_check.getCurrentTextColor()).substring(2);
                String hexColor2 = "#"+Integer.toHexString(serch_result.getCurrentTextColor()).substring(2);

                //사용가능한 상태(가입 조건 만족 상태)라면 가입하기!
                //인자만큼 db에 저장!! -> 인자:id,pass
                if(hexColor1.equals("#0000ff")
                        && hexColor2.equals("#0000ff")
                        && name_et.getText().toString().length()>0
                        && email_et.getText().toString().length()>0){

                    //db접속에서는 예외가 발생할 수 있으므로 try/catch문을 사용한다
                    try {
                        conn = new DB_conn();
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

                        //서버나 DB쪽에 문제가 있을 경우 에러창을 띄워준다.
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

    //뒤로가기 두번 클릭시 나가지는 이벤트
    @Override
    public void onBackPressed() {
        //뒤로가기 버튼을 2초안에 2번눌렀을 경우
        sec_time = System.currentTimeMillis();
        if(sec_time - fir_time < 2000){
            super.onBackPressed();
            finishAffinity();
        }
        Toast.makeText(this, "한번더 뒤로가기 클릭 시 종료", Toast.LENGTH_SHORT).show();
        fir_time = System.currentTimeMillis();
    }

}
