package com.beetalab.stickynote.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import java.io.Serializable;

import static com.beetalab.stickynote.config.Constant.TABLE_NAME_TO_DO;

@Entity(tableName = TABLE_NAME_TO_DO)
public class ToDoModel extends BaseModel implements Serializable {

    @PrimaryKey
   private long createdDate;

    private String task_title;
    private String description;
    private String task_date;
    private long date_as_long;
    private boolean notify;

    private String tag_id;
    private String tag_color;

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    private String tag_name;

    private String quary_date;
    private boolean is_complete;

    public long getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(long createdDate) {
        this.createdDate = createdDate;
    }

    public String getTask_title() {
        return task_title;
    }

    public void setTask_title(String task_title) {
        this.task_title = task_title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTask_date() {
        return task_date;
    }

    public void setTask_date(String task_date) {
        this.task_date = task_date;
    }

    public long getDate_as_long() {
        return date_as_long;
    }

    public void setDate_as_long(long date_as_long) {
        this.date_as_long = date_as_long;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_color() {
        return tag_color;
    }

    public void setTag_color(String tag_color) {
        this.tag_color = tag_color;
    }

    public String getQuary_date() {
        return quary_date;
    }

    public void setQuary_date(String quary_date) {
        this.quary_date = quary_date;
    }

    public boolean isIs_complete() {
        return is_complete;
    }

    public void setIs_complete(boolean is_complete) {
        this.is_complete = is_complete;
    }
}
