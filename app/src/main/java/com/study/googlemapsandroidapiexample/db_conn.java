package com.study.googlemapsandroidapiexample;

import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.study.googlemapsandroidapiexample.Main_Page.*;
import com.study.googlemapsandroidapiexample.Main_Page.AlertDialog.AlertDialog_Custom_dialog;
import com.study.googlemapsandroidapiexample.Main_Page.AlertDialog.AlertDialog_list_item;
import com.study.googlemapsandroidapiexample.Main_Page.CalendarDialog.EventDecorator;
import com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view.Sc_custom_listview;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

//인자1:doInBackground 2:onProgressUpdate 3:onPostExecute  들의 매개변수 타입결정
//비동기적 쓰레드, 백그라운드 쓰레드와 UI쓰레드(메인 쓰레드)와 같이 쓰기위해 쓰임
public class DB_conn extends AsyncTask<String, Void, String> {

    private Context             context;                         //MainActivity this
    private BufferedReader      bufferedReader          = null;  //버퍼
    private Get_set_package     get_set_package;                 //함수 저장 변수

    private String              router_string           = "";    //백그라운드 UI작업 구분자
    private String              second_router;                   //같은 구분자를 쓰며 다른 UI이 작업이 필요한 경우 얘가 쓰임

    private ArrayList<Marker>   vending_stack;                   //롱클릭시 저장되는 마커 배열들
//--------------------------------------UI작업때 쓰이는 변수들---------------------------------------
    private ListView            sc_lv;                           //Short_cut 리스트뷰   ->get_vending_info/short_cut
    private String              marker_title;                    //마커의 타이틀        ->get_vending_info/alert
    private String              user_login_id;                   //유저 로그인 아이디   ->get_vending_info/alert

    private MaterialCalendarView materialCalendarView;           //달력에 여러 작업을 하기위한 viee ->CalendarDialog/Create_AlertDialog
//--------------------------------------------------------------------------------------------------
    //받아올 php 경로 선택 1:aws 2:autoset
    private String              link = "http://13.125.134.167/android_db_conn_source/conn.php";
    //String link  = "http://172.25.1.26/android_db_conn_source/conn.php";

    //HTTP커넥션
    private HttpURLConnection   con;

    //------------------------------------생성자 오버로딩------------------------------------------
    //Activity의 작업이 필요할 경우
    public DB_conn(Context context){
        this.context = context;
    }

    //get_marker 구분자로 불릴시 사용
    public DB_conn(Context context, Get_set_package get_set_package, ArrayList<Marker> vending_stack){
        this.context                = context;
        this.get_set_package        = get_set_package;
        this.vending_stack          = vending_stack;
    }

    //달력 수정을 할때 사용
    public DB_conn(Context context, MaterialCalendarView materialCalendarView){
        this.context                = context;
        this.materialCalendarView   = materialCalendarView;
    }

    //get_vending_info 구분자로 불릴시 사용 -> short_cut
    public DB_conn(Context context, ListView sc_lv, String second_router){
        this.context                = context;
        this.sc_lv                  = sc_lv;
        this.second_router          = second_router;
    }

    //get_vending_info 구분자로 불릴시 사용 -> alert_dialog
    public DB_conn(Context context, String marker_title, String user_login_id, String second_router){
        this.context                = context;
        this. marker_title          = marker_title;
        this. user_login_id         = user_login_id;
        this.second_router          = second_router;
    }

    //ui작업 및 추가/삭제/업데이트 기능이 필요 없는 경우
    public DB_conn(){}
    //-------------------------------------------------------------------------------------------

    //excute시, 실행되는 콜백함수 //이전에 받은 인자들을 설정된 자료형 배열로 받아온다
    @Override
    protected String doInBackground(String... strings) {
        try {

            //구별 인자값의 널값 확인 - 예외처리
            if(strings[0] != null) {

                //백그라운드 ui작업을 위해 구분자를 만들어 놓는다
                router_string = strings[0];

                //구분자로 구분한다!
                switch (strings[0]){

                    //로그인 버튼 클릭시 넘어가는 인자값들
                    case "login":
                        if(strings[1] != null && strings[2] != null) {
                            //인자: 컨트롤러,이름,비밀번호 php를 통하여 쿼리 조회
                            link += "?con=select_all";
                            link += "&id="              + strings[1];
                            link += "&password="        + strings[2];
                        }
                        break;

                    //id중복확인 버튼 클릭시 넘어가는 인자값들
                    case "exist_id_check":
                        if(strings[1] != null) {
                            //인자: 컨트롤러,이름,비밀번호 php를 통하여 쿼리 조회
                            link += "?con=exist_id_check";
                            link += "&id="              + strings[1];
                        }
                        break;

                    //회원가입 버튼 클릭시(모든 조건이 올바르게 들어가있는 상태) - 예외처리 필요없음
                    case "create_user_ok":
                            link += "?con=create_user_ok";
                            link += "&id="              + strings[1];
                            link += "&password="        + strings[2];
                            link += "&name="            + strings[3];
                            link += "&email="           + strings[4];
                            link += "&phone="           + strings[5];
                            link += "&address="         + strings[6];
                        break;

                    //id찾기 버튼 클릭시
                    case "serch_id":
                        if(strings[1] != null) {
                            link += "?con=serch_id";
                            link += "&serch_name="      + strings[1];
                        }
                        break;

                    //pw찾기 버튼 클릭시
                    case "serch_pass":
                        if(strings[1] != null && strings[2] != null) {
                            link += "?con=serch_pass";
                            link += "&serch_id="        + strings[1];
                            link += "&serch_name="      + strings[2];
                        }
                        break;

                    //구글맵이 로딩되고 초기 마커들의 정보를 DB에서 가져온다
                    case "get_markers":
                        if(strings[1] != null && strings[2] != null) {
                            link += "?con=get_markers";
                            link += "&user_login_id="   + strings[1];
                            link += "&order_date="      + strings[2];
                        }
                        break;

                    //마커(자판기)를 눌렀을때 누른 자판기의 정보를 DB에서 가져온다
                    case "get_vending_info":
                        if(strings[1] != null){
                            link += "?con=get_vending_info";
                            link += "&vending_id="      + strings[1];
                        }
                        break;

                    //작업지시서 보기 버튼 클릭시, DB에서 값을 가져온다.
                    case "get_order_sheet":
                        if(strings[1] != null){
                            link += "?con=get_order_sheet";
                            link += "&user_login_id="   + strings[1];
                            link += "&serch_date="      + strings[2];

                        }
                        break;

                    //강제 갱신 버튼을 클릭시, DB의 값을 변경한다.
                    case "insert_vending":
                        if(strings[1] != null && strings[2] != null){
                            link += "?con=insert_vending";
                            link += "&vending_id="      + strings[1];
                            link += "&user_login_id="   + strings[2];
                        }
                        break;

                    //토큰을 update하기 위해 가져온 값으로 DB를 수정한다.
                    case "token":
                        if(strings[1] != null && strings[2] != null && strings[3] != null){
                            link += "?con=token";
                            link += "&token="           + strings[1];
                            link += "&user_info_id="    + strings[2];
                            link += "&name="            + strings[3];
                        }
                        break;

                    //작업지시서의 날짜들을 가져오기위해
                    case "calendar_get_Day":
                        if(strings[1] != null){
                            link += "?con=calendar_get_Day";
                            link += "&user_info_id="     + strings[1];
                        }
                        break;
                }
            }

            //URL을 연결한다!
            URL url = new URL(link);
            con     = (HttpURLConnection)url.openConnection();

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
                    StringBuilder sb    = new StringBuilder();

                    //buffer에 직접 씌울수 없으므로 IS리더를 사용한다 //캐릭터자료형은 utf8
                    bufferedReader      = new BufferedReader(new InputStreamReader(con.getInputStream(),"UTF-8"));

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
                        //자판기 아이콘 가져오기, 특정 자판기 정보 가져오기, 작업지시서 보기, 자판기 강제 갱신,
                        //토큰 업데이트,         작업지시서 날짜 가져오기
                        case "login":
                        case "exist_id_check":
                        case "create_user_ok":
                        case "serch_id":
                        case "serch_pass":
                        case "get_markers":
                        case "get_vending_info":
                        case "get_order_sheet":
                        case "token":
                        case "calendar_get_Day":

                            //연결과 반환이 정상적으로 이루어 졌을시
                            //차곡차곡 채워 넣은 데이터를 앞뒤공백 제거하여 반환한다 ->
                            // get()으로 받는 쪽으로 반환, onPostExcute함수 자동 실행
                            return sb.toString().trim();
                        case "insert_vending":
                            return strings[1];

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

    //백그라운드에서 ui 작업 할 수 있는 공간
    //doInBackground함수에서 반한되는 값이 전달되는 콜백 함수(반환된후 자동실행)
    @Override
    protected void onPostExecute(String result_String) {
        super.onPostExecute(result_String);

        //각 excute의 결과를 ui에 적용시킨다
        switch (router_string){
            case "login":
                break;

            case "exist_id_check":
                break;

            case "create_user_ok":
                break;

            case "serch_id":
                break;

            case "serch_pass":
                break;

            //모든 자판기들을 가져와 맵에 뿌려주는구문
            case "get_markers":
                try{
                    //받아온 값이 없거나 mysql구문의 에러의 경우 아무것도 실행하지 않고 다음으로 넘어간다
                    if (result_String.equals("no_marker") || result_String.equals("mysql_err")) {
                        Toast.makeText(context, "no marker or mysql_err", Toast.LENGTH_SHORT).show();
                    }

                    //받아온 값이 JSON객체로 있을 경우
                    else {
                        //json 객체로 변환하여 json배열에 저장
                        JSONObject jsonObject   = new JSONObject(result_String);
                        JSONArray json_result   = jsonObject.getJSONArray("result");

                        //검색된 배열을 순차적으로 돈다
                        for (int i = 0; i < json_result.length(); i++) {
                            String vending_info = "";

                            //vd_id, vd_name, vd_latitude, vd_longitude, vd_place, vd_supplement, vd_soldout 가 저장 되어 있음
                            JSONObject json_obj = json_result.getJSONObject(i);
                            LatLng latLng       = new LatLng(json_obj.getDouble("vd_latitude"), json_obj.getDouble("vd_longitude"));

                            //문자열로 저장
                            vending_info += json_obj.getInt("vd_id");//+"/br/";

                            // 필요시 주석 제거후 사용!
                            //vending_info += json_obj.getString("vd_place")+"/br/";
                            //vending_info += json_obj.getString("vd_supplement");

                            //실제로 마커를 구글맵에 그린다
                            get_set_package.drawMarkers(latLng, json_obj.getString("vd_name"), vending_info, json_obj.getInt("vd_soldout"), false);
                        }


                        //새로고침하여 저장된 배열을가져온다
                        ArrayList<Marker> origin_marker = get_set_package.getOriginMarkerlist();

                        //마커가 있는지 없는지 확인하는 변수
                        boolean temp_check = false;

                        //롱클릭 배열에 저장되있는 원소만큼 반복 한다
                        for(int i=0; i<vending_stack.size(); i++){

                            //자판기 배열에 저장되어 있는 원소만큼 반복한다
                            for(int j=0; j<origin_marker.size(); j++){

                                //롱클릭 스택에 있는 원소가 새로 생성된 자판기 배열에도 있다면 true로 반환한다
                                if(vending_stack.get(i).getPosition().equals(origin_marker.get(j).getPosition())){
                                    temp_check = true;
                                    break;
                                }
                            }

                            //만약 배열이 일치하지 않는다면 스택배열의 원소는 삭제한다
                            if(!temp_check){
                                vending_stack.remove(i);
                            }

                        }

                        //스택배열의 값만 복사할 배열을 생성
                        ArrayList<Marker> temp_stack = new ArrayList<Marker>();

                        //값만 복사한다
                        temp_stack.addAll(vending_stack);

                        //기존의 스택 배열은 지운다(안지울시 값이 2배씩 늘어나서 결국 터짐)
                        vending_stack.clear();

                        //복사한 값을 가지고 마커를 그린다(vending_stack배열에 다시 차곡차곡 쌓임)
                        for(int i=0; i<temp_stack.size(); i++){

                            //첫번째 원소인경우 빨간색으로 그린다
                            if(i == 0){
                                get_set_package.drawMarkers(temp_stack.get(i).getPosition(), temp_stack.get(i).getTitle(), temp_stack.get(i).getSnippet(), -1, false);
                            }

                            //두번째 원소인 경우 파란색으로 그린다
                            else{
                                get_set_package.drawMarkers(temp_stack.get(i).getPosition(), temp_stack.get(i).getTitle(), temp_stack.get(i).getSnippet(), -2, false);
                            }
                        }

                    }
                }catch (Exception e){
                    Toast.makeText(context, "DB_get_markers_err", Toast.LENGTH_SHORT).show();
                }

                break;

            //short_cut 생성 구문
            case "get_vending_info":
                try{
                    //에러상황들 예외처리
                    if (result_String.equals("no_vending")) {
                        Toast.makeText(context, "no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_String.equals("no_vending1")) {
                        Toast.makeText(context, "1no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_String.equals("no_vending2")) {
                        Toast.makeText(context, "2no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_String.equals("no_vending3")) {
                        Toast.makeText(context, "3no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_String.equals("no_vending4")) {
                        Toast.makeText(context, "4no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //값들을 제대로 받아 왔을 시,
                    else {

                        //shortcut / alert_dialog 의 router
                        switch (second_router){

                            //short_cut을 업데이트곳
                            case "short_cut":
                            //json 객체로 변환하여 json배열에 저장
                            JSONObject jsonObject = new JSONObject(result_String);

                            //생성자에서 sc_lv로 받아온 값이 널이 아니면 실행
                            if (sc_lv != null) {
                                //오른쪽 밑에 보여주는 listview를 custom하여 보여준다
                                Sc_custom_listview sc_custom = new Sc_custom_listview(context, jsonObject, sc_lv);

                                //custom Listview 만들기!!
                                sc_custom.change_listview();
                            }
                            break;

                            //특정 자판기의 정보(customalert)보여주는 곳
                            case "alert_dialog":

                                //특정 자판기의 아이템들을 담을 배열 선언
                                ArrayList<AlertDialog_list_item> list_itemArrayList = new ArrayList<AlertDialog_list_item>();

                                //json 객체로 변환하여 json배열에 저장
                                jsonObject              = new JSONObject(result_String);

                                //자판기의 돈 상태를 가져온다
                                JSONArray coin_arr   = jsonObject.getJSONArray("coin");

                                for(int i =0; i<coin_arr.length(); i++){

                                    //[0]=coin_var [1]=vd_id [2]=1000 [3]=500 [4]=100 [5]=50 [6]=10 [7]=5 [8]=1
                                    JSONObject json_obj = coin_arr.getJSONObject(i);
                                    Log.e("<<<<",json_obj.getString("1000")+"/"+json_obj.getString("500")+"/"+json_obj.getString("100"));

                                }

                                //실제 반복문을 도는 알맹이를 배열로 가져온다
                                JSONArray json_result   = jsonObject.getJSONArray("result");
                                Log.e("<><>",json_result.toString());

                                //검색된 배열을 순차적으로 돈다
                                for (int i = 0; i < json_result.length(); i++) {

                                    //[0]=vd_id [1]=vd_name [2]z=drink_name [3]=drink_path [4]=drink_stook [5]=drink_line [6]=note
                                    JSONObject json_obj = json_result.getJSONObject(i);
                                    //인자값 : 제품명,이미지경로,제품수량,제품라인

                                    //작업 지시가 없는 자판기의 경우 -> 맨 첫 줄은 공백으로 놔둔다
                                    if(json_obj.getString("note") == null || json_obj.getString("note").equals("null")){
                                        list_itemArrayList.add(new AlertDialog_list_item("작업 지시서가 없는 자판기 입니다",json_obj.getString("drink_name"), json_obj.getString("drink_path"), json_obj.getInt("drink_stook"), json_obj.getInt("drink_line")));
                                    }

                                    //작업 지시서가 있는 자판기의 경우 -> 맨 첫 줄은 작업지시 내용을 출력하게 한다.
                                    else {

                                        //첫번째 반복문이 아니며, 작업지시서가 있는 경우
                                        if(i != 0 && !json_obj.getString("note").equals(" ") && json_obj.getString("note") != null){

                                            //첫번째 작업지시서를 가져온다.
                                            AlertDialog_list_item temp = list_itemArrayList.get(0);

                                            //첫번째 작업지시서에 아무거도 작성되어 있지 않다면
                                            if(temp.getNote().equals("작업 지시서가 없는 자판기 입니다")){

                                                //첫번째 textview를 새로 덮어 씌워서 저장한다
                                                list_itemArrayList.set(0,new AlertDialog_list_item(json_obj.getString("note"), temp.getName(), temp.getImg_path(), temp.getCount(), temp.getDrink_line()));

                                            }

                                            //작업지시서에 작업지시가 작성되어 있다면
                                            else {

                                                //이전 textview에 덧붙여서 저장한다
                                                list_itemArrayList.set(0, new AlertDialog_list_item(temp.getNote() + "\r\n" + json_obj.getString("note"), temp.getName(), temp.getImg_path(), temp.getCount(), temp.getDrink_line()));

                                            }

                                            //현재 반복문의 결과값을 저장 한다.
                                            list_itemArrayList.add(new AlertDialog_list_item(json_obj.getString("note"), json_obj.getString("drink_name"), json_obj.getString("drink_path"), json_obj.getInt("drink_stook"), json_obj.getInt("drink_line")));
                                        }

                                        //첫번째 반복문(첫번째 라인)에 작업지시가 있는경우 set이아니라 add로 추가를 한다
                                        else if(i == 0 && !json_obj.getString("note").equals(" ") && json_obj.getString("note") != null){
                                            list_itemArrayList.add(new AlertDialog_list_item(json_obj.getString("note"), json_obj.getString("drink_name"), json_obj.getString("drink_path"), json_obj.getInt("drink_stook"), json_obj.getInt("drink_line")));
                                        }

                                        //작업지시서가 없는경우
                                        else {

                                            //작업지시에는 아무거도 작성하지 않고 값만 저장한다.
                                            list_itemArrayList.add(new AlertDialog_list_item(" ", json_obj.getString("drink_name"), json_obj.getString("drink_path"), json_obj.getInt("drink_stook"), json_obj.getInt("drink_line")));

                                        }

                                    }
                                }

                                //생성자로 올바른 값을 받았다면 custom alertdialog를 만들어서 띄운다
                                if(marker_title != "" && user_login_id != "") {
                                    //custom_dialog를 만들어서 보여준다
                                    AlertDialog_Custom_dialog custom_dialog = new AlertDialog_Custom_dialog(context, list_itemArrayList, marker_title, json_result.getJSONObject(0).getString("vd_id"), user_login_id);
                                    custom_dialog.callFunction();
                                }else{
                                    Toast.makeText(context, "no_title or user_login_id", Toast.LENGTH_SHORT).show();
                                }

                            break;
                        }

                    }
                }catch (Exception e){
                    Toast.makeText(context, "DB_get_vending_info_err", Toast.LENGTH_SHORT).show();
                }
                break;

            case "get_order_sheet":
                Log.e("<><>",result_String);
                break;

            case "insert_vending":
                Log.e("<><>",result_String+"/<>/");
                break;

            case "token":
                break;

            case "calendar_get_Day":
                //값이 없는 경우 아무것도 하지 않는다
                if(result_String.equals("no_date") || result_String == "" || result_String == " " ||result_String == null){
                    Toast.makeText(context, "No date Please check", Toast.LENGTH_SHORT).show();
                    return;
                }

                //값이 있는 경우
                else{
                    try {
                        //json 객체로 변환하여 json배열에 저장
                        JSONObject jsonObject   = new JSONObject(result_String);
                        JSONArray json_result   = jsonObject.getJSONArray("result");

                        //동그란 빨간점들이 저장되는 공간
                        ArrayList<CalendarDay> dates = new ArrayList<>();

                        //검색된 배열을 순차적으로 돈다
                        for (int i = 0; i < json_result.length(); i++) {
                            //order_date가 저장되어 있음
                            JSONObject json_obj = json_result.getJSONObject(i);

                            //[0]=년, [1]=월, [2]=일
                            String[] date_arr= json_obj.getString("order_date").split("-");
                            int year  = Integer.parseInt(date_arr[0]);
                            int month =  Integer.parseInt(date_arr[1])-1; // 왠지는 몰라도 달이 0부터 시작함.. 그래서 1빼야함
                            int day =  Integer.parseInt(date_arr[2]);

                            //원을 그리고 싶은 날짜를 지정한다
                            dates.add(CalendarDay.from(year,month,day));
                        }

                        //배열에 저장된 날짜들에 빨간 점을 전부 그린다
                        materialCalendarView.addDecorator(new EventDecorator(context, Color.RED, dates));

                        //배경색만 지정하고 싶은경우 얘를쓴다
                        //materialCalendarView.addDecorator(new EventDecorator(context, dates));'

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                break;
        }

        //연결 해제
        con.disconnect();
    }
}

