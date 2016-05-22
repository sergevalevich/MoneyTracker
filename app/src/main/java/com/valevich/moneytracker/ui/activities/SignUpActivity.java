package com.valevich.moneytracker.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.util.Log;
import android.view.Gravity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.valevich.moneytracker.R;
import com.valevich.moneytracker.network.rest.RestService;
import com.valevich.moneytracker.network.rest.model.UserLoginModel;
import com.valevich.moneytracker.network.rest.model.UserRegistrationModel;
import com.valevich.moneytracker.ui.taskshandlers.SignUpTask;
import com.valevich.moneytracker.utils.NetworkStatusChecker;

import org.androidannotations.annotations.AfterInject;
import org.androidannotations.annotations.AfterViews;
import org.androidannotations.annotations.Background;
import org.androidannotations.annotations.Bean;
import org.androidannotations.annotations.Click;
import org.androidannotations.annotations.EActivity;
import org.androidannotations.annotations.NonConfigurationInstance;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;
import org.androidannotations.annotations.res.StringRes;


@EActivity(R.layout.activity_sign_up)
public class SignUpActivity extends AppCompatActivity {

    private static final String LOG_TAG = SignUpActivity.class.getSimpleName();
    @ViewById(R.id.root)
    RelativeLayout mRootLayout;

    @ViewById(R.id.backgroundImageView)
    ImageView mBackground;

    @ViewById(R.id.userNameField)
    AppCompatEditText mUsernameField;

    @ViewById(R.id.passwordField)
    AppCompatEditText mPasswordField;

    @ViewById(R.id.signUpButton)
    Button mSignUpButton;

    @StringRes(R.string.network_unavailable)
    String mNetworkUnavailableMessage;

    @StringRes(R.string.wrong_auth_input)
    String mWrongInputMessage;

    @NonConfigurationInstance
    @Bean
    SignUpTask mSignUpTask;

    @AfterViews
    void setupViews() {
        Glide.with(this)
                .load(R.drawable.gray_bg)
                .placeholder(R.drawable.gray_bg_placeholder)
                .crossFade()
                .into(mBackground);
    }

    @Click(R.id.signUpButton)
    void submitAccountInfo() {
        Log.d(LOG_TAG,"Click");
        if(NetworkStatusChecker.isNetworkAvailable(this)) {
            String userName = mUsernameField.getText().toString();
            String password = mPasswordField.getText().toString();
            if(userName.length() < 5 || password.length() < 5) {
                notifyUser(mWrongInputMessage);
            } else {
                mSignUpTask.signUp(userName,password);
            }
        } else {
            notifyUser(mNetworkUnavailableMessage);
        }
    }
    public void notifyUser(String message) {
        Snackbar.make(mRootLayout,message,Snackbar.LENGTH_LONG).show();
    }
}
