package com.app.televisionguru.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface TaskDao {

    @Query("SELECT * FROM task where type = :type")
    LiveData<List<Task>> getAll(String type);

    @Query("SELECT * FROM task where type = :type ORDER BY name ASC")
    List<Task> getAllByType(String type);

    @Query("SELECT * FROM task where type = :type ORDER BY RANDOM()")
    List<Task> getAllRandomByType(String type);

    @Query("SELECT * FROM task where type = :type ORDER BY name ASC")
    LiveData<List<Task>> getAllByAscendingOrder(String type);

    @Query("SELECT * FROM task where type = :type and isVisible = 0 ORDER BY RANDOM() LIMIT 1;")
    Task getRandomTask(String type);

    @Insert
    void insert(Task task);

    @Insert
    void insertAll(List<Task> task);

    @Delete
    void delete(Task task);

    @Query("Delete from task where type =:type")
    int deleteAll(String type);

    @Update
    void update(Task task);

    @Query("update task set isVisible = :b where id = :id and type = :type")
    void updateVisibility(boolean b, int id, String type);
}