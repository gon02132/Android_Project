package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;

//Custom View를 만들어주는 class
public class Sc_adapter extends BaseAdapter{
    private Context                 context;           //activity 가져오기
    private ArrayList<Sc_list_item> sc_list;           //물품 리스트들을 담는 공간\

    private ImageView               line_image;        //제품 이미지 뷰
    private TextView                val;               //제품 수량 텍스트 뷰

    private String                  url;               //서버 주소

    //생성자
    public Sc_adapter(Context context, ArrayList<Sc_list_item> sc_list, String url) {
        this.context = context;
        this.sc_list = sc_list;
        this.url     = url;
    }
    //DB에서 값을 가져오기 때문에 set은 필요 없다!

    //--------------------------------get_Functions-----------------------------------------

    //저장된 배열의 크기(수량)
    @Override
    public int getCount() {
        //return sc_list.size();
        //4개씩 출력하도록 한다(배열갯수를 /4의 올림으로 반환)
        return (int) (Math.ceil(sc_list.size() / 4));
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
            convertView = LayoutInflater.from(context).inflate(R.layout.short_lv_items, null);
        }

        //------------------------첫번째 원소----------------------------
        //예외처리 / 만약 받아올 값이 더이상 없다면 그냥 넘어간다
        if((position*4) < sc_list.size()) {
            //제품 아이콘 이미지
            line_image = (ImageView) convertView.findViewById(R.id.line_image_1);

            //서버에서 이미지 가져오기
            com.squareup.picasso.Picasso.with(context)
                    .load("http://"+url+"/images/drink/" + sc_list.get(position * 4).getLv_product_name() + "_back.png")
                    .into(line_image);

            //제품 수량
            val = (TextView) convertView.findViewById(R.id.val_1);
            val.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정
            val.setText(sc_list.get(position * 4).getLv_product_count());
        }
        //------------------------두번째 원소----------------------------
        //예외처리 / 만약 받아올 값이 더이상 없다면 그냥 넘어간다
        if(((position*4) + 1) < sc_list.size()) {
            //제품 아이콘 이미지
            line_image = (ImageView) convertView.findViewById(R.id.line_image_2);

            //서버에서 이미지 가져오기
            com.squareup.picasso.Picasso.with(context)
                    .load("http://"+url+"/images/drink/" + sc_list.get((position * 4) + 1).getLv_product_name() + "_back.png")
                    .into(line_image);

            //제품 수량
            val = (TextView) convertView.findViewById(R.id.val_2);
            val.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정
            val.setText(sc_list.get((position * 4) + 1).getLv_product_count());
        }
        //------------------------세번째 원소----------------------------
        //예외처리 / 만약 받아올 값이 더이상 없다면 그냥 넘어간다
        if(((position*4) + 2) < sc_list.size()) {
            //제품 아이콘 이미지
            line_image = (ImageView) convertView.findViewById(R.id.line_image_3);

            //서버에서 이미지 가져오기
            com.squareup.picasso.Picasso.with(context)
                    .load("http://"+url+"/images/drink/" + sc_list.get((position * 4) + 2).getLv_product_name() + "_back.png")
                    .into(line_image);

            //제품 수량
            val = (TextView) convertView.findViewById(R.id.val_3);
            val.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정
            val.setText(sc_list.get((position * 4) + 2).getLv_product_count());
        }

        //------------------------네번째 원소----------------------------
        //예외처리 / 만약 받아올 값이 더이상 없다면 그냥 넘어간다
        if(((position*4) + 3) < sc_list.size()) {
            //제품 아이콘 이미지
            line_image = (ImageView) convertView.findViewById(R.id.line_image_4);

            //서버에서 이미지 가져오기
            com.squareup.picasso.Picasso.with(context)
                    .load("http://"+url+"/images/drink/" + sc_list.get((position * 4) + 3).getLv_product_name() + "_back.png")
                    .into(line_image);

            //제품 수량
            val = (TextView) convertView.findViewById(R.id.val_4);
            val.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정
            val.setText(sc_list.get((position * 4) + 3).getLv_product_count());
        }

        //view 반환
        return convertView;
    }
}
