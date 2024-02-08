// TODO remove after testing of HUD image sender

package com.matt.visor.fragments.home;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.matt.visor.R;

import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {

    private Context _context;
    private List<ImageButtonItem> _data;

    private OnImageButtonClickListener _listener;


    public RecyclerViewAdapter(Context context, OnImageButtonClickListener listener, List<ImageButtonItem> data) {
        _listener = listener;
        _context = context;
        _data = data;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(_context);
        view = mInflater.inflate(R.layout.image_button_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerViewAdapter.MyViewHolder holder, int position) {
//        holder.title.setText(_data.get(position).getTitle());
        holder.image.setImageBitmap(_data.get(position).getImageBitmap());
        holder.image.setOnClickListener(view -> _listener.onItemClick(position));
    }

    @Override
    public int getItemCount() {
        return _data.size();
    }


    public interface OnImageButtonClickListener {
        void onItemClick(int position);
    }

    /**
     * MyViewHolder describes an item view and metadata about its place within the RecyclerView.
     */
    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title;
        ImageView image;

        /**
         * Constructor to initialize the views for the MyViewHolder.
         *
         * @param itemView The item view to be held by this MyViewHolder.
         */
        public MyViewHolder(View itemView) {
            super(itemView);

//            title = (TextView) itemView.findViewById(R.id.image_button_title);
            image = (ImageView) itemView.findViewById(R.id.image_button_image);


        }
    }

}