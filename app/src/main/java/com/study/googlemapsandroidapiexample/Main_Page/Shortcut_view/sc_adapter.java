package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;

//Custom View를 만들어주는 class
public class Sc_adapter extends BaseAdapter{
    private Context                 context;           //activity 가져오기
    private ArrayList<Sc_list_item> sc_list;           //물품 리스트들을 담는 공간
    private TextView                name, line, count; //제품 명, 제품 라인, 제품 수량을 저장하는 공간

    public Sc_adapter(Context context, ArrayList<Sc_list_item> sc_list) {
        this.context = context;
        this.sc_list = sc_list;
    }
    //DB에서 값을 가져오기 때문에 set은 필요 없다!

    //--------------------------------get_Functions-----------------------------------------

    //저장된 배열의 크기(수량)
    @Override
    public int getCount() {
        return sc_list.size();
    }

    //현재 위치의 Obj
    @Override
    public Object getItem(int position) {
        return sc_list.get(position);
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
            convertView = LayoutInflater.from(context).inflate(R.layout.short_lv_item, null);
        }

        //제품 라인
        line = (TextView) convertView.findViewById(R.id.lv_product_line);
        line.setText(" "+sc_list.get(position).getLv_product_line());

        //제품 이름
        name = (TextView) convertView.findViewById(R.id.lv_product_name);
        name.setText("  "+sc_list.get(position).getLv_product_name());

        //제품 수량
        count = (TextView) convertView.findViewById(R.id.lv_product_count);
        count.setText(sc_list.get(position).getLv_product_count());

        //view 반환
        return convertView;
    }
}
