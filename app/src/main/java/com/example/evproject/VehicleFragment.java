package com.example.evproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class VehicleFragment extends Fragment {
    private static final int MIN_SPEED = 50;
    private static final int MAX_SPEED = 100;
    private static final int SPEED_INCREMENT = 1;

    private int currentSpeed = MIN_SPEED;
    private boolean increasing = true;
    private boolean isKmh = true;

    private TextView speedValue;
    private TextView speedType;
    private ImageButton carDoorStateButton;

    private static final int STATE_BOTH_DOORS_CLOSED = 0;
    private static final int STATE_LEFT_DOOR_OPEN = 1;
    private static final int STATE_RIGHT_DOOR_OPEN = 2;
    private int currentDoorState = STATE_BOTH_DOORS_CLOSED;

    private TextView gearP, gearR, gearN, gearD;

    private final Handler handler = new Handler();
    private final Runnable speedUpdater = new Runnable() {
        @Override
        public void run() {
            if (increasing) {
                currentSpeed += SPEED_INCREMENT;
                if (currentSpeed >= MAX_SPEED) {
                    currentSpeed = MAX_SPEED;
                    increasing = false;
                }
            } else {
                currentSpeed -= SPEED_INCREMENT;
                if (currentSpeed <= MIN_SPEED) {
                    currentSpeed = MIN_SPEED;
                    increasing = true;
                }
            }
            updateSpeedDisplay();
            handler.postDelayed(this, 1000);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);

        speedValue = view.findViewById(R.id.speedValue);
        speedType = view.findViewById(R.id.speedType);
        LinearLayout speedoMeter = view.findViewById(R.id.speedoMeter);

        carDoorStateButton = view.findViewById(R.id.carDoorState);
        carDoorStateButton.setOnClickListener(v -> toggleCarDoorState());

        speedoMeter.setOnClickListener(v -> toggleSpeedType());

        // Initialize gear text views
        gearP = view.findViewById(R.id.gearP);
        gearR = view.findViewById(R.id.gearR);
        gearN = view.findViewById(R.id.gearN);
        gearD = view.findViewById(R.id.gearD);

        // Set initial gear state to Parked
        setGearState(gearP);

        // Set up gear selection
        gearP.setOnClickListener(v -> setGearState(gearP));
        gearR.setOnClickListener(v -> setGearState(gearR));
        gearN.setOnClickListener(v -> setGearState(gearN));
        gearD.setOnClickListener(v -> setGearState(gearD));

        // Start updating the speed
        handler.post(speedUpdater);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(speedUpdater);
    }

    private void updateSpeedDisplay() {
        speedValue.setText(String.valueOf(currentSpeed));
    }

    private void toggleSpeedType() {
        isKmh = !isKmh;
        speedType.setText(isKmh ? "kmh" : "mph");
    }

    private void toggleCarDoorState() {
        switch (currentDoorState) {
            case STATE_BOTH_DOORS_CLOSED:
                currentDoorState = STATE_LEFT_DOOR_OPEN;
                carDoorStateButton.setImageResource(R.drawable.leftdoor);
                break;
            case STATE_LEFT_DOOR_OPEN:
                currentDoorState = STATE_RIGHT_DOOR_OPEN;
                carDoorStateButton.setImageResource(R.drawable.rightdoor);
                break;
            case STATE_RIGHT_DOOR_OPEN:
                currentDoorState = STATE_BOTH_DOORS_CLOSED;
                carDoorStateButton.setImageResource(R.drawable.bothdoor);
                break;
        }
    }

    private void setGearState(TextView selectedGear) {
        // Reset color of all gears to black
        gearP.setTextColor(getResources().getColor(android.R.color.black));
        gearR.setTextColor(getResources().getColor(android.R.color.black));
        gearN.setTextColor(getResources().getColor(android.R.color.black));
        gearD.setTextColor(getResources().getColor(android.R.color.black));

        // Set selected gear color to blue
        selectedGear.setTextColor(getResources().getColor(android.R.color.holo_blue_light));

    }
}
