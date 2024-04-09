/**
 * This class is part of the V.I.S.O.R app.
 * HistoryDetailFragment is responsible for displaying detailed information about specific journey.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.fragments.history;

import static androidx.core.content.ContextCompat.getSystemService;

import android.app.DownloadManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.R;
import com.matt.visor.TableKvpAdapter;
import com.matt.visor.TableKvpItem;
import com.matt.visor.app.recorder.Formatter;
import com.matt.visor.app.recorder.Journey;
import com.matt.visor.app.recorder.JourneyLoaderAndSaver;
import com.matt.visor.databinding.FragmentHistoryDetailBinding;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class HistoryDetailFragment extends Fragment {

    private static final String NOTIFICATION_TITLE = "GPX file downloaded.";
    private static final String NOTIFICATION_CHANNEL_ID = "file_download_channel";

    private FragmentHistoryDetailBinding _binding;

    /**
     * Inflates the layout for the fragment's view and displays journey details including images and data.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state.
     * @return The View for the fragment's UI.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();


        Bundle bundle = getArguments();
        if(bundle != null){
            Journey journey = JourneyLoaderAndSaver.getJourneyById(bundle.getString("JourneyID"), getContext());
            printData(journey);
            updateImage(journey.getImage());

            // Export button
            _binding.historyDetailBtnExport.setOnClickListener(v -> exportGPX(journey));
        }



        return root;
    }

    /**
     * Saves GPX file in the Download folder using current date and time as filename.
     * If save is successful, displays notification.
     *
     * @param journey Object containing journey that will be used to get the GPX file from.
     */
    private void exportGPX(Journey journey) {
        System.out.println(" EXPORTING ");

        File gpxFile = journey.gptGpxFile();

        // File name
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
        String dateTimeString = now.format(formatter);
        String destinationFileName = dateTimeString + ".gpx";

        File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File destinationFile = new File(downloadsDir, destinationFileName);

        try (FileChannel inChannel = new FileInputStream(gpxFile).getChannel();
             FileChannel outChannel = new FileOutputStream(destinationFile).getChannel()) {
            inChannel.transferTo(0, inChannel.size(), outChannel);

            // Show Notification
            showDownloadCompleteNotification(destinationFileName);

        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Problem during saving.", Toast.LENGTH_SHORT).show();
        }

    }

    /**
     * Displays notification informing the user that the file was saved. On click used Intent to open
     * Download Folder.
     *
     * @param filename String containing the name of the saved file
     */
    private void showDownloadCompleteNotification(String filename) {
        // Create channel
        NotificationChannel serviceChannel = new NotificationChannel(
                NOTIFICATION_CHANNEL_ID,
                NOTIFICATION_TITLE,
                NotificationManager.IMPORTANCE_DEFAULT
        );
        NotificationManager manager = getSystemService(getContext(), NotificationManager.class);
        manager.createNotificationChannel(serviceChannel);

        // Notification
        NotificationManager notificationManager = (NotificationManager) getContext().getSystemService(getContext().NOTIFICATION_SERVICE);

        Intent intent = new Intent(DownloadManager.ACTION_VIEW_DOWNLOADS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(getContext(), 0, intent, PendingIntent.FLAG_IMMUTABLE);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(android.R.drawable.stat_sys_download_done)
                .setContentTitle(NOTIFICATION_TITLE)
                .setContentText("Saved: " + filename)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true);

        builder.setChannelId(NOTIFICATION_CHANNEL_ID);

        notificationManager.notify(0, builder.build());
        Toast.makeText(getContext(), "Exported!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Updates the displayed image for the journey.
     *
     * @param image The image bitmap to display. If null, sets a default image.
     */
    private void updateImage(Bitmap image) {
        if(image != null)
            _binding.historyDetailImage.setImageBitmap(image);
        else
            _binding.historyDetailImage.setImageResource(Journey.DEFAULT_IMAGE);
    }

    /**
     * Displays detailed data of the journey in the UI, including distance, total time, start and end times, and more.
     *
     * @param journey The journey object containing the data to be displayed.
     */
    private void printData(Journey journey) {

        String distance     = Formatter.formatDistance(journey.getTotalDistance(), true);
        String timeTotal    = Formatter.secondsAsElapsedTime((int)journey.getTimeTotal());
        String timeStart    = Formatter.epochToDateAndTime(journey.getTimeStarted());
        String timeEnd      = Formatter.epochToDateAndTime(journey.getTimeFinished());


        System.out.println("Time started: " + journey.getTimeStarted() + " - " +  timeStart);
        System.out.println("Time ended: " + journey.getTimeFinished() + " - " +  timeEnd);

        String speedAvg     = Formatter.formatDistance(journey.getTotalDistance(), true);
        String eleUp        = Formatter.formatDistance(journey.getTotalElevationClimbed(), true);
        String eleDown      = Formatter.formatDistance(journey.getGet_totalElevationDescended(), true);

        List<TableKvpItem<?>> data = new ArrayList<>();
        data.add(new TableKvpItem<>("", "Distance", distance));

        data.add(new TableKvpItem<>("", "Duration", timeTotal));
        data.add(new TableKvpItem<>("", "Started", timeStart));
        data.add(new TableKvpItem<>("", "Ended", timeEnd));

        data.add(new TableKvpItem<>("", "Average speed", speedAvg));

        data.add(new TableKvpItem<>("", "Elevation climbed", eleUp));
        data.add(new TableKvpItem<>("", "Elevation descended", eleDown));


        // RecyclerView
        TableKvpAdapter _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_list, null);
        _binding.historyDetailTableKvp.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.historyDetailTableKvp.setAdapter(_rva);
    }

    /**
     * Cleans up resources and nullifies the binding when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}