package com.matt.visor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

public class RecorderService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private Recorder _recorder;

    private final IBinder binder = new LocalBinder();

    @Override
    public void onCreate() {
        super.onCreate();
        _recorder = new Recorder();
    }

    public void start(RecorderListener listener) {
        _recorder.setListener(listener);

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());

        _recorder.start();
    }

    public void pause() {
        _recorder.pause();
    }

    public void resume() {
        _recorder.resume();
    }


    public void getAllData() {

        // TODO get all data
        System.out.println("Getting all data");
        System.out.println("Getting all data");
        System.out.println("Getting all data");
        System.out.println("Getting all data");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        _recorder.stop();
    }




    private Notification getNotification() {
        String text = _recorder.isPaused() ? "Recording" : "Paused";
        text += " - ";
        text += Utils.secondsToTime(_recorder.getTime());

        // TODO fix the fact that it's not changing ... and that its in two methods for some stupid reason :D

        return getNotification(text);
    }

    private Notification getNotification(String contentText) {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("V.I.S.O.R - RIDE IN PROGRESS")
                .setContentText(contentText)
                .setSmallIcon(R.drawable.launcher_icon) // TODO better icon
//                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setSilent(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder.setChannelId(CHANNEL_ID);

        return builder.build();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    CHANNEL_ID,
                    "Foreground Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }






    // Binder given to clients

    public class LocalBinder extends Binder {
        RecorderService getService() {
            return RecorderService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }
}