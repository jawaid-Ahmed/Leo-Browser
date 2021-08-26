package com.jawaid.liobrowser.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jawaid.liobrowser.Adapters.BookmarksRecyclerAdapter;
import com.jawaid.liobrowser.Databases.DB_Bookmark;
import com.jawaid.liobrowser.Download.Data;
import com.jawaid.liobrowser.Interfaces.BookmarkOnItemClickListener;
import com.jawaid.liobrowser.R;
import com.jawaid.liobrowser.models.HistoryModel;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class BookmarksActivity extends AppCompatActivity implements BookmarkOnItemClickListener {

    private static final int STORAGE_PERMISSION_CODE = 37;
    RecyclerView recyclerView;
    List<HistoryModel> bookmarks;
    List<String> selectedBookmars;
    BookmarksRecyclerAdapter bookmarkAdapter;
    DB_Bookmark db_bookmark;

    private Toolbar toolbar;
    public boolean isContextualModeEnabled=false;
    private TextView counterText;
    private int counter=0;
    private Button editBookmark,clearBookmark;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarks);


        toolbar=findViewById(R.id.toolbar_history);
        counterText=findViewById(R.id.item_counter_text);
        setSupportActionBar(toolbar);

        setupToolbar();

        recyclerView=findViewById(R.id.bookmarksRecyclerView);
        clearBookmark=findViewById(R.id.clearbookmarkBtn);
        editBookmark=findViewById(R.id.editbookmarkBtn);
        db_bookmark=new DB_Bookmark(BookmarksActivity.this);
        bookmarks=new ArrayList<>();
        selectedBookmars=new ArrayList<String>();

       loadListView();

        /*recyclerView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                HistoryModel historyModel=bookmarks.get(i);
                Intent intent1=new Intent(BookmarksActivity.this, MainActivity.class);
                intent1.putExtra("link",historyModel.getUrl());
                startActivity(intent1);

            }
        });*/

        clearBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (bookmarks.size()>0) {
                    new MaterialAlertDialogBuilder(BookmarksActivity.this, R.style.myAlertDialoge)
                            .setTitle("Clear Bookmarks")
                            .setMessage("Do you really want to clear Bookmars?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    boolean is = db_bookmark.deleteAllBookmarks();
                                    if (is) {
                                        loadListView();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }
            }
        });

        editBookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LongClicked();
            }
        });

    }




    private void loadListView() {
        try {
            bookmarks=db_bookmark.getAllBookmark();
        }catch (Exception ex){
            Toast.makeText(this,"exception",Toast.LENGTH_SHORT).show();
        }
        bookmarkAdapter=new BookmarksRecyclerAdapter(this,bookmarks,this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(bookmarkAdapter);
    }

    private void setupToolbar() {

        counterText.setText("Bookmarks");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_normal,menu);
        return true;
    }


//    @Override
//    public boolean onLongClick(View view) {
//        LongClicked();
//        return true;
//    }

    private void LongClicked() {
        isContextualModeEnabled=true;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_contextual);
        bookmarkAdapter.notifyDataSetChanged();
        loadListView();
    }

    public void updateCounter(){
        counterText.setText(counter+" Items Selected");
    }
    public void checkSelection(View view, int i) {
        if (((CheckBox)view).isChecked()){
            selectedBookmars.add(bookmarks.get(i).getUrl());
            counter++;
            updateCounter();

        }else {
            selectedBookmars.remove(i);
            counter--;
            updateCounter();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.action_delete:
                deleteHistory();
                isContextualModeEnabled=false;
                toolbar.getMenu().clear();
                toolbar.inflateMenu(R.menu.menu_normal);
                counter=0;
                counterText.setText("");
                setupToolbar();
                loadListView();
                break;
            case R.id.action_settings:
                new MaterialAlertDialogBuilder(BookmarksActivity.this, R.style.myAlertDialoge)
                        .setTitle("Share Bookmarks")
                        .setMessage("Are you sure about this?")
                        .setPositiveButton(R.string.action_share, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                checkStoragePermissions();
                                String path= Data.getSaveDir()+"/exports/"+db_bookmark.BOOKMARK_TABLE_NAME+".csv";
                                File file=new File(path);
                                if (file.exists()){
                                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                    Uri screenshotUri = Uri.parse(file.getPath());
                                    sharingIntent.setType("*/*");
                                    sharingIntent.putExtra(Intent.EXTRA_STREAM, screenshotUri);
                                    startActivity(Intent.createChooser(sharingIntent, "Share using"));

                                }
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
        }

        return true;
    }


    private void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            exportDB();
        }
    }

    @Override
    public void onBackPressed() {
        if (isContextualModeEnabled){
            isContextualModeEnabled=false;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_normal);
            counter=0;
            counterText.setText("");
            setupToolbar();
            loadListView();
        }else {
            super.onBackPressed();
        }

    }

    private void deleteHistory() {

        if (selectedBookmars.size()>0) {
            db_bookmark.deleteHistorybyUrl(selectedBookmars);
        }
    }

    private void exportDB() {

        DB_Bookmark dbBookmark = new DB_Bookmark(getApplicationContext());
        File exportDir = new File(Data.getSaveDir(), "/exports");
        if (!exportDir.exists())
        {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, db_bookmark.BOOKMARK_TABLE_NAME+".csv");
        try
        {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbBookmark.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM bookmark",null);
            csvWrite.writeNext(curCSV.getColumnNames());
            while(curCSV.moveToNext())
            {
                //Which column you want to exprort
                String arrStr[] ={curCSV.getString(0),curCSV.getString(1), curCSV.getString(2)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        }
        catch(Exception sqlEx)
        {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    @Override
    public void onBItemClick(int position) {
        HistoryModel historyModel=bookmarks.get(position);
        Intent intent1=new Intent(BookmarksActivity.this, WebViewActivity.class);
        intent1.putExtra("urllink",historyModel.getUrl());
        startActivity(intent1);
        finish();
    }

    @Override
    public void onBLongItemClick(int position) {
        LongClicked();
    }

}
