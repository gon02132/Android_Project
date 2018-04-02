package com.study.googlemapsandroidapiexample.Login_Page;

import android.content.Context;
import android.content.SharedPreferences;

//로그인 유지(세션 같은 역할)
//어플을 지우거나 코딩으로 지우는 코드를 짜지않는이상 휴대폰을 꺼도 유지
public class Share_login_info{

    private SharedPreferences pref;
    private Context context;
    public Share_login_info(Context context){
        this.context = context;
        pref = context.getSharedPreferences("login_info", context.MODE_PRIVATE);
    }
    public String get_login_info(){
        //login_info 라는 파일을 가져온다.
        //그 파일 중 이름이 name인 애의 정보를 문자열로 가져온다.
        return pref.getString("user_info","");
    }

    public void set_login_info(String write){
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("user_info",write);
        editor.commit();
    }

    public void remove_name(){
        SharedPreferences.Editor editor = pref.edit();
        editor.remove("user_info");
        editor.commit();
    }

    public void remove_all(){
        SharedPreferences.Editor editor = pref.edit();
        editor.clear();
        editor.commit();
    }
}
