package com.app.televisionguru.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {
 
    @Query("SELECT * FROM task where type = :type")
    List<Task> getAll(String type);
 
    @Insert
    void insert(Task task);
 
    @Delete
    void delete(Task task);
 
    @Update
    void update(Task task);
}