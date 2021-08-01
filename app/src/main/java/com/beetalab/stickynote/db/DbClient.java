package com.beetalab.stickynote.db;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import com.beetalab.stickynote.model.NoteModel;
import com.beetalab.stickynote.model.TagModel;
import com.beetalab.stickynote.model.ToDoModel;

import java.util.List;

@Dao
public interface DbClient{

    @Insert
    void insertNewTodo(ToDoModel... data);

    @Query(value = "SELECT * FROM tbl_to_do WHERE quary_date=:date ORDER BY date_as_long ASC")
    List<ToDoModel> readAllTodo(String date);

    @Query(value = "SELECT * FROM tbl_to_do WHERE tag_id=:tag_id  ORDER BY date_as_long ASC")
    List<ToDoModel> getTodoByTagId(String tag_id);

    @Query(value = "SELECT quary_date FROM tbl_to_do WHERE tag_id=:tag_id AND date_as_long >=:dateLong ORDER BY date_as_long ASC")
    List<String> getAllQuaryDate(String tag_id,long dateLong);

    @Query(value = "SELECT COUNT(*) FROM tbl_to_do WHERE date_as_long >:date AND tag_id=:tag_id")
    int getTaskCount(long date,String tag_id);

    @Query(value = "DELETE FROM tbl_to_do WHERE date_as_long <:date")
    void deleteExpriredTodo(long date);

    @Update
    void updateTodo(ToDoModel... models);


    @Query("DELETE  FROM tbl_to_do WHERE tag_id=:tag_id")
    void deleteTodoByTagId(String tag_id);



    @Delete
    void deleteToDo(ToDoModel... data);

    @Insert
    void insertNewNote(NoteModel... models);

    @Update
    void updateNote(NoteModel... models);

    @Delete
    void deleteNote(NoteModel... models);

    @Query(value = "SELECT * FROM tbl_note ORDER BY id DESC")
    List<NoteModel>getAllNotes();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertTag(TagModel... models);

    @Query(value = "SELECT * FROM tbl_tags")
    List<TagModel> getAllTags();

    @Query(value = "SELECT * FROM tbl_tags WHERE tag_id=:id")
    TagModel getTagById(String id);

    @Update
    void updateTag(TagModel... models);

    @Delete
    void deleteTag(TagModel... model);

    @Query(value = "SELECT COUNT(*) FROM tbl_tags WHERE tag_is_custom=:isCustomTag")
    int getCustomTagCount(boolean isCustomTag);

}
