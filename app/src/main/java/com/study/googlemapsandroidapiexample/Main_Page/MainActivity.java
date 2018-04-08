package com.study.googlemapsandroidapiexample.Main_Page;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
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

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.study.googlemapsandroidapiexample.Main_Page.AlertDialog.AlertDialog_Custom_dialog;
import com.study.googlemapsandroidapiexample.Main_Page.AlertDialog.AlertDialog_list_item;
import com.study.googlemapsandroidapiexample.R;
import com.study.googlemapsandroidapiexample.Login_Page.*;
import com.study.googlemapsandroidapiexample.db_conn;
import com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view.*;

import org.json.JSONArray;
import org.json.JSONObject;

//공통 get set함수를 모아놓은 클래스
class get_set_package {
    private Context             context;            //MainActivity this
    private GoogleMap           googleMap;          //구글맵 객체
    private ArrayList<Marker>   originMarkerlist;   //구글맵에 그려진 마커들이 저장된 배열
    private Marker              next_Marker;         //다음 가야할 위치의 마커

    //생성자
    public get_set_package(Context context, GoogleMap googleMap, ArrayList<Marker> originMarkerlist) {
        this.context            = context;
        this.googleMap          = googleMap;
        this.originMarkerlist   = originMarkerlist;
    }

    //현재 그려진 모든 마커들 가져오기
    public ArrayList<Marker> getOriginMarkerlist() {
        return originMarkerlist;
    }

    //다음 가야할 마커 가져오기
    public Marker getNow_Marker() {return next_Marker;}

    //마커 그리기
    public void drawMarkers(LatLng latLng, String vd_name, String vending_info, Integer status, boolean draggable) {
        //최초 마커를 생성하는 경우 처음 그려주는 행위

        MarkerOptions markerOptions = new MarkerOptions();  //마커 옵션들을 설정할 수있게 해주는 함수 호출\
        markerOptions.position(latLng);                     //마커의 현재 위도와 경도
        markerOptions.title(vd_name);                       //제목(위치의 주소)
        markerOptions.snippet(vending_info);                //내용
        markerOptions.draggable(draggable);                 //드래그 허용

        //1=매진임박 2=매진 3=라인변경
        //resizeMapIcons함수를 사용하여 각각의 다른 사이즈의 사진이 들어와도
        //통일되게 사이즈를 재설정하여 아이콘을 만든다.
        if (status == 1) {
            //resize함수로 사이즈를 통일한다
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("japangi", 50, 60)));
        } else if (status == 2) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("japangi2", 50, 60)));
        } else if (status == 3) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("japangi3", 50, 60)));
        } else if (status == 0) {
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("now", 50, 60)));
        } else {//없을 경우(예외처리)
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(resizeMapIcons("x2", 50, 60)));
        }

        //다음 가야할 자판기를 그려 줄때 호출되는 함수
        if (status == 0) {

            //다음 가야할 자판기가 이미 그려져 있는 경우 맵에서 지운다.
            if (next_Marker != null) {
                next_Marker.remove();
            }

            //다음 가야할 자판기를 다시 맵에 그린다.
            next_Marker = googleMap.addMarker(markerOptions);
        }

        //이외에는 자판기들이 추가 된다!
        else {
            //리스트의 경우 0의자리는 자신의 위치를 나타내므로 더미로 초기화를 시켜준다
            if (originMarkerlist.size() == 0) {
                originMarkerlist.add(googleMap.addMarker(markerOptions));
            }

            // 구글맵에 마커 생성 + 마커배열 추가
            originMarkerlist.add(googleMap.addMarker(markerOptions));
        }
    }

    //아이콘들의 사이즈 설정
    public Bitmap resizeMapIcons(String iconName, int width, int height) {
        Bitmap imageBitmap      = BitmapFactory.decodeResource(context.getResources(), context.getResources().getIdentifier(iconName, "drawable", context.getPackageName()));
        Bitmap resizedBitmap    = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    //위치 이름 반환
    public String getAddress(LatLng latLng) {
        List<Address> list = null;
        try {
            //Geocoder로 지명을 가져오기위해 클래스를 가져온다
            Geocoder geocoder = new Geocoder(context);

            //주소 리스트 객체를 가져온다
            list = geocoder.getFromLocation(
                    latLng.latitude,
                    latLng.longitude,
                    10);

        } catch (IOException e) {
            e.printStackTrace();
        }

        //현재 위치의 지명이 저장되어 있는경우 그 지명을 반환해준다
        if (list != null) {
            //배열이 반환되는데 그 중에 그 위치의 지명 만을 반환
            return list.get(0).getAddressLine(0).toString();
        }

        //지명이 없는경우 지명이 없다는 것을 반환해준다.
        return "not found location name";
    }

    //마커옵션 객체 생성후 반환
    public MarkerOptions getMarkerOption(LatLng latLng, String addr, boolean draggable) {

        //마커 옵션 클래스 생성
        MarkerOptions markerOptions = new MarkerOptions();

        //위치 확인
        markerOptions.position(latLng);

        markerOptions.title(addr);               //제목(위치의 주소)
        markerOptions.snippet("[" + latLng.latitude + ":" + latLng.longitude + "]");//내용
        markerOptions.draggable(draggable);      //드래그 허용

        return markerOptions;
    }

    //현재 위치와 다음 위치의 직선거리(meter단위) 구하기
    static double getmeter(LatLng now_location, LatLng last_location) {
        int R = 6371000; //지구는 둥글구나아~

        //현제 위치와 다음자판기의 위도와 경도의 차
        double dLon = toRad(now_location.longitude - last_location.longitude);
        double dLat = toRad(now_location.latitude - last_location.latitude);

        double lat1 = toRad(now_location.latitude);
        double lat2 = toRad(last_location.latitude);

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
class locationlistener implements LocationListener, GoogleMap.OnMapLongClickListener {
    private GoogleMap       gmap;                       //구글 지도
    private TextView        my_status, next_vending;    //내 정보(왼쪽 위에 표시되는 tv), 다음 자판기까지의 거리
    private Context         context;                    //MainActivity this
    private get_set_package get_set_package;            //내가 만든 getset패키지 함수!
    private Marker          closestMarker;              //가장 가까운 마커(다음 가야할 자판기)
    private Menu            navi_menu;                  //drawerlayout의 menu
    private ListView        sc_lv;                      //오른쪽 밑에 표시되는 제품의 listview

    private ArrayList<Marker> originMarkerlist;         //현재 표시되어있는 자판기들 배열

    private String before_snippet = "";                 //db접속을 최소화 하기위한 String

    private Location lastlocation;                      //최초 한번만 update되는 함수가 호출되어
                                                        //생기는 에러를 방지하기위한 변수

    //-----한번만 실행되게 하기위한 Boolean 변수들-----
    //GPS추적 버튼 클릭시
    private boolean location_button = false;

    //가장 가까운 자판기 추적 버튼 클릭 시
    private boolean closest_vendingmachine_tracking_button = false;
    //-----------------------------------------------

    //생성자
    public locationlistener(Context context, ArrayList<Marker> originMarkerlist, GoogleMap gmap, ListView sc_lv, TextView my_status, TextView next_vending, get_set_package get_set_package, Menu navi_menu) {
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

    //프로그래머가 지정한 일정 시간 or 간 meter만큼 반복해서 실행되는 함수
    @Override
    public void onLocationChanged(Location location) {

        //위도 경도 고도
        Double lattitude = location.getLatitude();
        Double longitude = location.getLongitude();
        Double altitude = location.getAltitude();

        //status에 저장될 msg
        String msg = "";

        //현재 지명 가져오기
        String addr = get_set_package.getAddress(new LatLng(lattitude, longitude));

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

            //다음 가야할 마커 그리기
            get_set_package.drawMarkers(closestMarker.getPosition(), closestMarker.getTitle(), closestMarker.getSnippet(), 0, false);

            //다음 가야할 장소에대해 execute로 DB정보를 가져온다
            //매Update마다 가져오는건 부담이 크기때문에 계속 동일한 장소를 가리킬시, 한번만 가져오도록한다
            if (!before_snippet.equals(closestMarker.getSnippet())) {
                try {
                    //db 접속(try/catch 필수)
                    db_conn db_conn_obj = new db_conn(context);

                    //db에 접속하여 반환된 결과값 초기호
                    String result_str   = db_conn_obj.execute("get_vending_info", closestMarker.getSnippet()).get();

                    //에러상황들 예외처리
                    if (result_str.equals("no_vending")) {
                        Toast.makeText(context, "no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending1")) {
                        Toast.makeText(context, "1no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending2")) {
                        Toast.makeText(context, "2no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending3")) {
                        Toast.makeText(context, "3no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending4")) {
                        Toast.makeText(context, "4no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //값들을 제대로 받아 왔을 시,
                    else {
                        //json 객체로 변환하여 json배열에 저장
                        JSONObject jsonObject           = new JSONObject(result_str);

                        //오른쪽 밑에 보여주는 listview를 custom하여 보여준다
                        Sc_custom_listview sc_custom    = new Sc_custom_listview(context, jsonObject, sc_lv);

                        //custom Listview 만들기!!
                        sc_custom.change_listview();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //현재 가져온 정보를 저장한다
                before_snippet = closestMarker.getSnippet();

                //보충완료,닫기버튼을 보이게 한다.(강제형변환으로 액티비티를 접근한다. ->이게 최선이에요 ㅠㅠ)
                //순서대로 : 열기버튼,sortcutlayout,확인버튼,닫기버튼
                if((((Activity) context).findViewById(R.id.open_button)).getVisibility() == View.GONE) {
                    (((Activity) context).findViewById(R.id.sc_layout)).setVisibility(View.VISIBLE);
                    (((Activity) context).findViewById(R.id.ok_button)).setVisibility(View.VISIBLE);
                    (((Activity) context).findViewById(R.id.close_button)).setVisibility(View.VISIBLE);
                }

                //다음가야할 정보 출력공간을 보이게 한다. -> 오른쪽 맨위 layout
                if((((Activity) context).findViewById(R.id.next_vending_layout)).getVisibility() == View.GONE) {
                    (((Activity) context).findViewById(R.id.next_vending_layout)).setVisibility(View.VISIBLE);
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
        if (lastlocation != null) {

            //이전미터와 현재미터의 거리를 가져오기
            double d = get_set_package.getmeter(new LatLng(location.getLatitude(), location.getLongitude()), new LatLng(lastlocation.getLatitude(), lastlocation.getLongitude()));

            //현재 시간과 이전시간의 시간 차 가져오기
            double resultTime = location.getTime() - lastlocation.getTime();

            double kmPerHour = (d / resultTime) * 3600; //km/s -> km/h 변환 과정(1초->1시 = 3600)

            //현재 날짜 구하기
            long nowSecond = location.getTime();
            Date date = new Date(nowSecond);

            //데이터 포멧 설정
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년MM월dd일HH시mm분ss초");
            String nowDate = sdf.format(date);

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

            //추가된 마커가 하나이상 있을경우 실행
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
                    db_conn db_conn_obj = new db_conn(context);

                    //서버의 결과값을 받아온다
                    String result_str = db_conn_obj.execute("get_vending_info", originMarkerlist.get(i).getSnippet()).get();

                    //에러 예외처리
                    if (result_str.equals("no_vending")) {
                        Toast.makeText(context, "no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending1")) {
                        Toast.makeText(context, "1no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending2")) {
                        Toast.makeText(context, "2no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending3")) {
                        Toast.makeText(context, "3no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    } else if (result_str.equals("no_vending4")) {
                        Toast.makeText(context, "4no vending machine", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //값을 제대로 받아 왔을 시,
                    else {
                        //json 객체로 변환하여 json배열에 저장
                        JSONObject jsonObject = new JSONObject(result_str);

                        //jsonObj를 들고 그대로 customlistview를 만들러 간다
                        //오른쪽 밑의 shortcut을 롱클릭 할때마다 바뀌게 하기 위함
                        Sc_custom_listview sc_custom = new Sc_custom_listview(context, jsonObject, sc_lv);
                        sc_custom.change_listview();

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

                    }
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
class mark_click_event implements GoogleMap.OnMarkerClickListener {
    private Context             context;            //MainActivity this
    private db_conn             db_conn_obj;        //db접속 변수
    private ArrayList<Marker>   originMarkerlist;   //마커들이 저장되어있는 배열

    //생성자
    public mark_click_event(Context context, GoogleMap googleMap, ArrayList<Marker> originMarkerlist) {
        this.context            = context;
        this.originMarkerlist   = originMarkerlist;

        //마커 클릭을 활성화 시킨다
        googleMap.setOnMarkerClickListener(this);
    }

    //OnMarkerClickListener 오버라이딩
    //마커를 클릭했을시 이벤트 -> custom_alertDialog를 만들어서 띄워준다
    @Override
    public boolean onMarkerClick(Marker marker) {
        //DB에 저장되어있는 마커들을 불러온다 ->user의 login_id를 기준으로
        try {
            //db접속 try/catch 필수
            db_conn_obj = new db_conn(context);

            //서버의 결과값을 받아와 저장 한다
            String result_str = db_conn_obj.execute("get_vending_info", marker.getSnippet()).get();

            //에러 예외처리
            if (result_str.equals("no_vending")) {
                Toast.makeText(context, "no vending machine", Toast.LENGTH_SHORT).show();
                return true;
            } else if (result_str.equals("no_vending1")) {
                Toast.makeText(context, "1no vending machine", Toast.LENGTH_SHORT).show();
                return true;
            } else if (result_str.equals("no_vending2")) {
                Toast.makeText(context, "2no vending machine", Toast.LENGTH_SHORT).show();
                return true;
            } else if (result_str.equals("no_vending3")) {
                Toast.makeText(context, "3no vending machine", Toast.LENGTH_SHORT).show();
                return true;
            } else if (result_str.equals("no_vending4")) {
                Toast.makeText(context, "4no vending machine", Toast.LENGTH_SHORT).show();
                return true;
            }

            //값이 올바르게 들어왔을 경우
            else {
                //특정 자판기의 아이템들을 담을 배열 선언
                ArrayList<AlertDialog_list_item> list_itemArrayList = new ArrayList<AlertDialog_list_item>();

                //json 객체로 변환하여 json배열에 저장
                JSONObject jsonObject = new JSONObject(result_str);

                //실제 반복문을 도는 알맹이를 배열로 가져온다
                JSONArray json_result = jsonObject.getJSONArray("result");

                //검색된 배열을 순차적으로 돈다
                for (int i = 0; i < json_result.length(); i++) {

                    //[0]=vd_id [1]=vd_name [2]z=drink_name [3]=drink_path [4]=drink_stook [5]=drink_line [6]=note
                    JSONObject json_obj = json_result.getJSONObject(i);
                    //인자값 : 제품명,이미지경로,제품수량,제품라인

                    //작업 지시가 없는 자판기의 경우 -> 맨 첫 줄은 공백으로 놔둔다
                    if(json_obj.getString("note") == null || json_obj.getString("note").equals("null")){
                        list_itemArrayList.add(new AlertDialog_list_item(" ",json_obj.getString("drink_name"), json_obj.getString("drink_path"), json_obj.getInt("drink_stook"), json_obj.getInt("drink_line")));
                    }

                    //작업 지시서가 있는 자판기의 경우 -> 맨 첫 줄은 작업지시 내용을 출력하게 한다.
                    else {
                        list_itemArrayList.add(new AlertDialog_list_item(json_obj.getString("note"), json_obj.getString("drink_name"), json_obj.getString("drink_path"), json_obj.getInt("drink_stook"), json_obj.getInt("drink_line")));
                    }
                }

                //custom_dialog를 만들어서 보여준다
                AlertDialog_Custom_dialog custom_dialog = new AlertDialog_Custom_dialog(context, list_itemArrayList, marker.getTitle(),json_result.getJSONObject(0).getString("vd_id"));
                custom_dialog.callFunction();
            }

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
    private get_set_package     get_set_package;
    private mark_click_event    mark_click_event;
    private db_conn             db_conn_obj;
    private Menu                navi_menu;
    private GoogleMap           gmap;

    //--------------------------------------------변수--------------------------------------------
    private String[]            user_info;
    private long                fir_time, sec_time;
    private Boolean             drawer_check = true;
    private ArrayList<Marker>   originMarkerlist = new ArrayList<Marker>();

    //--------------------------------------------이외--------------------------------------------
    private locationlistener    listener;
    private Share_login_info    share_login_info_obj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        Window win = getWindow(); //activity의 context정보 변수에 담기
        win.setContentView(R.layout.main_page_activity_main); // 첫번째 layout을 추가한다.

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
            //현재 상태바를 보이지않게 한다.
            @Override
            public void onDrawerSlide(View drawerView, float slideOffset) {
                if (drawer_check) {
                    my_status.setVisibility(View.GONE);
                    drawer_check = false;
                }
            }

            @Override
            public void onDrawerOpened(View drawerView) {
            }

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

        //저장 소들 id가져오기!!!!!!
        my_status       =   (TextView)    findViewById(R.id.my_status);
        next_vending    =   (TextView)    findViewById(R.id.next_vending);
        sc_lv           =   (ListView)    findViewById(R.id.sc_lv);
        ok_button       =   (Button)      findViewById(R.id.ok_button);
        close_button    =   (Button)      findViewById(R.id.close_button);
        open_button     =   (ImageButton) findViewById(R.id.open_button);

        //네비게이션뷰 가져오기(왼쪽에서 오른쪽으로 슬라이드 할시, 나오는 창)
        NavigationView navigationView = (NavigationView) findViewById(R.id.navigation_view);

        //로그인 한 결과값을 가져온다(사용자 정보)
        Intent data = getIntent();

        if (data.getStringExtra("user_info") != null) {

            String str = data.getStringExtra("user_info");
            //[0]=login_id [1]=name [2]=email [3]=imgsrc
            user_info = str.split("/br/");

            //네비게이션의 헤더부분 가져오기
            View hView = navigationView.getHeaderView(0);

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

                        //custom alert 보여주기 클래스 생성(작업지시서) / 함수 실행
                        Order_sheet_alert order_sheet_alert = new Order_sheet_alert(MainActivity.this, user_info[0]);
                        order_sheet_alert.create_table();

                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
                        break;

                    //작업지시서 최신화 하기
                    case R.id.refrash_order_list:

                        //기존 마커 지우기
                        get_set_package.getOriginMarkerlist().clear();


                        //다음 가야할 위치 제거 하는 부분
                        Marker marker = get_set_package.getNow_Marker();
                        //만약 마커가 없을경우 넘어간다
                        if(marker != null){
                            marker.remove();
                        }

                        //오른쪽 밑 레이아웃 지우기
                        findViewById(R.id.sc_layout).setVisibility(View.GONE);

                        //마커 그리기
                        draw_marker();

                        //다음가야할 길이 레이아웃 숨기기
                        findViewById(R.id.next_vending_layout).setVisibility(View.GONE);

                        //다음 가야할 자판기 초기화
                        listener.setBefore_snippet("");

                        Toast.makeText(MainActivity.this, menuItem.getTitle(), Toast.LENGTH_LONG).show();
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



        //자판기 갱신 버튼 클릭시,
        ok_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //기존 마커 지우기
                get_set_package.getOriginMarkerlist().clear();

                //마커 그리기
                draw_marker();

                //다음 가야할 위치 제거
                get_set_package.getNow_Marker().remove();

                //오른쪽 밑의 레이아웃 안보이게 하기
                findViewById(R.id.sc_layout).setVisibility(View.GONE);

                //다음가야할 길이 레이아웃 숨기기
                findViewById(R.id.next_vending_layout).setVisibility(View.GONE);

                //이전 자판기에대한 정보 초기화
                listener.setBefore_snippet("");

                Toast.makeText(MainActivity.this, "갱신완료!", Toast.LENGTH_SHORT).show();
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

        //get_set_package 클래스 생성
        get_set_package = new get_set_package(this, googleMap, originMarkerlist);

        //길찾기 함수 호출(일본에서 경로표시)
        new Directions_Functions(gmap, get_set_package);
        draw_marker();

        //마커클릭 이벤트 클래스 생성
        mark_click_event = new mark_click_event(this, googleMap, originMarkerlist);

        //매초 혹은 미터 마다 갱신될 class 생성
        listener = new locationlistener(this, originMarkerlist, gmap, sc_lv, my_status,next_vending, get_set_package, navi_menu);

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

    //마커 그리기 -> 자판기 갱신 시
    private void draw_marker(){
        try {
            //db접속 try/catch 필수
            db_conn_obj = new db_conn(this);

            //DB에 저장되어있는 마커들을 불러온다 ->user의 login_id를 기준으로
            String result_str = db_conn_obj.execute("get_markers", user_info[0]).get();

            //받아온 값이 없거나 mysql구문의 에러의 경우 아무것도 실행하지 않고 다음으로 넘어간다
            if (result_str.equals("no_marker") || result_str.equals("mysql_err")) {
                Toast.makeText(this, "no marker or mysql_err", Toast.LENGTH_SHORT).show();
            }

            //받아온 값이 JSON객체로 있을 경우
            else {
                //json 객체로 변환하여 json배열에 저장
                JSONObject jsonObject  = new JSONObject(result_str);
                JSONArray  json_result = jsonObject.getJSONArray("result");

                //검색된 배열을 순차적으로 돈다
                for (int i = 0; i < json_result.length(); i++) {

                    String vending_info = "";

                    //vd_id, vd_name, vd_latitude, vd_longitude, vd_place, vd_supplement, vd_soldout 가 저장 되어 있음
                    JSONObject json_obj = json_result.getJSONObject(i);
                    LatLng     latLng   = new LatLng(json_obj.getDouble("vd_latitude"), json_obj.getDouble("vd_longitude"));

                    //문자열로 저장
                    vending_info += json_obj.getInt("vd_id");//+"/br/";

                    // 필요시 주석 제거후 사용!
                    //vending_info += json_obj.getString("vd_place")+"/br/";
                    //vending_info += json_obj.getString("vd_supplement");

                    //실제로 마커를 구글맵에 그린다
                    get_set_package.drawMarkers(latLng, json_obj.getString("vd_name"), vending_info, json_obj.getInt("vd_soldout"), false);

                }
            }
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
