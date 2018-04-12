package com.study.googlemapsandroidapiexample.Main_Page;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import com.squareup.picasso.Picasso;
import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.Login_Page.*;
import com.study.googlemapsandroidapiexample.DB_conn;

import de.hdodenhof.circleimageview.CircleImageView;

//로케이션리스너 생성 클래스
class Locationlistener implements LocationListener, GoogleMap.OnMapLongClickListener {
    private GoogleMap           gmap;                       //구글 지도
    private TextView            my_status, next_vending;    //내 정보(왼쪽 위에 표시되는 tv), 다음 자판기까지의 거리
    private Context             context;                    //MainActivity this
    private Get_set_package     get_set_package;            //내가 만든 getset패키지 함수!
    private Marker              closestMarker;              //가장 가까운 마커(다음 가야할 자판기)
    private Menu                navi_menu;                  //drawerlayout의 menu
    private ListView            sc_lv;                      //오른쪽 밑에 표시되는 제품의 listview

    private ArrayList<Marker>   originMarkerlist;           //현재 표시되어있는 자판기들 배열

    private String              before_snippet = "";        //db접속을 최소화 하기위한 String

    private Location            lastlocation;               //최초 한번만 update되는 함수가 호출되어
                                                            //생기는 에러를 방지하기위한 변수

    //-----한번만 실행되게 하기위한 Boolean 변수들-----
    //GPS추적 버튼 클릭시
    private boolean             location_button                         = false;

    //가장 가까운 자판기 추적 버튼 클릭 시
    private boolean             closest_vendingmachine_tracking_button  = false;
    //-----------------------------------------------

    //생성자
    public Locationlistener(Context context, ArrayList<Marker> originMarkerlist, GoogleMap gmap, ListView sc_lv, TextView my_status, TextView next_vending, Get_set_package get_set_package, Menu navi_menu) {

        this.originMarkerlist   = originMarkerlist;
        this.gmap               = gmap;
        this.sc_lv              = sc_lv;
        this.my_status          = my_status;
        this.next_vending       = next_vending;
        this.context            = context;
        this.get_set_package    = get_set_package;
        this.navi_menu          = navi_menu;

        gmap.setOnMapLongClickListener(this);
    }

    //이전 snippet을 수정하는 함수
    public void setBefore_snippet(String before_snippet) {
        this.before_snippet = before_snippet;
    }

    //가장 가까운 자판기 초기화
    public void setClosestMarker()  { closestMarker = null; }

    //마지막 현재위치 초기화
    public void setLastlocation()   { lastlocation = null; }

    //프로그래머가 지정한 일정 시간 or 간 meter만큼 반복해서 실행되는 함수
    @Override
    public void onLocationChanged(Location location) {

        //위도 경도 고도
        Double lattitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Double altitude  = location.getAltitude();

        //status에 저장될 msg
        String msg  = "";
        String addr = "";

        //get_set_package가 null이아니면
        if (get_set_package != null) {
            //현재 지명 가져오기
            addr = get_set_package.getAddress(new LatLng(lattitude, longitude));
        }

        //가장 가까운 자판기 추적 버튼 활성화시
        if (closest_vendingmachine_tracking_button) {

            //현재위치와 가장 가까운 마커를 찾는 과정
            for (int i = 1; i < originMarkerlist.size(); i++) {

                //값이 하나도 안들어 가 있을경우 초기화 시켜준다.
                if (closestMarker == null) {
                    closestMarker = originMarkerlist.get(i);
                }

                //값이 들어가 있는경우 거리 비교
                else {
                    LatLng my_latlng = new LatLng(lattitude, longitude);
                    double sec_meter = get_set_package.getmeter(my_latlng, closestMarker.getPosition());
                    double fir_meter = get_set_package.getmeter(my_latlng, originMarkerlist.get(i).getPosition());

                    //거리가 더 짧은게 올 경우 가장 가까운 마커를 최신화 시켜준다.
                    if (sec_meter > fir_meter) {
                        closestMarker = originMarkerlist.get(i);
                    }
                }
            }

            //만약 보충할 자판기가 없으면 다음 가야할 마커를 그리지 않는다.
            if (originMarkerlist.size() > 1) {
                //다음 가야할 마커 그리기
                get_set_package.drawMarkers(closestMarker.getPosition(), closestMarker.getTitle(), closestMarker.getSnippet(), 0, false);
            }

            //다음 가야할 장소에대해 execute로 DB정보를 가져온다
            //매Update마다 가져오는건 부담이 크기때문에 계속 동일한 장소를 가리킬시, 한번만 가져오도록한다
            //또한 마커가 하나이상,가까운 마커가 있을때만 받아오도록한다(예외처리)
            if (closestMarker != null) {
                if (!before_snippet.equals(closestMarker.getSnippet()) && originMarkerlist.size() > 1) {
                    try {
                        //db 접속(try/catch 필수)
                        DB_conn db_conn_obj = new DB_conn(context, sc_lv, "short_cut");

                        //db에 접속하여 short_cut 생성
                        db_conn_obj.execute("get_vending_info", closestMarker.getSnippet());

                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //현재 가져온 정보를 저장한다
                    before_snippet = closestMarker.getSnippet();

                    //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
                    //순서대로 : 열기버튼,sortcutlayout,확인버튼,닫기버튼
                    if ((((Activity) context).findViewById(R.id.open_button)).getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.sc_layout)).setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.ok_button)).setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
                    }

                    //다음가야할 정보 출력공간을 보이게 한다. -> 오른쪽 맨위 layout
                    if ((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
                    }
                }
            }
        }


        //0번째는 사용자의 위치! -> 아무것도 안들었다면 현재 위치를 넣어준다!
        if (originMarkerlist.size() == 0) {
            //0번째 arraylist에 배열 추가
            //0번째에는 항상 사용자의 위치가 들어간다
            //마커 옵션 함수를 만들고, 반환하여 구글맵에 마커를 그린다.
            originMarkerlist.add(0, gmap.addMarker(get_set_package.getMarkerOption(new LatLng(lattitude, longitude), addr, true)));
        }

        //0번째 위치를 갱신한다.
        else {
            //get으로 0번째의 마커를 지우고, set으로 새로운 위치에 마커를 생성한다.
            originMarkerlist.get(0).remove();
            originMarkerlist.set(0, gmap.addMarker(get_set_package.getMarkerOption(new LatLng(lattitude, longitude), addr, true)));
        }

        //자신의 위치에 그려지는 마크는 없앤다.
        originMarkerlist.get(0).setVisible(false);

        //위치 추적 활성화 버튼을 눌렀다면 추적 활성화
        if (location_button) {
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
        }

        //처음 받아오는 결과값으로는 연산이 불가능(이전꺼와 현재꺼 필요)
        //그러므로 최초 1번 받아오는 값은 저장만 하게 한다.
        //if문은 예외처리
        if (lastlocation != null && get_set_package != null && location != null) {

            //이전미터와 현재미터의 거리를 가져오기
            double d                = get_set_package.getmeter(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()));

            //현재 시간과 이전시간의 시간 차 가져오기
            double resultTime       = location.getTime() - lastlocation.getTime();

            double kmPerHour        = (d / resultTime) * 3600; //km/s -> km/h 변환 과정(1초->1시 = 3600)

            //현재 날짜 구하기
            long nowSecond          = location.getTime();
            Date date               = new Date(nowSecond);

            //데이터 포멧 설정
            SimpleDateFormat sdf    = new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초");
            String nowDate          = sdf.format(date);

            //0km/s근접 소수점일 경우 0으로 내림(E-7이런 식으로 나오는거 방지)
            if (kmPerHour < 0.1)
                kmPerHour = 0;

            //double형 -> String형 변환 + 소수점 한자리까지만 표현
            String SkmPerHour = String.format("%.1f", kmPerHour);   //현재 km/h
            //msg ="좌표: [ " + lattitude + ":" + longitude+"]\n"; //현재 위치의 좌표 출력 //주석 제거시 표현
            //msg+="고도: "+String.format("%.1f",altitude)+"m\n"; //고도의 세계표준을 한국표준으로 바꾸는걸 모르겟엉.. 쓸꺼면 걍써 잘 되니까...

            msg += addr + "\n";          //주소
            msg += nowDate + "\n";       //현재 날짜
            msg += SkmPerHour + "km/h";  //현재 km/h

            //추가된 자판기가 하나이상 있을경우 실행
            if (originMarkerlist.size() > 1 && closestMarker != null) {
                double next_meter = get_set_package.getmeter(originMarkerlist.get(0).getPosition(), closestMarker.getPosition());

                //1km 이상의 거리일 경우
                if((int)next_meter >= 1000){
                    //1000m가 넘어 갈시, km로만 표시한다
                    //현재 m의 올림으로 1000m미만으로 떨어질경우, 다시 m로 표시된다

                    int km = (int)Math.ceil(next_meter/1000);

                    next_vending.setText("NEXT:" + km + "km");
                }

                //1km미만의 거리 일경우 m로 표시하며 소수점 1자리까지 표시한다.
                else {
                    next_vending.setText("NEXT:" + String.format("%.1f", next_meter) + "m");
                }
            }

            //얼마나 신뢰도를 가지는지 보고 싶으면 주석 제거(하지만 딱히 신뢰도가 정확하지 않음)
            //나중에 신뢰도를 사용할 알고리즘이 있다면 이거 쓰지말고
            //직접 알고리즘으로 짜서 예외처리하는 것이 보다 효율적
            //msg+="\n신뢰도:"+location.getAccuracy();

            //왼쪽 위에 표시되는 textview
            my_status.setText(msg);

        }

        // 생성자 같은 역할(최초 한번만 실행)
        else {
            //최초 카메라 위치 잡기 ->자신의 위치가 갱신 된 이후 최초로 한번만 자신의 위치를 찾아서 카메라를 이동시킨다.
            //기본적으로 영진전문대 -> 자신의 위치 순으로 잡힌다.
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 17));
        }

        lastlocation = location;
    }

    //---------------------------------GPS 상태 관련 콜백 함수--------------------------------------
    //GPS 상태가 바뀔때 호출됨
    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    //GPS가 켜져있으면 실행됨!
    @Override
    public void onProviderEnabled(String s) {
        Toast.makeText(context, "Gps is turned on!! ",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String s) {
        //GPS가 꺼져 있으면 설정 화면으로 가기
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
        Toast.makeText(context, "Gps is turned off!! ",
                Toast.LENGTH_SHORT).show();
    }

    //------------------------------------------------------------------------------------

    //지구는 둥글구나아~
    private double toRad(Double d) {
        return d * Math.PI / 180;
    }

    //현재 위치 추적 버튼을 누를시,
    public void location_button() {
        location_button = (location_button) ? false : true;
    }


    //가장 가까운 자판기 추적 버튼을 누를시,
    public void closest_vendingmachine_tracking_button() {
        //자판기가 없을경우 에러 방지를 위해 작성(자판기는 최소 1개이상)
        if (originMarkerlist.size() > 1) {
            closest_vendingmachine_tracking_button = (closest_vendingmachine_tracking_button) ? false : true;
        } else {
            closest_vendingmachine_tracking_button = false;
        }
    }

    //마커를 길게 클릭 할경우 발생하는 콜백 함수
    //사용자가 직접 가고 싶은 위치를 지정 할 수 있게 한다.
    //클릭한 위치로부터 반경x만큼 지정하여 그반경에 마커가 있는지 확인한다.
    @Override
    public void onMapLongClick(LatLng latLng) {

        for (int i = 0; i < originMarkerlist.size(); i++) {

            //반경에 마커가 있는경우 DB를통해 그마커의 상세정보들을 가져온다.
            //실수를 많이 줄경우, 주변 자판기와 겹칠우려가 있으므로 너무 많이 주지않으며 클릭시, 적당한 반경을 탐색하게 해야한다.
            if (Math.abs(originMarkerlist.get(i).getPosition().latitude - latLng.latitude) < 0.0002 &&
                    Math.abs(originMarkerlist.get(i).getPosition().longitude - latLng.longitude) < 0.0002) {

                //클릭한 자판기의 지명을 출력한다.
                Toast.makeText(context, originMarkerlist.get(i).getTitle() + "", Toast.LENGTH_SHORT).show();

                try {
                    //db 접속 try/catch 필수
                    DB_conn db_conn_obj = new DB_conn(context, sc_lv, "short_cut");

                    //서버에서 값을 가져와 shortcut을 만들어준다
                    db_conn_obj.execute("get_vending_info", originMarkerlist.get(i).getSnippet());

                    //오른쪽 밑 레이아웃이 현재 안보이고 있다면 보이게 한다
                    if(((Activity) context).findViewById(R.id.open_button).getVisibility() == View.GONE){
                        //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
                        //순서대로 : 오른쪽 밑 레이아웃. 갱신버튼, 닫기버튼
                        (((Activity) context).findViewById(R.id.sc_layout)).setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.ok_button)).setVisibility(View.VISIBLE);
                        (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
                    }

                    //다음가야할 정보 출력공간을 보이게 한다.
                    //오른쪽 위 레이아웃
                    if((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                        (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
                    }

                    //다음 자판기 추적을 켜져있을경우 끈다.
                    closest_vendingmachine_tracking_button = false;

                    //추적을 실제로 껏을 경우, menu바의 추적 활성화도 비활성화로 바꾼다.
                    navi_menu.findItem(R.id.Closest_V_machine_tracking).setChecked(false);

                    //다음 가야할 자판기를 최신화 시킨다.
                    before_snippet = originMarkerlist.get(i).getSnippet();

                    next_vending.setText("Loading..");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //다음 가야할 자판기의 마커를 최신화 시킨다(맵의 마커)
                get_set_package.drawMarkers(originMarkerlist.get(i).getPosition(), originMarkerlist.get(i).getTitle(), originMarkerlist.get(i).getSnippet(), 0, false);

                //다음 가야할 자판기의 마커를 최신화시킨다(상태창의 마커)
                closestMarker = originMarkerlist.get(i);
                break;
            }
        }
    }
}

//마커를 클릭했을때, 이벤트들을 모아놓은 클래스(마커 클릭, 클릭시 출력되는 view 설정)
class Mark_click_event implements GoogleMap.OnMarkerClickListener {
    private Context             context;            //MainActivity this
    private DB_conn             db_conn_obj;        //db접속 변수
    private ArrayList<Marker>   originMarkerlist;   //마커들이 저장되어있는 배열
    private String              user_login_id;      //유저 로그인 아이디
    private Handler             handler;            //핸둘러!
    //생성자
    public Mark_click_event(Context context, GoogleMap googleMap, ArrayList<Marker> originMarkerlist, String user_login_id, Handler handler) {
        this.context            = context;
        this.originMarkerlist   = originMarkerlist;
        this.user_login_id      = user_login_id;
        this.handler            = handler;
        //마커 클릭을 활성화 시킨다
        googleMap.setOnMarkerClickListener(this);
    }

    //OnMarkerClickListener 오버라이딩
    //마커를 클릭했을시 이벤트 -> custom_alertDialog를 만들어서 띄워준다
    @Override
    public boolean onMarkerClick(Marker marker) {

        //핸들러 생성
        Runnable task = new Runnable() {
            @Override
            public void run() {

                //로딩 화면생성
                handler.sendEmptyMessage(1);
                try {
                    //1.5초 지연
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                //로딩화면 제거
                handler.sendEmptyMessage(2);
            }
        };

        //쓰레드 생성
        Thread thread = new Thread(task);


        try {

            //쓰레드 실행
            thread.start();
            //db접속 try/catch 필수
            db_conn_obj = new DB_conn(context, marker.getTitle(), user_login_id, "alert_dialog");

            //서버의 결과값으로 custom alert dailog를 만들어 띄운다
            db_conn_obj.execute("get_vending_info", marker.getSnippet());

        } catch (Exception e) {
            e.printStackTrace();
        }

        //반환형이 false일 경우 기존의 클릭 이벤트도 같이 발생된다.
        return true;
    }
}

//MainActivity
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    //-----------------------------------------레이아웃-----------------------------------------
    private DrawerLayout        drawerLayout;

    //-------------------------------------화면에 출력되는 애--------------------------------------
    private TextView            user_name_tv, user_email_tv, user_id_tv_hide, my_status, next_vending;
    private ListView            sc_lv;
    private Button              ok_button,close_button;
    private ImageButton         open_button;
    private Get_set_package     get_set_package;
    private Mark_click_event    mark_click_event;
    private DB_conn             db_conn_obj;
    private Menu                navi_menu;
    private GoogleMap           gmap;

    //--------------------------------------------변수--------------------------------------------
    private String[]            user_info;
    private long                fir_time, sec_time;
    private Boolean             drawer_check        = true;
    private ArrayList<Marker>   originMarkerlist    = new ArrayList<Marker>();
    private String              image_path          ="http://ec2-13-125-198-224.ap-northeast-2.compute.amazonaws.com/images/supplementer/";


    //--------------------------------------------이외--------------------------------------------
    private Locationlistener    listener;
    private Share_login_info    share_login_info_obj;
    private ImageView           loading_iv;             //로딩 화면 iv
    private Handler             handler;                //로딩Thread handler

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //쓰레드 핸들러(로딩화면을 보이거나 지운다)
        handler = new Handler(){
            @Override
            public void handleMessage(Message msg) {
                if(msg.what == 1){
                    //login_iv에 애니메이션을 넣어 놓는다.(로딩화면 애니메이션)
                    Animation animation = AnimationUtils.loadAnimation(MainActivity.this, R.anim.rotate);
                    loading_iv.setAnimation(animation);

                    loading_iv.setVisibility(View.VISIBLE);
                }else if(msg.what == 2){
                    //실행중인 애니메이션을 전부 지운다
                    loading_iv.clearAnimation();
                    loading_iv.setVisibility(View.GONE);
                }
            }
        };

        //-------------------------------인터넷연결 확인/관리 부분-------------------------------
        //인터넷 연결 체크 // 실시간으로 연결되는지 검사한다
        //연결될경우 실시간으로 알려준다.
        //만약 네트워크 변경이 일어난다면(네트워크에 새롭게 접속을 한다면)
        //(네트워크->다른 네크워크 or 없음 -> 네트워크) 접속시 앱 재시작
        Internet_conn_check receiver = new Internet_conn_check(this);
        IntentFilter        filter   = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(receiver, filter);

        //최초 네트워크 확인 + GPS On/Off확인 -> 연결되어있지 않다면 아무거도 보여주지 않는다
        if (!connect_check()) {
            return;
        }

        //------------------------------레이아웃 셋팅-------------------------------------------------

        //activity의 context정보 변수에 담기
        Window win = getWindow();

        // 첫번째 layout을 추가한다.
        win.setContentView(R.layout.main_page_activity_main);

        //inflater 얻어오기
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //xml 설정하기
        final ConstraintLayout linear = (ConstraintLayout) inflater.inflate(R.layout.main_page_second_activity, null);

        //레이아웃의 폭과 높이 설정
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);

        //기존의 레이아웃에 겹쳐서 배치
        win.addContentView(linear, params);

        //드래그 했을때 보여지는 레이아웃 설정
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.addDrawerListener(new DrawerLayout.DrawerListener() {

            //슬라이드를 시작했을 경우(열었을때 -> 닫았을때는 실행x)
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawer_check) {
                    //현재 상태바를 보이지않게 한다.
                    my_status.setVisibility(View.GONE);
                    drawer_check = false;
                }
            }

            //DrawLayout이 열렸을때 불리는 함수
            @Override
            public void onDrawerOpened(View drawerView) {}

            //DrawLayout을 닫는 행위를 할 경우
            //보이지않게 한 상태바를 다시 보이게 한다.
            @Override
            public void onDrawerClosed(View drawerView) {
                my_status.setVisibility(View.VISIBLE);
                drawer_check = true;
            }

            @Override
            public void onDrawerStateChanged(int newState) {
            }
        });
        //-----------------------------레이아웃 셋팅 끝--------------------------------------------

        //저장소들 id 가져오기!!!!!!
        my_status       =   (TextView)          findViewById(R.id.my_status);
        next_vending    =   (TextView)          findViewById(R.id.next_vending);
        sc_lv           =   (ListView)          findViewById(R.id.sc_lv);
        ok_button       =   (Button)            findViewById(R.id.ok_button);
        close_button    =   (Button)            findViewById(R.id.close_button);
        open_button     =   (ImageButton)       findViewById(R.id.open_button);
        loading_iv      =   (ImageView)         findViewById(R.id.loading_iv);


        //네비게이션뷰 가져오기(왼쪽에서 오른쪽으로 슬라이드 할시, 나오는 창)
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //로그인 한 결과값을 가져온다(사용자 정보)
        Intent data = getIntent();

        //정상적으로 값을 가져왔다면
        if (data.getStringExtra("user_info") != null) {

            //유저 정보를 가져온다
            String str = data.getStringExtra("user_info");
            //[0]=login_id [1]=name [2]=email [3]=imgsrc
            user_info = str.split("/br/");

            //네비게이션의 헤더부분 가져오기
            View hView = navigationView.getHeaderView(0);

            //보충기사 사진 업데이트
            Picasso.with(this)
                    .load(image_path+user_info[0]+".png")
                    //사진이 없을시 여기에 지정한 애로 출력됨
                    .placeholder(R.drawable.face_ex)
                    .into((CircleImageView) hView.findViewById(R.id.user_img_iv));

            //id부분 가져오기(사용자에게는 안보이게 = unique값으로 유지)
            user_id_tv_hide = (TextView) hView.findViewById(R.id.user_id_tv_hide);
            user_id_tv_hide.setText(user_info[0]);

            //이름부분 가져오기
            user_name_tv = (TextView) hView.findViewById(R.id.user_name_tv);
            user_name_tv.setText(user_info[1]);

            //이메일부분 가져오기
            user_email_tv = (TextView) hView.findViewById(R.id.user_email_tv);
            user_email_tv.setText(user_info[2]);
        }

        //네비게이션뷰의 메뉴들을 가져온다
        navi_menu = navigationView.getMenu();

        //메뉴의 버튼들 틀릭 시,
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                //실제로 옵션들을 눌렀을 때 ClickListener같은 놈
                int id = menuItem.getItemId();
                switch (id) {

                    //Tracking 버튼 활성화시 현재위치 계속 추적
                    case R.id.GPS_tracking:

                        if (gmap != null && listener != null) {
                            //토글 같은 느낌 켜져있으면 끄고 꺼져있으면 킨다
                            listener.location_button();
                        }

                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    //자판기 Tracking 버튼 활성화시, 주기적으로 가장 가까운 자판기 추적
                    case R.id.Closest_V_machine_tracking:

                        if (gmap != null && listener != null) {
                            listener.closest_vendingmachine_tracking_button();
                            Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        }

                        break;

                    //작업지시서 보기
                    case R.id.order_list:

                        //핸들러 생성
                        Runnable task = new Runnable() {
                            @Override
                            public void run() {

                                //로딩 화면생성
                                handler.sendEmptyMessage(1);
                                try {
                                    //1초 지연
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //로딩화면 제거
                                handler.sendEmptyMessage(2);
                            }
                        };

                        //쓰레드 생성
                        Thread thread = new Thread(task);

                        //쓰레드 실행
                        thread.start();

                        //custom alert 보여주기 클래스 생성(작업지시서) / 함수 실행
                        Order_sheet_alert order_sheet_alert = new Order_sheet_alert(MainActivity.this, user_info[0]);
                        order_sheet_alert.create_table();

                        break;

                    //작업지시서 최신화 하기
                    case R.id.refrash_order_list:

                        //마커 최신화 / 갱신
                        draw_marker();

                        //핸들러 생성
                        task = new Runnable() {
                            @Override
                            public void run() {

                                //로딩 화면생성
                                handler.sendEmptyMessage(1);
                                try {
                                    //2초 지연
                                    Thread.sleep(2000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                //로딩화면 제거
                                handler.sendEmptyMessage(2);
                            }
                        };

                        //쓰레드 생성
                        thread = new Thread(task);

                        //쓰레드 실행
                        thread.start();

                        break;

                    //일본 자판기 보여주기(경로알려주기)
                    case R.id.go_japan:

                        //맵이 로딩되어있지 않다면 보여주지 않는다
                        if (gmap != null) {

                            //GPS추적 버튼이 켜져 있으면 끈다
                            if(navi_menu.findItem(R.id.GPS_tracking).isChecked()) {

                                //토글 같은 느낌 켜져있으면 끄고 꺼져있으면 킨다 ->GPS버튼 끄기
                                listener.location_button();

                                //추적을 실제로 껏을 경우, menu바의 추적 활성화도 비활성화로 바꾼다.
                                navi_menu.findItem(R.id.GPS_tracking).setChecked(false);
                            }

                            //최초 카메라 위치 잡기 -> 자신의 위치가 갱신 되기전(영진전문대) 위치를 기본으로 시작한다.
                            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(33.589862, 130.395222), 16));

                        }
                        break;

                    //클릭시 로그아웃 후 로그인 화면으로 전환
                    case R.id.Logout:

                        //로그아웃 버튼 클릭시,
                        //현재 저장된 정보들을 전부 초기화하며 다시 메인페이지로 돌아간다.
                        share_login_info_obj = new Share_login_info(MainActivity.this);
                        share_login_info_obj.remove_all();

                        //로그인 화면으로 돌아간다
                        Intent intent = new Intent(MainActivity.this, Login_page_Activity.class);
                        startActivity(intent);
                        finish();
                        break;
                }

                //Settings들만 활성화/비활성화 기능 사용
                if (id == R.id.GPS_tracking || id == R.id.Closest_V_machine_tracking) {

                    //체크가 해제되어 있으면 눌러진상태로, 눌러진상태면 해제 시킨다
                    if (menuItem.isChecked() == true) {
                        menuItem.setChecked(false);
                    } else {
                        menuItem.setChecked(true);
                    }

                }

                //나머지는 눌러도 비활성화 되도록 한다.
                else {
                    menuItem.setChecked(false);
                }

                //누르고나서 열어놓은 창을 닫는다
                drawerLayout.closeDrawers();
                return true;
            }
        });



        //자판기(작업지시서) 갱신 버튼 클릭시,
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //마커 최신화 / 갱신
                draw_marker();

                //핸들러 생성
                Runnable task = new Runnable() {
                    @Override
                    public void run() {

                        //로딩 화면생성
                        handler.sendEmptyMessage(1);
                        try {
                            //2초 지연
                            Thread.sleep(2000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        //로딩화면 제거
                        handler.sendEmptyMessage(2);
                    }
                };

                //쓰레드 생성
                Thread thread = new Thread(task);

                //쓰레드 실행
                thread.start();
            }
        });

        //여는 버튼 클릭 시 -> 오른쪽 밑의 레이아웃이 보여진다
        close_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.sc_layout).setVisibility(View.GONE);
                open_button.setVisibility(View.VISIBLE);

            }
        });

        //닫는 버튼 클릭 시, 오른쪽 밑의 레이아웃이 숨겨진다
        open_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.sc_layout).setVisibility(View.VISIBLE);
                open_button.setVisibility(View.GONE);
            }
        });

        //구글 맵 그리기 OnMapReadyCallBack -> 정상 호출시, onMapReady함수 호출
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this); //최초 한번만 그리기
    }

    //최초 네트워크 확인 함수
    public boolean connect_check() {

        //현재 사용중인 네트워크를 가져온다.
        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();

        //네트워크에 연결 되어있는지 확인
        if (activityNetwork != null) {

            //wifi로 연결 중 일시,
            if (activityNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                //Toast.makeText(this, "You are now connected to wifi", Toast.LENGTH_SHORT).show();
            }

            //데이터(LTE/3G)로 연결 중일시
            else if (activityNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                // Toast.makeText(this, "You are now connected to data(LTE/3G)", Toast.LENGTH_SHORT).show();
            }

        }

        //네트워크가 연결 되어있지 않다면
        else {
            //비연결시 어플 다음작업 실행x -> 종료
            Toast.makeText(this, "Please connect with wifi or data(LTE/3G)", Toast.LENGTH_SHORT).show();
            return false;
        }

        //GPS 체크후 비활성화시 권한 요청
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED)) {

            //GPS가 꺼져있을경우
            Toast.makeText(this, "GPS is off, please check your GPS!!", Toast.LENGTH_SHORT).show();

            //GPS켜는 권한 사용자에게 요청
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1001);

        }

        //네트워크가 모든 조건을 만족하고 연결되어 있다면 true를 반환
        return true;
    }

    //OnMapReadyCallback 오버라이딩 // 구글 맵 로딩하기
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        //기존 맵 객체 가져오기
        gmap = googleMap;

        //오른쪽위에 나오는 gps버튼,나침반 제거
        gmap.getUiSettings().setMyLocationButtonEnabled(false);
        gmap.getUiSettings().setCompassEnabled(false);

        //Get_set_package 클래스 생성
        get_set_package = new Get_set_package(this, googleMap, originMarkerlist);

        //마커클릭 이벤트 클래스 생성
        mark_click_event = new Mark_click_event(this, googleMap, originMarkerlist, user_info[0], handler);

        //매초 혹은 미터 마다 갱신될 class 생성
        listener = new Locationlistener(this, originMarkerlist, gmap, sc_lv, my_status,next_vending, get_set_package, navi_menu);

        //마커 최신화 / 갱신
        draw_marker();

        //GPS가 켜져있다면 이 함수 실행
        initLocationManager();

        try {
            //내위치계층을 활성화 시킨다.
            gmap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Toast.makeText(this, "not found my location.", Toast.LENGTH_SHORT).show();
        }

        //최초 카메라 위치 잡기 -> 자신의 위치가 갱신 되기전(영진전문대) 위치를 기본으로 시작한다.
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(35.8963510, 128.6219001), 17));

    }

    //마커 최신화 / 갱신
    public void draw_marker(){

        //기존 마커들 지우기
        if(gmap != null) {
            gmap.clear();
        }

        //기존 마커들 저장소 지우기
        if(get_set_package.getOriginMarkerlist() != null || get_set_package.getOriginMarkerlist().size() != 0) {
            get_set_package.getOriginMarkerlist().clear();
        }

        //다음 가야할 위치 가져오기
        Marker marker = get_set_package.getNow_Marker();

        //만약 마커가 없을경우 넘어간다 //있을경우 제거한다
        if(marker != null){
            marker.remove();
        }

        //오른쪽 밑 레이아웃 지우기
        findViewById(R.id.sc_layout).setVisibility(View.GONE);

        //다음가야할 길이 레이아웃 숨기기
        findViewById(R.id.next_vending_layout).setVisibility(View.GONE);

        //길찾기 함수 호출(일본에서 경로표시)
        new Directions_Functions(gmap, get_set_package);

        //리스너가 있다면
        if(listener != null) {
            //다음 가야할 자판기의 정보 초기화
            listener.setBefore_snippet("");

            //가장 가까운 자판기 정보 초기화
            listener.setClosestMarker();

            //이전의 자신의 위치 조기화
            listener.setLastlocation();
        }

        try {
            //db접속 try/catch 필수
            db_conn_obj = new DB_conn(this,get_set_package);

            //DB에 저장되어있는 마커들을 불러온다 ->user의 login_id를 기준으로
            db_conn_obj.execute("get_markers", user_info[0]);


        } catch (Exception e) {
            Log.e(">>>>>>>>>>>>>>>>>>>>>>>", e.toString());

        }
    }

    //GPS가 켜져 있을 때, 최초 한번 실행
    private void initLocationManager() {
        LocationManager locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        try {
            //업데이트시 x초마다 실행! + GPS를 이용할껀지 네트웤을 이용할껀지 자동 선택
            //3.26 수정 / NetworkProvider을 받아야 실내에서도 시연 가능 하기때문에 이것만으로 바꿈
            //Update함수는 SetInterval같은 존재, 조건마다 계속 onLocationChanged 함수를 불러옴

            //실외에 사용시 if문 주석 제거후 사용하면 GPS_PROVIDER를 인식하여 조금더 정확하게 사용 가능하다!
            //  if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) == true) {
            //만들어놓은 locationlistener를 바탕으로 실행 // 프로바이터설정/최소시간/최소거리/로케이션 리스너
            //    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
            // } else {

            //시연 할때는 내부에서 함으로 NETWORK_PROVIDER를 이용한다
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
            //  }

        } catch (SecurityException e) {
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }

    //상단바의 Option클릭 시
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (id) {
            //왼쪽 상단에 버튼을 클릭시(사용자 지정 id 아님) -> 내장되어있는 버튼(드래그시 발생하는 이벤트)
            case android.R.id.home:
                //Drawlayout을 왼쪽으로 오른쪽으로 여는 행위
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    //뒤로가기 두번 클릭시 나가지는 이벤트
    @Override
    public void onBackPressed() {
        sec_time = System.currentTimeMillis();
        if (sec_time - fir_time < 2000) {
            super.onBackPressed();
            finishAffinity();
        }
        Toast.makeText(this, "한번더 뒤로가기 클릭 시 종료", Toast.LENGTH_SHORT).show();
        fir_time = System.currentTimeMillis();
    }
}

