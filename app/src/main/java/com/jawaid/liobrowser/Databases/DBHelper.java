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

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "FireBrowser.db";
    public static final int DATABASE_VERSION = 1;
    public static final String HISTORY_TABLE_NAME = "history";
    public static final String HISTORY_COLUMN_ID = "id";
    public static final String HISTORY_COLUMN_TITLE = "title";
    public static final String HISTORY_COLUMN_URL = "url";
    public static final String HISTORY_COLUMN_IMAGE = "byte";
    public static final String HISTORY_COLUMN_DATE = "date";

    private HashMap hp;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        String historyTable="create table " +HISTORY_TABLE_NAME+
                "("+HISTORY_COLUMN_ID+" integer primary key, "
                +HISTORY_COLUMN_TITLE+" text,"
                +HISTORY_COLUMN_URL+" text,"
                +HISTORY_COLUMN_DATE+" text," +
                ""+HISTORY_COLUMN_IMAGE+" Blob)";


        db.execSQL(historyTable);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub

        db.execSQL("DROP TABLE IF EXISTS "+HISTORY_TABLE_NAME);

        onCreate(db);



    }

    public boolean insertHistory(String title, String url,String date, byte[] image)throws SQLiteException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(HISTORY_COLUMN_TITLE, title);
        contentValues.put(HISTORY_COLUMN_URL, url);
        contentValues.put(HISTORY_COLUMN_IMAGE, image);
        contentValues.put(HISTORY_COLUMN_DATE, date);
        db.insert(HISTORY_TABLE_NAME, null, contentValues);


        return true;
    }


    public HistoryModel getHistoryData(int id) {
        HistoryModel historyModel = null;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+HISTORY_TABLE_NAME+" where id="+id+"", null );

        while(res.isAfterLast() == false){
            historyModel=new HistoryModel(res.getString(res.getColumnIndex(HISTORY_COLUMN_TITLE))
                    ,res.getString(res.getColumnIndex(HISTORY_COLUMN_URL))
                    ,res.getString(res.getColumnIndex(HISTORY_COLUMN_DATE))
            ,res.getBlob(res.getColumnIndex(HISTORY_COLUMN_IMAGE)));
            res.moveToNext();
        }
        return historyModel;
    }


//    public Integer getId(){
//        ArrayList<Integer> ids=new ArrayList<>();
//
//    }


    public boolean deleteHistorybyUrl (List<String> urls) {
        SQLiteDatabase db = this.getWritableDatabase();
        for (int i = 0; i < urls.size(); i++) {
            db.execSQL("DELETE FROM " + HISTORY_TABLE_NAME+" WHERE "+HISTORY_COLUMN_URL+" = ? ",new String[] {urls.get(i)});
        }
        db.close();
        return true;
    }

    public List<HistoryModel> getHistoryByDate (String date) {
        List<HistoryModel> todaysHistory=new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res=db.rawQuery("SELECT * FROM " + HISTORY_TABLE_NAME+" WHERE "+HISTORY_COLUMN_DATE+" = '"+date+"'",null);
        res.moveToFirst();

        while(!res.isAfterLast()){
            HistoryModel historyModel=new HistoryModel(res.getString(res.getColumnIndex(HISTORY_COLUMN_TITLE))
                    ,res.getString(res.getColumnIndex(HISTORY_COLUMN_URL))
                    ,res.getString(res.getColumnIndex(HISTORY_COLUMN_DATE))
                    ,res.getBlob(res.getColumnIndex(HISTORY_COLUMN_IMAGE)));
            todaysHistory.add(historyModel);
            res.moveToNext();
        }
        db.close();
        return todaysHistory;
    }

    public List<HistoryModel> getHistoryBeforeYesterday (String today,String yesterday) {
        List<HistoryModel> todaysHistory=new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();

        String COLUMNS[]={HISTORY_COLUMN_ID,HISTORY_COLUMN_TITLE,HISTORY_COLUMN_URL,HISTORY_COLUMN_IMAGE,HISTORY_COLUMN_DATE};
        String Query=HISTORY_COLUMN_DATE+" !='"+today+"' AND "+HISTORY_COLUMN_DATE+" !='"+yesterday+"'";

        Cursor res=db.query(HISTORY_TABLE_NAME,COLUMNS,Query,null,null,null,null);
        res.moveToFirst();
        while(!res.isAfterLast()){
            HistoryModel historyModel=new HistoryModel(res.getString(0)
                    ,res.getString(1)
                    ,res.getString(2)
                    ,res.getBlob(3));
            todaysHistory.add(historyModel);
            res.moveToNext();
        }
        db.close();
        return todaysHistory;

    }

    public boolean deleteAllHistory(){
        SQLiteDatabase db=this.getWritableDatabase();
        //Delete all records of table
       db.execSQL("DELETE FROM " + HISTORY_TABLE_NAME);

       return true;
    }

    public void UpdatePrimarykeynumbersequence(){
        SQLiteDatabase db=this.getWritableDatabase();
        //Reset the auto_increment primary key if you needed
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME=" + HISTORY_TABLE_NAME);

//For go back free space by shrinking sqlite file
        db.execSQL("VACUUM");
    }

    public ArrayList<HistoryModel> getAllHistory() {
        ArrayList<HistoryModel> array_list = new ArrayList();

        //hp = new HashMap();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor res =  db.rawQuery( "select * from "+HISTORY_TABLE_NAME, null );
        res.moveToFirst();

        while(res.isAfterLast() == false){
            array_list.add(new HistoryModel(res.getString(res.getColumnIndex(HISTORY_COLUMN_TITLE))
                    ,res.getString(res.getColumnIndex(HISTORY_COLUMN_URL))
                    ,res.getString(res.getColumnIndex(HISTORY_COLUMN_DATE))
                    ,res.getBlob(res.getColumnIndex(HISTORY_COLUMN_IMAGE))));
            res.moveToNext();
        }
        return array_list;
    }

}