package com.example.pratham.demo.owner_adapter;

import java.io.Serializable;

/**
 * Created by Pratham on 2/22/2017.
 */

public class AlbumOwner implements Serializable {
    String email, photo_url, name, mobile_no, stop_name, stop_latitude, stop_longitude;

    public String getStop_latitude() {
        return stop_latitude;
    }

    public void setStop_latitude(String stop_latitude) {
        this.stop_latitude = stop_latitude;
    }

    public String getStop_longitude() {
        return stop_longitude;
    }

    public void setStop_longitude(String stop_longitude) {
        this.stop_longitude = stop_longitude;
    }

    public AlbumOwner(String email, String photo_url, String name, String mobile_no, String stop_name, String stop_latitude, String stop_longitude) {
        this.email = email;
        this.photo_url = photo_url;
        this.name = name;
        this.mobile_no = mobile_no;
        this.stop_name = stop_name;
        this.stop_latitude = stop_latitude;
        this.stop_longitude = stop_longitude;
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
}
