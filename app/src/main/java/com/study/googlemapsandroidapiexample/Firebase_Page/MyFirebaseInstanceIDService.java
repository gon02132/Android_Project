package com.study.googlemapsandroidapiexample.Firebase_Page;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;

import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

//토큰 생성, 순환, 업데이트 처리
public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    //받아올 php 경로 선택 1:aws 2:autoset
    private String   link = "http://52.79.80.213/android_db_conn_source/resigster.php";
   //private String  link = "http://WIFI경로주소!/android_db_conn_source/resigster.php";

    //토큰이 새로 생성될때마다 실행되는 콜백 함수
    //앱을 완전히 지우고 새로 깔아야됨
    @Override
    public void onTokenRefresh() {

        // 햔재 토큰 검색(DB에 토큰 저장됨)
        String token = FirebaseInstanceId.getInstance().getToken();

        //현재 토큰으로 http통신(POST전송) 하는 함수
        sendRegistrationToServer(token);
    }

    //HTTP(POST) 접속 과정 -> 현재 토큰을 DB에 넣는다
    private void sendRegistrationToServer(String token) {
        // Add custom implementation, as needed.

        //OKHTTPCLIENT를 써서 POST전송을 한다
        OkHttpClient client = new OkHttpClient();

        //요청할 객체 생성 -> $_POST['Token'] = token 같은 느낌
        //name과 user_info_id는 생성후 로그인 과정에서 수정이 된다
        RequestBody body = new FormBody.Builder()
                .add("token", token)
                .add("name", "NOT_FOUND")
                .add("user_info_id", "NOT_FOUND")
                .build();

        //link를 가지고 body 객체를 POST로 전송한다
        //body에서 지정한.add로 받은 데이터를 $_POST['XXX']로 호출 가능 하다
        Request request = new Request.Builder()
                .url(link)
                .post(body)
                .build();

        try {
            //실행 -> IOExp 발생가능 try/catch 필수!
            client.newCall(request).execute();

        } catch (IOException e) {
            //에러시 로그를 찍어준다.
            Log.e("<<<<<<<<<<<",e.toString());
        }

    }
}