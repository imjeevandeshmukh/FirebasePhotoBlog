package com.bytelogs.photoblog;

public class UserPojo {

    String name;
    String pro_image_url;

    public UserPojo() {

    }

    public UserPojo(String name, String pro_image_url) {
        this.name = name;
        this.pro_image_url = pro_image_url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPro_image_url() {
        return pro_image_url;
    }

    public void setPro_image_url(String pro_image_url) {
        this.pro_image_url = pro_image_url;
    }
}
