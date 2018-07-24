package com.study.googlemapsandroidapiexample.Main_Page.CalendarDialog;

import android.app.Dialog;
import android.content.Context;
import android.view.Window;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.CalendarMode;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.study.googlemapsandroidapiexample.DB_conn;
import com.study.googlemapsandroidapiexample.Main_Page.Order_sheet_item_list.Order_sheet_alert;
import com.study.googlemapsandroidapiexample.R;

import java.util.Calendar;

public class Create_AlertDialog {
    private Context                 context;
    private String                  user_login_id;
    private MaterialCalendarView    materialCalendarView;
    private String                  url;

    //생성자
    public Create_AlertDialog(Context context, String user_login_id, String url) {
        this.context       = context;
        this.user_login_id = user_login_id;
        this.url           = url;
    }

    //생성 함수 호출
    public void callFunction(){
        //Dialog 객체 생성
        final Dialog dig = new Dialog(context);

        //타이틀제거(타이틀의 공간차지 방지)
        dig.requestWindowFeature(Window.FEATURE_NO_TITLE);

        //레이아웃 설정
        dig.setContentView(R.layout.calendar_custom_view);

        //커스텀 다이얼로그가 보여진다
        dig.show();

        //layout에서 달력 가져오기
        materialCalendarView = (MaterialCalendarView)dig.findViewById(R.id.calendarView);


        //보여 주기전에 속성을 지정 한다
        materialCalendarView.addDecorators(
                new SundayDecorator(),              //일요일의 경우 빨간색으로
                new SaturdayDecorator(),            //토요일의 경우 파란색으로
                new OneDayDecorator()               //현재 날짜 구해 오기(색, 폰트 등 조절 하여)
        );

        //실제로 달력을 보여주는곳, 보여지는 속성들을 설정하여 보여준다
        materialCalendarView.state().edit()
                .setFirstDayOfWeek(Calendar.SUNDAY)                             // 해당 주의 시작요일을 지정
                .setMinimumDate(CalendarDay.from(2015, 0, 1))   // 이 이하로는 볼 수 없다
                .setMaximumDate(CalendarDay.from(2030, 11, 31)) // 이 이상으로는 볼 수 없다
                .setCalendarDisplayMode(CalendarMode.MONTHS)                    // 한페이지에 얼만큼 보여줄것인지(달 만큼)
                .commit();                                                      // 보여주기!

        //특정 날을 클릭 했을 경우 이벤트를 발생 시킨다
        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {

            //클릭 했을 때, 콜백함수 실행
            @Override
            public void onDateSelected(MaterialCalendarView widget, CalendarDay date, boolean selected) {
                int year        = date.getYear();             //년
                int month       = date.getMonth()+1;          //월 -> 왠지는몰라도 -1만큼 출력됨.. 그래서 1을 추가해준다
                int day         = date.getDay();              //일
                String result   =  String.valueOf(year);      //년+"-"+월+"-"+일 이 저장될 공간

                //날짜 양식을 맞추기위해 문자열로 반환하여 저장한다
                //"달"이 한 자리 수인 경우 앞에 0을 붙인다
                if(month < 10){
                    result += "-0"+String.valueOf(month);
                }else{
                    result += "-"+String.valueOf(month);
                }

                //"일"이 한 자리 수인 경우 앞에 0을 붙인다
                if(day < 10){
                    result += "-0"+String.valueOf(day);
                }else{
                    result += "-"+String.valueOf(day);
                }
                //custom alert 보여주기 클래스 생성(작업지시서) / 함수 실행
                Order_sheet_alert order_sheet_alert = new Order_sheet_alert(context, user_login_id, result, url);
                order_sheet_alert.create_table(1, 0);

                //현재 열려있는 자판기 정보도 닫는다.
                dig.dismiss();

            }
        });

        //DB객체를 생성한다
        DB_conn db_conn = new DB_conn(context, materialCalendarView, url);
        //현재 접속된 userid로 DB에 날짜를 가져와 달력 작업을 한다
        db_conn.execute("calendar_get_Day",user_login_id);


    }
}
