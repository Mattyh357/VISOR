package com.matt.visor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.List;
import java.util.Map;

public class TableKvpAdapter extends RecyclerView.Adapter<TableKvpAdapter.ViewHolder> {

    private int _itemLayout;
    private Context _context;
    private List<TableKvpItem> _data;

    private TableKvpOnClickListener _onClickListener;

    // TODO delete
//    public TableKvpAdapter(Context context, Map<String, String> data) {
//        _context = context;
//
//        _data = new ArrayList<>();
//        for (Map.Entry<String, String> entry : data.entrySet()) {
//            _data.add(new TableKvpItem(entry.getKey(), entry.getValue()));
//        }
//    }
//
//    // TODO delete
//    public List<TableKvpItem> getData() {
//        return _data;
//    }

    public void update(Map<String, Object> newData) {
        for (int i = 0; i < _data.size(); i++) {
            String key = _data.get(i).getKey();
            if(newData.containsKey(key)) {
                Object value = newData.get(key);
                if(value != null) {
                    _data.get(i).setValue(value.toString());
                    notifyItemChanged(i);
                }
            }
        }
    }

    public void update(String key, Object value) {
        for (int i = 0; i < _data.size(); i++) {
            if(_data.get(i).setValueIfKey(key, value))
                notifyItemChanged(i);
        }
    }


    public TableKvpAdapter(Context context, List<TableKvpItem> data, int itemLayout, TableKvpOnClickListener onClickListener) {

        _context = context;
        _itemLayout = itemLayout;
        _data = data;
        _onClickListener = onClickListener;

    }






    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view ;
        LayoutInflater mInflater = LayoutInflater.from(_context);
        view = mInflater.inflate(_itemLayout, parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TableKvpItem item = _data.get(position);

        // Toggle handling
        if(item.getValue() != null) {
            holder.txtKey.setText(item.getKeyReadable());
            holder.txtValue.setText(item.getValue());
        }
        else {
            holder.toggle.setText(item.getKeyReadable());
            holder.toggle.setChecked(item.testValue);

            // OnClickListener
            if(_onClickListener != null)
                holder.toggle.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        _onClickListener.onItemClick(item);
                    }
                });
        }

    }

    @Override
    public int getItemCount () {
        return _data.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtKey;
        public TextView txtValue;
        public SwitchMaterial toggle;

        public ViewHolder(View view) {
            super(view);
            txtKey = view.findViewById(R.id.kvp_txt_key);
            txtValue = view.findViewById(R.id.kvp_txt_value);
            toggle = view.findViewById(R.id.kvp_toggle);
        }

    }


}














