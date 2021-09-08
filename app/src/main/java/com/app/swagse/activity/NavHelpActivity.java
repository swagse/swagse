package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.app.swagse.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NavHelpActivity extends AppCompatActivity {

    @BindView(R.id.whatsNew)
    RelativeLayout whatsNew;
    @BindView(R.id.checkforUpdate)
    RelativeLayout checkforUpdate;
    @BindView(R.id.contactUs)
    RelativeLayout contactUs;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_help);
        ButterKnife.bind(this);
        getSupportActionBar().setTitle("Help");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @OnClick({R.id.whatsNew, R.id.checkforUpdate, R.id.contactUs})
    public void onClickViewed(View view) {
        switch (view.getId()) {
            case R.id.whatsNew: {
                Toast.makeText(this, "No New Features Added Yet!", Toast.LENGTH_SHORT).show();
                break;
            }
            case R.id.checkforUpdate: {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
                }
                break;
            }
            case R.id.contactUs: {
                startActivity(new Intent(NavHelpActivity.this, ContactUsActivity.class));
                finish();
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}