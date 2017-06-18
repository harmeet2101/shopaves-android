package com.shop.shopaves.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.shop.shopaves.Constant.C;
import com.shop.shopaves.Constant.Constants;
import com.shop.shopaves.R;
import com.shop.shopaves.dataModel.AppStore;

import java.util.Locale;

public class LanguageActivity extends AppCompatActivity implements View.OnClickListener {

    private ImageView englishIcon,spanishIcon,japaneseIcon,vietNameseIcon;
    private String selectedLanguage;
    private boolean onFirstLoad = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_language);
        englishIcon = (ImageView)findViewById(R.id.englishicon);
        spanishIcon = (ImageView)findViewById(R.id.spanishicon);
        japaneseIcon = (ImageView)findViewById(R.id.japicon);
        vietNameseIcon = (ImageView)findViewById(R.id.vte);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.language));

        selectedLanguage = new AppStore(this).getData(Constants.LANGUAGE);

        findViewById(R.id.english).setOnClickListener(this);
        findViewById(R.id.spanish).setOnClickListener(this);
        findViewById(R.id.japanese).setOnClickListener(this);
        findViewById(R.id.vietnamese).setOnClickListener(this);
        findViewById(R.id.apply).setOnClickListener(this);
        findViewById(R.id.back_addrss).setOnClickListener(this);
        setDefault();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case  R.id.english:
                setDefault();
                englishIcon.setImageResource(R.drawable.selecton);
                selectedLanguage = "en";
            break;
            case  R.id.japanese:
                setDefault();
                japaneseIcon.setImageResource(R.drawable.selecton);
                selectedLanguage = "ja";
            break;
            case  R.id.spanish:
                setDefault();
                spanishIcon.setImageResource(R.drawable.selecton);
                selectedLanguage = "es";
            break;
            case  R.id.vietnamese:
                setDefault();
                vietNameseIcon.setImageResource(R.drawable.selecton);
                selectedLanguage = "vi";
            break;
            case R.id.apply:
                AlertDialog.Builder builder1 = new AlertDialog.Builder(LanguageActivity.this);
                builder1.setMessage("Are you sure you want to change the language?");
                builder1.setCancelable(true);

                builder1.setPositiveButton(
                        "Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new AppStore(LanguageActivity.this).setData(Constants.LANGUAGE,selectedLanguage);
               /* if(selectedLanguage.equals("vt") || selectedLanguage.equals("ja"))
                    selectedLanguage = "en";*/
                                C.setLocale(selectedLanguage,LanguageActivity.this);
                                dialog.cancel();
                            }
                        });

                builder1.setNegativeButton(
                        "No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

                AlertDialog alert11 = builder1.create();
                alert11.show();

                break;
            case  R.id.back_addrss:
                finish();
            break;
        }
    }

    private void setDefault(){
        englishIcon.setImageResource(R.drawable.selectoff);
        japaneseIcon.setImageResource(R.drawable.selectoff);
        spanishIcon.setImageResource(R.drawable.selectoff);
        vietNameseIcon.setImageResource(R.drawable.selectoff);

        if(onFirstLoad){
            if(selectedLanguage.equals("en"))
                englishIcon.setImageResource(R.drawable.selecton);
            else if(selectedLanguage.equals("ja"))
                japaneseIcon.setImageResource(R.drawable.selecton);
            else if(selectedLanguage.equals("es"))
                spanishIcon.setImageResource(R.drawable.selecton);
            else
                vietNameseIcon.setImageResource(R.drawable.selecton);
            onFirstLoad = false;
        }
    }
    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        Intent refresh = new Intent(this, HomeActivity.class);
        startActivity(refresh);
        finish();
    }
}
