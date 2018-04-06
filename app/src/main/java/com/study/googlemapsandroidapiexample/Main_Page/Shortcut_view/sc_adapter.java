package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.study.googlemapsandroidapiexample.R;

import java.util.ArrayList;

public class sc_adapter extends BaseAdapter{
    private Context context;
    private ArrayList<sc_list_item> sc_list;
    private TextView name,line,count;

    public sc_adapter(Context context, ArrayList<sc_list_item> sc_list) {

        this.context = context;
        this.sc_list = sc_list;
    }

    @Override
    public int getCount() {
        return sc_list.size();
    }

    @Override
    public Object getItem(int position) {
        return sc_list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.short_lv_item, null);
        }

        line = (TextView) convertView.findViewById(R.id.lv_product_line);
        line.setText(" "+sc_list.get(position).getLv_product_line());

        name = (TextView) convertView.findViewById(R.id.lv_product_name);
        name.setText("  "+sc_list.get(position).getLv_product_name());

        count = (TextView) convertView.findViewById(R.id.lv_product_count);
        count.setText(sc_list.get(position).getLv_product_count());
        return convertView;
    }
}
