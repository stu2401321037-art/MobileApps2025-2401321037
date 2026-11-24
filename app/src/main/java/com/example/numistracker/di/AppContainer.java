package com.example.numistracker.di;

import android.content.Context;
import com.example.numistracker.data.DatabaseHelper;
import com.example.numistracker.data.NumisRepository;

public class AppContainer {
    private static volatile AppContainer instance;
    private final DatabaseHelper dbHelper;
    private final NumisRepository repository;

    private AppContainer(Context context) {
        dbHelper = new DatabaseHelper(context.getApplicationContext());
        repository = new NumisRepository(dbHelper);
    }

    public static AppContainer getInstance(Context context) {
        if (instance == null) {
            synchronized (AppContainer.class) {
                if (instance == null) instance = new AppContainer(context);
            }
        }
        return instance;
    }
    public DatabaseHelper getDatabaseHelper() { return dbHelper; }
    public NumisRepository getRepository() { return repository; }
}