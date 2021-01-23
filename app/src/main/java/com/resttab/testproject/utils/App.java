package com.resttab.testproject.utils;

import android.app.Application;
import android.content.Context;

import androidx.room.Room;

import com.resttab.testproject.dbHelper.db.AddressDB;

import static com.resttab.testproject.utils.Constants.DB_NAME;

public class App extends Application {

    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
    }

    public Context getContext() {
        return context;
    }

    public static AddressDB getDatabase() {
        return Room.databaseBuilder(context, AddressDB.class, DB_NAME)
                .fallbackToDestructiveMigration()
                .allowMainThreadQueries()
                .build();
    }
}
