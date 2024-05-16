package com.example.basicsqlite.rv;

public class Data {
    String id, title, desc; int num;

    public Data(String title, String desc, int num) {
        this.title = title;
        this.desc = desc;
        this.num = num;
        this.id = String.valueOf(num);
        //  this.id = null;
    }

    public Data(String id, String title, String desc, int num) {
        this.id = id;
        this.title = title;
        this.desc = desc;
        this.num = num;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }
}
