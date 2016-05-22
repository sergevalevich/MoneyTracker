package com.valevich.moneytracker.ui.activities;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.valevich.moneytracker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;
import org.w3c.dom.Text;

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
                MainActivity_.intent(SplashActivity.this).start();
                finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }
}
