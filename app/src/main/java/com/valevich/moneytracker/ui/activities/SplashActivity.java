package com.valevich.moneytracker.ui.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.valevich.moneytracker.MoneyTrackerApplication_;
import com.valevich.moneytracker.R;

import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.ViewById;

@EActivity(R.layout.activity_splash)
public class SplashActivity extends AppCompatActivity {

    @ViewById(R.id.splashImage)
    ImageView mImageView;

    @ViewById(R.id.textView)
    TextView mTextView;


    @AfterViews
    void showLogo() {
        final Animation logoAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_slide_down);
        final Animation labelAnimation = AnimationUtils.loadAnimation(this, R.anim.splash_slide_up);
        final Animation fadeOutLogo = AnimationUtils.loadAnimation(this, R.anim.splash_fade_out);
        final Animation fadeOutLabel = AnimationUtils.loadAnimation(this, R.anim.splash_fade_out);
        logoAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTextView.setVisibility(View.VISIBLE);
                mTextView.startAnimation(labelAnimation);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        labelAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.startAnimation(fadeOutLogo);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOutLogo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mImageView.setVisibility(View.INVISIBLE);
                mTextView.startAnimation(fadeOutLabel);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        fadeOutLabel.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mTextView.setVisibility(View.INVISIBLE);
                if (!MoneyTrackerApplication_.isUserRegistered()) {
                    navigateToLogIn();
                } else {
                    navigateToMain();
                }
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        mTextView.setVisibility(View.INVISIBLE);
        mImageView.startAnimation(logoAnimation);
    }

    private void navigateToLogIn() {
        Intent intent = LoginActivity_.intent(this).get();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void navigateToMain() {
        MainActivity_.intent(this).start();
        finish();
    }
}
