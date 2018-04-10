package com.study.googlemapsandroidapiexample;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.study.googlemapsandroidapiexample.Login_Page.Share_login_info;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

//인자1:doInBackground 2:onProgressUpdate 3:onPostExecute  들의 매개변수 타입결정
//비동기적 쓰레드, 백그라운드 쓰레드와 UI쓰레드(메인 쓰레드)와 같이 쓰기위해 쓰임
public class DB_conn extends AsyncTask<String, Void, String> {
    private Context             context;                         //MainActivity this
    private BufferedReader      bufferedReader          = null;  //버퍼
    private Share_login_info    share_login_info_obj;            //로그인 정보

    //받아올 php 경로 선택 1:aws 2:autoset
    String link = "http://ec2-13-125-198-224.ap-northeast-2.compute.amazonaws.com/android_db_conn_source/conn.php";
    //String link  = "http://172.25.1.26/android_db_conn_source/conn.php";

    //HTTP커넥션
    HttpURLConnection con;

    //------------------------------------생성자 오버로딩------------------------------------------
    public DB_conn(Context context, Share_login_info share_login_info_obj){
        this.context              = context;
        this.share_login_info_obj = share_login_info_obj;
    }

    public DB_conn(Context context){
        this.context = context;
    }

    public DB_conn(){}
    //-------------------------------------------------------------------------------------------

    //excute시, 실행되는 콜백함수 //이전에 받은 인자들을 설정된 자료형 배열로 받아온다
    @Override
    protected String doInBackground(String... strings) {
        try {

            //구별 인자값의 널값 확인 - 예외처리
            if(strings[0] != null) {

                switch (strings[0]){

                    //로그인 버튼 클릭시 넘어가는 인자값들
                    case "login":
                        if(strings[1] != null && strings[2] != null) {
                            //인자: 컨트롤러,이름,비밀번호 php를 통하여 쿼리 조회
                            link += "?con=select_all";
                            link += "&id=" + strings[1];
                            link += "&password=" + strings[2];
                        }
                        break;

                    //id중복확인 버튼 클릭시 넘어가는 인자값들
                    case "exist_id_check":
                        if(strings[1] != null) {
                            //인자: 컨트롤러,이름,비밀번호 php를 통하여 쿼리 조회
                            link += "?con=exist_id_check";
                            link += "&id=" + strings[1];
                        }
                        break;

                    //회원가입 버튼 클릭시(모든 조건이 올바르게 들어가있는 상태) - 예외처리 필요없음
                    case "create_user_ok":
                        link += "?con=create_user_ok";
                        link += "&id=" + strings[1];
                        link += "&password=" + strings[2];
                        link += "&name=" + strings[3];
                        link += "&email=" + strings[4];
                        link += "&phone=" + strings[5];
                        link += "&address=" + strings[6];
                        break;

                    //id찾기 버튼 클릭시
                    case "serch_id":
                        if(strings[1] != null) {
                            link += "?con=serch_id";
                            link += "&serch_name=" + strings[1];
                        }
                        break;

                    //pw찾기 버튼 클릭시
                    case "serch_pass":
                        if(strings[1] != null && strings[2] != null) {
                            link += "?con=serch_pass";
                            link += "&serch_id=" + strings[1];
                            link += "&serch_name=" + strings[2];
                        }
                        break;

                    //구글맵이 로딩되고 초기 마커들의 정보를 DB에서 가져온다
                    case "get_markers":
                        if(strings[1] != null) {
                            link += "?con=get_markers";
                            link += "&user_id=" + strings[1];
                        }
                        break;

                    //마커(자판기)를 눌렀을때 누른 자판기의 정보를 DB에서 가져온다
                    case "get_vending_info":
                        if(strings[1] != null){
                            link += "?con=get_vending_info";
                            link += "&vending_id=" + strings[1];
                        }
                        break;

                    //작업지시서 보기 버튼 클릭시, DB에서 값을 가져온다.
                    case "get_order_sheet":
                        if(strings[1] != null){
                            link += "?con=get_order_sheet";
                            link += "&user_login_id=" + strings[1];
                        }
                        break;

                    //강제 갱신 버튼을 클릭시, DB의 값을 변경한다.
                    case "insert_vending":
                        if(strings[1] != null){
                            link += "?con=insert_vending";
                            link += "&vending_id=" + strings[1];
                        }
                        break;
                }
            }

            URL url = new URL(link);
            con = (HttpURLConnection)url.openConnection();

            //연결 성공시
            if(con != null) {

                //-----------------------------연결설정-------------------------------
                con.setRequestMethod("GET");    //get방식 통신
                con.setConnectTimeout(5000);    //지연됬을경우 기다려주는시간 5초
                con.setUseCaches(false);        //캐싱데이터를 안받음
                con.setDefaultUseCaches(false); //캐싱 데이터 디폴드 값 설정
                //-------------------------------------------------------------------

                //연결성공 코드가 반환됬을시
                if(con.getResponseCode() == HttpURLConnection.HTTP_OK) {

                    //문자열 빌더 생성
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
                        //값을 가져오는 경우

                        //로그인, id중복체크, 유저 생성, id찾기, pw찾기,
                        //자판기 아이콘 가져오기, 특정 자판기 정보 가져오기, 작업지시서 보기, 자판기 강제 갱신
                        case "login":
                        case "exist_id_check":
                        case "create_user_ok":
                        case "serch_id":
                        case "serch_pass":
                        case "get_markers":
                        case "get_vending_info":
                        case "get_order_sheet":
                        case "insert_vending":

                            //연결과 반환이 정상적으로 이루어 졌을시
                            //차곡차곡 채워 넣은 데이터를 앞뒤공백 제거하여 반환한다 ->
                            // get()으로 받는 쪽으로 반환, onPostExcute함수 자동 실행
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
            Log.e("<<<<<<<<<<<<<<<<<<<<<", e.toString());
            return "conn_failed";
        }
    }

    //doInBackground함수에서 반한되는 값이 전달되는 콜백 함수(반환된후 자동실행)
    //화면에 그려지는 역할은 여기서 해야함 - 로그인의 경우 get으로 받아서 프론트에서 그냥 다 계산 함
    //->이 계산이 끝나야 다음 화면의 작업이 진행되기 때문
    @Override
    protected void onPostExecute(String result_String) {
        super.onPostExecute(result_String);
        //String을 받아올 경우 함수를 종료시킨다.
        switch (result_String){

            //id랑 password 중 하나라도 미입력 시
            case "no_full":

            //id가 없을 시
            case "no_id":

            //비밀번호가 틀릴 시
            case "no_pass":

            //회원가입 id중복확인 부분 id가 중복 일 경우
            case "exist":

            //id가 중복이 아닐 경우
            case "no_exist":

            //insert 반환 부분
            case "insert_OK":

            //매진, 매진임박 자판기가 없을 시
            case "no_marker":

            //mysql query보낼때 에러뜰시
            case "mysql_err":

            //찾는 마커(자판기)가 없을 시
            case "no_vending":

                //알림창 띄우지 않고 종료
                con.disconnect();

                return;

            //접속 실패 시
            case "conn_failed":
                Toast.makeText(context, "Please Network connect", Toast.LENGTH_SHORT).show();
                return ;
        }
        //연결 해제
        con.disconnect();
    }
}
