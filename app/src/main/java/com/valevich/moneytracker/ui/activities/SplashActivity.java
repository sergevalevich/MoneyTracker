package com.valevich.moneytracker.ui.activities;

import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.utils.Preferences_;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EActivity
public class SplashActivity extends AppCompatActivity {

    private final int SPLASH_DISPLAY_LENGTH = 2000;

    @ViewById(R.id.splashImage)
    ImageView mImageView;

    @ViewById(R.id.textView)
    TextView mTextView;

    @Pref
    Preferences_ mPreferences;

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
        boolean tokenExists = mPreferences.loftApiToken().exists();
//        if(!tokenExists) {
//            navigateToLogIn();
//        } else {
//            navigateToMain();
//        }
        SignUpActivity_.intent(this).start();
        finish();
    }

    private void navigateToLogIn() {
        LoginActivity_.intent(this).start();
        finish();
    }

    private void navigateToMain() {
        MainActivity_.intent(this).start();
        finish();
    }


}
