/**
 * This class is part of the V.I.S.O.R app.
 * Represents user interface for testing connected HUD unit with the options of sending speed or images.
 *
 * @version 1.0
 * @since 08/02/2024
 */

package com.matt.visor.fragments.home;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.matt.visor.R;
import com.matt.visor.TableKvpAdapter;
import com.matt.visor.TableKvpItem;
import com.matt.visor.TableKvpOnClickListener;
import com.matt.visor.app.VisorApplication;
import com.matt.visor.databinding.FragmentHudDetailBinding;

import java.util.ArrayList;
import java.util.List;

public class HudDetailFragment extends Fragment implements TableKvpOnClickListener, SeekBar.OnSeekBarChangeListener {

    private FragmentHudDetailBinding _binding;
    private TextView _txt_status;
    private TextView _txt_speed;

    /**
     * Initializes the fragment's view and sets up UI components.
     *
     * @param inflater The LayoutInflater object that can be used to inflate any views in the fragment.
     * @param container If non-null, this is the parent view that the fragment's UI should be attached to.
     * @param savedInstanceState If non-null, this fragment is being re-constructed from a previous saved state as given here.
     * @return The root View for the fragment's layout.
     */
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        VisorApplication app = (VisorApplication) requireActivity().getApplication();

        _binding = FragmentHudDetailBinding.inflate(inflater, container, false);
        View root = _binding.getRoot();

        // Txt
        _txt_status = _binding.txtStatus;
        _txt_speed = _binding.txtSpeed;
        _txt_speed.setText("50");

        // Seekbar
        SeekBar sb_speed = _binding.sbSpeed;
        sb_speed.setOnSeekBarChangeListener(this);

        // Buttons
        _binding.btnSendSpeed.setOnClickListener(v -> sendDataSpeed(sb_speed.getProgress()));

        // Images
        List<TableKvpItem<?>> data = new ArrayList<>();

        int i = 0;
        for (Bitmap bitmap :  app.get_listOfImages()) {
            data.add(new TableKvpItem<>(String.valueOf(i),null, bitmap));
            i++;
        }

        // Recycler
        TableKvpAdapter _rva = new TableKvpAdapter(getContext(), data, R.layout.kvp_image, this);
        _binding.imageRecycler.setLayoutManager(new GridLayoutManager(getContext(), 4));
        _binding.imageRecycler.setAdapter(_rva);

        // Update status
        app.deviceManager.getHUD().setStatusChangeListener(() -> _txt_status.setText(app.deviceManager.getHUD().getStatusString()));

        //
        return root;
    }

    /**
     * Sends the current speed setting to the device.
     *
     * @param speed The speed value to send.
     */
    private void sendDataSpeed(int speed) {
        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        app.deviceManager.getHUD().sendSpeed(speed);
    }

    /**
     * Handles item click events in the TableKvpAdapter and sends image to the device.
     *
     * @param item The TableKvpItem that was clicked.
     */
    @Override
    public void onItemClick(TableKvpItem item) {
        System.out.println("On item click : " + item.getKey());

        VisorApplication app = (VisorApplication) requireActivity().getApplication();
        app.deviceManager.getHUD().sendImg(Integer.parseInt(item.getKey()));
    }

    /**
     * Cleans up resources when the view is destroyed.
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        _binding = null;
    }

    /**
     * Updates the displayed speed text when the seek bar progress changes.
     *
     * @param seekBar The SeekBar whose progress has changed.
     * @param progress The current progress level.
     * @param fromUser True if the progress change was initiated by the user.
     */
    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        _txt_speed.setText(String.valueOf(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {}

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {}
}