package com.example.earthquake;

public class details {

    private double mmagnitude;

    private String mplace;

    private long mtimeinmillisec;

    private String murl;

    public details(double magnitude, String place, long date, String url){
        mmagnitude = magnitude;
        mplace = place;
        mtimeinmillisec = date;
        murl = url;
    }

    public double getMagnitude(){
        return mmagnitude;
    }

    public String getPlace(){
        return mplace;
    }

    public long getTime(){
        return mtimeinmillisec;
    }

    public String getUrl(){return murl;}
}
