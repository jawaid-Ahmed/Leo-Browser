package com.jawaid.liobrowser.Activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.jawaid.liobrowser.Databases.FbPrefs;
import com.jawaid.liobrowser.R;
import com.jawaid.liobrowser.models.fSettings;

import net.cachapa.expandablelayout.ExpandableLayout;

public class SettingsActivity extends AppCompatActivity {

    private static final String MY_EMAIL = "jkahmed78@gmail.com";
    Toolbar toolbar;
    private TextView textCounter;
    private Button feedbackBtn;
    private SwitchCompat switchFormsData,switchsavePass,switchLoadImagesAuto,switchShowZoomControls;
    private SharedPreferences setingsPrefs;
    private SharedPreferences.Editor editor;

    private ExpandableLayout expandableLayoutAdvance;
    private Button articlesOnOff;
    private Button articlesTitle;
    private Button edvancedSettingsButton;


    private boolean booleanForms,booleanPasswords,booleanLoadImagesAuto,booleanShowZoomControls;
    private FbPrefs setings;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        setupToolbar();

        setingsPrefs=this.getSharedPreferences(fSettings.SETTINGS_PREF,MODE_PRIVATE);
        editor=setingsPrefs.edit();
        setings=new FbPrefs(this);

        feedbackBtn=findViewById(R.id.feedBackButton);
        switchShowZoomControls=findViewById(R.id.zoomControlsSwich);
        switchFormsData=findViewById(R.id.formsataSwich);
        switchsavePass=findViewById(R.id.passworddataSwich);
        switchLoadImagesAuto=findViewById(R.id.loadImagesSwich);
        expandableLayoutAdvance=findViewById(R.id.advancedSettingsLayout);
        articlesOnOff=findViewById(R.id.headlinesTitleVerticlev);
        articlesTitle=findViewById(R.id.headlinesTitleHorizontal);
        edvancedSettingsButton=findViewById(R.id.advanceSettingsButton);



        boolean saveForms=setingsPrefs.getBoolean(fSettings.FORMS_DATA,true);
        boolean savepasswords=setingsPrefs.getBoolean(fSettings.PASSWORD_DATA,true);
        boolean displayZoomControls=setingsPrefs.getBoolean(fSettings.DESPLAY_ZOOM_CONTROLS,false);
        boolean loadImagesAuto=setingsPrefs.getBoolean(fSettings.LOAD_IMAGES_AUTO,true);


        switchShowZoomControls.setChecked(displayZoomControls);
        switchLoadImagesAuto.setChecked(loadImagesAuto);
        switchsavePass.setChecked(savepasswords);
        switchFormsData.setChecked(saveForms);


        edvancedSettingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                expandableLayoutAdvance.toggle();
            }
        });

        articlesOnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View articlesView=getLayoutInflater().inflate(R.layout.articlestitlelayout,null,false);
                RadioGroup articlesRadioGroup=articlesView.findViewById(R.id.articlesRadioGroup);

                RadioButton none=articlesView.findViewById(R.id.articlesNone);
                RadioButton aHorizontal=articlesView.findViewById(R.id.articlesHorzontle);
                RadioButton aVerticle=articlesView.findViewById(R.id.articlesVerticle);
                RadioButton articlesboth=articlesView.findViewById(R.id.articlesAll);

                //downloading threeds prefs
                String val1=setings.getString("articles");
                if (val1.equals("none")){
                    none.setChecked(true);
                }else if (val1.equals("horizontle")){
                    aHorizontal.setChecked(true);
                }else if (val1.equals("verticle")){
                    aVerticle.setChecked(true);
                }else{
                    articlesboth.setChecked(true);
                }

                //storage radio group listener
                articlesRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener()
                {
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        switch(checkedId){
                            case R.id.articlesAll:
                                setings.putString("articles","both");
                                articlesboth.setChecked(true);
                                break;
                            case R.id.articlesHorzontle:
                                setings.putString("articles","horizontle");
                                aHorizontal.setChecked(true);
                                break;
                            case R.id.articlesVerticle:
                                setings.putString("articles","verticle");
                                aVerticle.setChecked(true);
                                break;
                            case R.id.articlesNone:
                                setings.putString("articles","none");
                                none.setChecked(true);
                                break;
                        }
                    }
                });

                new MaterialAlertDialogBuilder(SettingsActivity.this, R.style.myAlertDialoge)
                        .setTitle("Articles")
                        .setView(articlesView)
                        .setPositiveButton("Close", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Toast.makeText(SettingsActivity.this, "Restart App To Make Changes", Toast.LENGTH_SHORT).show();
                            }
                        }).show();


            }
        });

        articlesTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View articlesabout=getLayoutInflater().inflate(R.layout.articlesabout,null,false);
                EditText about=articlesabout.findViewById(R.id.articlesAboutEdittext);

                new MaterialAlertDialogBuilder(SettingsActivity.this, R.style.myAlertDialoge)
                        .setTitle("Articles About")
                        .setView(articlesabout)
                        .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                if (about!=null && about.getText().length()>0) {
                                    setings.putString("articlesTag", about.getText().toString());
                                    Toast.makeText(SettingsActivity.this, "Restart App To Make Changes", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }).setNegativeButton(R.string.cancel, null)
                        .show();

            }
        });

        feedbackBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                View feedbackView=getLayoutInflater().inflate(R.layout.feedbacklayout,null,false);
                EditText title=feedbackView.findViewById(R.id.feedbackEdittexttitle);
                EditText message=feedbackView.findViewById(R.id.feedbackEdittextmessage);

                new MaterialAlertDialogBuilder(SettingsActivity.this, R.style.myAlertDialoge)
                        .setTitle("Feedback")
                        .setView(feedbackView)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .setPositiveButton("Submit Feedback", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                Intent intent=new Intent(Intent.ACTION_SEND);
                                intent.setType("plain/text");
                                intent.putExtra(Intent.EXTRA_EMAIL,new String[]{MY_EMAIL});
                                intent.putExtra(Intent.EXTRA_PHONE_NUMBER,"+923312358408");
                                intent.putExtra(Intent.EXTRA_SUBJECT,"Firebrowser Feedback");
                                intent.putExtra(Intent.EXTRA_TEXT,"Name: "+title.getText()+" \n Message"+message.getText());
                                try {
                                    startActivity(Intent.createChooser(intent,"Please Select Email Source"));
                                }catch (ActivityNotFoundException ex){
                                    Toast.makeText(SettingsActivity.this, "No Email Clients Found", Toast.LENGTH_SHORT).show();
                                }

                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();

            }
        });

        switchFormsData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSwitchValue(fSettings.FORMS_DATA,switchFormsData.isChecked());
            }
        });

        switchsavePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSwitchValue(fSettings.PASSWORD_DATA,switchsavePass.isChecked());
            }
        });


        switchLoadImagesAuto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSwitchValue(fSettings.LOAD_IMAGES_AUTO,switchLoadImagesAuto.isChecked());
            }
        });

        switchShowZoomControls.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                changeSwitchValue(fSettings.DESPLAY_ZOOM_CONTROLS,switchShowZoomControls.isChecked());
            }
        });

    }
    private void changeSwitchValue(String formsData, boolean booleans) {
            editor.putBoolean(formsData,booleans);
            editor.commit();
            editor.apply();
    }

    private void setupToolbar() {
        toolbar = findViewById(R.id.toolbar_history);
        setSupportActionBar(toolbar);
        textCounter=findViewById(R.id.item_counter_text);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setElevation(0);
        getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        textCounter.setText("Settings");
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
}