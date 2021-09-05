package com.app.televisionguru.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.app.televisionguru.dao.Task;
import com.app.televisionguru.dao.TaskDao;

@Database(entities = {Task.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract TaskDao taskDao();
}