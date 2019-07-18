package pd.sahang.mas.palembang.smds.ahp;

import android.app.Activity;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationChannelGroup;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;

import com.crashlytics.android.Crashlytics;
import com.google.firebase.FirebaseApp;

import io.fabric.sdk.android.Fabric;
import pd.sahang.mas.palembang.smds.ahp.services.ReceiverNetworkService;

public class App extends Application implements Application.ActivityLifecycleCallbacks {

    public static final String GROUP_ID_UPLOAD = "groupUpload";
    public static final String GROUP_ID_LAPORAN = "groupLaporan";
    public static final String CHANNEL_ID_UPLOAD = "UPLOAD";
    public static final String CHANNEL_ID_LAPORAN = "LAPORAN";

    public static Activity mActivity;

    @Override
    public void onCreate() {
        super.onCreate();
        IntentFilter intentFilter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        getApplicationContext().registerReceiver(new ReceiverNetworkService(), intentFilter);
        registerActivityLifecycleCallbacks(this);
        FirebaseApp.initializeApp(this);
        Fabric.with(this, new Crashlytics());
        createNotificationChannels();
    }

    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannelGroup channelGroupUpload = new NotificationChannelGroup(
                    GROUP_ID_UPLOAD, "Group Upload"
            );

            NotificationChannelGroup channelGroupLaporan = new NotificationChannelGroup(
                    GROUP_ID_LAPORAN, "Group Laporan"
            );

            NotificationChannel channelUpload = new NotificationChannel(
                    CHANNEL_ID_UPLOAD, "Upload", NotificationManager.IMPORTANCE_DEFAULT
            );
            channelUpload.setDescription("Upload");
            channelUpload.setGroup(GROUP_ID_UPLOAD);

            NotificationChannel channelLaporan = new NotificationChannel(
                    CHANNEL_ID_LAPORAN, "Laporan", NotificationManager.IMPORTANCE_HIGH
            );
            channelLaporan.setDescription("Laporan");
            channelLaporan.setGroup(GROUP_ID_LAPORAN);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannelGroup(channelGroupUpload);
            manager.createNotificationChannelGroup(channelGroupLaporan);
            manager.createNotificationChannel(channelUpload);
            manager.createNotificationChannel(channelLaporan);
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        activity.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {
        mActivity = activity;
        sendBroadcast(new Intent(this, ReceiverNetworkService.class));
    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}
