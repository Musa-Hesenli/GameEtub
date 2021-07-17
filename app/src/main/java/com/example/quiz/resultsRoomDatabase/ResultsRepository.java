package com.example.quiz.resultsRoomDatabase;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;

public class ResultsRepository {
    private final ResultDao resultDao;
    private final LiveData<List<ResultEntity>> allEntities;
    public ResultsRepository(Application application) {
        UserResultRoomDatabase db = UserResultRoomDatabase.getDatabase(application);
        resultDao = db.resultDao();
        allEntities = resultDao.getAllResults();
    }
    LiveData<List<ResultEntity>> getAllWords() {
        return allEntities;
    }

    void insert(ResultEntity entity) {
        UserResultRoomDatabase.databaseWriterExecutor.execute(() -> {
            resultDao.insert(entity);
        });
    }


}
