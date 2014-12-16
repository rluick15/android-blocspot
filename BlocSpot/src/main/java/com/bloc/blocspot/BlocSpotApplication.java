package com.bloc.blocspot.blocspot;

import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.bloc.blocspot.database.BlocSpotDbHelper;

public class BlocSpotApplication  extends Application {
    private static BlocSpotDbHelper mDatabase;
    private static Context context;

    public BlocSpotApplication() {}

    @Override
    public void onCreate() {
        mDatabase = new BlocSpotDbHelper(getApplicationContext());
        BlocSpotApplication.context = getApplicationContext();
    }

    public SQLiteDatabase getWritableDb() {
        return mDatabase.getWritableDatabase();
    }

    public static BlocSpotApplication get() {
        return (BlocSpotApplication) BlocSpotApplication.context;
    }
}
