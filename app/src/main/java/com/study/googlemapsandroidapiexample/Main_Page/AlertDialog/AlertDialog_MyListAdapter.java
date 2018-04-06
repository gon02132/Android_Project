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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class AlertDialog_MyListAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<AlertDialog_list_item> list_item_Arraylist;
    private TextView name, count, drink_line, note;
    private ImageView drk_img, drk_img2;
    private String img_url = "http://ec2-13-125-198-224.ap-northeast-2.compute.amazonaws.com/";

    public AlertDialog_MyListAdapter(Context context, ArrayList<AlertDialog_list_item> list_item_Arraylist) {
        this.context = context;
        this.list_item_Arraylist = list_item_Arraylist;
    }

    //데이터 총 갯수 지정
    @Override
    public int getCount() {//몇개의 아이템을 가지고 있나
        //짝수 일 경우
        if (list_item_Arraylist.size() % 2 == 0) {
            return (int) (Math.ceil(list_item_Arraylist.size() / 2))+1;
        }
        //홀수 일 경우
        else {
            return (int) (Math.ceil(list_item_Arraylist.size() / 2) + 1)+1;
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
    public View getView(int position, View convertView, ViewGroup parent) {//반복문
        //첫번째는
        if(position == 0){
            convertView = LayoutInflater.from(context).inflate(R.layout.alertdialog_first_item, null);
            note = (TextView)convertView.findViewById(R.id.note);
            note.setText(list_item_Arraylist.get(position).getNote());
            note.setTextColor(Color.parseColor("#ff0000"));
            note.setTextSize(20);
            return convertView;
        }

        else if(convertView.findViewById(R.id.product_count) == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.alertdialog_list_item, null);
        }

        int pos = position - 1;
        //안해도되지만 예외처리
        if (list_item_Arraylist.size() > pos * 2) {
            count = (TextView) convertView.findViewById(R.id.product_count);

            //count에 출력할 문자열 조합
            String temp = list_item_Arraylist.get(pos * 2).getCount()+"/35";
            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, temp.length()-3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(50),0,temp.length()-3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게한다
            sp.setSpan(new AbsoluteSizeSpan(18),temp.length()-3,temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);
            //count.setText(list_item_Arraylist.get(pos * 2).getCount() + "");

            drink_line = (TextView) convertView.findViewById(R.id.drink_line);
            drink_line.setText(list_item_Arraylist.get(pos * 2).getDrink_line() + "");
            //drink_line.setTextColor(Color.WHITE);
            //drink_line.setBackgroundColor(Color.WHITE);

            if(list_item_Arraylist.get(pos * 2).getCount() < 9){
                drink_line.setTextColor(Color.RED);
                //drink_line.setBackgroundColor(Color.RED);
            }else{
                drink_line.setTextColor(Color.BLACK);
               // drink_line.setBackgroundColor(Color.WHITE);
            }

            name = (TextView) convertView.findViewById(R.id.product_name);
            name.setText(list_item_Arraylist.get(pos * 2).getName() + "");

            drk_img = (ImageView) convertView.findViewById(R.id.drk_img);
            Picasso.with(context)
                    .load(img_url + list_item_Arraylist.get(pos * 2).getImg_path())
                    .into(drk_img);
        }

        //홀수일경우 하나가 빌수도있다/ 그렇기 때문에 꼭 예외처리해야함
        //다음 추가할 내용이 없다면 넘어간다
        if (list_item_Arraylist.size() > (pos * 2) + 1) {
            count = (TextView) convertView.findViewById(R.id.product_count2);

            //count에 출력할 문자열 조합
            String temp = list_item_Arraylist.get((pos * 2) + 1).getCount()+"/35";
            //하나의 textView에 글자색, 크기를 다르게 하는 함수
            SpannableStringBuilder sp = new SpannableStringBuilder(temp);

            //실 보충 수량은 까만색으로 뚜렷하게, 글자 크게
            sp.setSpan(new ForegroundColorSpan(Color.BLACK), 0, temp.length()-3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            sp.setSpan(new AbsoluteSizeSpan(50),0,temp.length()-3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            //최대 보충수량은 글씨크기를 작게한다
            sp.setSpan(new AbsoluteSizeSpan(18),temp.length()-3,temp.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            //뭔가 채워져있으면 비우고 append로 수정한 문자열을 넣는다.
            count.setText("");
            count.append(sp);
            //count.setText(list_item_Arraylist.get((pos * 2)+1).getCount() + "");

            drink_line = (TextView) convertView.findViewById(R.id.drink_line2);
            drink_line.setText(list_item_Arraylist.get((pos * 2)+1).getDrink_line() + "");
            //drink_line.setTextColor(Color.WHITE);
            //drink_line.setBackgroundColor(Color.WHITE);

            if(list_item_Arraylist.get((pos * 2)+1).getCount() < 9){
                drink_line.setTextColor(Color.RED);
                //drink_line.setBackgroundColor(Color.RED);
            }else{
                drink_line.setTextColor(Color.BLACK);
               // drink_line.setBackgroundColor(Color.WHITE);
            }

            name = (TextView) convertView.findViewById(R.id.product_name2);
            name.setText(list_item_Arraylist.get((pos * 2) + 1).getName() + "");

            drk_img2 = (ImageView) convertView.findViewById(R.id.drk_img2);
            Picasso.with(context)
                    .load(img_url + list_item_Arraylist.get((pos * 2) + 1).getImg_path())
                    .into(drk_img2);

        }

        return convertView;
    }
}
