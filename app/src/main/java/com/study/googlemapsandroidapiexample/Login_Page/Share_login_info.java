package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Context;
import android.content.SharedPreferences;

//로그인 유지(세션 같은 역할)
//어플을 지우거나 코딩으로 지우는 코드를 짜지않는이상 휴대폰을 꺼도 유지
public class Share_login_info{

    private SharedPreferences pref;    //실제로 저장될 변수
    private Context           context; //mainactivity에서 this를 받아온다.

    //생성자
    public Share_login_info(Context context){
        this.context = context;

        //login_info라는 저장소를 만든다.
        pref = context.getSharedPreferences("login_info", context.MODE_PRIVATE);
    }

    //로그인 정보 get
    public String get_login_info(){
        //login_info 라는 파일을 가져온다.
        //그 파일 중 이름이 name인 애의 정보를 문자열로 가져온다.
        return pref.getString("user_info","");
    }

    //로그인 정보 set
    public void set_login_info(String write){
        //login_info라는 저장소에 user_info라는 파일에 write문자열을 넣어 저장한다.
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_info",write);
        editor.commit();
    }

    //특정 파일 삭제
    // 추후 필요 할시 사용 할 수 있기 때문에 남겨놓는다.
    public void remove_name(){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("user_info");
        editor.commit();
    }

    //모든 파일 삭제
    public void remove_all(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
