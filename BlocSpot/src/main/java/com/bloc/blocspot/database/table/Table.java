package com.bloc.blocspot.database.table;

import android.database.sqlite.SQLiteDatabase;

import com.bloc.blocspot.blocspot.BlocSpotApplication;

public abstract class Table {
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
        new Thread() {
            @Override
            public void run() {
                super.run();
                if (BlocSpotApplication.get() == null) {
                    while (BlocSpotApplication.get() == null) {
                        try {
                            mDb = BlocSpotApplication.get().getWritableDb();
                        }
                        catch (NullPointerException ignored) {}
                    }
                } else {
                    mDb = BlocSpotApplication.get().getWritableDb();
                }
            }

        }.start();
    }
//
//            Handler handler = new Handler();
//            handler.postDelayed(new Runnable() {
//                public void run() {
//                    mDb = BlocSpotApplication.get().getWritableDb();
//                }
//            }, 2000);


    //Todo:share intent

    /*
     * Return the create statement for this Table
     */
    public abstract String getCreateStatement();

}
