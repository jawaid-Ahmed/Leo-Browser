package com.jawaid.liobrowser.Activities;

import android.os.Bundle;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.jawaid.liobrowser.Databases.FbPrefs;
import com.jawaid.liobrowser.R;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Parcelable;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.PermissionRequest;
import android.webkit.SslErrorHandler;
import android.webkit.URLUtil;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.jawaid.liobrowser.Adapters.AdapterArticlesAll;
import com.jawaid.liobrowser.Adapters.AdapterRecycler;
import com.jawaid.liobrowser.ApiClient.ApiClientCall;
import com.jawaid.liobrowser.Databases.DBDownload;
import com.jawaid.liobrowser.Databases.DBHelper;
import com.jawaid.liobrowser.Databases.DBUtility;
import com.jawaid.liobrowser.Databases.DB_Bookmark;
import com.jawaid.liobrowser.Databases.FileUtils;
import com.jawaid.liobrowser.Download.Data;
import com.jawaid.liobrowser.Helpers.DownPicUtil;
import com.jawaid.liobrowser.InterceptUrls.UrlCache;
import com.jawaid.liobrowser.Interfaces.ApiInterface;
import com.jawaid.liobrowser.Interfaces.OnHorizontalItemClickListener;
import com.jawaid.liobrowser.Interfaces.OnItemClickListener;
import com.jawaid.liobrowser.TabImplementation.CardDataImpl;
import com.jawaid.liobrowser.TabImplementation.ECBackgroundSwitcherView;
import com.jawaid.liobrowser.TabImplementation.ECCardData;
import com.jawaid.liobrowser.TabImplementation.ECPagerView;
import com.jawaid.liobrowser.TabImplementation.ECPagerViewAdapter;
import com.jawaid.liobrowser.TabImplementation.ItemsCountView;
import com.jawaid.liobrowser.models.Article;
import com.jawaid.liobrowser.models.News;
import com.jawaid.liobrowser.models.fSettings;
import com.kaopiz.kprogresshud.KProgressHUD;
import com.monstertechno.adblocker.AdBlockerWebView;
import com.monstertechno.adblocker.util.AdBlocker;


import net.cachapa.expandablelayout.ExpandableLayout;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.webkit.WebSettingsCompat;
import androidx.webkit.WebViewFeature;
import im.delight.android.webview.AdvancedWebView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class WebViewActivity extends Activity implements RewardedVideoAdListener,AdvancedWebView.Listener, View.OnClickListener, OnItemClickListener, OnHorizontalItemClickListener{


    private static final int FILE_SELECT_CODE = 0;
    //news api components
    private TextView textView;
    private String API_KEY = "ea7b66d8481c44e88ddeb4d4e03ae132";
    public static final String USER_AGENT = "Mozilla/5.0 (Linux; Android 4.1.1; Galaxy Nexus Build/JRO03C) AppleWebKit/535.19 (KHTML, like Gecko) Chrome/18.0.1025.166 Mobile Safari/535.19";

    private List<Article> articles = new ArrayList<>();
    private List<Article> articlesall = new ArrayList<>();
    private RecyclerView recyclerView, recyclerView2;
    private AdapterRecycler adapterRecycler;
    private AdapterArticlesAll adapterArticlesAll;

    private ProgressBar downloadResourcesProgress;


    private ECPagerView ecPagerView;
    public static ImageButton backArrow, forwordArrow, homeButton, menubtn, navBtn;
    private ImageButton addTab, removeTab;
    public static boolean DARK_MODE;
    private TextView darkMode_text;


    private List<ECCardData> dataset;
    private ECPagerViewAdapter adapter;
    private LinearLayout addRemoveButtonslayout;
    private boolean expanded = true;

    private AdvancedWebView mWebView;

    private DBHelper dbHelper;
    private DB_Bookmark db_bookmark;
    String addbookmarkTitle = "";
    private RelativeLayout noInternetLayout;
    private DBDownload dbDownloadHelper;
    private BottomSheetDialog bottomSheetDialog;
    private Handler handler;
    private Bitmap fevicon;
    private byte[] feviconByte;


    private boolean allowAnonymous = false;
    private int fragmentState = 0;
    private LinearLayout mainHomelayout;
    private ImageView homeBitmap;
    private String searchEngineQuery = "https://www.google.com/search?q=";


    private EditText searchBoxHome;
    private ImageButton twiter, yahoo, spotify, gmail, pinterest, amazon;
    private ImageButton youtube, google,  gamingbtn, crickbuzz, musicbtn, duckduckgo;

    private ImageButton drawableLeft, drawableRight, incognito, deskTopMode;
    private String currentDate;
    private boolean readView;

    private ExtendedFloatingActionButton floatingActionButton;
    private View toplayout;
    private View bottomLayout;
    private ScheduledExecutorService scheduleTaskExecutor;

    private FirebaseAnalytics mFirebaseAnalytics;

    private boolean darkMode;
    private BottomSheetDialog toolsbottomSheetDialog;
    private ItemsCountView itemsCountView;

    private Button expandableButtonHome;
    private ExpandableLayout expandableLayouthome;
    private Bitmap mainHomelayoutbitmap;
    private boolean isDesktopMode;
    private boolean onbackloading;
    private RelativeLayout rootView;
    private int numberOfAdsBlocked = 0;
    private TextView noOfAdsTextView;
    private AlertDialog webPopup;

    private String lastUrl;
    private SharedPreferences settingsPrefs;
    private FbPrefs settings;
    private SharedPreferences.Editor editorSettings;

    //upload functions
    private static final int FILECHOOSER_RESULTCODE = 22;
    private ValueCallback<Uri> mUploadMessage;
    private ValueCallback<Uri[]> mUploadMessages;
    private Uri mCapturedImageURI = null;
    private KProgressHUD progressDialog;
    private PermissionRequest myRequest;
    private String articlesTag="technology";

    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_view);

        rootView = findViewById(R.id.rootViewhome);
        //bottom navigation buttons
        backArrow = findViewById(R.id.backarrow);
        forwordArrow = findViewById(R.id.forwordarrow);
        homeButton = findViewById(R.id.homebtn);
        menubtn = findViewById(R.id.menubtn);
        navBtn = findViewById(R.id.navBtn);
        addTab = findViewById(R.id.addCard);
        removeTab = findViewById(R.id.removeCard);
        addRemoveButtonslayout = findViewById(R.id.addRemoveButtons);

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);


        //ca-app-pub-1582220138871006~8600279286
        //Rewarded Ad Implementation
        MobileAds.initialize(this, "ca-app-pub-1582220138871006~8600279286");
        mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        mRewardedVideoAd.setRewardedVideoAdListener(this);
        //Interstitial Ads Implementation
        //ca-app-pub-1582220138871006/2286152377
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-1582220138871006/2286152377");


        dbHelper = new DBHelper(this);
        db_bookmark = new DB_Bookmark(this);


        currentDate = todaysDate();

        //card implementation
        ecPagerView = findViewById(R.id.ec_pager_element);
        ecPagerView.setAttributes(300, 550, 0);
        dataset = CardDataImpl.generateExampleData();
        fevicon = BitmapFactory.decodeResource(getResources(), R.drawable.global);
        feviconByte = DBUtility.bitmapToByte(fevicon);
        handler = new Handler();
        bottomLayout = findViewById(R.id.bottom_nav);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        refreshCards();







        ecPagerView.setBackgroundSwitcherView((ECBackgroundSwitcherView) findViewById(R.id.ec_bg_switcher_element));
        addTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dataset.add(new CardDataImpl("newCard"));
                adapter.notifyDataSetChanged();
            }
        });


        adapter.notifyDataSetChanged();



    }

    private void toggleView(ImageView bitmap) {
        if (expanded) {
            expandableLayouthome.collapse();
            ecPagerView.collapse();
            addRemoveButtonslayout.setVisibility(View.VISIBLE);
            expanded = false;
            if (mainHomelayout.getVisibility() == View.VISIBLE) {
                bitmap.setImageBitmap(mainHomelayoutbitmap);
            } else {
                bitmap.setBackgroundColor(getResources().getColor(R.color.darkColor));
            }
            bitmap.setVisibility(View.VISIBLE);

        } else {
            bitmap.setVisibility(View.GONE);
            ecPagerView.expand();
            addRemoveButtonslayout.setVisibility(View.GONE);
            expanded = true;


        }
    }


    private String todaysDate() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        DateFormat dateFormat = new SimpleDateFormat("d MMM yyyy", Locale.getDefault());
        String todayDate = dateFormat.format(calendar.getTime());
        return todayDate;
    }

    private void hideView(View view) {
        view.animate()
                .translationY(0)
                .setDuration(2000)
                .alpha(0.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.GONE);
                    }
                });
    }

    private void showView(View view) {
        view.animate()
                .translationY(0)
                .setDuration(1000)
                .alpha(1.0f)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        view.setVisibility(View.VISIBLE);
                    }
                });
    }

    private void refreshCards() {
        adapter = new ECPagerViewAdapter(this, dataset) {
            @Override
            public void instantiateCard(LayoutInflater inflaterService, ViewGroup head, ECCardData data) {


                View view = inflaterService.inflate(data.getHeadBackgroundResource(), head, false);
                toplayout = view.findViewById(R.id.edittextdrawableslayoutmain);
                floatingActionButton = view.findViewById(R.id.extended_fab);


                mWebView = view.findViewById(R.id.advancedWebView);
                InitializeComponents(view);
                setupWebView(mWebView);

                mainHomelayoutbitmap = loadBitmapFromView(WebViewActivity.this, mainHomelayout);

                floatingActionButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showView(toplayout);
                        showView(bottomLayout);
                        hideView(floatingActionButton);
                    }
                });
                head.removeAllViews();
                head.addView(view);
                try {
                    String url = getIntent().getStringExtra("urllink");
                    if (url!=null && url.length()>0){
                        loadWebView(url);
                    }
                }catch (Exception ex){

                }
                handleIntents();


                //downloading threeds prefs
                articlesTag=settings.getString("articlesTag");
                if (articlesTag.equals("")||articlesTag==null|| !(articlesTag.length()>0)){
                    articlesTag=getCountry();
                }
                String val1=settings.getString("articles");
                if (val1.equals("none")){

                }else if (val1.equals("horizontle")){
                    loadArticlesHorizontal(articlesTag);
                }else if (val1.equals("verticle")){
                    loadArticlesVerticle();
                }else{
                    loadArticlesVerticle();
                    loadArticlesHorizontal(articlesTag);
                }




                removeTab.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //dataset.remove(adapter.getPosition());
                        if (adapter.getCount() == 1) {
                            adapter.getDataset().remove(adapter.getPosition());
                            adapter.getDataset().add(new CardDataImpl("Home"));
                            adapter.notifyDataSetChanged();
                            ecPagerView.setPagerViewAdapter(adapter);
                        } else {
                            adapter.getDataset().remove(adapter.getPosition());
                            adapter.notifyDataSetChanged();
                            ecPagerView.setPagerViewAdapter(adapter);
                        }


                    }
                });

                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                ecPagerView.expand();
                            }
                        });
                    }
                }).start();


                ImageView bitmap = view.findViewById(R.id.bitmapHome);

                bitmap.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleView(bitmap);
                    }
                });
                navBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        toggleView(bitmap);
                    }
                });


            }
        };
        ecPagerView.setPagerViewAdapter(adapter);

        itemsCountView = (ItemsCountView) findViewById(R.id.items_count_view);
        ecPagerView.setOnCardSelectedListener(new ECPagerView.OnCardSelectedListener() {
            @Override
            public void cardSelected(int newPosition, int oldPosition, int totalElements) {
                itemsCountView.update(newPosition, oldPosition, totalElements);
            }
        });


    }


    private void executeScheduler() {
        scheduleTaskExecutor = Executors.newScheduledThreadPool(50);
        //Schedule a task to run every 5 seconds (or however long you want)
        scheduleTaskExecutor.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                // Do stuff here!

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (mInterstitialAd.isLoaded()){
                            mInterstitialAd.show();
                        }
                        loadRewardedVideoAd();
                    }
                });

            }
        }, 9, 9, TimeUnit.MINUTES);
    }

    private void InitializeComponents(View view) {

        mainHomelayout = view.findViewById(R.id.homeLayout);
        noInternetLayout = view.findViewById(R.id.noInternetLogo);
        homeBitmap = view.findViewById(R.id.bitmapHome);


        expandableLayouthome = view.findViewById(R.id.expandable_layouthome);
        expandableButtonHome = view.findViewById(R.id.expandButtonhome);


        //loading articles recyclerview setup
        //news api components
        textView = view.findViewById(R.id.text_view_result);
        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView2 = view.findViewById(R.id.recyclerView2);
        //recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        recyclerView.setNestedScrollingEnabled(false);
        recyclerView2.setNestedScrollingEnabled(false);


        youtube = view.findViewById(R.id.youtubebtn);
        twiter = view.findViewById(R.id.twiterbtn);
        amazon = view.findViewById(R.id.amazon);
        google = view.findViewById(R.id.googlebtn);
        yahoo = view.findViewById(R.id.yahoobtn);
        spotify = view.findViewById(R.id.spotifybtn);
        gmail = view.findViewById(R.id.gmailbtn);
        pinterest = view.findViewById(R.id.pinterestbtn);
        searchBoxHome = view.findViewById(R.id.search_home);
        searchBoxHome.setSelectAllOnFocus(true);

        drawableLeft = view.findViewById(R.id.drawableLeft);
        drawableRight = view.findViewById(R.id.drawableRight);
        gamingbtn = view.findViewById(R.id.gamingbtn);
        musicbtn = view.findViewById(R.id.musicbtn);
        crickbuzz = view.findViewById(R.id.crickbuzzbtn);
        duckduckgo = view.findViewById(R.id.entertainmentbtn);


        gamingbtn.setOnClickListener(this);
        musicbtn.setOnClickListener(this);
        crickbuzz.setOnClickListener(this);
        duckduckgo.setOnClickListener(this);
        youtube.setOnClickListener(this);
        twiter.setOnClickListener(this);
        amazon.setOnClickListener(this);
        google.setOnClickListener(this);
        yahoo.setOnClickListener(this);
        gmail.setOnClickListener(this);
        spotify.setOnClickListener(this);
        pinterest.setOnClickListener(this);
        searchBoxHome.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                String query = searchBoxHome.getText().toString().trim();
                if (query != "") {
                    loadWebView(query);
                }
                return true;
            }
        });
        backArrow.setOnClickListener(this);
        forwordArrow.setOnClickListener(this);
        menubtn.setOnClickListener(this);
        homeButton.setOnClickListener(this);

        drawableRight.setOnClickListener(this);
        drawableLeft.setOnClickListener(this);

        progressDialog = new KProgressHUD(this);


        expandableButtonHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableLayoutToggle(expandableLayouthome, expandableButtonHome);
            }
        });

    }

    private void expandableLayoutToggle(ExpandableLayout expandableLayout, Button expandButtonToday) {
        if (expandableLayout.isExpanded()) {
            expandableLayout.collapse();
            Drawable img = getResources().getDrawable(R.drawable.ic_expand_24);
            img.setBounds(0, 0, 60, 60);
            expandButtonToday.setCompoundDrawables(null, null, img, null);
        } else {
            expandableLayout.expand();
            Drawable img = getResources().getDrawable(R.drawable.ic_collapse_24);
            img.setBounds(0, 0, 60, 60);
            expandButtonToday.setCompoundDrawables(null, null, img, null);
        }

    }


    private void setAnonymous(boolean anonymous, AdvancedWebView webView) {
        CookieManager.getInstance().setAcceptCookie(anonymous);
        webView.getSettings().setAppCacheEnabled(anonymous);
        webView.clearHistory();
        webView.clearCache(anonymous);
        webView.clearFormData();
        webView.getSettings().setSavePassword(anonymous);
        webView.getSettings().setSaveFormData(anonymous);
        allowAnonymous = anonymous;
    }

    private void handleIntents() {
        String intentUrl = null;
        Intent intent = getIntent();
        Uri uri = intent.getData();
        try {
            intentUrl = uri.toString();
            if (intentUrl != null) {
                loadWebView(intentUrl);
            }
        } catch (Exception ex) {

        }
    }

    private void setupWebView(AdvancedWebView webView) {
        webView.setWebChromeClient(new WebViewActivity.ChromeClient());
        webView.setListener(WebViewActivity.this, this);
        webView.setWebViewClient(new WebViewActivity.MyWebClient(this));
        webView.addHttpHeader("X-Requested-With", "");
        webView.addHttpHeader("Referer", "httpReferer");
        new AdBlockerWebView.init(this).initializeWebView(webView);


        //webview extra settings
        webView.setScrollBarStyle(View.SCROLLBARS_INSIDE_OVERLAY);
        webView.setLayerType(View.LAYER_TYPE_HARDWARE, null);
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setAllowFileAccess(true);
        webSettings.setUserAgentString(USER_AGENT);
        webSettings.setRenderPriority(WebSettings.RenderPriority.HIGH);
        webSettings.setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);
        webSettings.setAppCacheEnabled(true);
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true);
        webSettings.setMediaPlaybackRequiresUserGesture(false);
        webSettings.setSupportMultipleWindows(false);

        webSettings.setDomStorageEnabled(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        webSettings.setUseWideViewPort(true);
        webSettings.setEnableSmoothTransition(true);
        webSettings.setAllowContentAccess(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setPluginState(WebSettings.PluginState.ON);
        webView.setMixedContentAllowed(true);
        webView.setCookiesEnabled(true);
        webView.setThirdPartyCookiesEnabled(true);
        webView.setHorizontalScrollBarEnabled(false);
        webView.setMixedContentAllowed(true);
        webView.setDesktopMode(false);

        webSettings.setLoadWithOverviewMode(true);


        if (WebViewFeature.isFeatureSupported(WebViewFeature.FORCE_DARK)) {
            int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
            if (nightModeFlags == Configuration.UI_MODE_NIGHT_YES) {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_ON);
            } else {
                WebSettingsCompat.setForceDark(webSettings, WebSettingsCompat.FORCE_DARK_OFF);

            }
        } else {
            showToast("Sorry Web Dark Not Supported");
        }

        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {

                final WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
                if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                        hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

                    String imageUrl = hitTestResult.getExtra();
                    createPopupDialoge(imageUrl);
                }


                return true;
            }

        });

        settingsPrefs = this.getSharedPreferences(fSettings.SETTINGS_PREF, MODE_PRIVATE);
        editorSettings = settingsPrefs.edit();
        settings=new FbPrefs(this);

        prefsSettings(webSettings, webView);

        try {
            String url2 = settingsPrefs.getString("lasturl", null);
            if (url2.length() > 2) {
                loadWebView(url2);
            }
        } catch (Exception ex) {

        }

    }

    private void prefsSettings(WebSettings webSettings, AdvancedWebView webView) {

        boolean saveForms = settingsPrefs.getBoolean(fSettings.FORMS_DATA, true);
        boolean savePasswords = settingsPrefs.getBoolean(fSettings.PASSWORD_DATA, true);
        boolean displayZoomControls = settingsPrefs.getBoolean(fSettings.DESPLAY_ZOOM_CONTROLS, false);
        boolean loadImagesAuto = settingsPrefs.getBoolean(fSettings.LOAD_IMAGES_AUTO, true);

        webSettings.setDisplayZoomControls(displayZoomControls);
        webSettings.setSaveFormData(saveForms);
        webSettings.setSavePassword(savePasswords);
        webView.getSettings().setLoadsImagesAutomatically(loadImagesAuto);


    }


    private void createPopupDialoge(String imageUrl) {

        String[] items = new String[]{"Download", "Save", "Share"};


        MaterialAlertDialogBuilder dialoge = new MaterialAlertDialogBuilder(WebViewActivity.this, R.style.myAlertDialoge)
                .setTitle("Popup")
                .setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {


                        switch (i) {
                            case 0:
                            case 1:
                                downloadImage(imageUrl);
                                break;
                            case 2:
                                shareImage();
                                break;

                        }
                    }
                });

        dialoge.show();
    }

    private void shareImage() {
        final WebView.HitTestResult hitTestResult = mWebView.getHitTestResult();
        if (hitTestResult.getType() == WebView.HitTestResult.IMAGE_TYPE ||
                hitTestResult.getType() == WebView.HitTestResult.SRC_IMAGE_ANCHOR_TYPE) {

            String imageUrl = hitTestResult.getExtra();
            Intent sharei = new Intent(android.content.Intent.ACTION_SEND);
            sharei.setType("text/plain");
            sharei.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            sharei.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
            sharei.putExtra(Intent.EXTRA_TEXT, imageUrl);
            startActivity(Intent.createChooser(sharei, "Share link!"));


        }
    }

    private void downloadImage(String imageUrl) {


        CookieSyncManager.createInstance(WebViewActivity.this);
        CookieSyncManager.getInstance().sync();
        CookieManager cookieManager = CookieManager.getInstance();
        String cookie = cookieManager.getCookie(mWebView.getUrl());
        DownPicUtil.downPic(imageUrl, USER_AGENT, mWebView.getUrl(), cookie, new DownPicUtil.DownFinishListener() {

            @Override
            public void onDownFinish(String path) {
                showToast("Image Downloaded " + Data.getSaveDir() + "/Images");
                getApplicationContext().sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" + path)));
            }

            @Override
            public void onError() {
                showToast("Error Downloading");
            }
        });

    }


    private void defaultSearchEngine() {
        Dialog dialog = new Dialog(this, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
        View view = LayoutInflater.from(this).inflate(R.layout.search_engine_layout, null, false);

        Button google = view.findViewById(R.id.googleEngine);
        Button yahoo = view.findViewById(R.id.yahoo_engine);
        Button baidu = view.findViewById(R.id.baiduengine);
        Button bing = view.findViewById(R.id.bingengine);
        Button duckgo = view.findViewById(R.id.duck_duck_GoEngine);
        Button wiki = view.findViewById(R.id.wikipediaEngine);
        Button ask = view.findViewById(R.id.askEngine);


        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.goog);
                searchEngineQuery = "https://www.google.com/search?q=";
                dialog.dismiss();

            }
        });
        duckgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.duckduckgo);
                searchEngineQuery = " https://duckduckgo.com/?q=";
                dialog.dismiss();

            }
        });
        wiki.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.wikipedia);
                searchEngineQuery = "https://en.m.wikipedia.org/wiki/";
                dialog.dismiss();

            }
        });
        yahoo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.yahoo_engine);
                searchEngineQuery = "http://intl.m.yahoo.com/w/search?q=";
                dialog.dismiss();

            }
        });
        bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.bing_engine);
                searchEngineQuery = "http://www.bing.com/search?q=";
                dialog.dismiss();

            }
        });
        baidu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.baidu_engine);
                searchEngineQuery = "https://www.baidu.com/s?wd=";
                dialog.dismiss();

            }
        });
        ask.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawableLeft.setImageResource(R.drawable.ask_engine);
                searchEngineQuery = "https://www.ask.com/youtube?q=";
                dialog.dismiss();

            }
        });

        dialog.setContentView(view);
        dialog.create();
        dialog.show();
    }

    private void showToast(String s) {
        Toast.makeText(this, s + "", Toast.LENGTH_SHORT).show();
    }

    public static Bitmap loadBitmapFromView(Context context, View v) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        v.measure(View.MeasureSpec.makeMeasureSpec(dm.widthPixels, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(dm.heightPixels, View.MeasureSpec.EXACTLY));
        v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
        Bitmap returnedBitmap = Bitmap.createBitmap(v.getMeasuredWidth(),
                v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(returnedBitmap);
        v.draw(c);
        return returnedBitmap;
    }

    private boolean isNetworkConnected() {
        ConnectivityManager conMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMgr.getActiveNetworkInfo() != null) {
            NetworkInfo activeInfo = conMgr.getActiveNetworkInfo();
            if (!activeInfo.isConnected() || !activeInfo.isAvailable()) {
                return false;
            }
            return true;
        }
        return false;

//        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
//
//        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnected();
    }

    @Override
    public void onPageStarted(String url, Bitmap favic) {

        progressDialog.create(WebViewActivity.this)
                .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE)
                .setCancellable(true)
                .setAnimationSpeed(2)
                .setDimAmount(0.5f);

        progressDialog.show();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        savedInstanceState.putString("MyString", mWebView.getUrl());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String myString = savedInstanceState.getString("MyString");

        if (myString.length() > 0) {
            loadWebView(myString);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        handleIntents();
        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }
        loadRewardedVideoAd();
        executeScheduler();
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd("ca-app-pub-1582220138871006/9119154039",
                new AdRequest.Builder().build());
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }

    @Override
    protected void onResume() {
        mRewardedVideoAd.resume(this);
        handleIntents();
        super.onResume();
    }


    @Override
    protected void onPause() {
        super.onPause();
        mRewardedVideoAd.pause(this);
    }


    @Override
    protected void onStop() {
        if (mWebView.getVisibility() == View.VISIBLE) {
            editorSettings.putString("lasturl", mWebView.getUrl());
            editorSettings.commit();
            editorSettings.apply();
        } else {
            editorSettings.remove("lasturl");
            editorSettings.commit();
            editorSettings.apply();
        }
        scheduleTaskExecutor.shutdown();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mWebView.onDestroy();
        mRewardedVideoAd.destroy(this);
        super.onDestroy();
    }


    private void handleUploadMessage(int requestCode, int resultCode, Intent intent) {
        Uri result = null;
        try {
            if (resultCode != RESULT_OK) {
                result = null;
            } else {
                // retrieve from the private variable if the intent is null

                result = intent == null ? mCapturedImageURI : intent.getData();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUploadMessage.onReceiveValue(result);
        mUploadMessage = null;
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void handleUploadMessages(int requestCode, int resultCode, Intent intent) {
        Uri[] results = null;
        try {
            if (resultCode != RESULT_OK) {
                results = null;
            } else {
                if (intent != null) {
                    String dataString = intent.getDataString();
                    ClipData clipData = intent.getClipData();
                    if (clipData != null) {
                        results = new Uri[clipData.getItemCount()];
                        for (int i = 0; i < clipData.getItemCount(); i++) {
                            ClipData.Item item = clipData.getItemAt(i);
                            results[i] = item.getUri();
                        }
                    }
                    if (dataString != null) {
                        results = new Uri[]{Uri.parse(dataString)};
                    }
                } else {
                    results = new Uri[]{mCapturedImageURI};
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        mUploadMessages.onReceiveValue(results);
        mUploadMessages = null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

        switch (requestCode) {
            case 1:
                if (resultCode == RESULT_OK && null != intent) {
                    ArrayList<String> results = intent.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    if (results.get(0) != "") {
                        loadWebView(results.get(0));
                    }

                }
                break;
            case FILE_SELECT_CODE:
                if (resultCode == RESULT_OK) {
                    Uri uri = intent.getData();
                    try {
                        String path = FileUtils.getPath(WebViewActivity.this, uri);
                        File file = new File(path);
                        if (file.getPath().endsWith("html")) {
                            if (file.isFile()) {
                                BufferedReader br = new BufferedReader(new FileReader(file));
                                StringBuilder sb = new StringBuilder();
                                String htmltext = br.readLine();
                                while (htmltext != null) {
                                    sb.append(htmltext).append("\n");
                                    htmltext = br.readLine();
                                }
                                String fileAsString = sb.toString();

                                if (mWebView.getVisibility() == View.VISIBLE) {
                                    mWebView.loadHtml(fileAsString);
                                    mWebView.reload();
                                } else {
                                    mainHomelayout.setVisibility(View.GONE);
                                    noInternetLayout.setVisibility(View.GONE);
                                    mWebView.setVisibility(View.VISIBLE);
                                    mWebView.loadHtml(fileAsString);
                                }
                            }
                        } else showToast("Choose html file please");


                    } catch (URISyntaxException | FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                break;

            case FILECHOOSER_RESULTCODE:
                if (null == mUploadMessage && null == mUploadMessages) {
                    return;
                }

                if (null != mUploadMessage) {
                    showToast(intent.getData().toString());
                    handleUploadMessage(requestCode, resultCode, intent);

                } else if (mUploadMessages != null) {
                    showToast(intent.getData().toString());
                    handleUploadMessages(requestCode, resultCode, intent);
                }
                break;

        }

        //mWebView.onActivityResult(requestCode, resultCode, intent);
    }

    @Override
    public void onBackPressed() {
        if (mWebView.canGoBack()) {
            mWebView.goBack();
            forwordArrow.setImageResource(R.drawable.ic_arrow_forward);
            onbackloading = true;
        } else {
            if (mainHomelayout.getVisibility() == View.VISIBLE) {
                super.onBackPressed();
            } else {
                goToHome();
            }
        }

    }

    @Override
    public void onPageFinished(String url) {

        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }


        backArrow.setImageResource(R.drawable.ic_arrow_back_black_24dp);
        homeButton.setImageResource(R.drawable.ic_home_black_24dp);
        searchBoxHome.setText(mWebView.getUrl());
        mWebView.setVisibility(View.VISIBLE);

        try {
            if (!allowAnonymous) {
                noInternetLayout.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
                if (!onbackloading) {
                    dbHelper.insertHistory(mWebView.getTitle(), url, currentDate, feviconByte);
                }
            } else {

                noInternetLayout.setVisibility(View.GONE);
                mWebView.setVisibility(View.VISIBLE);
            }

        } catch (Exception ex) {
            showToast("ception");
        }

    }

    @Override
    public void onPageError(int errorCode, String description, String failingUrl) {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();

    }

    @Override
    public void onDownloadRequested(final String s, final String s1, String s2, long l, String s3, String contentDisposition) {

        if (mRewardedVideoAd.isLoaded()) {
            mRewardedVideoAd.show();
        }

        final String title = URLUtil.guessFileName(s, s2, s3);
        bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
        View view2 = LayoutInflater.from(this).inflate(R.layout.download_dialoge, null, false);
        Button btn = view2.findViewById(R.id.download_dial_btn);
        TextView textView = view2.findViewById(R.id.download_dial_text);

        downloadResourcesProgress = view2.findViewById(R.id.download_dial_progress);
        textView.setText(title + " ");
        final TextView sizeText = view2.findViewById(R.id.download_dial_sizeText);
        final long[] file_size = new long[1];

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    URL myUrl = new URL(s);
                    URLConnection urlConnection = myUrl.openConnection();
                    urlConnection.connect();
                    file_size[0] = urlConnection.getContentLength();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        sizeText.setText(bytesToMb(file_size[0]));
                        downloadResourcesProgress.setVisibility(View.GONE);
                    }
                });
            }
        }).start();
        //thread.start();


        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Data.downloadUrls.add(s);
                } catch (Exception ex) {
                    showToast("Download Database Excepion");
                }
                Intent intent = new Intent(WebViewActivity.this, DownloadActivity.class);
                startActivity(intent);
                bottomSheetDialog.dismiss();

            }
        });
        bottomSheetDialog.setContentView(view2);
        bottomSheetDialog.show();

    }

    @Override
    public void onExternalPageRequest(String url) {
        showToast("External Link" + url);
    }


    @Override
    public void onItemClick(int position) {
        loadWebView(articlesall.get(position).getUrl());
    }

    @Override
    public void onLongItemClick(int position) {
    }

    private static String bytesToMb(long size) {
        if (size <= 0)
            return "0";

        final String[] units = new String[]{"B", "KB", "MB", "GB", "TB"};
        int digitGroups = (int) (Math.log10(size) / Math.log10(1024));

        return new DecimalFormat("#,##0.#").format(size / Math.pow(1024, digitGroups)) + " " + units[digitGroups];
    }

    private void openImageChooser() {
        try {
            File imageStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "FolderName");
            if (!imageStorageDir.exists()) {
                imageStorageDir.mkdirs();
            }
            File file = new File(imageStorageDir + File.separator + "IMG_" + String.valueOf(System.currentTimeMillis()) + ".jpg");
            mCapturedImageURI = Uri.fromFile(file);

            final Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mCapturedImageURI);

            Intent i = new Intent(Intent.ACTION_GET_CONTENT);
            i.addCategory(Intent.CATEGORY_OPENABLE);
            i.setType("*/*");

            Intent chooserIntent = Intent.createChooser(i, "File Chooser");
            chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Parcelable[]{captureIntent});

            startActivityForResult(chooserIntent, FILECHOOSER_RESULTCODE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onHItemClick(int position) {
        loadWebView(articles.get(position).getUrl());
    }

    @Override
    public void onHLongItemClick(int position) {
    }

    @Override
    public void onRewarded(RewardItem reward) {

        // Reward the user.
    }

    @Override
    public void onRewardedVideoAdLeftApplication() {
    }

    @Override
    public void onRewardedVideoAdClosed() {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int errorCode) {
        loadRewardedVideoAd();
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {
    }

    @Override
    public void onRewardedVideoStarted() {
    }

    @Override
    public void onRewardedVideoCompleted() {
    }
    private class ChromeClient extends WebChromeClient {
        private View mCustomView;
        private WebChromeClient.CustomViewCallback mCustomViewCallback;
        protected FrameLayout mFullscreenContainer;
        private int mOriginalOrientation;
        private int mOriginalSystemUiVisibility;

        ChromeClient() {
        }

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType) {
            mUploadMessage = uploadMsg;
            openImageChooser();
        }

        // For Lollipop 5.0+ Devices

        public boolean onShowFileChooser(WebView mWebView, ValueCallback<Uri[]> filePathCallback, WebChromeClient.FileChooserParams fileChooserParams) {
            mUploadMessages = filePathCallback;
            openImageChooser();
            return true;
        }

        // openFileChooser for Android < 3.0

        public void openFileChooser(ValueCallback<Uri> uploadMsg) {
            openFileChooser(uploadMsg, "");
        }

        //openFileChooser for other Android versions

        public void openFileChooser(ValueCallback<Uri> uploadMsg, String acceptType, String capture) {
            openFileChooser(uploadMsg, acceptType);
        }


        public Bitmap getDefaultVideoPoster() {
            if (mCustomView == null) {
                return null;
            }
            return BitmapFactory.decodeResource(getResources(), 2130837573);
        }

        public void onHideCustomView() {
            ((FrameLayout) getWindow().getDecorView()).removeView(this.mCustomView);
            this.mCustomView = null;
            getWindow().getDecorView().setSystemUiVisibility(this.mOriginalSystemUiVisibility);
            setRequestedOrientation(this.mOriginalOrientation);
            this.mCustomViewCallback.onCustomViewHidden();
            this.mCustomViewCallback = null;
        }

        public void onShowCustomView(View paramView, WebChromeClient.CustomViewCallback paramCustomViewCallback) {
            if (this.mCustomView != null) {
                onHideCustomView();
                return;
            }
            this.mCustomView = paramView;
            this.mOriginalSystemUiVisibility = getWindow().getDecorView().getSystemUiVisibility();
            this.mOriginalOrientation = getRequestedOrientation();
            this.mCustomViewCallback = paramCustomViewCallback;
            ((FrameLayout) getWindow().getDecorView()).addView(this.mCustomView, new FrameLayout.LayoutParams(-1, -1));
            getWindow().getDecorView().setSystemUiVisibility(3846 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }


        @Override
        public void onReceivedIcon(WebView view, Bitmap icon) {
            super.onReceivedIcon(view, icon);
            feviconByte = DBUtility.bitmapToByte(icon);

        }
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.backarrow:
                if (mWebView.canGoBack()) {
                    mWebView.goBack();
                    forwordArrow.setImageResource(R.drawable.ic_arrow_forward);
                    onbackloading = true;
                } else {
                    goToHome();
                }
                break;
            case R.id.forwordarrow:
                if (mWebView.canGoForward()) {
                    mWebView.goForward();
                } else {
                    forwordArrow.setImageResource(R.drawable.ic_arrow_forward_desabled);
                }
                break;
            case R.id.homebtn:
                goToHome();
                break;
            case R.id.youtubebtn:
                loadWebView("https://www.youtube.com");
                break;
            case R.id.googlebtn:
                loadWebView("https://www.google.com");
                break;
            case R.id.twiterbtn:
                loadWebView("https://www.twitter.com");
                break;
            case R.id.amazon:
                loadWebView("https://www.amazon.com");
                break;
            case R.id.yahoobtn:
                loadWebView("https://www.yahoo.com");
                break;
            case R.id.gmailbtn:
                loadWebView("https://m.dailyweather.io");
                break;
            case R.id.spotifybtn:
                loadWebView("https://www.spotify.com");
                break;
            case R.id.pinterestbtn:
                loadWebView("https://www.pinterest.com");
                break;
            case R.id.gamingbtn:
                loadWebView("https://www.tiktok.com/");
                break;
            case R.id.crickbuzzbtn:
                loadWebView("https://m.cricbuzz.com");
                break;
            case R.id.musicbtn:
                loadWebView("https://gaana.com/");
                break;
            case R.id.entertainmentbtn:
                loadWebView("https://html.duckduckgo.com/html/");
                break;
            case R.id.menubtn:
                bottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                View view2 = LayoutInflater.from(this).inflate(R.layout.menu_dialoge, null, false);
                ImageButton addbookmark = view2.findViewById(R.id.addbookmark);
                ImageButton showBookmarks = view2.findViewById(R.id.bookmarks);
                ImageButton history = view2.findViewById(R.id.history);
                ImageButton tools = view2.findViewById(R.id.tools_menu);
                ImageButton downloads = view2.findViewById(R.id.downloads);
                incognito = view2.findViewById(R.id.incognito);
                deskTopMode = view2.findViewById(R.id.deskTopMode);
                darkMode_text = view2.findViewById(R.id.darkMode_text);
                noOfAdsTextView = view2.findViewById(R.id.noOfAdsBlocked);
                ImageButton settings = view2.findViewById(R.id.settings);
                ImageButton share = view2.findViewById(R.id.share_menu_btn);
                ImageButton close = view2.findViewById(R.id.close_menu_btn);
                ImageButton shutapp = view2.findViewById(R.id.shut_app_menu_btn);

                noOfAdsTextView.setText(" " + numberOfAdsBlocked);
                if (isDesktopMode) {
                    darkMode_text.setText("Mobile");
                    deskTopMode.setImageResource(R.drawable.ic_mobile_friendly_24);
                }

                addbookmark.setOnClickListener(this);
                showBookmarks.setOnClickListener(this);
                history.setOnClickListener(this);
                tools.setOnClickListener(this);
                downloads.setOnClickListener(this);
                incognito.setOnClickListener(this);
                deskTopMode.setOnClickListener(this);
                settings.setOnClickListener(this);
                share.setOnClickListener(this);
                close.setOnClickListener(this);
                shutapp.setOnClickListener(this);

                bottomSheetDialog.setContentView(view2);
                bottomSheetDialog.show();
                break;

            case R.id.incognito:
                if (allowAnonymous) {
                    setAnonymous(false, mWebView);
                    bottomSheetDialog.dismiss();
                    allowAnonymous = false;
                    Snackbar snackbar = Snackbar
                            .make(rootView, "Anonymous Off", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    setAnonymous(true, mWebView);
                    bottomSheetDialog.dismiss();
                    allowAnonymous = true;
                    Snackbar snackbar = Snackbar
                            .make(rootView, "Anonymous On", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
                break;
            case R.id.bookmarks:
                Intent intent = new Intent(this, BookmarksActivity.class);
                bottomSheetDialog.dismiss();
                startActivity(intent);
                break;
            case R.id.history:
                Intent intent2 = new Intent(this, HistoryActivity.class);
                bottomSheetDialog.dismiss();
                startActivity(intent2);
                break;
            case R.id.tools_menu:
                bottomSheetDialog.dismiss();
                toolsbottomSheetDialog = new BottomSheetDialog(this, R.style.BottomSheetDialog);
                View toolsView = LayoutInflater.from(this).inflate(R.layout.tools_menu_dialoge, null, false);
                ImageButton print = toolsView.findViewById(R.id.tool_print);
                ImageButton barcode = toolsView.findViewById(R.id.tool_barcode);
                ImageButton fullscreen = toolsView.findViewById(R.id.full_screen);
                ImageButton loadwebsite = toolsView.findViewById(R.id.tool_extra);

                print.setOnClickListener(this);
                barcode.setOnClickListener(this);
                fullscreen.setOnClickListener(this);
                loadwebsite.setOnClickListener(this);

                toolsbottomSheetDialog.setContentView(toolsView);
                toolsbottomSheetDialog.show();
                break;
            case R.id.tool_barcode:
                Intent intent3 = new Intent(this, BarcodeActivity.class);
                bottomSheetDialog.dismiss();
                startActivity(intent3);
                toolsbottomSheetDialog.dismiss();
                break;
            case R.id.full_screen:
                hideView(toplayout);
                hideView(bottomLayout);
                showView(floatingActionButton);
                toolsbottomSheetDialog.dismiss();
                break;
            case R.id.tool_print:
                if (mainHomelayout.getVisibility() == View.GONE && mWebView.getVisibility() == View.VISIBLE) {
                    createWebPrintJob(mWebView);
                    toolsbottomSheetDialog.dismiss();
                } else {
                    showToast("No Web Page Found");
                }
                break;
            case R.id.tool_extra:
                loadHtmlWebsite();
                toolsbottomSheetDialog.dismiss();
                break;
            case R.id.downloads:
                Intent intent4 = new Intent(this, DownloadActivity.class);
                bottomSheetDialog.dismiss();
                startActivity(intent4);
                break;
            case R.id.settings:
                bottomSheetDialog.dismiss();
                startActivity(new Intent(this, SettingsActivity.class));
                break;
            case R.id.deskTopMode:
                if (isDesktopMode) {
                    mWebView.setDesktopMode(false);
                    darkMode_text.setText("Desktop");
                    deskTopMode.setImageResource(R.drawable.ic_desktop_friendly_24);
                    isDesktopMode = false;
                } else {
                    darkMode_text.setText("Mobile");
                    mWebView.setDesktopMode(true);
                    deskTopMode.setImageResource(R.drawable.ic_mobile_friendly_24);
                    isDesktopMode = true;
                }
                bottomSheetDialog.dismiss();
                break;
            case R.id.addbookmark:
                if (mWebView.getVisibility() == View.VISIBLE) {
                    bottomSheetDialog.dismiss();
                    createAlertDialoge();
                } else {
                    showToast("No Web Page Found");
                }
                break;
            case R.id.drawableLeft:
                defaultSearchEngine();

                break;
            case R.id.drawableRight:
                if (mainHomelayout.getVisibility() == View.GONE) {
                    if (isNetworkConnected()) {
                        noInternetLayout.setVisibility(View.GONE);
                        mWebView.setVisibility(View.VISIBLE);
                        mWebView.reload();
                        drawableRight.startAnimation(
                                AnimationUtils.loadAnimation(this, R.anim.rotate));
                    } else {
                        mWebView.setVisibility(View.GONE);
                        noInternetLayout.setVisibility(View.VISIBLE);
                    }
                } else {
                    speak_Home();
                }
                break;
            case R.id.close_menu_btn:
                bottomSheetDialog.dismiss();
                break;
            case R.id.share_menu_btn:
                if (mainHomelayout.getVisibility() == View.GONE) {
                    Intent sharei = new Intent(android.content.Intent.ACTION_SEND);
                    sharei.setType("text/plain");
                    sharei.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                    sharei.putExtra(Intent.EXTRA_SUBJECT, mWebView.getTitle());
                    sharei.putExtra(Intent.EXTRA_TEXT, mWebView.getUrl());
                    startActivity(Intent.createChooser(sharei, "Share link!"));
                } else {
                    showToast("No Web Page Found");

                }
                bottomSheetDialog.dismiss();
                break;
            case R.id.shut_app_menu_btn:
                bottomSheetDialog.dismiss();
                new MaterialAlertDialogBuilder(this, R.style.myAlertDialoge)
                        .setTitle("Close App")
                        .setMessage("Do you really want to close App")
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                int pid = android.os.Process.myPid();
                                android.os.Process.killProcess(pid);
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                break;

        }

    }

    private void loadHtmlWebsite() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);

        try {
            startActivityForResult(
                    Intent.createChooser(intent, "Select an html file"),
                    FILE_SELECT_CODE);
        } catch (android.content.ActivityNotFoundException ex) {
            // Potentially direct the user to the Market with a Dialog
            showToast("Please Install a File Manager");
        }

    }


    private void goToHome() {
        forwordArrow.setImageResource(R.drawable.ic_arrow_forward_desabled);
        backArrow.setImageResource(R.drawable.ic_arrow_back_desabled);
        homeButton.setImageResource(R.drawable.ic_home_black_desabled);
        mWebView.clearHistory();
        mWebView.clearCache(true);
        mWebView.onPause();
        mWebView.setVisibility(View.GONE);
        mWebView.stopLoading();
        noInternetLayout.setVisibility(View.GONE);
        mainHomelayout.setVisibility(View.VISIBLE);
        drawableRight.setImageResource(R.drawable.ic_mic_24);
        searchBoxHome.setText("");
    }

    private void speak_Home() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Please Speak Something");
        try {
            startActivityForResult(intent, 1);
        } catch (Exception ex) {
            showToast(ex.getMessage());
        }

    }

    private void loadWebView(String query) {

        if (isNetworkConnected()) {
            onbackloading = false;
            noInternetLayout.setVisibility(View.GONE);
            if (mainHomelayout.getVisibility() == View.GONE) {

                String url = searchEngineQuery + query;
                mWebView.onResume();
                if (query.contains("https://") || query.contains(".com") || query.contains("http://")) {
                    //pass query
                    mWebView.loadUrl(query);
                } else {
                    //pass url
                    mWebView.loadUrl(url);
                }
            } else {
                mainHomelayout.setVisibility(View.GONE);
                drawableRight.setImageResource(R.drawable.ic_reload);
                loadWebView(query);
            }
        } else {
            mainHomelayout.setVisibility(View.GONE);
            drawableRight.setImageResource(R.drawable.ic_reload);
            noInternetLayout.setVisibility(View.VISIBLE);
            homeButton.setImageResource(R.drawable.ic_home_black_24dp);
        }
    }

    private void createAlertDialoge() {
        AlertDialog dialogBuilder = new MaterialAlertDialogBuilder(this, R.style.myAlertDialoge)
                .setView(R.layout.bookmark_dialoge).show();


        EditText editText = (EditText) dialogBuilder.findViewById(R.id.bookmark_dial_editText);
        Button save = (Button) dialogBuilder.findViewById(R.id.bookmark_dial_btn);
        Button cancel = (Button) dialogBuilder.findViewById(R.id.bookmark_dial_cancelbtn);
        editText.setText(mWebView.getTitle());


        URL hostUrl = null;
        try {
            hostUrl = new URL(mWebView.getUrl());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        String title = hostUrl.getHost();
        title = title.startsWith("www.") ? title.substring(4) : title;
        editText.setText(title + "");

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addbookmarkTitle = editText.getText().toString();
                if (addbookmarkTitle.length() > 0) {
                    boolean ins = db_bookmark.insertBookmark(mWebView.getTitle(), mWebView.getOriginalUrl(), currentDate, feviconByte);
                    if (ins) {
                        showToast("bookkmark Saved Successfully");
                        bottomSheetDialog.dismiss();
                        dialogBuilder.dismiss();
                    }
                } else {
                    showToast("Enter Title");
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogBuilder.dismiss();
            }
        });
    }


    //newsApi methods

    private void loadArticlesHorizontal(String Tag) {

        ApiInterface apiInterface = ApiClientCall.getApiClient().create(ApiInterface.class);

        Call<News> call;
        call = apiInterface.getEverything(articlesTag, getLanguage(), "publishedAt", API_KEY);
        call.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {

                if (response.isSuccessful() && response.body().getArticles() != null) {
                    if (!articlesall.isEmpty()) {
                        articlesall.clear();
                    }

                    articlesall.addAll(response.body().getArticles());
                    recyclerView.setLayoutManager(new LinearLayoutManager(WebViewActivity.this, RecyclerView.HORIZONTAL, false));
                    adapterRecycler = new AdapterRecycler(articlesall, WebViewActivity.this, WebViewActivity.this);
                    recyclerView.setAdapter(adapterRecycler);
                    adapterRecycler.notifyDataSetChanged();


                } else {
                    textView.setText("No Articles Found: ");
                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {
                //textView.setText(t.getMessage());

            }
        });
    }

    private void loadArticlesVerticle(){
        ApiInterface apiInterface = ApiClientCall.getApiClient().create(ApiInterface.class);

        String Country = getCountry();
        Call<News> call2;
        call2 = apiInterface.getNews(Country, API_KEY, getLanguage());
        call2.enqueue(new Callback<News>() {
            @Override
            public void onResponse(Call<News> call, Response<News> response) {
                if (response.isSuccessful() && response.body().getArticles() != null) {
                    if (!articles.isEmpty()) {
                        articles.clear();
                    }

                    articles = response.body().getArticles();

                    recyclerView2.addItemDecoration(new DividerItemDecoration(WebViewActivity.this, DividerItemDecoration.VERTICAL));
                    recyclerView2.setLayoutManager(new LinearLayoutManager(WebViewActivity.this, LinearLayoutManager.VERTICAL, false));
                    adapterArticlesAll = new AdapterArticlesAll(articles, WebViewActivity.this, WebViewActivity.this);
                    recyclerView2.setAdapter(adapterArticlesAll);
                    adapterArticlesAll.notifyDataSetChanged();


                } else {
                    textView.setText("No Articles Found: ");
                }

            }

            @Override
            public void onFailure(Call<News> call, Throwable t) {

            }


        });

    }

    private String getcountryCode() {
        String country_name = null;
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Geocoder geocoder = new Geocoder(this);
        for (String provider : lm.getAllProviders()) {
            @SuppressWarnings("ResourceType") Location location = lm.getLastKnownLocation(provider);
            if (location != null) {
                try {
                    List<Address> addresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);
                    if (addresses != null && addresses.size() > 0) {
                        //country_name = addresses.get(0).getCountryName();
                        country_name = addresses.get(0).getCountryCode();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return country_name;
    }

    public static String getCountry() {
        Locale locale = Locale.getDefault();
        String country = String.valueOf(locale.getCountry());
        return country;
    }

    public static String getLanguage() {
        Locale locale = Locale.getDefault();
        String lang = String.valueOf(locale.getLanguage());
        return lang;
    }


    private class MyWebClient extends WebViewClient {
        private Activity activity = null;
        private UrlCache urlCache = null;

        public MyWebClient(Activity activity) {
            this.activity = activity;
            this.urlCache = new UrlCache(activity);

            this.urlCache.register("http://tutorials.jenkov.com/", "tutorials-jenkov-com.html",
                    "text/html", "UTF-8", 5 * UrlCache.ONE_MINUTE);

        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView wv, String url) {
            if (url.startsWith("whatsapp:") || url.startsWith("mailto:") || url.startsWith("market:")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);
                return true;
            } else if (url.startsWith("tel:")) {
                try {
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                } catch (android.content.ActivityNotFoundException e) {
                }
            }
            //launch media players intent to play video in webview
            /*if (url.endsWith(".mp4")) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.parse(url), "video/*");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                return true;
            }*/
            else if (URLUtil.isNetworkUrl(url)) {
                return false;
            } else if (appInstalledOrNot(url)) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intent);
            } else {
                showToast("App Not Installed");
            }

            return true;


        }

        private Map<String, Boolean> loadedUrls = new HashMap<>();

        @SuppressWarnings("deprecation")
        @Override
        public WebResourceResponse shouldInterceptRequest(WebView view, String url) {

            if (AdBlockerWebView.blockAds(view, url)) {
                numberOfAdsBlocked += 1;
                return AdBlocker.createEmptyResource();
            } else {
                return super.shouldInterceptRequest(view, url);
            }

        }

        @Override
        public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
            super.onReceivedSslError(view, handler, error);
            handler.proceed();
        }

    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return true;


    }


    private void createWebPrintJob(AdvancedWebView webView) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            PrintManager printManager = (PrintManager) getSystemService(Context.PRINT_SERVICE);
            PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();

            String jobName = getString(R.string.app_name) + " Print";
            printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
        }
    }


}