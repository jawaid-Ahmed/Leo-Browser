package com.jawaid.liobrowser.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.tabs.TabLayout;
import com.jawaid.liobrowser.Adapters.ViewPagerFragmentAdapter;
import com.jawaid.liobrowser.Adapters.ZoomOutPageTransformer;
import com.jawaid.liobrowser.Databases.FbPrefs;
import com.jawaid.liobrowser.Fragments.DownloadFragment;
import com.jawaid.liobrowser.Fragments.FilesFragment;
import com.jawaid.liobrowser.R;

public class DownloadActivity extends AppCompatActivity {


    public static ViewPager2 viewPager;

    private ViewPagerFragmentAdapter pagerAdapter;
    private TabLayout tabLayout;
    public static SwitchCompat networkSwitch;
    private Button btnDownloading,btnDownloaded;
    private Toolbar toolbar;
    private TextView toolbarTitle;
    public static ImageButton settingsbtn;
    public static FbPrefs fbPrefs;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);

        toolbar=findViewById(R.id.toolbar_download);
        toolbarTitle=findViewById(R.id.item_counter_text);
        setSupportActionBar(toolbar);
        settingsbtn=findViewById(R.id.settingsDownload);


        toolbarTitle.setText("Downloads");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        networkSwitch= findViewById(R.id.networkSwitch);
         btnDownloading=findViewById(R.id.downloadingTab);
         btnDownloaded=findViewById(R.id.downloadedTab);
         fbPrefs=new FbPrefs(this);


        tabLayout =findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.pager);

        pagerAdapter = new ViewPagerFragmentAdapter(this);
        pagerAdapter.addFragment(new DownloadFragment());
        pagerAdapter.addFragment(new FilesFragment());

        viewPager.setPageTransformer(new ZoomOutPageTransformer());
        viewPager.setAdapter(pagerAdapter);

        viewPager.setUserInputEnabled(false);

        btnDownloading.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnDownloaded.setTextColor(getApplication().getResources().getColor(R.color.whiteColor));
                btnDownloading.setTextColor(getApplication().getResources().getColor(R.color.colorPurple));
                viewPager.setCurrentItem(0,true);

            }
        });
        btnDownloaded.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewPager.setCurrentItem(1,true);
                btnDownloading.setTextColor(getApplication().getResources().getColor(R.color.whiteColor));
                btnDownloaded.setTextColor(getApplication().getResources().getColor(R.color.colorPurple));

            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }



    private void showToast(String storage) {
        Toast.makeText(this, storage+" ", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        if (viewPager.getCurrentItem() == 0) {
//            super.onBackPressed();
//        } else {
//            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
//        }

    }
}