package com.study.googlemapsandroidapiexample.Main_Page.Shortcut_view;


//custom list_view에 저장되는 item들
public class Sc_list_item {
    private String lv_product_name;  //제품 명
    private String lv_product_line;  //제품 라인
    private String lv_product_count; //제품 수량

    //생성자
    public Sc_list_item(String lv_product_name, String lv_product_line, String lv_product_count) {
        this.lv_product_name    = lv_product_name;
        this.lv_product_line    = lv_product_line;
        this.lv_product_count   = lv_product_count;
    }

    //----------------------------getFunction-----------------------------------
    public String getLv_product_name() {
        return lv_product_name;
    }


    public String getLv_product_line() {
        return lv_product_line;
    }


    public String getLv_product_count() {
        return lv_product_count;
    }
//-------------------------------------------------------------------------------
}
