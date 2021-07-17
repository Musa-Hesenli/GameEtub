package com.example.quiz.resultsRoomDatabase;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import java.util.List;

public class ViewModel extends AndroidViewModel {
    private final ResultsRepository resultsRepository;
    private final LiveData<List<ResultEntity>> allEntities;
    public ViewModel(Application application) {
        super(application);
        resultsRepository = new ResultsRepository(application);
        allEntities = resultsRepository.getAllWords();
    }

    public LiveData<List<ResultEntity>> getAllEntities() {
        return  allEntities;
    }

    public void insert(ResultEntity entity) {
        resultsRepository.insert(entity);
    }
}
