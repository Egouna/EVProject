package com.example.evproject;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class VehicleFragment extends Fragment {
    private static final int MIN_SPEED = 50;
    private static final int MAX_SPEED = 100;
    private static final int SPEED_INCREMENT = 1;
    private static final int SERVER_PORT = 12345;

    private int currentSpeed = MIN_SPEED;
    private boolean increasing = true;
    private boolean isKmh = true;

    private TextView speedValue;
    private TextView speedType;
    private TextView readyText;
    private TextView gearP, gearR, gearN, gearD;
    private ImageView lowBeamLight, highBeamLight;
    private ImageView leftTurnLight, rightTurnLight;
    private ImageView generalFaultLight, electricalFaultLight, limitedPowerLight, lowBatteryLight, pedestrianAlertLight;
    private ImageButton carDoorState;

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

    private boolean blinkingLeft, blinkingRight, blinkingBoth;
    private final Runnable blinkerUpdater = new Runnable() {
        @Override
        public void run() {
            if (blinkingLeft) {
                toggleVisibility(leftTurnLight);
            }
            if (blinkingRight) {
                toggleVisibility(rightTurnLight);
            }
            if (blinkingBoth) {
                toggleVisibility(leftTurnLight);
                toggleVisibility(rightTurnLight);
            }

            if (blinkingLeft || blinkingRight || blinkingBoth) {
                handler.postDelayed(this, 500);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_vehicle, container, false);
        speedValue = view.findViewById(R.id.speedValue);
        speedType = view.findViewById(R.id.speedType);
        readyText = view.findViewById(R.id.readyText);
        gearP = view.findViewById(R.id.gearP);
        gearR = view.findViewById(R.id.gearR);
        gearN = view.findViewById(R.id.gearN);
        gearD = view.findViewById(R.id.gearD);
        lowBeamLight = view.findViewById(R.id.lowBeamLight);
        highBeamLight = view.findViewById(R.id.highBeamLight);
        leftTurnLight = view.findViewById(R.id.leftTurnLight);
        rightTurnLight = view.findViewById(R.id.rightTurnLight);
        generalFaultLight = view.findViewById(R.id.generalfaultyLight);
        electricalFaultLight = view.findViewById(R.id.electricalfaultLight);
        limitedPowerLight = view.findViewById(R.id.limitedpowerLight);
        lowBatteryLight = view.findViewById(R.id.lowbatteryLight);
        pedestrianAlertLight = view.findViewById(R.id.pedestrianalertLight);
        carDoorState = view.findViewById(R.id.carDoorState);
        LinearLayout speedoMeter = view.findViewById(R.id.speedoMeter);

        speedoMeter.setOnClickListener(v -> toggleSpeedType());

        // Start updating the speed
        handler.post(speedUpdater);

        // Initialize all states
        initializeGears();
        initializeBeams();
        initializeTurnSignals();
        initializeFaults();
        initializeCarDoors();
        // Start the server socket for receiving commands
        startServerSocket();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(speedUpdater);
        handler.removeCallbacks(blinkerUpdater);
    }

    private void updateSpeedDisplay() {
        speedValue.setText(String.valueOf(currentSpeed));
    }

    private void toggleSpeedType() {
        isKmh = !isKmh;
        speedType.setText(isKmh ? "kmh" : "mph");
    }

    private void initializeGears() {
        gearP.setTextColor(getResources().getColor(R.color.blue));
        gearR.setTextColor(getResources().getColor(android.R.color.black));
        gearN.setTextColor(getResources().getColor(android.R.color.black));
        gearD.setTextColor(getResources().getColor(android.R.color.black));
    }

    private void initializeBeams() {
        lowBeamLight.setVisibility(View.INVISIBLE);
        highBeamLight.setVisibility(View.INVISIBLE);
    }

    private void initializeTurnSignals() {
        leftTurnLight.setVisibility(View.INVISIBLE);
        rightTurnLight.setVisibility(View.INVISIBLE);
    }

    private void initializeFaults() {
        generalFaultLight.setVisibility(View.INVISIBLE);
        electricalFaultLight.setVisibility(View.INVISIBLE);
        limitedPowerLight.setVisibility(View.INVISIBLE);
        lowBatteryLight.setVisibility(View.INVISIBLE);
        pedestrianAlertLight.setVisibility(View.INVISIBLE);
    }

    private void initializeCarDoors() {
        carDoorState.setImageResource(R.drawable.bothdoor);
    }

    private void setGearColor(String gear) {
        gearP.setTextColor(getResources().getColor(android.R.color.black));
        gearR.setTextColor(getResources().getColor(android.R.color.black));
        gearN.setTextColor(getResources().getColor(android.R.color.black));
        gearD.setTextColor(getResources().getColor(android.R.color.black));

        switch (gear) {
            case "P":
                gearP.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "R":
                gearR.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "N":
                gearN.setTextColor(getResources().getColor(R.color.blue));
                break;
            case "D":
                gearD.setTextColor(getResources().getColor(R.color.blue));
                break;
        }
    }

    private void toggleVisibility(View view) {
        if (view.getVisibility() == View.VISIBLE) {
            view.setVisibility(View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    private void startBlinkingLeft() {
        stopAllBlinking();
        blinkingLeft = true;
        handler.post(blinkerUpdater);
    }

    private void startBlinkingRight() {
        stopAllBlinking();
        blinkingRight = true;
        handler.post(blinkerUpdater);
    }

    private void startBlinkingBoth() {
        stopAllBlinking();
        blinkingBoth = true;
        handler.post(blinkerUpdater);
    }

    private void stopAllBlinking() {
        blinkingLeft = false;
        blinkingRight = false;
        blinkingBoth = false;
        handler.removeCallbacks(blinkerUpdater);
        leftTurnLight.setVisibility(View.INVISIBLE);
        rightTurnLight.setVisibility(View.INVISIBLE);
    }

    private void setCarDoorState(String door) {
        switch (door) {
            case "1":
                carDoorState.setImageResource(R.drawable.leftdoor);
                break;
            case "2":
                carDoorState.setImageResource(R.drawable.rightdoor);
                break;
            case "3":
                carDoorState.setImageResource(R.drawable.bothdoor);
                break;
        }
    }

    private void startServerSocket() {
        Thread serverThread = new Thread(() -> {
            try (ServerSocket serverSocket = new ServerSocket(SERVER_PORT)) {
                while (true) {
                    Socket clientSocket = serverSocket.accept();
                    BufferedReader input = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                    String inputLine = input.readLine();
                    if ("S".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(readyText));
                    } else if (inputLine != null && inputLine.matches("[PRND]")) {
                        final String gear = inputLine.trim();
                        requireActivity().runOnUiThread(() -> setGearColor(gear));
                    } else if ("L".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(lowBeamLight));
                    } else if ("H".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(highBeamLight));
                    } else if ("Z".equals(inputLine)) {
                        requireActivity().runOnUiThread(this::startBlinkingLeft);
                    } else if ("X".equals(inputLine)) {
                        requireActivity().runOnUiThread(this::startBlinkingRight);
                    } else if ("C".equals(inputLine)) {
                        requireActivity().runOnUiThread(this::startBlinkingBoth);
                    } else if ("B".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(generalFaultLight));
                    } else if ("N".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(electricalFaultLight));
                    } else if ("M".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(limitedPowerLight));
                    } else if ("K".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(lowBatteryLight));
                    } else if ("L2".equals(inputLine)) {
                        requireActivity().runOnUiThread(() -> toggleVisibility(pedestrianAlertLight));
                    } else {
                        assert inputLine != null;
                        if (inputLine.matches("[123]")) {
                            final String door = inputLine.trim();
                            requireActivity().runOnUiThread(() -> setCarDoorState(door));
                        }
                    }
                    clientSocket.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
                requireActivity().runOnUiThread(() -> Toast.makeText(getContext(), "Server Error: " + e.getMessage(), Toast.LENGTH_SHORT).show());
            }
        });
        serverThread.start();
    }
}
