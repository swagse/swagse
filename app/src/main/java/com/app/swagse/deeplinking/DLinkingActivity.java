package com.app.swagse.deeplinking;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.webkit.WebView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.app.swagse.R;
import com.app.swagse.activity.MainActivity;
import com.app.swagse.activity.SwagTubeDetailsActivity;

import java.util.List;

public class DLinkingActivity extends AppCompatActivity {
    Intent intent;
    LinearLayout linearLayout;
    private WebView webView;
    FrameLayout Fragment_frame;
    ImageView dlinksplash, dlinksplash1;
    ActionBar actionBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_d_linking);
        // Get intent, action and MIME type
        webView = (WebView) findViewById(R.id.webView);
        Fragment_frame = findViewById(R.id.fragment_frame);
        linearLayout = findViewById(R.id.linearlaydlink);
        dlinksplash = findViewById(R.id.dlinksplash);
        dlinksplash1 = findViewById(R.id.dlinksplash1);
        Uri uri = getIntent().getData();
        if (uri != null) {
            String linkfilter;
            List<String> params = uri.getPathSegments();
            int psu = params.size();
            if (psu != 0) {
                linkfilter = params.get(params.size() - psu);
                if (linkfilter.equalsIgnoreCase("watch")) {
                    String title = params.get(params.size() - 1);
                    intent = new Intent(this, SwagTubeDetailsActivity.class).putExtra(DLinkingActivity.class.getSimpleName(), title);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                    startActivity(intent);
                }
            } else {
                intent = new Intent(this, MainActivity.class);
                startActivity(intent);

            }
        } else {

        }
    }

    @Override
    public void onBackPressed() {
        Intent intent;
        intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        finish();
        startActivity(intent);
    }
}