package com.beetalab.stickynote.db;

import android.content.Context;

import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.beetalab.stickynote.model.NoteModel;
import com.beetalab.stickynote.model.TagModel;
import com.beetalab.stickynote.model.ToDoModel;

@androidx.room.Database(entities = {ToDoModel.class, NoteModel.class, TagModel.class},version = 2,exportSchema = false)
public abstract class Database extends RoomDatabase {

    private static Database INSTANT;

    public static Database getInstance(Context context){

        if(INSTANT == null){
            INSTANT = Room.databaseBuilder(context,Database.class,"stick_note_db")
                    .fallbackToDestructiveMigration()
                    .build();
        }
        return INSTANT;
    }


    public abstract DbClient getDao();

}
