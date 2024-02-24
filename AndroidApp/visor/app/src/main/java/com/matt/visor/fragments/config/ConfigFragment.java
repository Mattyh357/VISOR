package com.matt.visor.fragments.config;

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
import com.matt.visor.TableKvpOnClickListener;
import com.matt.visor.databinding.FragmentConfigBinding;

import java.util.ArrayList;
import java.util.List;

public class ConfigFragment extends Fragment implements TableKvpOnClickListener {

    private FragmentConfigBinding _binding;

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        _binding = FragmentConfigBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();





        // TEST RECYCLER
        List<TableKvpItem<?>> data = new ArrayList<>();
        data.add(new TableKvpItem<>("", "Config 1", true));
        data.add(new TableKvpItem<>("", "Config 2", true));
        data.add(new TableKvpItem<>("", "Config 3", true));
        data.add(new TableKvpItem<>("", "Config 4", true));
        data.add(new TableKvpItem<>("", "Config 5", true));


        // RecyclerView
        TableKvpAdapter _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_toggle, this);
        _binding.configKvp.setLayoutManager(new LinearLayoutManager(getContext()));
        _binding.configKvp.setAdapter(_rva);




//        final TextView textView = _binding.textConfig;
//        textView.setText("CONFIG");
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    @Override
    public void onItemClick(TableKvpItem item) {

        System.out.println("CLICK ON SOMETHING: " + item);

    }
}