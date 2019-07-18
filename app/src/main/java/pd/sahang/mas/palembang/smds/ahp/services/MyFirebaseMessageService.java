package pd.sahang.mas.palembang.smds.ahp.services;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Random;

import pd.sahang.mas.palembang.smds.ahp.utils.NotificationUtils;
import pd.sahang.mas.palembang.smds.ahp.utils.SharedPrefFirebase;

public class MyFirebaseMessageService extends FirebaseMessagingService {

    private static final String TAG = MyFirebaseMessageService.class.getSimpleName();

    NotificationUtils notificationUtils;
    final int notificationID = new Random().nextInt(3000);
    @Override
    public void onNewToken(String s) {
        Log.d(TAG, "onNewToken: " + s);
        SharedPrefFirebase.getInstance(getApplicationContext()).saveDeviceToken(s);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        notificationUtils = new NotificationUtils(getApplicationContext());
        if (remoteMessage.getData().size() > 0) {
            Log.e(TAG, "Data Payload: " + remoteMessage.getData().toString());
            try {
                JSONObject json = new JSONObject(remoteMessage.getData());
                sendPushNotification(json);
            } catch (Exception e) {
                Log.e(TAG, "Exception: " + e.getMessage());
            }
        }
        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message Notification Body: " + remoteMessage.getNotification().getBody());
        }
    }

    private void sendPushNotification(@NonNull JSONObject json) {
        Log.e(TAG, "Notification JSON " + json.toString());
        try {
            String title = json.getString("title");
            String subtitle = json.getString("subtitle");
            String message = json.getString("message");

            notificationUtils.Laporan(notificationID, title, subtitle, message);

            Log.e(TAG, "sendPushNotification: " + json.toString());
        } catch (JSONException e) {
            Log.e(TAG, "sendPushNotification: ", e);
        } catch (Exception e) {
            Log.e(TAG, "sendPushNotification: ", e);
        }
    }
}
