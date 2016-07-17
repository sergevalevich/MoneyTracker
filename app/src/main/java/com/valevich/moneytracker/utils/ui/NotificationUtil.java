package com.valevich.moneytracker.utils.ui;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;

import com.valevich.moneytracker.R;
import com.valevich.moneytracker.ui.activities.MainActivity_;
import com.valevich.moneytracker.utils.ConstantsManager;
import com.valevich.moneytracker.utils.Preferences_;

import org.androidannotations.annotations.EBean;
import org.androidannotations.annotations.RootContext;
import org.androidannotations.annotations.SystemService;
import org.androidannotations.annotations.res.StringRes;
import org.androidannotations.annotations.sharedpreferences.Pref;

@EBean
public class NotificationUtil {

    private final static int REQUEST_CODE = 0;

    @RootContext
    Context mContext;

    @SystemService
    NotificationManager mNotificationManager;

    @StringRes(R.string.app_name)
    String mContentTitle;

    @StringRes(R.string.notification_message)
    String mContentText;

    @Pref
    Preferences_ mPreferences;

    private static final int NOTIFICATION_ID = 0;

    public void updateNotification() {
        boolean displayNotification = mPreferences.notificationPreference().get();
        if(displayNotification) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(mContext);
            Intent intent = MainActivity_.intent(mContext).intentId(ConstantsManager.NOTIFICATION_INTENT_ID).get();
            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
            PendingIntent pendingIntent = PendingIntent.getActivity(mContext, REQUEST_CODE,intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(pendingIntent);
            builder.setSmallIcon(R.drawable.ic_notification);

            boolean indicatorEnabled = mPreferences.indicatorPreference().get();
            boolean soundEnabled = mPreferences.soundPreference().get();
            boolean vibrationEnabled = mPreferences.vibrationPreference().get();
            if(indicatorEnabled)
            builder.setLights(Color.CYAN,300,1500);
            if(vibrationEnabled)
            builder.setVibrate(new long[]{1000,1000,1000,1000,1000});
            if(soundEnabled)
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

            builder.setAutoCancel(true);
            builder.setContentTitle(mContentTitle);
            builder.setContentText(mContentText);

            Bitmap largeIcon = BitmapFactory
                    .decodeResource(mContext.getResources(),R.mipmap.ic_launcher);
            builder.setLargeIcon(largeIcon);

            Notification notification = builder.build();

            mNotificationManager.notify(NOTIFICATION_ID,notification);
        }

    }
}
