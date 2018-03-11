package com.study.googlemapsandroidapiexample;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;


public class Serch_pass_Activity extends AppCompatActivity{
    private Button back_bt, serch_bt;
    private EditText serch_id_et, serch_name_et;
    private db_conn conn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.serch_pass);

        serch_id_et = (EditText)findViewById(R.id.serch_id_et);
        serch_name_et = (EditText)findViewById(R.id.serch_name_et);

        conn = new db_conn(this);

        back_bt = (Button) findViewById(R.id.back_bt);
        back_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Serch_pass_Activity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        serch_bt = (Button) findViewById(R.id.serch_bt);
        serch_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String serch_id_str = serch_id_et.getText().toString();
                    String serch_name_str = serch_name_et.getText().toString();

                    //빈칸이 아닐경우
                    if((!serch_id_str.equals("")) && (!serch_name_str.equals("")) ) {
                        //doin함수 호출(구분자 serch_pass)
                        String result_String = conn.execute("serch_pass", serch_id_str, serch_name_str).get();
                        //Toast.makeText(Serch_pass_Activity.this, result_String, Toast.LENGTH_SHORT).show();
                        //받은 값이 없다면(입력한 값이랑 일치하는 id와 name이 없다면)
                        if(result_String.equals("no_exist")){
                            //결과Activity로 이동(결과값 넘겨줌)
                            Intent intent = new Intent(Serch_pass_Activity.this, Serch_result_pw_Activity.class);
                            intent.putExtra("user_info", "존재하지 않습니다.");
                            startActivity(intent);
                            finish();
                        }

                        //받은 값이 있다면(입력한 값이랑 일치하는 값이 있다면)
                        else{
                            String print_string ="";
                            //json 객체로 변환하여 json배열에 저장
                            JSONObject jsonObject = new JSONObject(result_String);
                            //계산 구분자
                            String json_select = jsonObject.getString("select");
                            JSONArray json_result = jsonObject.getJSONArray("result");
                            for(int i=0; i<json_result.length(); i++){
                                JSONObject json_obj =  json_result.getJSONObject(i);
                                print_string += "id:"       +json_obj.getString("id")+"\n";
                                print_string += "password:" +json_obj.getString("password")+"\n";
                                print_string += "name:"     +json_obj.getString("name")+"\n";
                            }
                            Toast.makeText(Serch_pass_Activity.this, result_String, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(Serch_pass_Activity.this, Serch_result_pw_Activity.class);
                            intent.putExtra("user_info", print_string);
                            startActivity(intent);
                            finish();
                        }

                    }else{
                        Toast.makeText(Serch_pass_Activity.this, "빈칸이 있습니다", Toast.LENGTH_SHORT).show();
                    }

                }catch (Exception e){
                    Log.e(">>>>>>>>>>>>>>>>",e.toString());
                }
            }
        });
    }
}
