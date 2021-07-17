package com.example.quiz.resultsRoomDatabase;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface ResultDao {
    @Insert
    void insert(ResultEntity resultEntity);

    @Query("SELECT * FROM results")
    public LiveData<List<ResultEntity>> getAllResults();
}
