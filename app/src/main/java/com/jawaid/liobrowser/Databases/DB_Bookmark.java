package com.jawaid.liobrowser.Databases;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import com.jawaid.liobrowser.models.HistoryModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DB_Bookmark extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Bookmark.db";
    public static final int DATABASE_VERSION = 1;
    public static final String BOOKMARK_TABLE_NAME = "bookmark";
    public static final String BOOKMARK_COLUMN_ID = "id";
    public static final String BOOKMARK_COLUMN_TITLE = "title";
    public static final String BOOKMARK_COLUMN_URL = "url";
    public static final String BOOKMARK_COLUMN_IMAGE = "byte";
    public static final String BOOKMARK_COLUMN_DATE = "date";
    private HashMap hp;

    public DB_Bookmark(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String bookmarkTable="create table " +BOOKMARK_TABLE_NAME+
                "("+BOOKMARK_COLUMN_ID+" integer primary key, "
                +BOOKMARK_COLUMN_TITLE+" text,"
                +BOOKMARK_COLUMN_URL+" text,"
                +BOOKMARK_COLUMN_DATE+" text,"
                +BOOKMARK_COLUMN_IMAGE+" Blob)";


        db.execSQL(bookmarkTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS "+BOOKMARK_TABLE_NAME);



        onCreate(db);



    }


    public boolean insertBookmark(String title, String url, String date,byte[] image)throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(BOOKMARK_COLUMN_TITLE, title);
        contentValues.put(BOOKMARK_COLUMN_URL, url);
        contentValues.put(BOOKMARK_COLUMN_DATE, date);
        contentValues.put(BOOKMARK_COLUMN_IMAGE, image);
        db.insert(BOOKMARK_TABLE_NAME, null, contentValues);
        return true;
    }


    public HistoryModel getBookmarkData(int id) {
        HistoryModel historyModel = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+BOOKMARK_TABLE_NAME+" where id="+id+"", null );


        while(res.isAfterLast() == false){
            historyModel=new HistoryModel(res.getString(res.getColumnIndex(BOOKMARK_COLUMN_TITLE))
                    ,res.getString(res.getColumnIndex(BOOKMARK_COLUMN_URL))
                    ,res.getString(res.getColumnIndex(BOOKMARK_COLUMN_DATE))
                    ,res.getBlob(res.getColumnIndex(BOOKMARK_COLUMN_IMAGE)));
            res.moveToNext();
        }
        return historyModel;
    }

    public boolean deleteAllBookmarks(){
        SQLiteDatabase db=this.getWritableDatabase();
        //Delete all records of table
        db.execSQL("DELETE FROM " + BOOKMARK_TABLE_NAME);

        return true;
    }

    public boolean deleteHistorybyUrl (List<String> urls) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < urls.size(); i++) {
            db.execSQL("DELETE FROM " + BOOKMARK_TABLE_NAME+" WHERE "+BOOKMARK_COLUMN_URL+" = ? ",new String[] {urls.get(i)});
        }
        db.close();
        return true;
    }


    public boolean updateBookmark (Integer id, String title, String url, byte[] b_byte) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("title", title);
        contentValues.put("url", url);
        contentValues.put("byte", b_byte);

        db.update(BOOKMARK_TABLE_NAME, contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }



    public Integer deleteBookmark (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(BOOKMARK_TABLE_NAME,
                "id = ? ",
                new String[] { Integer.toString(id) });
    }


    public ArrayList<HistoryModel> getAllBookmark() {
        ArrayList<HistoryModel> array_list = new ArrayList();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+BOOKMARK_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new HistoryModel(res.getString(res.getColumnIndex(BOOKMARK_COLUMN_TITLE))
                    ,res.getString(res.getColumnIndex(BOOKMARK_COLUMN_URL))
                    ,res.getString(res.getColumnIndex(BOOKMARK_COLUMN_DATE))
            ,res.getBlob(res.getColumnIndex(BOOKMARK_COLUMN_IMAGE))));
            res.moveToNext();
        }
        return array_list;
    }
}