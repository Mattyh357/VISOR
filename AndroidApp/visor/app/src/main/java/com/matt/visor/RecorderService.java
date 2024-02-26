/**
 * This class is part of the V.I.S.O.R app.
 * Service responsible for handling background recording of the user Journey. Handles starting,
 * pausing, and resuming of the recordings, as well as saving the recorded data.
 * Utilizes a local binding for communication with the app.
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;

import androidx.core.app.NotificationCompat;

import com.google.android.gms.maps.GoogleMap;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.app.recorder.Recorder;
import com.matt.visor.app.recorder.RecorderListener;

public class RecorderService extends Service {

    private static final String CHANNEL_ID = "ForegroundServiceChannel";
    private static final int NOTIFICATION_ID = 1;
    private Recorder _recorder;
    private final IBinder binder = new LocalBinder();

    /**
     * Initializes the recorder service and its components.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        _recorder = new Recorder((VisorApplication) getApplication());
    }


    // Binder given to clients



    /**
     * Returns the binder to allow interaction with the service.
     *
     * @param intent The intent that was used to bind to the service.
     * @return A binder for clients to interact with the service.
     */
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    /**
     * Binder class to provide the RecorderService instance to clients.
     *
     * @return The current instance of RecorderService.
     */
    public class LocalBinder extends Binder {
        RecorderService getService() {
            return RecorderService.this;
        }
    }

    /**
     * Starts the recorder and foreground notification.
     *
     * @param listener The listener to receive recorder events.
     */
    public void start(RecorderListener listener) {
        _recorder.setListener(listener);

        createNotificationChannel();
        startForeground(NOTIFICATION_ID, getNotification());

        _recorder.start();
    }

    /**
     * Pauses the recording process.
     */
    public void pause() {
        _recorder.pause();
    }

    /**
     * Resumes the recording process.
     */
    public void resume() {
        _recorder.resume();
    }

    /**
     * Saves the current recording data.
     *
     * @param map The GoogleMap instance to be used for thumbnail
     * @param context The context used for saving the data.
     */
    public void saveData(GoogleMap map, Context context) {
        _recorder.saveData(context, map);
    }

    /**
     * Stops the recorder and cleans up resources on service destruction.
     */
    @Override
    public void onDestroy() {
        super.onDestroy();

        _recorder.stop();
    }

    /**
     * Creates a notification for the foreground service.
     *
     * @return The constructed notification.
     */
    private Notification getNotification() {
        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder =new NotificationCompat.Builder(this, CHANNEL_ID)
                .setContentTitle("V.I.S.O.R - RIDE IN PROGRESS")
                .setContentText("Ride in progress.")
                .setSmallIcon(R.drawable.launcher_icon) // TODO better icon
//                .setContentIntent(pendingIntent)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setOnlyAlertOnce(true)
                .setSilent(true);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            builder.setChannelId(CHANNEL_ID);

        return builder.build();
    }


    /**
     * Creates a notification channel for the foreground service.
     */
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
}