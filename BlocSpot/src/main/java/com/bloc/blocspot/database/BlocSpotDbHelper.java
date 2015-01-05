package com.bloc.blocspot.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bloc.blocspot.database.table.PoiTable;
import com.bloc.blocspot.database.table.Table;
import com.bloc.blocspot.utils.Constants;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by Rich on 12/14/2014.
 */
public class BlocSpotDbHelper extends SQLiteOpenHelper {

    //Create Tables
    private static Set<Table> sTables = new HashSet<Table>();
    static {
        sTables.add(new PoiTable());
    }

    public BlocSpotDbHelper(Context context) {
        super(context, Constants.DATABASE_NAME, null, Constants.DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Iterator<Table> tables = sTables.iterator();
        while (tables.hasNext()){
            sqLiteDatabase.execSQL(tables.next().getCreateStatement());
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion == 12 && newVersion == 13) {
            db.execSQL("DROP TABLE " + Constants.TABLE_POI_NAME);
            onCreate(db);
        }
    }


}
