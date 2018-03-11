package com.study.googlemapsandroidapiexample;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;



import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


//공통 get set함수를 모아놓은 클래스
class get_set_package{
    private Context context;
    private GoogleMap googleMap;
    ArrayList<Marker> originMarkerlist;

    public get_set_package(Context context, GoogleMap googleMap, ArrayList<Marker> originMarkerlist) {
        this.context = context;
        this.googleMap = googleMap;
        this.originMarkerlist = originMarkerlist;
    }


    //선을 추가하는 경우 // 오버로딩1
    public void drawLine(LatLng latLng){
        //최초 마커를 생성하는 경우 처음 그려주는 행위

        String addr = getAddress(latLng);
        MarkerOptions markerOptions = getMarkerOption(latLng,addr,true);

        // 구글맵에 마커 생성 + 마커배열 추가
        originMarkerlist.add(googleMap.addMarker(markerOptions));
    }


    //위치 이름 반환
    public String getAddress(LatLng latLng){
        List<Address> list = null;
        try {
            Geocoder geocoder = new Geocoder(context);
            list = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    10);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(context, "주소오류", Toast.LENGTH_SHORT).show();
        }
        //배열이 반환되는데 그 중에 그 위치의 지명 만을 반환
        return list.get(0).getAddressLine(0).toString();
    }

    //마커옵션 객체 생성후 반환
    public MarkerOptions getMarkerOption(LatLng latLng, String title, boolean draggable){
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.title(title); //제목
        markerOptions.snippet("[" + latLng.latitude + ":" + latLng.longitude+"]");//내용
        markerOptions.draggable(draggable);//드래그 허용
        //markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN));
        markerOptions.icon(BitmapDescriptorFactory.fromResource(R.drawable.japangi));
        return markerOptions;
    }

    //현재 위치와 이전 위치의 직선거리(meter단위) 구하기
    static double getmeter(LatLng now_location, LatLng last_location){
        int R = 6371000; //지구는 둥글구나아~
        double dLon = toRad(now_location.longitude-last_location.longitude);
        double dLat = toRad(now_location.latitude - last_location.latitude);

        double lat1 =toRad(now_location.latitude);
        double lat2 =toRad(last_location.latitude);

        //거리구하는공식 a b c d 임시 변수 //getspeed()하면 제대로된 값이 안나와서 수작업계산
        //공식을 이해하려고 하면 머리아프니 걍 아~ 이런공식을 써서 속도를 구하는 구나 라고 알고있으면 편함
        //위도,경도의 각각의 차를 제곱하여 더한다음 루뜨 씌우면 직선상의 거리가 나옴
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.sin(dLon / 2) * Math.sin(dLon / 2) * Math.cos(lat1) * Math.cos(lat2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double d = R * c;
        return d;
    }

    //지구는 둥글구나아~
    static double toRad(Double d) {
        return d * Math.PI / 180;
    }

}

//로케이션리스너 생성 클래스
class locationlistener implements LocationListener{
    private ArrayList<Marker> originMarkerlist;
    private GoogleMap gmap;
    private TextView textView;
    private Context context;
    private get_set_package get_set_package;
    private boolean location_button = false;

    private Location lastlocation;

    public locationlistener(Context context,ArrayList<Marker> originMarkerlist, GoogleMap gmap, TextView textView, get_set_package get_set_package) {
        this.originMarkerlist = originMarkerlist;
        this.gmap = gmap;
        this.textView = textView;
        this.context = context;
        this.get_set_package = get_set_package;
    }

    @Override
    public void onLocationChanged(Location location) {
        Button b1,b2;

        //위도 경도 고도
        Double lattitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Double altitude = location.getAltitude();
        String msg = "";
        String addr = get_set_package.getAddress(new LatLng(location.getLatitude(),location.getLongitude()));

        if(originMarkerlist.size()==0){
            //0번째 arraylist에 배열 추가
            //0번째에는 항상 사용자의 위치가 들어간다
            //마커 옵션 함수를 만들고, 반환하여 구글맵에 마커를 그린다.
            originMarkerlist.add(0,gmap.addMarker(get_set_package.getMarkerOption(new LatLng(location.getLatitude(),location.getLongitude()),addr,true)));
        }
        else{
            //get으로 0번째의 마커를 지우고, set으로 새로운 위치에 마커를 생성한다.
            originMarkerlist.get(0).remove();
            originMarkerlist.set(0,gmap.addMarker(get_set_package.getMarkerOption(new LatLng(location.getLatitude(),location.getLongitude()),addr,true)));
            ;
        }

        originMarkerlist.get(0).setVisible(false);

        if(location_button) {
            gmap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()),17));
        }
        //처음 받아오는 결과값으로는 연산이 불가능(이전꺼와 현재꺼 필요)
        //그러므로 최초 1번 받아오는 값은 저장만 하게 한다.
        if (lastlocation != null) {

            //이전미터와 현재미터의 거리를 가져오기
            double d = get_set_package.getmeter(new LatLng(location.getLatitude(),location.getLongitude()), new LatLng(lastlocation.getLatitude(),lastlocation.getLongitude()));
            //현재 시간과 이전시간의 시간 차 가져오기
            double resultTime = location.getTime() - lastlocation.getTime();

            double kmPerHour = (d/resultTime)*3600; //km/s -> km/h 변환 과정(1초->1시 = 3600)
            //현재 날짜 구하기
            long nowSecond = location.getTime();
            Date date = new Date(nowSecond);
            //데이터 포멧 설정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초");
            String nowDate = sdf.format(date);

            //0km/s근접 소수점일 경우 0으로 내림(E-7이딴식으로 나오는거 방지)
            if(kmPerHour<0.1)
                kmPerHour = 0;
            //double형 -> String형 변환 + 소수점 한자리까지만 표현
            String SkmPerHour = String.format("%.1f",kmPerHour);
            //msg ="좌표: [ " + lattitude + ":" + longitude+"]\n"; //현재 위치의 좌표 출력 //주석 제거시 표현
            msg+="주소: "+addr+"\n";
            //msg+="고도: "+String.format("%.1f",altitude)+"m\n"; //고도의 세계표준을 한국표준으로 바꾸는걸 모르겟엉.. 쓸꺼면 걍써...
            msg+="속도: "+SkmPerHour+"km/h\n";
            msg+="시간: "+nowDate+"\n";
            if(originMarkerlist.size() > 1) {
                double next_meter = get_set_package.getmeter(originMarkerlist.get(0).getPosition(),originMarkerlist.get(1).getPosition());
                msg += "다음 위치 까지의 거리" + String.format("%.1f", next_meter) + " m\n";
            }
            //얼마나 신뢰도를 가지는지 보고 싶으면 주석 제거(하지만 딱히 신뢰도가 정확하지 않음)
            //나중에 신뢰도를 사용할 알고리즘이 있다면 이거 쓰지말고
            //직접 알고리즘으로 짜서 예외처리하는 것이 보다 효율적
            //msg+="신뢰도:"+location.getAccuracy()+"//";

            // 갱신되는거 눈으로 확인하려면 주석 제거
            //Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

            //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
            if((((Activity)context).findViewById(R.id.ok_button)).getVisibility() != View.VISIBLE) {
                (((Activity) context).findViewById(R.id.ok_button)).setVisibility(View.VISIBLE);
                (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
            }
        }
        textView.setText(msg);
        lastlocation=location;
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {}

    //GPS 상태 관련 오버라이딩
    @Override
    public void onProviderEnabled(String s) {
        //GPS가 켜져있으면 실행됨!
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

    //지구는 둥글구나아~
    private double toRad(Double d) {
        return d * Math.PI / 180;
    }

    //현재 위치 추적 버튼을 누를시,
    public void location_button() {location_button = (location_button) ? false : true;}

}

//맵 관련해서 일어나는 일을 모아놓은 클래스(맵클릭, 맵 롱클릭, 마커 드래그)
//마커 드래그는 맵에서 끌어다 놓는 일밖에 하지않으므로 맵이벤트 클래스에 넣어놓음
class map_event_package implements GoogleMap.OnMapClickListener,
                                   GoogleMap.OnMapLongClickListener,
                                   GoogleMap.OnMarkerDragListener{
    private Context context;
    private ArrayList<Marker> originMarkerlist;
    private get_set_package get_set_package;
    private GoogleMap googleMap;

    private int dragstart_position = -1;

    public map_event_package(Context context, ArrayList<Marker> originMarkerlist, GoogleMap googleMap, get_set_package get_set_package) {
        this.context = context;
        this.originMarkerlist = originMarkerlist;
        this.googleMap = googleMap;
        this.get_set_package = get_set_package;

        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnMapClickListener(this);
        //googleMap.setOnMapLongClickListener(this);
    }

    @Override
    public void onMapClick(LatLng latLng) {
        //첫번째 배열인덱스는 사용자 현재 위치를 나타내게 하기 위해 if로 제어
        //현재위치가 로딩이 끝나고(업데이트 끝)맵을 클릭시 마커 추가 가능
        if(originMarkerlist.size() > 0) {
            get_set_package.drawLine(latLng);
            //현재 최단거리의 위치에 있는 마커
            double before_meter = get_set_package.getmeter(originMarkerlist.get(0).getPosition(),originMarkerlist.get(1).getPosition());
            //추가한 마커
            double add_meter = get_set_package.getmeter(originMarkerlist.get(0).getPosition(),originMarkerlist.get(originMarkerlist.size()-1).getPosition());

            //추가한 카거가 거리가 더 가까울경우
            //최단거리의 위치에있는 마커와 배열을 바꾼다.
            if(before_meter > add_meter){
                Marker temp = originMarkerlist.get(1);
                originMarkerlist.set(1,originMarkerlist.get(originMarkerlist.size()-1));
                originMarkerlist.set(originMarkerlist.size()-1,temp);
            }
        }
    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    //OnMarkerDragListener 오버라이딩
    @Override
    public void onMarkerDragStart(Marker marker) {
        boolean check = false;

        for(int i = 0; i<originMarkerlist.size(); i++){
            //현제marker list중에 드래그를 하는 marker의 제목과 내용이 일치하는 마커가 있다면 통과
            if((originMarkerlist.get(i).getSnippet().equals(marker.getSnippet())) &&
                    originMarkerlist.get(i).getTitle().equals(marker.getTitle())){
                dragstart_position = i;
                check = true;
                break;
            }
        }
        if(check == false){
            dragstart_position = -1;
        }

    }

    //OnMarkerDragListener 오버라이딩
    @Override
    public void onMarkerDrag(Marker marker) {
        //start에서 일치하는 마커가 있었을 경우에만 작동(없으면 작동하지 않는다.)
        if(dragstart_position == -1) {
            return ;
        }
    }

    //OnMarkerDragListener 오버라이딩
    @Override
    public void onMarkerDragEnd(Marker marker) {
        //드래그 하는동안 바뀌는 부분의 선을 업데이트한다
        //start에서 일치하는 마커가 있었을 경우에만 작동(없으면 작동하지 않는다.)
        if(dragstart_position == -1) {
            return ;
        }

        LatLng latLng = marker.getPosition();
        String addr = get_set_package.getAddress(new LatLng(latLng.latitude, latLng.longitude));
        originMarkerlist.get(dragstart_position).remove();
        originMarkerlist.set(dragstart_position, googleMap.addMarker(get_set_package.getMarkerOption(latLng, addr, true)));

    }
}

//마커를 클릭했을때, 이벤트들을 모아놓은 클래스(마커 클릭, 클릭시 출력되는 view 설정)
class mark_click_event implements GoogleMap.OnMarkerClickListener{
    private Context context;
    public mark_click_event(Context context, GoogleMap googleMap) {
        this.context = context;
        googleMap.setOnMarkerClickListener(this);
    }

    //OnMarkerClickListener 오버라이딩
    @Override
    public boolean onMarkerClick(Marker marker) {
        //마커를 클릭했을시 이벤트
        LatLng latLng = marker.getPosition();
        //Toast.makeText(context, latLng.latitude+":"+latLng.longitude, Toast.LENGTH_SHORT).show();
        //반환형이 false일 경우 기존의 클릭 이벤트도 같이 발생된다.
        return false;
    }
}
//MainActivity
public class MainActivity extends AppCompatActivity implements OnMapReadyCallback,
                                                               GoogleMap.InfoWindowAdapter{
    private GoogleMap gmap;
    private TextView textView, db_text_tv;
    private Button button, ok_button, close_button, logout_bt;
    private get_set_package get_set_package;
    private map_event_package map_event_package;
    private mark_click_event mark_click_event;
    private db_package db_package_obj;

    private ArrayList<Marker> originMarkerlist = new ArrayList<Marker>();


    private locationlistener listener;
    private Share_login_info share_login_info_obj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        Window win = getWindow(); //activity의 context정보 변수에 담기
        win.setContentView(R.layout.activity_main); // 첫번째 layout을 추가한다.

        //inflater 얻어오기
        LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        //xml 설정하기
        ConstraintLayout linear = (ConstraintLayout)inflater.inflate(R.layout.second_activity,null);

        //레이아웃의 폭과 높이 설정
        ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_PARENT,
                ConstraintLayout.LayoutParams.MATCH_PARENT);

        //기존의 레이아웃에 겹쳐서 배치
        win.addContentView(linear, params);

        //-----------------------------레이아웃 셋팅 끝--------------------------------------------
        textView = (TextView)findViewById(R.id.text1);
        db_text_tv = (TextView)findViewById(R.id.db_text);
        button = (Button)findViewById(R.id.button);
        ok_button = (Button)findViewById(R.id.ok_button);
        close_button = (Button)findViewById(R.id.close_button);
        logout_bt = (Button)findViewById(R.id.logout_bt);

        Intent data = getIntent();
        if(data.getStringExtra("username") != null) {
            db_text_tv.setText(data.getStringExtra("username"));
        }
        //버튼 클릭 리스너(토글버튼) -> 자신의 위치(GPS) 추적버튼
        button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(gmap != null){
                    listener.location_button();
                }
            }
        });

        //보충완료 버튼 클릭시,
        ok_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "ok버튼 누름!", Toast.LENGTH_SHORT).show();
            }
        });

        //닫기 버튼 클릭시,
        close_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "close버튼 누름!", Toast.LENGTH_SHORT).show();
            }
        });

        //로그아웃 버튼 클릭 시
        logout_bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //로그아웃 버튼 클릭시,
                //현재 저장된 정보들을 전부 초기화하며 다시 메인페이지로 돌아간다.
                share_login_info_obj = new Share_login_info(MainActivity.this);
                share_login_info_obj.remove_all();
                Intent intent = new Intent(MainActivity.this, Login_page_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        //인터넷 연결, 위치 권환 체크
        //체크 통과시, 다음 작업 진행
        connect_check();
    }

    public void connect_check(){
        //(wifi / LTE/3G) 연결 확인
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo activityNetwork = cm.getActiveNetworkInfo();
        if(activityNetwork != null){
            //wifi로 연결 중 일시,
            if(activityNetwork.getType() == ConnectivityManager.TYPE_WIFI){
                //Toast.makeText(this, "You are now connected to wifi", Toast.LENGTH_SHORT).show();
            }
            //데이터(LTE/3G)로 연결 중일시
            else if(activityNetwork.getType() == ConnectivityManager.TYPE_MOBILE){
                //Toast.makeText(this, "You are now connected to data(LTE/3G)", Toast.LENGTH_SHORT).show();
            }
        }else{
            //비연결시 어플 다음작업 실행x -> 종료
            Toast.makeText(this, "Please connect with wifi or data(LTE/3G)", Toast.LENGTH_SHORT).show();
            return;
        }

        //GPS 체크후 비활성화시 권한 요청
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED)){
            //GPS가 꺼져있을경우
            Toast.makeText(this, "GPS is off, please check your GPS!!", Toast.LENGTH_SHORT).show();
            //GPS켜는 권한 사용자에게 요청
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION},1001);

        }else{
            Toast.makeText(this, "start", Toast.LENGTH_SHORT).show();
            //GPS가 켜져 있는경우 어플 다음작업 실행
            //구글 맵 그리기 OnMapReadyCallBack -> 정상 호출시, onMapReady함수 호출
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(this); //최초 한번만 그리기
        }
    }

    //OnMapReadyCallback 오버라이딩 // 구글 맵 로딩하기
    @Override
    public void onMapReady(final GoogleMap googleMap) {

        gmap=googleMap;
        //디자인 사용자지정 구현
        gmap.setInfoWindowAdapter(this);
        gmap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
        //get_set_package 클래스 생성
        get_set_package = new get_set_package(this,googleMap, originMarkerlist);

        //클릭 이벤트 클래스 생성
        map_event_package = new map_event_package(this,originMarkerlist,googleMap,get_set_package);

        //마커클릭 이벤트 클래스 생성
        mark_click_event = new mark_click_event(this,googleMap);

        //매초 혹은 미터 마다 갱신될 class 생성
        listener = new locationlistener(this, originMarkerlist,gmap,textView, get_set_package);

        initLocationManager(); //GPS가 켜져있다면 이 함수 실행

        try {
            //내위치계층을 활성화 시킨다.
            gmap.setMyLocationEnabled(true);
        }catch (SecurityException e){
            Toast.makeText(this, "not found my location.", Toast.LENGTH_SHORT).show();
        }

        //최초 카메라 위치 잡기
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom( new LatLng(35.8963510, 128.6219001),17));
    }

    //GPS가 켜져 있을 때, 최초 한번 실행
    private void initLocationManager(){
        LocationManager locationManager = (LocationManager)this.getSystemService(Context.LOCATION_SERVICE);
        try {
            //업데이트시 x초마다 실행! + GPS를 이용할껀지 네트웤을 이용할껀지 자동 선택
            //개인적으로 GPS가 NETWORK보다 더 잘 반응함으로 이것을 우선순위로 뒀음
            //Update함수는 SetInterval같은 존재, 조건마다 계속 onLocationChanged 함수를 불러옴
            if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER) == true) {
                //만들어놓은 locationlistener를 바탕으로 실행 // 프로바이터설정/최소시간/최소거리/로케이션 리스너
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 2000, 0, listener);
            } else {
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 2000, 0, listener);
            }
        }catch (SecurityException e){
            Toast.makeText(this, "권한이 필요합니다.", Toast.LENGTH_SHORT).show();
        }
    }


    //InfoWindowAdapter 오버라이딩
    //얘는 마커 클릭시 나오는 틀자체를 새로 만드는거
    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    //InfoWindowAdapter 오버라이딩
    //얘는 마커클릭시 틀은 유지하되 내용물만 새로 만드는거
    @Override
    public View getInfoContents(Marker marker) {
        //마커를 클릭시 나오는 틀(layout)설정
        //view 객체 생성
        View myContentsView = getLayoutInflater().inflate(R.layout.cutsom_contents, null);

        //이미지 설정
        ImageView imageView = (ImageView)myContentsView.findViewById(R.id.imageView);
        imageView.setImageResource(R.drawable.a1);

        //제목 설정
        TextView tvTitle = ((TextView)myContentsView.findViewById(R.id.title));
        tvTitle.setText(marker.getTitle().toString());

        //내용 설정
        TextView tvSnippet = ((TextView)myContentsView.findViewById(R.id.snippet));
        tvSnippet.setText(marker.getSnippet().toString());

        //삭제 버튼 설정
        Button remove_button = ((Button)myContentsView.findViewById(R.id.remove_icon));
        remove_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(MainActivity.this, "버튼누름!", Toast.LENGTH_SHORT).show();
            }
        });
        //remove_button

        return myContentsView;
    }
}

//인자1:doInBackground 2:onProgressUpdate 3:onPostExecute  들의 매개변수 타입결정
//비동기적 쓰레드, 백그라운드 쓰레드와 UI쓰레드(메인 쓰레드)와 같이 쓰기위해 쓰임
class db_package extends AsyncTask<String, Void, String>{
    private BufferedReader bufferedReader = null;
    private String result_String = "";
    private Context context;
    public db_package(Context context){
        this.context = context;
    }

    //doInBackground함수가 실행되기전 호출
    //초기화작업 혹은 로딩등을 하는데 쓰임
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    //excute실행시 background에서 쓰레드가 실행되어 이함수 호출
    //여기선 UI작업 하지 말 것!
    @Override
    protected String doInBackground(String... strings) {
        try {
            //link뒤에 ?X=~~~~로 GET으로 값을 넘겨줄수도 있다.
            //link의 경우, server의 주소를 가져와야 한다
            //받아올 php 경로 선택 1:aws 2:autoset
            //String link = "http://ec2-13-125-220-71.ap-northeast-2.compute.amazonaws.com/test/conn.php";
            String link  = "http://172.25.1.89/test/conn.php";

            URL url = new URL(link);
            HttpURLConnection con = (HttpURLConnection)url.openConnection();
            StringBuilder sb = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String line;
            while((line = bufferedReader.readLine())!=null){
                sb.append(line);
            }
            bufferedReader.close();
            return sb.toString().trim();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    //doInBackground에서 실행되고 반환된 결과를 가지고 이함수가 실행됨
    //여기선 UI작업을 하면됨
    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
    }
}
