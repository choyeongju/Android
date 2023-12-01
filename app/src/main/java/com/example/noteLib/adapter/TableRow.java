package com.example.noteLib.adapter;

public class TableRow {
    private String title1 = null;
    private String title2 = null;
    private String title3 = null;

    public TableRow(String title1, String title2, String title3){
        this.title1 = title1;
        this.title2 = title2;
        this.title3 = title3;
    }

    public String getTitle1() {
        return title1;
    }
    public String getTitle2() {
        return title2;
    }
    public String getTitle3() {
        return title3;
    }
}
