package sindh.agriculureextension.fadiary.fcm;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import sindh.agriculureextension.fadiary.R;
import sindh.agriculureextension.fadiary.database.Queries;

public class FCMService extends FirebaseMessagingService {

    private static final String NOTIFICATION_CHANNEL_ID = "Information Channel";
    private static final String CHANNEL_NAME = "Information Channel";
    private static final int NOTIFICATION_ID = 23835;

    private NotificationManager notificationManager;

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        //Push FCM token to Database
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        //Get message data
        System.out.println("DATA NOTIFICATION : " + remoteMessage.getData());
        System.out.println("NOTIFICATION: " + remoteMessage.getNotification());
        if (remoteMessage.getData() != null && remoteMessage.getData().get(Queries.TITLE) != null)
            notifyUser(remoteMessage.getData().get(Queries.TITLE), remoteMessage.getData().get(Queries.MESSAGE));
        else {
            RemoteMessage.Notification notification = remoteMessage.getNotification();

            if (notification != null) {
                notifyUser(notification.getTitle(), notification.getBody());
            }
        }

    }

    private void notifyUser(String title, String msg) {

        //check app version for notification channel handling because of api level 28+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannel();
        }
        //indeterminate true because size and tasks aren't defined in numbers
        NotificationCompat.Builder notification = new NotificationCompat.Builder(getApplicationContext(), NOTIFICATION_CHANNEL_ID)
                .setAutoCancel(false)
                .setContentTitle(title)
                .setContentText(msg)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(NotificationCompat.CATEGORY_ALARM)
                .setSmallIcon(R.mipmap.ic_launcher_foreground);

        //notification service
        notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        assert notificationManager != null;
        notificationManager.notify(NOTIFICATION_ID, notification.build());
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void createChannel() {
        //Notification channel should only be created for devices running Android 26
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID
                    , CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(notificationChannel);
            }
        }

    }
}
