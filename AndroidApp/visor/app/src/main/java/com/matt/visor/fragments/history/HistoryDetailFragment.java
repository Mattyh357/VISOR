/**
 * This class is part of the V.I.S.O.R app.
 * HistoryDetailFragment is responsible for displaying detailed information about specific journey.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.fragments.history;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.matt.visor.R;
import com.matt.visor.TableKvpAdapter;
import com.matt.visor.TableKvpItem;
import com.matt.visor.app.recorder.Formatter;
import com.matt.visor.app.recorder.Journey;
import com.matt.visor.app.recorder.JourneyLoaderAndSaver;
import com.matt.visor.databinding.FragmentHistoryDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailFragment extends Fragment {

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
        }
        return root;
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