package com.study.googlemapsandroidapiexample;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Login_page_Activity extends AppCompatActivity implements View.OnClickListener{
    Button login_bt, id_serch_bt, pass_serch_bt, create_id_bt;
    EditText id_et, pass_et;
    TextView textView;

    db_conn test_obj;
    Share_login_info share_login_info_obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        textView = (TextView)findViewById(R.id.textView);

        share_login_info_obj = new Share_login_info(this);
        //이전에 로그인을 했다면
        if(share_login_info_obj.get_login_info().length() > 0){
            //textView.setText(share_login_info_obj.get_login_info());

            //로그인 되어있다면 바로 다음페이지로 이동(로그인된 정보와 같이)
            Intent intent = new Intent(Login_page_Activity.this, MainActivity.class);
            intent.putExtra("username", share_login_info_obj.get_login_info());
            startActivity(intent);
            finish();
        }
        //이전에 로그인 기록이 없다면(로그아웃 혹은 새로 킬경우)
        else{
            //textView.setText(share_login_info_obj.get_login_info());
        }

        //로그인 버튼 클릭시
        login_bt = (Button)findViewById(R.id.login_bt);
        id_serch_bt = (Button)findViewById(R.id.id_serch_bt);
        pass_serch_bt = (Button)findViewById(R.id.pass_serch_bt);
        create_id_bt = (Button)findViewById(R.id.create_id_bt);

        login_bt.setOnClickListener(this);
        id_serch_bt.setOnClickListener(this);
        pass_serch_bt.setOnClickListener(this);
        create_id_bt.setOnClickListener(this);


    }

    @Override
    public void onClick(View v) {
        Intent intent;

        switch (v.getId()){

            //아이디 찾기 버튼
            case R.id.id_serch_bt:
                intent = new Intent(Login_page_Activity.this, Serch_id_Activity.class);
                startActivity(intent);
                finish();
                break;

            //비밀번호 찾기 버튼
            case R.id.pass_serch_bt:
                intent = new Intent(Login_page_Activity.this, Serch_pass_Activity.class);
                startActivity(intent);
                finish();
                break;

            //회원가입 버튼
            case R.id.create_id_bt:
                intent = new Intent(Login_page_Activity.this, Create_user_Acitivty.class);
                startActivity(intent);
                finish();
                break;

            //로그인 버튼
            case R.id.login_bt:
                id_et = (EditText)findViewById(R.id.id_et);
                pass_et = (EditText)findViewById(R.id.pass_et);
                test_obj = new db_conn(Login_page_Activity.this, share_login_info_obj);
                //doInBackground 실행(인자를 2개로 넘겨준다 // ID,비밀번호)
                try {
                    //doin함수에서 반환되는 값을 가져와서 에러가 있을 경우 처리를 한다.
                    String result_String = test_obj.execute("login", id_et.getText().toString(), pass_et.getText().toString()).get();
                    switch (result_String){
                        //id랑 password 중 하나라도 미입력 시
                        case "no_full":
                            Toast.makeText(this, "빈 칸이 있습니다", Toast.LENGTH_SHORT).show();
                            return;

                        //id가 없을 시,
                        case "no_id":
                            Toast.makeText(this, "찾는 id가 없습니다", Toast.LENGTH_SHORT).show();
                            return;

                        //비밀번호가 틀릴 시,
                        case "no_pass":
                            Toast.makeText(this, "비밀번호가 틀립니다.", Toast.LENGTH_SHORT).show();
                            return;

                        //이외(mysql_err ....)
                        case "mysql_err":
                            Toast.makeText(this, "mysql에러! 구문을 재확인 하십쇼", Toast.LENGTH_SHORT).show();
                            return;

                        default:
                            //json 객체로 변환하여 json배열에 저장
                            JSONObject jsonObject = new JSONObject(result_String);

                            String print_string = "";
                            JSONArray json_result = jsonObject.getJSONArray("result");
                            //계산 결과
                            //db에 올바르지 않은 값이 들어가 있을 시,
                            if(json_result == null || json_result.length() == 0){
                                Toast.makeText(this, "데이터베이스 입력값 오류", Toast.LENGTH_SHORT).show();
                                return;
                            }
                            //모든 예외처리 통과시 이 구문이 실행됨
                            //for (int i = 0; i < json_result.length(); i++) {
                            int i = 0;
                            JSONObject c = json_result.getJSONObject(i);
                            String a1 = "id:"+c.getString("id")+"\n";
                            String b1 = "name:"+c.getString("name");
                            print_string += a1 + b1;
                            //}

                            //강제로 tv에 쑤셔넣는 작업
                            //TextView tv = (((Activity) context).findViewById(R.id.textView));
                            //tv.setText(print_string);

                            //얘는 휴대폰을 꺼도 접속유지를위한애
                            share_login_info_obj.set_login_info(print_string);
                            Intent intent_2 = new Intent(this, MainActivity.class);
                            intent_2.putExtra("username", print_string);
                            startActivity(intent_2);
                            finish();

                            return;
                    }
                }catch (Exception e){

                }
                break;
        }


    }
}

//인자1:doInBackground 2:onProgressUpdate 3:onPostExecute  들의 매개변수 타입결정
//비동기적 쓰레드, 백그라운드 쓰레드와 UI쓰레드(메인 쓰레드)와 같이 쓰기위해 쓰임
class db_conn extends AsyncTask<String, Void, String> {
    private Context context;
    private BufferedReader bufferedReader = null;
    private Share_login_info share_login_info_obj;
    HttpURLConnection con;

    //---------------------------생성자----------------------------------------------------------
    public db_conn(Context context, Share_login_info share_login_info_obj){
        this.context = context;
        this.share_login_info_obj = share_login_info_obj;
    }

    public db_conn(Context context){
        this.context = context;
    }

    public db_conn(){}
    //-------------------------------------------------------------------------------------------

    //excute시, 실행되는 콜백함수 //이전에 받은 인자들을 설정된 자료형 배열로 받아온다
    @Override
    protected String doInBackground(String... strings) {
        try {

            //받아올 php 경로 선택 1:aws 2:autoset
            //String link = "http://ec2-13-125-220-71.ap-northeast-2.compute.amazonaws.com/test/conn.php";
            String link  = "http://172.25.1.89/test/conn.php";

            //구별 인자값의 널값 확인 - 예외처리
            if(strings[0] != null) {

                //로그인 버튼 클릭시 넘어가는 인자값들
                if (strings[0].equals("login")) {
                    if(strings[1] != null && strings[2] != null) {
                        //인자: 컨트롤러,이름,비밀번호 php를 통하여 쿼리 조회
                        link += "?con=select_all";
                        link += "&id=" + strings[1];
                        link += "&password=" + strings[2];
                    }
                }

                //id중복확인 버튼 클릭시 넘어가는 인자값들
                else if(strings[0].equals("exist_id_check")) {
                    if(strings[1] != null) {
                        //인자: 컨트롤러,이름,비밀번호 php를 통하여 쿼리 조회
                        link += "?con=exist_id_check";
                        link += "&id=" + strings[1];
                    }
                }

                //회원가입 버튼 클릭시(모든 조건이 올바르게 들어가있는 상태) - 예외처리 필요없음
                else if(strings[0].equals("create_user_ok")){
                    link += "?con=create_user_ok";
                    link += "&id=" + strings[1];
                    link += "&password=" + strings[2];
                    link += "&name=" + strings[3];
                }

                //id찾기 버튼 클릭시
                else if(strings[0].equals("serch_id")){
                    if(strings[1] != null) {
                        link += "?con=serch_id";
                        link += "&serch_name=" + strings[1];
                    }
                }

                //pw찾기 버튼 클릭시
                else if(strings[0].equals("serch_pass")){
                    if(strings[1] != null && strings[2] != null) {
                        link += "?con=serch_pass";
                        link += "&serch_id=" + strings[1];
                        link += "&serch_name=" + strings[2];
                    }

                }

            }

            URL url = new URL(link);
            con = (HttpURLConnection)url.openConnection();
            //연결 성공시
            if(con != null) {

                //---------------연결설정-------------------------------------------
                con.setRequestMethod("GET");    //get방식 통신
                con.setConnectTimeout(5000);    //지연됬을경우 기다려주는시간 5초
                con.setUseCaches(false);        //캐싱데이터를 안받음
                con.setDefaultUseCaches(false); //캐싱 데이터 디폴드 값 설정
                //-------------------------------------------------------------------

                //연결성공 코드가 반환됬을시
                if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder sb = new StringBuilder();
                    //buffer에 직접 씌울수 없으므로 IS리더를 사용한다 //캐릭터자료형은 utf8
                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));

                    //차곡차곡 가져온 데이터를 한줄씩 채워넣는다
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        sb.append(line);
                    }

                    //안쓰는 자원은 꼭 닫자!
                    bufferedReader.close();

                    //가져온 데이터의 가공 전 배분과정
                    switch (strings[0]) {
                        //값을 가져오는 경우 로그인,id중복체크,유저 생성,id찾기,pw찾기
                        case "login":
                        case "exist_id_check":
                        case "create_user_ok":
                        case "serch_id":
                        case "serch_pass":
                            //연결과 반환이 정상적으로 이루어 졌을시
                            //차곡차곡 채워 넣은 데이터를 앞뒤공백 제거하여 반환한다 ->onPostExcute함수 자동 실행
                            return sb.toString().trim();


                        default:
                            return "conn_fail";
                    }
                }else{
                    //연결코드 오류시
                    return "conn_fail";
                }
            }else{
                //연결 실패시
                return "conn_fail";
            }
        } catch (Exception e) {
            e.printStackTrace();
            return "conn_fail";
        }
    }

    //doInBackground함수에서 반한되는 값이 전달되는 콜백 함수(반환된후 자동실행)
    //화면에 그려지는 역할은 여기서 해야함 - 나는 get으로 받아서 프론트에서 그냥 다 계산 함
    @Override
    protected void onPostExecute(String result_String) {
        super.onPostExecute(result_String);
        String print_string = "";
        //String을 받아올 경우 함수를 종료시킨다.
        switch (result_String){
            //id랑 password 중 하나라도 미입력 시
            case "no_full":
                //id가 없을 시,
            case "no_id":
                //비밀번호가 틀릴 시,
            case "no_pass":
                //회원가입 id중복확인 부분
            case "exist":
            case "no_exist":
                //insert 반환 부분
            case "insert_OK":
                //mysql query보낼때 에러뜰시
            case "mysql_err":
                con.disconnect();
                return;

        }
        //연결 해제
        con.disconnect();
    }
}

//로그인 유지(세션 같은 역할)
//어플을 지우거나 코딩으로 지우는 코드를 짜지않는이상 휴대폰을 꺼도 유지
class Share_login_info{

    private  SharedPreferences pref;
    private Context context;
    public Share_login_info(Context context){
        this.context = context;
        pref = context.getSharedPreferences("login_info", context.MODE_PRIVATE);
    }
    public String get_login_info(){
        //login_info 라는 파일을 가져온다.
        //그 파일 중 이름이 name인 애의 정보를 문자열로 가져온다.
        return pref.getString("name","");
    }

    public void set_login_info(String write){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("name",write);
        editor.commit();
    }

    public void remove_name(){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("name");
        editor.commit();
    }

    public void remove_all(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
