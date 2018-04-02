package com.study.googlemapsandroidapiexample.Main_Page;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.support.v4.content.IntentCompat;


public class Internet_conn_check extends BroadcastReceiver {
    private Context context;
    private Boolean fir_skip = false;

    //이전에 연결된 애가 없었는 경우에만 앱을 재시작 하게 하려고했는데
    //네트워크가 변경될때 무조건 얘가먼저 호출되서... 의미없게됨 그래도 놔둠
    private Boolean pre_conn = false;

    public Internet_conn_check(Context context) {
        this.context = context;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        //else if로 하면 겹칠경우 확인이 불가능하기때문에 전부 if로 null을 확인
        //이후 이 네트워크가 연결되있는지 확인후 종류에 따라 처리

        //네트워크 변경이 일어난다면 실행
        if(intent.getAction().equals(ConnectivityManager.CONNECTIVITY_ACTION)){

            //wifi
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI) != null){
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).isConnected()){
                    if(fir_skip == true && pre_conn == true) {
                        restartApp(context);
                        return;
                    }
                    pre_conn = false;
                }
            }

            //nework
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE) != null){
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).isConnected()){
                    if(fir_skip == true && pre_conn == true) {
                        restartApp(context);
                        return;
                    }
                    pre_conn = false;
                }
            }

            //wimax
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX) != null){
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIMAX).isConnected()){
                    if(fir_skip == true && pre_conn == true) {
                        restartApp(context);
                        return;
                    }
                    pre_conn = false;
                }
            }

            //nework
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_DUN) != null){
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE_DUN).isConnected()){
                    if(fir_skip == true && pre_conn == true) {
                        restartApp(context);
                        return;
                    }
                    pre_conn = false;
                }
            }

            //ethernet
            if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET) != null){
                if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET).isConnected()){
                    if(fir_skip == true && pre_conn == true) {
                        restartApp(context);
                        return;
                    }
                    pre_conn = false;
                }
            }
        }
        fir_skip = true;
        pre_conn = true;
    }

    //앱 재시작
    public static void restartApp(Context context) {
        PackageManager packageManager = context.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(context.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        context.startActivity(mainIntent);
        System.exit(0);
    }

}
