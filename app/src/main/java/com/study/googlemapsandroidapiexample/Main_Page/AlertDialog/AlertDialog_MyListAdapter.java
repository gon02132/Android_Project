package com.study.googlemapsandroidapiexample.Main_Page.AlertDialog;

import android.content.Context;
import android.graphics.Color;
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
    private Context context;                        //activity를 가져온다
    private ArrayList<AlertDialog_list_item> list_item_Arraylist;            //출력 될 arraylist
    private TextView name, count, drink_line, note;  //한 공간마다의 저장소들
    private ImageView drk_img;                        //음료 이미지
    private String img_url;                        //서버 주소

    //생성자
    public AlertDialog_MyListAdapter(Context context, ArrayList<AlertDialog_list_item> list_item_Arraylist) {
        this.context = context;
        this.list_item_Arraylist = list_item_Arraylist;

        //이미지를 가져오기위해 서버의 주소를 가져온다
        img_url = "http://13.125.134.167/";
    }

    //데이터 총 갯수 지정
    @Override
    public int getCount() { //몇개의 아이템을 가지고 있나
        //한번에 2개씩 보여주기 때문에 짝수와 홀수를 구분해서 사용해야 한다.
        //1)그렇기 때문에 최대 사이즈/2 에서 홀수 인경우 더미값으로 +1을 해준다.
        //2)또한 첫번째 라인은 작업지시를 보여주는 라인으로 +1을하여 모든 음료를 보여 줄 수 있게 한다.

        //짝수 일 경우
        if (list_item_Arraylist.size() % 2 == 0) {
            return (int) (Math.ceil(list_item_Arraylist.size() / 2)) + 1;
        }

        //홀수 일 경우
        else {
            return (int) (Math.ceil(list_item_Arraylist.size() / 2) + 1) + 1;
        }
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
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {//for문 같은 역할

        //첫번째 위치에는 작업지시가 있는지 보는 라인이다.
        if (position == 0) {

            //custom화 시킨 view를 보여준다.
            convertView = LayoutInflater.from(context).inflate(R.layout.alertdialog_first_item, null);

            //출력될 저장소를 가져온다
            note = (TextView) convertView.findViewById(R.id.note);

            //작업지시의 내용을 출력한다.
            note.setText(list_item_Arraylist.get(position).getNote());

            //색상은 빨갛게!
            note.setTextColor(Color.parseColor("#566270"));

            //사이즈는 기본보다 크게!
            note.setTextSize(20);

            //view return
            return convertView;
        }

        //---------------------이외의 경우 제품 리스트들을 보여준다.----------------------------------
        else if (convertView.findViewById(R.id.product_count) == null) {
            //custom화 시킨 view를 보여준다.
            convertView = LayoutInflater.from(context).inflate(R.layout.alertdialog_list_item, null);
        }

        //첫번째 위치는 작업지시를 보는 라인이기때문에 position을 -1로해야 첫번째 부터 시작한다.
        int pos = position - 1;

        //안해도되지만 예외처리 -> 원래는 MAX이상 넘어가면 이 함수가 호출되지 않지만
        //홀수에서 MAX가 넘어갈 경우 NULL을 받아오는 경우가 있기 때문에 홀수에서는 예외처리가 필수적이다.
        if (list_item_Arraylist.size() > pos * 2) {
            //수량 저장소
            count = (TextView) convertView.findViewById(R.id.product_count);

            //count에 출력할 문자열 조합
            // (+"/35") 는 각 라인마다 최대로 들어갈수 있는 양으로, 실제로는 이것또한 35가아니라
            //DB에서 받아와 동적으로 바뀌게 해야한다(하지만 35로 고정이기 때문에 나도 고정으로 했다)
            String temp = list_item_Arraylist.get(pos * 2).getCount() + "/35";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(50), 0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게한다, 색상은 기본 색상(회색)으로
            sp.setSpan(new AbsoluteSizeSpan(18), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);

            //각 라인들을 표시해주는 저장소
            drink_line = (TextView) convertView.findViewById(R.id.drink_line);
            drink_line.setText(list_item_Arraylist.get(pos * 2).getDrink_line() + "");

            //지정한 매진임박량 이하가 될 경우(X<9) 텍스트 색상을 빨간색으로 바꾼다
            if (list_item_Arraylist.get(pos * 2).getCount() < 9) {
                drink_line.setTextColor(Color.RED);
            } else {
                drink_line.setTextColor(Color.BLACK);
            }

            //제품 명을 저장하는 공간
            name = (TextView) convertView.findViewById(R.id.product_name);
            name.setText(list_item_Arraylist.get(pos * 2).getName() + "");

            //제품 이미지를 저장하는 공간
            drk_img = (ImageView) convertView.findViewById(R.id.drk_img);

            //Picasso lib를 쓴 이유는
            //서버에서 Image를 직접적으로 가져오는데 기존의 setImageResource는 OOM(OutOfMemory)문제가
            //있기 때문에 그 문제점을 없앤 Picasso를 쓴다 -> 실제로 기존의 것을 쓰면
            //이미지 로딩이 느리게 되거나 아예 안된다.
            Picasso.with(context)
                    .load(img_url + list_item_Arraylist.get(pos * 2).getImg_path())
                    .into(drk_img);
        }

        //--------------------------------홀수 part--------------------------------
        //홀수일경우 null을 받아오는 경우가 있다/ 그렇기 때문에 꼭 예외처리해야함
        //예외:배열에 담긴 갯수보다 현재 출력 하고자하는 갯수가 많은 경우
        if (list_item_Arraylist.size() > (pos * 2) + 1) {

            //=================================나머지는 위와 동일!====================================

            count = (TextView) convertView.findViewById(R.id.product_count2);

            //count에 출력할 문자열 조합
            String temp = list_item_Arraylist.get((pos * 2) + 1).getCount() + "/35";

            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(50), 0, temp.length() - 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게한다
            sp.setSpan(new AbsoluteSizeSpan(18), temp.length() - 3, temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);

            //제품 라인 저장소
            drink_line = (TextView) convertView.findViewById(R.id.drink_line2);
            drink_line.setText(list_item_Arraylist.get((pos * 2) + 1).getDrink_line() + "");

            //매진 임박량 이하로 내려간 제품인 경우 텍스트 색상을 빨갛게!
            if (list_item_Arraylist.get((pos * 2) + 1).getCount() < 9) {
                drink_line.setTextColor(Color.RED);
            } else {
                drink_line.setTextColor(Color.BLACK);
            }

            //제품 이름 저장소
            name = (TextView) convertView.findViewById(R.id.product_name2);
            name.setText(list_item_Arraylist.get((pos * 2) + 1).getName() + "");

            //이미지 저장소
            drk_img = (ImageView) convertView.findViewById(R.id.drk_img2);
            Picasso.with(context)
                    .load(img_url + list_item_Arraylist.get((pos * 2) + 1).getImg_path())
                    .into(drk_img);

        }

        //view 반환
        return convertView;
    }
}
