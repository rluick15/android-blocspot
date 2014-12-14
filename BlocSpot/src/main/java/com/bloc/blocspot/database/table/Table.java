package com.bloc.blocspot.database.table;

import android.database.sqlite.SQLiteDatabase;

import com.bloc.blocspot.blocspot.BlocSpotApplication;

/**
 * Created by Rich on 12/14/2014.
 */
public abstract class Table {
    protected boolean mLoaded = false;
    private String TABLE_NAME;
    SQLiteDatabase mDb;

    public Table(String name) {
        this.TABLE_NAME = name;
        load();
    }

    public String getName() {
        return this.TABLE_NAME;
    }

    public final void load() {
        if (isLoaded()) {
            return;
        }
        new Thread(){
            @Override
            public void run() {
                super.run();
                mDb =  BlocSpotApplication.get().getWritableDb();
            }
        }.start();
        setLoaded(true);
    }

    protected void setLoaded(boolean loaded) {
        mLoaded = loaded;
    }

    public boolean isLoaded() {
        return mLoaded;
    }

    /*
     * Return the create statement for this Table
     */
    public abstract String getCreateStatement();

    /*
     * Upgrade the table
     */
    public abstract void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion);
}
