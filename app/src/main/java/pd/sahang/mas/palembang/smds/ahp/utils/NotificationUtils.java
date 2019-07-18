package pd.sahang.mas.palembang.smds.ahp.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.SystemClock;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import pd.sahang.mas.palembang.smds.ahp.App;
import pd.sahang.mas.palembang.smds.ahp.R;

import static pd.sahang.mas.palembang.smds.ahp.App.CHANNEL_ID_LAPORAN;

public class NotificationUtils extends ContextWrapper {

    private static final int REQUEST_CODE_NOTIFICATION = 76435;
    private static final int ID_LARGE_NOTIFICATION = 2365;
    private static final int ID_SMALL_NOTIFICATION = 2354;

    private static final String CHANNEL_ID = App.mActivity.getPackageName();
    private static final CharSequence CHANNEL_NAME = App.mActivity.getString(R.string.app_name);
    private static final String GROUP_LAPORAN = "GROUP_LAPORAN";

    private final Context mContext;
    private PendingIntent pendingIntent;
    private Uri uri;

    private Notification mNotification;
    private NotificationManager mNotificationManager;
    private NotificationChannel mNotificationChannel;
    private NotificationManagerCompat mNotificationManagerCompat;
    private NotificationCompat.Builder mBuilder;
    private NotificationCompat.BigPictureStyle mBigPictureStyle;

    public NotificationUtils(Context base) {
        super(base);
        mContext = base;
    }

    public void Laporan(int id, String title, String subtitle, String message) {
        uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID_LAPORAN)
                .setSmallIcon(R.drawable.ic_notifications_white_24dp)
                .setContentTitle(title)
                .setSubText(subtitle)
                .setContentText(message)
                .setOngoing(false)
                .setOnlyAlertOnce(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setCategory(NotificationCompat.CATEGORY_MESSAGE)
                .setGroup("Group Laporan")
                .setSound(uri);
        mNotification = new NotificationCompat.Builder(mContext, CHANNEL_ID_LAPORAN)
                .setSmallIcon(R.drawable.ic_notifications_grey_24dp)
                .setStyle(new NotificationCompat.InboxStyle()
                    .addLine(title).addLine(title)
                    .setSummaryText("Laporan"))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setGroup("Group Laporan")
                .setGroupAlertBehavior(NotificationCompat.GROUP_ALERT_CHILDREN)
                .setGroupSummary(true)
                .build();
        mNotificationManager.notify(id, mBuilder.build());
        SystemClock.sleep(2000);
        mNotificationManager.notify(0, mNotification);
    }
}