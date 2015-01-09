package com.bloc.blocspot.database.table;

import android.content.ContentValues;
import android.database.Cursor;

import com.bloc.blocspot.utils.Constants;

public class PoiTable extends Table {

    private static final String SQL_CREATE_POI =
            "CREATE TABLE " + Constants.TABLE_POI_NAME + " (" +
                    Constants.TABLE_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                    Constants.TABLE_COLUMN_POI_NAME + " TEXT," +
                    Constants.TABLE_COLUMN_NOTE + " TEXT," +
                    Constants.TABLE_COLUMN_VISITED + " TEXT," +
                    Constants.TABLE_COLUMN_LATITUDE + " DOUBLE," +
                    Constants.TABLE_COLUMN_LONGITUDE + " DOUBLE," +
                    Constants.TABLE_COLUMN_CAT_NAME + " TEXT," +
                    Constants.TABLE_COLUMN_CAT_COLOR + " TEXT," +
                    Constants.TABLE_COLUMN_GEO_ID + " TEXT," +
                    "UNIQUE(" + Constants.TABLE_COLUMN_POI_NAME +
                    ") ON CONFLICT REPLACE" +
                    ")";

    public PoiTable() {
        super(Constants.TABLE_POI_NAME);
    }

    @Override
    public String getCreateStatement() {
        return SQL_CREATE_POI;
    }

    public void addNewPoi(String name, double lat, double lng, String catName, String catColor, String geoId) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_POI_NAME, name);
        values.put(Constants.TABLE_COLUMN_LATITUDE, lat);
        values.put(Constants.TABLE_COLUMN_LONGITUDE, lng);
        values.put(Constants.TABLE_COLUMN_CAT_NAME, catName);
        values.put(Constants.TABLE_COLUMN_CAT_COLOR, catColor);
        values.put(Constants.TABLE_COLUMN_NOTE, "");
        values.put(Constants.TABLE_COLUMN_VISITED, false);
        values.put(Constants.TABLE_COLUMN_GEO_ID, geoId);
        mDb.insert(Constants.TABLE_POI_NAME, null, values);
    }

    public Cursor poiQuery() {
        return mDb.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                        Constants.TABLE_COLUMN_NOTE, Constants.TABLE_COLUMN_VISITED,
                        Constants.TABLE_COLUMN_LATITUDE, Constants.TABLE_COLUMN_LONGITUDE,
                        Constants.TABLE_COLUMN_CAT_NAME, Constants.TABLE_COLUMN_CAT_COLOR,
                        Constants.TABLE_COLUMN_GEO_ID},
                null, null, null, null, null, null);
    }

    public Cursor poiSpecificQuery(String id) {
        return mDb.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                        Constants.TABLE_COLUMN_NOTE, Constants.TABLE_COLUMN_VISITED,
                        Constants.TABLE_COLUMN_LATITUDE, Constants.TABLE_COLUMN_LONGITUDE,
                        Constants.TABLE_COLUMN_CAT_NAME, Constants.TABLE_COLUMN_CAT_COLOR,
                        Constants.TABLE_COLUMN_GEO_ID},
                Constants.TABLE_COLUMN_ID + " = ?",
                new String[]{id},
                null, null, null, null);
    }

    public Cursor notificationQuery(String queryString, String[] geoIds) {
        String query = "SELECT * FROM "+ Constants.TABLE_POI_NAME + " WHERE "+
                Constants.TABLE_COLUMN_GEO_ID +" IN (" + queryString + ")";
        return mDb.rawQuery(query, geoIds);
    }

    public Cursor filterQuery(String filter) {
        return mDb.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME,
                        Constants.TABLE_COLUMN_NOTE, Constants.TABLE_COLUMN_VISITED,
                        Constants.TABLE_COLUMN_LATITUDE, Constants.TABLE_COLUMN_LONGITUDE,
                        Constants.TABLE_COLUMN_CAT_NAME, Constants.TABLE_COLUMN_CAT_COLOR,
                        Constants.TABLE_COLUMN_GEO_ID},
                Constants.TABLE_COLUMN_CAT_NAME + " = ?",
                new String[]{filter},
                null, null, null, null);
    }

    public Cursor alreadyPoiCheck(String name) {
        return mDb.query(Constants.TABLE_POI_NAME,
                new String[]{Constants.TABLE_COLUMN_ID, Constants.TABLE_COLUMN_POI_NAME
                        , Constants.TABLE_COLUMN_CAT_COLOR},
                Constants.TABLE_COLUMN_POI_NAME + " = ?",
                new String[]{name},
                null, null, null, null);
    }

    public void updateNote(String id, String note) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_NOTE, note);
        mDb.update(Constants.TABLE_POI_NAME, values,
                Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }

    public void updateVisited(String id, Boolean visited) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_VISITED, visited);
        mDb.update(Constants.TABLE_POI_NAME, values,
                Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }

    public void deletePoi(String id) {
        mDb.delete(Constants.TABLE_POI_NAME, Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }

    public void updateCategory(String id, String category, String catColor) {
        ContentValues values = new ContentValues();
        values.put(Constants.TABLE_COLUMN_CAT_NAME, category);
        values.put(Constants.TABLE_COLUMN_CAT_COLOR, catColor);
        mDb.update(Constants.TABLE_POI_NAME, values,
                Constants.TABLE_COLUMN_ID + " = ?", new String[]{id});
    }
}
