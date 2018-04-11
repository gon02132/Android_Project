package com.study.googlemapsandroidapiexample.Main_Page;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//공통 get set함수를 모아놓은 클래스
class Get_set_package {
    private Context             context;            //MainActivity this
    private GoogleMap           googleMap;          //구글맵 객체
    private ArrayList<Marker>   originMarkerlist;   //구글맵에 그려진 마커들이 저장된 배열
    private Marker              next_Marker;        //다음 가야할 위치의 마커

    //생성자
    public Get_set_package(Context context, GoogleMap googleMap, ArrayList<Marker> originMarkerlist) {
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