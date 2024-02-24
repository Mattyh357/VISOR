/**
 * This class is part of the V.I.S.O.R app.
 * HistoryRecyclerViewAdapter extends RecyclerView.Adapter to provide an adapter to display a
 * list rides.
 *
 * @version 1.0
 * @since 21/02/2024
 */

package com.matt.visor.fragments.history;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.visor.R;
import com.matt.visor.app.recorder.Formatter;
import com.matt.visor.app.recorder.Journey;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryHolder>{

    private final Context _context;
    private final List<Journey> _data;

    /**
     * Constructor for initializing the adapter with context and data
     *
     * @param context The context in which the adapter is operating.
     * @param data The data to populate the RecyclerView with.
     */
    public HistoryRecyclerViewAdapter(Context context, List<Journey> data) {
        _context = context;
        _data = data;
    }

    /**
     * Provides the logic for creating ViewHolder objects for RecyclerView items.
     *
     * @param parent The ViewGroup into which the new View will be added.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds the View for each item.
     */
    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(_context);
        view = mInflater.inflate(R.layout.item_history,parent,false);
        return new HistoryHolder(view);
    }

    /**
     * Binds the data to the ViewHolder in each RecyclerView item.
     * Each item is assigned an onSetClickListener which redirects to detail fragment
     * passing ID of the journey
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {
        holder.datetime.setText(Formatter.epochToDateAndTime(_data.get(position).getTimeFinished()));
        holder.distance.setText(Formatter.formatDistance(_data.get(position).getTotalDistance(), true));
        holder.duration.setText(Formatter.secondsAsElapsedTime((int)_data.get(position).getTimeTotal()));
        holder.avgSpeed.setText(Formatter.formatSpeed(_data.get(position).getAverageSpeed(), true));
        holder.elevation.setText(Formatter.formatDistance(_data.get(position).getTotalElevationClimbed(), true));

        if(_data.get(position).getImage() != null)
            holder.image.setImageBitmap(_data.get(position).getImage());
        else
            holder.image.setImageResource(Journey.DEFAULT_IMAGE);

        //On Click
        holder.cardView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("JourneyID", _data.get(position).getJourneyID());

            //Navigate
            NavController navController = Navigation.findNavController((Activity) _context, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_history_detail, bundle);
        });
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount () {
            return _data.size();
        }

    /**
     * ViewHolder class for caching view components of each RecyclerView item.
     */
    public static class HistoryHolder extends RecyclerView.ViewHolder {

        CardView cardView ;
        TextView datetime;
        ImageView image;
        TextView distance;
        TextView duration;
        TextView avgSpeed;
        TextView elevation;

        /**
         * Constructor to initialize the views for the HistoryHolder.
         *
         * @param itemView The item view to be held by this HistoryHolder.
         */
        public HistoryHolder(View itemView) {
            super(itemView);

            cardView = itemView.findViewById(R.id.item_history_id);

            datetime = itemView.findViewById(R.id.item_history_datetime);
            image =  itemView.findViewById(R.id.item_history_image);

            distance = itemView.findViewById(R.id.item_history_distance);
            duration = itemView.findViewById(R.id.item_history_duration);
            avgSpeed = itemView.findViewById(R.id.item_history_avg_speed);
            elevation = itemView.findViewById(R.id.item_history_elevation);


        }
    }

}