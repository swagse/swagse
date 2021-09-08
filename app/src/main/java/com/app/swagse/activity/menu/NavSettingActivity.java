package com.app.swagse.activity.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;

import com.app.swagse.R;
import com.app.swagse.activity.AboutUsActivity;
import com.app.swagse.activity.PrivacyPolicyActivity;
import com.app.swagse.activity.TermsConditionActivity;

public class NavSettingActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout rate_app, privacy_policy, terms_condition, about_us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nav_setting);
        getSupportActionBar().setTitle("Settings");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        rate_app = findViewById(R.id.rate_app);
        privacy_policy = findViewById(R.id.privacy_policy);
        terms_condition = findViewById(R.id.terms_condition);
        about_us = findViewById(R.id.about_us);

        rate_app.setOnClickListener(this);
        privacy_policy.setOnClickListener(this);
        about_us.setOnClickListener(this);
        terms_condition.setOnClickListener(this);
    }

    /*
     * Start with rating the app
     * Determine if the Play Store is installed on the device
     *
     * */
    public void rateApplication() {
        try {
            Intent rateIntent = rateIntentForUrl("market://details");
            startActivity(rateIntent);
        } catch (ActivityNotFoundException e) {
            Intent rateIntent = rateIntentForUrl("https://play.google.com/store/apps/details");
            startActivity(rateIntent);
        }
    }

    private Intent rateIntentForUrl(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(String.format("%s?id=%s", url, getPackageName())));
        int flags = Intent.FLAG_ACTIVITY_NO_HISTORY | Intent.FLAG_ACTIVITY_MULTIPLE_TASK;
        if (Build.VERSION.SDK_INT >= 21) {
            flags |= Intent.FLAG_ACTIVITY_NEW_DOCUMENT;
        } else {
            //noinspection deprecation
            flags |= Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET;
        }
        intent.addFlags(flags);
        return intent;
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

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rate_app: {
                rateApplication();
                break;
            }
            case R.id.privacy_policy: {
                startActivity(new Intent(this, PrivacyPolicyActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.terms_condition: {
                startActivity(new Intent(this, TermsConditionActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
            case R.id.about_us: {
                startActivity(new Intent(this, AboutUsActivity.class));
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
                break;
            }
        }
    }
}