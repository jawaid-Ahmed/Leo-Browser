package com.jawaid.liobrowser.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.jawaid.liobrowser.models.DownloadModel;

import java.util.ArrayList;
import java.util.HashMap;

public class DBDownload extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Database.db";
    public static final String TAG = "mytag";
    public static final int DATABASE_VERSION = 1;
    String TABLE_NAME = "thistable";
    String KEY_ID = "id";
    String TITLE = "title";
    String URL = "url";
    String DOWNLOADID = "downloadid";
    String ISDOWNLOADING = "isdownloading";

    private Context context;
    private HashMap hp;

    public DBDownload(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);

        this.context = context;
        Log.d(TAG, "DB CREATED");
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME +
                " ( " +
                KEY_ID + " INTEGER PRIMARY KEY," +
                TITLE + " TEXT," +
                URL + " TEXT," +
                ISDOWNLOADING + " TEXT," +
                DOWNLOADID + " INTEGER ) ";

        db.execSQL(query);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);


        onCreate(db);


    }


    public boolean insertDownload(String title, String url, String isdownloading, int downloadid) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(URL, url);
        contentValues.put(DOWNLOADID, downloadid);
        contentValues.put(ISDOWNLOADING, isdownloading);
        db.insert(TABLE_NAME, null, contentValues);

        Log.d(TAG, "VALUE INSERTED");


        return true;
    }


    public Cursor getDownloadData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res = db.rawQuery("select * from mydownload where id=" + id + "", null);


        return res;
    }


    public boolean updateDownload(Integer id, String title, String url, int downloadid, String isdownloading) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(TITLE, title);
        contentValues.put(URL, url);
        contentValues.put(DOWNLOADID, downloadid);
        contentValues.put(ISDOWNLOADING, isdownloading);

        db.update(TABLE_NAME, contentValues, "id = ? ", new String[]{Integer.toString(id)});
        return true;
    }


    public Integer deleteDownload(Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_NAME,
                "id = ? ",
                new String[]{Integer.toString(id)});
    }

    public boolean deleteAllDownloads(){
        SQLiteDatabase db=this.getWritableDatabase();
        //Delete all records of table
        db.execSQL("DELETE FROM " + TABLE_NAME);

        return true;
    }

    public ArrayList<DownloadModel> getAllDownloads() {
        ArrayList<DownloadModel> array_list = new ArrayList();


        SQLiteDatabase db = this.getReadableDatabase();

        Cursor res = db.rawQuery("select * from " + TABLE_NAME, null);

        res.moveToFirst();

        while (res.isAfterLast() == false) {

            if (res.moveToFirst()) {
                do {
                    DownloadModel std = new DownloadModel();
                    std.setTitle(res.getString(res.getColumnIndex(TITLE)));
                    std.setUrl(res.getString(res.getColumnIndex(URL)));
                    std.setDownloadid(Integer.parseInt(res.getString(res.getColumnIndex(DOWNLOADID))));
                    std.setIsDownloading(res.getString(res.getColumnIndex(ISDOWNLOADING)));

                    array_list.add(new DownloadModel(std.getTitle(), std.getUrl(), std.getIsDownloading(), std.getDownloadid()));
                } while (res.moveToNext());
            }

        }
        return array_list;
    }
}