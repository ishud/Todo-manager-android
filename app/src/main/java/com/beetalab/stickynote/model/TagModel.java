package com.beetalab.stickynote.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.beetalab.stickynote.config.Constant;

import java.io.Serializable;

@Entity(tableName = Constant.TABLE_NAME_TAGS)
public class TagModel implements Serializable {

    @PrimaryKey
    @NonNull
    private String tag_id;

    private String tag_name;
    private String tag_color;
    private boolean tag_is_custom;
    private int task_count;

    public TagModel(@NonNull String tag_id, String tag_name, String tag_color, boolean tag_is_custom, int task_count) {
        this.tag_id = tag_id;
        this.tag_name = tag_name;
        this.tag_color = tag_color;
        this.tag_is_custom = tag_is_custom;
        this.task_count = task_count;
    }

    public TagModel() {
    }

    @NonNull
    public String getTag_id() {
        return tag_id;
    }

    public void setTag_id(@NonNull String tag_id) {
        this.tag_id = tag_id;
    }

    public String getTag_name() {
        return tag_name;
    }

    public void setTag_name(String tag_name) {
        this.tag_name = tag_name;
    }

    public String getTag_color() {
        return tag_color;
    }

    public void setTag_color(String tag_color) {
        this.tag_color = tag_color;
    }

    public boolean isTag_is_custom() {
        return tag_is_custom;
    }

    public void setTag_is_custom(boolean tag_is_custom) {
        this.tag_is_custom = tag_is_custom;
    }

    public int getTask_count() {
        return task_count;
    }

    public void setTask_count(int task_count) {
        this.task_count = task_count;
    }
}
