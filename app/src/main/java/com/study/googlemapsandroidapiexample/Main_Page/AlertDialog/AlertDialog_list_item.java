package com.study.googlemapsandroidapiexample.Main_Page.AlertDialog;

public class AlertDialog_list_item {
    private String note;
    private String name;
    private String img_path;
    private Integer count;
    private Integer drink_line;

    public AlertDialog_list_item(String note, String name, String img_path, Integer count, Integer drink_line) {
        this.note = note;
        this.name = name;
        this.img_path = img_path;
        this.count = 35-count;
        this.drink_line = drink_line;
    }

    public String getNote() {
        return note;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getImg_path() {
        return img_path;
    }

    public Integer getCount() {
        return count;
    }


    public Integer getDrink_line() {
        return drink_line;
    }

}
