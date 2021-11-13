package com.teamnova.ptmanager.service.chatting;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;


import com.teamnova.ptmanager.R;
import com.teamnova.ptmanager.ui.changehistory.inbody.InBodyModifyActivity;
import com.teamnova.ptmanager.ui.chatting.ChattingActivity;
import com.teamnova.ptmanager.ui.splash.SplashActivity;

/** 알림과 메시지를 전송하는 서비스 */
/*
public class ChattingNotificationService extends FirebaseMessagingService {
    // TrainerHomeAct or MemberHomeAct의 running 여부
    // 타입(트레이너, 회원)에 따라 onCreate에서 true, onDestroy에서 false로 변경한다.
    // 아니네 이렇게 하면
    public static boolean isActRunning = false;


    public ChattingNotificationService() {}

    // 메시지 수신 시 액티비티의 상태에 따라 다르게 처리한다.
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        // 1. 여기에 오나?
        System.out.println("여기 오나?");

        // 액티비티가
        if(isActRunning){

        } else {

        }
        // 메시지 전송
        sendNotification(remoteMessage);

    }


    private void sendNotification(RemoteMessage remoteMessage) {
        // 여기로 들어오나?
        System.out.println("여기 오나?2222");

        Intent intent = new Intent(this, ChattingActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 */
/* Request code *//*
, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("title") // 이부분은 어플 켜놓은 상태에서 알림 메세지 받으면 저 텍스트로 띄워준다.
                .setContentText(remoteMessage.getData().get("message"))
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 */
/* ID of notification *//*
, notificationBuilder.build());
    }


    // 새로운 토큰 생성 시 서버에 저장!
    @Override
    public void onNewToken(@NonNull String token) {
        System.out.println("현재토큰:" + token);
        super.onNewToken(token);
    }

    // 현재토큰 가져오기
    public static String getToken(){
        final String[] token = {null};

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            return;
                        }

                        // Get new FCM registration token
                        token[0] = task.getResult();
                    }
                });

        return token[0];
    }
}*/
