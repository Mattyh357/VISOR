package com.matt.visor;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;
import java.util.Map;

public class TableKvpAdapter extends RecyclerView.Adapter<TableKvpAdapter.ViewHolder> {

    private int _itemLayout;
    private Context _context;
    private List<TableKvpItem> _data;

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

            if(newData.containsKey(_data.get(i).getKey())) {

                String newText = newData.get(_data.get(i).getKey()).toString();

                _data.get(i).setValue(newText);
                notifyItemChanged(i);
            }
        }

    }



    public TableKvpAdapter(Context context, List<TableKvpItem> data, int itemLayout) {

        _context = context;
        _itemLayout = itemLayout;
        _data = data;

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
        holder.txtKey.setText(item.getKeyReadable());
        holder.txtValue.setText(item.getValue());
    }

    @Override
    public int getItemCount () {
        return _data.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txtKey;
        public TextView txtValue;

        public ViewHolder(View view) {
            super(view);
            txtKey = view.findViewById(R.id.kvp_txt_key);
            txtValue = view.findViewById(R.id.kvp_txt_value);
        }
    }

}














