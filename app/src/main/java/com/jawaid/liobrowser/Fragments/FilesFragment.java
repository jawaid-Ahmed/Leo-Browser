package com.jawaid.liobrowser.Fragments;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import com.jawaid.liobrowser.Adapters.DownloadedFileAdapter;
import com.jawaid.liobrowser.BuildConfig;
import com.jawaid.liobrowser.Download.Data;
import com.jawaid.liobrowser.Interfaces.OnItemClickListener;
import com.jawaid.liobrowser.R;
import com.kaopiz.kprogresshud.KProgressHUD;

import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static com.jawaid.liobrowser.Activities.DownloadActivity.settingsbtn;


public class FilesFragment extends Fragment implements View.OnClickListener,OnItemClickListener {

    private LinearLayout videos,musics,documents,apks,zips,images;
    public static String currentUri;
    private LinearLayout filesMenu,filesList;
    private RecyclerView filesRecyclerView;
    private Button backBtnFiles;
    private List<File> downloadedFiles;

    private int videosfiles,musicfiles,imagesfiles,archives,documentsfiles,apksfiles;
    private DownloadedFileAdapter downloadedAdapter;
    private TextView noOfImages,noOfVideos,noOfArchives,noOfMusics,noOfApks,noOfDocs;
    private Handler handler,handler2;
    private String path;
    public Button refresh;
    private BottomSheetDialog bottomSheetDialog;
    private KProgressHUD progressDialog;

    String fireBrowserPath=Data.getSaveDir()+"/";
    String internalPath=Environment.getExternalStorageDirectory().getAbsolutePath();
    String externalPath="/storage/sdcard1/";

    private SharedPreferences sharedPref;
    private SharedPreferences.Editor editor;



    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_files,container,false);

        InitializtComponents(view);
        handler=new Handler();
        handler2=new Handler();
        downloadedFiles=new ArrayList<>();
        progressDialog = new KProgressHUD(getContext());




        sharedPref = getActivity().getSharedPreferences("downloadPrefs", Context.MODE_PRIVATE);
        editor=sharedPref.edit();

        path=fireBrowserPath;
        refresh();

        settingsbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                settingsMenu();
            }
        });


        return view;
    }


    private void settingsMenu() {
        bottomSheetDialog = new BottomSheetDialog(getContext(), R.style.BottomSheetDialog);
        View view = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_download_settings, null, false);
        refresh= view.findViewById(R.id.save_refresh_downloadbotmsheet);
        RadioGroup storageGroup=view.findViewById(R.id.storageGroup);
        RadioGroup threadsGroup=view.findViewById(R.id.threadsGroup);

        //storage radio buttons
        RadioButton storage=view.findViewById(R.id.check_internal);
        RadioButton sdcard=view.findViewById(R.id.check_sd_card);
        RadioButton firebrowser=view.findViewById(R.id.check_firebrowser);


        //downloading threads radio buttons
        RadioButton one=view.findViewById(R.id.check_one);
        RadioButton two=view.findViewById(R.id.check_two);
        RadioButton three=view.findViewById(R.id.check_three);
        RadioButton four=view.findViewById(R.id.check_four);
        RadioButton five=view.findViewById(R.id.check_five);

        firebrowser.setChecked(true);

        //downloading threeds prefs
        int val1=sharedPref.getInt("threads",4);
        if (val1==1){
            one.setChecked(true);
        }else if (val1==2){
            two.setChecked(true);
        }else if (val1==3){
            three.setChecked(true);
        }else if (val1==5){
            five.setChecked(true);
        }else{
            four.setChecked(true);
        }

        //storage radio group listener
        storageGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.check_sd_card:
                        path=externalPath;
                        break;
                    case R.id.check_internal:
                        path=internalPath;
                        break;
                    case R.id.check_firebrowser:
                        path=fireBrowserPath;
                        break;
                }
            }
        });

        //threads radio group listener
        threadsGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
        {
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId){
                    case R.id.check_one:
                        editor.putInt("threads",1);
                        editor.apply();
                        editor.commit();
                        one.setChecked(true);
                        break;
                    case R.id.check_two:
                        editor.putInt("threads",2);
                        editor.apply();
                        editor.commit();
                        two.setChecked(true);
                        break;
                    case R.id.check_three:
                        editor.putInt("threads",3);
                        editor.apply();
                        editor.commit();
                        three.setChecked(true);

                        break;
                    case R.id.check_four:
                        editor.putInt("threads",4);
                        editor.apply();
                        editor.commit();
                        four.setChecked(true);

                        break;
                    case R.id.check_five:
                        editor.putInt("threads",5);
                        editor.apply();
                        editor.commit();
                        five.setChecked(true);
                        break;
                }
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetDialog.dismiss();
               createLoadingDialoge();
                refresh();
            }
        });

        bottomSheetDialog.setContentView(view);
        bottomSheetDialog.show();

    }



    public void refresh() {
        imagesfiles=0;
        archives=0;
        videosfiles=0;
        musicfiles=0;
        apksfiles=0;
        documentsfiles=0;
        downloadedFiles.clear();
        noOfVideos.setText("loading...");
        noOfMusics.setText("loading...");
        noOfImages.setText("loading...");
        noOfDocs.setText("loading...");
        noOfArchives.setText("loading...");
        noOfApks.setText("loading...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        showFilesNumbers();
                    }
                });
            }
        }).start();
    }

    private void createLoadingDialoge() {
        progressDialog.create(getContext())
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        progressDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        //refresh();
    }

    private void showFilesNumbers() {
        loadArchives(path);
        loadApks(path);
        loadDocs(path);
        loadImages(path);
        loadMusic(path);
        loadVideos(path);
        noOfVideos.setText(" "+videosfiles);
        noOfMusics.setText(" "+musicfiles);
        noOfImages.setText(" "+imagesfiles);
        noOfDocs.setText(" "+documentsfiles);
        noOfArchives.setText(" "+archives);
        noOfApks.setText(" "+apksfiles);
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }

    }

    private void InitializtComponents(View view) {
        videos=view.findViewById(R.id.videosfilemanager);
        musics=view.findViewById(R.id.musicfilemanager);
        documents=view.findViewById(R.id.docsfilemanager);
        apks=view.findViewById(R.id.apksfilemanager);
        zips=view.findViewById(R.id.zipsfilemanager);
        images=view.findViewById(R.id.imagesfilemanager);
        filesMenu=view.findViewById(R.id.filesMenu);
        filesList=view.findViewById(R.id.filesList);
        filesRecyclerView=view.findViewById(R.id.recyclerViewFiles);
        backBtnFiles=view.findViewById(R.id.backBtnFiles);

        noOfApks=view.findViewById(R.id.noOfApks);
        noOfArchives=view.findViewById(R.id.noOfArchives);
        noOfDocs=view.findViewById(R.id.noOfDocs);
        noOfImages=view.findViewById(R.id.noOfImages);
        noOfMusics=view.findViewById(R.id.noOfMusics);
        noOfVideos=view.findViewById(R.id.noOfVideos);

        videos.setOnClickListener(this);
        musics.setOnClickListener(this);
        documents.setOnClickListener(this);
        apks.setOnClickListener(this);
        zips.setOnClickListener(this);
        images.setOnClickListener(this);
        backBtnFiles.setOnClickListener(this);

    }

    private void loadImages(String path){
        File directory=new File(path);
        if (!directory.exists()){
            directory.mkdir();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isFile() && file.getPath().endsWith(".jpg")
                        || file.getPath().endsWith(".jpeg")
                        || file.getPath().endsWith(".png")
                        || file.getPath().endsWith(".gif")
                        || file.getPath().endsWith(".tiff")) {
                    downloadedFiles.add(file);
                    imagesfiles++;
                }if (file.isDirectory()){
                    loadImages(file.getAbsolutePath());
                }
            }
        }
    }
    private void loadArchives(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isFile() && file.getPath().endsWith(".zip")
                        || file.getPath().endsWith(".tar")
                        || file.getPath().endsWith(".rar")) {
                    downloadedFiles.add(file);
                    archives++;
                }else if (file.isDirectory()){
                    loadArchives(file.getAbsolutePath());
                }
            }
        }
    }
    private void loadApks(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (file.isFile() && file.getPath().endsWith(".apk")) {
                    downloadedFiles.add(file);
                    apksfiles++;
                }else if (file.isDirectory()){
                    loadApks(file.getAbsolutePath());
                }
            }
        }
    }
    private void loadMusic(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (!file.isDirectory() && file.getPath().contains(".mp3")|| file.getPath().contains(".wav")) {
                    downloadedFiles.add(file);
                    musicfiles++;
                }else if (file.isDirectory()){
                    loadMusic(file.getAbsolutePath());
                }
            }
        }
    }
    private void loadDocs(String path) {

        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (!file.isDirectory() && file.getPath().contains(".txt")
                        || file.getPath().contains(".pdf")
                        || file.getPath().contains(".ppt")
                        || file.getPath().contains(".pptx")
                        || file.getPath().contains(".rtf")
                        || file.getPath().contains(".doc")
                        || file.getPath().contains(".docx")
                        || file.getPath().contains(".xls")
                        || file.getPath().contains(".xlsx")) {
                    downloadedFiles.add(file);
                    documentsfiles++;
                }else if (file.isDirectory()){
                    loadDocs(file.getAbsolutePath());
                }
            }
        }
    }
    private void loadVideos(String path) {
        File directory = new File(path);
        if (!directory.exists()) {
            directory.mkdir();
        }
        if (directory.exists()) {
            for (File file : directory.listFiles()) {
                if (!file.isDirectory() && file.getPath().contains(".3gp")
                        || file.getPath().contains(".mpg")
                        || file.getPath().contains(".mpeg")
                        || file.getPath().contains(".flv")
                        || file.getPath().contains(".mkv")
                        || file.getPath().contains(".mpe")
                        || file.getPath().contains(".mp4")
                        || file.getPath().contains(".avi")) {
                    downloadedFiles.add(file);
                    videosfiles++;
                }else if (file.isDirectory()){
                    loadVideos(file.getAbsolutePath());
                }
            }
        }
    }

    private void deleteTempFolder(String dir) {
        File myDir = new File(Data.getSaveDir());
        if (myDir.isDirectory()) {
            String[] children = myDir.list();
            for (int i = 0; i < children.length; i++) {
                new File(myDir, children[i]).delete();
            }
        }
    }

    private void refreshRecycler() {
        filesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        filesRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        downloadedAdapter = new DownloadedFileAdapter(downloadedFiles, getContext(), this);
        filesRecyclerView.setAdapter(downloadedAdapter);
        filesRecyclerView .setVisibility(View.VISIBLE);
    }

    private void showToast(String tost){
        Toast.makeText(getContext(),tost+" ",Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.videosfilemanager:
                filesMenu.setVisibility(View.GONE);
                downloadedFiles.clear();
                downloadedFiles=new ArrayList<>();
                loadVideos(path);
                refreshRecycler();
                filesList.setVisibility(View.VISIBLE);
                break;
            case R.id.imagesfilemanager:
                filesMenu.setVisibility(View.GONE);
                downloadedFiles.clear();
                downloadedFiles=new ArrayList<>();
                loadImages(path);
                refreshRecycler();
                filesList.setVisibility(View.VISIBLE);
                break;
            case R.id.musicfilemanager:
                filesMenu.setVisibility(View.GONE);
                downloadedFiles.clear();
                downloadedFiles=new ArrayList<>();
                loadMusic(path);
                refreshRecycler();
                filesList.setVisibility(View.VISIBLE);
                break;
            case R.id.docsfilemanager:
                filesMenu.setVisibility(View.GONE);
                downloadedFiles.clear();
                downloadedFiles=new ArrayList<>();
                loadDocs(path);
                refreshRecycler();
                filesList.setVisibility(View.VISIBLE);
                break;
            case R.id.apksfilemanager:
                filesMenu.setVisibility(View.GONE);
                downloadedFiles.clear();
                downloadedFiles=new ArrayList<>();
                loadApks(path);
                refreshRecycler();
                filesList.setVisibility(View.VISIBLE);
                break;
            case R.id.zipsfilemanager:
                filesMenu.setVisibility(View.GONE);
                downloadedFiles.clear();
                downloadedFiles=new ArrayList<>();
                loadArchives(path);
                refreshRecycler();
                filesList.setVisibility(View.VISIBLE);
                break;
            case R.id.backBtnFiles:
                filesList.setVisibility(View.GONE);
                refreshRecycler();
                filesMenu.setVisibility(View.VISIBLE);

        }

    }
    @Override
    public void onItemClick(int position) {
        String url = downloadedFiles.get(position).getPath();
        if (url.endsWith(".apk")) {
            Intent intent;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri apkUri = FileProvider.getUriForFile(getContext(), BuildConfig.APPLICATION_ID + ".provider", new File(url));
                intent = new Intent(Intent.ACTION_INSTALL_PACKAGE);
                intent.setData(apkUri);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(Intent.EXTRA_NOT_UNKNOWN_SOURCE, true);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.setData(apkUri);
                getContext().startActivity(intent);
            } else {
               // intent = new Intent(Intent.ACTION_VIEW,Uri.parse(url));
                //intent.setDataAndType(Uri.parse(url), "application/vnd.android.package-archive");
                //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.setDataAndType(Uri.parse(url),
                        "application/*");

            }
            getContext().startActivity(intent);
        }
        else if (url.endsWith("zip")) {
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "application/zip");
                startActivity(intent);
            } catch (ActivityNotFoundException Ae) {
                Uri uri = Uri.parse("market://search?q=" + "application/zip");
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "You don't have Google Play installed", Toast.LENGTH_LONG).show();
                }
            }
        } else {
            try {
                openFile(url);
            } catch (ActivityNotFoundException Ae) {
                Uri uri = Uri.parse("market://search?q=" + "application/zip");
                Intent myAppLinkToMarket = new Intent(Intent.ACTION_VIEW, uri);
                try {
                    startActivity(myAppLinkToMarket);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(getContext(), "You don't have Google Play installed", Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void openFile(String url) {
        Uri uri = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        if (url.toString().contains(".doc") || url.toString().contains(".docx")) {
            // Word document
            intent.setDataAndType(uri, "application/msword");
        } else if (url.toString().contains(".pdf")) {
            // PDF file
            intent.setDataAndType(uri, "application/pdf");
        } else if (url.toString().contains(".ppt") || url.toString().contains(".pptx")) {
            // Powerpoint file
            intent.setDataAndType(uri, "application/vnd.ms-powerpoint");
        } else if (url.toString().contains(".xls") || url.toString().contains(".xlsx")) {
            // Excel file
            intent.setDataAndType(uri, "application/vnd.ms-excel");
        } else if (url.toString().contains(".zip") || url.toString().contains(".rar")) {
            // WAV audio file
            intent.setDataAndType(uri, "application/x-wav");
        } else if (url.toString().contains(".rtf")) {
            // RTF file
            intent.setDataAndType(uri, "application/rtf");
        } else if (url.toString().contains(".wav") || url.toString().contains(".mp3")) {
            // WAV audio file
            intent.setDataAndType(uri, "audio/*");

        } else if (url.toString().contains(".gif")) {
            // GIF file
            intent.setDataAndType(uri, "image/gif");
        } else if (url.toString().contains(".jpg") || url.toString().contains(".jpeg")
                ||url.toString().contains(".svg")|| url.toString().contains(".png")) {
            // JPG file
            intent.setDataAndType(uri, "image/jpeg");
        } else if (url.toString().contains(".txt")) {
            // Text file
            intent.setDataAndType(uri, "text/plain");
        } else if (url.toString().contains(".3gp") || url.toString().contains(".mpg") ||
                url.toString().contains(".mpeg") ||url.toString().contains(".flv") ||url.toString().contains(".mkv") ||
                url.toString().contains(".mpe") || url.toString().contains(".mp4") || url.toString().contains(".avi")) {
            // Video files
            intent.setDataAndType(uri, "video/*");

        }
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }

    @Override
    public void onLongItemClick(int position) {
        new MaterialAlertDialogBuilder(getContext(), R.style.myAlertDialoge)
                .setMessage(R.string.delete_title + downloadedFiles.get(position).getName())
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        if (deleteFile(downloadedFiles.get(position))) {
                            Toast.makeText(getContext(), "File Deleted", Toast.LENGTH_SHORT).show();
                            refreshRecycler();
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, null)
                .show();

    }

    private boolean deleteFile(File file) {
        boolean isdeleted = file.delete();
        if (isdeleted)
            return true;
        return false;
    }
}
