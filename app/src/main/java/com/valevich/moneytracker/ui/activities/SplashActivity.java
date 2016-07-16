package com.valevich.moneytracker.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity
public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @ViewById(R.id.splashImage)
    ImageView mImageView;

    @ViewById(R.id.textView)
    TextView mTextView;


    @AfterViews
    void showImageWithText() {
        YoYo.with(Techniques.SlideInDown)
                .duration(2000)
                .playOn(mImageView);
        YoYo.with(Techniques.SlideInDown)
                .duration(2000)
                .playOn(mTextView);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {
                checkIfUserRegistered();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

    private void checkIfUserRegistered() {
        boolean tokenExists = MoneyTrackerApplication_.isLoftTokenExist()
                || MoneyTrackerApplication_.isGoogleTokenExist();
        if(!tokenExists) {
            navigateToLogIn();
        } else {
            navigateToMain();
        }
    }

    private void navigateToLogIn() {
        Intent intent = new Intent(this,LoginActivity_.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToMain() {
        MainActivity_.intent(this).start();
        finish();
    }


}
