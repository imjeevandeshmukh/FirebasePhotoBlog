package com.bytelogs.photoblog;

import java.util.Date;

public class CommentPojo {

    String message;
    Date time_stamp;
    String user_id;
    public CommentPojo(){

    }

    public CommentPojo(String message, Date time_stamp, String user_id) {
        this.message = message;
        this.time_stamp = time_stamp;
        this.user_id = user_id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTime_stamp() {
        return time_stamp;
    }

    public void setTime_stamp(Date time_stamp) {
        this.time_stamp = time_stamp;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
}
