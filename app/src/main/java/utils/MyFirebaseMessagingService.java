package utils;

import android.content.Intent;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.helpme.helpmemrboband.MainActivity;

import java.util.Map;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "iKosmo";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "onMessageReceived() 호출됨.");

        // 앱이 실행 중일 때 알림 메시지가 올 경우 처리
        // 메시지 안에 payload 데이터가 있을 경우 : 가령 message:value
        if (remoteMessage.getData().size() > 0) {
            Map<String, String> data = remoteMessage.getData();
            Log.d(TAG, "Message data payload: " + data.toString());

            String contents = data.get("message");
            Log.d(TAG, "contents message: " + contents);

            processMsg(contents);
        }

        // 시스템 알림으로 오는 제목과 내용을 후킹할 수 있다.
        // 그러나 앱이 실행되고 있을 때는 굳이 처리할 필요가 없다.

        if (remoteMessage.getNotification() != null) {
            String notiTitle = remoteMessage.getNotification().getTitle();
            String notiBody = remoteMessage.getNotification().getBody();
            Log.d(TAG, "Message Notification Title: " + notiTitle);
            Log.d(TAG, "Message Notification Body: " + notiBody);
        }
    }

    private void processMsg(String contents) {
        // 메시지가 왔을 때의 처리를 만든다.
        // 이 예제에서는 메시지 내용을 메인으로 보낸다.
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.putExtra("message", contents);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_SINGLE_TOP);

        getApplicationContext().startActivity(intent);
    }

    @Override
    public void onNewToken(String token) {
        Log.d(TAG, "Refreshed token: " + token);

        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }
}
