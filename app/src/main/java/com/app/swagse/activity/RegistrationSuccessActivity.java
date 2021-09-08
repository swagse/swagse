package com.app.swagse.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.app.swagse.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class RegistrationSuccessActivity extends AppCompatActivity {

    @BindView(R.id.successButton)
    AppCompatButton successButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_success);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.successButton)
    public void onClickViewed(View view) {
        startActivity(new Intent(RegistrationSuccessActivity.this, MainActivity.class));
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
    }
}