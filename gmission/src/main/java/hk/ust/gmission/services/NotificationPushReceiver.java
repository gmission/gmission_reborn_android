package hk.ust.gmission.services;


import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.baidu.android.pushservice.PushMessageReceiver;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import hk.ust.gmission.models.Message;
import hk.ust.gmission.ui.activities.HitActivity;
import hk.ust.gmission.util.BaiduPushUtils;
import hk.ust.gmission.util.GsonUtil;


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

        String customContent = msg;

        Message message = null;

        if (customContent != null && customContent.length() != 0) {
            try {
                message = GsonUtil.getGson().fromJson(customContent, Message.class);
                // ...
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(message!=null){
            Log.d(TAG, "用户收到了新通知" + message.getType());
            if( message.getType().equals(Message.MESSAGE_TYPE_ANSWER)) {
                Log.d(TAG, "用户收到了新答案，发送broadcase");
                Intent msgIntent = new Intent(context, HitActivity.class);
//                msgIntent.putExtra("message", message);

                context.sendBroadcast(msgIntent);
            } else if (message.getType().equals(Message.MESSAGE_TYPE_FEED)){
                Log.d(TAG, "用户收到了新任务，发送broadcase");
                Intent msgIntent = new Intent(context, HitActivity.class);
//                msgIntent.putExtra("message", message);

                context.sendBroadcast(msgIntent);
            }

            content = message.getContent();

            NotificationHelper.showMessageNotification(context, nm, title, content, message);
        }
    }


    @Override
    public void onNotificationClicked(Context context, String title,
                                      String description, String customContentString) {
        String notifyString = "Notification clicked title=\"" + title + "\" description=\""
                + description + "\" customContent=" + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        //TODO: response for notification clicked
    }


    @Override
    public void onNotificationArrived(Context context, String title,
                                      String description, String customContentString) {

        String notifyString = "onNotificationArrived  title=\"" + title
                + "\" description=\"" + description + "\" customContent="
                + customContentString;
        Log.d(TAG, notifyString);

        // 自定义内容获取方式，mykey和myvalue对应通知推送时自定义内容中设置的键和值
        if (!TextUtils.isEmpty(customContentString)) {
            JSONObject customJson = null;
            try {
                customJson = new JSONObject(customContentString);
                String myvalue = null;
                if (!customJson.isNull("mykey")) {
                    myvalue = customJson.getString("mykey");
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        //TODO: response for notification received

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