package com.shop.shopaves.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.shop.shopaves.R;

public class ManageSocialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_social);
        TextView title = (TextView)findViewById(R.id.title);
        title.setText(getResources().getString(R.string.managesocial));
        findViewById(R.id.back_addrss).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
/*
    private void shareContent(){
        Intent intent = getPackageManager().getLaunchIntentForPackage(application);
        if (intent != null) {
            // The application exists
            Intent shareIntent = new Intent();
            shareIntent.setAction(Intent.ACTION_SEND);
            shareIntent.setPackage(application);

            shareIntent.putExtra(android.content.Intent.EXTRA_TITLE, "facebook");
            shareIntent.putExtra(Intent.EXTRA_TEXT, "share");
            // Start the specific social application
           startActivity(shareIntent);
        } else {
            // The application does not exist
            // Open GooglePlay or use the default system picker
        }
    }*/
}
