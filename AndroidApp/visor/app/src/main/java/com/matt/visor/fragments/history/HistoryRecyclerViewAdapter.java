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

import com.matt.visor.Journey;
import com.matt.visor.R;

import java.util.List;

public class HistoryRecyclerViewAdapter extends RecyclerView.Adapter<HistoryRecyclerViewAdapter.HistoryHolder>{

    private Context _context;
    private List<Journey> _data;

    public HistoryRecyclerViewAdapter(Context context, List<Journey> data) {
        _context = context;
        _data = data;
    }

    @NonNull
    @Override
    public HistoryHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        LayoutInflater mInflater = LayoutInflater.from(_context);
        view = mInflater.inflate(R.layout.item_history,parent,false);
        return new HistoryHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HistoryHolder holder, int position) {

        holder.datetime.setText(_data.get(position).getTimeFinished());

        holder.duration.setText(_data.get(position).getDuration());
        holder.avgSpeed.setText(_data.get(position).getAvgSpeed());
        holder.elevation.setText(_data.get(position).getElevationClimbed());

        if(_data.get(position).getImage() != null)
            holder.image.setImageBitmap(_data.get(position).getImage());
        else
            holder.image.setImageResource(_data.get(position).getDefaultImage());

        //On Click
        holder.cardView.setOnClickListener(v -> {
            System.out.println("test");


            Bundle bundle = new Bundle();
            bundle.putInt("ItemHistoryPos", position);

            //Navigate
            NavController navController = Navigation.findNavController((Activity) _context, R.id.nav_host_fragment_activity_main);
            navController.navigate(R.id.navigation_history_detail, bundle);
        });
    }


    @Override
    public int getItemCount () {
            return _data.size();
        }


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

            cardView = (CardView) itemView.findViewById(R.id.item_history_id);

            datetime = (TextView) itemView.findViewById(R.id.item_history_datetime);
            image = (ImageView) itemView.findViewById(R.id.item_history_image);

            distance = (TextView) itemView.findViewById(R.id.item_history_distance);
            duration = (TextView) itemView.findViewById(R.id.item_history_duration);
            avgSpeed = (TextView) itemView.findViewById(R.id.item_history_avg_speed);
            elevation = (TextView) itemView.findViewById(R.id.item_history_elevation);


        }
    }

}