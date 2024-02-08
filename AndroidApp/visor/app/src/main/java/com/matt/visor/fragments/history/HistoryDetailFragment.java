package com.matt.visor.fragments.history;

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
import com.matt.visor.databinding.FragmentHistoryDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class HistoryDetailFragment extends Fragment {

    private FragmentHistoryDetailBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        _binding = FragmentHistoryDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();



        // TEST RECYCLER
        List<TableKvpItem> data = new ArrayList<>();
        data.add(new TableKvpItem("", "Speed", "123"));
        data.add(new TableKvpItem("", "Distance", "321"));
        data.add(new TableKvpItem("", "etc", "123"));
        data.add(new TableKvpItem("", "Speed", "123"));
        data.add(new TableKvpItem("", "Last update", "123"));



        // RecyclerView
        TableKvpAdapter _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_list);
        _binding.historyDetailTableKvp.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.historyDetailTableKvp.setAdapter(_rva);

        // TEST RECYCLER





        return root;
    }




//    /**
//     * TODO comment
//     * @param data
//     */
//    private void fillText(Map<String, String> data) {
//        TableLayout table = _binding.historyDetailTable;
//
//        for (Map.Entry<String, String> e : data.entrySet()) {
//            TableRow tableRow = new TableRow(getContext());
//            tableRow.setLayoutParams(new TableLayout.LayoutParams(
//                    TableLayout.LayoutParams.MATCH_PARENT,
//                    TableLayout.LayoutParams.WRAP_CONTENT));
//
//            TextView label = new TextView(getContext());
//            label.setText(e.getKey());
//            label.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//
//            TextView value = new TextView(getContext());
//            value.setText(e.getValue());
//            value.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
//
//            tableRow.addView(label);
//            tableRow.addView(value);
//
//            table.addView(tableRow);
//        }
//
//
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }
}