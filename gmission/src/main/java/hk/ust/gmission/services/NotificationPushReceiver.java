package hk.ust.gmission.services;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

import java.util.List;

import hk.ust.gmission.core.Constants;
import hk.ust.gmission.models.Message;
import hk.ust.gmission.ui.activities.HitActivity;
import hk.ust.gmission.util.BaiduPushUtils;
import hk.ust.gmission.util.GsonUtils;


public class NotificationPushReceiver extends PushMessageReceiver {
    /** TAG to Log */
    public static final String TAG = NotificationPushReceiver.class
            .getSimpleName();

    private NotificationManager nm;

    @Override
    public void onBind(Context context, int errorCode, String appid,
                       String userId, String channelId, String requestId) {
        String responseString = "onBind errorCode=" + errorCode + " appid="
                + appid + " userId=" + userId + " channelId=" + channelId
                + " requestId=" + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            BaiduPushUtils.setBind(context, true);
            BaiduPushUtils.setBaiduPushUserID(context, userId);
            BaiduPushUtils.setBaiduPushChannelID(context, channelId);
        }

    }

    @Override
    public void onMessage(Context context, String msg,
                          String customContentString) {
        String messageString = "透传消息 message=\"" + msg
                + "\" customContentString=" + customContentString;

        Log.d(TAG, messageString);

        if (null == nm) {
            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        }

        String title = "gMission";
        String content = "";

        Message message = null;

        if (msg != null && msg.length() != 0) {
            try {
                message = GsonUtils.getGson().fromJson(msg, Message.class);
                // ...
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(message!=null){

            content = message.getContent();

            NotificationHelper.showMessageNotification(context, nm, title, content, message);
        }
    }

//    @Override
//    public void onMessage(Context context, String msg,
//                          String customContentString) {
//        String messageString = "透传消息 message=\"" + msg
//                + "\" customContentString=" + customContentString;
//
//        Log.d(TAG, messageString);
//
//        if (null == nm) {
//            nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//
//        String title = "gMission";
//        String content = "";
//
//        String customContent = msg;
//        Log.d(TAG, "用户收到了新答案，发送broadcase");
////        Intent msgIntent = new Intent(context, HitActivity.class);
////
////        context.sendBroadcast(msgIntent);
//
//        Message message = new Message();
//        message.setType(Message.MESSAGE_TYPE_ANSWER);
//        message.setAttachment("2");
//        NotificationHelper.showMessageNotification(context, nm, title, content, message);
//
//    }

    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {

        //TODO: response for notification clicked
    }


    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {

        Log.d(TAG, title);

    }

    @Override
    public void onSetTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags, String requestId) {
    }


    @Override
    public void onDelTags(Context context, int errorCode,
                          List<String> successTags, List<String> failTags, String requestId) {
    }


    @Override
    public void onListTags(Context context, int errorCode, List<String> tags,
                           String requestId) {
    }


    @Override
    public void onUnbind(Context context, int errorCode, String requestId) {
        String responseString = "onUnbind errorCode=" + errorCode
                + " requestId = " + requestId;
        Log.d(TAG, responseString);

        if (errorCode == 0) {
            BaiduPushUtils.setBind(context, false);
        }
    }


}