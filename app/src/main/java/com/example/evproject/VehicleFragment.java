package com.example.evproject;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VehicleFragment extends Fragment {
    private TextView speedValue;
    private int speed = 50;
    private boolean increasing = true;
    private static final int MIN_SPEED = 50;
    private static final int MAX_SPEED = 100;
    private static final long INTERVAL = 1000; // 1 second

    private final Handler speedHandler = new Handler(Looper.getMainLooper());

    private final Runnable speedRunnable = new Runnable() {
        @Override
        public void run() {
            if (increasing) {
                if (speed < MAX_SPEED) {
                    speed++;
                } else {
                    increasing = false;
                }
            } else {
                if (speed > MIN_SPEED) {
                    speed--;
                } else {
                    increasing = true;
                }
            }
            speedValue.setText(String.valueOf(speed));
            speedHandler.postDelayed(this, INTERVAL);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);
        speedValue = view.findViewById(R.id.speedValue);
        speedValue.setText(String.valueOf(speed));
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        speedHandler.post(speedRunnable);
    }

    @Override
    public void onPause() {
        super.onPause();
        speedHandler.removeCallbacks(speedRunnable);
    }
}
