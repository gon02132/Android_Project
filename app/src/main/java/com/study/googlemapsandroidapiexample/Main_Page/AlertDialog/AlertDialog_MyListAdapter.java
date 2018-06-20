package com.study.googlemapsandroidapiexample.Main_Page.AlertDialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;

public class AlertDialog_MyListAdapter extends BaseAdapter {
    private Context                             context;                        //activity를 가져온다
    private ArrayList<AlertDialog_list_item>    list_item_Arraylist;            //출력 될 arraylist
    private TextView                            name, count, drink_line, note;  //한 공간마다의 저장소들
    private ImageView                           drk_img;                        //음료 이미지
    private String                              img_url;                        //서버 주소

    //생성자
    public AlertDialog_MyListAdapter(Context context, ArrayList<AlertDialog_list_item> list_item_Arraylist) {
        this.context                = context;
        this.list_item_Arraylist    = list_item_Arraylist;

        //이미지를 가져오기위해 서버의 주소를 가져온다
        img_url = "http://52.78.198.67/";
    }

    //데이터 총 갯수 지정
    @Override
    public int getCount() { //몇개의 아이템을 가지고 있나
        //한번에 2개씩 보여주기 때문에 짝수와 홀수를 구분해서 사용해야 한다.
        //1)그렇기 때문에 최대 사이즈/2 에서 홀수 인경우 더미값으로 +1을 해준다.
        //2)또한 첫번째 라인은 작업지시를 보여주는 라인으로 +1을하여 모든 음료를 보여 줄 수 있게 한다.

        //4의 올림으로 갯수를 정한다
        return (int) (Math.ceil(list_item_Arraylist.size() / 4));

    }

    //현재 위치의 객체를 가져온다
    @Override
    public Object getItem(int position) {//현재 어떤 아이템인가
        return list_item_Arraylist.get(position);
    }

    //현재 위치를 가져온다
    @Override
    public long getItemId(int position) {//현재 어떤 포지션인가
        return position;
    }

    //반복하면서 처리하는 구문
    @SuppressLint("ResourceType")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//for문 같은 역할

        //재사용을 하기위한 view를 설정한다/ 만약 view가 없다면 생성하고
        //있다면 이전에 사용하던 view를 재사용한다
        if (convertView == null) {
            //custom화 시킨 view를 보여준다.
            convertView = LayoutInflater.from(context).inflate(R.layout.alertdialog_item, null);

        }

        if((position * 4) < list_item_Arraylist.size()) {
            //수량 저장소
            count = (TextView) convertView.findViewById(R.id.val_1);
            count.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정

            //count에 출력할 문자열 조합
            // (+"/35") 는 각 라인마다 최대로 들어갈수 있는 양으로, 실제로는 이것또한 35가아니라
            //DB에서 받아와 동적으로 바뀌게 해야한다(하지만 35로 고정이기 때문에 나도 고정으로 했다)
            String temp = list_item_Arraylist.get(position * 4).getCount() + "/35";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 글자를 크게 한다
            sp.setSpan(new AbsoluteSizeSpan(65),0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게, 까만색으로 출력
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);

            //각 라인들을 표시해주는 저장소
            drink_line = (TextView) convertView.findViewById(R.id.line_1);
            drink_line.setText("LINE "+list_item_Arraylist.get(position * 4).getDrink_line());

            //지정한 매진임박량 이하가 될 경우(X<9) 텍스트 색상을 빨간색으로 바꾼다
            if (list_item_Arraylist.get(position * 4).getCount() < 9) {
                drink_line.setTextColor(Color.RED);
            } else {
                drink_line.setTextColor(Color.BLACK);
            }

            //제품 이미지를 저장하는 공간
            drk_img = (ImageView) convertView.findViewById(R.id.line_image_1);

            //Picasso lib를 쓴 이유는
            //서버에서 Image를 직접적으로 가져오는데 기존의 setImageResource는 OOM(OutOfMemory)문제가
            //있기 때문에 그 문제점을 없앤 Picasso를 쓴다 -> 실제로 기존의 것을 쓰면
            //이미지 로딩이 느리게 되거나 아예 안된다.

            Picasso.with(context)
                    .load(img_url + "/images/drink/" + list_item_Arraylist.get(position * 4).getName()+"_back.png")
                    .into(drk_img);
        }

        if(((position * 4)+1) < list_item_Arraylist.size()) {
            //수량 저장소
            count = (TextView) convertView.findViewById(R.id.val_2);
            count.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정

            //count에 출력할 문자열 조합
            // (+"/35") 는 각 라인마다 최대로 들어갈수 있는 양으로, 실제로는 이것또한 35가아니라
            //DB에서 받아와 동적으로 바뀌게 해야한다(하지만 35로 고정이기 때문에 나도 고정으로 했다)
            String temp = list_item_Arraylist.get((position * 4)+1).getCount() + "/35";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new AbsoluteSizeSpan(65),0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게, 까만색으로 출력
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);

            //각 라인들을 표시해주는 저장소
            drink_line = (TextView) convertView.findViewById(R.id.line_2);
            drink_line.setText("LINE "+list_item_Arraylist.get((position * 4)+1).getDrink_line());

            //지정한 매진임박량 이하가 될 경우(X<9) 텍스트 색상을 빨간색으로 바꾼다
            if (list_item_Arraylist.get((position * 4)+1).getCount() < 9) {
                drink_line.setTextColor(Color.RED);
            } else {
                drink_line.setTextColor(Color.BLACK);
            }

            //제품 이미지를 저장하는 공간
            drk_img = (ImageView) convertView.findViewById(R.id.line_image_2);

            //Picasso lib를 쓴 이유는
            //서버에서 Image를 직접적으로 가져오는데 기존의 setImageResource는 OOM(OutOfMemory)문제가
            //있기 때문에 그 문제점을 없앤 Picasso를 쓴다 -> 실제로 기존의 것을 쓰면
            //이미지 로딩이 느리게 되거나 아예 안된다.

            Picasso.with(context)
                    .load(img_url + "/images/drink/" + list_item_Arraylist.get((position * 4)+1).getName()+"_back.png")
                    .into(drk_img);
        }

        if(((position * 4)+2) < list_item_Arraylist.size()) {
            //수량 저장소
            count = (TextView) convertView.findViewById(R.id.val_3);
            count.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정

            //count에 출력할 문자열 조합
            // (+"/35") 는 각 라인마다 최대로 들어갈수 있는 양으로, 실제로는 이것또한 35가아니라
            //DB에서 받아와 동적으로 바뀌게 해야한다(하지만 35로 고정이기 때문에 나도 고정으로 했다)
            String temp = list_item_Arraylist.get((position * 4)+2).getCount() + "/35";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new AbsoluteSizeSpan(65),0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게, 까만색으로 출력
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);

            //각 라인들을 표시해주는 저장소
            drink_line = (TextView) convertView.findViewById(R.id.line_3);
            drink_line.setText("LINE "+list_item_Arraylist.get((position * 4)+2).getDrink_line());

            //지정한 매진임박량 이하가 될 경우(X<9) 텍스트 색상을 빨간색으로 바꾼다
            if (list_item_Arraylist.get((position * 4)+2).getCount() < 9) {
                drink_line.setTextColor(Color.RED);
            } else {
                drink_line.setTextColor(Color.BLACK);
            }

            //제품 이미지를 저장하는 공간
            drk_img = (ImageView) convertView.findViewById(R.id.line_image_3);

            //Picasso lib를 쓴 이유는
            //서버에서 Image를 직접적으로 가져오는데 기존의 setImageResource는 OOM(OutOfMemory)문제가
            //있기 때문에 그 문제점을 없앤 Picasso를 쓴다 -> 실제로 기존의 것을 쓰면
            //이미지 로딩이 느리게 되거나 아예 안된다.

            Picasso.with(context)
                    .load(img_url + "/images/drink/" + list_item_Arraylist.get((position * 4)+2).getName()+"_back.png")
                    .into(drk_img);
        }

        if(((position * 4)+3) < list_item_Arraylist.size()) {
            //수량 저장소
            count = (TextView) convertView.findViewById(R.id.val_4);
            count.setTypeface(Typeface.createFromAsset(context.getAssets(), "fonts/Futura Heavy Italic font.ttf"));  //폰트 설정

            //count에 출력할 문자열 조합
            // (+"/35") 는 각 라인마다 최대로 들어갈수 있는 양으로, 실제로는 이것또한 35가아니라
            //DB에서 받아와 동적으로 바뀌게 해야한다(하지만 35로 고정이기 때문에 나도 고정으로 했다)
            String temp = list_item_Arraylist.get((position * 4)+3).getCount() + "/35";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new AbsoluteSizeSpan(65),0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게, 까만색으로 출력
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(25), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);

            //각 라인들을 표시해주는 저장소
            drink_line = (TextView) convertView.findViewById(R.id.line_4);
            drink_line.setText("LINE "+list_item_Arraylist.get((position * 4)+3).getDrink_line());

            //지정한 매진임박량 이하가 될 경우(X<9) 텍스트 색상을 빨간색으로 바꾼다
            if (list_item_Arraylist.get((position * 4)+3).getCount() < 9) {
                drink_line.setTextColor(Color.RED);
            } else {
                drink_line.setTextColor(Color.BLACK);
            }

            //제품 이미지를 저장하는 공간
            drk_img = (ImageView) convertView.findViewById(R.id.line_image_4);

            //Picasso lib를 쓴 이유는
            //서버에서 Image를 직접적으로 가져오는데 기존의 setImageResource는 OOM(OutOfMemory)문제가
            //있기 때문에 그 문제점을 없앤 Picasso를 쓴다 -> 실제로 기존의 것을 쓰면
            //이미지 로딩이 느리게 되거나 아예 안된다.

            Picasso.with(context)
                    .load(img_url + "/images/drink/" + list_item_Arraylist.get((position * 4)+3).getName()+"_back.png")
                    .into(drk_img);
        }



        //view 반환
        return convertView;
    }
}
