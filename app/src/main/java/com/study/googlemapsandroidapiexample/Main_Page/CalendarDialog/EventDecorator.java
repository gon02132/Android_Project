package com.study.googlemapsandroidapiexample.Main_Page.CalendarDialog;

import android.content.Context;
import android.graphics.drawable.Drawable;

import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.DayViewDecorator;
import com.prolificinteractive.materialcalendarview.DayViewFacade;
import com.prolificinteractive.materialcalendarview.spans.DotSpan;
import com.study.googlemapsandroidapiexample.R;

import java.util.List;

//받아온 날짜들을 꾸며주는 클래스
public class EventDecorator implements DayViewDecorator{
    private int                     color = -99;
    private List<CalendarDay>       dates = null;

    private Drawable drawable;

    //점을 찍고 싶을 경우 사용하는 생성자
    public EventDecorator(Context context, int color, List<CalendarDay> dates) {
        this.color = color;
        this.dates = dates;
    }

    //배경색을 지정하고 싶은 경우 사용하는 생성자
    public  EventDecorator(Context context, List<CalendarDay> dates){
        this.dates = dates;
        drawable   = context.getResources().getDrawable(R.drawable.now);
    }

    @Override
    public boolean shouldDecorate(CalendarDay day) {
        return dates.contains(day);
    }

    @Override
    public void decorate(DayViewFacade view) {
        //배경색 출력
        if(drawable != null) {
            //배경 이미지지정 출력
            view.setBackgroundDrawable(drawable);
        }

        //점 출력
        else if(color != -99) {
            //점 출력
            view.addSpan(new DotSpan(10,color));
        }
    }
}
