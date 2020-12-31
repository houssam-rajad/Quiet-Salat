package com.quiet.salat;

import java.util.ArrayList;

public class Salat {
    public int id;
    public volatile String time;
    public volatile boolean isEnabled;

    public Salat(int id, String time, boolean isEnabled) {
        this.isEnabled = isEnabled;
        this.time = time;
        this.id=id;
    }

}

class SalatData {
    public static String DURATION;
    public static  SalatData shared = new SalatData();
    public   ArrayList<Salat> salatList=new ArrayList<Salat>();
}
