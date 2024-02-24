/**
 * This class is part of the V.I.S.O.R app.
 * TableKvpAdapter extends RecyclerView.Adapter to provide an generic adapter for KvpItem
 *
 * @version 1.0
 * @since 21/02/2024
 */


package com.matt.visor;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Map;

public class TableKvpAdapter extends RecyclerView.Adapter<TableKvpAdapter.ViewHolder> {

    private final int _itemLayout;
    private final Context _context;
    private final List<TableKvpItem<?>> _data;
    private final TableKvpOnClickListener _onClickListener;

    /**
     * Constructor for initializing the adapter with context, data, layout, and click listener.
     *
     * @param context The context in which the adapter is operating.
     * @param data The data to populate the RecyclerView with.
     * @param itemLayout The layout resource ID for each item.
     * @param onClickListener The listener for item click events.
     */
    public TableKvpAdapter(Context context, List<TableKvpItem<?>> data, int itemLayout, TableKvpOnClickListener onClickListener) {

        _context = context;
        _itemLayout = itemLayout;
        _data = data;
        _onClickListener = onClickListener;

    }

    /**
     * Updates the adapter's data with a new map of values and notifies changes.
     *
     * @param newData The new data map to update the adapter with.
     */
    public void update(Map<String, Object> newData) {
        for (int i = 0; i < _data.size(); i++) {
            String key = _data.get(i).getKey();
            if(newData.containsKey(key)) {
                Object value = newData.get(key);
                if(value != null) {
                    _data.get(i).setValue(value);
                    notifyItemChanged(i);
                }
            }
        }
    }

    /**
     * Updates a specific item in the adapter based on key and notifies changes.
     *
     * @param key The key of the item to update.
     * @param value The new value for the item.
     */
    public void update(String key, Object value) {
        for (int i = 0; i < _data.size(); i++) {
            if(_data.get(i).setValueIfKey(key, value))
                notifyItemChanged(i);
        }
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
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(_context);
        view = mInflater.inflate(_itemLayout, parent,false);
        return new ViewHolder(view);
    }

    /**
     * Binds the data to the ViewHolder in each RecyclerView item.
     * Based on the <T> of the value in TableKvpItem does different things :)
     * It's like magic! (or rly bad code... depends how you look at it I guess)
     *
     * @param holder The ViewHolder which should be updated to represent the contents of the item.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        TableKvpItem<?> item = _data.get(position);

        // Value
        Object value = item.getValue();

        if(value instanceof String) {
            holder.txtKey.setText(item.getKeyReadable());
            holder.txtValue.setText((String) value);
        }
        else if (value instanceof Boolean) {
            // For toggle
            holder.toggle.setText(item.getKeyReadable());
            holder.toggle.setChecked((Boolean) value);
            holder.toggle.setOnClickListener(v -> onItemClick(item));
        } else if (value instanceof Bitmap) {
            // BitMap
            holder.imageView.setImageBitmap((Bitmap) value);
            holder.imageView.setOnClickListener(v -> onItemClick(item));
        }

    }

    /**
     * Handles item click events, delegating to the registered click listener.
     *
     * @param item The item that was clicked.
     */
    private void onItemClick(TableKvpItem<?> item) {
        if (_onClickListener != null)
            _onClickListener.onItemClick(item);
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
    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView txtKey;

        // Different values
        public TextView txtValue;
        public SwitchMaterial toggle;
        public ImageView imageView;


        public ViewHolder(View view) {
            super(view);

            txtKey = view.findViewById(R.id.kvp_txt_key);

            // Values
            txtValue = view.findViewById(R.id.kvp_txt_value);
            toggle = view.findViewById(R.id.kvp_toggle);
            imageView = view.findViewById(R.id.kvp_image);
        }

    }


}














