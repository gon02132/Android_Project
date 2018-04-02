package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;


public class sc_list_item {
    private String lv_product_name;
    private String lv_product_line;
    private String lv_product_count;

    public sc_list_item(String lv_product_name, String lv_product_line, String lv_product_count) {
        this.lv_product_name = lv_product_name;
        this.lv_product_line = lv_product_line;
        this.lv_product_count = lv_product_count;
    }

    public String getLv_product_name() {
        return lv_product_name;
    }


    public String getLv_product_line() {
        return lv_product_line;
    }


    public String getLv_product_count() {
        return lv_product_count;
    }

}
