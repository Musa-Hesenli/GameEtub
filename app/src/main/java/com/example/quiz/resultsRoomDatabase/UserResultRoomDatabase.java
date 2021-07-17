package com.example.quiz.resultsRoomDatabase;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(entities = {ResultEntity.class}, version = 1, exportSchema = false)
public abstract class UserResultRoomDatabase extends RoomDatabase {
    public abstract ResultDao resultDao();
    private static UserResultRoomDatabase INSTANCE;

    private static final int NUMBER_OF_THREADS = 4;
    static final ExecutorService databaseWriterExecutor = Executors.newFixedThreadPool(NUMBER_OF_THREADS);


    public static UserResultRoomDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (UserResultRoomDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                            UserResultRoomDatabase.class, "results")
                            .build();
                }
            }
        }
        return INSTANCE;
    }

}
