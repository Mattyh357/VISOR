/**
 * This class is part of the V.I.S.O.R app.
 * SensorRecyclerViewAdapter extends RecyclerView.Adapter to provide an adapter for sensor item
 * with an onclickListener that navigates to the appropriate fragment.
 *
 * @version 1.0
 * @since 21/02/2024
 */


package com.matt.visor.fragments.home;


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

import com.matt.visor.app.MySensor;
import com.matt.visor.R;

import java.util.List;

public class SensorRecyclerViewAdapter extends RecyclerView.Adapter<SensorRecyclerViewAdapter.SensorHolder>{

    private final Context _context;
    private final List<MySensor> _data;

    public SensorRecyclerViewAdapter(Context context, List<MySensor> data) {
        _context = context;
        _data = data;
    }

    @NonNull
    @Override
    public SensorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(_context);
        view = mInflater.inflate(R.layout.item_sensor,parent,false);
        return new SensorHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SensorHolder holder, int position) {
        holder.name.setText(_data.get(position).getName());
        holder.status.setText(_data.get(position).getStatusString());
        holder.image.setImageResource(_data.get(position).getImage());

        //On Change
        _data.get(position).setStatusChangeListener(() -> notifyItemChanged(position));

        //On Click
        holder.cardView.setOnClickListener(view -> {
            // Bundle
            Bundle bundle = new Bundle();
            bundle.putInt("SensorType", position);

            //Navigate
            NavController navController = Navigation.findNavController((Activity) _context, R.id.nav_host_fragment_activity_main);

            if (_data.get(position).hasAddress())
                navController.navigate(R.id.navigation_sensor_detail, bundle);
        });
    }

        @Override
        public int getItemCount () {
            return _data.size();
        }


    public static class SensorHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView status;
        ImageView image;
        CardView cardView ;

        /**
         * Constructor to initialize the views for the SensorHolder.
         *
         * @param itemView The item view to be held by this SensorHolder.
         */
        public SensorHolder(View itemView) {
            super(itemView);

            name = itemView.findViewById(R.id.sensor_item_name) ;
            status = itemView.findViewById(R.id.sensor_item_status) ;
            image = itemView.findViewById(R.id.sensor_item_image);
            cardView = itemView.findViewById(R.id.sensor_item_id);


        }
    }

}