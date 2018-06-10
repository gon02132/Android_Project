package com.study.googlemapsandroidapiexample.Main_Page.Order_sheet_item_list;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;

public class Osil_Adapter extends BaseAdapter{
    private Context             context;            // 어디에 그릴지 정하는 context
    private ArrayList<String>   product_val_arr;    // 제품 이름들이 들어가는 배열
    private ImageView           drink_icon_iv;      // 제품 아이콘 이미지뷰
    private TextView            drink_name_tv;      // 제품 이름 텍스트뷰


    //생성자
    public Osil_Adapter(Context context, ArrayList<String> product_val_arr) {
        this.context            = context;
        this.product_val_arr    = product_val_arr;
    }

    //저장된 배열의 크기(수량)
    @Override
    public int getCount() {
        return product_val_arr.size();
    }

    //현재 위치의 Obj
    @Override
    public Object getItem(int position) {
        return product_val_arr.get(position);
    }

    //Item 위치
    @Override
    public long getItemId(int position) {
        return position;
    }

    //실제로 view를 만들어서 반환해주는 함수 // 콜백 함수
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        //재사용을 위해 null일때 한번만 view를 보여준다
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.orderseet_table_lv_item, null);
        }

        //제품 아이콘 그리기
        drink_name_tv = (TextView) convertView.findViewById(R.id.drink_name);
        drink_name_tv.setText(product_val_arr.get(position));

        //제품 이름 설정
        drink_icon_iv = (ImageView) convertView.findViewById(R.id.drink_icon);
        //Picasso lib를 쓴 이유는
        //서버에서 Image를 직접적으로 가져오는데 기존의 setImageResource는 OOM(OutOfMemory)문제가
        //있기 때문에 그 문제점을 없앤 Picasso를 쓴다 -> 실제로 기존의 것을 쓰면
        //이미지 로딩이 느리게 되거나 아예 안된다.
        Picasso.with(context)
                .load(R.drawable.japangi2)
                .into(drink_icon_iv);

        //만들어진 뷰를 반환한다
        return convertView;
    }

}
