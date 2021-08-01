package com.beetalab.stickynote.model;

import java.io.Serializable;
import java.util.List;

public class GroupTodo implements Serializable {

    public GroupTodo(String title) {
        this.title = title;
    }

    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
