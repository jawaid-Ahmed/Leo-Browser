package com.jawaid.liobrowser.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.jawaid.liobrowser.Adapters.HistoryRecyclerAdapter;
import com.jawaid.liobrowser.Databases.DBHelper;
import com.jawaid.liobrowser.Download.Data;
import com.jawaid.liobrowser.Interfaces.OnHorizontalItemClickListener;
import com.jawaid.liobrowser.R;
import com.jawaid.liobrowser.models.HistoryModel;
import com.opencsv.CSVWriter;

import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.File;
import java.io.FileWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class HistoryActivity extends AppCompatActivity implements OnHorizontalItemClickListener{

    List<HistoryModel> history, historyYesterday, historyBeforeYesterday,allHistory;
    List<String> selectedHistory;
    HistoryRecyclerAdapter historyRecyclerAdapter;
    RecyclerView historyRecyclerView,historyRecyclerYesterday,historyRecyclerBeforeYesterday;

    DBHelper dbHelper;
    Button clearHistory, btn;
    private Toolbar toolbar;
    public boolean isContextualModeEnabled = false;
    private TextView textCounter;
    private int counter = 0;
    private String currentDate,yesterdayDate;
    private Button editHistory;
    private ExpandableLayout expandableLayout, expandableLayoutYesterday, expandableLayoutBeforeYesterday;
    private Button expandButtonToday, expandButtonYesterday, expandButtonbeforeYesterday;
    private int STORAGE_PERMISSION_CODE = 200;
    private View mainView;
    private RelativeLayout rootView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        toolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        mainView = findViewById(R.id.mainHistoryView);

        //date wise history organizing
        historyRecyclerView=findViewById(R.id.historyRecyclerView);
        historyRecyclerYesterday = findViewById(R.id.historyRecyclerViewYesterday);
        historyRecyclerBeforeYesterday = findViewById(R.id.historyRecyclerBeforeYesterday);


        history = new ArrayList<>();
        historyYesterday = new ArrayList<>();
        historyBeforeYesterday = new ArrayList<>();
        allHistory=new ArrayList<>();

        expandableLayout = findViewById(R.id.expandable_layout);
        expandableLayoutYesterday = findViewById(R.id.expandable_layoutYesterday);
        expandableLayoutBeforeYesterday = findViewById(R.id.expandable_layoutBeforeYesterday);

        expandButtonToday = findViewById(R.id.expandButtonToday);
        expandButtonYesterday = findViewById(R.id.expandButtonYesterday);
        expandButtonbeforeYesterday = findViewById(R.id.expandButtonBeforeYesterday);

        dbHelper = new DBHelper(HistoryActivity.this);
        clearHistory = findViewById(R.id.clearhistoryBtn);
        editHistory = findViewById(R.id.edithistoryBtn);
        textCounter = findViewById(R.id.item_counter_text);
        selectedHistory = new ArrayList<>();

        currentDate = todaysDate();
        setupToolbar();
        yesterdayDate=getYesterdayDateString();

        loadListViewBeforeYesterday();
        loadListView();
        loadListViewYesterday();

        mergeAllHistory();


        expandButtonToday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableLayout.isExpanded()){
                    expandableLayout.collapse();
                    Drawable img = getResources().getDrawable(R.drawable.ic_expand_24);
                    img.setBounds(0, 0, 60, 60);
                    expandButtonToday.setCompoundDrawables(null, null, img, null);
                }else {
                    expandableLayout.expand();
                    expandableLayoutYesterday.collapse();
                    expandableLayoutBeforeYesterday.collapse();
                    Drawable img = getResources().getDrawable(R.drawable.ic_collapse_24);
                    img.setBounds(0, 0, 60, 60);
                    expandButtonToday.setCompoundDrawables(null, null, img, null);

                }

            }
        });


        expandButtonYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableLayoutYesterday.isExpanded()){
                    expandableLayoutYesterday.collapse();
                    Drawable img = getResources().getDrawable(R.drawable.ic_expand_24);
                    img.setBounds(0, 0, 60, 60);
                    expandButtonToday.setCompoundDrawables(null, null, img, null);
                }else {
                    expandableLayout.collapse();
                    expandableLayoutYesterday.expand();
                    expandableLayoutBeforeYesterday.collapse();
                    Drawable img = getResources().getDrawable(R.drawable.ic_collapse_24);
                    img.setBounds(0, 0, 60, 60);
                    expandButtonToday.setCompoundDrawables(null, null, img, null);

                }

            }
        });

        expandButtonbeforeYesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(expandableLayoutBeforeYesterday.isExpanded()){
                    expandableLayoutBeforeYesterday.collapse();
                    Drawable img = getResources().getDrawable(R.drawable.ic_expand_24);
                    img.setBounds(0, 0, 60, 60);
                    expandButtonToday.setCompoundDrawables(null, null, img, null);
                }else {
                    expandableLayout.collapse();
                    expandableLayoutYesterday.collapse();
                    expandableLayoutBeforeYesterday.expand();
                    Drawable img = getResources().getDrawable(R.drawable.ic_collapse_24);
                    img.setBounds(0, 0, 60, 60);
                    expandButtonToday.setCompoundDrawables(null, null, img, null);

                }
            }
        });

        clearHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (history.size() > 0) {

                    new MaterialAlertDialogBuilder(HistoryActivity.this, R.style.myAlertDialoge)
                            .setTitle("Clear History")
                            .setMessage("Do you really want to clear history?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    boolean is = dbHelper.deleteAllHistory();
                                    if (is) {
                                        loadListView();
                                    }
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();

                }
            }
        });


        editHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (history.size()>0) {
                    longClicked();
                }
            }
        });
    }

    private void mergeAllHistory() {
        allHistory.addAll(history);
        allHistory.addAll(historyYesterday);
        allHistory.addAll(historyBeforeYesterday);

    }


    private void setupToolbar() {

        textCounter.setText("History");
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_normal, menu);
        return true;
    }

    private void loadListView() {
        try {
            history = dbHelper.getHistoryByDate(currentDate);
            Collections.reverse(history);
        } catch (Exception ex) {

        }
        historyRecyclerAdapter=new HistoryRecyclerAdapter(this,history,this);
        historyRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerView.setAdapter(historyRecyclerAdapter);
    }

    private String getYesterdayDateString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);
        DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        String yesterdayAsString = dateFormat.format(calendar.getTime());
        return yesterdayAsString;
    }

    private void loadListViewYesterday() {
        try {
            historyYesterday = dbHelper.getHistoryByDate(yesterdayDate);
            Collections.reverse(historyYesterday);

        } catch (Exception ex) {

        }
        historyRecyclerAdapter = new HistoryRecyclerAdapter(this, historyYesterday,this);
        historyRecyclerYesterday.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerYesterday.setAdapter(historyRecyclerAdapter);
    }

    private void loadListViewBeforeYesterday(){
        try {
            historyBeforeYesterday = dbHelper.getHistoryBeforeYesterday(currentDate, yesterdayDate);
            Collections.reverse(historyBeforeYesterday);

        } catch (Exception ex) {

        }
        historyRecyclerAdapter=new HistoryRecyclerAdapter(this,historyBeforeYesterday,this);
        historyRecyclerBeforeYesterday.setLayoutManager(new LinearLayoutManager(this));
        historyRecyclerBeforeYesterday.setAdapter(historyRecyclerAdapter);

    }

    private String todaysDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());
        return todayDate;
    }


    private void longClicked() {
        isContextualModeEnabled = true;
        toolbar.getMenu().clear();
        toolbar.inflateMenu(R.menu.menu_contextual);
        historyRecyclerAdapter.notifyDataSetChanged();
        loadListView();
        loadListViewYesterday();
        loadListViewBeforeYesterday();
    }

    @Override
    public void onBackPressed() {
        if (isContextualModeEnabled) {
            isContextualModeEnabled = false;
            toolbar.getMenu().clear();
            toolbar.inflateMenu(R.menu.menu_normal);
            counter = 0;
            textCounter.setText("");
            setupToolbar();
            loadListView();
        } else {
            super.onBackPressed();
        }

    }

    public void updateCounter() {
        textCounter.setText(counter + " Items Selected");
    }

    public void checkSelection(View view, int i) {
        if (((CheckBox) view).isChecked()) {
            if (expandableLayout.isExpanded()){
                selectedHistory.add(history.get(i).getUrl());
            }else if (expandableLayoutYesterday.isExpanded()){
                selectedHistory.add(historyYesterday.get(i).getUrl());
            }else{
                selectedHistory.add(historyBeforeYesterday.get(i).getUrl());
            }
            counter++;
            updateCounter();
        } else {
            selectedHistory.remove(i);
            counter--;
            updateCounter();
        }

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_delete:
                if (selectedHistory.size() > 0) {
                    new MaterialAlertDialogBuilder(HistoryActivity.this, R.style.myAlertDialoge)
                            .setTitle("Delete Items")
                            .setMessage("Are you sure about this?")
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                                public void onClick(DialogInterface dialog, int whichButton) {
                                    deleteHistory();
                                    isContextualModeEnabled = false;
                                    toolbar.getMenu().clear();
                                    toolbar.inflateMenu(R.menu.menu_normal);
                                    counter = 0;
                                    selectedHistory.clear();
                                    setupToolbar();
                                    loadListView();
                                    loadListViewYesterday();
                                    loadListViewBeforeYesterday();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();


                }


                break;
            case R.id.action_settings:
                new MaterialAlertDialogBuilder(HistoryActivity.this, R.style.myAlertDialoge)
                        .setTitle("Share History")
                        .setMessage("Are you sure about this?")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(R.string.action_share, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                checkStoragePermissions();
                                String path = Data.getSaveDir() + "/exports/" + dbHelper.HISTORY_TABLE_NAME + ".csv";
                                File file = new File(path);
                                if (file.exists()) {
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == STORAGE_PERMISSION_CODE && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            exportDB();
        } else {
            Snackbar.make(mainView, R.string.permission_not_enabled, Snackbar.LENGTH_INDEFINITE).show();
        }
    }


    private void deleteHistory() {

        if (selectedHistory.size() > 0) {
            dbHelper.deleteHistorybyUrl(selectedHistory);
        }
    }

    private void checkStoragePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        } else {
            exportDB();
        }
    }

    private void exportDB() {

        DBHelper dbhelper = new DBHelper(getApplicationContext());
        File exportDir = new File(Data.getSaveDir(), "/exports");
        if (!exportDir.exists()) {
            exportDir.mkdirs();
        }

        File file = new File(exportDir, DBHelper.HISTORY_TABLE_NAME + ".csv");
        try {
            file.createNewFile();
            CSVWriter csvWrite = new CSVWriter(new FileWriter(file));
            SQLiteDatabase db = dbhelper.getReadableDatabase();
            Cursor curCSV = db.rawQuery("SELECT * FROM history", null);
            curCSV.moveToFirst();
            csvWrite.writeNext(curCSV.getColumnNames());
            while (curCSV.moveToNext()) {
                //Which column you want to exprort
                String arrStr[] = {curCSV.getString(0), curCSV.getString(1), curCSV.getString(2), curCSV.getString(3)};
                csvWrite.writeNext(arrStr);
            }
            csvWrite.close();
            curCSV.close();
        } catch (Exception sqlEx) {
            Log.e("MainActivity", sqlEx.getMessage(), sqlEx);
        }
    }

    @Override
    public void onHItemClick(int position) {

        if (expandableLayout.isExpanded()){
            HistoryModel historyModel=history.get(position);
           openLinkInMainActivity(historyModel);
        }else if (expandableLayoutYesterday.isExpanded()){
            HistoryModel historyModel=historyYesterday.get(position);
            openLinkInMainActivity(historyModel);
        }else{
            HistoryModel historyModel=historyBeforeYesterday.get(position);
            openLinkInMainActivity(historyModel);
        }
    }

    private void openLinkInMainActivity(HistoryModel historyModel) {
        Intent intent1=new Intent(HistoryActivity.this, WebViewActivity.class);
        intent1.putExtra("urllink",historyModel.getUrl());
        startActivity(intent1);
        finish();
    }

    @Override
    public void onHLongItemClick(int position) {
        longClicked();
    }
}