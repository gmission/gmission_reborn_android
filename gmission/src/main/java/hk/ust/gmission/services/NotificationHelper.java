package hk.ust.gmission.services;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import hk.ust.gmission.R;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.ui.activities.AnswerListActivity;
import hk.ust.gmission.ui.activities.HitActivity;
import hk.ust.gmission.ui.activities.MainActivity;

import static hk.ust.gmission.core.Constants.Extra.HIT_ID;
import static hk.ust.gmission.core.Constants.Extra.IS_VIEW_ANSWER;

/**
 * Created by rui on 14-6-7.
 */
public class NotificationHelper {
    public static void showMessageNotification(
            Context context, NotificationManager nm,
            String title, String msgContent, Message message) {

        Intent select = new Intent();
        select.setClass(context, MainActivity.class);
        select.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);


        Notification notification = new NotificationCompat.Builder(context)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("gMission Message")
                .setOnlyAlertOnce(true)
                .setContentText(msgContent).build();


        int notificationId = getNofiticationID();

        PendingIntent contentIntent = null;
        if(message.getType().equals(Message.MESSAGE_TYPE_FEED)){
            Intent notificationIntent = new Intent(context, HitActivity.class);
            notificationIntent.putExtra(HIT_ID, message.getAttachment());
            // # TODO notification with location
            contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else if(message.getType().equals(Message.MESSAGE_TYPE_ANSWER)){
            Intent notificationIntent = new Intent(context, AnswerListActivity.class);
            notificationIntent.putExtra(HIT_ID, message.getAttachment());
            contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        } else {
            contentIntent = PendingIntent.getActivity(context, 0, select, PendingIntent.FLAG_UPDATE_CURRENT);
        }
        notification.contentIntent = contentIntent;

        showMessageNotificationLocal(context, nm, notification, notificationId);
    }

    private static void showMessageNotificationLocal(
            Context context, NotificationManager nm, Notification notification, int notificationId) {
        notification.ledARGB = 0xff00ff00;
        notification.ledOnMS = 300;
        notification.ledOffMS = 1000;
        notification.flags |= Notification.FLAG_SHOW_LIGHTS;
        notification.flags |= Notification.FLAG_AUTO_CANCEL;

        notification.defaults = Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE;

        nm.notify(notificationId, notification);
    }

    public static int getNofiticationID() {
        return 1024;
    }
}
