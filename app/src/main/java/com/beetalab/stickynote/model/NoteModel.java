package com.beetalab.stickynote.model;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.beetalab.stickynote.config.Constant;

import java.io.Serializable;

@Entity(tableName = Constant.TABLE_NAME_NOTE)
public class NoteModel implements Serializable {

    @PrimaryKey
    private long id;
    private String title;
    private String note;
    private String color;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }
}
