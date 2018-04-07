package com.study.googlemapsandroidapiexample.Main_Page;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.study.googlemapsandroidapiexample.R;

import org.json.JSONObject;

public class Order_sheet_alert {
    private JSONObject product_info;
    private Context context;

    public Order_sheet_alert(JSONObject product_info, Context context) {
        this.product_info = product_info;
        this.context = context;
    }

    public void create_table() {
        final Dialog dig = new Dialog(context);

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.orderseet_table_view);

        //커스텀 다이얼로그가 보여진다
        dig.show();


    }
}
