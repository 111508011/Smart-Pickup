package com.example.pratham.demo.adapter;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;

/**
 * Created by Pratham on 2/22/2017.
 */

public class Album implements Serializable {
    String email, photo_url, name, mobile_no, stop_name, expected_time;
    double distance;
    String stop_latitude, stop_logitude;

    public String getStop_latitude() {
        return stop_latitude;
    }

    public void setStop_latitude(String stop_latitude) {
        this.stop_latitude = stop_latitude;
    }

    public String getStop_logitude() {
        return stop_logitude;
    }

    public void setStop_logitude(String stop_logitude) {
        this.stop_logitude = stop_logitude;
    }

    public Album(String email, String photo_url, String name, String mobile_no, String stop_name, double distance, String expected_time, String stop_latitude, String stop_logitude) {
        this.email = email;
        this.photo_url = photo_url;
        this.name = name;
        this.mobile_no = mobile_no;
        this.stop_name = stop_name;

        this.expected_time = expected_time;
        this.distance = distance;
        this.stop_latitude = stop_latitude;
        this.stop_logitude = stop_logitude;
    }

    public String getExpected_time() {
        return expected_time;
    }

    public void setExpected_time(String expected_time) {
        this.expected_time = expected_time;
    }



    public Album(String email, String photo_url, String name, String mobile_no, String stop_name, double distance, String expected_time) {
        this.email = email;
        this.photo_url = photo_url;
        this.name = name;

        this.mobile_no = mobile_no;
        this.stop_name = stop_name;
        this.distance = distance;
        this.expected_time = expected_time;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoto_url() {
        return photo_url;
    }

    public void setPhoto_url(String photo_url) {
        this.photo_url = photo_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMobile_no() {
        return mobile_no;
    }

    public void setMobile_no(String mobile_no) {
        this.mobile_no = mobile_no;
    }

    public String getStop_name() {
        return stop_name;
    }

    public void setStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }


}
