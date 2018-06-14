package com.bytelogs.photoblog;

import java.util.Date;

public class PostPojo {

    String image_url,description,thumbnail,user_id;
    Date time_stamp;
    String postid;

    public PostPojo() {

    }

    public PostPojo(String image_url, String description, String thumbnail, String user_id, Date time_stamp,String postid) {
        this.image_url = image_url;
        this.description = description;
        this.thumbnail = thumbnail;
        this.user_id = user_id;
        this.time_stamp = time_stamp;
        this.postid = postid;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public void setThumbnail(String thumbnail) {
        this.thumbnail = thumbnail;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getPostid() {
        return postid;
    }

    public void setPostid(String postid) {
        this.postid = postid;
    }
}
